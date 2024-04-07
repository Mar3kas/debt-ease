import { ICreditor } from "../creditor";
import { IDebtCaseType } from "../debt-case-type";
import { IDebtor } from "../debtor";

export interface IDebtCase {
  id: number;
  amountOwed: number;
  lateInterestRate: number;
  dueDate: string;
  createdDate: string;
  modifiedDate?: string;
  debtCaseType: IDebtCaseType;
  debtCaseStatus: string;
  creditor: ICreditor;
  debtor: IDebtor;
}
