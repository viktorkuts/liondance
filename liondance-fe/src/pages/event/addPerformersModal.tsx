import React, { useState, useEffect } from "react";
import { Modal, Button, MultiSelect } from "@mantine/core";
import { Event } from "@/models/Event";
import { useEventService } from "@/services/eventService";
import { useUserService } from "@/services/userService";
import { Student } from "@/models/Users";
import { useTranslation } from "react-i18next";

interface AddPerformersModalProps {
    event: Event;
    onClose: () => void;
    onAddPerformers: (updatedEvent: Event) => void;
}

const AddPerformersModal: React.FC<AddPerformersModalProps> = ({ event, onClose, onAddPerformers }) => {
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
                const filteredStudents = studentsData.filter((student: Student) => !event.performers.includes(student.userId!));
                setStudents(filteredStudents);
                const currentPerformersData = studentsData.filter((student: Student) => event.performers.includes(student.userId!));
                setCurrentPerformers(currentPerformersData);
            } catch {
                setError(t("Failed to fetch students. Please try again."));
            }
        };

        fetchStudents();
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleAddPerformers = async () => {
        setLoading(true);
        setError(null);
        try {
            const updatedEvent = await eventService.assignPerformers(event.eventId!, performerIds);
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
                    {currentPerformers.map(performer => (
                        <li key={performer.userId}>{`${performer.firstName} ${performer.lastName}`}</li>
                    ))}
                </ul>
            </div>
            <MultiSelect
                label={t("Select Performers to add")}
                placeholder={t("Pick performers")}
                data={students.map(student => ({ value: student.userId!, label: `${student.firstName} ${student.lastName}` }))}
                value={performerIds}
                onChange={setPerformerIds}
                searchable
                nothingfound={t("No students found")}
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
