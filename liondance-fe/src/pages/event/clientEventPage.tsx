import React, { useEffect, useState } from "react";
import { useEventService } from "@/services/eventService";
import { Event } from "@/models/Event.ts";
import "./eventsList.css";

const GetEventsByEmail: React.FC<{ email: string }> = ({ email }) => {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const eventService = useEventService();

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const response = (await eventService.getEventsByEmail(email)) as
          | Event[]
          | { error: string };
        if (response && Array.isArray(response)) {
          setEvents(response);
        } else if (response?.error === "notfound") {
          setError("You have No Events");
        } else {
          console.error("Unexpected response structure:", response);
          setError("Failed to fetch events: Unexpected response format.");
        }
      } catch (err: unknown) {
        console.error("Error fetching events:", err);
        setError(
          "Failed to fetch events. " +
            (err instanceof Error ? err.message : String(err))
        );
      } finally {
        setLoading(false);
      }
    };

    fetchEvents();
  }, [email, eventService]);

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="events-list">
      <h1>Your Events</h1>
      {events.length === 0 ? (
        <p className="no-data">No events found.</p>
      ) : (
        <table className="events-table">
          <thead>
            <tr>
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
                <td>{event.eventStatus ?? "N/A"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default GetEventsByEmail;
