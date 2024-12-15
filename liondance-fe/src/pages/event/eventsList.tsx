import React, { useEffect, useState } from "react";
import eventService from "@/services/eventService";
import { Event, EventStatus } from "@/models/Event.ts";
import "./eventsList.css";

function GetAllEvents() {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const [showModal, setShowModal] = useState<boolean>(false);
  const [selectedEvent, setSelectedEvent] = useState<Event | null>(null);
  const [newStatus, setNewStatus] = useState<string>("");

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const events = await eventService.getAllEvents();
        setEvents(events);
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
    console.log("Selected Event:", event);
    setSelectedEvent(event);
    setShowModal(true);
  };

  console.log("Current selectedEvent before status change:", selectedEvent);

  const handleStatusChange = async () => {
    console.log("Current selectedEvent before status change:", selectedEvent); // Debugging

    // Ensure the id field is being used
    if (!selectedEvent || !selectedEvent.id) {
      console.error("Selected event or id is undefined");
      setError("Unable to update status. No event selected.");
      return;
    }

    try {
      await eventService.updateEventStatus(selectedEvent.id, newStatus as EventStatus);
      setEvents((prevEvents) =>
       prevEvents.map((evt) =>
        evt.id === selectedEvent.id
         ? { ...evt, eventStatus: newStatus as EventStatus }
         : evt
       )
      );
      setShowModal(false);
    } catch (error) {
      console.error("Error updating event status:", error);
      setError("Failed to update event status. " + error);
    }
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;

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
             <button onClick={() => handleStatusClick(event)}>{event.eventStatus ?? "N/A"}</button>
           </td>
         </tr>
        ))}
        </tbody>
      </table>
     )}
     {showModal && (
      <div className="modal">
        <div className="modal-content">
          <h2>Change Event Status</h2>
          <select value={newStatus} onChange={(e) => setNewStatus(e.target.value)}>
            <option value="">Select Status</option>
            <option value="Pending">Pending</option>
            <option value="Confirmed">Confirmed</option>
            <option value="Cancelled">Cancelled</option>
          </select>
          <button onClick={handleStatusChange}>Save</button>
          <button
           onClick={() => {
             setShowModal(false);
             setNewStatus("");
           }}
          >
            Cancel
          </button>
        </div>
      </div>
     )}
   </div>
  );
}

export default GetAllEvents;