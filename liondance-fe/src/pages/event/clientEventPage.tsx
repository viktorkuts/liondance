import React, { useEffect, useState } from "react";
import { useEventService } from "@/services/eventService";
import { Event } from "@/models/Event.ts";
import { useTranslation } from "react-i18next";
import "./eventsList.css";
import { useNavigate } from "react-router-dom";

const GetMyEvents: React.FC = () => {
  const { t } = useTranslation();
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const eventService = useEventService();
  const navigate = useNavigate(); 

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const response = (await eventService.getSelfEvents()) as
          | Event[]
          | { error: string };
        if (response && Array.isArray(response)) {
          setEvents(response);
        } else if (response?.error === "notfound") {
          setError(t("You have No Events"));
        } else {
          console.error("Unexpected response structure:", response);
          setError(t("Failed to fetch events: Unexpected response format."));
        }
      } catch (err: unknown) {
        console.error("Error fetching events:", err);
        setError(
          t("Failed to fetch events. ") +
            (err instanceof Error ? err.message : String(err))
        );
      } finally {
        setLoading(false);
      }
    };

    fetchEvents();
  }, [eventService, t]);

  if (loading) return <div className="loading">{t("Loading...")}</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="events-list">
      <h1>{t("Your Events")}</h1>
      {events.length === 0 ? (
        <p className="no-data">{t("No events found.")}</p>
      ) : (
        <table className="events-table">
          <thead>
            <tr>
              <th>{t("Location")}</th>
              <th>{t("Event Date & Time")}</th>
              <th>{t("Event Type")}</th>
              <th>{t("Special Request")}</th>
              <th>{t("Event Status")}</th>
              <th>{t("Actions")}</th>
            </tr>
          </thead>
          <tbody>
            {events.map((event) => (
              <tr key={event.eventId}>
                <td>
                  {event.venue?.streetAddress ?? t("N/A")}
                  {event.venue?.city ? `, ${event.venue.city}` : ""}
                </td>
                <td>{new Date(event.eventDateTime).toLocaleString()}</td>
                <td>{t(event.eventType)}</td>
                <td>
                  {event.specialRequest && event.specialRequest.trim()
                    ? event.specialRequest
                    : t("No special requests")}
                </td>
                <td>{t(event.eventStatus) ?? t("N/A")}</td>
                <td>
                  {event.eventStatus === "COMPLETED" && (
                    <button
                      className="feedback-button"
                      onClick={() => navigate(`/feedback-form/${event.eventId}`)}
                    >
                      {t("Give Feedback")}
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default GetMyEvents;