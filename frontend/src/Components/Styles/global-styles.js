import { makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme) => ({
  // Reset some default browser styles
  body: {
    margin: 0,
    padding: 0,
    fontFamily: "'Open Sans', sans-serif",
    lineHeight: 1.6,
  },
  
  // Header Styles
  header: {
    backgroundColor: theme.palette.primary.main,
    color: theme.palette.common.white,
    padding: theme.spacing(2),
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
    backgroundColor: theme.palette.primary.main,
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
}));

export default useStyles;