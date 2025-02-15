import { useEffect, useState } from "react";
import { useAxiosInstance } from "../../utils/axiosInstance.ts";
import { Event } from "@/models/Event.ts";
import { User } from "@/models/Users.ts";
import "./eventsList.css";
import UpdateEventStatus from "./updateEventStatus.tsx";
import RescheduleEvent from "./rescheduleEvent.tsx";
import UpdateEventDetails from "./updateEventDetails.tsx";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useUserService } from "@/services/userService.ts";
import { useEventService } from "@/services/eventService.ts";
import ContactClientModal from "./contactClientModal";

interface EventWithClient extends Event {
  client?: User;
}

type SortConfig = {
  key: string;
  direction: 'asc' | 'desc' | 'none';
};

interface ExpandableEventTableProps {
  title: string;
  events: EventWithClient[];
  sortConfig: SortConfig;
  handleSort: (key: string) => void;
  handleStatusClick: (event: EventWithClient) => void;
  handleRescheduleClick: (event: EventWithClient) => void;
  handleUpdateDetailsClick: (event: EventWithClient) => void;
  handleViewFeedback: (eventId: string) => void;
  handleRequestFeedback: (eventId: string) => void;
  handleContactClick: (event: EventWithClient) => void;
}

const ExpandableEventTable: React.FC<ExpandableEventTableProps> = ({
  title,
  events,
  sortConfig,
  handleSort,
  handleStatusClick,
  handleRescheduleClick,
  handleUpdateDetailsClick,
  handleViewFeedback,
  handleRequestFeedback,
  handleContactClick,
}) => {
  const { t } = useTranslation();
  const [isExpanded, setIsExpanded] = useState(false);

  if (events.length === 0) return null;

  return (
    <div className="expandable-table">
      <div 
        className="expandable-header" 
        onClick={() => setIsExpanded(!isExpanded)}
      >
        <h2>{title} ({events.length})</h2>
        <span>{isExpanded ? '▼' : '▶'}</span>
      </div>
      {isExpanded && (
        <table className="events-table">
          <thead>
            <tr>
              <th>#</th>
              <th onClick={() => handleSort('name')} className="sortable">
                {t("Name")} {sortConfig.key === 'name' && sortConfig.direction !== 'none' && 
                  (sortConfig.direction === 'asc' ? '↑' : '↓')}
              </th>
              <th onClick={() => handleSort('email')} className="sortable">
                {t("Email")} {sortConfig.key === 'email' && sortConfig.direction !== 'none' && 
                  (sortConfig.direction === 'asc' ? '↑' : '↓')}
              </th>
              <th onClick={() => handleSort('phone')} className="sortable">
                {t("Phone")} {sortConfig.key === 'phone' && sortConfig.direction !== 'none' && 
                  (sortConfig.direction === 'asc' ? '↑' : '↓')}
              </th>
              <th onClick={() => handleSort('location')} className="sortable">
                {t("Location")} {sortConfig.key === 'location' && sortConfig.direction !== 'none' && 
                  (sortConfig.direction === 'asc' ? '↑' : '↓')}
              </th>
              <th onClick={() => handleSort('date')} className="sortable">
                {t("Event Date & Time")} {sortConfig.key === 'date' && sortConfig.direction !== 'none' && 
                  (sortConfig.direction === 'asc' ? '↑' : '↓')}
              </th>
              <th onClick={() => handleSort('type')} className="sortable">
                {t("Event Type")} {sortConfig.key === 'type' && sortConfig.direction !== 'none' && 
                  (sortConfig.direction === 'asc' ? '↑' : '↓')}
              </th>
              <th onClick={() => handleSort('request')} className="sortable">
                {t("Special Request")} {sortConfig.key === 'request' && sortConfig.direction !== 'none' && 
                  (sortConfig.direction === 'asc' ? '↑' : '↓')}
              </th>
              <th onClick={() => handleSort('privacy')} className="sortable">
                {t("Event Privacy")} {sortConfig.key === 'privacy' && sortConfig.direction !== 'none' && 
                  (sortConfig.direction === 'asc' ? '↑' : '↓')}
              </th>
              <th onClick={() => handleSort('status')} className="sortable">
                {t("Event Status")} {sortConfig.key === 'status' && sortConfig.direction !== 'none' && 
                  (sortConfig.direction === 'asc' ? '↑' : '↓')}
              </th>
              <th>{t("Actions")}</th>
            </tr>
          </thead>
          <tbody>
            {events.map((event: EventWithClient, index: number) => (
              <tr key={event.eventId}>
                <td>{index + 1}</td>
                <td>{`${event.client?.firstName ?? t("N/A")} ${event.client?.lastName ?? ""}`}</td>
                <td>{event.client?.email ?? t("N/A")}</td>
                <td>{event.client?.phone ?? t("N/A")}</td>
                <td>
                  {event.venue?.streetAddress ?? "N/A"}
                  {event.venue?.city ? `, ${event.venue.city}` : ""}
                </td>
                <td>
                  {new Date(event.eventDateTime).toLocaleString("en-CA", {
                    day: "2-digit",
                    month: "2-digit",
                    year: "numeric",
                    hour: "2-digit",
                    minute: "2-digit",
                    second: "2-digit",
                  })}
                </td>
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
                  {event.eventStatus === 'COMPLETED' && (
                    <button
                      onClick={() =>
                        event.eventId && handleViewFeedback(event.eventId)
                      }
                      className="button_view_feedback"
                    >
                      {t("View Feedback")}
                    </button>
                  )}
                  <button onClick={() => handleUpdateDetailsClick(event)} className="button_view_feedback">
                    {t("Update Details")}
                  </button>
                  <button onClick={() => event.eventId && handleRequestFeedback(event.eventId)}>
                    {t("Request Feedback")}
                  </button>
                  <button onClick={() => handleContactClick(event)} className="button_contact_client">
                    {t("Contact Client")}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

function GetAllEvents() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [events, setEvents] = useState<EventWithClient[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const [selectedEvent, setSelectedEvent] = useState<EventWithClient | null>(null);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [showRescheduleModal, setShowRescheduleModal] = useState<boolean>(false);
  const [showUpdateDetailsModal, setShowUpdateDetailsModal] = useState<boolean>(false);
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [sortConfig, setSortConfig] = useState<SortConfig>({ key: '', direction: 'none' });
  const [showContactModal, setShowContactModal] = useState<boolean>(false);
  const [selectedClientToContact, setSelectedClientToContact] = useState<EventWithClient | null>(null);

  const axiosInstance = useAxiosInstance();
  const userService = useUserService();
  const eventService = useEventService();

  const handleSort = (key: string) => {
    setSortConfig((prevConfig) => ({
      key: prevConfig.key === key && prevConfig.direction === 'desc' ? '' : key,
      direction: 
        prevConfig.key !== key ? 'asc' :
        prevConfig.direction === 'asc' ? 'desc' :
        prevConfig.direction === 'desc' ? 'none' : 'asc'
    }));
  };

  const getSortedEvents = (events: EventWithClient[]) => {
    const sortedEvents = [...events];
    if (sortConfig.key && sortConfig.direction !== 'none') {
      sortedEvents.sort((a, b) => {
        let aValue: string | number;
        let bValue: string | number;
  
        switch (sortConfig.key) {
          case 'name':
            aValue = `${a.client?.firstName ?? ''} ${a.client?.lastName ?? ''}`.trim();
            bValue = `${b.client?.firstName ?? ''} ${b.client?.lastName ?? ''}`.trim();
            break;
          case 'email':
            aValue = a.client?.email ?? '';
            bValue = b.client?.email ?? '';
            break;
          case 'phone':
            aValue = a.client?.phone ?? '';
            bValue = a.client?.phone ?? '';
            break;
          case 'location':
            aValue = `${a.venue?.streetAddress ?? ''} ${a.venue?.city ?? ''}`.trim();
            bValue = `${b.venue?.streetAddress ?? ''} ${b.venue?.city ?? ''}`.trim();
            break;
          case 'date':
            aValue = new Date(a.eventDateTime).getTime();
            bValue = new Date(b.eventDateTime).getTime();
            break;
          case 'type':
            aValue = a.eventType;
            bValue = b.eventType;
            break;
          case 'request':
            aValue = a.specialRequest ?? '';
            bValue = b.specialRequest ?? '';
            break;
          case 'privacy':
            aValue = a.eventPrivacy;
            bValue = b.eventPrivacy;
            break;
          case 'status':
            aValue = a.eventStatus;
            bValue = b.eventStatus;
            break;
          default:
            return 0;
        }
  
        if (aValue < bValue) {
          return sortConfig.direction === 'asc' ? -1 : 1;
        }
        if (aValue > bValue) {
          return sortConfig.direction === 'asc' ? 1 : -1;
        }
        return 0;
      });
    }
    return sortedEvents;
  };

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const eventsResponse = await axiosInstance.get<Event[]>("/events");
        const clientsResponse = await userService.getAllClients();

        if (eventsResponse.data) {
          const eventsWithClients = eventsResponse.data.map((event: Event) => {
            const foundClient = clientsResponse.find((client: User) => 
              client.userId === event.clientId
            );
            return {
              ...event,
              client: foundClient
            };
          });
          setEvents(eventsWithClients);
        } else {
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
  }, [axiosInstance]);

  const handleStatusClick = (event: EventWithClient): void => {
    setSelectedEvent(event);
    setShowModal(true);
  };

  const handleRescheduleClick = (event: EventWithClient): void => {
    setSelectedEvent(event);
    setShowRescheduleModal(true);
  };

  const handleUpdateDetailsClick = (event: EventWithClient): void => {
    setSelectedEvent(event);
    setShowUpdateDetailsModal(true);
  };

  const handleModalClose = (): void => {
    setShowModal(false);
    setSelectedEvent(null);
  };

  const handleRescheduleModalClose = (): void => {
    setShowRescheduleModal(false);
    setSelectedEvent(null);
  };

  const handleUpdateDetailsModalClose = (): void => {
    setShowUpdateDetailsModal(false);
    setSelectedEvent(null);
  };

  const handleViewFeedback = (eventId: string): void => {
    navigate(`/feedbacks/${eventId}`);
  };

  const handleEventUpdate = (updatedEvent: Event): void => {
    setEvents((prevEvents) =>
      prevEvents.map((event) =>
        event.eventId === updatedEvent.eventId 
          ? { ...updatedEvent, client: event.client }
          : event
      )
    );
  };

  const handleReschedule = (updatedEvent: Event): void => {
    setEvents((prevEvents) =>
      prevEvents.map((event) =>
        event.eventId === updatedEvent.eventId 
          ? { ...updatedEvent, client: event.client }
          : event
      )
    );
  };

  const handleRequestFeedback = async (eventId: string) => {
    try {
      await eventService.handleRequestFeedback(eventId);
    } catch (error) {
      console.error("Error sending feedback request:", error);
    }
  };

  const handleContactClick = (event: EventWithClient): void => {
    setSelectedClientToContact(event);
    setShowContactModal(true);
  };

  const handleContactModalClose = (): void => {
    setShowContactModal(false);
    setSelectedClientToContact(null);
  };

  const filteredEvents = events.filter((event: EventWithClient) => {
    if (searchTerm.trim() === "") return true;
    
    const searchValue = searchTerm.toLowerCase();
    
    return (
      event.client?.firstName?.toLowerCase().includes(searchValue) ||
      event.client?.lastName?.toLowerCase().includes(searchValue) ||
      event.client?.email?.toLowerCase().includes(searchValue) ||
      event.client?.phone?.toLowerCase().includes(searchValue) ||
      event.venue?.streetAddress?.toLowerCase().includes(searchValue) ||
      event.venue?.city?.toLowerCase().includes(searchValue) ||
      event.venue?.state?.toLowerCase().includes(searchValue) ||
      event.venue?.zip?.toLowerCase().includes(searchValue) ||
      event.eventType.toLowerCase().includes(searchValue) ||
      event.paymentMethod.toLowerCase().includes(searchValue) ||
      event.specialRequest?.toLowerCase().includes(searchValue) ||
      event.eventStatus.toLowerCase().includes(searchValue) ||
      event.eventPrivacy.toLowerCase().includes(searchValue)
    );
  });

  const sortedEvents = getSortedEvents(filteredEvents);
  const allEvents = sortedEvents;
  const pendingEvents = sortedEvents.filter(event => event.eventStatus === 'PENDING');
  const confirmedEvents = sortedEvents.filter(event => event.eventStatus === 'CONFIRMED');
  const cancelledEvents = sortedEvents.filter(event => event.eventStatus === 'CANCELLED');
  const completedEvents = sortedEvents.filter(event => event.eventStatus === 'COMPLETED');

  if (loading) return <div className="loading">{t("Loading...")}</div>;
  if (error) return <div className="error">{t(error)}</div>;

  return (
    <div className="events-list">
      <h1>{t("Events")}</h1>

      <div className="search-container">
        <input
          type="text"
          placeholder={t("Search events...")}
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
        {searchTerm && (
          <button 
            className="clear-search"
            onClick={() => setSearchTerm("")}
            title={t("Clear search")}
          >
            ×
          </button>
        )}
      </div>

      {filteredEvents.length === 0 ? (
        <p className="no-data">
          {events.length === 0 ? t("No events found.") : t("No matching events found.")}
        </p>
      ) : (
        <div className="expandable-tables-container">
          <ExpandableEventTable
            title={t("All Events")}
            events={allEvents}
            sortConfig={sortConfig}
            handleSort={handleSort}
            handleStatusClick={handleStatusClick}
            handleRescheduleClick={handleRescheduleClick}
            handleUpdateDetailsClick={handleUpdateDetailsClick}
            handleViewFeedback={handleViewFeedback}
            handleRequestFeedback={handleRequestFeedback}
            handleContactClick={handleContactClick}
          />
          <ExpandableEventTable
            title={t("Pending Events")}
            events={pendingEvents}
            sortConfig={sortConfig}
            handleSort={handleSort}
            handleStatusClick={handleStatusClick}
            handleRescheduleClick={handleRescheduleClick}
            handleUpdateDetailsClick={handleUpdateDetailsClick}
            handleViewFeedback={handleViewFeedback}
            handleRequestFeedback={handleRequestFeedback}
            handleContactClick={handleContactClick}
          />
          <ExpandableEventTable
            title={t("Confirmed Events")}
            events={confirmedEvents}
            sortConfig={sortConfig}
            handleSort={handleSort}
            handleStatusClick={handleStatusClick}
            handleRescheduleClick={handleRescheduleClick}
            handleUpdateDetailsClick={handleUpdateDetailsClick}
            handleViewFeedback={handleViewFeedback}
            handleRequestFeedback={handleRequestFeedback}
            handleContactClick={handleContactClick}
          />
          <ExpandableEventTable
            title={t("Cancelled Events")}
            events={cancelledEvents}
            sortConfig={sortConfig}
            handleSort={handleSort}
            handleStatusClick={handleStatusClick}
            handleRescheduleClick={handleRescheduleClick}
            handleUpdateDetailsClick={handleUpdateDetailsClick}
            handleViewFeedback={handleViewFeedback}
            handleRequestFeedback={handleRequestFeedback}
            handleContactClick={handleContactClick}
          />
          <ExpandableEventTable
            title={t("Completed Events")}
            events={completedEvents}
            sortConfig={sortConfig}
            handleSort={handleSort}
            handleStatusClick={handleStatusClick}
            handleRescheduleClick={handleRescheduleClick}
            handleUpdateDetailsClick={handleUpdateDetailsClick}
            handleViewFeedback={handleViewFeedback}
            handleRequestFeedback={handleRequestFeedback}
            handleContactClick={handleContactClick}
          />
        </div>
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
      {selectedClientToContact && showContactModal && (
        <ContactClientModal
          clientEmail={selectedClientToContact.client?.email ?? ""}
          clientName={`${selectedClientToContact.client?.firstName ?? ""} ${selectedClientToContact.client?.lastName ?? ""}`}
          onClose={handleContactModalClose}
        />
      )}
    </div>
  );
}

export default GetAllEvents;