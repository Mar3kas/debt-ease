import { IUser } from "../user";
import { IVerifiedPhoneNumberInformation } from "../verified-phone-number-information";

export interface IDebtor {
  id: number;
  name: string;
  surname: string;
  email?: string;
  phoneNumber?: string;
  verifiedPhoneNumberInformation?: IVerifiedPhoneNumberInformation;
  user: IUser;
}
