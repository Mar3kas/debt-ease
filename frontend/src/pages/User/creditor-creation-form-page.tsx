import React, { FC, ReactElement, useEffect, useState } from "react";
import {
  Paper,
  Typography,
  Box,
  Button,
  TextField,
  Divider,
  Grid,
} from "@mui/material";
import Navbar from "../../components/page-navbar/navbar";
import Footer from "../../components/page-footer/footer";
import { IPage } from "../../shared/models/page";
import useStyles from "../../components/page-styles/global-styles";
import { usePost } from "../../services/api-service";
import { useNavigate } from "react-router-dom";
import useErrorHandling from "../../services/handle-responses";
import { ICreditorDTO } from "../../shared/dtos/creditor-dto";
import { ICreditor } from "../../shared/models/creditor";

const CreditorCreationFormPage: FC<IPage> = ({
  openSnackbar,
}): ReactElement => {
  const classes = useStyles("light");
  const navigate = useNavigate();
  const { handleErrorResponse } = useErrorHandling();
  const [creationCompleted, setCreationCompleted] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const { data, error, postData } = usePost<ICreditor>("creditors", {});

  const [form, setForm] = useState<Record<string, any>>({
    name: { value: "", errorMessage: "" },
    address: { value: "", errorMessage: "" },
    phoneNumber: { value: "", errorMessage: "" },
    email: { value: "", errorMessage: "" },
    accountNumber: { value: "", errorMessage: "" },
    username: { value: "", errorMessage: "" },
    password: { value: "", errorMessage: "" },
  });

  useEffect(() => {
    handleAPIError(error, openSnackbar);
  }, [error, openSnackbar]);

  useEffect(() => {
    if (data !== null && creationCompleted) {
      openSnackbar("Creditor created successfully", "success");
      navigate(-1);
    }
  }, [data, creationCompleted, openSnackbar, navigate]);

  const handleAPIError = (error: any, snackbar: any) => {
    if (error && [401, 403].includes(error.statusCode)) {
      handleErrorResponse(error.statusCode);
      snackbar(error.message, "error");
    } else if (error?.description.includes("Refresh Token")) {
      navigate("/login");
      openSnackbar("You need to login again", "warning");
    } else if (error && error.statusCode === 422) {
      const updatedForm = { ...form };
      const fieldErrors = JSON.parse(error.description);
      Object.keys(fieldErrors).forEach((field) => {
        if (updatedForm[field]) {
          updatedForm[field].errorMessage = fieldErrors[field];
        }
      });
      setForm(updatedForm);
    } else if (error) {
      snackbar(error.message, "error");
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
    const { name, value } = e.target;
    setForm({
      ...form,
      [name]: {
        value,
        errorMessage: "",
      },
    });
  };

  const handleSaveChanges = async (
    e: React.MouseEvent<HTMLElement>
  ): Promise<void> => {
    e.preventDefault();

    const request: ICreditorDTO = Object.keys(form).reduce(
      (acc, key) => ({ ...acc, [key]: form[key].value }),
      {} as ICreditorDTO
    );

    await postData(request);

    setCreationCompleted(true);
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
      <Paper className={classes.paper} elevation={16} square>
        <Box>
          <Typography variant="h5" color="black" align="center" marginTop={2}>
            Creditor Profile Creation
          </Typography>
          <Divider />
        </Box>
        <Box className={classes.form}>
          {error !== null && error.statusCode !== 422 && (
            <Box
              sx={{
                flexGrow: 1,
                display: "flex",
                width: "100%",
                justifyContent: "center",
              }}
            >
              <Typography variant="body1" color="red">
                {error.statusCode === 401
                  ? "Bad Credentials"
                  : error.description}
              </Typography>
            </Box>
          )}
          <Grid container spacing={1} justifyContent="flex-start">
            {Object.entries(form).map(([key, { value, errorMessage }]) => (
              <TextField
                key={key}
                id={key}
                label={key
                  .split(/(?=[A-Z])/)
                  .map(
                    (word) =>
                      word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
                  )
                  .join(" ")}
                name={key}
                type={key === "password" && !showPassword ? "password" : "text"}
                value={value}
                onChange={handleChange}
                error={errorMessage !== ""}
                helperText={errorMessage}
                size="small"
                margin="normal"
                required
                fullWidth
                className={classes.textField}
              />
            ))}
            <Button
              sx={{
                color: "black",
                backgroundColor: "white",
                border: "3px solid #8FBC8F",
                "&:hover": {
                  color: "black",
                  backgroundColor: "#F8DE7E",
                },
                padding: "5px 5px",
                fontSize: "10px",
              }}
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? "Hide Password" : "Show Password"}
            </Button>
          </Grid>
          <Grid container spacing={1} justifyContent="flex-end">
            <Box
              sx={{
                marginTop: 1,
                display: "flex",
                justifyContent: "flex-end",
                padding: 1,
              }}
            >
              <Button
                onClick={handleSaveChanges}
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
                onClick={(): void => {
                  navigate(-1);
                }}
              >
                Cancel
              </Button>
            </Box>
          </Grid>
        </Box>
      </Paper>
      <Footer />
    </Box>
  );
};

export default CreditorCreationFormPage;
