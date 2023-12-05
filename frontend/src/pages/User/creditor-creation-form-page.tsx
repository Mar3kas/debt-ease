import React, { FC, ReactElement, useEffect, useState } from 'react';
import {
    Paper,
    Typography,
    Box,
    Button,
    TextField,
    Divider,
} from '@mui/material';
import { LoadingButton } from '@mui/lab';
import Navbar from '../../Components/Navbar/navbar';
import Footer from '../../Components/Footer/footer';
import { IPage } from '../../shared/models/Page';
import useStyles from '../../Components/Styles/global-styles';
import { usePost } from '../../services/api-service';
import { useNavigate } from 'react-router-dom';
import useErrorHandling from '../../services/handle-responses';
import { ICreditorDTO } from '../../shared/dtos/CreditorDTO';

const CreditorCreationFormPage: FC<IPage> = ({ openSnackbar }): ReactElement => {
    const classes = useStyles('light');
    const navigate = useNavigate();
    const { handleErrorResponse } = useErrorHandling();
    const [creationCompleted, setCreationCompleted] = useState(false);

    const { data, error, postData } = usePost<ICreditorDTO>('creditors', {});

    const [form, setForm] = useState<Record<string, any>>({
        name: { value: '', errorMessage: '' },
        address: { value: '', errorMessage: '' },
        phoneNumber: { value: '', errorMessage: '' },
        email: { value: '', errorMessage: '' },
        accountNumber: { value: '', errorMessage: '' },
        username: { value: '', errorMessage: '' },
    });

    useEffect(() => {
        handleAPIError(error, openSnackbar);
    }, [error, openSnackbar]);

    useEffect(() => {
        if (data !== null && creationCompleted) {
            openSnackbar('Creditor created successfully', 'success');
            navigate(-1);
        }
    }, [data, creationCompleted, openSnackbar, navigate]);

    const handleAPIError = (error: any, snackbar: any) => {
        if (error && [401, 403].includes(error.statusCode)) {
            handleErrorResponse(error.statusCode);
            snackbar(error.message, 'error');
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
            snackbar(error.message, 'error');
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
        const { name, value } = e.target;
        setForm({
            ...form,
            [name]: {
                value,
                errorMessage: '',
            },
        });
    };

    const handleSaveChanges = async (
        e: React.MouseEvent<HTMLElement>
    ): Promise<void> => {
        e.preventDefault();

        const request: ICreditorDTO = Object.keys(form).reduce(
            (acc, key) => ({ ...acc, [key]: form[key].value }),
            {}
        );

        await postData(request);

        setCreationCompleted(true);
    };

    return (
        <Box
            className={classes.body}
            sx={{
                flexGrow: 1,
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                minHeight: '100vh',
                height: '100%',
                overflow: 'hidden',
                backgroundColor: '#8FBC8F',
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
                                display: 'flex',
                                width: '100%',
                                justifyContent: 'center',
                            }}
                        >
                            <Typography variant="body1" color="red">
                                {error.statusCode === 401 ? 'Bad Credentials' : error.description}
                            </Typography>
                        </Box>
                    )}
                    {Object.entries(form).map(([key, { value, errorMessage }]) => (
                        <TextField
                            key={key}
                            id={key}
                            label={key}
                            name={key}
                            type={key}
                            value={value}
                            onChange={handleChange}
                            error={errorMessage !== ''}
                            helperText={errorMessage}
                            size="small"
                            margin="normal"
                            required
                        />
                    ))}
                    <Box
                        sx={{
                            flexGrow: 1,
                            display: 'flex',
                            width: '100%',
                            paddingTop: '16px',
                            justifyContent: 'center',
                        }}
                    >
                        <LoadingButton
                            size="medium"
                            color="inherit"
                            variant="outlined"
                            onClick={handleSaveChanges}
                            sx={{
                                marginRight: '8px',
                            }}
                        >
                            Save
                        </LoadingButton>
                        <Button
                            size="medium"
                            color="inherit"
                            variant="outlined"
                            onClick={(): void => {
                                navigate('/');
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

export default CreditorCreationFormPage;