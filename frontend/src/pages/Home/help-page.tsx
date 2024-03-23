import React, { FC, ReactElement } from "react";
import { Box, Typography, IconButton } from "@mui/material";
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import { useNavigate } from "react-router-dom";
import Navbar from "../../Components/Navbar/navbar";
import Footer from "../../Components/Footer/footer";

const SystemInfoPage: FC = (): ReactElement => {
  const navigate = useNavigate();

  return (
    <Box
      sx={{
        flexGrow: 1,
        display: "flex",
        flexDirection: "column",
        justifyContent: "space-between",
        minHeight: "100vh",
        height: "100%",
        overflow: "hidden",
        backgroundColor: "#8FBC8F",
      }}
    >
      <Navbar title="System Information" />
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          padding: 2,
          marginTop: 20,
        }}
      >
        <Typography variant="h4" component="div" sx={{ color: "black" }}>
          System Information Page
        </Typography>
        <Typography variant="body1">
          Welcome to our system information page! Here, we're excited to share
          with you our mission, vision, and values, along with details about how
          our platform strives to revolutionize debt management for both debtors
          and creditors.
        </Typography>
        <Box sx={{ marginTop: 20 }}>
          <Typography variant="h5" sx={{ color: "black", marginBottom: 1 }}>
            Our Mission, Vision, and Values:
          </Typography>
          <Typography variant="body1" sx={{ color: "black" }}>
            <strong>Mission:</strong> Our mission is to empower individuals
            through trustworthy debt management, fostering financial well-being,
            and building lasting trust. We believe in providing tools and
            resources that enable users to take control of their financial
            futures confidently.
          </Typography>
          <Typography variant="body1" sx={{ color: "black" }}>
            <strong>Vision:</strong> We envision a future where debt management
            is redesigned for real-time connections, innovative tools, and
            financial empowerment. Our goal is to create a platform that enables
            users to navigate their financial journeys with ease, transparency,
            and empowerment.
          </Typography>
          <Typography variant="body1" sx={{ color: "black" }}>
            <strong>Values:</strong> At the core of our operations are
            integrity, collaboration, and user empowerment. We are committed to
            redefining the debt management experience by prioritizing these
            values in everything we do. By fostering a culture of trust,
            transparency, and inclusivity, we aim to create meaningful
            relationships and positive outcomes for all stakeholders involved.
          </Typography>
        </Box>
      </Box>
      <Footer />
      <IconButton
        color="inherit"
        sx={{
          position: "fixed",
          bottom: 20,
          left: 20,
          backgroundColor: "#F8DE7E",
          "&:hover": {
            color: "#8B7255",
            backgroundColor: "#F8DE7E",
          },
          color: "#654321",
        }}
        onClick={() => {
          navigate(-1);
        }}
      >
        <ArrowBackIosIcon />
      </IconButton>
    </Box>
  );
};

export default SystemInfoPage;
