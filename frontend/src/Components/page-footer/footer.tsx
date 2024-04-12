import React, { FC, ReactElement } from "react";
import { Box, Typography } from "@mui/material";
import useStyles from "../page-styles/global-styles";

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
        © 2024 DebtEase. Data is collected and stored in the register of legal
        entities. All rights reserved in the Republic of Lithuania laws.
      </Typography>
    </Box>
  );
};

export default Footer;
