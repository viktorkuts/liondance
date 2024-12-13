// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { BrowserRouter } from "react-router";
import Router from "./pages/router";
import "@mantine/core/styles.css";
import "@mantine/dates/styles.css";
import { createTheme, MantineProvider } from "@mantine/core";
import Navbar from "./components/navbar";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { Auth0Provider } from "@auth0/auth0-react";

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
      <Auth0Provider
        domain={String(import.meta.env.OKTA_ISSUER)
          .replace("//", "")
          .replace("https:", "")
          .replace("/", "")}
        clientId={import.meta.env.OKTA_CLIENT_ID}
        authorizationParams={{
          redirect_uri: window.location.origin,
          audience: import.meta.env.OKTA_AUDIENCE,
        }}
        cacheLocation="localstorage"
      >
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <MantineProvider theme={theme}>
            <BrowserRouter>
              <Navbar />
              <Router />
            </BrowserRouter>
          </MantineProvider>
        </LocalizationProvider>
      </Auth0Provider>
    </>
  );
}

export default App;
