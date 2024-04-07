import { ICompany } from "../company-information";
import { IUser } from "../user";

export interface ICreditor {
  id: number;
  name: string;
  address: string;
  phoneNumber: string;
  email: string;
  accountNumber: string;
  user: IUser;
  company: ICompany;
}
