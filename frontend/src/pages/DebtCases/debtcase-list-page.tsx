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
import { IDebtor } from "../../shared/models/Debtor";
import WebSocketService from "../../services/websocket-service";
import { ICreditor } from "../../shared/models/Creditor";
import { ICompany } from "../../shared/models/CompanyInformation";

const DebtcaseListPage: FC<IPage> = (props): ReactElement => {
  const classes = useStyles("light");
  const navigate = useNavigate();
  const role = localStorage.getItem("role");
  const username = localStorage.getItem("username") ?? "";
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
  const { handleErrorResponse } = useErrorHandling();
  const { openSnackbar } = props;

  const roleSpecificEndpoint = (() => {
    switch (role) {
      case "CREDITOR":
        return `debtcases/creditor/${username}`;
      case "DEBTOR":
        return `debtcases/debtor/${username}`;
      case "ADMIN":
        return "debtcases";
      default:
        return "";
    }
  })();

  const {
    data: deletedData,
    error: deleteError,
    deleteData,
  } = useDelete<any>("debtcases/{id}/creditors/{creditorId}", {
    creditorId: debtCaseToDelete?.creditorId,
    id: debtCaseToDelete?.id,
  });

  const {
    data: fileData,
    error: fileError,
    postData,
  } = usePost<FormData>("debtcases/creditors/{username}/file", {
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
    "debtcases/generate/report/debtor/{username}",
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
  }, [error, openSnackbar]);

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
  }, [debtCaseData]);

  useEffect(() => {
    const webSocketService = WebSocketService.getInstance(
      "http://localhost:8080/ws"
    );
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
  }, []);

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
  }, [pdfData, fileLoading, pdfError, downloadPdf, openSnackbar]);

  useEffect(() => {
    if (fileData) {
      openSnackbar(
        "CSV uploaded successfully and debt cases are being enriched",
        "success"
      );
      setCreditorId(undefined);
    } else if (fileError) {
      openSnackbar(fileError.description, "error");
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
    navigate(`/debtcases/${id}/creditor/${creditorId}`);
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

  const getCurrentDateString = () => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");
    return `${year}${month}${day}`;
  };

  const renderActionButtons = (creditorId: number, id: number) => (
    <Grid container spacing={1} justifyContent="flex-end">
      {["Edit", "Delete"].filter(Boolean).map((action) => (
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
                : handleDelete(creditorId, id)
            }
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

  const renderAccordionSection = (
    title: string,
    cases: IDebtCase[],
    status: string,
    highlightedCases: number[]
  ) => (
    <div>
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
            <Typography sx={{ fontWeight: "bold" }}>
              Debt case information
            </Typography>
            <Typography>
              Late Interest Rate: {debtCase.lateInterestRate}%
            </Typography>
            <>
              {debtCase.outstandingBalance > 0 ? (
                <>
                  <Typography>
                    Initial Amount Owed: {debtCase.amountOwed}€
                  </Typography>
                  <Typography>
                    Outstanding Balance: {debtCase.outstandingBalance}€
                  </Typography>
                </>
              ) : (
                <Typography>Amount Owed: {debtCase.amountOwed}€</Typography>
              )}
            </>
            <Typography>Due Date: {debtCase.dueDate}</Typography>
            <Typography>Date Created: {debtCase.createdDate}</Typography>
            {debtCase.modifiedDate && (
              <Typography>Date Modified: {debtCase.modifiedDate}</Typography>
            )}
            {renderDebtorDetails(debtCase.debtor)}
            {renderCreditorDetails(debtCase.creditor)}
            {(role === "CREDITOR" || role === "ADMIN") && (
              <>{renderActionButtons(debtCase.creditor.id, debtCase.id)}</>
            )}
          </AccordionDetails>
        </Accordion>
      ))}
    </div>
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
                  Download generated report
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
              {currentDebtCases &&
                groupedDebtCases &&
                Object.entries(groupedDebtCases).map(([status, cases]) =>
                  renderAccordionSection(
                    status === "unpaid"
                      ? "Unpaid Debt Cases"
                      : status.charAt(0).toUpperCase() +
                          status.slice(1) +
                          " Debt Cases",
                    currentDebtCases.filter(
                      (debtCase) =>
                        debtCase.debtCaseStatus.status.toLowerCase() === status
                    ),
                    status,
                    highlightedCases
                  )
                )}
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

export default DebtcaseListPage;
