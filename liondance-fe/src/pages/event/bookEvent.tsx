import { useEffect, useState } from "react";
import { useForm } from "@mantine/form";
import {
  Autocomplete,
  Button,
  Group,
  Loader,
  Select,
  Stepper,
  TextInput,
} from "@mantine/core";
import dayjs from "dayjs";
import { DateInput } from "@mantine/dates";
import geoService from "@/services/geoService";
import { Province } from "@/types/geo";
import classes from "./booking.module.css";

import {
  EventType,
  Event,
  PaymentMethod,
  EventStatus,
  EventPrivacy,
} from "@/models/Event";
import { useEventService } from "@/services/eventService";
import { useUserContext } from "@/utils/userProvider";
import { useNavigate } from "react-router";
import { useTranslation } from "react-i18next";

let submitDebounce = false;

function BookEvent() {
  const navigate = useNavigate();
  const { user, isLoading } = useUserContext();

  const { t, i18n } = useTranslation();
  const eventService = useEventService();
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [selectedTime, setSelectedTime] = useState<string | null>(null);
  const [cityData, setCityData] = useState<string[]>([]);
  const [cityDataLoading, setCityDataLoading] = useState<boolean>(false);
  const [activeStep, setActiveStep] = useState(0);
  const [selectedProvince, setSelectedProvince] = useState<string>("");
  const [provinces, setProvinces] = useState<Province[]>(
    geoService.provincesCache
  );

  const provincesFormatted = () => {
    return provinces.map((val) => {
      return val.name;
    });
  };

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

      const combinedDateTime = dayjs(date)
        .hour(hour24)
        .minute(minutes)
        .second(0)
        .toISOString();

      form.setFieldValue("eventDateTime", combinedDateTime);
      form.clearFieldError("eventDateTime");
    } else {
      form.setFieldValue("eventDateTime", "");
      form.setFieldError("eventDateTime", "Both date and time are required");
    }
  };

  useEffect(() => {
    const run = async () => {
      setProvinces(await geoService.provinces());
    };
    run();
  }, []);

  useEffect(() => {
    if (!isLoading && user == null) {
      navigate("/client-registration");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user, isLoading]);

  const form = useForm({
    mode: "controlled",
    initialValues: {
      venue: {
        streetAddress: "",
        city: "",
        state: "",
        zip: "",
      },
      eventDateTime: "",
      eventType: "",
      paymentMethod: "",
      specialRequest: "",
      eventPrivacy: "",
    },

    validate: {
      eventDateTime: (value) =>
        value && value.length > 0 ? null : "Both date and time are required",
      eventType: (value) => (value.length > 0 ? null : "Field is required"),
      paymentMethod: (value) => (value.length > 0 ? null : "Field is required"),
      eventPrivacy: (value) => (value.length > 0 ? null : "Field is required"),
      venue:
        (activeStep === 1 && {
          streetAddress: (value) =>
            value.length > 0 ? null : "Field is required",
          state: (value) => (value.length > 0 ? null : "Field is required"),
          city: (value) => (value.length > 0 ? null : "Field is required"),
          zip: (value) =>
            /^[A-Za-z]\d[A-Za-z][ -]?\d[A-Za-z]\d$/.test(value)
              ? null
              : "Field is invalid",
        }) ||
        undefined,
    },
  });

  form.watch("venue.state", ({ value }) => {
    const code = provinces.find((provinceObj) => provinceObj.name === value);
    setSelectedProvince(code ? code.code : "");
  });

  form.watch("venue.city", ({ value }) => {
    setCityDataLoading(true);

    if (value.trim().length === 0) {
      setCityDataLoading(false);
    } else {
      setCityDataLoading(true);
      const run = async () => {
        const results = await geoService.getCities(value, selectedProvince);
        setCityData(results.map((city) => city.name));
        setCityDataLoading(false);
      };
      run();
    }
  });

  const nextStep = () => {
    setActiveStep((current) => {
      console.log(form.validate().errors);
      if (form.validate().hasErrors) {
        return current;
      }
      return current < 4 ? current + 1 : current;
    });
  };

  const previousStep = () => {
    setActiveStep((current) => {
      return current < 0 ? current : current - 1;
    });
  };

  const submit = async () => {
    if (submitDebounce) return;
    submitDebounce = true;
    const values = form.getValues();
    const newEvent: Event = {
      venue: values.venue,
      eventDateTime: new Date(values.eventDateTime),
      eventType: values.eventType as EventType,
      paymentMethod: values.paymentMethod as PaymentMethod,
      specialRequest: values.specialRequest,
      eventStatus: EventStatus.PENDING,
      eventPrivacy: values.eventPrivacy as EventPrivacy,
      performers: [],
    };

    await eventService.bookEvent(newEvent);
    setActiveStep(4);
  };

  return (
    <div className={classes.registrationForm}>
      <h1>{t("Event Registration Form")}</h1>
      <Stepper active={activeStep}>
        <Stepper.Step label={t("Event Information")}>
          <DateInput
            label={t("Event Date")}
            placeholder={t("Pick a date")}
            minDate={dayjs().add(14, "day").toDate()}
            value={selectedDate}
            onChange={(date) => {
              setSelectedDate(date);
              combineDateTime(date, selectedTime);
            }}
            error={form.errors.eventDateTime}
            required
            locale={i18n.language}
          />

          <Select
            label={t("Event Time")}
            placeholder={t("Select a time")}
            data={timeOptions}
            value={selectedTime}
            onChange={(time) => {
              setSelectedTime(time);
              combineDateTime(selectedDate, time);
            }}
            error={form.errors.eventDateTime}
            required
          />

          <Select
            label={t("Event Type")}
            placeholder={t("Wedding")}
            data={Object.values(EventType).map((type) => ({
              value: type,
              label: t(type),
            }))}
            key={form.key("eventType")}
            required
            {...form.getInputProps("eventType")}
          />

          <Select
            label={t("Payment Method")}
            placeholder={t("Cash")}
            data={Object.values(PaymentMethod).map((method) => ({
              value: method,
              label: t(method),
            }))}
            key={form.key("paymentMethod")}
            required
            {...form.getInputProps("paymentMethod")}
          />
          <TextInput
            label={t("Special Request")}
            placeholder={t("Request...")}
            key={form.key("specialRequest")}
            {...form.getInputProps("specialRequest")}
          />
          <Select
            label={t("Event Privacy")}
            placeholder={t("PRIVATE")}
            data={Object.values(EventPrivacy).map((privacy) => ({
              value: privacy,
              label: t(privacy),
            }))}
            key={form.key("eventPrivacy")}
            required
            {...form.getInputProps("eventPrivacy")}
          />
        </Stepper.Step>
        <Stepper.Step label={t("Location Information")}>
          <TextInput
            label={t("Address Line")}
            placeholder="123 Main Street"
            key={form.key("venue.streetAddress")}
            required
            {...form.getInputProps("venue.streetAddress")}
          />
          <Select
            label={t("Province")}
            placeholder="Quebec"
            comboboxProps={{ withinPortal: true }}
            data={provincesFormatted()}
            key={form.key("venue.state")}
            required
            {...form.getInputProps("venue.state")}
          />
          <Autocomplete
            label={t("City")}
            rightSection={cityDataLoading ? <Loader size={12} /> : null}
            data={cityData}
            placeholder="Montreal"
            key={form.key("venue.city")}
            limit={10}
            required
            {...form.getInputProps("venue.city")}
          />
          <TextInput
            label={t("Postal Code")}
            placeholder="H1H 1H1"
            key={form.key("venue.zip")}
            required
            {...form.getInputProps("venue.zip")}
          />
        </Stepper.Step>
        <Stepper.Step label={t("Confirmation")}>
          <div className={classes.completedForm}>
            <h2>Does this information look correct?</h2>
            <p>
              Location: {form.getValues().venue.streetAddress},{" "}
              {form.getValues().venue.city}, {form.getValues().venue.state},{" "}
              {form.getValues().venue.zip}
            </p>
            <p>
              Event Date:{" "}
              {dayjs(form.getValues().eventDateTime).format("MMMM D, YYYY")}
            </p>
            <p>
              Event Time:{" "}
              {dayjs(form.getValues().eventDateTime).format("h:mm A")}
            </p>
            <p>Event Privacy: {form.getValues().eventPrivacy}</p>
          </div>
        </Stepper.Step>
        <Stepper.Completed>
          {activeStep === 4 && (
            <div className={classes.completedForm}>
              <h2>Request Submitted!</h2>
              <p>Thank you for choosing the LVH Lion Dance Team!</p>
              <p>
                We will be in touch with you shortly to confirm your booking
                request. Please check your email inbox for updates.
              </p>
              <Button component="a" href="/">
                Back to Home
              </Button>
            </div>
          )}
        </Stepper.Completed>
      </Stepper>

      <Group justify="flex-end" mt="xl">
        {activeStep > 0 && activeStep < 2 && (
          <Button variant="default" onClick={previousStep}>
            Back
          </Button>
        )}
        {activeStep > 0 && activeStep === 2 && (
          <Button variant="default" onClick={previousStep}>
            I need to correct something!
          </Button>
        )}
        {activeStep < 2 && <Button onClick={nextStep}>Next</Button>}
        {activeStep === 2 && <Button onClick={submit}>Looks Good!</Button>}
      </Group>
    </div>
  );
}

export default BookEvent;
