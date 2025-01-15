import axiosInstance from "../utils/axiosInstance";

const getFeedbacksByEventId = async (eventId: string) => {
  try {
    const response = await axiosInstance.get(`/feedbacks/event/${eventId}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching feedback by event ID:", error);
    throw error;
  }
};

export default {
  getFeedbacksByEventId,
};