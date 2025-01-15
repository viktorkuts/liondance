import React, { useState } from "react";
import { Modal, Button, Select } from "@mantine/core";
import { DateInput } from "@mantine/dates";
import dayjs from "dayjs";
import axiosInstance from "@/utils/axiosInstance";
import { Event } from "@/models/Event";
import '@/components/studentProfile.css';

interface RescheduleEventStatusProps {
    event: Event;
    onClose: () => void;
    onReschedule: (rescheduledEvent: Event) => void;
}

const RescheduleEvent: React.FC<RescheduleEventStatusProps> = ({ event, onClose, onReschedule }) => {
    const [selectedDate, setSelectedDate] = useState<Date | null>(null);
    const [selectedTime, setSelectedTime] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>("");

    const timeOptions = Array.from({ length: 18 }, (_, index) => {
        const hour = 12 + Math.floor(index / 2);
        const minute = index % 2 === 0 ? "00" : "30";
        return `${hour > 12 ? hour - 12 : hour}:${minute} ${hour >= 12 ? "PM" : "AM"}`;
    });

    const combineDateTime = (date: Date | null, time: string | null) => {
        if (date && time) {
            const [hoursMinutes, period] = time.split(" ");
            const [hours, minutes] = hoursMinutes.split(":").map(Number);
            const hour24 = period === "PM" && hours !== 12 ? hours + 12 : hours % 12;

            return dayjs(date)
                .hour(hour24)
                .minute(minutes)
                .second(0)
                .toISOString();
        }
        return null;
    };

    const handleReschedule = async () => {
        if (!selectedDate || !selectedTime) {
            setError("Please select both a new date and time.");
            return;
        }
        const newDateTime = combineDateTime(selectedDate, selectedTime);
        if (newDateTime && dayjs(newDateTime).isBefore(dayjs())) {
            setError("The new date and time cannot be before the current date and time.");
            return;
        }
        setLoading(true);
        try {
            const response = await axiosInstance.patch<Event>(`/events/${event.id}/date`, { eventDateTime: newDateTime });
            onReschedule(response.data);
            onClose();
        } catch (error) {
            setError("Failed to reschedule event, please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal opened={true} onClose={onClose} title="Reschedule Event">
            {error && <div className="error">{error}</div>}
            <div>
                <label>New Date</label>
                <DateInput
                    value={selectedDate}
                    onChange={(date) => {
                        setSelectedDate(date);
                        setError("");
                    }}
                    minDate={dayjs().toDate()}
                />
            </div>
            <div>
                <label>New Time</label>
                <Select
                    data={timeOptions}
                    value={selectedTime}
                    onChange={(time) => {
                        setSelectedTime(time);
                        setError("");
                    }}
                />
            </div>
            <Button onClick={handleReschedule} loading={loading}>
                Reschedule
            </Button>
        </Modal>
    );
};

export default RescheduleEvent;