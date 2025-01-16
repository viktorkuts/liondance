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

const updateEventStatus = async (eventId: string, status: EventStatus): Promise<Event> => {
  const response = await axiosInstance.patch<Event>(`/events/${eventId}/status`, { status });
  return response.data;
};

const rescheduleEvent = async (eventId: string, eventDateTime: Date): Promise<Event> => {
  const response = await axiosInstance.patch<Event>(`/events/${eventId}/date`, { eventDateTime });
  return response.data;
};

const getEventById = async (eventId: string): Promise<Event> => {
  const response = await axiosInstance.get<Event>(`/events/${eventId}`);
  return response.data;
};

const updateEventDetails = async (eventId: string, event: Event): Promise<Event> => {
  const response = await axiosInstance.put<Event>(`/events/${eventId}`, event);

const getEventsByEmail = async (email: string): Promise<Event[]> => {
  const response = await axiosInstance.get<Event[]>(`/events/email/${email}`);

  return response.data;
}

const eventService = {
  getAllEvents,
  bookEvent,
  updateEventStatus,
  rescheduleEvent,
  getEventById,
  updateEventDetails,
  getEventsByEmail
};

export default eventService;