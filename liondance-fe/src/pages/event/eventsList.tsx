import { useEffect, useState } from "react";
import axiosInstance from "../../utils/axiosInstance.ts";
import { Event } from "@/models/Event.ts";
import "./eventsList.css";
import UpdateEventStatus from "./updateEventStatus.tsx";
import RescheduleEvent from "./rescheduleEvent.tsx";
import { useNavigate } from "react-router-dom";

function GetAllEvents() {
  const navigate = useNavigate();
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const [selectedEvent, setSelectedEvent] = useState<Event | null>(null);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [showRescheduleModal, setShowRescheduleModal] = useState<boolean>(false);

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

  const handleRescheduleClick = (event: Event) => {
    setSelectedEvent(event);
    setShowRescheduleModal(true);
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;

  const handleModalClose = () => {
    setShowModal(false);
    setSelectedEvent(null);
  };

  const handleRescheduleModalClose = () => {
    setShowRescheduleModal(false);
    setSelectedEvent(null);
  };

  const handleViewFeedback = (eventId: string) => {
    navigate(`/feedbacks/${eventId}`);
  };

  const handleEventUpdate = (updatedEvent: Event) => {
    setEvents((prevEvents) =>
     prevEvents.map((event) => (event.id === updatedEvent.id ? updatedEvent : event))
    );
  };
  const handleReschedule = (updatedEvent: Event) => {
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
          <th>Actions</th>
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
               <td>{new Date(event.eventDateTime).toLocaleString("en-CA", { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit' })}</td>
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
           <td>
             <button onClick={() => handleRescheduleClick(event)}>
               Reschedule
             </button>
             <button onClick={() => event.id && handleViewFeedback(event.id)} className="button_view_feedback">
              View Feedback
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
     {selectedEvent && showRescheduleModal && (
      <RescheduleEvent
       event={selectedEvent}
       onClose={handleRescheduleModalClose}
       onReschedule={handleReschedule}
      />
     )}
   </div>
  );

}

export default GetAllEvents;