import { type ReactElement } from "react";
import { type SnackbarType } from "./types";

export interface ISnackbarReturn {
  openSnackbar: (message: string, type: SnackbarType) => void;
  renderSnackbar: () => ReactElement;
}
