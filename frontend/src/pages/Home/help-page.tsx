import React, { FC, ReactElement, useEffect } from "react";
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
import Navbar from "../../components/page-navbar/navbar";
import Footer from "../../components/page-footer/footer";
import { useGet } from "../../services/api-service";

const SystemInfoPage: FC = (): ReactElement => {
  const navigate = useNavigate();

  const {
    data: csvData,
    loading: csvFileLoading,
    error: csvError,
    getData: getCSVData,
  } = useGet<any>("files/debt/case/example", {}, false, false, "blob");

  const {
    data: wordData,
    loading: wordFileLoading,
    error: wordError,
    getData: getWordData,
  } = useGet<any>("files/agreement/form", {}, false, false, "blob");

  useEffect(() => {
    if (csvData && !csvFileLoading && !csvError) {
      if (csvData) {
        const blob = new Blob([csvData], { type: "text/csv" });
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `debt_case_upload_example.csv`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
      }
    }
  }, [csvData, csvFileLoading, csvError]);

  const handleDownloadCSV = async () => {
    getCSVData();
  };

  const handleDownloadWord = async () => {
    getWordData();
  };

  useEffect(() => {
    if (wordData && !wordFileLoading && !wordError) {
      if (wordData) {
        const blob = new Blob([wordData], {
          type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        });
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `agreement_form.docx`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
      }
    }
  }, [wordData, wordFileLoading, wordError]);

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
            Welcome to the DebtEase system information page! Here, we're excited
            to share with you our mission, vision, and values, along with
            details about how our platform strives to revolutionize debt
            management for both debtors and creditors.
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
                gaining access to your debts with detailed information, paying
                your debts conveniently, viewing debt reports, and determining
                when you will become debt-free by applying Snowball or Avalanche
                payment strategies. Our platform is designed to empower you to
                take control of your finances and work towards a debt-free
                future. We provide well-timed notifications about your debts,
                enriched data about creditors, and much more!
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
                Creditors play a vital role in our debt management system by
                uploading debt cases which are then enriched by additional data.
                To participate, creditors are required to fill in agreement form
                with accurate details. Upon receiving the completed form, our
                team will initiate the debt management process and work towards
                mutually beneficial resolutions.
                <br />
                <br />
                We enrich debt case data about your company and provide
                additional information about debtors!
                <br />
                <br />
                To start using the DebtEase system, you need to apply by
                downloading and filling in this form:
                <a href="#" onClick={handleDownloadWord}>
                  Download Agreement Form
                </a>
                . Further details are written in the agreement.
                <br />
                <br />
                When uploading debt cases, please download the CSV example and
                use it as a guideline:
                <a href="#" onClick={handleDownloadCSV}>
                  Download CSV Example
                </a>
                .
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
                <strong>Vision:</strong> We envision a future where debt
                management is redesigned for real-time connections, innovative
                tools, and financial empowerment. Our goal is to create a
                platform that enables users to navigate their financial journeys
                with ease, transparency, and empowerment.
                <br />
                <br />
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
