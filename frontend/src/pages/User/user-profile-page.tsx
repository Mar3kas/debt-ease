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
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import BusinessIcon from '@mui/icons-material/Business';
import PersonIcon from '@mui/icons-material/Person';
import Navbar from '../../Components/Navbar/navbar';
import Footer from '../../Components/Footer/footer';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import { IPage } from '../../shared/models/Page';
import { IDebtorDTO } from '../../shared/dtos/DebtorDTO';
import { ICreditorDTO } from '../../shared/dtos/CreditorDTO';

const UserProfilePage: FC<IPage> = (props): ReactElement => {
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');

    const { data, loading, error } = useGet<ICreditor | IDebtor | IAdmin>(
        'users/{username}',
        username ? { username: username } : {}
    );

    const roleSpecificEndpoint = data?.user.role.name === 'CREDITOR' ? 'creditors/{id}' :
        data?.user.role.name === 'DEBTOR' ? 'debtors/{id}' : '';

    const { editData: editDataRequest, loading: editLoading, error: editError } = useEdit<ICreditorDTO | IDebtorDTO>(
        roleSpecificEndpoint,
        { id: data?.id }
    );

    const classes = useStyles('light');
    const navigate = useNavigate();

    const [editMode, setEditMode] = useState(false);
    const [editedData, setEditedData] = useState<ICreditor | IDebtor | null>(null);

    const handleEditModeToggle = () => {
        if (editMode) {
            setEditedData(data ? { ...data } : null);
        }
        setEditMode((prevEditMode) => !prevEditMode);
    };

    const handleInputChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
        field: string
    ) => {
        setEditedData((prevData) => {
            if (prevData) {
                return {
                    ...prevData,
                    [field]: e.target.value,
                };
            }
            return prevData;
        });
    };

    useEffect(() => {
        setEditedData(data ? { ...data } : null);
    }, [data]);

    const handleSaveChanges = () => {
        if (!editedData) {
            return;
        }

        let editData: ICreditorDTO | IDebtorDTO;

        if ('accountNumber' in editedData) {
            let data = editedData as ICreditor;
            editData = {
                name: data.name,
                address: data.address,
                phoneNumber: data.phoneNumber,
                email: data.email,
                accountNumber: data.accountNumber,
            };
        } else {
            let data = editedData as IDebtor;
            editData = {
                name: data.name,
                surname: data.surname,
                email: data.email,
                phoneNumber: data.phoneNumber,
            };
        }

        editDataRequest(editData);

        window.location.reload();
    };

    const renderFields = () => {
        if (!editedData) {
            return null;
        }

        return (
            <>
                {renderAdditionalFields()}
            </>
        );
    };


    const renderAdditionalFields = () => {
        if (data && editedData) {
            let additionalFields;

            if (data.user.username === username) {
                if (data.user.role.name === 'CREDITOR') {
                    const editedData = data as ICreditor;
                    additionalFields = (
                        <>
                            <Typography>
                                Address:{' '}
                                {editMode ? (
                                    <input
                                        defaultValue={editedData?.address}
                                        onChange={(e) => handleInputChange(e, 'address')}
                                    />
                                ) : (
                                    editedData?.address
                                )}
                            </Typography>
                            <Typography>
                                Phone:{' '}
                                {editMode ? (
                                    <input
                                        defaultValue={editedData?.phoneNumber}
                                        onChange={(e) => handleInputChange(e, 'phoneNumber')}
                                    />
                                ) : (
                                    editedData?.phoneNumber
                                )}
                            </Typography>
                            <Typography>
                                Email:{' '}
                                {editMode ? (
                                    <input
                                        defaultValue={editedData?.email}
                                        onChange={(e) => handleInputChange(e, 'email')}
                                    />
                                ) : (
                                    editedData?.email
                                )}
                            </Typography>
                            <Typography>
                                Account Number:{' '}
                                {editMode ? (
                                    <input
                                        defaultValue={editedData?.accountNumber}
                                        onChange={(e) => handleInputChange(e, 'accountNumber')}
                                    />
                                ) : (
                                    editedData?.accountNumber
                                )}
                            </Typography>
                        </>
                    );
                } else if (data.user.role.name === 'DEBTOR') {
                    const editedData = data as IDebtor;
                    additionalFields = (
                        <>
                            <Typography>
                                Surname:{' '}
                                {editMode ? (
                                    <input
                                        defaultValue={editedData?.surname}
                                        onChange={(e) => handleInputChange(e, 'surname')}
                                    />
                                ) : (
                                    editedData?.surname
                                )}
                            </Typography>
                            <Typography>
                                Email:{' '}
                                {editMode ? (
                                    <input
                                        defaultValue={editedData?.email}
                                        onChange={(e) => handleInputChange(e, 'email')}
                                    />
                                ) : (
                                    editedData?.email
                                )}
                            </Typography>
                            <Typography>
                                Phone:{' '}
                                {editMode ? (
                                    <input
                                        defaultValue={editedData?.phoneNumber}
                                        onChange={(e) => handleInputChange(e, 'phoneNumber')}
                                    />
                                ) : (
                                    editedData?.phoneNumber
                                )}
                            </Typography>
                        </>
                    );
                }
            }

            return additionalFields;
        }

        return null;
    };


    const renderUserProfile = () => {
        if (data) {
            let icon;
            let title;
            let additionalContent;

            if (data.user.username === username) {
                if (data.user.role.name === 'CREDITOR') {
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
                } else if (data.user.role.name === 'DEBTOR') {
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
                } else if (data.user.role.name === 'ADMIN') {
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
                            <IconButton onClick={editMode ? handleSaveChanges : handleEditModeToggle}>
                                {editMode ? <SaveIcon /> : <EditIcon />}
                            </IconButton>
                        </Box>
                    </Typography>
                    <Divider sx={{ marginBottom: 2 }} />
                    <Typography>Name: {editMode ? <input value={editedData?.name} onChange={(e) => handleInputChange(e, 'name')} /> : editedData?.name}</Typography>
                    {editMode && renderFields()}
                    {!editMode && additionalContent}
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
                {loading &&
                    <Box
                        sx={{
                            flexGrow: 1,
                            display: 'flex',
                            width: '100%',
                            justifyContent: 'center',
                            height: '200px',
                            marginTop: 3,
                        }}
                    >Loading...</Box>}
                {error !== null && (
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
                <Box sx={{ padding: 2 }}>{renderUserProfile()}</Box>
            </Paper>
            <Footer />
        </Box>
    );
};

export default UserProfilePage;