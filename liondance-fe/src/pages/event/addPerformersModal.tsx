import React, { useState, useEffect } from "react";
import { Modal, Button, MultiSelect } from "@mantine/core";
import { Event } from "@/models/Event";
import { useEventService } from "@/services/eventService";
import { useUserService } from "@/services/userService";
import { Student } from "@/models/Users";

interface AddPerformersModalProps {
    event: Event;
    onClose: () => void;
    onAddPerformers: (updatedEvent: Event) => void;
}

const AddPerformersModal: React.FC<AddPerformersModalProps> = ({ event, onClose, onAddPerformers }) => {
    const [performerIds, setPerformerIds] = useState<string[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [students, setStudents] = useState<Student[]>([]);
    const eventService = useEventService();
    const userService = useUserService();

    useEffect(() => {
        const fetchStudents = async () => {
            try {
                const studentsData = await userService.getAllStudents();
                setStudents(studentsData);
            } catch {
                setError("Failed to fetch students. Please try again.");
            }
        };

        fetchStudents();
    }, []);

    const handleAddPerformers = async () => {
        setLoading(true);
        setError(null);
        try {
            const updatedEvent = await eventService.assignPerformers(event.eventId!, performerIds);
            onAddPerformers(updatedEvent);
            onClose();
        } catch {
            setError("Failed to add performers. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal opened={true} onClose={onClose} title="Add Performers">
            <MultiSelect
                label="Select Performers"
                placeholder="Pick performers"
                data={students.map(student => ({ value: student.userId!, label: `${student.firstName} ${student.lastName}` }))}
                value={performerIds}
                onChange={setPerformerIds}
                searchable
                nothingFound="No students found"
                clearable
            />
            {error && <div className="error">{error}</div>}
            <Button onClick={handleAddPerformers} loading={loading}>
                Add Performers
            </Button>
        </Modal>
    );
};

export default AddPerformersModal;
