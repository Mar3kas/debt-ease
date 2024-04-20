import React, { FC, ReactElement, useEffect, useState } from "react";
import {
  Paper,
  Typography,
  Box,
  Button,
  TextField,
  Divider,
  Grid,
  InputAdornment,
} from "@mui/material";
import Navbar from "../../components/page-navbar/navbar";
import Footer from "../../components/page-footer/footer";
import { IPage } from "../../shared/models/page";
import useStyles from "../../components/page-styles/global-styles";
import { usePost } from "../../services/api-service";
import { Link, useNavigate } from "react-router-dom";
import useErrorHandling from "../../services/handle-responses";
import { IDebtPaymentStrategyDTO } from "../../shared/dtos/debt-payment-strategy-dto";
import AuthService from "../../services/jwt-service";
import { Chart, registerables } from "chart.js";
import { IDebtPaymentStrategy } from "../../shared/models/debt-payment-strategy";
import { Line } from "react-chartjs-2";

const DebtFreePage: FC<IPage> = ({ openSnackbar }): ReactElement => {
  const authService = AuthService.getInstance();
  const username = authService.getUsername();
  const classes = useStyles("light");
  const navigate = useNavigate();
  const { handleErrorResponse } = useErrorHandling();
  Chart.register(...registerables);

  const { data, error, postData } = usePost<IDebtPaymentStrategy>(
    "debt/cases/debtor/{username}/payment/strategy",
    username ? { username: username } : {}
  );

  const [form, setForm] = useState<Record<string, any>>({
    minimalMonthlyPaymentForEachDebt: { value: "", errorMessage: "" },
    extraMonthlyPaymentForHighestDebt: { value: "", errorMessage: "" },
  });

  useEffect(() => {
    handleAPIError(error, openSnackbar);
  }, [error, openSnackbar]);

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

  const handleAnalyzeChanges = async (
    e: React.MouseEvent<HTMLElement>
  ): Promise<void> => {
    e.preventDefault();

    const request: IDebtPaymentStrategyDTO = Object.keys(form).reduce(
      (acc, key) => ({ ...acc, [key]: form[key].value }),
      {}
    ) as IDebtPaymentStrategyDTO;

    await postData(request);
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
            Debt Payment Strategy
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
                type="text"
                value={value}
                onChange={handleChange}
                error={errorMessage !== ""}
                helperText={errorMessage}
                size="small"
                margin="normal"
                required
                fullWidth
                className={classes.textField}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">€</InputAdornment>
                  ),
                }}
              />
            ))}
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
                onClick={handleAnalyzeChanges}
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
                Analyze
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
      {data !== null &&
        data.avalancheBalanceEachMonth.length > 0 &&
        data.snowballBalanceEachMonth.length > 0 && (
          <Paper className={classes.graph} elevation={16} square>
            <Grid container spacing={2} justifyContent="center">
              <Grid item xs={12} md={6}>
                <Box mt={2}>
                  <Typography variant="h6" align="center" gutterBottom>
                    <Link
                      to="https://www.forbes.com/advisor/debt-relief/debt-avalanche-method-how-it-works/"
                      style={{ textDecoration: "underline", color: "inherit" }}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      Avalanche Debt Payment Strategy
                    </Link>
                  </Typography>
                  <Line
                    data={{
                      labels: data.avalancheBalanceEachMonth.map(
                        (_, index) => index + 1
                      ),
                      datasets: [
                        {
                          label: "Total Debt",
                          data: data.avalancheBalanceEachMonth,
                          borderColor: "#2E8B57",
                          fill: false,
                        },
                      ],
                    }}
                    options={{
                      scales: {
                        y: {
                          beginAtZero: true,
                          ticks: {
                            color: "black",
                          },
                          grid: {
                            color: "black",
                          },
                          title: {
                            display: true,
                            text: "Total Debt",
                            color: "black ",
                          },
                        },
                        x: {
                          title: {
                            display: true,
                            text: "Month Count",
                            color: "black",
                          },
                          ticks: {
                            color: "black",
                          },
                          grid: {
                            color: "black",
                          },
                        },
                      },
                      plugins: {
                        legend: {
                          display: false,
                        },
                      },
                    }}
                  />
                </Box>
              </Grid>
              <Grid item xs={12} md={6}>
                <Box mt={2}>
                  <Typography variant="h6" align="center" gutterBottom>
                    <Link
                      to="https://www.forbes.com/advisor/debt-relief/debt-snowball-method-how-it-works/"
                      style={{ textDecoration: "underline", color: "inherit" }}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      Snowball Debt Payment Strategy
                    </Link>
                  </Typography>
                  <Line
                    data={{
                      labels: data.snowballBalanceEachMonth.map(
                        (_, index) => index + 1
                      ),
                      datasets: [
                        {
                          label: "Total Debt",
                          data: data.snowballBalanceEachMonth,
                          borderColor: "#2E8B57",
                          fill: false,
                        },
                      ],
                    }}
                    options={{
                      scales: {
                        y: {
                          beginAtZero: true,
                          ticks: {
                            color: "black",
                          },
                          grid: {
                            color: "black",
                          },
                          title: {
                            display: true,
                            text: "Total Debt",
                            color: "black ",
                          },
                        },
                        x: {
                          title: {
                            display: true,
                            text: "Month Count",
                            color: "black",
                          },
                          ticks: {
                            color: "black",
                          },
                          grid: {
                            color: "black",
                          },
                        },
                      },
                      plugins: {
                        legend: {
                          display: false,
                        },
                      },
                    }}
                  />
                </Box>
              </Grid>
            </Grid>
          </Paper>
        )}
      <Footer />
    </Box>
  );
};

export default DebtFreePage;
