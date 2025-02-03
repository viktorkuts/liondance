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
import { DateInput } from "@mantine/dates";
import classes from "./registration.module.css";
import { Calendar, LogIn } from "react-feather";
import geoService from "@/services/geoService";
import { Province } from "@/types/geo";
import { Client } from "@/models/Users";
import { useUserService } from "@/services/userService";
import { useAuth0 } from "@auth0/auth0-react";
import { useUserContext } from "@/utils/userProvider";
import { useNavigate } from "react-router";

function ClientRegistration() {
  const { isAuthenticated, loginWithRedirect } = useAuth0();
  const { user } = useUserContext();
  const navigate = useNavigate();
  const userService = useUserService();
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

  useEffect(() => {
    const run = async () => {
      setProvinces(await geoService.provinces());
    };
    run();
  }, []);

  useEffect(() => {
    if (user != null) {
      navigate("/booking");
    }
  });

  const form = useForm({
    mode: "controlled",
    initialValues: {
      firstName: "",
      middleName: "",
      lastName: "",
      dob: "",
      email: "",
      address: {
        streetAddress: "",
        city: "",
        state: "",
        zip: "",
      },
      phone: "",
    },

    validate: {
      firstName: (value) => (value.length > 0 ? null : "Field is required"),
      lastName: (value) => (value.length > 0 ? null : "Field is required"),
      dob: (value) => {
        if (typeof value !== "object") {
          return "Field is required";
        } else if ((value as Date) > new Date()) {
          return "Cannot be set in the future!";
        }
      },
      email: (value) => (/^\S+@\S+$/.test(value) ? null : "Invalid email"),
      phone: (value) => (/\(\d{3}\) \d{3}-\d{4}$/.test(value) ? null : "Invalid phone number format"),
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
      return current < 3 ? current + 1 : current;
    });
  };

  const previousStep = () => {
    setActiveStep((current) => {
      return current < 0 ? current : current - 1;
    });
  };

  const submit = () => {
    setActiveStep(3);
    const values = form.getValues();
    const newStudent: Client = {
      firstName: values.firstName,
      middleName: values.middleName,
      lastName: values.lastName,
      dob: values.dob,
      email: values.email,
      phone: values.phone,
      address: values.address,
    };

    const run = async () => {
      await userService.registerClient(newStudent);
    };
    run();
  };

  {
    return !isAuthenticated ? (
      <div className={classes.loginPrompt}>
        <h1>Client Registration</h1>
        <p>Please login with Google or Facebook using the button below</p>
        <Button
          onClick={() => {
            loginWithRedirect({
              authorizationParams: {
                redirect_uri: window.location.origin + window.location.pathname,
              },
            });
          }}
        >
          <LogIn />
        </Button>
      </div>
    ) : (
      <div className={classes.registrationForm}>
        <h1>Client Registration Form</h1>
        <Stepper active={activeStep}>
          <Stepper.Step label="Client Information">
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
              label="Primary E-Mail"
              placeholder="john.doe@example.com"
              key={form.key("email")}
              required
              {...form.getInputProps("email")}
            />
            <TextInput
              label="Phone Number"
              placeholder="514-123-1234"
              key={form.key("phone")}
              required
              {...form.getInputProps("phone")}
            />
            <DateInput
              rightSection={<Calendar />}
              label="Date of Birth"
              placeholder="January 1, 2000"
              maxDate={new Date()}
              key={form.key("dob")}
              required
              {...form.getInputProps("dob")}
            />
          </Stepper.Step>
          <Stepper.Step label="Address Information">
            <TextInput
              label="Address Line"
              placeholder="123 Main Street"
              key={form.key("address.streetAddress")}
              required
              {...form.getInputProps("address.streetAddress")}
            />
            <Select
              mt="md"
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
          <Stepper.Completed>
            <div className={classes.completedForm}>
              <h2>Welcome, {form.getValues().firstName}!</h2>
              The primary e-mail that will be used for communications:
              <h2>{form.getValues().email}</h2>
              Please make sure all information is correct.
            </div>
            {activeStep === 3 && (
              <div className={classes.completedForm}>
                <h2>Submission complete!</h2>
                <p>
                  Please check your inbox (sometimes in junk or spam) for a
                  confirmation!
                </p>
                <p>You may now book an event.</p>
                <Button component="a" href="/booking">
                  Back to Event Form
                </Button>
              </div>
            )}
          </Stepper.Completed>
        </Stepper>

        <Group justify="flex-end" mt="xl">
          {activeStep > 0 && activeStep < 3 && (
            <Button variant="default" onClick={previousStep}>
              Back
            </Button>
          )}
          {activeStep < 2 && <Button onClick={nextStep}>Next</Button>}
          {activeStep === 2 && <Button onClick={submit}>Submit</Button>}
        </Group>
      </div>
    );
  }
}

export default ClientRegistration;
