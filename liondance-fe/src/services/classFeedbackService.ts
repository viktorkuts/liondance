import { useAxiosInstance } from "../utils/axiosInstance";
import { ClassFeedbackRequestModel, ClassFeedbackResponseModel } from "@/models/ClassFeedback";

export const useClassFeedbackService = () => {
  const axiosInstance = useAxiosInstance();

  const addFeedback = async (feedback: ClassFeedbackRequestModel): Promise<ClassFeedbackResponseModel> => {
    const response = await axiosInstance.post<ClassFeedbackResponseModel>("/classfeedback", feedback);
    return response.data;
  };

  return {
    addFeedback,
  };
};

export default useClassFeedbackService;
