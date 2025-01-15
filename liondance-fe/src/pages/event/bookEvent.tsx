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
import { IMaskInput } from 'react-imask';
import { InputBase } from '@mantine/core';
import { EventType, Event, PaymentMethod, EventStatus } from "@/models/Event";
import eventService from "@/services/eventService";

function BookEvent() {
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [selectedTime, setSelectedTime] = useState<string | null>(null);
  const [emailData, setEmailData] = useState<string[]>([]);
  const [emailDataLoading, setEmailDataLoading] = useState<boolean>(false);
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

  const form = useForm({
    mode: "controlled",
    initialValues: {
      firstName: "",
      middleName: "",
      lastName: "",
      email: "",
      phone: "",
      address: {
        streetAddress: "",
        city: "",
        state: "",
        zip: "",
      },
      eventDateTime: "",
      eventType: "",
      paymentMethod: "",
      specialRequest: "",
    },

    validate: {
      firstName: (value) => (value.length > 0 ? null : "Field is required"),
      lastName: (value) => (value.length > 0 ? null : "Field is required"),
      email: (value) => (/^\S+@\S+$/.test(value) ? null : "Invalid email"),
      phone: (value) =>
        value && value.length > 0
          ? /\(\d{3}\) \d{3}-\d{4}$/.test(value)
            ? null
            : "Invalid phone number format"
          : "Field is required",
      eventDateTime: (value) =>
        value && value.length > 0 ? null : "Both date and time are required",
      eventType: (value) => (value.length > 0 ? null : "Field is required"),
      paymentMethod: (value) => (value.length > 0 ? null : "Field is required"),
      address:
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

  form.watch("email", ({ value }) => {
    setEmailDataLoading(true);

    if (value.trim().length === 0 || value.includes("@")) {
      setEmailDataLoading(false);
    } else {
      setEmailDataLoading(true);
      setEmailData(
        ["gmail.com", "outlook.com", "hotmail.com", "yahoo.com"].map(
          (prov) => `${value}@${prov}`
        )
      );
      setEmailDataLoading(false);
    }
  });

  form.watch("address.state", ({ value }) => {
    const code = provinces.find((provinceObj) => provinceObj.name === value);
    setSelectedProvince(code ? code.code : "");
  });

  form.watch("address.city", ({ value }) => {
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

  const submit = () => {
    setActiveStep(4);
    const values = form.getValues();
    const newEvent: Event = {
      firstName: values.firstName,
      middleName: values.middleName,
      lastName: values.lastName,
      email: values.email,
      phone: values.phone,
      address: values.address,
      eventDateTime: new Date(values.eventDateTime),
      eventType: values.eventType as EventType,
      paymentMethod: values.paymentMethod as PaymentMethod,
      specialRequest: values.specialRequest,
      eventStatus: EventStatus.PENDING
    };

    const run = async () => {
      await eventService.bookEvent(newEvent);
    };
    run();
  };

  return (
    <div className={classes.registrationForm}>
      <h1>Event Registration Form</h1>
      <Stepper active={activeStep}>
        <Stepper.Step label="Event Information">
          <TextInput
            label="First Name"
            placeholder="John"
            key={form.key("firstName")}
            required
            {...form.getInputProps("firstName")}
          />
          <TextInput
            label="Middle Name"
            placeholder="Z."
            key={form.key("middleName")}
            {...form.getInputProps("middleName")}
          />
          <TextInput
            label="Last Name"
            placeholder="Doe"
            key={form.key("lastName")}
            required
            {...form.getInputProps("lastName")}
          />
          <Autocomplete
            rightSection={emailDataLoading ? <Loader size={12} /> : null}
            data={emailData}
            label="E-Mail"
            placeholder="john.doe@example.com"
            key={form.key("email")}
            required
            {...form.getInputProps("email")}
          />
          <InputBase
            label="Phone Number"
            placeholder="(123) 456-7890"
            component={IMaskInput}
            mask="(000) 000-0000"
            {...form.getInputProps("phone", { withError: true })} 
            onAccept={(value: string) => {
              form.setFieldValue("phone", value); 
              form.validateField("phone"); 
            }}
            required
          />
          <DateInput
            label="Event Date"
            placeholder="Pick a date"
            minDate={dayjs().add(14, "day").toDate()} 
            value={selectedDate}
            onChange={(date) => {
              setSelectedDate(date);
              combineDateTime(date, selectedTime);
            }}
            error={form.errors.eventDateTime}
            required
          />


          <Select
            label="Event Time"
            placeholder="Select a time"
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
            label="Event Type"
            placeholder="Wedding"
            data={Object.values(EventType)}
            key={form.key("eventType")}
            required
            {...form.getInputProps("eventType")}
          />
          <Select
            label="Payment Method"
            placeholder="Cash"
            data={Object.values(PaymentMethod)}
            key={form.key("paymentMethod")}
            required
            {...form.getInputProps("paymentMethod")}
          />
          <TextInput
            label="Special Request"
            placeholder="Request..."
            key={form.key("specialRequest")}
            {...form.getInputProps("specialRequest")}
          />
        </Stepper.Step>
        <Stepper.Step label="Location Information">
          <TextInput
            label="Address Line"
            placeholder="123 Main Street"
            key={form.key("address.streetAddress")}
            required
            {...form.getInputProps("address.streetAddress")}
          />
          <Select
            label="Province"
            placeholder="Quebec"
            comboboxProps={{ withinPortal: true }}
            data={provincesFormatted()}
            key={form.key("address.state")}
            required
            {...form.getInputProps("address.state")}
          />
          <Autocomplete
            label="City"
            rightSection={cityDataLoading ? <Loader size={12} /> : null}
            data={cityData}
            placeholder="Montreal"
            key={form.key("address.city")}
            limit={10}
            required
            {...form.getInputProps("address.city")}
          />
          <TextInput
            label="Postal Code"
            placeholder="H1H 1H1"
            key={form.key("address.zip")}
            required
            {...form.getInputProps("address.zip")}
          />
        </Stepper.Step>
        <Stepper.Step label="Confirmation">
          <div className={classes.completedForm}>
            <h2>Does this information look correct?</h2>
            <p>
              Name: {form.getValues().firstName} {form.getValues().lastName}
              </p>
            <p>
              Email: {form.getValues().email}
              </p>
            <p>
              Phone: {form.getValues().phone}
              </p>
            <p>
              Location: {form.getValues().address.streetAddress},{" "}
              {form.getValues().address.city}, {form.getValues().address.state},{" "}
              {form.getValues().address.zip}
            </p>
            <p>
              Event Date: {dayjs(form.getValues().eventDateTime).format("MMMM D, YYYY")}
              </p>
            <p>
              Event Time: {dayjs(form.getValues().eventDateTime).format("h:mm A")}
              </p>
          </div>
        </Stepper.Step>
        <Stepper.Completed>
          {activeStep === 4 && (
            <div className={classes.completedForm}>
              <h2>Request Submitted!</h2>
              <p>
                Thank you for choosing the LVH Lion Dance Team, {form.getValues().firstName}!
              </p>
              <p>
                We will be in touch with you shortly to confirm your booking request. 
                Please check your email inbox for updates.
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