// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-nocheck
import { useForm } from "@mantine/form";
import { TextInput, Button, Select, Group, Notification } from "@mantine/core";
import { DateInput } from "@mantine/dates";
import { useState } from "react";
import { useUserService } from "@/services/userService";
import { useTranslation } from "react-i18next";
import "./AddUserForm.css";
import { User } from "@/models/Users";

const AddNewUser = () => {
  const { t } = useTranslation();
  const [success, setSuccess] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const userService = useUserService();

  const form = useForm({
    initialValues: {
      firstName: "",
      middleName: "",
      lastName: "",
      dob: null,
      email: "",
      phone: "",
      address: {
        streetAddress: "",
        city: "",
        state: "",
        zip: "",
      },
      role: "",
    },

    validate: {
      firstName: (value) =>
        value.length > 0 ? null : t("First name is required"),
      lastName: (value) =>
        value.length > 0 ? null : t("Last name is required"),
      dob: (value) => (value ? null : t("Date of birth is required")),
      email: (value) =>
        /^\S+@\S+$/.test(value) ? null : t("Invalid email address"),
      role: (value) => (value ? null : t("Role is required")),
      "address.streetAddress": (value) =>
        value ? null : t("Street address is required"),
      "address.city": (value) => (value ? null : t("City is required")),
      "address.state": (value) => (value ? null : t("State is required")),
      "address.zip": (value) =>
        /^[A-Za-z]\d[A-Za-z][ -]?\d[A-Za-z]\d$/.test(value)
          ? null
          : t("Invalid postal code"),
    },
  });

  const handleSubmit = async () => {
    const values = form.values;
    const newUser: User = {
      firstName: values.firstName,
      middleName: values.middleName,
      lastName: values.lastName,
      address: values.address,
      dob: values.dob,
      email: values.email,
      phone: values.phone,
      roles: [values.role],
    };

    try {
      userService.registerUser(newUser);
      setSuccess(
        t("User created successfully. An email has been sent to the user.")
      );
      setError(null);
      form.reset();
    } catch (err) {
      setError(
        t("Failed to create user. Please check the form for errors.") +
          " " +
          err
      );
      setSuccess(null);
    }
  };

  return (
    <div className="add-new-user-container">
      <div className="add-new-user-form">
        <h1>{t("Add New User")}</h1>
        {success && (
          <Notification
            color="teal"
            title={t("Success")}
            onClose={() => setSuccess(null)}
            mt="md"
          >
            {success}
          </Notification>
        )}
        {error && (
          <Notification
            color="red"
            title={t("Error")}
            onClose={() => setError(null)}
            mt="md"
          >
            {error}
          </Notification>
        )}
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <TextInput
            label={t("First Name")}
            placeholder={t("John")}
            {...form.getInputProps("firstName")}
            required
          />
          <TextInput
            label={t("Middle Name")}
            placeholder={t("M.")}
            {...form.getInputProps("middleName")}
          />
          <TextInput
            label={t("Last Name")}
            placeholder={t("Doe")}
            {...form.getInputProps("lastName")}
            required
          />
          <DateInput
            label={t("Date of Birth")}
            placeholder={t("YYYY-MM-DD")}
            {...form.getInputProps("dob")}
            required
          />
          <TextInput
            label={t("Email")}
            placeholder={t("example@example.com")}
            {...form.getInputProps("email")}
            required
          />
          <TextInput
            label={t("Phone")}
            placeholder={t("555-555-5555")}
            {...form.getInputProps("phone")}
          />
          <TextInput
            label={t("Street Address")}
            placeholder={t("123 Main St")}
            {...form.getInputProps("address.streetAddress")}
            required
          />
          <TextInput
            label={t("City")}
            placeholder={t("City")}
            {...form.getInputProps("address.city")}
            required
          />
          <TextInput
            label={t("State")}
            placeholder={t("State/Province")}
            {...form.getInputProps("address.state")}
            required
          />
          <TextInput
            label={t("Postal Code")}
            placeholder={t("12345")}
            {...form.getInputProps("address.zip")}
            required
          />
          <Select
            label={t("Role")}
            placeholder={t("Select role")}
            data={[t("STAFF"), t("CLIENT"), t("ADMIN")]}
            {...form.getInputProps("role")}
            required
          />
          <Group position="right" mt="md">
            <Button type="submit">{t("Submit")}</Button>
          </Group>
        </form>
      </div>
    </div>
  );
};

export default AddNewUser;
