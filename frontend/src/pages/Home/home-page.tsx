import React, { FC, ReactElement, useState, useEffect } from "react";
import { Box, Typography, IconButton } from "@mui/material";
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import HelpIcon from "@mui/icons-material/Help";
import ContactSupportIcon from "@mui/icons-material/ContactSupport";
import { useTransition, animated, useSpring } from "@react-spring/web";
import useStyles from "../../Components/Styles/global-styles";
import { useNavigate } from "react-router-dom";
import Navbar from "../../Components/Navbar/navbar";
import Footer from "../../Components/Footer/footer";

const slides = [
  {
    id: 1,
    title: "Mission",
    content:
      "Empower through trustworthy debt management, fostering financial well-being and lasting trust.",
  },
  {
    id: 2,
    title: "Vision",
    content:
      "Redesigning debt management for real-time connections, innovative tools, and financial empowerment.",
  },
  {
    id: 3,
    title: "Values",
    content:
      "Guided by integrity, collaboration, and user empowerment to redefine the debt management experience.",
  },
];

const HomePage: FC = (): ReactElement => {
  const classes = useStyles("light");
  const navigate = useNavigate();
  const [index, setIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      handleNextSlide();
    }, 5000);

    return () => clearInterval(interval);
  }, [index]);

  const transitions = useTransition(slides[index], {
    keys: (slide) => slide.id,
    from: { opacity: 0, transform: "translate3d(100%,0,0)" },
    enter: { opacity: 1, transform: "translate3d(0%,0,0)" },
    leave: { opacity: 0, transform: "translate3d(-100%,0,0)" },
    config: { tension: 220, friction: 20 },
  });

  const handleNextSlide = () => {
    setIndex((prevIndex) => (prevIndex + 1) % slides.length);
  };

  const handleBackSlide = () => {
    setIndex((prevIndex) =>
      prevIndex === 0 ? slides.length - 1 : prevIndex - 1
    );
  };

  const sloganStyle = useSpring({
    opacity: 1,
    from: { opacity: 0 },
    config: { duration: 3000 },
  });

  return (
    <Box
      className={classes.body}
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
      <Navbar title="DebtEase" />
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          padding: 2,
          position: "relative",
        }}
      >
        <animated.div style={sloganStyle}>
          <Typography variant="h4" component="div" sx={{ color: "black" }}>
            Trust In Every Debt
          </Typography>
        </animated.div>
        {transitions((style, item) => (
          <animated.div
            style={{
              ...style,
              position: "absolute",
              width: "60%",
            }}
          >
            <Box
              sx={{
                border: "2px solid #8FBC8F",
                backgroundColor: "white",
                padding: 3,
                borderRadius: 8,
                textAlign: "center",
                position: "relative",
              }}
            >
              <ArrowBackIosIcon
                onClick={handleBackSlide}
                sx={{
                  position: "absolute",
                  left: 10,
                  top: "50%",
                  transform: "translateY(-50%)",
                  color: "black",
                  "&:hover": {
                    color: "#654321",
                  },
                }}
              />
              <Typography variant="h3" textAlign="center">
                {item.title}
              </Typography>
              <Typography>{item.content}</Typography>
              <ArrowForwardIosIcon
                onClick={handleNextSlide}
                sx={{
                  position: "absolute",
                  right: 10,
                  top: "50%",
                  transform: "translateY(-50%)",
                  color: "black",
                  "&:hover": {
                    color: "#654321",
                  },
                }}
              />
            </Box>
          </animated.div>
        ))}
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            marginTop: "160px",
          }}
        >
          <IconButton
            color="inherit"
            sx={{
              borderRadius: "50%",
              margin: "0 5px",
              padding: 3,
              backgroundColor: "#F8DE7E",
              "&:hover": {
                color: "#8B7255",
                backgroundColor: "#F8DE7E",
              },
              color: "#654321",
            }}
            onClick={() => { }}
          >
            <HelpIcon style={{ fontSize: 60 }} />
          </IconButton>
          <IconButton
            color="inherit"
            sx={{
              borderRadius: "50%",
              margin: "0 30px",
              padding: 3,
              backgroundColor: "#F8DE7E",
              "&:hover": {
                color: "#8B7255",
                backgroundColor: "#F8DE7E",
              },
              color: "#654321",
            }}
            onClick={() => { }}
          >
            <ContactSupportIcon style={{ fontSize: 60 }} />
          </IconButton>
        </Box>
      </Box>
      <Footer />
    </Box>
  );
};

export default HomePage;