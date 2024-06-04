import React, { FC, ReactElement, useEffect, useState } from "react";
import {
  Box,
  Button,
  Checkbox,
  Divider,
  FormControlLabel,
  Grid,
  InputAdornment,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import { IPage } from "../../shared/models/page";
import useStyles from "../../components/page-styles/global-styles";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../../components/page-navbar/navbar";
import Footer from "../../components/page-footer/footer";
import { loadStripe } from "@stripe/stripe-js";
import {
  CardElement,
  Elements,
  useElements,
  useStripe,
} from "@stripe/react-stripe-js";
import { IPaymentRequestDTO } from "../../shared/dtos/payment-request-dto";
import { usePost } from "../../services/api-service";
import useErrorHandling from "../../services/handle-responses";

const DebtCasePayPage: FC<IPage> = (props): ReactElement => {
  const { handleErrorResponse } = useErrorHandling();

  const stripePromise = loadStripe(
    "pk_test_51P1sUuRw8eJ4L4u1hINLyPF2kvS3myUpLrimJn2NUiVbClJgZsHdrBeQa9GtnbEhQet9ko9URVEKK8Ei4fgoDfWl00ewolJr2Q"
  );

  return (
    <Elements stripe={stripePromise}>
      <DebtCasePaymentForm {...props} {...handleErrorResponse} />
    </Elements>
  );
};

const DebtCasePaymentForm: FC<IPage> = (props): ReactElement => {
  const stripe = useStripe();
  const elements = useElements();
  const { openSnackbar } = props;
  const { handleErrorResponse } = useErrorHandling();
  const [isFullPayment, setIsFullPayment] = useState(true);
  const [paymentAmount, setPaymentAmount] = useState("");
  const [paymentCompleted, setPaymentCompleted] = useState(false);
  const classes = useStyles("light");
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const { amount } = useParams<{ amount?: string }>();
  const displayedAmount = amount ? parseFloat(amount) : 0;
  const { data, error, postData } = usePost<String>("payments/{id}/pay", {
    id: Number(id),
  });

  useEffect(() => {
    if (data !== null && paymentCompleted) {
      openSnackbar("Payment was successfull", "success");
      navigate(-1);
    }
  }, [data, paymentCompleted, openSnackbar, navigate]);

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
    } else if (error) {
      snackbar(error.message, "error");
    }
  };

  const handleCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsFullPayment(event.target.checked);
  };

  const handlePaymentAmountChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setPaymentAmount(event.target.value);
  };

  const handleSubmit = async (event: any) => {
    event.preventDefault();

    if (!stripe || !elements) {
      return;
    }

    const cardElement = elements.getElement(CardElement);
    if (!cardElement) {
      openSnackbar("Card element not found. Please try again", "error");
      return;
    }

    const { error, paymentMethod } = await stripe.createPaymentMethod({
      type: "card",
      card: cardElement,
    });

    if (error) {
      openSnackbar("Error creating payment method. Please try again", "error");
      return;
    }

    const request: IPaymentRequestDTO = {
      sourceId: paymentMethod.id,
      paymentAmount: parseFloat(paymentAmount),
      isPaymentInFull: isFullPayment,
    };
    await postData(request);
    setPaymentCompleted(true);
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
        </Grid>

        <Paper className={classes.paper} elevation={16} square>
          <Box p={2}>
            <Typography variant="h5" color="black" align="center" gutterBottom>
              Debt Case Payment
            </Typography>
            <Divider />
          </Box>
          <Box p={2}>
            <CardElement />
          </Box>
          <Box p={2}>
            <Typography
              variant="body1"
              color="textSecondary"
              align="center"
              gutterBottom
            >
              Total Amount Due: €{displayedAmount}
            </Typography>
          </Box>
          {!isFullPayment && (
            <Box p={2}>
              <TextField
                label="Payment Amount"
                variant="outlined"
                value={paymentAmount}
                className={classes.textField}
                onChange={handlePaymentAmountChange}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">€</InputAdornment>
                  ),
                }}
              />
            </Box>
          )}
          <Grid
            container
            justifyContent="center"
            alignItems="center"
            spacing={2}
          >
            <Grid item xs={9}>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={isFullPayment}
                    onChange={handleCheckboxChange}
                    sx={{
                      "&.Mui-checked": {
                        color: "#8FBC8F",
                      },
                    }}
                  />
                }
                label="Paying in full"
              />
            </Grid>
            <Grid item xs={2}>
              <Box p={2} textAlign="center">
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
                  onClick={handleSubmit}
                >
                  Pay
                </Button>
              </Box>
            </Grid>
          </Grid>
        </Paper>
      </Box>
      <Footer />
    </Box>
  );
};

export default DebtCasePayPage;
