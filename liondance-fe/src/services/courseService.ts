import { useAxiosInstance } from "../utils/axiosInstance";

export const useCourseService = () => {
  const axiosInstance = useAxiosInstance();

  const getAllCourses = async () => {
    const response = await axiosInstance.get(`/courses`);
    return response.data;
  }

  const cancelCourse = async (courseId: string, cancelledDates: string[]): Promise<void> => {
    const response = await axiosInstance.patch(`/courses/${courseId}/date`, { cancelledDates });
    return response.data;
  };

  return {
    getAllCourses,
    cancelCourse,
  };
};