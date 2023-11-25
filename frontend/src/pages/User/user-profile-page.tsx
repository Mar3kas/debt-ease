import React, { FC, ReactElement, useState } from 'react';
import { useGet } from '../../services/api-service';
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

const UserProfilePage: FC = (): ReactElement => {
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    const { data, loading, error } = useGet<ICreditor | IDebtor | IAdmin>(
        'users/{username}',
        username ? { username: username } : {}
    );

    const classes = useStyles('light');
    const navigate = useNavigate();

    const [editMode, setEditMode] = useState(false);
    const [editedData, setEditedData] = useState<ICreditor | IDebtor | IAdmin | null>(data);

    const handleEditModeToggle = () => {
        setEditMode((prevEditMode) => !prevEditMode);
    };

    const handleInputChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
        field: keyof (ICreditor | IDebtor | IAdmin)
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

      const handleSaveChanges = () => {
        console.log('Save changes:', editedData);
        setEditMode(false);
    };

    const renderFields = () => {
        return (
            <>
                <Typography>
                    Name: {editMode ? <input value={editedData?.name} onChange={(e) => handleInputChange(e, 'name')} /> : editedData?.name}
                </Typography>
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
                            <Typography>Address: {editedData?.address}</Typography>
                            <Typography>Phone: {editedData?.phoneNumber}</Typography>
                            <Typography>Email: {editedData?.email}</Typography>
                            <Typography>Account Number: {editedData?.accountNumber}</Typography>
                        </>
                    );
                } else if (data.user.role.name === 'DEBTOR') {
                    const editedData = data as IDebtor;
                    additionalFields = (
                        <>
                            <Typography>Surname: {editedData?.surname}</Typography>
                            <Typography>Email: {editedData?.email}</Typography>
                            <Typography>Phone Number: {editedData?.phoneNumber}</Typography>
                        </>
                    );
                } else if (data.user.role.name === 'ADMIN') {
                    const editedData = data as IAdmin;
                    additionalFields = (
                        <>
                            <Typography>Surname: {editedData?.surname}</Typography>
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
                    title = 'Creditor Profile';
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
                    title = 'Debtor Profile';
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
                    title = 'Admin Profile';
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
                            {/* Edit button */}
                            <IconButton onClick={handleEditModeToggle}>
                                {editMode ? <SaveIcon /> : <EditIcon />}
                            </IconButton>
                        </Box>
                    </Typography>
                    <Divider sx={{ marginBottom: 2 }} />
                    <Typography>Name: {data.name}</Typography>
                    {additionalContent}
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