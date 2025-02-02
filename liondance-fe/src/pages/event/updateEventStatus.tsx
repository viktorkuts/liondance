import React, { useState } from "react";
import { Modal, Button, Select } from "@mantine/core";
import { useAxiosInstance } from "@/utils/axiosInstance";
import { Event, EventStatus } from "@/models/Event";
import "@/components/studentProfile.css";
import { useTranslation } from "react-i18next";

interface UpdateEventStatusProps {
  event: Event;
  onClose: () => void;
  onUpdate: (updatedEvent: Event) => void;
}

const UpdateEventStatus: React.FC<UpdateEventStatusProps> = ({
  event,
  onClose,
  onUpdate,
}) => {
  const { t } = useTranslation();
  const axiosInstance = useAxiosInstance();
  const [newStatus, setNewStatus] = useState<EventStatus>(event.eventStatus);
  const [loading, setLoading] = useState<boolean>(false);
  const [, setError] = useState<string>("");

  const handleUpdateStatus = async () => {
    setLoading(true);
    try {
      const response = await axiosInstance.patch<Event>(
        `/events/${event.id}/status`,
        { eventStatus: newStatus }
      );
      onUpdate(response.data);
      onClose();
    } catch (error) {
      setError(
        t("Failed to update event status, please provide a valid choice.") + error
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal opened={true} onClose={onClose} title={t("Update Event Status")}>
      <Select
        label={t("Event Status")}
        value={newStatus}
        onChange={(value) => setNewStatus(value as EventStatus)}
        data={Object.values(EventStatus).map((status) => ({
          value: status,
          label: t(status),
        }))}
      />
      <Button onClick={handleUpdateStatus} loading={loading}>
        {t("Update Status")}
      </Button>
    </Modal>
  );
};

export default UpdateEventStatus;
