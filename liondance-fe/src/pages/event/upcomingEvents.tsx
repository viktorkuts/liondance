import { useEffect, useState } from "react";
import { useAxiosInstance } from "../../utils/axiosInstance";
import { UpcomingEvent } from "@/models/UpcomingEvent.ts";
import { useTranslation } from "react-i18next";
import "./upcomingEvents.css";

function UpcomingEvents() {
  const { t } = useTranslation();
  const [events, setEvents] = useState<UpcomingEvent[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const axiosInstance = useAxiosInstance();
  
  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const response = await axiosInstance.get("/events/filtered-events");
        console.log("Fetched events:", response.data); 
        if (Array.isArray(response.data)) {
          setEvents(response.data);
        } else {
          console.error("Expected an array of events, received:", response.data);
          setError(t("Failed to load events: Data format incorrect"));
        }
        setLoading(false);
      } catch (error) {
        const err = error as Error; 
        console.error("Error fetching events:", err);
        setError(t(`Failed to fetch events: ${err.message}`));
        setLoading(false);
      }
    };
    fetchEvents();
  }, [axiosInstance, t]);

  if (loading) return <div className="loading">{t("Loading...")}</div>;
  if (error) return <div className="error">{error}</div>;

  const eventTypeImages = {
    WEDDING: "/src/assets/Images/wedding.jpg",
    PARADE: "/src/assets/Images/parade.jpg",
    FESTIVAL: "/src/assets/Images/festival.jpg",
    BIRTHDAY: "/src/assets/Images/birthday.jpg",
    OTHER: "/src/assets/Images/other.jpg"
  };

  return (
    <div className="events-container">
      {events.length > 0 ? events.map((event, index) => (
        <div key={event.eventId || `event-${index}`} className="event-card">
          <div className="event-info"> 
            <div className="event-header">{t("Event Type")}: {t(event.eventType)}</div>
            <div className="event-details">
              <div className="event-detail">{t("Date")}: {new Date(event.eventDateTime).toLocaleString()}</div>
              <div className="event-detail event-privacy">{t("Privacy")}: {t(event.eventPrivacy)}</div>
              {event.eventPrivacy === "PUBLIC" && (
                <div className="event-detail">
                  {t("Address")}: {event.eventAddress.streetAddress}, {event.eventAddress.city}, {event.eventAddress.state} {event.eventAddress.zip}
                </div>
              )}
            </div>
          </div>
          <div className="event-image">
            <img 
              src={eventTypeImages[event.eventType] || eventTypeImages.OTHER}
              alt={`${event.eventType} event`}
              className="event-type-image"
            />
          </div>
          <div className="event-info">
            <div className="event-header">{t("Event Type")}: {t(event.eventType)}</div>
            <div className="event-details">
              <div className="event-detail">{t("Date")}: {new Date(event.eventDateTime).toLocaleString()}</div>
              <div className="event-detail event-privacy">{t("Privacy")}: {t(event.eventPrivacy)}</div>
              {event.eventPrivacy === "PUBLIC" && (
                <div className="event-detail">
                  {t("Address")}: {event.eventAddress.streetAddress}, {event.eventAddress.city}, {event.eventAddress.state} {event.eventAddress.zip}
                </div>
              )}
            </div>
          </div>
        </div>
      )) : (
        <div className="events-list">{t("No events found.")}</div>
      )}
    </div>
  );
}

export default UpcomingEvents;
