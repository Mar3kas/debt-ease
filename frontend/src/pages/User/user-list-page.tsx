import React, { FC, ReactElement, useEffect, useState } from "react";
import {
    Box,
    Button,
    Typography,
    Grid,
    Accordion,
    AccordionSummary,
    AccordionDetails,
    TextField,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import useStyles from "../../Components/Styles/global-styles";
import { useNavigate } from "react-router-dom";
import Navbar from "../../Components/Navbar/navbar";
import Footer from "../../Components/Footer/footer";
import { IPage } from "../../shared/models/Page";
import { useGet } from "../../services/api-service";
import useErrorHandling from "../../services/handle-responses";
import { ICreditor } from "../../shared/models/Creditor";
import { IDebtor } from "../../shared/models/Debtor";

const UserListPage: FC<IPage> = (props): ReactElement => {
    const navigate = useNavigate();
    const classes = useStyles('light');
    const [shouldRefetch, setShouldRefetch] = useState(false);
    const [confirmationDialogOpen, setConfirmationDialogOpen] = useState(false);
    const [userToDelete, setuserToDelete] = useState<{ id: number } | null>(null);
    const [showMoreCreditor, setShowMoreCreditor] = useState(3);
    const [showMoreDebtor, setShowMoreDebtor] = useState(3);
    const [creditorSearchQuery, setCreditorSearchQuery] = useState("");
    const [debtorSearchQuery, setDebtorSearchQuery] = useState("");
    const { handleErrorResponse } = useErrorHandling();
    const { openSnackbar } = props;

    const {
        data: creditorData,
        loading: creditorLoading,
        error: creditorError,
    } = useGet<ICreditor[]>("creditors", {}, shouldRefetch);
    
    const {
        data: debtorData,
        loading: debtorLoading,
        error: debtorError,
    } = useGet<IDebtor[]>("debtors", {}, shouldRefetch);

    const filteredCreditors = creditorData
        ? creditorData.filter(
            (creditor: ICreditor) =>
                creditor.user?.username.includes(creditorSearchQuery) ||
                creditor.name.includes(creditorSearchQuery) ||
                creditor.email.includes(creditorSearchQuery) ||
                creditor.phoneNumber.includes(creditorSearchQuery)
        )
        : [];

    const filteredDebtors = debtorData
        ? debtorData.filter(
            (debtor: IDebtor) =>
                debtor.user?.username.includes(debtorSearchQuery) ||
                debtor.name.includes(debtorSearchQuery) ||
                debtor.surname.includes(debtorSearchQuery) ||
                debtor.email?.includes(debtorSearchQuery) ||
                debtor.phoneNumber?.includes(debtorSearchQuery)
        )
        : [];

    useEffect(() => {
        handleAPIError(creditorError, openSnackbar);
    }, [creditorError, openSnackbar]);

    useEffect(() => {
        handleAPIError(debtorError, openSnackbar);
    }, [debtorError, openSnackbar]);

    const handleAPIError = (error: any, snackbar: any) => {
        if (error && (error.statusCode === 401 || error.statusCode === 403)) {
            handleErrorResponse(error.statusCode);
            snackbar(error.message, "error");
        }
    };

    const handleEdit = (id: number) => {
        // Implement edit functionality
    };

    const handleDelete = (id: number) => {
        // Implement delete functionality
    };

    const renderActionButtons = (id: number) => (
        <Grid container spacing={1} justifyContent="flex-end">
            {["Edit", "Delete"].map((action) => (
                <Grid item key={`${action}-${id}`}>
                    <Button
                        sx={{
                            color: "black",
                            backgroundColor: "white",
                            border: "3px solid #8FBC8F",
                        }}
                        onClick={() =>
                            action === "Edit" ? handleEdit(id) : handleDelete(id)
                        }
                    >
                        {action}
                    </Button>
                </Grid>
            ))}
        </Grid>
    );

    const renderExpandButton = (
        showMore: number,
        setShowMore: React.Dispatch<React.SetStateAction<number>>,
        data: ICreditor[] | IDebtor[],
        label: string
    ) => (
        <Button
            sx={{
                color: "black",
                backgroundColor: "white",
                border: "2px solid",
                marginBottom: "5px",
                "&:hover": {
                    color: "black",
                    backgroundColor: "#F8DE7E",
                },
            }}
            onClick={() => setShowMore(showMore + 5)}
            disabled={showMore >= data.length}
        >
            Show More {label}
        </Button>
    );

    return (
        <>
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
                    <Button
                        sx={{
                            color: "black",
                            backgroundColor: "white",
                            border: "2px solid",
                            "&:hover": {
                                color: "black",
                                backgroundColor: "#F8DE7E",
                            },
                        }}
                        onClick={() => navigate(-1)}
                    >
                        Back
                    </Button>
                    {creditorLoading || debtorLoading ? (
                        <Typography>Loading...</Typography>
                    ) : (
                        <Box>
                            <Typography variant="h5" sx={{ display: 'flex', alignItems: 'center' }}>
                                Creditors
                                <Box sx={{ marginLeft: 'auto', display: 'flex', alignItems: 'center' }}>
                                    <TextField
                                        label="Search Creditors"
                                        sx={{ background: "white" }}
                                        variant="outlined"
                                        value={creditorSearchQuery}
                                        onChange={(e) => setCreditorSearchQuery(e.target.value)}
                                        margin="normal"
                                        size="small"
                                    />
                                </Box>
                            </Typography>
                            {renderExpandButton(showMoreCreditor, setShowMoreCreditor, creditorData || [], "Creditors")}
                            {filteredCreditors?.slice(0, showMoreCreditor).map((creditor) => (
                                <Accordion key={creditor.id}>
                                    <AccordionSummary
                                        expandIcon={<ExpandMoreIcon />}
                                        aria-controls={`creditor-details-${creditor.id}`}
                                        id={`creditor-details-${creditor.id}`}
                                    >
                                        <Typography>{creditor.name}</Typography>
                                    </AccordionSummary>
                                    <AccordionDetails>
                                        <Box>
                                            <Typography>{`Address: ${creditor.address}`}</Typography>
                                            <Typography>{`Phone Number: ${creditor.phoneNumber}`}</Typography>
                                            <Typography>{`Email: ${creditor.email}`}</Typography>
                                            <Typography>{`Bank Account Number: ${creditor.accountNumber}`}</Typography>
                                            <Typography>{`Username: ${creditor.user?.username}`}</Typography>
                                            {renderActionButtons(creditor.id)}
                                        </Box>
                                    </AccordionDetails>
                                </Accordion>
                            ))}
                            <Typography variant="h5" sx={{ display: 'flex', alignItems: 'center' }}>
                                Debtors
                                <Box sx={{ marginLeft: 'auto', display: 'flex', alignItems: 'center' }}>
                                    <TextField
                                        label="Search Debtors"
                                        sx={{ background: "white" }}
                                        variant="outlined"
                                        value={debtorSearchQuery}
                                        onChange={(e) => setDebtorSearchQuery(e.target.value)}
                                        margin="normal"
                                        size="small"
                                    />
                                </Box>
                            </Typography>
                            {renderExpandButton(showMoreDebtor, setShowMoreDebtor, debtorData || [], "Debtors")}
                            {filteredDebtors?.slice(0, showMoreDebtor).map((debtor) => (
                                <Accordion key={debtor.id}>
                                    <AccordionSummary
                                        expandIcon={<ExpandMoreIcon />}
                                        aria-controls={`debtor-details-${debtor.id}`}
                                        id={`debtor-details-${debtor.id}`}
                                    >
                                        <Typography>{`${debtor.name} ${debtor.surname}`}</Typography>
                                    </AccordionSummary>
                                    <AccordionDetails>
                                        <Box>
                                            <Typography>{`Email: ${debtor.email}`}</Typography>
                                            <Typography>{`Phone Number: ${debtor.phoneNumber}`}</Typography>
                                            <Typography>{`Username: ${debtor.user?.username}`}</Typography>
                                            {renderActionButtons(debtor.id)}
                                        </Box>
                                    </AccordionDetails>
                                </Accordion>
                            ))}
                        </Box>
                    )}
                </Box>
                <Footer />
            </Box>
        </>
    );
};

export default UserListPage;