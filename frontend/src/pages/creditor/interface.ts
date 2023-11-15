import { IDebtCase } from "../debtcases";
import { IUser } from "../user";

export interface ICreditor {
    id: number;
    name: string;
    address: string;
    phone: string;
    email: string;
    accountNumber: string;
    debtCase: IDebtCase[];
    user: IUser;
}