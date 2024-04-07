import React, { FC, ReactElement, useEffect, useState } from "react";
import {
  Paper,
  Typography,
  Box,
  Button,
  Grid,
  Divider,
  TextField,
} from "@mui/material";
import Navbar from "../../components/page-navbar/navbar";
import Footer from "../../components/page-footer/footer";
import { IPage } from "../../shared/models/page";
import useStyles from "../../components/page-styles/global-styles";
import { useEdit, useGet } from "../../services/api-service";
import { IDebtorDTO } from "../../shared/dtos/debtor-dto";
import { useNavigate, useParams } from "react-router-dom";
import useErrorHandling from "../../services/handle-responses";
import { IDebtor } from "../../shared/models/debtor";

const DebtorFormPage: FC<IPage> = (props): ReactElement => {
  const classes = useStyles("light");
  const { openSnackbar } = props;
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const { handleErrorResponse } = useErrorHandling();
  const [fieldErrors, setFieldErrors] = useState<{ [key: string]: string }>({});
  const [editCompleted, setEditCompleted] = useState(false);
  const [editedData, setEditedData] = useState<IDebtor | null>(null);

  const { error: editError, editData } = useEdit<IDebtorDTO>(`debtors/${id}`, {
    id: Number(id),
  });

  const { data: debtorData, error: debtorError } = useGet<IDebtor>(
    `debtors/${id}`,
    { id: Number(id) },
    false,
    true
  );

  useEffect(() => {
    handleAPIError(debtorError || editError, openSnackbar);
  }, [debtorError, editError, openSnackbar]);

  useEffect(() => {
    setEditedData(debtorData ? { ...debtorData } : null);
  }, [debtorData]);

  const handleAPIError = (error: any, snackbar: any) => {
    if (error && [401, 403].includes(error.statusCode)) {
      handleErrorResponse(error.statusCode);
      snackbar(error.message, "error");
    } else if (error?.description.includes("Refresh Token")) {
      navigate("/login");
      openSnackbar("You need to login again", "warning");
    } else if (error && error.statusCode === 422) {
      const fieldErrors = JSON.parse(error.description);
      setFieldErrors(fieldErrors);
    } else if (editCompleted) {
      setFieldErrors({});
      navigate(-1);
      openSnackbar("Profile edited successfully", "success");
    }
  };

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
    field: keyof IDebtor
  ) => {
    setFieldErrors((prevErrors) => ({ ...prevErrors, [field]: "" }));

    setEditedData((prevData) => {
      if (prevData) {
        return { ...prevData, [field]: e.target.value };
      }
      return prevData;
    });
  };

  const handleSaveChanges = async () => {
    if (!editedData) {
      return;
    }

    const debtorEditedData: IDebtorDTO = {
      name: editedData.name,
      surname: editedData.surname,
      email: editedData.email,
      phoneNumber: editedData.phoneNumber,
    };

    await editData(debtorEditedData);
    setEditCompleted(true);

    setTimeout(() => setEditCompleted(false), 1000);
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
        {editError && editError.statusCode !== 422 && (
          <Box
            sx={{
              flexGrow: 1,
              display: "flex",
              width: "100%",
              justifyContent: "center",
              marginTop: 3,
            }}
          >
            <Typography variant="body1" color="red">
              {editError.description}
            </Typography>
          </Box>
        )}
        <Box>
          <Typography variant="h5" color="black" align="center">
            Debtor Profile Edit
          </Typography>
          <Divider sx={{ marginBottom: 2 }} />
        </Box>
        <form>
          <Grid container spacing={2} sx={{ padding: 0.5 }}>
            {[
              { label: "Name", field: "name" },
              { label: "Surname", field: "surname" },
              { label: "Email", field: "email" },
              { label: "Phone Number", field: "phoneNumber" },
            ].map(({ label, field }) => (
              <Grid item xs={12} key={field}>
                <TextField
                  fullWidth
                  label={label}
                  value={(editedData as any)?.[field] || ""}
                  onChange={(e) => handleInputChange(e, field as keyof IDebtor)}
                  error={!!fieldErrors[field]}
                  helperText={fieldErrors[field]}
                  className={classes.textField}
                />
              </Grid>
            ))}
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
              onClick={() => navigate(-1)}
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

export default DebtorFormPage;
