import React, { useState } from "react";
import { Modal, Button, TextInput, Select } from "@mantine/core";
import axiosInstance from "@/utils/axiosInstance";
import { Event } from "@/models/Event";
import '@/components/studentProfile.css';
import { EventType, PaymentMethod } from "@/models/Event";


interface UpdateEventDetailsProps {
    event: Event;
    onClose: () => void;
    onUpdate: (updatedEvent: Event) => void;
}

const UpdateEventDetails: React.FC<UpdateEventDetailsProps> = ({ event, onClose, onUpdate }) => {
    const [loading, setLoading] = useState<boolean>(false);
    const [firstName, setFirstName] = useState<string>(event.firstName);
    const [middleName, setMiddleName] = useState<string>(event.middleName || "");
    const [lastName, setLastName] = useState<string>(event.lastName);
    const [email, setEmail] = useState<string>(event.email);
    const [phone, setPhone] = useState<string>(event.phone);
    const [streetAddress, setStreetAddress] = useState<string>(event.address.streetAddress);
    const [city, setCity] = useState<string>(event.address.city);
    const [state, setState] = useState<string>(event.address.state);
    const [zip, setZip] = useState<string>(event.address.zip);
    const [eventType, setEventType] = useState<EventType>(event.eventType);
    const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>(event.paymentMethod);
    const [specialRequest, setSpecialRequest] = useState<string>(event.specialRequest || "");
    const [error, setError] = useState<string>("");

    const handleUpdate = async () => {
        if (!firstName || !lastName || !email || !phone || !streetAddress || !city || !state || !zip || !eventType || !paymentMethod) {
            setError("Please fill in all fields.");
            return;
        }
        setLoading(true);
        try {
            const updatedEvent = { ...event, firstName: firstName, lastName: lastName, middleName, email, phone, address: { streetAddress, city, state, zip }, eventType, paymentMethod, specialRequest };
            const response = await axiosInstance.put<Event>(`/events/${event.id}`, updatedEvent);
            onUpdate(response.data);
            onClose();
        } catch {
            setError("Failed to update event, please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal opened={true} onClose={onClose} title="Update Event Details">
            {error && <div className="error">{error}</div>}
            <div>
                <label>First Name</label>
                <TextInput
                    value={firstName}
                    onChange={(e) => {
                        setFirstName(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>Middle Name</label>
                <TextInput
                    value={middleName}
                    onChange={(e) => {
                        setMiddleName(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>Last Name</label>
                <TextInput
                    value={lastName}
                    onChange={(e) => {
                        setLastName(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>Email</label>
                <TextInput
                    value={email}
                    onChange={(e) => {
                        setEmail(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>Phone</label>
                <TextInput
                    value={phone}
                    onChange={(e) => {
                        setPhone(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>Street Address</label>
                <TextInput
                    value={streetAddress}
                    onChange={(e) => {
                        setStreetAddress(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>City</label>
                <TextInput
                    value={city}
                    onChange={(e) => {
                        setCity(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>State</label>
                <TextInput
                    value={state}
                    onChange={(e) => {
                        setState(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>Zip</label>
                <TextInput
                    value={zip}
                    onChange={(e) => {
                        setZip(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <div>
                <label>Event Type</label>
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
                <label>Payment Method</label>
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
                <label>Special Request</label>
                <TextInput
                    value={specialRequest}
                    onChange={(e) => {
                        setSpecialRequest(e.currentTarget.value);
                        setError("");
                    }}
                />
            </div>
            <Button onClick={handleUpdate} loading={loading}>
                Update
            </Button>
        </Modal>
    );
};

export default UpdateEventDetails;