import axiosInstance from "@/utils/axiosInstance";
import { Event, EventStatus } from "@/models/Event.ts";
import { AxiosResponse } from "axios";

const getAllEvents = async (): Promise<Event[]> => {
  const response = await axiosInstance.get<Event[]>("/events");
  return response.data;
};

const bookEvent = async (event: Event): Promise<AxiosResponse<Event>> => {
  return await axiosInstance.post<Event>("/events", event);
};

const updateEventStatus = async (
 eventId: string,
 status: EventStatus
): Promise<AxiosResponse<Event>> => {
  return await axiosInstance.put<Event>(`/events/${eventId}/status`, { status });
};

const getEventById = async (eventId: string): Promise<Event> => {
  const response = await axiosInstance.get<Event>(`/events/${eventId}`);
  return response.data;
};

export default {
  getAllEvents,
  bookEvent,
  updateEventStatus,
  getEventById
};