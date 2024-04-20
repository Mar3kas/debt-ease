// global-styles.js
import { makeStyles } from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
  body: {
    backgroundColor: "#8FBC8F",
    margin: 0,
    padding: 0,
    fontFamily: "'Open Sans', sans-serif",
    lineHeight: 1.6,
  },

  header: {
    backgroundColor: "#ffffff",
    color: theme.palette.common.white,
  },

  // Content Styles
  main: {
    maxWidth: 1200,
    margin: "0 auto",
    padding: theme.spacing(2),
  },

  article: {
    border: `1px solid ${theme.palette.divider}`,
    marginBottom: theme.spacing(2),
    padding: theme.spacing(2),
  },

  footer: {
    backgroundColor: "#2E8B57",
    color: theme.palette.common.white,
    padding: theme.spacing(2),
    textAlign: "center",
  },

  // Responsive Menu Styles
  menu: {
    listStyle: "none",
    display: "flex",
  },

  menuItem: {
    marginRight: theme.spacing(2),
  },

  textField: {
    "& .MuiOutlinedInput-root": {
      "& fieldset": {
        borderColor: "#CCCCCC",
      },
      "&:hover fieldset": {
        borderColor: "black",
      },
      "& .MuiInputBase-input": {
        color: "black",
      },
      "&.Mui-focused fieldset": {
        borderColor: "#8FBC8F",
      },
    },
    "& .MuiInputLabel-root.Mui-focused": {
      color: "black",
    },
  },

  searchTextField: {
    "& .MuiOutlinedInput-root": {
      "& fieldset": {
        borderColor: "#black",
      },
      "&:hover fieldset": {
        borderColor: "black",
      },
      "& .MuiInputBase-input": {
        color: "black",
      },
      "&.Mui-focused fieldset": {
        borderColor: "black",
      },
    },
    "& .MuiInputLabel-root.Mui-focused": {
      color: "black",
    },
  },

  dateTimePicker: {
    "& .MuiOutlinedInput-root": {
      "& fieldset": {
        borderColor: "#CCCCCC",
      },
      "&:hover fieldset": {
        borderColor: "black",
      },
      "& .MuiInputBase-input": {
        color: "black",
      },
      "&.Mui-focused fieldset": {
        borderColor: "#8FBC8F",
      },
    },
    "& .MuiInputLabel-root.Mui-focused": {
      color: "black",
    },
  },

  scrollToTop: {
    position: "fixed",
    bottom: theme.spacing(-1),
    left: "50%",
    transform: "translateX(-50%)",
    width: "40px",
    height: "40px",
  },

  pagination: {
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(3),
    "& .MuiPagination-root": {
      display: "inline-block",
    },
    "& .MuiPagination-ul": {
      justifyContent: "center",
    },
    "& .MuiPaginationItem-root": {
      borderRadius: "50%",
      backgroundColor: "white",
      border: "1px solid black",
      "&:hover": {
        backgroundColor: "#F8DE7E",
      },
    },
    "& .Mui-selected": {
      backgroundColor: "#2E8B57 !important",
      color: "black",
    },
  },

  // Responsive Styles
  "@media only screen and (max-width: 768px)": {
    // Responsive Menu Styles
    menu: {
      flexDirection: "column",
      alignItems: "center",
    },

    // Hamburger Icon
    hamburgerIcon: {
      display: "block",
    },

    // Horizontal Dots Hidden
    horizontalDots: {
      display: "none",
    },
  },

  paper: {
    width: "40%",
    margin: "auto",
    [theme.breakpoints.up("md")]: {
      width: "40%",
    },
  },

  graph: {
    width: "60%",
    margin: "auto",
    [theme.breakpoints.up("md")]: {
      width: "60%",
    },
  },

  form: {
    flexGrow: 1,
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    padding: theme.spacing(2),
  },

  creditorProfile: {
    flexGrow: 1,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    padding: theme.spacing(2),
  },

  debtorProfile: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    padding: theme.spacing(2),
  },

  adminProfile: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    padding: theme.spacing(2),
  },
}));

export default useStyles;
