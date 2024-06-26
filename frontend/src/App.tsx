import React, { FC } from "react";
import "./App.css";
import { useSnackbar } from "./components";
import { routes as appRoutes } from "./routes";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { CssBaseline } from "@mui/material";

const App: FC = () => {
  const snackbar = useSnackbar();

  return (
    <div>
      <CssBaseline />
      <Router>
        <Routes>
          {appRoutes.map((route) => (
            <Route
              key={route.key}
              path={route.path}
              element={<route.component openSnackbar={snackbar.openSnackbar} />}
            />
          ))}
        </Routes>
        {snackbar.renderSnackbar()}
      </Router>
    </div>
  );
};

export default App;
