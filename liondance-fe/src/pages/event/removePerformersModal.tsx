import React, { useState, useEffect } from "react";
import { Modal, Button, MultiSelect } from "@mantine/core";
import { Event } from "@/models/Event";
import { useEventService } from "@/services/eventService";
import { useUserService } from "@/services/userService";
import { Student } from "@/models/Users";
import { useTranslation } from "react-i18next";

interface RemovePerformersModalProps {
  event: Event;
  onClose: () => void;
  onRemovePerformers: (updatedEvent: Event) => void;
}

const RemovePerformersModal: React.FC<RemovePerformersModalProps> = ({ event, onClose, onRemovePerformers }) => {
  const { t } = useTranslation();
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
        const currentPerformersData = studentsData.filter((student: Student) => event.performers.includes(student.userId!));
        setStudents(currentPerformersData);
        setCurrentPerformers(currentPerformersData);
      } catch {
        setError(t("Failed to fetch students. Please try again."));
      }
    };

    fetchStudents();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleRemovePerformers = async () => {
    setLoading(true);
    setError(null);
    try {
      const updatedEvent = await eventService.removePerformers(event.eventId!, performerIds);
      onRemovePerformers(updatedEvent);
      onClose();
    } catch {
      setError(t("Failed to remove performers. Please try again."));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal opened={true} onClose={onClose} title={t("Remove Performers")}>
      <div>
        <h3>{t("Current Performers")}</h3>
        <ul>
          {currentPerformers.map(performer => (
            <li key={performer.userId}>{`${performer.firstName} ${performer.lastName}`}</li>
          ))}
        </ul>
      </div>
      <MultiSelect
        label={t("Select Performers to remove")}
        placeholder={t("Pick performers")}
        data={students.map(student => ({ value: student.userId!, label: `${student.firstName} ${student.lastName}` }))}
        value={performerIds}
        onChange={setPerformerIds}
        searchable
        nothingfound={t("No performers found")}
        clearable
      />
      {error && <div className="error">{error}</div>}
      <Button onClick={handleRemovePerformers} loading={loading}>
        {t("Remove Performers")}
      </Button>
    </Modal>
  );
};

export default RemovePerformersModal;
