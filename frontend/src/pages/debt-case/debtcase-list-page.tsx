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
  Pagination,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import useStyles from "../../components/page-styles/global-styles";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/page-navbar/navbar";
import Footer from "../../components/page-footer/footer";
import { IPage } from "../../shared/models/page";
import { IDebtCase } from "../../shared/models/debt-case";
import { useDelete, useGet, usePost } from "../../services/api-service";
import useErrorHandling from "../../services/handle-responses";
import { IDebtor } from "../../shared/models/debtor";
import WebSocketService from "../../services/websocket-service";
import { ICreditor } from "../../shared/models/creditor";
import AuthService from "../../services/jwt-service";
import ScrollToTopButton from "../../components/scroll-to-top/scroll-top";

const DebtCaseListPage: FC<IPage> = (props): ReactElement => {
  const authService = AuthService.getInstance();
  const classes = useStyles("light");
  const navigate = useNavigate();
  const role = authService.getRole();
  const username = authService.getUsername() ?? "";
  const [shouldRefetch, setShouldRefetch] = useState(false);
  const [confirmationDialogOpen, setConfirmationDialogOpen] = useState(false);
  const [debtCaseToDelete, setDebtCaseToDelete] = useState<{
    creditorId: number;
    id: number;
  } | null>(null);
  const [creditorId, setCreditorId] = useState<number | undefined>(undefined);
  const [currentDebtCases, setCurrentDebtCases] = useState<IDebtCase[] | null>(
    null
  );
  const [highlightedCases, setHighlightedCases] = useState<number[]>([]);
  const [downloadPdf, setDownloadPdf] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [casesPerPage] = useState(5);
  const totalCases = currentDebtCases?.length || 0;
  const totalPages = Math.ceil(totalCases / casesPerPage);
  const startIndex = (currentPage - 1) * casesPerPage;
  const endIndex = Math.min(startIndex + casesPerPage, totalCases);
  const paginatedCases = currentDebtCases?.slice(startIndex, endIndex);
  const groupedDebtCases = paginatedCases?.reduce((acc, debtCase) => {
    const status = debtCase.debtCaseStatus.toLowerCase();
    acc[status] = acc[status] || [];
    acc[status].push(debtCase);
    return acc;
  }, {} as Record<string, IDebtCase[]>);
  const { handleErrorResponse } = useErrorHandling();
  const { openSnackbar } = props;
  const webSocketService = WebSocketService.getInstance(
    "http://localhost:8080/ws"
  );

  const roleSpecificEndpoint = (() => {
    switch (role) {
      case "CREDITOR":
        return `debt/cases/creditor/${username}`;
      case "DEBTOR":
        return `debt/cases/debtor/${username}`;
      case "ADMIN":
        return "debt/cases";
      default:
        return "";
    }
  })();

  const {
    data: deletedData,
    error: deleteError,
    deleteData,
  } = useDelete<any>("debt/cases/{id}/creditors/{creditorId}", {
    creditorId: debtCaseToDelete?.creditorId,
    id: debtCaseToDelete?.id,
  });

  const {
    data: fileData,
    error: fileError,
    postData,
  } = usePost<FormData>("debt/cases/creditors/{username}/file", {
    username: username,
  });

  const {
    data: debtCaseData,
    loading: debtCaseLoading,
    error,
  } = useGet<IDebtCase[]>(
    roleSpecificEndpoint,
    role === "ADMIN" ? {} : username ? { username } : {},
    shouldRefetch,
    true
  );

  const {
    data: pdfData,
    loading: fileLoading,
    error: pdfError,
    getData,
  } = useGet<any>(
    "debt/cases/generate/report/debtor/{username}",
    {
      username: username,
    },
    shouldRefetch,
    false,
    "blob"
  );

  useEffect(() => {
    if (error && [401, 403].includes(error.statusCode)) {
      handleErrorResponse(error.statusCode);
      openSnackbar(error.message, "error");
    } else if (error?.description.includes("Refresh Token")) {
      navigate("/login");
      openSnackbar("You need to login again", "warning");
    }
  }, [error, handleErrorResponse, navigate, openSnackbar]);

  useEffect(() => {
    if (!debtCaseLoading) {
      setShouldRefetch(false);
    }
  }, [debtCaseLoading]);

  useEffect(() => {
    if (debtCaseData) {
      setCurrentDebtCases(debtCaseData);
    }
  }, [debtCaseData]);

  useEffect(() => {
    if (debtCaseData && creditorId === undefined && role === "CREDITOR") {
      const userWithSameUsername = debtCaseData.find(
        (debtCase) => debtCase.creditor.user.username === username
      );
      if (userWithSameUsername) {
        setCreditorId(userWithSameUsername.creditor.id);
      }
    }
  }, [creditorId, debtCaseData, role, username]);

  useEffect(() => {
    if (role === "CREDITOR") {
      webSocketService.subscribe(
        `/user/${username}/topic/enriched-debt-cases`,
        (message) => {
          const debtCase: IDebtCase = message;
          setCurrentDebtCases((prevDebtCases) => {
            const caseExists = prevDebtCases?.some(
              (existingCase) => existingCase.id === debtCase.id
            );
            if (!caseExists) {
              setHighlightedCases((prevCases) => [...prevCases, debtCase.id]);
              setTimeout(() => {
                setHighlightedCases((prevCases) =>
                  prevCases.filter((id) => id !== debtCase.id)
                );
              }, 5000);
              return [...(prevDebtCases || []), debtCase];
            } else {
              openSnackbar(
                `Enriched ${transformDebtType(
                  debtCase.debtCaseType.type
                )} debt case for ${debtCase.debtor.name} ${
                  debtCase.debtor.surname
                } already exists`,
                "warning"
              );
            }
            return prevDebtCases;
          });
        }
      );
    }
    return () => {
      webSocketService.unsubscribeAll();
    };
  }, [role, username, openSnackbar, webSocketService]);

  useEffect(() => {
    if (downloadPdf && !fileLoading && !pdfError) {
      if (pdfData) {
        const blob = new Blob([pdfData], { type: "application/pdf" });
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `debt_cases_report_${getCurrentDateString()}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
      }
      setDownloadPdf(false);
    } else if (fileError) {
      openSnackbar("Error generating Debt Case report", "error");
      setDownloadPdf(false);
    }
  }, [pdfData, fileLoading, pdfError, downloadPdf, openSnackbar, fileError]);

  useEffect(() => {
    if (fileError) {
      openSnackbar(fileError.description, "error");
    } else if (fileData) {
      openSnackbar(
        "CSV uploaded successfully and debt cases are being enriched",
        "success"
      );
      setCreditorId(undefined);
    }
  }, [fileData, fileError, openSnackbar]);

  useEffect(() => {
    if (
      deletedData &&
      deletedData.statusCode === 204 &&
      deletedData.description.includes("Deleted successfully")
    ) {
      openSnackbar("Debt Case deleted successfully", "success");
      setCurrentDebtCases((prevDebtCases) =>
        prevDebtCases
          ? prevDebtCases.filter(
              (debtCase) => debtCase.id !== debtCaseToDelete?.id
            )
          : []
      );
      setCurrentDebtCases((updatedDebtCases) => {
        return updatedDebtCases;
      });
      setConfirmationDialogOpen(false);
      setShouldRefetch(false);
    } else if (deleteError && deleteError.statusCode !== 204) {
      openSnackbar(deleteError.description, "error");
    }
    setDebtCaseToDelete(null);
  }, [deleteError, deletedData, openSnackbar, setCurrentDebtCases]);

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      const formData = new FormData();
      formData.append("file", file);
      postData(formData, true);
    }
  };

  const handleDownloadPdf = async () => {
    setDownloadPdf(true);
    getData();
  };

  const handleEdit = (creditorId: number, id: number) => {
    navigate(`/debt/cases/${id}/creditor/${creditorId}`);
  };

  const handlePay = (id: number, amount: number) => {
    navigate(`/debt/cases/${id}/pay/${amount}`);
  };

  const handleDelete = (creditorId: number, id: number) => {
    setDebtCaseToDelete({ creditorId, id });
    setConfirmationDialogOpen(true);
  };

  const handleDeleteConfirmed = async () => {
    deleteData();
    setShouldRefetch(true);
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handleDeleteCancelled = () => {
    setConfirmationDialogOpen(false);
    setDebtCaseToDelete(null);
  };

  const getCurrentDateString = () => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");
    return `${year}${month}${day}`;
  };

  const renderActionButtons = (
    creditorId: number,
    amount: number,
    id: number,
    status: String
  ) => (
    <Grid container spacing={1} justifyContent="flex-end">
      {[
        role === "CREDITOR" || role === "ADMIN" ? "Edit" : null,
        role === "CREDITOR" || role === "ADMIN" ? "Delete" : null,
        role === "DEBTOR" && status !== "CLOSED" ? "Pay" : null,
      ]
        .filter(Boolean)
        .map((action) => (
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
                action === "Edit"
                  ? handleEdit(creditorId, id)
                  : action === "Delete"
                  ? handleDelete(creditorId, id)
                  : handlePay(id, amount)
              }
            >
              {action}
            </Button>
          </Grid>
        ))}
    </Grid>
  );

  const renderPaginationControls = () => (
    <Pagination
      className={classes.pagination}
      count={totalPages}
      page={currentPage}
      onChange={(event, page) => handlePageChange(page)}
      variant="outlined"
      shape="rounded"
      size="large"
    />
  );

  const renderAccordionSection = (
    title: string,
    cases: IDebtCase[],
    status: string,
    highlightedCases: number[]
  ) => (
    <React.Fragment>
      {cases.length > 0 && (
        <Typography sx={{ marginTop: "20px" }} variant="h6" gutterBottom>
          {title}
        </Typography>
      )}
      {cases.map((debtCase: IDebtCase, index: number) => (
        <Accordion
          key={`${status}-${index}`}
          sx={{
            background: highlightedCases.includes(debtCase.id)
              ? "#F8DE7E"
              : "white",
            boxShadow: "0px 4px 8px rgba(0, 0, 0, 0.1)",
            "&:hover": {
              boxShadow: "0px 8px 16px rgba(0, 0, 0, 0.2)",
            },
            my: 1,
          }}
        >
          <AccordionSummary
            expandIcon={<ExpandMoreIcon sx={{ color: "#2E8B57" }} />}
            sx={{ borderBottom: "1px solid #ccc" }}
          >
            <Typography style={{ marginRight: "5px", fontWeight: "bold" }}>
              {debtCase.creditor?.name}
            </Typography>
            <Typography style={{ marginRight: "5px" }}>
              {transformDebtType(debtCase.debtCaseType.type)}
            </Typography>
            <Typography style={{ marginRight: "5px" }}>
              for Debtor {debtCase.debtor.name} {debtCase.debtor.surname}
            </Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <Typography sx={{ fontWeight: "bold" }}>
                  Debt case information
                </Typography>
                <Typography>
                  Late Interest Rate: {debtCase.lateInterestRate}%
                </Typography>
                <Typography>
                  Debt Interest Rate: {debtCase.debtInterestRate}%
                </Typography>
                <Typography>Amount Owed: {debtCase.amountOwed}€</Typography>
                <Typography>Due Date: {debtCase.dueDate}</Typography>
                <Typography>Date Created: {debtCase.createdDate}</Typography>
                {debtCase.modifiedDate && (
                  <Typography>
                    Date Modified: {debtCase.modifiedDate}
                  </Typography>
                )}
              </Grid>
              <Grid item xs={6}>
                {renderDebtorDetails(debtCase.debtor)}
                {renderCreditorDetails(debtCase.creditor)}
              </Grid>
            </Grid>
            <Grid item xs={12}>
              {renderActionButtons(
                debtCase.creditor.id,
                debtCase.amountOwed,
                debtCase.id,
                debtCase.debtCaseStatus
              )}
            </Grid>
          </AccordionDetails>
        </Accordion>
      ))}
    </React.Fragment>
  );

  const renderDebtorDetails = (debtor: IDebtor) => (
    <React.Fragment>
      <Typography sx={{ fontWeight: "bold" }}>Debtor information</Typography>
      <Typography>Email: {debtor.email}</Typography>
      <Typography>Phone Number: {debtor.phoneNumber}</Typography>
      {debtor.verifiedPhoneNumberInformation &&
        debtor.verifiedPhoneNumberInformation.lineType && (
          <Typography>
            Line Type: {debtor.verifiedPhoneNumberInformation.lineType}
          </Typography>
        )}
      {debtor.verifiedPhoneNumberInformation &&
        debtor.verifiedPhoneNumberInformation.location && (
          <Typography>
            Location: {debtor.verifiedPhoneNumberInformation.location}
          </Typography>
        )}
      {debtor.verifiedPhoneNumberInformation &&
        debtor.verifiedPhoneNumberInformation.carrier && (
          <Typography>
            Carrier: {debtor.verifiedPhoneNumberInformation.carrier}
          </Typography>
        )}
      <Typography>
        Is Valid: {debtor.verifiedPhoneNumberInformation?.valid}
      </Typography>
    </React.Fragment>
  );

  const renderCreditorDetails = (creditor: ICreditor) => (
    <React.Fragment>
      <Typography sx={{ fontWeight: "bold" }}>Creditor Information</Typography>
      <Typography>Industry: {creditor.company.industry}</Typography>
      <Typography>Email: {creditor.email}</Typography>
      <Typography>Phone Number: {creditor.phoneNumber}</Typography>
      <Typography>
        Address: {creditor.address.split(",")[0]}, {creditor.company.locality}
      </Typography>
      <Typography>Account Number: {creditor.accountNumber}</Typography>{" "}
      <Typography>
        Domain:{" "}
        <a
          href={`https://${creditor.company.domain}`}
          target="_blank"
          rel="noopener noreferrer"
        >
          {creditor.company.domain}
        </a>
      </Typography>
    </React.Fragment>
  );

  const transformDebtType = (type: string): string => {
    const lowerCaseType = type.toLowerCase();
    return lowerCaseType
      .split("_")
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(" ");
  };

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
            {role === "DEBTOR" && debtCaseData && debtCaseData.length > 0 && (
              <Grid item>
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
                  onClick={handleDownloadPdf}
                >
                  Download debt report
                </Button>
              </Grid>
            )}
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
                    Upload debt cases
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
              {paginatedCases &&
                groupedDebtCases &&
                Object.entries(groupedDebtCases).map(([status, cases]) =>
                  renderAccordionSection(
                    status === "unpaid"
                      ? "Unpaid Debt Cases"
                      : status.charAt(0).toUpperCase() +
                          status.slice(1) +
                          " Debt Cases",
                    cases,
                    status,
                    highlightedCases
                  )
                )}
              <Box sx={{ textAlign: "center", marginTop: "20px" }}>
                {renderPaginationControls()}
                <ScrollToTopButton />
              </Box>
            </Box>
          )}
          {debtCaseToDelete && (
            <Dialog
              open={confirmationDialogOpen}
              onClose={handleDeleteCancelled}
            >
              <DialogTitle>Confirm Deletion</DialogTitle>
              <DialogContent>
                <Typography>
                  Are you sure you want to delete this debt case?
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

export default DebtCaseListPage;
