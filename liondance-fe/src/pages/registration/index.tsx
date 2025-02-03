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
import { Calendar } from "react-feather";
import geoService from "@/services/geoService";
import { Province } from "@/types/geo";
import { Student } from "@/models/Users";
import { useUserService } from "@/services/userService";
import { useTranslation } from "react-i18next";
import i18n from "i18next";

function Registration() {
  const { t } = useTranslation();
  const userService = useUserService();
  const [emailData, setEmailData] = useState<string[]>([]);
  const [emailDataLoading, setEmailDataLoading] = useState<boolean>(false);
  const [emailParentData, setEmailParentData] = useState<string[]>([]);
  const [emailParentDataLoading, setEmailParentDataLoading] =
    useState<boolean>(false);
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
      parentFirstName: "",
      parentMiddleName: "",
      parentLastName: "",
      parentEmail: "",
      parentPhone: "",
    },

    validate: {
      firstName: (value) => (value.length > 0 ? null : t("Field is required")),
      lastName: (value) => (value.length > 0 ? null : t("Field is required")),
      dob: (value) => {
        if (typeof value !== "object") {
          return t("Field is required");
        } else if ((value as Date) > new Date()) {
          return t("Cannot be set in the future!");
        }
      },
      email: (value) => (/^\S+@\S+$/.test(value) ? null : t("Invalid email")),
      address:
        (activeStep === 1 && {
          streetAddress: (value) =>
            value.length > 0 ? null : t("Field is required"),
          state: (value) => (value.length > 0 ? null : t("Field is required")),
          city: (value) => (value.length > 0 ? null : t("Field is required")),
          zip: (value) =>
            /^[A-Za-z]\d[A-Za-z][ -]?\d[A-Za-z]\d$/.test(value)
              ? null
              : t("Field is invalid"),
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

  form.watch("parentEmail", ({ value }) => {
    setEmailParentDataLoading(true);

    if (value.trim().length === 0 || value.includes("@")) {
      setEmailParentDataLoading(false);
    } else {
      setEmailDataLoading(true);
      setEmailParentData(
        ["gmail.com", "outlook.com", "hotmail.com", "yahoo.com"].map(
          (prov) => `${value}@${prov}`
        )
      );
      setEmailParentDataLoading(false);
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
    const newStudent: Student = {
      firstName: values.firstName,
      middleName: values.middleName,
      lastName: values.lastName,
      dob: values.dob,
      email: values.email,
      phone: values.phone,
      address: values.address,
      parentFirstName: values.parentFirstName,
      parentMiddleName: values.parentMiddleName,
      parentLastName: values.parentLastName,
      parentEmail: values.parentEmail,
      parentPhone: values.parentPhone,
    };

    const run = async () => {
      await userService.registerStudent(newStudent);
    };
    run();
  };

  return (
    <div className={classes.registrationForm}>
      <h1>{t("Student Registration Form")}</h1>
      <Stepper active={activeStep}>
        <Stepper.Step label={t("Student Information")}>
          <TextInput
            label={t("Student First Name")}
            placeholder="John"
            key={form.key("firstName")}
            required
            {...form.getInputProps("firstName")}
          />
          <TextInput
            label={t("Student Middle Name")}
            placeholder="Z."
            key={form.key("middleName")}
            {...form.getInputProps("middleName")}
          />
          <TextInput
            label={t("Student Last Name")}
            placeholder="Doe"
            key={form.key("lastName")}
            required
            {...form.getInputProps("lastName")}
          />
          <Autocomplete
            rightSection={emailDataLoading ? <Loader size={12} /> : null}
            data={emailData}
            label={t("Primary E-Mail")}
            placeholder="john.doe@example.com"
            key={form.key("email")}
            required
            {...form.getInputProps("email")}
          />
          <TextInput
            label={t("Student Phone Number")}
            placeholder="514-123-1234"
            key={form.key("phone")}
            {...form.getInputProps("phone")}
          />
          <DateInput
            rightSection={<Calendar />}
            label={t("Student Date of Birth")}
            placeholder={t("January 1, 2000")}
            maxDate={new Date()}
            key={form.key("dob")}
            required
            locale={i18n.language}
            {...form.getInputProps("dob")}
          />
        </Stepper.Step>
        <Stepper.Step label={t("Address Information")}>
          <TextInput
            label={t("Address Line")}
            placeholder="123 Main Street"
            key={form.key("address.streetAddress")}
            required
            {...form.getInputProps("address.streetAddress")}
          />
          <Select
            mt="md"
            label={t("Province")}
            placeholder="Quebec"
            comboboxProps={{ withinPortal: true }}
            data={provincesFormatted()}
            key={form.key("address.state")}
            required
            {...form.getInputProps("address.state")}
          />
          <Autocomplete
            label={t("City")}
            rightSection={cityDataLoading ? <Loader size={12} /> : null}
            data={cityData}
            placeholder="Montreal"
            key={form.key("address.city")}
            limit={10}
            required
            {...form.getInputProps("address.city")}
          />
          <TextInput
            label={t("Postal Code")}
            placeholder="H1H 1H1"
            key={form.key("address.zip")}
            required
            {...form.getInputProps("address.zip")}
          />
        </Stepper.Step>
        <Stepper.Step label={t("Parent Information")} description={t("Optional")}>
          <TextInput
            label={t("Parent First Name")}
            placeholder="Jack"
            key={form.key("parentFirstName")}
            {...form.getInputProps("parentFirstName")}
          />
          <TextInput
            label={t("Parent Middle Name")}
            placeholder="A."
            key={form.key("parentMiddleName")}
            {...form.getInputProps("parentMiddleName")}
          />
          <TextInput
            label={t("Parent Last Name")}
            placeholder="Dode"
            key={form.key("parentLastName")}
            {...form.getInputProps("parentLastName")}
          />
          <Autocomplete
            rightSection={emailParentDataLoading ? <Loader size={12} /> : null}
            data={emailParentData}
            label={t("Parent E-Mail")}
            placeholder="jack.dode@example.com"
            key={form.key("parentEmail")}
            {...form.getInputProps("parentEmail")}
          />
          <TextInput
            label={t("Parent Phone Number")}
            placeholder="514-123-1234"
            key={form.key("parentPhone")}
            {...form.getInputProps("parentPhone")}
          />
        </Stepper.Step>
        <Stepper.Completed>
          <div className={classes.completedForm}>
            <h2>{t("Welcome,")} {form.getValues().firstName}!</h2>
            {t("The primary e-mail that will be used for communications:")}
            <h2>{form.getValues().email}</h2>
            {t("Please make sure all information is correct.")}
          </div>
          {activeStep === 4 && (
            <div className={classes.completedForm}>
              <h2>{t("Submission complete!")}</h2>
              <p>
                {t("Please check your inbox (sometimes in junk or spam) for a confirmation!")}
              </p>
              <Button component="a" href="/">
                {t("Back to Home")}
              </Button>
            </div>
          )}
        </Stepper.Completed>
      </Stepper>

      <Group justify="flex-end" mt="xl">
        {activeStep > 0 && activeStep < 4 && (
          <Button variant="default" onClick={previousStep}>
            {t("Back")}
          </Button>
        )}
        {activeStep < 3 && <Button onClick={nextStep}>{t("Next")}</Button>}
        {activeStep === 3 && <Button onClick={submit}>{t("Submit")}</Button>}
      </Group>
    </div>
  );
}

export default Registration;
