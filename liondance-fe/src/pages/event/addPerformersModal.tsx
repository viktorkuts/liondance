import React, { useState, useEffect } from "react";
import { Modal, Button, MultiSelect } from "@mantine/core";
import { Event } from "@/models/Event";
import { useEventService } from "@/services/eventService";
import { PerformerResponseModel, User } from "@/models/Users";
import { useTranslation } from "react-i18next";

interface AddPerformersModalProps {
  event: Event;
  onClose: () => void;
  onAddPerformers: (updatedEvent: Event) => void;
}

const AddPerformersModal: React.FC<AddPerformersModalProps> = ({
  event,
  onClose,
  onAddPerformers,
}) => {
  const { t } = useTranslation();
  const [performerIds, setPerformerIds] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [students, setStudents] = useState<User[]>([]);
  const [currentPerformers, setCurrentPerformers] = useState<
    PerformerResponseModel[]
  >([]);
  const eventService = useEventService();

  useEffect(() => {
    const fetchStudents = async () => {
      try {
        if (!event.eventId) {
          setError(t("Event Id not set!"));
          return;
        }
        const studentsData = await eventService.getAllPerformers(event.eventId);
        const currentPerformers = await eventService.getEventPerformers(
          event.eventId
        );
        setStudents(
          studentsData.map((e: PerformerResponseModel) => e.performer)
        );
        setCurrentPerformers(currentPerformers);
      } catch {
        setError(t("Failed to fetch performers. Please try again."));
      }
    };

    fetchStudents();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [event.eventId]);

  const handleAddPerformers = async () => {
    setLoading(true);
    setError(null);
    try {
      const updatedEvent = await eventService.assignPerformers(
        event.eventId!,
        performerIds
      );
      onAddPerformers(updatedEvent);
      onClose();
    } catch {
      setError(t("Failed to add performers. Please try again."));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal opened={true} onClose={onClose} title={t("Add Performers")}>
      <div>
        <h3>{t("Current Performers")}</h3>
        <ul>
          {currentPerformers.map((performer) => (
            <li
              key={performer.performer.userId}
            >{`${performer.performer.firstName} ${performer.performer.lastName} : ${performer.status}`}</li>
          ))}
        </ul>
      </div>
      <MultiSelect
        label={t("Select Performers to add")}
        placeholder={t("Pick performers")}
        data={students.map((student) => ({
          value: student.userId!,
          label: `${student.firstName} ${student.lastName}`,
        }))}
        value={performerIds}
        onChange={setPerformerIds}
        searchable
        clearable
      />
      {error && <div className="error">{error}</div>}
      <Button onClick={handleAddPerformers} loading={loading}>
        {t("Add Performers")}
      </Button>
    </Modal>
  );
};

export default AddPerformersModal;
