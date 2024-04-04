import { type FC } from "react";
import {
  HomePage,
  LoginPage,
  UserProfilePage,
  DebtCaseListPage,
  UserListPage,
  DebtorFormPage,
  CreditorFormPage,
  CreditorCreationFormPage,
  DebtCaseFormPage,
  SystemInfoPage,
  DebtCasePayPage,
} from "./pages";

interface Route {
  key: string;
  title: string;
  path: string;
  enabled: boolean;
  component: FC<any>;
}

export const routes: Route[] = [
  {
    key: "home-page-route",
    title: "Home",
    path: "/",
    enabled: true,
    component: HomePage,
  },
  {
    key: "help-page-route",
    title: "Information",
    path: "/information",
    enabled: true,
    component: SystemInfoPage,
  },
  {
    key: "login-page-route",
    title: "Login",
    path: "/login",
    enabled: true,
    component: LoginPage,
  },
  {
    key: "profile-page-route",
    title: "Profile",
    path: "/profile",
    enabled: true,
    component: UserProfilePage,
  },
  {
    key: "debtcase-list-page-route",
    title: "DebtCases",
    path: "/debt/cases",
    enabled: true,
    component: DebtCaseListPage,
  },
  {
    key: "user-list-page-route",
    title: "Users",
    path: "/users",
    enabled: true,
    component: UserListPage,
  },
  {
    key: "debtor-form-page-route",
    title: "Debtor Edit",
    path: "/debtors/:id?",
    enabled: true,
    component: DebtorFormPage,
  },
  {
    key: "creditor-form-page-route",
    title: "Creditor Edit",
    path: "/creditors/:id?",
    enabled: true,
    component: CreditorFormPage,
  },
  {
    key: "creditor-create-form-page-route",
    title: "Creditor Creation",
    path: "/users/new",
    enabled: true,
    component: CreditorCreationFormPage,
  },
  {
    key: "debtcase-form-page-route",
    title: "DebtCase Edit",
    path: "/debt/cases/:debtcaseId?/creditor/:creditorId?",
    enabled: true,
    component: DebtCaseFormPage,
  },
  {
    key: "pay-page-route",
    title: "DebtCase Payment",
    path: "/debt/cases/:id?/pay",
    enabled: true,
    component: DebtCasePayPage,
  },
];
