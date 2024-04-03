import React, { FC, ReactElement } from "react";
import {
  Box,
  Typography,
  IconButton,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from "@mui/material";
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
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
        }}
      >
        <Box
          sx={{
            backgroundColor: "white",
            padding: 2,
            borderRadius: 8,
            marginBottom: 4,
          }}
        >
          <Typography
            variant="h4"
            sx={{ color: "black", textAlign: "center", paddingBottom: 2 }}
          >
            System Information
          </Typography>
          <Typography variant="body1" sx={{ color: "black" }}>
            Welcome to DebtEase system information page! Here, we're excited to
            share with you our mission, vision, and values, along with details
            about how our platform strives to revolutionize debt management for
            both debtors and creditors.
          </Typography>
        </Box>

        <Box>
          <Accordion>
            <AccordionSummary
              aria-controls="panel1a-content"
              id="panel1a-header"
              expandIcon={<ExpandMoreIcon sx={{ color: "#2E8B57" }} />}
              sx={{ borderBottom: "1px solid #ccc" }}
            >
              <Typography variant="h5" sx={{ color: "black" }}>
                Information for Debtors
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <Typography variant="body1" sx={{ color: "black" }}>
                As a debtor, you can benefit from our debt management system by
                gaining access to personalized financial counseling, debt
                negotiation services, and tailored repayment plans. Our platform
                is designed to empower you to take control of your finances and
                work towards a debt-free future. We provide well timed
                notifications about your debts, enriched data about creditors
                and much more!
              </Typography>
            </AccordionDetails>
          </Accordion>

          <Accordion>
            <AccordionSummary
              aria-controls="panel1a-content"
              id="panel1a-header"
              expandIcon={<ExpandMoreIcon sx={{ color: "#2E8B57" }} />}
              sx={{ borderBottom: "1px solid #ccc" }}
            >
              <Typography variant="h5" sx={{ color: "black" }}>
                Information for Creditors
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <Typography variant="body1" sx={{ color: "black" }}>
                Creditors play a vital role in our debt management system. To
                participate, creditors are required to contact our support at
                debtease@gmail.com and request a form, fill it out with accurate
                details, and send it back to the same email. Upon receiving the
                completed form, our team will initiate the debt management
                process and work towards mutually beneficial resolutions.
                <br />
                <br />
                We enrich debt case data about your company and additional
                information about debtors!
                <br />
              </Typography>
            </AccordionDetails>
          </Accordion>
          <Accordion>
            <AccordionSummary
              aria-controls="panel1a-content"
              id="panel1a-header"
              expandIcon={<ExpandMoreIcon sx={{ color: "#2E8B57" }} />}
              sx={{ borderBottom: "1px solid #ccc" }}
            >
              <Typography variant="h5" sx={{ color: "black" }}>
                Our Mission, Values, and Vision
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <Typography variant="body1" sx={{ color: "black" }}>
                <strong>Mission:</strong> Our mission is to empower individuals
                through trustworthy debt management, fostering financial
                well-being, and building lasting trust. We believe in providing
                tools and resources that enable users to take control of their
                financial futures confidently.
                <br />
                <br />
              </Typography>
              <Typography variant="body1" sx={{ color: "black" }}>
                <strong>Vision:</strong> We envision a future where debt
                management is redesigned for real-time connections, innovative
                tools, and financial empowerment. Our goal is to create a
                platform that enables users to navigate their financial journeys
                with ease, transparency, and empowerment.
                <br />
                <br />
              </Typography>
              <Typography variant="body1" sx={{ color: "black" }}>
                <strong>Values:</strong> At the core of our operations are
                integrity, collaboration, and user empowerment. We are committed
                to redefining the debt management experience by prioritizing
                these values in everything we do. By fostering a culture of
                trust, transparency, and inclusivity, we aim to create
                meaningful relationships and positive outcomes for all
                stakeholders involved.
              </Typography>
            </AccordionDetails>
          </Accordion>
        </Box>
      </Box>
      <Footer />
      <IconButton
        color="inherit"
        sx={{
          position: "fixed",
          bottom: 10,
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
