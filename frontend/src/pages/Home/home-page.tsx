import { Box, Typography } from "@mui/material";
import React, { FC, ReactElement } from "react";
import { IPage } from "../../shared/models/Page/interface";
import useStyles from "../../Components/Styles/global-styles";

const HomePage: FC<IPage> = (): ReactElement => {
    const classes = useStyles();
    
    return (
      <Box
        sx={{
          flexGrow: 1,
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center'
        }}
      >
        <Typography variant='h3'>Home</Typography>
      </Box>
    );
  };

export default HomePage;