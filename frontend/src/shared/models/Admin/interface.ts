import { IUser } from "../User";

export interface IAdmin {
  id: number;
  name: string;
  surname: string;
  user: IUser;
}
