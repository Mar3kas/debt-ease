import { IUser } from "../User";

export interface IDebtor {
    id: number;
    name: string;
    surname: string;
    email?: string;
    phoneNumber?: string;
    user: IUser;
}