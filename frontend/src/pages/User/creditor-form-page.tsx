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
import { useNavigate, useParams } from "react-router-dom";
import useErrorHandling from "../../services/handle-responses";
import { ICreditor } from "../../shared/models/creditor";
import { ICreditorDTO } from "../../shared/dtos/creditor-dto";

const CreditorFormPage: FC<IPage> = (props): ReactElement => {
  const classes = useStyles("light");
  const { openSnackbar } = props;
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const { handleErrorResponse } = useErrorHandling();
  const [fieldErrors, setFieldErrors] = useState<{ [key: string]: string }>({});
  const [editCompleted, setEditCompleted] = useState(false);
  const [editedData, setEditedData] = useState<ICreditor | null>(null);

  const {
    data: creditorEditData,
    error: editError,
    editData,
  } = useEdit<ICreditorDTO>(`creditors/${id}`, { id: Number(id) });

  const { data: creditorData, error: creditorError } = useGet<ICreditor>(
    `creditors/${id}`,
    { id: Number(id) },
    false,
    true
  );

  useEffect(() => {
    handleAPIError(creditorError, openSnackbar, null);
  }, [creditorError, openSnackbar]);

  useEffect(() => {
    handleAPIError(editError, openSnackbar, editCompleted);
  }, [editCompleted, editError, openSnackbar]);

  useEffect(() => {
    setEditedData(creditorData ? { ...creditorData } : null);
  }, [creditorData]);

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
    } else if (error && error.statusCode === 422) {
      const fieldErrors = JSON.parse(error.description);
      setFieldErrors(fieldErrors);
    } else if (error) {
      snackbar(error.message, "error");
    } else if (Object.keys(fieldErrors).length === 0 && editCompleted) {
      navigate("/users");
      openSnackbar("Profile edited successfully", "success");
    }
  };

  useEffect(() => {
    if (creditorEditData !== null) {
      setEditCompleted(true);
      setFieldErrors({});
      const timeoutId = setTimeout(() => setEditCompleted(false), 1000);

      return () => clearTimeout(timeoutId);
    }
  }, [creditorEditData]);

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
    field: keyof ICreditor
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

    const creditorEditedData: ICreditorDTO = {
      name: editedData.name,
      address: editedData.address,
      phoneNumber: editedData.phoneNumber,
      email: editedData.email,
      accountNumber: editedData.accountNumber,
    };

    await editData(creditorEditedData);
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
        {editError !== null && editError.statusCode !== 422 && (
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
              {editError.description}
            </Typography>
          </Box>
        )}
        <Box>
          <Typography variant="h5" color="black" align="center">
            Creditor Profile Edit
          </Typography>
          <Divider sx={{ marginBottom: 2 }} />
        </Box>
        <form>
          <Grid container spacing={2} sx={{ padding: 0.5 }}>
            {[
              { label: "Name", field: "name" },
              { label: "Address", field: "address" },
              { label: "Phone Number", field: "phoneNumber" },
              { label: "Email", field: "email" },
              { label: "Bank Account Number", field: "accountNumber" },
            ].map(({ label, field }) => (
              <Grid item xs={12} key={field}>
                <TextField
                  fullWidth
                  label={label}
                  value={(editedData as any)?.[field] || ""}
                  onChange={(e) =>
                    handleInputChange(e, field as keyof ICreditor)
                  }
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

export default CreditorFormPage;
