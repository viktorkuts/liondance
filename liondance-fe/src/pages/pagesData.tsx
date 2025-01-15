import { routerType } from "../types/router.types";
import Home from "./home";
import Registration from "./registration";
import PendingRegistrations from "./registration/pending-registrations";
import UserList from "../components/userList";
import UserProfile from "../components/userProfile";
import StudentCourses from "./student/StudentCourses";
import AddNewUser from "./admin/AddNewUser";
import StudentList from "../components/studentList";
import StudentProfile from "../components/studentProfile";
import GetAllEvents from "./event/eventsList";

import BookEvent from "./event/bookEvent";
import PromotionsList from "./admin/promotionsList";
import PromotionDetails from "./admin/promotionDetails";
import FeedbackList from "./feedback/feedbackList";

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
    path: "booking",
    element: <BookEvent />,
    title: "Book Event",
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
    element: <StudentList />,
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
  {
    path: "student-profile/:studentId",
    element: <StudentProfile />,
    title: "Student Profile",
  },
  {
    path:"add-new-user",
    element: <AddNewUser />,
    title:"Add Users"
  },
  {
    path: "events",
    element: <GetAllEvents />,
    title: "Events",
  },
  {
    path: "promotions",
    element: <PromotionsList/>,
    title: "Promotions"
  },
  {
    path:"promotions/:promotionId",
    element: <PromotionDetails/>,
    title: "Promotion Details"
  },
  {
    path: "feedbacks/:eventId",
    element: <FeedbackList eventId={":eventId"} />,
    title: "Feedback List",
  }
];

export default pagesData;
