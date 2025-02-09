// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-nocheck
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Loader, Button } from "@mantine/core";
import { useUserService } from "../services/userService";
import { useTranslation } from "react-i18next";
import "./clientProfile.css";

interface EventModel {
  eventId: string;
  venue: {
    streetAddress: string;
    city: string;
    state: string;
    zip: string;
  };
  eventDateTime: string;
  eventType: string;
  paymentMethod: string;
  specialRequest?: string;
  eventStatus: string;
  eventPrivacy: string;
}

interface ClientResponseModel {
  clientId: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  email: string;
  phone?: string;
  activeEvents: EventModel[];
  pastEvents: EventModel[];
}

const ClientProfile: React.FC = () => {
  const { t } = useTranslation();
  const clientService = useUserService();
  const { clientId } = useParams<{ clientId: string }>();
  const [client, setClient] = useState<ClientResponseModel | null>(null);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchClient = async () => {
      try {
        const data = await clientService.getClientProfile(clientId!);
        setClient(data);
      } catch (err: unknown) {
        setError(err instanceof Error ? err.message : String(err));
      }
    };

    fetchClient();
  }, [clientId]);

  if (error) {
    return <div className="error">{t('Error')}: {error}</div>;
  }

  if (!client) {
    return <Loader className="loading" />;
  }

  return (
    <div className="client-profile">
      <h1>{t('Client Profile')}</h1>
      <div className="client-details">
        <p><strong>{t('Name')}:</strong> {client.firstName} {client.lastName} {client.middleName}</p>
        <p><strong>{t('Email')}:</strong> {client.email}</p>
        <p><strong>{t('Phone')}:</strong> {client.phone || t('N/A')}</p>

        {/* Active Events Section */}
        <div className="active-events">
          <h2>{t('Active Events')}</h2>
          {client.activeEvents.length > 0 ? (
            <ul>
              {client.activeEvents.map((event) => (
                <li key={event.eventId}>
                  <strong>{t(event.eventType)}</strong> -{" "}
                  {new Date(event.eventDateTime).toLocaleString("en-CA", {
                    year: "numeric",
                    month: "2-digit",
                    day: "2-digit",
                    hour: "2-digit",
                    minute: "2-digit",
                    second: "2-digit",
                    hour12: true,
                  }).replace(",", "")}
                  <br />
                  {t('Venue')}: {event.venue.streetAddress}, {event.venue.city}, {event.venue.state} {event.venue.zip}
                </li>
              ))}
            </ul>
          ) : (
            <p>{t('No active events.')}</p>
          )}
        </div>

        {/* Past Events Section */}
        <div className="past-events">
          <h2>{t('Past Events')}</h2>
          {client.pastEvents.length > 0 ? (
            <ul>
              {client.pastEvents.map((event) => (
                <li key={event.eventId}>
                  <strong>{t(event.eventType)}</strong> -{" "}
                  {new Date(event.eventDateTime).toLocaleString("en-CA", {
                    year: "numeric",
                    month: "2-digit",
                    day: "2-digit",
                    hour: "2-digit",
                    minute: "2-digit",
                    second: "2-digit",
                    hour12: true,
                  }).replace(",", "")}
                  <br />
                  {t('Venue')}: {event.venue.streetAddress}, {event.venue.city}, {event.venue.state} {event.venue.zip}
                </li>
              ))}
            </ul>
          ) : (
            <p>{t('No past events available.')}</p>
          )}
        </div>
      </div>

      <div className="button-container">
        <Button onClick={() => navigate("/clients")}>{t('Back to Client List')}</Button>
      </div>
    </div>
  );
};

export default ClientProfile;