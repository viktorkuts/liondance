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
    const [selectedPerformers, setSelectedPerformers] = useState<string[]>([]);
    const [students, setStudents] = useState<Student[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const eventService = useEventService();
    const studentService = useUserService();

    useEffect(() => {
        const fetchStudents = async () => {
            try {
                const data = await studentService.getAllStudents();
                setStudents(data);
            } catch (error) {
                console.error("Failed to fetch students", error);
            }
        };

        fetchStudents();
    }, []);

    const handleAddPerformers = async () => {
        setLoading(true);
        setError(null);
        try {
            const updatedEvent = await eventService.addPerformers(event.eventId!, selectedPerformers);
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
                data={students.map(student => ({ value: student.userId, label: `${student.firstName} ${student.lastName}` }))}
                value={selectedPerformers}
                onChange={setSelectedPerformers}
                placeholder="Select performers"
                label="Performers"
            />
            {error && <div className="error">{error}</div>}
            <Button onClick={handleAddPerformers} loading={loading}>
                Add Performers
            </Button>
        </Modal>
    );
};

export default AddPerformersModal;