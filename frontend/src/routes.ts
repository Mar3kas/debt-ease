import { type FC } from "react";
import { HomePage, LoginPage } from "./pages";

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
    key: "login-page-route",
    title: "Login",
    path: "/login",
    enabled: true,
    component: LoginPage,
  },
];