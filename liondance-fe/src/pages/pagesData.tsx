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
import ClientEventPage from "./event/clientEventPage";
import BookEvent from "./event/bookEvent";
import PromotionsList from "./promotions/promotionsList";
import PromotionDetails from "./promotions/promotionDetails";
import FeedbackList from "./feedback/feedbackList";
import UpcomingEvents from "./event/upcomingEvents";
import ClientList from "@/components/clientList";
import ClassFeedbackForm from "./feedback/ClassFeedbackForm";
import ClientRegistration from "./registration/client-registration";
import CancelCourse from "@/components/cancelCourse";
import ClientProfile from "@/components/clientProfile";
import GetMyEvents from "./event/clientEventPage";
import ClassFeedbackReportPage from "./feedback/ClassFeedbackReportPage";
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
    path: "add-new-user",
    element: <AddNewUser />,
    title: "Add Users",
  },
  {
    path: "events",
    element: <GetAllEvents />,
    title: "Events",
  },
  {
    path: "promotions",
    element: <PromotionsList />,
    title: "Promotions",
  },
  {
    path: "promotions/:promotionId",
    element: <PromotionDetails />,
    title: "Promotion Details",
  },
  {
    path: "filtered-events",
    element: <UpcomingEvents />,
    title: "Upcoming Events",
  },
  {
    path: "events",
    element: <ClientEventPage />,
    title: "Events",
  },
  {
    path: "my-events",
    element: <GetMyEvents />,
    title: "Events",
  },
  {
    path: "feedbacks/:eventId",
    element: <FeedbackList eventId={":eventId"} />,
    title: "Feedback List",
  },
  {
    path: "clients",
    element: <ClientList />,
    title: "Client List",
  },
  {
    path: "classfeedback/:date",
    element: <ClassFeedbackForm />,
    title: "Class Feedback Form",
  },
  {
    path: "courses",
    element: <CancelCourse />,
    title: "Courses",
  },
  {
    path: "client-registration",
    element: <ClientRegistration />,
    title: "Client Registration Form",
  },
  {
    path: "client-profile/:clientId",
    element: <ClientProfile />,
    title: "Client Profile",
  },

  {
    path: "classfeedback/reports",
    element: <ClassFeedbackReportPage />,
    title: "Class Feedback Reports",
  }

];

export default pagesData;
