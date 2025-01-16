import React, { useState } from "react";
import { Modal, Button, Select } from "@mantine/core";
import axiosInstance from "@/utils/axiosInstance";
import { Event, EventStatus } from "@/models/Event";
import "@/components/studentProfile.css";

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
  const [newStatus, setNewStatus] = useState<EventStatus>(event.eventStatus);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

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
        "Failed to update event status, please provide a valid choice." + error
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal opened={true} onClose={onClose} title="Update Event Status">
      {error && <div className="error">{error}</div>}
      <Select
        label="Event Status"
        value={newStatus}
        onChange={(value) => setNewStatus(value as EventStatus)}
        data={Object.values(EventStatus).map((status) => ({
          value: status,
          label: status,
        }))}
      />
      <Button onClick={handleUpdateStatus} loading={loading}>
        Update Status
      </Button>
    </Modal>
  );
};

export default UpdateEventStatus;
