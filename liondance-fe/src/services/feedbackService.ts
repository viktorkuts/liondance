import { Visibility } from "@/models/Feedback";
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

  const getPublicFeedbacks = async () => {
    try {
      const response = await axiosInstance.get(`/feedbacks`);
      return response.data;
    } catch (error) {
      console.error("Error fetching public feedbacks:", error);
      throw error;
    }
  };

  const submitFeedback = async (
    eventId: string,
    rating: number,
    comment: string
  ) => {
    try {
      const response = await axiosInstance.post(`/events/${eventId}/feedback`, {
        rating,
        feedback: comment,
      });
      return response.data;
    } catch (error) {
      console.error("Error submitting feedback:", error);
      throw error;
    }
  };

  const updateFeedbackStatus = async (
    feedbackId: string,
    status: Visibility
  ) => {
    try {
      const response = await axiosInstance.patch(
        `/feedbacks/${feedbackId}`,
        status
      );
      return response.data;
    } catch (error) {
      console.error("Error fetching feedback by feedback ID:", error);
      throw error;
    }
  };

  return {
    getFeedbacksByEventId,
    submitFeedback,
    updateFeedbackStatus,
    getPublicFeedbacks,
  };
};
