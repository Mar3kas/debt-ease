import React, { FC, ReactElement, useEffect, useState } from "react";
import {
  Box,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Button,
  Typography,
  Grid,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import useStyles from "../../Components/Styles/global-styles";
import { useNavigate } from "react-router-dom";
import Navbar from "../../Components/Navbar/navbar";
import Footer from "../../Components/Footer/footer";
import { IPage } from "../../shared/models/Page";
import { IDebtCase } from "../../shared/models/Debtcases";
import { useGet } from "../../services/api-service";
import useErrorHandling from "../../services/handle-responses";

const DebtcaseListPage: FC<IPage> = (props): ReactElement => {
  const classes = useStyles("light");
  const navigate = useNavigate();
  const username = localStorage.getItem("username");
  const role = localStorage.getItem("role");
  const [shouldRefetch, setShouldRefetch] = useState(false);
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

  useEffect(() => {
    if (error && (error.statusCode === 404 || error?.statusCode === 403)) {
      handleErrorResponse(error.statusCode);
      openSnackbar(error.message, 'error');
    }
  }, [error, openSnackbar]);

  const handleEdit = (id: number) => {
    // Implement edit functionality
  };

  const handleDelete = (id: number) => {
    // Implement delete functionality
  };

const renderActionButtons = (id: number) => (
  <Grid container spacing={1} justifyContent="flex-end">
    {["Edit", "Delete"].map((action) => (
      <Grid item key={`${action}-${id}`}>
        <Button
          sx={{
            color: "black",
            backgroundColor: "white",
            border: "3px solid #8FBC8F",
          }}
          onClick={() => (action === "Edit" ? handleEdit(id) : handleDelete(id))}
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
      <Typography variant="h6" gutterBottom>
        {title}
      </Typography>
      {cases.map((debtCase: IDebtCase, index: number) => (
        <Accordion
        key={`${status}-${index}`}
          sx={{
            background: "white",
            boxShadow: "0px 4px 8px rgba(0, 0, 0, 0.1)",
            borderRadius: "8px",
            marginBottom: "16px",
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
            {(role === "CREDITOR" || role === "ADMIN") && debtCase.debtors?.map(renderDebtorDetails)}
            {renderActionButtons(debtCase.id)}
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
        </Box>
        <Footer />
      </Box>
    </>
  );
};

export default DebtcaseListPage;