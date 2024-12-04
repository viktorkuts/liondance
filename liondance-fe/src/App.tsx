import "./App.css";
import { BrowserRouter } from "react-router";
import Router from "./pages/router";
import { MantineProvider } from "@mantine/core";
import Navbar from "./components/navbar";

function App() {
  return (
    <>
      <MantineProvider>
        <BrowserRouter>
          <Navbar />
          <Router />
        </BrowserRouter>
      </MantineProvider>
    </>
  );
}

export default App;
