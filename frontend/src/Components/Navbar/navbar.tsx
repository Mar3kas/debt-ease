import React, { FC, ReactElement, useEffect, useState } from "react";
import {
  AppBar,
  Toolbar,
  Button,
  Typography,
  IconButton,
  Drawer,
  List,
  ListItem,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import MenuIcon from "@mui/icons-material/Menu";
import { useNavigate } from "react-router-dom";
import { NavbarProps } from "./interface";
import useStyles from "../Styles/global-styles";
import { usePost } from "../../services/api-service";
import WebSocketService from "../../services/websocket-service";

const Navbar: FC<NavbarProps> = ({ title }): ReactElement => {
  const classes = useStyles("light");
  const theme = useTheme();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const currentPageUrl = window.location.pathname;
  const { postData, data, error } = usePost<any>("logout");
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  const toggleMobileMenu = () => {
    setMobileMenuOpen((prev) => !prev);
  };

  const handleLogoClick = () => {
    navigate("/");
  };

  const handleLogout = () => {
    const webSocketService = WebSocketService.getInstance(
      "http://localhost:8080/ws"
    );
    webSocketService.disconnect();
    postData();
  };

  useEffect(() => {
    if (data !== null && data.hasOwnProperty("message")) {
      localStorage.clear();
      navigate("/");
    }
  }, [data, error, navigate]);

  return (
    <>
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
                    "&:hover": {
                      color: "black",
                      backgroundColor: "#F8DE7E",
                    },
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
              {currentPageUrl !== "/logout" && !isMobile && (
                <Button
                  sx={{
                    color: "black",
                    backgroundColor: "white",
                    border: "3px solid #8FBC8F",
                    marginRight: 2,
                    "&:hover": {
                      color: "black",
                      backgroundColor: "#F8DE7E",
                    },
                  }}
                  onClick={handleLogout}
                >
                  Logout
                </Button>
              )}
              {currentPageUrl !== "/users" && role === "ADMIN" && !isMobile && (
                <Button
                  sx={{
                    color: "black",
                    backgroundColor: "white",
                    border: "3px solid #8FBC8F",
                    marginRight: 2,
                    "&:hover": {
                      color: "black",
                      backgroundColor: "#F8DE7E",
                    },
                  }}
                  onClick={() => {
                    navigate("/users");
                  }}
                >
                  Users
                </Button>
              )}
              {currentPageUrl !== "/debtcases" && !isMobile && (
                <Button
                  sx={{
                    color: "black",
                    backgroundColor: "white",
                    border: "3px solid #8FBC8F",
                    marginRight: 1,
                    "&:hover": {
                      color: "black",
                      backgroundColor: "#F8DE7E",
                    },
                  }}
                  onClick={() => {
                    navigate("/debtcases");
                  }}
                >
                  DebtCases
                </Button>
              )}
              {currentPageUrl !== "/profile" && !isMobile && (
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
          )}
          {token && isMobile && (
            <IconButton
              edge="start"
              color="inherit"
              onClick={toggleMobileMenu}
              sx={{
                marginRight: 2,
                color: "black",
                display: { xs: "block", md: "none" },
              }}
            >
              <MenuIcon />
            </IconButton>
          )}
          <Drawer
            anchor="right"
            open={mobileMenuOpen}
            onClose={toggleMobileMenu}
          >
            <List>
              {token && (
                <>
                  <ListItem
                    sx={{
                      display: "flex",
                      justifyContent: "center",
                    }}
                  >
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
                  </ListItem>
                  <ListItem
                    sx={{
                      display: "flex",
                      justifyContent: "center",
                    }}
                  >
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
                  </ListItem>
                  {role === "ADMIN" && (
                    <ListItem
                      sx={{
                        display: "flex",
                        justifyContent: "center",
                      }}
                    >
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
                    </ListItem>
                  )}
                  <ListItem
                    sx={{
                      display: "flex",
                      justifyContent: "center",
                    }}
                  >
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
                  </ListItem>
                </>
              )}
            </List>
          </Drawer>
        </Toolbar>
      </AppBar>
    </>
  );
};

export default Navbar;
