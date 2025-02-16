import React, { useState, useEffect } from "react";
import { Modal, Button, MultiSelect } from "@mantine/core";
import { Event } from "@/models/Event";
import { useEventService } from "@/services/eventService";
import { useUserService } from "@/services/userService";
import { Student } from "@/models/Users";

interface RemovePerformersModalProps {
  event: Event;
  onClose: () => void;
  onRemovePerformers: (updatedEvent: Event) => void;
}

const RemovePerformersModal: React.FC<RemovePerformersModalProps> = ({ event, onClose, onRemovePerformers }) => {
  const [performerIds, setPerformerIds] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [students, setStudents] = useState<Student[]>([]);
  const [currentPerformers, setCurrentPerformers] = useState<Student[]>([]);
  const eventService = useEventService();
  const userService = useUserService();

  useEffect(() => {
    const fetchStudents = async () => {
      try {
        const studentsData = await userService.getAllStudents();
        const currentPerformersData = studentsData.filter(student => event.performers.includes(student.userId!));
        setStudents(currentPerformersData);
        setCurrentPerformers(currentPerformersData);
      } catch {
        setError("Failed to fetch students. Please try again.");
      }
    };

    fetchStudents();
  }, []);

  const handleRemovePerformers = async () => {
    setLoading(true);
    setError(null);
    try {
      const updatedEvent = await eventService.removePerformers(event.eventId!, performerIds);
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
      <div>
        <h3>Current Performers</h3>
        <ul>
          {currentPerformers.map(performer => (
            <li key={performer.userId}>{`${performer.firstName} ${performer.lastName}`}</li>
          ))}
        </ul>
      </div>
      <MultiSelect
        label="Select Performers to remove"
        placeholder="Pick performers"
        data={students.map(student => ({ value: student.userId!, label: `${student.firstName} ${student.lastName}` }))}
        value={performerIds}
        onChange={setPerformerIds}
        searchable
        nothingFound="No performers found"
        clearable
      />
      {error && <div className="error">{error}</div>}
      <Button onClick={handleRemovePerformers} loading={loading}>
        Remove Performers
      </Button>
    </Modal>
  );
};

export default RemovePerformersModal;
