import { IRole } from "../Role";

export interface IUser {
  username: string;
  password: string;
  role: IRole;
}
