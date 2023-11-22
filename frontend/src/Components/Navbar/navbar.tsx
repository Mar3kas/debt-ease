import React, { FC, ReactElement } from "react";
import { AppBar, Toolbar, Button, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { NavbarProps } from "./interface";
import useStyles from "../Styles/global-styles";

const Navbar: FC<NavbarProps> = ({ title }): ReactElement => {
  const classes = useStyles("light");
  const navigate = useNavigate();
  const currentPageUrl = window.location.pathname;

  const handleLogoClick = () => {
    navigate("/");
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
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;