import { Anchor, Image } from "@mantine/core";
import React from "react";
import classes from "./navbar.module.css";
import logo from "@/assets/logo.png";

function Navbar() {
  return (
    <div className={classes.navbar}>
      <a className={classes.mainSection} href="/">
        <Image src={logo} className={classes.logo} />
        <h2>Welcome!</h2>
      </a>
      <div className={classes.rightSection}>
        <Anchor href="/reviews" fw={1000} fz="h2">
          Reviews
        </Anchor>
        <Anchor href="/contact-us" fw={1000} fz="h2">
          Contact
        </Anchor>
        <Anchor href="/calendar" fw={1000} fz="h2">
          Calendar
        </Anchor>
        <Anchor href="/booking" fw={1000} fz="h2">
          Booking
        </Anchor>
        <Anchor href="/registration" fw={1000} fz="h2">
          Registration
        </Anchor>
        <Anchor href="/login" fw={1000} fz="h2">
          Login
        </Anchor>
      </div>
    </div>
  );
}

export default Navbar;
