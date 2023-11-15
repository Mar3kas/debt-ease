import { IDebtCaseStatus } from "../../models/debtCaseStatus";
import { IDebtCaseType } from "../../models/debtCaseType";
import { ICreditor } from "../creditor";
import { IDebtor } from "../debtor";

export interface IDebtCase {
    id: number;
    amountOwed: number;
    dueDate: string;
    debtCaseType: IDebtCaseType;
    debtCaseStatus: IDebtCaseStatus;
    creditor: ICreditor;
    debtors: IDebtor[];
    isSent: number;
}