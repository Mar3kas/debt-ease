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

  // Header Styles
  header: {
    backgroundColor: "#ffffff",
    color: theme.palette.common.white,
  },

  // Content Styles
  main: {
    maxWidth: 1200,
    margin: '0 auto',
    padding: theme.spacing(2),
  },

  article: {
    border: `1px solid ${theme.palette.divider}`,
    marginBottom: theme.spacing(2),
    padding: theme.spacing(2),
  },

  // Footer Styles
  footer: {
    backgroundColor: "#2E8B57",
    color: theme.palette.common.white,
    padding: theme.spacing(2),
    textAlign: 'center',
  },

  // Responsive Menu Styles
  menu: {
    listStyle: 'none',
    display: 'flex',
  },

  menuItem: {
    marginRight: theme.spacing(2),
  },

  // TextField Styles
  textField: {
    '& .MuiOutlinedInput-root': {
      '& fieldset': {
        borderColor: 'black',
      },
      '&:hover fieldset': {
        borderColor: 'rgba(152, 251, 152, 0.7)',
      },
      '&.Mui-focused fieldset': {
        borderColor: 'rgba(152, 251, 152, 1)',
      },
    },
    '& .Mui-focused': {
      color: 'rgba(152, 251, 152, 1)',
    },
  },

  // Responsive Styles
  '@media only screen and (max-width: 768px)': {
    // Responsive Menu Styles
    menu: {
      flexDirection: 'column',
      alignItems: 'center',
    },

    // Hamburger Icon
    hamburgerIcon: {
      display: 'block',
    },

    // Horizontal Dots Hidden
    horizontalDots: {
      display: 'none',
    },
  },

  paper: {
    width: '40%',
    margin: 'auto',
    [theme.breakpoints.up('md')]: {
      width: '40%',
    },
  },

  form: {
    flexGrow: 1,
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
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