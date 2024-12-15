import React, { useEffect, useState } from "react";
import axiosInstance from "../../utils/axiosInstance.ts";
import { Event } from "@/models/Event.ts";
import "./eventsList.css";
import UpdateEventStatus from "./updateEventStatus.tsx";

function GetAllEvents() {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const [selectedEvent, setSelectedEvent] = useState<Event | null>(null);
  const [showModal, setShowModal] = useState<boolean>(false);

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const response = await axiosInstance.get<{ data: Event[] }>("/events");
        if (response.data && Array.isArray(response.data)) {
          setEvents(response.data);
        } else {
          console.error("Unexpected response structure:", response);
          setError("Failed to fetch events: Unexpected response format.");
        }
        setLoading(false);
      } catch (error) {
        console.error("Error fetching events:", error);
        setError("Failed to fetch events. " + error);
        setLoading(false);
      }
    };
    fetchEvents();
  }, []);

  const handleStatusClick = (event: Event) => {
    setSelectedEvent(event);
    setShowModal(true);
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;

  const handleModalClose = () => {
    setShowModal(false);
    setSelectedEvent(null);
  };

  const handleEventUpdate = (updatedEvent: Event) => {
    setEvents((prevEvents) =>
     prevEvents.map((event) => (event.id === updatedEvent.id ? updatedEvent : event))
    );
  };
  return (
   <div className="events-list">
     <h1>All Events</h1>
     {events.length === 0 ? (
      <p className="no-data">No events found.</p>
     ) : (
      <table className="events-table">
        <thead>
        <tr>
          <th>Name</th>
          <th>Email</th>
          <th>Phone</th>
          <th>Location</th>
          <th>Event Date & Time</th>
          <th>Event Type</th>
          <th>Special Request</th>
          <th>Event Status</th>
        </tr>
        </thead>
        <tbody>
        {events.map((event) => (
         <tr key={event.id}>
           <td>{event.firstName} {event.lastName}</td>
           <td>{event.email}</td>
           <td>{event.phone}</td>
           <td>
             {event.address?.streetAddress ?? "N/A"}
             {event.address?.city ? `, ${event.address.city}` : ""}
           </td>
           <td>{new Date(event.eventDateTime).toLocaleString()}</td>
           <td>{event.eventType}</td>
           <td>
             {event.specialRequest && event.specialRequest.trim()
              ? event.specialRequest
              : "No special requests"}
           </td>
           <td>
             <button onClick={() => handleStatusClick(event)}>
               {event.eventStatus ?? "N/A"}
             </button>
           </td>
         </tr>
        ))}
        </tbody>
      </table>
     )}
     {selectedEvent && showModal && (
      <UpdateEventStatus
       event={selectedEvent}
       onClose={handleModalClose}
       onUpdate={handleEventUpdate}
      />
     )}
   </div>
  );

}

export default GetAllEvents;