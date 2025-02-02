import { useEffect, useState } from "react";
import { useAxiosInstance } from "../../utils/axiosInstance.ts";
import { Event } from "@/models/Event.ts";
import "./eventsList.css";
import UpdateEventStatus from "./updateEventStatus.tsx";
import RescheduleEvent from "./rescheduleEvent.tsx";
import UpdateEventDetails from "./updateEventDetails.tsx";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";

function GetAllEvents() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const [selectedEvent, setSelectedEvent] = useState<Event | null>(null);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [showRescheduleModal, setShowRescheduleModal] = useState<boolean>(false);
  const [showUpdateDetailsModal, setShowUpdateDetailsModal] = useState<boolean>(false);

  const axiosInstance = useAxiosInstance();

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
  }, [axiosInstance]); //wathc out for this

  const handleStatusClick = (event: Event) => {
    setSelectedEvent(event);
    setShowModal(true);
  };

  const handleRescheduleClick = (event: Event) => {
    setSelectedEvent(event);
    setShowRescheduleModal(true);
  };

  const handleUpdateDetailsClick = (event: Event) => {
    setSelectedEvent(event);
    setShowUpdateDetailsModal(true);
  };

  if (loading) return <div className="loading">{t("Loading...")}</div>;
  if (error) return <div className="error">{t(error)}</div>;

  const handleModalClose = () => {
    setShowModal(false);
    setSelectedEvent(null);
  };

  const handleRescheduleModalClose = () => {
    setShowRescheduleModal(false);
    setSelectedEvent(null);
  };

  const handleUpdateDetailsModalClose = () => {
    setShowUpdateDetailsModal(false);
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
      <h1>{t("All Events")}</h1>
      {events.length === 0 ? (
        <p className="no-data">{t("No events found.")}</p>
      ) : (
        <table className="events-table">
          <thead>
            <tr>
              <th>#</th>
              <th>{t("Name")}</th>
              <th>{t("Email")}</th>
              <th>{t("Phone")}</th>
              <th>{t("Location")}</th>
              <th>{t("Event Date & Time")}</th>
              <th>{t("Event Type")}</th>
              <th>{t("Special Request")}</th>
              <th>{t("Event Privacy")}</th>
              <th>{t("Event Status")}</th>
              <th>{t("Actions")}</th>
            </tr>
          </thead>
          <tbody>
            {events.map((event, index) => (
              <tr key={event.id}>
                <td>{index + 1}</td>
                <td>{event.firstName} {event.lastName}</td>
                <td>{event.email}</td>
                <td>{event.phone}</td>
                <td>
                  {event.address?.streetAddress ?? t("N/A")}
                  {event.address?.city ? `, ${event.address.city}` : ""}
                </td>
                <td>{new Date(event.eventDateTime).toLocaleString("en-CA", { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit' })}</td>
                <td>{t(event.eventType)}</td>
                <td>
                  {event.specialRequest && event.specialRequest.trim()
                  ? event.specialRequest
                  : t("No special requests")}
                </td>
                <td>{t(event.eventPrivacy)}</td>
                <td>
                  <button onClick={() => handleStatusClick(event)}>
                  {t(event.eventStatus) ?? t("N/A")}
                  </button>
                </td>
                <td>
                  <button onClick={() => handleRescheduleClick(event)}>
                    {t("Reschedule")}
                  </button>
                  <button onClick={() => event.id && handleViewFeedback(event.id)} className="button_view_feedback">
                    {t("View Feedback")}
                  </button>
                  <button onClick={() => handleUpdateDetailsClick(event)} className="button_view_feedback">
                    {t("Update Details")}
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
      {selectedEvent && showUpdateDetailsModal && (
        <UpdateEventDetails
          event={selectedEvent}
          onClose={handleUpdateDetailsModalClose}
          onUpdate={handleEventUpdate}
        />
      )}
    </div>
  );
}

export default GetAllEvents;