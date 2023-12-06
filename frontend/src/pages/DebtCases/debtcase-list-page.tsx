import React, { FC, ReactElement, useEffect, useState } from "react";
import {
  Box,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Button,
  Typography,
  Grid,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import useStyles from "../../Components/Styles/global-styles";
import { useNavigate } from "react-router-dom";
import Navbar from "../../Components/Navbar/navbar";
import Footer from "../../Components/Footer/footer";
import { IPage } from "../../shared/models/Page";
import { IDebtCase } from "../../shared/models/Debtcases";
import { useDelete, useGet, usePost } from "../../services/api-service";
import useErrorHandling from "../../services/handle-responses";
import { ICreditor } from "../../shared/models/Creditor";

const DebtcaseListPage: FC<IPage> = (props): ReactElement => {
  const classes = useStyles("light");
  const navigate = useNavigate();
  const role = localStorage.getItem("role");
  const username = localStorage.getItem("username");
  const [shouldRefetch, setShouldRefetch] = useState(false);
  const [confirmationDialogOpen, setConfirmationDialogOpen] = useState(false);
  const [debtCaseToDelete, setDebtCaseToDelete] = useState<{ creditorId: number, id: number } | null>(null);
  const [creditorId, setCreditorId] = useState<number | undefined>(undefined);
  const { handleErrorResponse } = useErrorHandling();
  const { openSnackbar } = props;

  const roleSpecificEndpoint = (() => {
    switch (role) {
      case "CREDITOR":
        return `creditor/${username}/debtcases`;
      case "DEBTOR":
        return `creditor/debtcases/debtor/${username}`;
      case "ADMIN":
        return "debtcases";
      default:
        return "";
    }
  })();

  const { data: deletedData, error: deleteError, deleteData } = useDelete<any>("creditor/{creditorId}/debtcases/{id}", { creditorId: debtCaseToDelete?.creditorId, id: debtCaseToDelete?.id });
  const { data: fileData, error: fileError, postData } = usePost<FormData>("creditor/{id}/debtcases/file", { id: creditorId });
  const { data: debtCaseData, loading: debtCaseLoading, error } = useGet<IDebtCase[]>(
    roleSpecificEndpoint,
    role === "ADMIN" ? {} : username ? { username } : {},
    shouldRefetch
  );

  useEffect(() => {
    if (error && [401, 403].includes(error.statusCode)) {
      handleErrorResponse(error.statusCode);
      openSnackbar(error.message, 'error');
    } else if (error?.description.includes("Refresh Token")) {
      navigate("/login");
      openSnackbar("You need to login again", 'warning');
  } 
  }, [error, openSnackbar]);

  useEffect(() => {
    if (!debtCaseLoading) {
      setShouldRefetch(false);
    }
  }, [debtCaseLoading]);

  useEffect(() => {
    if (debtCaseData && creditorId === undefined && role === "CREDITOR") {
      const userWithSameUsername = debtCaseData.find(debtCase => debtCase.creditor.user.username === username);

      if (userWithSameUsername) {
        setCreditorId(userWithSameUsername.creditor.id);
      }
    }
  }, [debtCaseData]);

  const handleEdit = (creditorId: number, id: number) => {
    navigate(`/creditor/${creditorId}/debtcases/${id}`);
  };

  const handleDelete = (creditorId: number, id: number) => {
    setDebtCaseToDelete({ creditorId, id });
    setConfirmationDialogOpen(true);
  };

  const handleFileUpload = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    const file = event.target.files?.[0];

    if (file) {
      const formData = new FormData();
      formData.append("file", file);
      postData(formData, true);
    }
  };

  useEffect(() => {
    if (fileData) {
      openSnackbar("File uploaded successfully", 'success');
      setShouldRefetch(true);
      setCreditorId(undefined);
    } else if (fileError) {
      openSnackbar(fileError.description, 'error');
    }
  }, [fileData, fileError, openSnackbar]);

  const handleDeleteConfirmed = async () => {
    deleteData();
  };

  const handleDeleteCancelled = () => {
    setConfirmationDialogOpen(false);
    setDebtCaseToDelete(null);
  };

  useEffect(() => {
    if (deletedData && deletedData.statusCode === 204 && deletedData.description.includes("Deleted successfully")) {
      openSnackbar("Debt Case deleted successfully", 'success');
      setShouldRefetch(true);
      setConfirmationDialogOpen(false);
    } else if (deleteError && deleteError.statusCode !== 204) {
      openSnackbar(deleteError.description, 'error');
    }

    setDebtCaseToDelete(null);
  }, [deleteError, deletedData, openSnackbar]);

  const renderActionButtons = (creditorId: number, id: number) => (
    <Grid container spacing={1} justifyContent="flex-end">
      {["Edit", role === "ADMIN" && "Delete"].filter(Boolean).map((action) => (
        <Grid item key={`${action}-${id}`}>
          <Button
            sx={{
              color: "black",
              backgroundColor: "white",
              border: "3px solid #8FBC8F",
            }}
            onClick={() => (action === "Edit" ? handleEdit(creditorId, id) : handleDelete(creditorId, id))}
          >
            {action}
          </Button>
        </Grid>
      ))}
    </Grid>
  );

  const groupedDebtCases = debtCaseData?.reduce((acc, debtCase) => {
    const status = debtCase.debtCaseStatus.status.toLowerCase();
    acc[status] = acc[status] || [];
    acc[status].push(debtCase);
    return acc;
  }, {} as Record<string, IDebtCase[]>);

  const renderAccordionSection = (title: string, cases: IDebtCase[], status: string) => (
    <div>
      <Typography sx={{ marginTop: "10px" }} variant="h6" gutterBottom>
        {title}
      </Typography>
      {cases.map((debtCase: IDebtCase, index: number) => (
        <Accordion
          key={`${status}-${index}`}
          sx={{
            background: "white",
            boxShadow: "0px 4px 8px rgba(0, 0, 0, 0.1)",
            "&:hover": {
              boxShadow: "0px 8px 16px rgba(0, 0, 0, 0.2)",
            },
          }}
        >
          <AccordionSummary
            expandIcon={<ExpandMoreIcon sx={{ color: "#2E8B57" }} />}
            sx={{ borderBottom: "1px solid #ccc" }}
          >
            <Typography style={{ marginRight: "5px" }}>{debtCase.creditor?.name}</Typography>
            <Typography style={{ marginRight: "5px" }}>{transformDebtType(debtCase.debtCaseType.type)}</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Typography>Due Date: {debtCase.dueDate}</Typography>
            <Typography>Amount Owed: {debtCase.amountOwed}€</Typography>
            <Typography>Is Notification Sent? {debtCase.isSent ? "No" : "Yes"}</Typography>
            {(role === "CREDITOR" || role === "ADMIN")
              && debtCase.debtors?.map(renderDebtorDetails)
              && renderActionButtons(debtCase.creditor.id, debtCase.id)}
          </AccordionDetails>
        </Accordion>
      ))}
    </div>
  );

  const renderDebtorDetails = (debtor: any, index: number) => (
    <React.Fragment key={index}>
      <Typography variant="subtitle1">Debtor: {debtor.name} {debtor.surname}</Typography>
      <Typography variant="body2">Email: {debtor.email}</Typography>
      <Typography variant="body2">Phone Number: {debtor.phoneNumber}</Typography>
    </React.Fragment>
  );

  const transformDebtType = (type: string): string => {
    const lowerCaseType = type.toLowerCase();
    return lowerCaseType.split('_').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ');
  };

  return (
    <>
      <Box className={classes.body} sx={{ flexGrow: 1, display: "flex", flexDirection: "column", justifyContent: "space-between", minHeight: "100vh", height: "100%", overflow: "hidden" }}>
        <Navbar title="DebtEase" />
        <Box sx={{ padding: "16px" }}>
          <Grid container spacing={1} justifyContent="space-between">
            <Grid item>
              <Button
                sx={{
                  color: "black",
                  backgroundColor: "white",
                  border: "2px solid",
                  marginRight: "10px",
                  "&:hover": {
                    color: "black",
                    backgroundColor: "#F8DE7E",
                  },
                }}
                onClick={() => navigate(-1)}
              >
                Back
              </Button>
            </Grid>
            {role === "CREDITOR" && (
              <Grid item>
                <label htmlFor="file-upload">
                  <Button
                    component="span"
                    sx={{
                      color: "black",
                      backgroundColor: "white",
                      border: "2px solid",
                      "&:hover": {
                        color: "black",
                        backgroundColor: "#F8DE7E",
                      },
                    }}
                  >
                    Upload
                  </Button>
                </label>
                <input
                  id="file-upload"
                  type="file"
                  style={{ display: "none" }}
                  onChange={(event) => handleFileUpload(event)}
                />
              </Grid>
            )}
          </Grid>
          {debtCaseLoading ? (
            <Typography>Loading...</Typography>
          ) : (
            <Box>
              {groupedDebtCases && Object.entries(groupedDebtCases).map(([status, cases]) => (
                renderAccordionSection(status === 'unpaid' ? 'Unpaid Debt Cases' : status.charAt(0).toUpperCase() + status.slice(1) + ' Debt Cases', cases, status)
              ))}
            </Box>
          )}
          {debtCaseToDelete && (
            <Dialog open={confirmationDialogOpen} onClose={handleDeleteCancelled}>
              <DialogTitle>Confirm Deletion</DialogTitle>
              <DialogContent>
                <Typography>
                  Are you sure you want to delete this debt case?
                </Typography>
              </DialogContent>
              <DialogActions>
                <Button sx={{
                  color: "black",
                  backgroundColor: "white",
                  border: "3px solid #8FBC8F",
                  marginRight: 2,
                }}
                  onClick={handleDeleteCancelled}>Cancel</Button>
                <Button
                  sx={{
                    color: "red",
                    backgroundColor: "white",
                    border: "3px solid #8FBC8F",
                    marginRight: 2,
                  }}
                  onClick={handleDeleteConfirmed}>
                  Confirm
                </Button>
              </DialogActions>
            </Dialog>
          )}
        </Box>
        <Footer />
      </Box>
    </>
  );
};

export default DebtcaseListPage;