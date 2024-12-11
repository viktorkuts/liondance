import { routerType } from "../types/router.types";
import Home from "./home";
import Registration from "./registration";
import PendingRegistrations from "./registration/pending-registrations";
import UserList from "../components/userList";
import UserProfile from "../components/userProfile";
import StudentCourses from "./student/StudentCourses";
import StudentList from "@/components/studentList.tsx";
const pagesData: routerType[] = [
  {
    path: "",
    element: <Home />,
    title: "home",
  },
  {
    path: "registration",
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
    path: "students",
    element: <StudentList/>,
    title: "Student List",
  },
  {
    path: "profile/:userId",
    element: <UserProfile />,
    title: "User Profile",
  },
  {
    path: "student-courses",
    element: <StudentCourses />,
    title: "Student Courses",
  },
];

export default pagesData;
