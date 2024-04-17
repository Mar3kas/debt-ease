import { IDebtCase } from "../debt-case";

export interface IPayment {
  id: number;
  amount: number;
  paymentMethod: string;
  description: string;
  paymentDate: string;
  debtCase: IDebtCase;
}
