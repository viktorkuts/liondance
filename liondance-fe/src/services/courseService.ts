import { useAxiosInstance } from "../utils/axiosInstance";
import { CourseStatus } from "@/models/Courses.ts";

export const useCourseService = () => {
  const axiosInstance = useAxiosInstance();
  const cancelCourse = async (courseId: string, status: CourseStatus): Promise<Event> => {
    const response = await axiosInstance.patch<Event>(`/courses/${courseId}/status`, { status });
    return response.data;
  };

  return {
    cancelCourse
  };
};