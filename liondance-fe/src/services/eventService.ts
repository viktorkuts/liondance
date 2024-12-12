import axiosInstance from "../utils/axiosInstance";
import { Event } from "@/models/Event.ts";
import { AxiosResponse } from "axios";

const getAllEvents = async () => {
  const response = await axiosInstance.get("/events");
  return response.data;
};

const bookEvent = async (
  event: Event
): Promise<AxiosResponse<Event>> => {
  return await axiosInstance.post<Event>("/events", event);
};

export default {
  getAllEvents,
    bookEvent,
};
