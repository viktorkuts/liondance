import { useAxiosInstance } from "../utils/axiosInstance";
import { ClassFeedbackRequestModel, ClassFeedbackResponseModel, ClassFeedbackReportResponseModel } from "@/models/ClassFeedback";

export const useClassFeedbackService = () => {
  const axiosInstance = useAxiosInstance();

  const addFeedback = async (feedback: ClassFeedbackRequestModel): Promise<ClassFeedbackResponseModel> => {
    const response = await axiosInstance.post<ClassFeedbackResponseModel>("/classfeedback", feedback);
    return response.data;
  };

  const getAllReports = async (): Promise<ClassFeedbackReportResponseModel[]> => {
    const response = await axiosInstance.get<ClassFeedbackReportResponseModel[]>("/classfeedback/reports");
    return response.data;
  };

  const downloadReport = async (reportId: string): Promise<Blob> => {
    const response = await axiosInstance.get(`/classfeedback/reports/${reportId}/download`, {
      responseType: "blob",
    });
    return response.data;
  };


  return {
    addFeedback,
    getAllReports,
    downloadReport,
  };
};

export default useClassFeedbackService;
