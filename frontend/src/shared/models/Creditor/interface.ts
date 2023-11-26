import { IUser } from "../User";

export interface ICreditor {
    id: number;
    name: string;
    address: string;
    phoneNumber: string;
    email: string;
    accountNumber: string;
    user?: IUser;
}