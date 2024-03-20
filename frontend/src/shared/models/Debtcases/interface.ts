import { ICreditor } from "../Creditor";
import { IDebtCaseStatus } from "../DebtCaseStatus";
import { IDebtCaseType } from "../DebtCaseType";
import { IDebtor } from "../Debtor";

export interface IDebtCase {
  id: number;
  amountOwed: number;
  lateInterestRate: number;
  outstandingBalance: number;
  dueDate: string;
  createdDate: string;
  modifiedDate?: string;
  debtCaseType: IDebtCaseType;
  debtCaseStatus: IDebtCaseStatus;
  creditor: ICreditor;
  debtor: IDebtor;
}
