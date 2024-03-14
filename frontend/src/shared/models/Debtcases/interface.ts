import { ICompany } from "../CompanyInformation";
import { ICreditor } from "../Creditor";
import { IDebtCaseStatus } from "../DebtCaseStatus";
import { IDebtCaseType } from "../DebtCaseType";
import { IDebtor } from "../Debtor";

export interface IDebtCase {
  id: number;
  amountOwed: number;
  dueDate: string;
  debtCaseType: IDebtCaseType;
  debtCaseStatus: IDebtCaseStatus;
  creditor: ICreditor;
  debtor: IDebtor;
  company: ICompany;
  isSent: number;
}
