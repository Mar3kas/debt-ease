import React, { FC, ReactElement, useEffect, useState } from "react";
import { IPage } from "../../shared/models/page";
import { useNavigate, useParams } from "react-router-dom";
import useStyles from "../../components/page-styles/global-styles";
import useErrorHandling from "../../services/handle-responses";
import { useEdit, useGet } from "../../services/api-service";
import { IDebtCase } from "../../shared/models/debt-case";
import Footer from "../../components/page-footer/footer";
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
import { IDebtorDTO } from "../../shared/dtos/debtor-dto";
import { IDebtor } from "../../shared/models/debtor";
import Navbar from "../../components/page-navbar/navbar";
import { IDebtCaseDTO } from "../../shared/dtos/debt-case-dto";
import { IDebtCaseType } from "../../shared/models/debt-case-type";

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
    "debt/cases/{id}",
    { id: Number(debtcaseId) },
    false,
    true
  );

  const { data: debtCaseTypeData, error: debtCaseTypeError } = useGet<
    IDebtCaseType[]
  >("debt/case/types", {}, false, true);

  const {
    data: editDebtCaseData,
    error: editDebtCaseError,
    editData: editDataDebtCase,
  } = useEdit<IDebtCaseDTO>("debt/cases/{debtcaseId}/creditors/{creditorId}", {
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
      navigate("/debt/cases");
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

    const now = dayjs();
    const dueDate = dayjs(editedDebtCaseData.dueDate);
    if (dueDate.isBefore(now)) {
      setFieldErrors((prevErrors) => ({
        ...prevErrors,
        dueDate: "Due date cannot be in the past.",
      }));
      return;
    }

    const debtCaseRequest: IDebtCaseDTO = {
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
      <Paper
        className={classes.paper}
        elevation={16}
        square={true}
        sx={{
          marginTop: 3,
          marginBottom: 3,
        }}
      >
        {editDebtCaseError !== null && editDebtCaseError.statusCode !== 422 && (
          <Box
            sx={{
              flexGrow: 1,
              display: "flex",
              width: "100%",
              justifyContent: "center",
              height: "200px",
              backgroundColor: "#8FBC8F",
              marginTop: 3,
              padding: 2,
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
            {["dueDate", "debtCaseType"].map((field) => (
              <Grid item xs={12} key={field}>
                <React.Fragment>
                  <Typography variant="subtitle1" sx={{ color: "black" }}>
                    <strong>
                      {field
                        .split(/(?=[A-Z])/)
                        .map(
                          (word) =>
                            word.charAt(0).toUpperCase() +
                            word.slice(1).toLowerCase()
                        )
                        .join(" ")}
                      :
                    </strong>
                  </Typography>
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
                      fullWidth
                      className={classes.textField}
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
                        className={classes.dateTimePicker}
                      />
                    </LocalizationProvider>
                  ) : (
                    <TextField
                      type="text"
                      value={(editedDebtCaseData as any)?.[field] || ""}
                      onChange={(e) =>
                        handleInputChange(e, field as keyof IDebtCase)
                      }
                      fullWidth
                      className={classes.textField}
                    />
                  )}
                  <Typography variant="subtitle2" sx={{ color: "red" }}>
                    {fieldErrors[field]}
                  </Typography>
                </React.Fragment>
              </Grid>
            ))}
            {debtCaseData && debtCaseData.debtor && (
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={editDebtor}
                      onChange={handleCheckboxChange}
                      sx={{
                        "&.Mui-checked": {
                          color: "#8FBC8F",
                        },
                      }}
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
                  style={{ marginLeft: "1em" }}
                  fullWidth
                  className={classes.textField}
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
                    <React.Fragment>
                      <Typography variant="subtitle1" sx={{ color: "black" }}>
                        <strong>
                          {field
                            .split(/(?=[A-Z])/)
                            .map(
                              (word) =>
                                word.charAt(0).toUpperCase() +
                                word.slice(1).toLowerCase()
                            )
                            .join(" ")}
                          :
                        </strong>
                      </Typography>
                      <TextField
                        type="text"
                        value={(editedDebtor as any)?.[field] || ""}
                        onChange={(e) =>
                          handleEditDebtorChange(e, field as keyof IDebtor)
                        }
                        fullWidth
                        className={classes.textField}
                      />
                      <Typography variant="subtitle2" sx={{ color: "red" }}>
                        {fieldErrors[field]}
                      </Typography>
                    </React.Fragment>
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
              onClick={() => navigate("/debt/cases")}
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
