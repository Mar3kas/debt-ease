import React, { FC, ReactElement } from "react";
import { Box, Typography } from "@mui/material";
import useStyles from "../Styles/global-styles";

const Footer: FC = (): ReactElement => {
  const classes = useStyles("light");

  return (
    <Box
      component="footer"
      className={classes.footer}
      sx={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
      }}
    >
      <Typography variant="body2" align="center" gutterBottom>
        All rights are reserved.
      </Typography>
      <Typography variant="body2" align="center">
        Created by Marijus Petkevičius
      </Typography>
    </Box>
  );
};

export default Footer;