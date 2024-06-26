import React, { FC, ReactElement, useEffect, useState } from "react";
import { useEdit, useGet } from "../../services/api-service";
import { IDebtor } from "../../shared/models/debtor";
import useStyles from "../../components/page-styles/global-styles";
import { useNavigate } from "react-router-dom";
import { ICreditor } from "../../shared/models/creditor";
import { IAdmin } from "../../shared/models/admin";
import {
  Paper,
  Typography,
  Box,
  IconButton,
  Divider,
  Button,
  Grid,
  TextField,
  Pagination,
  List,
  ListItemText,
  ListItem,
} from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import BusinessIcon from "@mui/icons-material/Business";
import PersonIcon from "@mui/icons-material/Person";
import Navbar from "../../components/page-navbar/navbar";
import Footer from "../../components/page-footer/footer";
import EditIcon from "@mui/icons-material/Edit";
import { IPage } from "../../shared/models/page";
import useErrorHandling from "../../services/handle-responses";
import { ICreditorDTO } from "../../shared/dtos/creditor-dto";
import { IDebtorDTO } from "../../shared/dtos/debtor-dto";
import AuthService from "../../services/jwt-service";
import { IPayment } from "../../shared/models/payment";

const UserProfilePage: FC<IPage> = (props): ReactElement => {
  const authService = AuthService.getInstance();
  const username = authService.getUsername();
  const classes = useStyles("light");
  const navigate = useNavigate();
  const { openSnackbar } = props;
  const [shouldRefetch, setShouldRefetch] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<{ [key: string]: string }>({});
  const [editMode, setEditMode] = useState(false);
  const [editedData, setEditedData] = useState<ICreditor | IDebtor | null>(
    null
  );
  const [editCompleted, setEditCompleted] = useState(false);
  const { handleErrorResponse } = useErrorHandling();
  const { data, loading, error } = useGet<ICreditor | IDebtor | IAdmin>(
    "users/{username}",
    username ? { username: username } : {},
    shouldRefetch,
    true
  );

  const { data: payments, error: paymentsError } = useGet<IPayment[]>(
    "payments/{username}",
    username ? { username: username } : {},
    shouldRefetch,
    true
  );
  const canEditProfile = data?.user?.role !== "ADMIN";

  const [currentPage, setCurrentPage] = useState(1);
  const [paymentsPerPage] = useState(5);
  const totalPayments = payments?.length || 0;
  const totalPages = Math.ceil(totalPayments / paymentsPerPage);
  const startIndex = (currentPage - 1) * paymentsPerPage;
  const endIndex = Math.min(startIndex + paymentsPerPage, totalPayments);
  const paginatedPayments = payments?.slice(startIndex, endIndex);

  const roleSpecificEndpoint =
    data?.user?.role === "CREDITOR"
      ? "creditors/{id}"
      : data?.user?.role === "DEBTOR"
      ? "debtors/{id}"
      : "";

  const { error: editError, editData } = useEdit<ICreditor | IDebtorDTO>(
    roleSpecificEndpoint,
    { id: data?.id }
  );

  useEffect(() => {
    setEditedData(data ? { ...data } : null);
  }, [data]);

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
    if (paymentsError && [401, 403].includes(paymentsError.statusCode)) {
      handleErrorResponse(paymentsError.statusCode);
      openSnackbar(paymentsError.message, "error");
    } else if (paymentsError?.description.includes("Refresh Token")) {
      navigate("/login");
      openSnackbar("You need to login again", "warning");
    }
  }, [paymentsError, handleErrorResponse, navigate, openSnackbar]);

  useEffect(() => {
    if (editError !== null && editError.statusCode === 422) {
      const fieldErrors = JSON.parse(editError.description);
      setFieldErrors(fieldErrors);
    } else if (editError && [401, 403].includes(editError.statusCode)) {
      handleErrorResponse(editError.statusCode);
      openSnackbar(editError.message, "error");
    } else if (editCompleted) {
      setFieldErrors({});
      setEditMode(false);
      setShouldRefetch((prev) => !prev);
      openSnackbar("Profile edited successfully", "success");
    }
  }, [editError, editCompleted, openSnackbar, handleErrorResponse]);

  const handleSaveChanges = async () => {
    if (!editedData) {
      return;
    }

    let userEditedData: ICreditorDTO | IDebtorDTO;

    if ("accountNumber" in editedData) {
      const data = editedData as ICreditor;
      userEditedData = {
        name: data.name,
        address: data.address,
        phoneNumber: data.phoneNumber,
        email: data.email,
        accountNumber: data.accountNumber,
      };
    } else {
      const data = editedData as IDebtor;
      userEditedData = {
        name: data.name,
        surname: data.surname,
        email: data.email || "",
        phoneNumber: data.phoneNumber || "",
      };
    }

    await editData(userEditedData);

    setEditCompleted(true);

    setTimeout(() => setEditCompleted(false), 1000);
  };

  const handleEditModeToggle = () => {
    setEditMode((prevEditMode) => {
      if (prevEditMode) {
        setEditedData(data ? { ...data } : null);
      }
      return !prevEditMode;
    });
  };

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
    field: string
  ) => {
    setFieldErrors((prevErrors) => ({ ...prevErrors, [field]: "" }));

    setEditedData((prevData) => {
      if (prevData) {
        return { ...prevData, [field]: e.target.value };
      }
      return prevData;
    });
  };

  const renderFields = () =>
    editedData ? <>{renderAdditionalFields()}</> : null;

  const renderAdditionalFields = () => {
    if (data && editedData) {
      let additionalFields;

      if (data.user?.username === username) {
        if (data.user?.role === "CREDITOR") {
          const editedData = data as ICreditor;
          additionalFields = (
            <>
              <Typography>
                <strong>Address: </strong>
                {renderField("address", "address", editedData?.address)}
              </Typography>
              <Typography>
                <strong>Phone Number: </strong>
                {renderField(
                  "phoneNumber",
                  "phoneNumber",
                  editedData?.phoneNumber
                )}
              </Typography>
              <Typography>
                <strong>Email: </strong>
                {renderField("email", "email", editedData?.email)}
              </Typography>
              <Typography>
                <strong>Bank Account Number: </strong>
                {renderField(
                  "acountNumber",
                  "accountNumber",
                  editedData?.accountNumber
                )}
              </Typography>
            </>
          );
        } else if (data.user.role === "DEBTOR") {
          const editedData = data as IDebtor;
          additionalFields = (
            <>
              <Typography>
                <strong>Surname: </strong>
                {renderField("surname", "surname", editedData?.surname)}
              </Typography>
              <Typography>
                <strong>Email: </strong>
                {renderField("email", "email", editedData?.email)}
              </Typography>
              <Typography>
                <strong>Phone Number: </strong>
                {renderField(
                  "phoneNumber",
                  "phoneNumber",
                  editedData?.phoneNumber
                )}
              </Typography>
            </>
          );
        }
      }

      return additionalFields;
    }

    return null;
  };

  const renderField = (
    label: string,
    field: string,
    value: string | undefined
  ) => (
    <>
      {editMode ? (
        <>
          <TextField
            style={{ width: "100%", marginBottom: "1rem" }}
            defaultValue={value}
            onChange={(e) => handleInputChange(e, field)}
            className={classes.textField}
          />
          <span style={{ color: "red" }}>{fieldErrors[field]}</span>
        </>
      ) : (
        value
      )}
    </>
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

  const transformDebtType = (type: string): string => {
    const lowerCaseType = type.toLowerCase();
    return lowerCaseType
      .split("_")
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(" ");
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const renderPaymentHistory = () => {
    if (data && data.user?.role === "DEBTOR") {
      return (
        <Paper elevation={3} square sx={{ padding: 2 }}>
          <Typography variant="h5" sx={{ marginBottom: 1 }}>
            Payment History
          </Typography>
          <List>
            {paginatedPayments &&
              paginatedPayments.map((payment) => (
                <ListItem key={payment.id}>
                  <ListItemText
                    primary={`Payment for ${
                      payment.debtCase.creditor.name
                    } ${transformDebtType(
                      payment.debtCase.debtCaseType.type
                    )} ${payment.paymentDate}`}
                    secondary={`${payment.amount}€ ${payment.paymentMethod}`}
                  />
                </ListItem>
              ))}
          </List>
          <Box sx={{ textAlign: "center", marginTop: "20px" }}>
            {renderPaginationControls()}
          </Box>
        </Paper>
      );
    }
    return null;
  };

  const renderUserProfile = () => {
    if (data) {
      let icon;
      let title;
      let additionalContent;

      if (data.user?.username === username) {
        if (data.user?.role === "CREDITOR") {
          const creditorData = data as ICreditor;
          icon = <BusinessIcon sx={{ fontSize: "3rem" }} />;
          title = "Creditor";
          additionalContent = (
            <>
              <Typography variant="subtitle1">
                <strong>Address:</strong> {creditorData.address}
              </Typography>
              <Typography variant="subtitle1">
                <strong>Phone Number:</strong> {creditorData.phoneNumber}
              </Typography>
              <Typography variant="subtitle1">
                <strong>Email:</strong> {creditorData.email}
              </Typography>
              <Typography variant="subtitle1">
                <strong>Bank Account Number:</strong>{" "}
                {creditorData.accountNumber}
              </Typography>
            </>
          );
        } else if (data.user?.role === "DEBTOR") {
          const debtorData = data as IDebtor;
          icon = <PersonIcon sx={{ fontSize: "3rem" }} />;
          title = "Debtor";
          additionalContent = (
            <>
              <Typography variant="subtitle1">
                <strong>Surname:</strong> {debtorData.surname}
              </Typography>
              <Typography variant="subtitle1">
                <strong>Email:</strong> {debtorData.email}
              </Typography>
              <Typography variant="subtitle1">
                <strong>Phone Number:</strong> {debtorData.phoneNumber}
              </Typography>
            </>
          );
        } else if (data.user?.role === "ADMIN") {
          const adminData = data as IAdmin;
          icon = <AccountCircleIcon sx={{ fontSize: "3rem" }} />;
          title = "Admin";
          additionalContent = (
            <>
              <Typography variant="subtitle1">
                <strong>Surname:</strong> {adminData.surname}
              </Typography>
            </>
          );
        }
      }

      return (
        <Box>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={2} md={1}>
              <IconButton onClick={() => navigate(-1)} aria-label="Back">
                <ArrowBackIcon />
              </IconButton>
            </Grid>
            <Grid item xs={10} md={7} lg={6}>
              <Typography
                variant="h4"
                sx={{
                  fontSize: { xs: "1.5rem", md: "2rem", lg: "2.5rem" },
                  display: "flex",
                  alignItems: "center",
                }}
              >
                {icon}
                <span style={{ marginLeft: "0.5rem" }}>{title}</span>
                {canEditProfile && !editMode && (
                  <IconButton onClick={handleEditModeToggle} aria-label="Edit">
                    <EditIcon sx={{ color: "#8FBC8F" }} />
                  </IconButton>
                )}
              </Typography>
            </Grid>
          </Grid>
          <Divider sx={{ marginBottom: 2 }} />
          <Typography variant="subtitle1">
            <strong>Name:</strong>{" "}
            {renderField("name", "name", editedData?.name)}
          </Typography>
          {editMode && renderFields()}
          {!editMode && additionalContent}
          {editMode && (
            <Box
              sx={{
                marginTop: 1,
                display: "flex",
                justifyContent: "flex-end",
              }}
            >
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
                onClick={handleSaveChanges}
              >
                Save
              </Button>
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
                onClick={handleEditModeToggle}
              >
                Cancel
              </Button>
            </Box>
          )}
        </Box>
      );
    }

    return null;
  };

  return (
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
        backgroundColor: "#8FBC8F",
      }}
    >
      <Navbar title="DebtEase" />
      {data?.user?.role === "DEBTOR" && (
        <Box sx={{ display: "flex", justifyContent: "center", mt: 2 }}>
          <Grid container spacing={2} justifyContent="center">
            <Grid item xs={12} md={10} lg={8}>
              {loading && (
                <Box
                  sx={{
                    flexGrow: 1,
                    display: "flex",
                    width: "100%",
                    justifyContent: "center",
                    height: "200px",
                    marginTop: 3,
                  }}
                >
                  Loading...
                </Box>
              )}
              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <Paper elevation={16} square sx={{ padding: 2 }}>
                    {renderUserProfile()}
                  </Paper>
                </Grid>
                <Grid item xs={12} md={6}>
                  <Paper elevation={16} square>
                    {renderPaymentHistory()}
                  </Paper>
                </Grid>
              </Grid>
              {error !== null && error.statusCode !== 422 && (
                <Box
                  sx={{
                    flexGrow: 1,
                    display: "flex",
                    width: "100%",
                    justifyContent: "center",
                    height: "200px",
                    marginTop: 3,
                  }}
                >
                  <Typography variant="body1" color="red">
                    {error.description}
                  </Typography>
                </Box>
              )}
              {error &&
                (error.statusCode === 404 || error?.statusCode === 403) && (
                  <>
                    {handleErrorResponse(error.statusCode)}
                    {openSnackbar(error.message, "error")}
                  </>
                )}
            </Grid>
          </Grid>
        </Box>
      )}
      {data?.user?.role !== "DEBTOR" && (
        <Paper className={classes.paper} elevation={16} square={true}>
          <Box sx={{ padding: 2 }}>
            {renderUserProfile()}
            {error !== null && error.statusCode !== 422 && (
              <Box
                sx={{
                  flexGrow: 1,
                  display: "flex",
                  width: "100%",
                  justifyContent: "center",
                  height: "200px",
                  marginTop: 3,
                }}
              >
                <Typography variant="body1" color="red">
                  {error.description}
                </Typography>
              </Box>
            )}
            {error &&
              (error.statusCode === 404 || error?.statusCode === 403) && (
                <>
                  {handleErrorResponse(error.statusCode)}
                  {openSnackbar(error.message, "error")}
                </>
              )}
          </Box>
        </Paper>
      )}
      <Footer />
    </Box>
  );
};

export default UserProfilePage;
