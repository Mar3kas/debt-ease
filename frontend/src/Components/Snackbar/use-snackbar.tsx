import React, {
  type ReactElement,
  useState,
  useCallback,
  forwardRef,
} from "react";
import CloseIcon from "@mui/icons-material/Close";
import { type SnackbarType } from "./types";
import { type ISnackbarReturn } from "./interface";
import { AlertProps, Grow, IconButton, Snackbar } from "@mui/material";
import MuiAlert from "@mui/material/Alert";

const Alert = (props: any, ref: any): ReactElement => {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
};

const SnackbarAlert = forwardRef<HTMLDivElement, AlertProps>(Alert);

const useSnackbar = (): ISnackbarReturn => {
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const [snackbarType, setSnackbarType] =
    useState<AlertProps["severity"]>("info");

  const handleSnackbarOpen = useCallback(
    (message: string, type: SnackbarType = "info"): void => {
      setSnackbarMessage(message);
      setSnackbarType(type);
      setSnackbarOpen(true);
    },
    []
  );

  const handleSnackbarClose = (
    event: React.SyntheticEvent | Event,
    reason?: string
  ): void => {
    if (reason === "clickaway") {
      return;
    }
    setSnackbarOpen(false);
  };

  const openSnackbar = useCallback(
    (message: string, type: SnackbarType = "info"): void => {
      handleSnackbarOpen(message, type);
    },
    [handleSnackbarOpen]
  );

  const renderSnackbar = useCallback((): ReactElement => {
    return (
      <Snackbar
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
        TransitionComponent={Grow}
        open={snackbarOpen}
        autoHideDuration={5000}
        onClose={handleSnackbarClose}
      >
        <SnackbarAlert
          severity={snackbarType}
          action={
            <IconButton
              size="small"
              aria-label="close"
              color="inherit"
              onClick={handleSnackbarClose}
            >
              <CloseIcon fontSize="small" />
            </IconButton>
          }
        >
          {snackbarMessage}
        </SnackbarAlert>
      </Snackbar>
    );
  }, [snackbarType, snackbarMessage, snackbarOpen]);

  return { openSnackbar, renderSnackbar };
};

export default useSnackbar;