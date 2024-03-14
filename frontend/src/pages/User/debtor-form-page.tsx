import React, { FC, ReactElement, useEffect, useState } from "react";
import { Paper, Typography, Box, Button, Grid, Divider } from "@mui/material";
import Navbar from "../../Components/Navbar/navbar";
import Footer from "../../Components/Footer/footer";
import { IPage } from "../../shared/models/Page";
import useStyles from "../../Components/Styles/global-styles";
import { useEdit, useGet } from "../../services/api-service";
import { IDebtorDTO } from "../../shared/dtos/DebtorDTO";
import { useNavigate, useParams } from "react-router-dom";
import useErrorHandling from "../../services/handle-responses";
import { IDebtor } from "../../shared/models/Debtor";

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
    { id: Number(id) }
  );

  useEffect(() => {
    handleAPIError(debtorError, openSnackbar, null);
  }, [debtorError, openSnackbar]);

  useEffect(() => {
    handleAPIError(editError, openSnackbar, null);
  }, [editError, openSnackbar]);

  useEffect(() => {
    handleAPIError(null, openSnackbar, editCompleted);
  }, [editCompleted, openSnackbar]);

  useEffect(() => {
    setEditedData(debtorData ? { ...debtorData } : null);
  }, [debtorData]);

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
    } else if (editCompleted) {
      setFieldErrors({});
      navigate(-1);
      openSnackbar("Profile edited successfully", "success");
    }
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
            {["name", "surname", "email", "phoneNumber"].map((field) => (
              <Grid item xs={12} key={field}>
                <div>
                  <label style={{ color: "black", marginRight: "5px" }}>
                    {field.charAt(0).toUpperCase() + field.slice(1)}
                  </label>
                  <input
                    type="text"
                    value={(editedData as any)?.[field] || ""}
                    onChange={(e) =>
                      handleInputChange(e, field as keyof IDebtor)
                    }
                    style={{ width: "30%" }}
                  />
                  <span style={{ color: "red" }}>{fieldErrors[field]}</span>
                </div>
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
