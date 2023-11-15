import { IDebtCase } from "../debtcases";
import { IUser } from "../user";

export interface IDebtor {
    id: number;
    name: string;
    surname: string;
    email?: string;
    phoneNumber?: string;
    debtCase: IDebtCase[];
    user?: IUser;
}