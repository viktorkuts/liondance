import React, { useState, useEffect } from "react";
import { Modal, Button, Table, Text } from "@mantine/core";
import { Event } from "@/models/Event";
import { useTranslation } from "react-i18next";
import { Student, User } from "@/models/Users";
import { useUserService } from "@/services/userService";
import { useEventService } from "@/services/eventService"; 
import AddPerformersModal from "./addPerformersModal";
import RemovePerformersModal from "./removePerformersModal";
import "./eventDetailsModal.css";

interface EventDetailsModalProps {
  event: Event;
  onClose: () => void;
  onAddPerformers: (updatedEvent: Event) => void;
  onRemovePerformers: (updatedEvent: Event) => void;
}

const EventDetailsModal: React.FC<EventDetailsModalProps> = ({
  event,
  onClose,
  onAddPerformers,
  onRemovePerformers,
}) => {
  const { t } = useTranslation();
  const [currentPerformers, setCurrentPerformers] = useState<Student[]>([]);
  const [clientInfo, setClientInfo] = useState<User | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showRemoveModal, setShowRemoveModal] = useState(false);
  const userService = useUserService();
  const eventService = useEventService(); 

  const fetchClientInfo = async () => {
    try {
      if (!event.clientId) {
        setError(t("Client ID is missing."));
        return;
      }
      const clientsResponse = await userService.getAllClients();
      const foundClient = clientsResponse.find((client: User) => 
        client.userId === event.clientId
      );
      if (foundClient) {
        setClientInfo(foundClient);
      } else {
        setError(t("Client not found."));
      }
    } catch {
      setError(t("Failed to fetch client information."));
    }
  };

  const fetchPerformers = async () => {
    try {
      if (!event.eventId) {
        setError(t("Event ID is missing."));
        return;
      }
      const performerIds = await eventService.getEventPerformers(event.eventId);
      const studentsData = await userService.getAllStudents();
      const performers = studentsData.filter((student: Student) =>
        performerIds.includes(student.userId!)
      );
      setCurrentPerformers(performers);
    } catch {
      setError(t("Failed to fetch performers."));
    }
  };

  useEffect(() => {
    fetchClientInfo();
    fetchPerformers();
  }, [event.clientId, event.performers]);

  return (
    <>
      <Modal
        opened={true}
        onClose={onClose}
        title={t("Event Details")}
        size="lg"
        className="events-modal"
      >
        <div>
          <Text size="lg" fw={500} mb="md">
            {t("Client Information")}
          </Text>
          <Table className="events-table">
            <tbody>
              <tr>
                <td>{t("Name")}</td>
                <td>{`${clientInfo?.firstName ?? t("N/A")} ${clientInfo?.lastName ?? ""}`}</td>
              </tr>
              <tr>
                <td>{t("Email")}</td>
                <td>{clientInfo?.email ?? t("N/A")}</td>
              </tr>
              <tr>
                <td>{t("Phone")}</td>
                <td>{clientInfo?.phone ?? t("N/A")}</td>
              </tr>
            </tbody>
          </Table>

          <Text size="lg" fw={500} mt="md" mb="md">
            {t("Event Information")}
          </Text>
          <Table className="events-table">
            <tbody>
              <tr>
                <td>{t("Event Type")}</td>
                <td>{event.eventType}</td>
              </tr>
              <tr>
                <td>{t("Date & Time")}</td>
                <td>
                  {new Date(event.eventDateTime).toLocaleString()}
                </td>
              </tr>
              <tr>
                <td>{t("Status")}</td>
                <td>{event.eventStatus}</td>
              </tr>
              <tr>
                <td>{t("Privacy")}</td>
                <td>{event.eventPrivacy}</td>
              </tr>
              <tr>
                <td>{t("Special Request")}</td>
                <td>{event.specialRequest || t("None")}</td>
              </tr>
            </tbody>
          </Table>

          <Text size="lg" fw={500} mt="md" mb="md">
            {t("Venue Information")}
          </Text>
          <Table className="events-table">
            <tbody>
              <tr>
                <td>{t("Address")}</td>
                <td>{event.venue?.streetAddress}</td>
              </tr>
              <tr>
                <td>{t("City")}</td>
                <td>{event.venue?.city}</td>
              </tr>
            </tbody>
          </Table>

          <Text size="lg" fw={500} mt="md" mb="md">
            {t("Performers")} ({currentPerformers.length})
          </Text>
          {error ? (
            <Text className="error">{error}</Text>
          ) : (
            <Table className="events-table">
              <tbody>
                {currentPerformers.map((performer) => (
                  <tr key={performer.userId}>
                    <td>{`${performer.firstName} ${performer.lastName}`}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}

          <div style={{ display: "flex", gap: "1rem", marginTop: "1rem" }}>
            <Button onClick={() => setShowAddModal(true)}>
              {t("Add Performers")}
            </Button>
            <Button onClick={() => setShowRemoveModal(true)} color="red">
              {t("Remove Performers")}
            </Button>
          </div>
        </div>
      </Modal>

      {showAddModal && (
        <AddPerformersModal
          event={event}
          onClose={() => setShowAddModal(false)}
          onAddPerformers={(updatedEvent) => {
            onAddPerformers(updatedEvent);
            fetchPerformers(); 
          }}
        />
      )}

      {showRemoveModal && (
        <RemovePerformersModal
          event={event}
          onClose={() => setShowRemoveModal(false)}
          onRemovePerformers={(updatedEvent) => {
            onRemovePerformers(updatedEvent);
            fetchPerformers(); 
          }}
        />
      )}
    </>
  );
};

export default EventDetailsModal;