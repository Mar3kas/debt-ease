import { IUser } from "../user";

export interface IAdmin {
  id: number;
  name: string;
  surname: string;
  user: IUser;
}
