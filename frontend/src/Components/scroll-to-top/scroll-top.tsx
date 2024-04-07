import { IconButton } from "@mui/material";
import React, { useState, useEffect } from "react";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
import useStyles from "../page-styles/global-styles";

const ScrollToTopButton = () => {
  const classes = useStyles("light");
  const [isVisible, setIsVisible] = useState(false);
  useEffect(() => {
    const toggleVisibility = () => {
      if (window.scrollY > 400) {
        setIsVisible(true);
      } else {
        setIsVisible(false);
      }
    };

    window.addEventListener("scroll", toggleVisibility);

    return () => {
      window.removeEventListener("scroll", toggleVisibility);
    };
  }, []);

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  return (
    <IconButton
      className={classes.scrollToTop}
      onClick={scrollToTop}
      style={{
        display: isVisible ? "block" : "none",
        color: "black",
        backgroundColor: "white",
      }}
      aria-label="scroll to top"
    >
      <KeyboardArrowUpIcon />
    </IconButton>
  );
};

export default ScrollToTopButton;
