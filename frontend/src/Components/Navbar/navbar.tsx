import React, { FC, ReactElement } from "react";
import { AppBar, Toolbar, Button, Typography, IconButton } from "@mui/material";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import { useNavigate } from "react-router-dom";
import { NavbarProps } from "./interface";
import useStyles from "../Styles/global-styles";
import { usePost } from "../../services/api-service";

const Navbar: FC<NavbarProps> = ({ title }): ReactElement => {
  const classes = useStyles("light");
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const role = localStorage.getItem("role");
  const currentPageUrl = window.location.pathname;

  const { postData, data, loading, error } = usePost<null>("logout");

  const handleLogoClick = () => {
    navigate("/");
  };

  const handleLogout = () => {
    postData();
    navigate("/");
    localStorage.clear();
  };

  return (
    <AppBar
      position="static"
      component="header"
      className={classes.header}
      sx={{ backgroundColor: "#ffffff", color: "white" }}
    >
      <Toolbar>
        <a href="/" onClick={handleLogoClick}>
          <img
            src={require("../../Images/debtease.png")}
            alt="DebtEase Logo"
            style={{ height: "80px", cursor: "pointer" }}
          />
        </a>
        <Typography
          variant="h6"
          component="div"
          sx={{ flexGrow: 1, color: "black" }}
        >
          {title}
        </Typography>
        {token === null ? (
          <>
            {currentPageUrl !== "/login" && (
              <Button
                sx={{
                  color: "black",
                  backgroundColor: "white",
                  border: "3px solid #8FBC8F",
                }}
                onClick={() => {
                  navigate("/login");
                }}
              >
                Login
              </Button>
            )}
          </>
        ) : (
          <>
            {currentPageUrl !== "/logout" && (
              <Button
                sx={{
                  color: "black",
                  backgroundColor: "white",
                  border: "3px solid #8FBC8F",
                  marginRight: 2,
                }}
                onClick={handleLogout}
              >
                Logout
              </Button>
            )}
            {currentPageUrl !== "/users" && role === "ADMIN" && (
              <Button
                sx={{
                  color: "black",
                  backgroundColor: "white",
                  border: "3px solid #8FBC8F",
                  marginRight: 2,
                }}
                onClick={() => {
                  navigate("/users");
                }}
              >
                Users
              </Button>
            )}
            {currentPageUrl !== "/debtcases" && (
              <Button
                sx={{
                  color: "black",
                  backgroundColor: "white",
                  border: "3px solid #8FBC8F",
                  marginRight: 1,
                }}
                onClick={() => {
                  navigate("/debtcases");
                }}
              >
                DebtCases
              </Button>
            )}
            {currentPageUrl !== "/profile" && (
              <IconButton
                color="inherit"
                sx={{
                  borderRadius: "50%",
                  margin: "0 5px",
                  padding: 1,
                  backgroundColor: "#F8DE7E",
                  "&:hover": {
                    color: "#8B7255",
                    backgroundColor: "#F8DE7E",
                  },
                  color: "#654321",
                }}
                onClick={() => {
                  navigate("/profile");
                }}
              >
                <AccountCircleIcon style={{ fontSize: 30 }} />
              </IconButton>
            )}
          </>
        )};
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;