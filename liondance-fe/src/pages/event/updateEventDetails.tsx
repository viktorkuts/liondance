import React, { useState } from "react";
import { Modal, Button, TextInput, Select } from "@mantine/core";
import { useAxiosInstance } from "@/utils/axiosInstance";
import { Event } from "@/models/Event";
import '@/components/studentProfile.css';
import { EventType, PaymentMethod } from "@/models/Event";
import { useTranslation } from "react-i18next";

interface UpdateEventDetailsProps {
    event: Event;
    onClose: () => void;
    onUpdate: (updatedEvent: Event) => void;
}

const UpdateEventDetails: React.FC<UpdateEventDetailsProps> = ({ event, onClose, onUpdate }) => {
    const { t } = useTranslation();
    const [loading, setLoading] = useState<boolean>(false);
    const [streetAddress, setStreetAddress] = useState<string>(event.venue.streetAddress);
    const [city, setCity] = useState<string>(event.venue.city);
    const [state, setState] = useState<string>(event.venue.state);
    const [zip, setZip] = useState<string>(event.venue.zip);
    const [eventType, setEventType] = useState<EventType>(event.eventType);
    const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>(event.paymentMethod);
    const [specialRequest, setSpecialRequest] = useState<string>(event.specialRequest || "");
    const [error, setError] = useState<string>("");
    const axiosInstance = useAxiosInstance();
    const handleUpdate = async () => {
        if (!streetAddress || !city || !state || !zip || !eventType || !paymentMethod) {
            setError(t("Please fill in all fields."));
            return;
        }
        setLoading(true);
        try {
            const updatedEvent = { ...event, venue: { streetAddress, city, state, zip }, eventType, paymentMethod, specialRequest };
            const response = await axiosInstance.put<Event>(`/events/${event.eventId}`, updatedEvent);
            onUpdate(response.data);
            onClose();
        } catch {
            setError(t("Failed to update event, please try again."));
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal opened={true} onClose={onClose} title={t("Update Event Details")}>
            {error && <div className="error">{error}</div>}
            <div>
                <label>{t("Street Address")}</label>
                <TextInput
                    value={streetAddress}
                    onChange={(e) => {
                        setStreetAddress(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>{t("City")}</label>
                <TextInput
                    value={city}
                    onChange={(e) => {
                        setCity(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>{t("State")}</label>
                <TextInput
                    value={state}
                    onChange={(e) => {
                        setState(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>{t("Zip")}</label>
                <TextInput
                    value={zip}
                    onChange={(e) => {
                        setZip(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>{t("Event Type")}</label>
                <Select
                    data={Object.values(EventType)}
                    value={eventType}
                    onChange={(type: string | null) => {
                        if (type) {
                            setEventType(type as EventType);
                        }
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>{t("Payment Method")}</label>
                <Select
                    data={Object.values(PaymentMethod)}
                    value={paymentMethod}
                    onChange={(method: string | null) => {
                        if (method) {
                            setPaymentMethod(method as PaymentMethod);
                        }
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>{t("Special Request")}</label>
                <TextInput
                    value={specialRequest}
                    onChange={(e) => {
                        setSpecialRequest(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <Button onClick={handleUpdate} loading={loading}>
                {t("Update")}
            </Button>
        </Modal>
    );
};

export default UpdateEventDetails;