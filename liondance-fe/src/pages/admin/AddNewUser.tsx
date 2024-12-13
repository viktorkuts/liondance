import { useForm } from "@mantine/form";
import {
  TextInput,
  Button,
  Select,
  Group,
  Notification,
} from "@mantine/core";
import { DateInput } from "@mantine/dates";
import { useState } from "react";
import axios from "axios";
import "./AddUserForm.css";

const AddNewUser = () => {
  const [success, setSuccess] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const form = useForm({
    initialValues: {
      firstName: "",
      middleName: "",
      lastName: "",
      gender: "",
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
      firstName: (value) => (value.length > 0 ? null : "First name is required"),
      lastName: (value) => (value.length > 0 ? null : "Last name is required"),
      gender: (value) => (value ? null : "Gender is required"),
      dob: (value) => (value ? null : "Date of birth is required"),
      email: (value) =>
        /^\S+@\S+$/.test(value) ? null : "Invalid email address",
      role: (value) => (value ? null : "Role is required"),
      "address.streetAddress": (value) =>
        value ? null : "Street address is required",
      "address.city": (value) => (value ? null : "City is required"),
      "address.state": (value) => (value ? null : "State is required"),
      "address.zip": (value) =>
        /^[A-Za-z]\d[A-Za-z][ -]?\d[A-Za-z]\d$/.test(value)
          ? null
          : "Invalid postal code",
    },
  });

  const handleSubmit = async () => {
    const values = form.values;

    try {
      await axios.post(
        `http://localhost:8080/api/v1/users?role=${values.role.toUpperCase()}`,
        {
          ...values,
          address: { ...values.address },
        }
      );
      setSuccess("User created successfully. An email has been sent to the user.");
      setError(null);
      form.reset();
    } catch (err) {
      setError("Failed to create user. Please check the form for errors. " + err);
      setSuccess(null);
    }
  };

  return (
    <div className="add-new-user-container">
      <div className="add-new-user-form">
        <h1>Add New User</h1>
        {success && (
          <Notification
            color="teal"
            title="Success"
            onClose={() => setSuccess(null)}
            mt="md"
          >
            {success}
          </Notification>
        )}
        {error && (
          <Notification
            color="red"
            title="Error"
            onClose={() => setError(null)}
            mt="md"
          >
            {error}
          </Notification>
        )}
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <TextInput
            label="First Name"
            placeholder="John"
            {...form.getInputProps("firstName")}
            required
          />
          <TextInput
            label="Middle Name"
            placeholder="M."
            {...form.getInputProps("middleName")}
          />
          <TextInput
            label="Last Name"
            placeholder="Doe"
            {...form.getInputProps("lastName")}
            required
          />
          <Select
            label="Gender"
            placeholder="Select gender"
            data={["MALE", "FEMALE", "OTHER"]}
            {...form.getInputProps("gender")}
            required
          />
          <DateInput
            label="Date of Birth"
            placeholder="YYYY-MM-DD"
            {...form.getInputProps("dob")}
            required
          />
          <TextInput
            label="Email"
            placeholder="example@example.com"
            {...form.getInputProps("email")}
            required
          />
          <TextInput
            label="Phone"
            placeholder="555-555-5555"
            {...form.getInputProps("phone")}
          />
          <TextInput
            label="Street Address"
            placeholder="123 Main St"
            {...form.getInputProps("address.streetAddress")}
            required
          />
          <TextInput
            label="City"
            placeholder="City"
            {...form.getInputProps("address.city")}
            required
          />
          <TextInput
            label="State"
            placeholder="State/Province"
            {...form.getInputProps("address.state")}
            required
          />
          <TextInput
            label="Postal Code"
            placeholder="12345"
            {...form.getInputProps("address.zip")}
            required
          />
          <Select
            label="Role"
            placeholder="Select role"
            data={["STAFF", "CLIENT", "ADMIN"]}
            {...form.getInputProps("role")}
            required
          />
          <Group position="right" mt="md">
            <Button type="submit">Submit</Button>
          </Group>
        </form>
      </div>
    </div>
  );
};

export default AddNewUser;
