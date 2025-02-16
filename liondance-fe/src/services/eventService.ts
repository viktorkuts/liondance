import { useAxiosInstance } from "../utils/axiosInstance";
import { Event, EventStatus } from "@/models/Event.ts";
import { AxiosResponse } from "axios";

export const useEventService = () => {
  const axiosInstance = useAxiosInstance();
  const getAllEvents = async () => {
    const response = await axiosInstance.get("/events");
    return response.data;
  };

  const bookEvent = async (event: Event): Promise<AxiosResponse<Event>> => {
    return await axiosInstance.post<Event>("/events", event);
  };

  const updateEventStatus = async (
    eventId: string,
    status: EventStatus
  ): Promise<Event> => {
    const response = await axiosInstance.patch<Event>(
      `/events/${eventId}/status`,
      { status }
    );
    return response.data;
  };

  const rescheduleEvent = async (
    eventId: string,
    eventDateTime: Date
  ): Promise<Event> => {
    const response = await axiosInstance.patch<Event>(
      `/events/${eventId}/date`,
      { eventDateTime }
    );
    return response.data;
  };

  const getEventById = async (eventId: string): Promise<Event> => {
    const response = await axiosInstance.get<Event>(`/events/${eventId}`);
    return response.data;
  };

  const getSelfEvents = async (): Promise<Event[]> => {
    const response = await axiosInstance.get<Event[]>(
      `/clients/current-client/events`
    );
    return response.data;
  };

  const fetchPublicEvents = async () => {
    const response = await axiosInstance.get("/events/filtered-events");
    return response.data;
  };

  const updateEventDetails = async (
    eventId: string,
    event: Event
  ): Promise<Event> => {
    const response = await axiosInstance.put<Event>(
      `/events/${eventId}`,
      event
    );
    return response.data;
  };

  const handleRequestFeedback = async (eventId: string) => {
    try {
      await axiosInstance.post(`/events/${eventId}/request-feedback`);
      alert("Feedback request sent successfully.");
    } catch (error) {
      console.error("Error sending feedback request:", error);
      alert("Failed to send feedback request.");
    }
  };

  const assignPerformers = async (
    eventId: string,
    performers: string[]
  ): Promise<Event> => {
    const response = await axiosInstance.patch<Event>(
      `/events/${eventId}/assign-performers`,
      { performers }
    );
    return response.data;
  }

  const removePerformers = async (
    eventId: string,
    performers: string[]
  ): Promise<Event> => {
    const response = await axiosInstance.patch<Event>(
      `/events/${eventId}/remove-performers`,
      { performers }
    );
    return response.data;
  }


  return {
    getAllEvents,
    bookEvent,
    updateEventStatus,
    rescheduleEvent,
    getEventById,
    getSelfEvents,
    fetchPublicEvents,
    updateEventDetails,
    handleRequestFeedback,
    assignPerformers,
    removePerformers,
  };
};
