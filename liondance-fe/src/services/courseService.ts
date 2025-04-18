import { useAxiosInstance } from "../utils/axiosInstance";

export const useCourseService = () => {
  const axiosInstance = useAxiosInstance();

  const getAllCourses = async () => {
    const response = await axiosInstance.get(`/courses`);
    return response.data;
  };

  const cancelCourse = async (
    courseId: string,
    cancelledDates: Date[]
  ): Promise<void> => {
    const response = await axiosInstance.patch(`/courses/${courseId}/dates`, {
      cancelledDates,
    });
    return response.data;
  };

  const getStudentCourses = async (studentId: string) => {
    const response = await axiosInstance.get(`/students/${studentId}/courses`);
    return response.data;
  };

  return {
    getAllCourses,
    cancelCourse,
    getStudentCourses,
  };
};
