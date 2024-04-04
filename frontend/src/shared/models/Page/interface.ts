import { type SnackbarType } from "../../../Components/Snackbar";

export interface IPage {
  openSnackbar: (message: string, type: SnackbarType) => void;
  handleErrorResponse: (error: Error) => void;
}
