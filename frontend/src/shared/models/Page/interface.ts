import { type SnackbarType } from "../../../components/page-snackbar";

export interface IPage {
  openSnackbar: (message: string, type: SnackbarType) => void;
  handleErrorResponse: (error: Error) => void;
}
