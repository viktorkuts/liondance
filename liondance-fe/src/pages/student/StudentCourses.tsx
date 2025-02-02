import React, { useEffect, useState } from "react";
import axios, { AxiosError } from "axios";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import { Modal, Text } from "@mantine/core";
import { useTranslation } from "react-i18next";
import "./StudentCourses.css";

interface Course {
  courseId: string;
  name: string;
  startTime: string;
  endTime: string;
  dayOfWeek: string;
  instructorFirstName: string;
  instructorMiddleName?: string;
  instructorLastName: string;
  cancelledDates: string[];
}

const StudentCourses: React.FC = () => {
  const { t } = useTranslation();
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [selectedCourses, setSelectedCourses] = useState<Course[]>([]);
  const [showModal, setShowModal] = useState<boolean>(false);

  const studentId = "a79b0c3c-2462-42a1-922d-1a20be857cba";

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        const response = await axios.get<Course[]>(
          `${import.meta.env.BACKEND_URL}/api/v1/students/${studentId}/courses`
        );
        setCourses(response.data);
        setLoading(false);
      } catch (err) {
        const axiosError = err as AxiosError;
        if (axiosError.response?.status === 404) {
          setError(t("You aren't in any courses."));
        } else {
          setError(t("Failed to load courses. Please try again later."));
        }
        setLoading(false);
      }
    };

    fetchCourses();
  }, [t]);

  const handleDayClick = (date: Date) => {
    const clickedCourses = courses.filter(
      (course) =>
        course.dayOfWeek ===
        date.toLocaleDateString("en-US", { weekday: "long" }).toUpperCase()
    );

    setSelectedCourses(clickedCourses);
    setSelectedDate(date);
    setShowModal(true);
  };

  const getTileClassName = ({ date }: { date: Date }) => {
    const hasCourse = courses.some(
      (course) =>
        course.dayOfWeek ===
        date.toLocaleDateString("en-US", { weekday: "long" }).toUpperCase()
    );
    return hasCourse ? "course-day" : "";
  };

  if (loading) return <Text style={{ textAlign: "center" }}>{t("Loading...")}</Text>;
  if (error)
    return <Text style={{ textAlign: "center", color: "red" }}>{error}</Text>;

  return (
    <div className="student-courses-container">
      {/* Course List Section */}
      <div className="course-list">
        <h1>{t("My Courses")}</h1>
        {courses.length === 0 ? (
          <p className="no-courses">{t("No courses assigned yet.")}</p>
        ) : (
          <table className="courses-table">
            <thead>
              <tr>
                <th>{t("Course Name")}</th>
                <th>{t("Day")}</th>
                <th>{t("Time")}</th>
                <th>{t("Instructor")}</th>
              </tr>
            </thead>
            <tbody>
              {courses.map((course) => (
                <tr key={course.courseId}>
                  <td>{t(course.name)}</td>
                  <td>{t(course.dayOfWeek)}</td>
                  <td>
                    {course.startTime} - {course.endTime}
                  </td>
                  <td>
                    {course.instructorFirstName}{" "}
                    {course.instructorMiddleName &&
                      course.instructorMiddleName + " "}
                    {course.instructorLastName}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Calendar Section */}
      <div className="calendar-container">
        <h2>{t("Course Calendar")}</h2>
        <Calendar
          onClickDay={handleDayClick}
          tileClassName={getTileClassName}
        />
      </div>

      {/* Modal for Selected Date */}
      <Modal
        opened={showModal}
        onClose={() => setShowModal(false)}
        title={`${t("Courses on")} ${selectedDate?.toLocaleDateString()}`}
      >
        {selectedCourses.map((course) => (
          <div key={course.courseId}>
            <p>
              <strong>{t(course.name)}</strong>
              <br />
              {t("Time")}: {course.startTime} - {course.endTime}
              <br />
              {t("Instructor")}: {course.instructorFirstName}{" "}
              {course.instructorMiddleName && course.instructorMiddleName + " "}
              {course.instructorLastName}
            </p>
            <hr />
          </div>
        ))}
      </Modal>
    </div>
  );
};

export default StudentCourses;
