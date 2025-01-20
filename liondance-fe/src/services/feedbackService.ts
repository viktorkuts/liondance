import { useAxiosInstance } from "../utils/axiosInstance";

export const useFeedbackService = () => {
  const axiosInstance = useAxiosInstance();
  const getFeedbacksByEventId = async (eventId: string) => {
    try {
      const response = await axiosInstance.get(`/feedbacks/event/${eventId}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching feedback by event ID:", error);
      throw error;
    }
  };

  return {
    getFeedbacksByEventId,
  };
};
