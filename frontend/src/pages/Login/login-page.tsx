import { FC, ReactElement, useEffect, useState } from "react";
import { IPage } from "../../shared/models/Page";
import useStyles from "../../Components/Styles/global-styles";
import { useNavigate } from "react-router-dom";
import { usePost } from "../../services/api-service";
import { Box, Paper, Typography, TextField, Button } from "@mui/material";
import { LoadingButton } from "@mui/lab";
import { IUserDTO } from "../../shared/dtos/UserDTO";
import { IAccessMap } from "../../shared/models/AccessMap";
import Footer from "../../Components/Footer/footer";
import Navbar from "../../Components/Navbar/navbar";
import AuthService from "../../services/jwt-service";

const LoginPage: FC<IPage> = (props): ReactElement => {
    const { openSnackbar } = props;
    const { postData, data, loading, error } = usePost<IAccessMap>("login");

    const classes = useStyles("light");
    const navigate = useNavigate();

    const [form, setForm] = useState<Record<string, any>>({
        username: {
            value: "",
            errorMessage: "",
        },
        password: {
            value: "",
            errorMessage: "",
        },
    });

    useEffect(() => {
        if (data !== null) {
            localStorage.setItem("token", data.accessToken);
            localStorage.setItem("refreshToken", data.refreshToken);
            const authService = AuthService.getInstance();
            openSnackbar('Welcome!', 'success');
            const decodedToken = authService.decodeToken();

            if (decodedToken) {
                const expirationString = decodedToken.exp.toString();
                localStorage.setItem('username', decodedToken.sub);
                localStorage.setItem('role', decodedToken.role);
                localStorage.setItem('exp', expirationString);
            }

            navigate('/');
        } else if (error !== null) {
            if (error.statusCode === 422) {
                const updatedForm = { ...form };
                const fieldErrors = JSON.parse(error.description);
                Object.keys(fieldErrors).forEach((field) => {
                    if (updatedForm[field]) {
                        updatedForm[field].errorMessage = fieldErrors[field];
                    }
                });
                setForm(updatedForm);
            }
        }
    }, [data, error, openSnackbar, navigate]);

    const handleLogin = async (
        e: React.MouseEvent<HTMLElement>
    ): Promise<void> => {
        e.preventDefault();

        const request: IUserDTO = {
            username: form.username.value,
            password: form.password.value,
        };

        postData(request);
    };

    const handleChange = (e: React.ChangeEvent<HTMLElement>): void => {
        const event = e.target as any;
        setForm({
            ...form,
            [event.name]: {
                value: event.value,
                errorMessage: "",
            },
        });
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
            <Paper elevation={16} square={true} className={classes.paper}>
                <Box className={classes.form}>
                    <Typography variant="h4">Login</Typography>
                    {error !== null && error.statusCode !== 422 && (
                        <Box
                            sx={{
                                flexGrow: 1,
                                display: "flex",
                                width: "100%",
                                justifyContent: 'center',
                            }}
                        >
                            <Typography variant="body1" color="red">
                                {error.statusCode === 401 ? "Bad Credentials" : error.description}
                            </Typography>
                        </Box>
                    )}
                    <TextField
                        id="username"
                        label="Username"
                        name="username"
                        type="text"
                        value={form.username.value}
                        onChange={handleChange}
                        error={form.username.errorMessage !== ""}
                        helperText={form.username.errorMessage}
                        size="small"
                        margin="normal"
                        required
                    />
                    <TextField
                        id="password"
                        label="Password"
                        name="password"
                        type="password"
                        value={form.password.value}
                        onChange={handleChange}
                        error={form.password.errorMessage !== ""}
                        helperText={form.password.errorMessage}
                        size="small"
                        margin="normal"
                        required
                    />
                    <Box
                        sx={{
                            flexGrow: 1,
                            display: "flex",
                            width: "100%",
                            paddingTop: "16px",
                            justifyContent: 'center',
                        }}
                    >
                        <LoadingButton
                            size="medium"
                            color="inherit"
                            variant="outlined"
                            onClick={handleLogin}
                            sx={{
                                marginRight: "8px",
                            }}
                        >
                            Login
                        </LoadingButton>
                        <Button
                            size="medium"
                            color="inherit"
                            variant="outlined"
                            onClick={(): void => {
                                navigate("/");
                            }}
                        >
                            Cancel
                        </Button>
                    </Box>
                </Box>
            </Paper>
            <Footer />
        </Box>
    );
};

export default LoginPage;