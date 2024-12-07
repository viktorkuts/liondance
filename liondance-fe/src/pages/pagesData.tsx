import { routerType } from "../types/router.types";
import Home from "./home";
import Registration from "./registration";
import PendingRegistrations from "./registration/pending-registrations";
import UserList from "../components/userList";
import UserProfile from "../components/userProfile";

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
  {
    path: "pending-registrations",
    element: <PendingRegistrations />,
    title: "Pending Registrations",
  },
  {
    path: "users",
    element: <UserList />,
    title: "User List",
  },
  {
    path: "profile/:userId",
    element: <UserProfile />,
    title: "User Profile",
  },
];

export default pagesData;
