import React, { useState } from "react";
import { Modal, Button, TextInput } from "@mantine/core";
import { Event } from "@/models/Event";
import { useEventService } from "@/services/eventService";

interface RemovePerformersModalProps {
  event: Event;
  onClose: () => void;
  onRemovePerformers: (updatedEvent: Event) => void;
}

const RemovePerformersModal: React.FC<RemovePerformersModalProps> = ({ event, onClose, onRemovePerformers }) => {
  const [performers, setPerformers] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const eventService = useEventService();

  const handleRemovePerformers = async () => {
    setLoading(true);
    setError(null);
    try {
      const updatedEvent = await eventService.removePerformers(event.eventId!, performers.split(","));
      onRemovePerformers(updatedEvent);
      onClose();
    } catch {
      setError("Failed to remove performers. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal opened={true} onClose={onClose} title="Remove Performers">
      <TextInput
        label="Performers (comma-separated emails)"
        value={performers}
        onChange={(e) => setPerformers(e.currentTarget.value)}
      />
      {error && <div className="error">{error}</div>}
      <Button onClick={handleRemovePerformers} loading={loading}>
        Remove Performers
      </Button>
    </Modal>
  );
};

export default RemovePerformersModal;