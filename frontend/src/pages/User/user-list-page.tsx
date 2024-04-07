import React, { FC, ReactElement, useEffect, useState } from "react";
import {
  Box,
  Button,
  Typography,
  Grid,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  TextField,
  Dialog,
  DialogTitle,
  DialogActions,
  DialogContent,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import useStyles from "../../components/page-styles/global-styles";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/page-navbar/navbar";
import Footer from "../../components/page-footer/footer";
import { IPage } from "../../shared/models/page";
import { useDelete, useGet } from "../../services/api-service";
import useErrorHandling from "../../services/handle-responses";
import { ICreditor } from "../../shared/models/creditor";
import { IDebtor } from "../../shared/models/debtor";
import ScrollToTopButton from "../../components/scroll-to-top/scroll-top";

const UserListPage: FC<IPage> = (props): ReactElement => {
  const navigate = useNavigate();
  const classes = useStyles("light");
  const [shouldRefetch, setShouldRefetch] = useState(false);
  const [confirmationDialogOpen, setConfirmationDialogOpen] = useState(false);
  const [userToDelete, setUserToDelete] = useState<{
    id: number;
    type: string;
  } | null>(null);
  const [showMoreCreditor, setShowMoreCreditor] = useState(3);
  const [showMoreDebtor, setShowMoreDebtor] = useState(3);
  const [creditorSearchQuery, setCreditorSearchQuery] = useState("");
  const [debtorSearchQuery, setDebtorSearchQuery] = useState("");
  const { handleErrorResponse } = useErrorHandling();
  const { openSnackbar } = props;

  const {
    data: creditorData,
    loading: creditorLoading,
    error: creditorError,
  } = useGet<ICreditor[]>("creditors", {}, shouldRefetch, true);

  const {
    data: debtorData,
    loading: debtorLoading,
    error: debtorError,
  } = useGet<IDebtor[]>("debtors", {}, shouldRefetch, true);

  const useDeleteCreditor = (id: number) => {
    const { data, loading, error, deleteData } = useDelete<any>(
      "creditors/{id}",
      { id: id }
    );
    return { data, loading, error, deleteCreditor: deleteData };
  };

  const useDeleteDebtor = (id: number) => {
    const { data, loading, error, deleteData } = useDelete<any>(
      "debtors/{id}",
      { id: id }
    );
    return { data, loading, error, deleteDebtor: deleteData };
  };

  const {
    data: deletedDataCreditor,
    error: deleteErrorCreditor,
    deleteCreditor,
  } = useDeleteCreditor(userToDelete?.id || 0);
  const {
    data: deletedDataDebtor,
    error: deleteErrorDebtor,
    deleteDebtor,
  } = useDeleteDebtor(userToDelete?.id || 0);

  const filteredCreditors = creditorData
    ? creditorData.filter(
        (creditor: ICreditor) =>
          creditor.user?.username.includes(creditorSearchQuery) ||
          creditor.name.includes(creditorSearchQuery) ||
          creditor.email.includes(creditorSearchQuery) ||
          creditor.phoneNumber.includes(creditorSearchQuery)
      )
    : [];

  const filteredDebtors = debtorData
    ? debtorData.filter(
        (debtor: IDebtor) =>
          debtor.user?.username.includes(debtorSearchQuery) ||
          debtor.name.includes(debtorSearchQuery) ||
          debtor.surname.includes(debtorSearchQuery) ||
          debtor.email?.includes(debtorSearchQuery) ||
          debtor.phoneNumber?.includes(debtorSearchQuery)
      )
    : [];

  useEffect(() => {
    handleAPIError(creditorError, openSnackbar);
  }, [creditorError, openSnackbar]);

  useEffect(() => {
    handleAPIError(debtorError, openSnackbar);
  }, [debtorError, openSnackbar]);

  useEffect(() => {
    handleAPIError(deleteErrorCreditor, openSnackbar);
  }, [deleteErrorCreditor, openSnackbar]);

  useEffect(() => {
    handleAPIError(deleteErrorDebtor, openSnackbar);
  }, [deleteErrorDebtor, openSnackbar]);

  useEffect(() => {
    handleAPIError(deletedDataCreditor, openSnackbar);
  }, [deletedDataCreditor, openSnackbar]);

  useEffect(() => {
    handleAPIError(deletedDataDebtor, openSnackbar);
  }, [deletedDataDebtor, openSnackbar]);

  const handleAPIError = (error: any, snackbar: any) => {
    if (error && [401, 403].includes(error.statusCode)) {
      handleErrorResponse(error.statusCode);
      snackbar(error.message, "error");
    } else if (error && error.statusCode === 204) {
      setShouldRefetch(true);
      setConfirmationDialogOpen(false);
      openSnackbar("User deleted successfully", "success");
    } else if (error) {
      setConfirmationDialogOpen(false);
      snackbar(error.message, "error");
    } else if (error?.description.includes("Refresh Token")) {
      navigate("/login");
      openSnackbar("You need to login again", "warning");
    }
  };

  const handleEdit = (id: number, type: string) => {
    if (type === "creditor") {
      navigate(`/creditors/${id}`);
    } else if (type === "debtor") {
      navigate(`/debtors/${id}`);
    }
  };

  const handleDelete = (id: number, type: string) => {
    setUserToDelete({ id, type });
    setConfirmationDialogOpen(true);
  };

  const handleDeleteConfirmed = async () => {
    if (userToDelete) {
      if (userToDelete.type === "creditor") {
        await deleteCreditor();
      } else if (userToDelete.type === "debtor") {
        await deleteDebtor();
      }
    }
  };

  const handleDeleteCancelled = () => {
    setConfirmationDialogOpen(false);
    setUserToDelete(null);
  };

  const renderActionButtons = (id: number, type: string) => (
    <Grid container spacing={1} justifyContent="flex-end">
      {["Edit", "Delete"].map((action) => (
        <Grid item key={`${action}-${id}`}>
          <Button
            sx={{
              color: "black",
              backgroundColor: "white",
              border: "3px solid #8FBC8F",
              "&:hover": {
                color: "black",
                backgroundColor: "#F8DE7E",
              },
            }}
            onClick={() =>
              action === "Edit" ? handleEdit(id, type) : handleDelete(id, type)
            }
          >
            {action}
          </Button>
        </Grid>
      ))}
    </Grid>
  );

  const renderExpandButton = (
    showMore: number,
    setShowMore: React.Dispatch<React.SetStateAction<number>>,
    data: ICreditor[] | IDebtor[],
    label: string
  ) => (
    <Button
      sx={{
        color: "black",
        backgroundColor: "white",
        border: "2px solid",
        marginBottom: "5px",
        "&:hover": {
          color: "black",
          backgroundColor: "#F8DE7E",
        },
      }}
      onClick={() =>
        setShowMore((prev) => {
          if (prev === data.length) {
            return 3;
          } else {
            return data.length;
          }
        })
      }
    >
      {showMore === data.length ? `Show Less ${label}` : `Show More ${label}`}
    </Button>
  );

  return (
    <>
      <Box
        className={classes.body}
        sx={{
          flexGrow: 1,
          display: "flex",
          flexDirection: "column",
          justifyContent: "space-between",
          minHeight: "100vh",
          height: "100%",
          overflow: "hidden",
        }}
      >
        <Navbar title="DebtEase" />
        <Box sx={{ padding: "16px" }}>
          <Button
            sx={{
              color: "black",
              backgroundColor: "white",
              border: "2px solid",
              "&:hover": {
                color: "black",
                backgroundColor: "#F8DE7E",
              },
            }}
            onClick={() => navigate(-1)}
          >
            Back
          </Button>
          {creditorLoading || debtorLoading ? (
            <Typography>Loading...</Typography>
          ) : (
            <Box>
              <Typography
                variant="h5"
                sx={{ display: "flex", alignItems: "center" }}
              >
                Creditors
                <Box
                  sx={{
                    marginLeft: "auto",
                    display: "flex",
                    alignItems: "center",
                  }}
                >
                  <TextField
                    label="Search Creditors"
                    sx={{ background: "white", borderRadius: "4px" }}
                    variant="outlined"
                    value={creditorSearchQuery}
                    onChange={(e) => setCreditorSearchQuery(e.target.value)}
                    margin="normal"
                    size="small"
                    className={classes.searchTextField}
                  />
                </Box>
              </Typography>
              {renderExpandButton(
                showMoreCreditor,
                setShowMoreCreditor,
                creditorData || [],
                "Creditors"
              )}
              <Button
                sx={{
                  color: "black",
                  backgroundColor: "white",
                  border: "2px solid",
                  marginBottom: "5px",
                  marginLeft: "5px",
                  "&:hover": {
                    color: "black",
                    backgroundColor: "#F8DE7E",
                  },
                }}
                onClick={() => navigate("/users/new")}
              >
                Create Creditor
              </Button>
              {filteredCreditors?.slice(0, showMoreCreditor).map((creditor) => (
                <Accordion key={creditor.id} sx={{ my: 1 }}>
                  <AccordionSummary
                    expandIcon={<ExpandMoreIcon />}
                    aria-controls={`creditor-details-${creditor.id}`}
                    id={`creditor-details-${creditor.id}`}
                    sx={{ borderBottom: "1px solid #ccc" }}
                  >
                    <Typography>{creditor.name}</Typography>
                  </AccordionSummary>
                  <AccordionDetails>
                    <Box>
                      <Typography>{`Address: ${creditor.address}`}</Typography>
                      <Typography>{`Phone Number: ${creditor.phoneNumber}`}</Typography>
                      <Typography>{`Email: ${creditor.email}`}</Typography>
                      <Typography>{`Bank Account Number: ${creditor.accountNumber}`}</Typography>
                      <Typography>{`Username: ${creditor.user?.username}`}</Typography>
                      {renderActionButtons(creditor.id, "creditor")}
                    </Box>
                  </AccordionDetails>
                </Accordion>
              ))}
              <Typography
                variant="h5"
                sx={{ display: "flex", alignItems: "center" }}
              >
                Debtors
                <Box
                  sx={{
                    marginLeft: "auto",
                    display: "flex",
                    alignItems: "center",
                  }}
                >
                  <TextField
                    label="Search Debtors"
                    sx={{ background: "white", borderRadius: "4px" }}
                    variant="outlined"
                    value={debtorSearchQuery}
                    onChange={(e) => setDebtorSearchQuery(e.target.value)}
                    margin="normal"
                    size="small"
                    className={classes.searchTextField}
                  />
                </Box>
              </Typography>
              {renderExpandButton(
                showMoreDebtor,
                setShowMoreDebtor,
                debtorData || [],
                "Debtors"
              )}
              {filteredDebtors?.slice(0, showMoreDebtor).map((debtor) => (
                <Accordion key={debtor.id} sx={{ my: 1 }}>
                  <AccordionSummary
                    expandIcon={<ExpandMoreIcon />}
                    aria-controls={`debtor-details-${debtor.id}`}
                    id={`debtor-details-${debtor.id}`}
                    sx={{ borderBottom: "1px solid #ccc" }}
                  >
                    <Typography>{`${debtor.name} ${debtor.surname}`}</Typography>
                  </AccordionSummary>
                  <AccordionDetails>
                    <Box>
                      <Typography>{`Email: ${debtor.email}`}</Typography>
                      <Typography>{`Phone Number: ${debtor.phoneNumber}`}</Typography>
                      <Typography>{`Username: ${debtor.user?.username}`}</Typography>
                      {renderActionButtons(debtor.id, "debtor")}
                    </Box>
                  </AccordionDetails>
                </Accordion>
              ))}
              <ScrollToTopButton />
            </Box>
          )}
          {userToDelete && (
            <Dialog
              open={confirmationDialogOpen}
              onClose={handleDeleteCancelled}
            >
              <DialogTitle>Confirm Deletion</DialogTitle>
              <DialogContent>
                <Typography>
                  Are you sure you want to delete this user?
                </Typography>
              </DialogContent>
              <DialogActions>
                <Button
                  sx={{
                    color: "black",
                    backgroundColor: "white",
                    border: "3px solid #8FBC8F",
                    marginRight: 2,
                    "&:hover": {
                      color: "black",
                      backgroundColor: "#F8DE7E",
                    },
                  }}
                  onClick={handleDeleteCancelled}
                >
                  Cancel
                </Button>
                <Button
                  sx={{
                    color: "red",
                    backgroundColor: "white",
                    border: "3px solid #8FBC8F",
                    marginRight: 2,
                    "&:hover": {
                      color: "red",
                      backgroundColor: "#F8DE7E",
                    },
                  }}
                  onClick={handleDeleteConfirmed}
                >
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

export default UserListPage;
