import React, { FC, ReactElement, useEffect, useState } from 'react';
import { useEdit, useGet } from '../../services/api-service';
import { IDebtor } from '../../shared/models/Debtor';
import useStyles from '../../Components/Styles/global-styles';
import { useNavigate } from 'react-router-dom';
import { ICreditor } from '../../shared/models/Creditor';
import { IAdmin } from '../../shared/models/Admin';
import {
    Paper,
    Typography,
    Box,
    IconButton,
    Divider,
    Button,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import BusinessIcon from '@mui/icons-material/Business';
import PersonIcon from '@mui/icons-material/Person';
import Navbar from '../../Components/Navbar/navbar';
import Footer from '../../Components/Footer/footer';
import EditIcon from '@mui/icons-material/Edit';
import { IPage } from '../../shared/models/Page';
import { IDebtorDTO } from '../../shared/dtos/DebtorDTO';
import { ICreditorDTO } from '../../shared/dtos/CreditorDTO';
import useErrorHandling from '../../services/handle-responses';

const UserProfilePage: FC<IPage> = (props): ReactElement => {
    const username = localStorage.getItem('username');
    const classes = useStyles('light');
    const navigate = useNavigate();
    const { openSnackbar } = props;
    const [shouldRefetch, setShouldRefetch] = useState(false);
    const [fieldErrors, setFieldErrors] = useState<{ [key: string]: string }>({});
    const [editMode, setEditMode] = useState(false);
    const [editedData, setEditedData] = useState<ICreditor | IDebtor | null>(null);
    const [editCompleted, setEditCompleted] = useState(false);
    const { handleErrorResponse } = useErrorHandling();

    const { data, loading, error } = useGet<ICreditor | IDebtor | IAdmin>(
        'users/{username}',
        username ? { username: username } : {},
        shouldRefetch
    );

    useEffect(() => {
        if (error && (error.statusCode === 404 || error?.statusCode === 403)) {
            handleErrorResponse(error.statusCode);
            openSnackbar(error.message, 'error');
        }
    }, [error, openSnackbar]);

    const canEditProfile = data?.user?.role.name !== 'ADMIN';

    const roleSpecificEndpoint =
        data?.user?.role.name === 'CREDITOR'
            ? 'creditors/{id}'
            : data?.user?.role.name === 'DEBTOR'
                ? 'debtors/{id}'
                : '';

    const {
        editData: editDataRequest,
        loading: editLoading,
        error: editError,
    } = useEdit<ICreditorDTO | IDebtorDTO>(roleSpecificEndpoint, { id: data?.id });


    const handleEditModeToggle = () => {
        setEditMode((prevEditMode) => {
            if (prevEditMode) {
                setEditedData(data ? { ...data } : null);
            }
            return !prevEditMode;
        });
    };

    const handleInputChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
        field: string
    ) => {
        setFieldErrors((prevErrors) => ({ ...prevErrors, [field]: '' }));

        setEditedData((prevData) => {
            if (prevData) {
                return { ...prevData, [field]: e.target.value };
            }
            return prevData;
        });
    };

    useEffect(() => {
        setEditedData(data ? { ...data } : null);
    }, [data]);

    useEffect(() => {
        if (editError !== null && editError.statusCode === 422) {
            const fieldErrors = JSON.parse(editError.description);
            setFieldErrors(fieldErrors);
            setEditedData((prevData) => {
                if (prevData && typeof prevData === 'object') {
                    let updatedData = { ...prevData };

                    Object.keys(fieldErrors).forEach((field) => {
                        const key = field as keyof typeof updatedData;

                        if (updatedData[key]) {
                            updatedData = {
                                ...updatedData,
                                [key]: {
                                    ...(updatedData[key] as Object),
                                    errorMessage: fieldErrors[field],
                                },
                            };
                        }
                    });

                    return updatedData;
                }

                return prevData;
            });
        } else if (editError && (editError.statusCode === 404 || editError?.statusCode === 403)) {
            handleErrorResponse(editError.statusCode);
            openSnackbar(editError.message, 'error');
        }
        else if (editCompleted) {
            setFieldErrors({});
            setEditMode(false);
            setShouldRefetch((prev) => !prev);
            openSnackbar('Profile edited successfully', 'success');
        }
    }, [editError, editCompleted, openSnackbar]);

    const handleSaveChanges = async () => {
        if (!editedData) {
            return;
        }

        let editData: ICreditorDTO | IDebtorDTO;

        if ('accountNumber' in editedData) {
            const data = editedData as ICreditor;
            editData = {
                name: data.name,
                address: data.address,
                phoneNumber: data.phoneNumber,
                email: data.email,
                accountNumber: data.accountNumber,
            };
        } else {
            const data = editedData as IDebtor;
            editData = {
                name: data.name,
                surname: data.surname,
                email: data.email,
                phoneNumber: data.phoneNumber,
            };
        }

        await editDataRequest(editData);

        setEditCompleted(true);

        setTimeout(() => setEditCompleted(false), 1000);
    };

    const renderFields = () => (editedData ? <>{renderAdditionalFields()}</> : null);

    const renderAdditionalFields = () => {
        if (data && editedData) {
            let additionalFields;

            if (data.user?.username === username) {
                if (data.user?.role.name === 'CREDITOR') {
                    const editedData = data as ICreditor;
                    additionalFields = (
                        <>
                            <Typography>
                                Address:{' '}
                                {renderField('address', 'address', editedData?.address)}
                            </Typography>
                            <Typography>
                                Phone:{' '}
                                {renderField('phoneNumber', 'phoneNumber', editedData?.phoneNumber)}
                            </Typography>
                            <Typography>
                                Email:{' '}
                                {renderField('email', 'email', editedData?.email)}
                            </Typography>
                            <Typography>
                                Account Number:{' '}
                                {renderField('acountNumber', 'accountNumber', editedData?.accountNumber)}
                            </Typography>
                        </>
                    );
                } else if (data.user.role.name === 'DEBTOR') {
                    const editedData = data as IDebtor;
                    additionalFields = (
                        <>
                            <Typography>
                                Surname:{' '}
                                {renderField('surname', 'surname', editedData?.surname)}
                            </Typography>
                            <Typography>
                                Email:{' '}
                                {renderField('email', 'email', editedData?.email)}
                            </Typography>
                            <Typography>
                                Phone:{' '}
                                {renderField('phoneNumber', 'phoneNumber', editedData?.phoneNumber)}
                            </Typography>
                        </>
                    );
                }
            }

            return additionalFields;
        }

        return null;
    };

    const renderField = (label: string, field: string, value: string | undefined) => (
        <>
            {editMode ? (
                <>
                    <input defaultValue={value} onChange={(e) => handleInputChange(e, field)} />
                    <span style={{ color: 'red' }}>{fieldErrors[field]}</span>
                </>
            ) : (
                value
            )}
        </>
    );

    const renderUserProfile = () => {
        if (data) {
            let icon;
            let title;
            let additionalContent;

            if (data.user?.username === username) {
                if (data.user?.role.name === 'CREDITOR') {
                    const creditorData = data as ICreditor;
                    icon = <BusinessIcon fontSize="large" />;
                    title = 'Creditor';
                    additionalContent = (
                        <>
                            <Typography>Address: {creditorData.address}</Typography>
                            <Typography>Phone: {creditorData.phoneNumber}</Typography>
                            <Typography>Email: {creditorData.email}</Typography>
                            <Typography>Account Number: {creditorData.accountNumber}</Typography>
                        </>
                    );
                } else if (data.user?.role.name === 'DEBTOR') {
                    const debtorData = data as IDebtor;
                    icon = <PersonIcon fontSize="large" />;
                    title = 'Debtor';
                    additionalContent = (
                        <>
                            <Typography>Surname: {debtorData.surname}</Typography>
                            <Typography>Email: {debtorData.email}</Typography>
                            <Typography>Phone Number: {debtorData.phoneNumber}</Typography>
                        </>
                    );
                } else if (data.user?.role.name === 'ADMIN') {
                    const adminData = data as IAdmin;
                    icon = <AccountCircleIcon fontSize="large" />;
                    title = 'Admin';
                    additionalContent = (
                        <>
                            <Typography>Surname: {adminData.surname}</Typography>
                        </>
                    );
                }
            }

            return (
                <Box>
                    <Typography variant="h4">
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                            <IconButton onClick={() => navigate(-1)} aria-label="Back">
                                <ArrowBackIcon />
                            </IconButton>
                            {icon} {title}
                            {canEditProfile && !editMode && (
                                <IconButton onClick={handleEditModeToggle} aria-label="Edit">
                                    <EditIcon sx={{ color: '#8FBC8F' }} />
                                </IconButton>
                            )}
                        </Box>
                    </Typography>
                    <Divider sx={{ marginBottom: 2 }} />
                    <Typography>
                        Name: {renderField('name', 'name', editedData?.name)}
                    </Typography>
                    {editMode && renderFields()}
                    {!editMode && additionalContent}
                    {editMode && (
                        <Box sx={{ marginTop: 1 }}>
                            <Button
                                sx={{
                                    color: 'black',
                                    backgroundColor: 'white',
                                    border: '3px solid #8FBC8F',
                                    marginRight: 2,
                                }}
                                onClick={handleSaveChanges}
                            >
                                Save
                            </Button>
                            <Button
                                sx={{
                                    color: 'black',
                                    backgroundColor: 'white',
                                    border: '3px solid #8FBC8F',
                                }}
                                onClick={handleEditModeToggle}
                            >
                                Cancel
                            </Button>
                        </Box>
                    )}
                </Box>
            );
        }

        return null;
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
            <Paper className={classes.paper} elevation={16} square={true}>
                {loading && (
                    <Box
                        sx={{
                            flexGrow: 1,
                            display: 'flex',
                            width: '100%',
                            justifyContent: 'center',
                            height: '200px',
                            marginTop: 3,
                        }}
                    >
                        Loading...
                    </Box>
                )}
                {error !== null && error.statusCode !== 422 && (
                    <Box
                        sx={{
                            flexGrow: 1,
                            display: 'flex',
                            width: '100%',
                            justifyContent: 'center',
                            height: '200px',
                            marginTop: 3,
                        }}
                    >
                        <Typography variant="body1" color="red">
                            {error.description}
                        </Typography>
                    </Box>
                )}
                {error && (error.statusCode === 404 || error?.statusCode === 403) && (
                    <>
                        {handleErrorResponse(error.statusCode)}
                        {openSnackbar(error.message, 'error')}
                    </>
                )}
                <Box sx={{ padding: 2 }}>
                    {renderUserProfile()}
                </Box>
            </Paper>
            <Footer />
        </Box>
    );
};

export default UserProfilePage;