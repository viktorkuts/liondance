import { Anchor, Image } from "@mantine/core";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import classes from "./navbar.module.css";
import logo from "../../assets/logo.png";
import { useAuth0 } from "@auth0/auth0-react";
import { useUserContext } from "@/utils/userProvider";
import { Role } from "@/models/Users";

function Navbar() {
  const { t } = useTranslation();
  const [adminDropdownOpen, setAdminDropdownOpen] = useState(false);

  const toggleAdminDropdown = () => {
    setAdminDropdownOpen(!adminDropdownOpen);
  };

  const { loginWithRedirect, logout, isAuthenticated } = useAuth0();
  const { user } = useUserContext();

  useEffect(() => {
    console.log(user);
  }, [user]);

  return (
    <div className={classes.navbar}>
      <a className={classes.mainSection} href="/">
        <Image src={logo} className={classes.logo} />
        <h2>{t('Welcome!')}</h2>
      </a>
      <div className={classes.rightSection}>
        <Anchor href="/reviews" fw={1000} fz="h2">
          {t('Reviews')}
        </Anchor>
        <Anchor href="/contact-us" fw={1000} fz="h2">
          {t('Contact')}
        </Anchor>
        {user && user.roles?.includes(Role.STUDENT) ? (
          <Anchor href="/student-courses" fw={1000} fz={"h2"}>
            {t('Courses')}
          </Anchor>
        ) : null}
        <Anchor href="/filtered-events" fw={1000} fz="h2">
          {t('Upcoming Events')}
        </Anchor>
        <Anchor href="/booking" fw={1000} fz="h2">
          {t('Book Event')}
        </Anchor>
        {user && user.roles?.includes(Role.CLIENT) ? (
          <Anchor href="/events/email/:email" fw={1000} fz="h2">
            {t('Your Events')}
          </Anchor>
        ) : null}
        <Anchor href="/registration" fw={1000} fz="h2">
          {t('Registration')}
        </Anchor>
        {user && user.roles?.includes(Role.STAFF) ? (
          <Anchor href="/pending-registrations" fw={1000} fz="h2">
            {t('Pending Registrations')}
          </Anchor>
        ) : null}
        <Anchor
          onClick={() => {
            if (!isAuthenticated) {
              loginWithRedirect();
            } else {
              logout({
                logoutParams: {
                  returnTo: window.location.origin,
                },
              });
            }
          }}
          fw={1000}
          fz="h2"
        >
          {isAuthenticated ? t('Logout') : t('Login')}
        </Anchor>
        {user && user.roles?.includes(Role.STAFF) ? (
          <div className={classes.dropdown}>
            <Anchor onClick={toggleAdminDropdown} fw={1000} fz="h2">
              {t('Admin')}
            </Anchor>
            {adminDropdownOpen && (
              <div className={classes.dropdownContent}>
                <Anchor href="/users" fw={1000} fz="h2">
                  {t('Users')}
                </Anchor>
                <Anchor href="/students" fw={1000} fz="h2">
                  {t('Students')}
                </Anchor>
                <Anchor href="/events" fw={1000} fz="h2">
                  {t('Events')}
                </Anchor>
                <Anchor href="/promotions" fw={1000} fz="h2">
                  {t('Promotions')}
                </Anchor>
                <Anchor href="/clients" fw={1000} fz="h2">
                  {t('Client List')}
                </Anchor>
                <Anchor href="/courses" fw={1000} fz="h2">
                  {t('Cancel Course')}
                </Anchor>
              </div>
            )}
          </div>
        ) : null}
      </div>
    </div>
  );
}

export default Navbar;
