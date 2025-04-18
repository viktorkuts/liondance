import React, { useState } from "react";
import { Modal, Button, Select } from "@mantine/core";
import { DateInput } from "@mantine/dates";
import dayjs from "dayjs";
import { useAxiosInstance } from "@/utils/axiosInstance";
import { Event } from "@/models/Event";
import "@/pages/event/rescheduleEvent.css";
import { useTranslation } from "react-i18next";

interface RescheduleEventStatusProps {
  event: Event;
  onClose: () => void;
  onReschedule: (rescheduledEvent: Event) => void;
}

const RescheduleEvent: React.FC<RescheduleEventStatusProps> = ({
  event,
  onClose,
  onReschedule,
}) => {
  const { t } = useTranslation();
  const axiosInstance = useAxiosInstance();
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

      return dayjs(date).hour(hour24).minute(minutes).second(0).toISOString();
    }
    return null;
  };

  const handleReschedule = async () => {
    if (!selectedDate || !selectedTime) {
      setError(t("Please select both a new date and time."));
      return;
    }
    const newDateTime = combineDateTime(selectedDate, selectedTime);
    if (newDateTime && dayjs(newDateTime).isBefore(dayjs())) {
      setError(
        t("The new date and time cannot be before the current date and time.")
      );
      return;
    }
    setLoading(true);
    try {
      const response = await axiosInstance.patch<Event>(
        `/events/${event.eventId}/date`,
        { eventDateTime: newDateTime }
      );
      onReschedule(response.data);
      onClose();
    } catch (error) {
      setError(t("Failed to reschedule event, please try again.") + error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal opened={true} onClose={onClose} title={t("Reschedule Event")}>
      {error && <div className="error">{error}</div>}
      <div>
        <label>{t("New Date")}</label>
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
        <label>{t("New Time")}</label>
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
        {t("Reschedule")}
      </Button>
    </Modal>
  );
};

export default RescheduleEvent;
