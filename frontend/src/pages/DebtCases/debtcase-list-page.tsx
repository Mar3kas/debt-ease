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
import { useDelete, useGet } from "../../services/api-service";
import useErrorHandling from "../../services/handle-responses";

const DebtcaseListPage: FC<IPage> = (props): ReactElement => {
  const classes = useStyles("light");
  const navigate = useNavigate();
  const role = localStorage.getItem("role");
  const username = localStorage.getItem("username");
  const [shouldRefetch, setShouldRefetch] = useState(false);
  const [confirmationDialogOpen, setConfirmationDialogOpen] = useState(false);
  const [debtCaseToDelete, setDebtCaseToDelete] = useState<{ creditorId: number, id: number } | null>(null);
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

  const { data, loading, error } = useGet<IDebtCase[]>(
    roleSpecificEndpoint,
    role === "ADMIN" ? {} : username ? { username } : {},
    shouldRefetch
  );

  const { data: deletedData, loading: deleteLoading, error: deleteError, deleteData } = useDelete("creditor/{creditorId}/debtcases/{id}", { creditorId: debtCaseToDelete?.creditorId, id: debtCaseToDelete?.id });

  useEffect(() => {
    if (error && (error.statusCode === 401 || error?.statusCode === 403)) {
      handleErrorResponse(error.statusCode);
      openSnackbar(error.message, 'error');
    }
  }, [error, openSnackbar]);

  const handleEdit = (creditorId: number, id: number) => {
    // Implement edit functionality
  };

  const handleDelete = (creditorId: number, id: number) => {
    setDebtCaseToDelete({ creditorId, id });
    setConfirmationDialogOpen(true);
  };

  const handleDeleteConfirmed = async () => {
    deleteData();
  };

  const handleDeleteCancelled = () => {
    setConfirmationDialogOpen(false);
    setDebtCaseToDelete(null);
  };

  useEffect(() => {
    if (deleteError && deleteError.statusCode === 204 && deleteError.description.includes("Deleted sucessfully")) {
      setShouldRefetch(true);
      setConfirmationDialogOpen(false);
      openSnackbar("Debt Case deleted successfully", 'success');
    } else if (deleteError && deleteError.statusCode !== 204) {
      setConfirmationDialogOpen(false);
      openSnackbar(deleteError.description, 'error');
    }

    if (shouldRefetch) {
      setShouldRefetch(false);
    }

    setDebtCaseToDelete(null);
  }, [deleteError, openSnackbar, setShouldRefetch]);

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

  const groupedDebtCases = data?.reduce((acc, debtCase) => {
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
          {loading ? (
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