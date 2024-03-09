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
    isSent: number;
}