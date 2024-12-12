// eslint-disable-next-line @typescript-eslint/no-unused-vars
import classes from "./app.module.css";
import { BrowserRouter } from "react-router";
import Router from "./pages/router";
import "@mantine/core/styles.css";
import "@mantine/dates/styles.css";
import { createTheme, MantineProvider } from "@mantine/core";
import Navbar from "./components/navbar";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

const theme = createTheme({
  colors: {
    lionRed: [
      "#a53939",
      "#a53939",
      "#a53939",
      "#a53939",
      "#a53939",
      "#a53939",
      "#a53939",
      "#a53939",
      "#a53939",
      "#a53939",
    ],
    lionGold: [
      "#ecba4b",
      "#ecba4b",
      "#ecba4b",
      "#ecba4b",
      "#ecba4b",
      "#ecba4b",
      "#ecba4b",
      "#ecba4b",
      "#ecba4b",
      "#ecba4b",
    ],
  },
});

function App() {
  return (
    <>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <MantineProvider theme={theme}>
          <BrowserRouter>
            <Navbar />
            <Router />
          </BrowserRouter>
        </MantineProvider>
      </LocalizationProvider>
    </>
  );
}

export default App;
