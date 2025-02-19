import { Event } from "@/models/Event";
import { PerformerResponseModel, PerformerStatus, Role } from "@/models/Users";
import { useEventService } from "@/services/eventService";
import { useUserContext } from "@/utils/userProvider";
import { useEffect, useState } from "react";
import "./performerconf.css";
import { Button, Card, Select, Table, Text } from "@mantine/core";
import { useAuth0 } from "@auth0/auth0-react";
import { LogIn } from "react-feather";
import { useParams } from "react-router";
import { AxiosError } from "axios";
import { NotFound } from "../errors/notfound";
import { t } from "i18next";

export const PerformerConfirmation = () => {
  const { eventId } = useParams<{ eventId: string }>();
  const [eventDetails, setEventDetails] = useState<Event>();
  const [performerDetails, setPerformerDetails] =
    useState<PerformerResponseModel>();
  const [sel, setSel] = useState<PerformerStatus>();
  const [notFound, setIsNotFound] = useState<boolean>(false);
  const eventService = useEventService();
  const { user } = useUserContext();
  const { loginWithPopup } = useAuth0();
  const [success, setSuccess] = useState(false);
  const [debounce, setDebounce] = useState(false);

  const submit = async () => {
    if (!eventId) return;
    if (!sel) return;
    if (debounce) return;
    setSuccess(true);
    try {
      await eventService.updatePerformersStatus(eventId, {
        status: sel,
      });

      setDebounce(false);
      setSuccess(true);
    } catch (e) {
      console.log(e);
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      if (!eventId) return;
      if (!user) return;
      if (!user.userId) return;
      if (
        user.roles?.includes(Role.STUDENT) ||
        user.roles?.includes(Role.STAFF)
      ) {
        try {
          const eventData = await eventService.getPerformersStatus(eventId);
          setEventDetails(eventData.eventInfo);
          setPerformerDetails(eventData.performerInfo);
          if (eventData.performerInfo.status !== PerformerStatus.PENDING) {
            setSel(eventData.performerInfo.status);
          }
        } catch (e) {
          if (e instanceof AxiosError) {
            if (e.status == 404) {
              setIsNotFound(true);
            }
          } else {
            console.log(e);
          }
        }
      }
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [eventId, user]);

  if (notFound) return <NotFound />;

  return (
    <div>
      {user == null ? (
        <div className="loginPrompt">
          <h1>Performance Participation</h1>
          <p>
            You are currently logged out, please login using the button below.
          </p>
          <p>
            Access to confirm participation will be available after determining
            if you are a performer for this event.
          </p>
          <Button
            onClick={() => {
              loginWithPopup();
            }}
          >
            <LogIn />
          </Button>
        </div>
      ) : (
        <div>
          {user.roles?.includes(Role.STAFF) ||
          user.roles?.includes(Role.STUDENT) ? (
            <div className="registrationForm events-modal">
              <h1>Performance Participation</h1>
              <p>Hello {performerDetails?.performer.firstName},</p>
              <p>You have been invited to participate to an upcoming event:</p>
              {success ? <Card>Status saved with success!</Card> : null}

              <h2>Will you participate?</h2>
              <Select
                placeholder="Pick an option"
                value={sel}
                onChange={(val) => {
                  setSuccess(false);
                  if (val)
                    setSel(
                      // i hate typescript
                      PerformerStatus[val as keyof typeof PerformerStatus]
                    );
                }}
                data={Object.keys(PerformerStatus)
                  .filter((e) => e != PerformerStatus.PENDING)
                  .map((e) => {
                    return {
                      value: e,
                      label:
                        e.toString().at(0)?.toUpperCase() +
                        e.toString().substring(1).toLowerCase(),
                    };
                  })}
              ></Select>
              <Button onClick={submit}>Save</Button>
              <Text size="lg" fw={500} mt="md" mb="md">
                {t("Event Information")}
              </Text>
              <Table className="events-table">
                <tbody>
                  <tr>
                    <td>{t("Event Id")}</td>
                    <td>{eventDetails?.eventId}</td>
                  </tr>
                  <tr>
                    <td>{t("Event Type")}</td>
                    <td>{eventDetails?.eventType}</td>
                  </tr>
                  <tr>
                    <td>{t("Date & Time")}</td>
                    <td>
                      {eventDetails?.eventDateTime &&
                        new Date(eventDetails?.eventDateTime).toLocaleString()}
                    </td>
                  </tr>
                  <tr>
                    <td>{t("Status")}</td>
                    <td>{eventDetails?.eventStatus}</td>
                  </tr>
                  <tr>
                    <td>{t("Privacy")}</td>
                    <td>{eventDetails?.eventPrivacy}</td>
                  </tr>
                  <tr>
                    <td>{t("Special Request")}</td>
                    <td>{eventDetails?.specialRequest || t("None")}</td>
                  </tr>
                </tbody>
              </Table>

              <Text size="lg" fw={500} mt="md" mb="md">
                {t("Venue Information")}
              </Text>
              <Table className="events-table">
                <tbody>
                  <tr>
                    <td>{t("Address")}</td>
                    <td>{eventDetails?.venue?.streetAddress}</td>
                  </tr>
                  <tr>
                    <td>{t("Zip")}</td>
                    <td>{eventDetails?.venue?.zip}</td>
                  </tr>
                  <tr>
                    <td>{t("City")}</td>
                    <td>{eventDetails?.venue?.city}</td>
                  </tr>
                </tbody>
              </Table>
            </div>
          ) : (
            <p>You do not have permission to access this ressource :(</p>
          )}
        </div>
      )}
    </div>
  );
};
