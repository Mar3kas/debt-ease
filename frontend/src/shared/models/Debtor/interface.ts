import { IUser } from "../User";
import { IVerifiedPhoneNumberInformation } from "../VerifiedPhoneNumberInformation";

export interface IDebtor {
  id: number;
  name: string;
  surname: string;
  email?: string;
  phoneNumber?: string;
  verifiedPhoneNumberInformation?: IVerifiedPhoneNumberInformation;
  user: IUser;
}
