import React, { useState } from "react";
import { useCourseService } from "@/services/courseService";
import { CourseStatus } from "@/models/Courses";

const CancelCoursePage: React.FC = () => {
    const { cancelCourse } = useCourseService();
    const [courseId, setCourseId] = useState<string>("");
    const [status, setStatus] = useState<CourseStatus>(CourseStatus.ACTIVE);
    const [message, setMessage] = useState<string>("");

    const handleCancelCourse = async () => {
        try {
            await cancelCourse(courseId, status);
            setMessage(`Course ${courseId} status updated to ${status}`);
        } catch (error) {
            setMessage(`Failed to update course status: ${error}`);
        }
    };

    return (
        <div>
            <h1>Cancel Course</h1>
            <input
                type="text"
                placeholder="Course ID"
                value={courseId}
                onChange={(e) => setCourseId(e.target.value)}
            />
            <select
                value={status}
                onChange={(e) => setStatus(e.target.value as CourseStatus)}
            >
                <option value={CourseStatus.ACTIVE}>Active</option>
                <option value={CourseStatus.CANCELLED}>Cancelled</option>
            </select>
            <button onClick={handleCancelCourse}>Update Status</button>
            {message && <p>{message}</p>}
        </div>
    );
};

export default CancelCoursePage;