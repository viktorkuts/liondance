import { Anchor, Image } from "@mantine/core";
import React, { useState } from "react";
import classes from "./navbar.module.css";
import logo from "../../assets/logo.png";


function Navbar() {
  const [adminDropdownOpen, setAdminDropdownOpen] = useState(false);

  const toggleAdminDropdown = () => {
    setAdminDropdownOpen(!adminDropdownOpen);
  };

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
          Book Event
        </Anchor>
        <Anchor href="/registration" fw={1000} fz="h2">
          Registration
        </Anchor>
        <Anchor href="/pending-registrations" fw={1000} fz="h2">
          Pending Registrations
        </Anchor>
        <Anchor href="/login" fw={1000} fz="h2">
          Login
        </Anchor>
        <div className={classes.dropdown}>
          <Anchor onClick={toggleAdminDropdown} fw={1000} fz="h2">
            Admin
          </Anchor>
          {adminDropdownOpen && (
           <div className={classes.dropdownContent}>
             <Anchor href="/users" fw={1000} fz="h2">
               Users
             </Anchor>
             <Anchor href="/students" fw={1000} fz="h2">
               Students
             </Anchor>
              <Anchor href="/events" fw={1000} fz="h2">
               Events
              </Anchor>
              <Anchor href="/promotions" fw={1000} fz="h2">
              Promotions
              </Anchor>
           </div>
          )}
        </div>
        <Anchor href="/student-courses" fw={1000} fz={"h2"}>
          Courses
        </Anchor>
      </div>
    </div>
  );
}

export default Navbar;
