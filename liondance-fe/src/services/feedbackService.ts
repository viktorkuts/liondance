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

  const submitFeedback = async (eventId: string, rating: number, comment: string) => {
    try {
        const response = await axiosInstance.post(`/events/${eventId}/feedback`, {
            rating,
            feedback: comment, // No need to send eventId in the body anymore
        });
        return response.data;
    } catch (error) {
        console.error("Error submitting feedback:", error);
        throw error;
    }
  };


  return {
    getFeedbacksByEventId,
    submitFeedback,
  };
};
