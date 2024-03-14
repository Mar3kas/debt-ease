import React, { FC, ReactElement, useEffect, useState } from "react";
import { IPage } from "../../shared/models/Page";
import { useNavigate, useParams } from "react-router-dom";
import useStyles from "../../Components/Styles/global-styles";
import useErrorHandling from "../../services/handle-responses";
import { useEdit, useGet } from "../../services/api-service";
import { IDebtCase } from "../../shared/models/Debtcases";
import Footer from "../../Components/Footer/footer";
import {
  Box,
  Button,
  Checkbox,
  Divider,
  FormControlLabel,
  Grid,
  MenuItem,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import { DateTimePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs, { Dayjs } from "dayjs";
import { IDebtorDTO } from "../../shared/dtos/DebtorDTO";
import { IDebtor } from "../../shared/models/Debtor";
import Navbar from "../../Components/Navbar/navbar";
import { IDebtCaseDTO } from "../../shared/dtos/DebtCaseDTO";
import { IDebtCaseType } from "../../shared/models/DebtCaseType";

const DebtCaseFormPage: FC<IPage> = (props): ReactElement => {
  const classes = useStyles("light");
  const { creditorId, debtcaseId } = useParams<{
    creditorId?: string;
    debtcaseId?: string;
  }>();
  const { handleErrorResponse } = useErrorHandling();
  const [fieldErrors, setFieldErrors] = useState<{ [key: string]: string }>({});
  const [editCompleted, setEditCompleted] = useState(false);
  const [editDebtor, setEditDebtor] = useState(false);
  const [selectedDebtor, setSelectedDebtor] = useState<IDebtor | null>(null);
  const [editedDebtor, setEditedDebtor] = useState<IDebtor | null>(null);
  const [editedDebtCaseData, setEditedDebtCaseData] =
    useState<IDebtCase | null>(null);
  const { openSnackbar } = props;
  const navigate = useNavigate();

  const { data: debtCaseData, error: debtCaseError } = useGet<IDebtCase>(
    "debtcases/{id}",
    { id: Number(debtcaseId) }
  );

  const { data: debtCaseTypeData, error: debtCaseTypeError } = useGet<
    IDebtCaseType[]
  >("debtcase/types", {});

  const {
    data: editDebtCaseData,
    error: editDebtCaseError,
    editData: editDataDebtCase,
  } = useEdit<IDebtCaseDTO>("debtcases/{debtcaseId}/creditors/{creditorId}", {
    debtcaseId: Number(debtcaseId),
    creditorId: Number(creditorId),
  });

  const {
    data: editDebtorData,
    error: editDebtorError,
    editData: editDataDebtor,
  } = useEdit<IDebtorDTO>("debtors/{id}", { id: selectedDebtor?.id });

  useEffect(
    () => handleAPIError(debtCaseError, openSnackbar, null),
    [debtCaseError, openSnackbar]
  );
  useEffect(
    () => handleAPIError(debtCaseTypeError, openSnackbar, null),
    [debtCaseTypeError, openSnackbar]
  );
  useEffect(
    () => handleAPIError(editDebtCaseError, openSnackbar, editCompleted),
    [editCompleted, editDebtCaseError, openSnackbar]
  );
  useEffect(
    () => handleAPIError(editDebtorError, openSnackbar, editCompleted),
    [editCompleted, editDebtorError, openSnackbar]
  );

  useEffect(
    () =>
      setEditedDebtCaseData(
        debtCaseData
          ? { ...debtCaseData, debtCaseType: debtCaseData.debtCaseType || {} }
          : null
      ),
    [debtCaseData]
  );

  useEffect(() => {
    if (editDebtor && editDebtCaseData && editDebtorData) {
      setEditCompleted(true);
      setFieldErrors({});
      const timeoutId = setTimeout(() => setEditCompleted(false), 1000);

      return () => clearTimeout(timeoutId);
    } else if (!editDebtor && editDebtCaseData) {
      setEditCompleted(true);
      setFieldErrors({});
      const timeoutId = setTimeout(() => setEditCompleted(false), 1000);

      return () => clearTimeout(timeoutId);
    }
  }, [editDebtCaseData, editDebtorData, editDebtor]);

  const handleAPIError = (
    error: any,
    snackbar: any,
    editCompleted: boolean | null
  ) => {
    if (error && [401, 403].includes(error.statusCode)) {
      handleErrorResponse(error.statusCode);
      snackbar(error.message, "error");
    } else if (error?.description.includes("Refresh Token")) {
      navigate("/login");
      openSnackbar("You need to login again", "warning");
    } else if (
      error &&
      error.statusCode === 422 &&
      Object.keys(fieldErrors).length > 0
    ) {
      const fieldErrors = JSON.parse(error.description);
      setFieldErrors((prevErrors) => ({ ...prevErrors, ...fieldErrors }));
    } else if (error) {
      snackbar(error.message, "error");
    } else if (Object.keys(fieldErrors).length === 0 && editCompleted) {
      setFieldErrors({});
      navigate("/debtcases");
      openSnackbar("Debt Case edited successfully", "success");
    }
  };

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
    field: keyof IDebtCase
  ) => {
    setFieldErrors((prevErrors) => ({ ...prevErrors, [field]: "" }));
    setEditedDebtCaseData((prevData) => {
      if (prevData) {
        return {
          ...prevData,
          [field]:
            field === "debtCaseType"
              ? { ...prevData.debtCaseType, id: e.target.value }
              : e.target.value,
        };
      }
      return prevData;
    });
  };

  const handleDateTimeChange = (date: Dayjs | null, field: keyof IDebtCase) => {
    setFieldErrors((prevErrors) => ({ ...prevErrors, [field]: "" }));
    setEditedDebtCaseData((prevData) => {
      if (prevData) {
        const dateToSet = date
          ? dayjs(date).format("YYYY-MM-DD HH:mm:ss")
          : null;
        const formattedDate = dateToSet?.replace(/[ZT]/g, "");
        return { ...prevData, [field]: formattedDate };
      }
      return prevData;
    });
  };

  const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEditDebtor(e.target.checked);
  };

  const handleEditDebtorChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
    field: keyof IDebtor
  ) => {
    setFieldErrors((prevErrors) => ({ ...prevErrors, [field]: "" }));
    setEditedDebtor((prevData) => {
      if (prevData) {
        return {
          ...prevData,
          [field]: e.target.value,
        };
      }
      return prevData;
    });
  };

  const handleSaveChanges = async () => {
    if (!editedDebtCaseData) {
      return;
    }

    const debtCaseRequest: IDebtCaseDTO = {
      amountOwed: editedDebtCaseData.amountOwed,
      dueDate: editedDebtCaseData.dueDate,
      typeId: editedDebtCaseData.debtCaseType.id,
    };

    if (editDebtor && editedDebtor && selectedDebtor) {
      const debtorRequest: IDebtorDTO = {
        name: editedDebtor.name,
        surname: editedDebtor.surname,
        email: editedDebtor.email,
        phoneNumber: editedDebtor.phoneNumber,
      };

      await editDataDebtor(debtorRequest);
    }

    await editDataDebtCase(debtCaseRequest);
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
      <Paper className={classes.paper} elevation={16} square={true}>
        {editDebtCaseError !== null && editDebtCaseError.statusCode !== 422 && (
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
              {editDebtCaseError.description}
            </Typography>
          </Box>
        )}
        <Box>
          <Typography variant="h5" color="black" align="center">
            Debt Case Edit
          </Typography>
          <Divider sx={{ marginBottom: 2 }} />
        </Box>
        <form>
          <Grid container spacing={2} sx={{ padding: 0.5 }}>
            {["amountOwed", "dueDate", "debtCaseType"].map((field) => (
              <Grid item xs={12} key={field}>
                <div>
                  <label style={{ color: "black", marginRight: "5px" }}>
                    {field
                      .split(/(?=[A-Z])/)
                      .map(
                        (word) =>
                          word.charAt(0).toUpperCase() +
                          word.slice(1).toLowerCase()
                      )
                      .join(" ")}
                  </label>
                  {field === "debtCaseType" ? (
                    <TextField
                      select
                      value={
                        editedDebtCaseData && editedDebtCaseData.debtCaseType
                          ? editedDebtCaseData.debtCaseType.id || ""
                          : ""
                      }
                      onChange={(e) =>
                        handleInputChange(e, field as keyof IDebtCase)
                      }
                      style={{ width: "30%" }}
                    >
                      {debtCaseTypeData?.map((debtCaseType) => (
                        <MenuItem key={debtCaseType.id} value={debtCaseType.id}>
                          {debtCaseType.type}
                        </MenuItem>
                      ))}
                    </TextField>
                  ) : field === "dueDate" ? (
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                      <DateTimePicker
                        value={dayjs((editedDebtCaseData as any)?.[field])}
                        views={["year", "day", "hours", "minutes", "seconds"]}
                        onChange={(date) =>
                          handleDateTimeChange(date, field as keyof IDebtCase)
                        }
                      />
                    </LocalizationProvider>
                  ) : (
                    <input
                      type="text"
                      value={(editedDebtCaseData as any)?.[field] || ""}
                      onChange={(e) =>
                        handleInputChange(e, field as keyof IDebtCase)
                      }
                      style={{ width: "30%" }}
                    />
                  )}
                  <span style={{ color: "red" }}>{fieldErrors[field]}</span>
                </div>
              </Grid>
            ))}
            {debtCaseData && debtCaseData.debtor && (
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={editDebtor}
                      onChange={handleCheckboxChange}
                    />
                  }
                  label="Edit Debtor"
                />
              </Grid>
            )}
            {editDebtor && editedDebtCaseData && (
              <>
                <TextField
                  select
                  value={selectedDebtor ? selectedDebtor.id : ""}
                  onChange={(e) => {
                    const selectedDebtor = editedDebtCaseData.debtor;
                    setSelectedDebtor(selectedDebtor || null);
                    setEditedDebtor(selectedDebtor || null);
                  }}
                  style={{ width: "30%", marginLeft: "20px" }}
                >
                  <MenuItem
                    key={editedDebtCaseData.debtor.id}
                    value={editedDebtCaseData.debtor.id}
                  >
                    {editedDebtCaseData.debtor.name}{" "}
                    {editedDebtCaseData.debtor.surname}
                  </MenuItem>
                </TextField>
                {["name", "surname", "email", "phoneNumber"].map((field) => (
                  <Grid item xs={12} key={field}>
                    <div>
                      <label style={{ color: "black", marginRight: "5px" }}>
                        {field
                          .split(/(?=[A-Z])/)
                          .map(
                            (word) =>
                              word.charAt(0).toUpperCase() +
                              word.slice(1).toLowerCase()
                          )
                          .join(" ")}
                      </label>
                      <input
                        type="text"
                        value={(editedDebtor as any)?.[field] || ""}
                        onChange={(e) =>
                          handleEditDebtorChange(e, field as keyof IDebtor)
                        }
                        style={{ width: "30%" }}
                      />
                      <span style={{ color: "red" }}>{fieldErrors[field]}</span>
                    </div>
                  </Grid>
                ))}
              </>
            )}
          </Grid>
          <Box
            sx={{
              marginTop: 1,
              display: "flex",
              justifyContent: "flex-end",
              padding: 1,
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
              onClick={() => navigate("/debtcases")}
            >
              Cancel
            </Button>
          </Box>
        </form>
      </Paper>
      <Footer />
    </Box>
  );
};

export default DebtCaseFormPage;
