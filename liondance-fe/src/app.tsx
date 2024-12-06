// eslint-disable-next-line @typescript-eslint/no-unused-vars
import classes from "./app.module.css";
import { BrowserRouter } from "react-router";
import Router from "./pages/router";
import "@mantine/core/styles.css";
import { createTheme, MantineProvider } from "@mantine/core";
import Navbar from "./components/navbar";

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
      <MantineProvider theme={theme}>
        <BrowserRouter>
          <Navbar />
          <Router />
        </BrowserRouter>
      </MantineProvider>
    </>
  );
}

export default App;
