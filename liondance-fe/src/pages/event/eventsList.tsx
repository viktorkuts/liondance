import React, { useEffect, useState } from "react";
import axiosInstance from "../../utils/axiosInstance.ts";
import { Event } from "@/models/Event.ts";
import "./booking.module.css";

function GetAllEvents() {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const response = await axiosInstance.get<{ data: Event[] }>("/events");

        // Access the nested `data` property
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
              <th>Street Address</th>
              <th>City</th>
              <th>State</th>
              <th>Zip</th>
              <th>Event Date Time</th>
              <th>Event Type</th>
              <th>Payment Method</th>
              <th>Special Request</th>
              <th>Event Status</th>
            </tr>
          </thead>
          <tbody>
            {events.map((event) => (
              <tr key={event.id}>
                <td>{event.firstName} {event.middleName ?? ""}</td>
                <td>{event.email}</td>
                <td>{event.phone}</td>
                <td>{event.address?.streetAddress ?? "N/A"}</td>
                <td>{event.address?.city ?? "N/A"}</td>
                <td>{event.address?.state ?? "N/A"}</td>
                <td>{event.address?.zip ?? "N/A"}</td>
                <td>{new Date(event.eventDateTime).toLocaleString()}</td>
                <td>{event.eventType}</td>
                <td>{event.paymentMethod}</td>
                <td>{event.specialRequest ?? "N/A"}</td>
                <td>{event.eventStatus ?? "N/A"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default GetAllEvents;
