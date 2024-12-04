import { routerType } from "../types/router.types";
import Home from "./home";
import Registration from "./registration";
import React from "react";

const pagesData: routerType[] = [
  {
    path: "",
    element: <Home />,
    title: "home",
  },
  {
    path: "register",
    element: <Registration />,
    title: "registration",
  },
];

export default pagesData;
