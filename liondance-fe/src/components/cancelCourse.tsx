import React, { useEffect, useState } from "react";
import { Modal, Button, Text } from "@mantine/core";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import { useCourseService } from "@/services/courseService";
import dayjs from "dayjs";
import "./cancelCourse.css";

interface Course {
    courseId: string;
    name: string;
    dayOfWeek: string;
    startTime: string;
    endTime: string;
    instructorFirstName: string;
    instructorMiddleName?: string;
    instructorLastName: string;
    cancelledDates: Date[];
}

const CancelCourse: React.FC = () => {
    const { getAllCourses, cancelCourse } = useCourseService();
    const [courses, setCourses] = useState<Course[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [selectedDate, setSelectedDate] = useState<Date | null>(null);
    const [showModal, setShowModal] = useState<boolean>(false);

    useEffect(() => {
        const fetchCourses = async () => {
            try {
                const data = await getAllCourses();
                setCourses(data);
                setLoading(false);
            } catch {
                setError("Failed to load courses. Please try again later.");
                setLoading(false);
            }
        };

        fetchCourses();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleCancelCourse = async (courseId: string, date: Date) => {
        try {
            const cancelledDate = dayjs(date).toISOString();
            await cancelCourse(courseId, [new Date(cancelledDate)]);
            setCourses((prevCourses) =>
                prevCourses.map((course) =>
                    course.courseId === courseId
                        ? {
                                ...course,
                                cancelledDates: [...course.cancelledDates, new Date(cancelledDate)],
                            }
                        : course
                )
            );
            setShowModal(false);
        } catch {
            setError("Failed to cancel course. Please try again later.");
        }
    };

    const handleDayClick = (date: Date) => {
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

    const tileDisabled = ({ date }: { date: Date }) => {
        return date.getDay() !== 0; // Disable all days except Sundays (0 is Sunday)
    };

    if (loading) return <Text style={{ textAlign: "center" }}>Loading...</Text>;
    if (error)
        return <Text style={{ textAlign: "center", color: "red" }}>{error}</Text>;

    return (
        <div className="cancel-course-container">
            <div className="course-list">
                <h1>Upcoming Courses</h1>
                {courses.length === 0 ? (
                    <p className="no-courses">No courses available.</p>
                ) : (
                    <table className="courses-table">
                        <thead>
                            <tr>
                                <th>Course Name</th>
                                <th>Day</th>
                                <th>Time</th>
                                <th>Instructor</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {courses.map((course) => (
                                <tr key={course.courseId}>
                                    <td>{course.name}</td>
                                    <td>{course.dayOfWeek}</td>
                                    <td>
                                        10:00 AM - 12:00 AM
                                    </td>
                                    <td>
                                        {course.instructorFirstName}{" "}
                                        {course.instructorMiddleName &&
                                            course.instructorMiddleName + " "}
                                        {course.instructorLastName}
                                    </td>
                                    <td>
                                        <Button
                                            onClick={() => handleDayClick(new Date(course.startTime))}
                                        >
                                            Cancel
                                        </Button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>

            <div className="calendar-container">
                <h2>Course Calendar</h2>
                <Calendar
                    onClickDay={handleDayClick}
                    tileClassName={getTileClassName}
                    tileDisabled={tileDisabled}
                />
            </div>

            <Modal
                opened={showModal}
                onClose={() => setShowModal(false)}
                title={`Cancel Course on ${selectedDate ? dayjs(selectedDate).format("MMMM D, YYYY") : ""}`}
            >
                <div>
                    <p>Are you sure you want to cancel the course on this date?</p>
                    <Button
                        onClick={() => {
                            const course = courses.find(
                                (course) =>
                                    course.dayOfWeek ===
                                    selectedDate
                                        ?.toLocaleDateString("en-US", { weekday: "long" })
                                        .toUpperCase()
                            );
                            if (course && selectedDate) {
                                handleCancelCourse(course.courseId, selectedDate);
                            }
                            setShowModal(false);
                        }}
                    >
                        Confirm
                    </Button>
                </div>
            </Modal>
        </div>
    );
};

export default CancelCourse;