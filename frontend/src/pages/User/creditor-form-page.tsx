import React, { FC, ReactElement } from 'react';
import {
    Paper,
    Typography,
    Box,
} from '@mui/material';
import Navbar from '../../Components/Navbar/navbar';
import Footer from '../../Components/Footer/footer';
import { IPage } from '../../shared/models/Page';
import useStyles from '../../Components/Styles/global-styles';
import { useEdit } from '../../services/api-service';
import { ICreditorDTO } from '../../shared/dtos/CreditorDTO';

const CreditorFormPage: FC<IPage> = (props): ReactElement => {
    const classes = useStyles('light');
    const { openSnackbar } = props;

    const { data, loading, error, } = useEdit<ICreditorDTO>("creditors/{id}", {id: 2});

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
                        {openSnackbar(error.message, 'error')}
                    </>
                )}
            </Paper>
            <Footer />
        </Box>
    );
};

export default CreditorFormPage;