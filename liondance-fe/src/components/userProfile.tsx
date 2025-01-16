import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Loader,
  Button,
  Autocomplete,
  Select,
  TextInput,
  Modal,
  MultiSelect,
  Notification,
} from "@mantine/core";
import userService from "../services/userService";
import geoService from "@/services/geoService";
import { Gender, Address, User } from "@/models/Users.ts";
import "./userProfile.css";
import { useForm } from "@mantine/form";
import { Province } from "@/types/geo";
import { Calendar } from "react-feather";
import { DateInput } from "@mantine/dates";

interface UserResponseModel {
  userId: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  gender: Gender;
  dob: string;
  email: string;
  phone?: string;
  address: Address;
}

const UserProfile: React.FC = () => {
  const { userId } = useParams<{ userId: string }>();
  const [user, setUser] = useState<UserResponseModel | null>(null);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const [provinces, setProvinces] = useState<Province[]>(
    geoService.provincesCache
  );
  const [cityData, setCityData] = useState<string[]>([]);
  const [cityDataLoading, setCityDataLoading] = useState<boolean>(false);
  const [isEditing, setIsEditing] = useState(false);
  const [selectedProvince, setSelectedProvince] = useState<string>("");
  const [roleModalOpened, setRoleModalOpened] = useState(false);
  const [roles, setRoles] = useState<string[]>([]);
  const [notification, setNotification] = useState<{
    type: "success" | "error";
    message: string;
  } | null>(null);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const data = await userService.getUserProfile(userId!);
        setUser(data);
        form.setValues({
          firstName: data.firstName,
          middleName: data.middleName,
          lastName: data.lastName,
          gender: data.gender,
          // eslint-disable-next-line
          // @ts-ignore
          dob: new Date(data.dob),
          email: data.email,
          phone: data.phone,
          address: {
            streetAddress: data.address.streetAddress,
            city: data.address.city,
            state: data.address.state,
            zip: data.address.zip,
          },
        });
      } catch (err: unknown) {
        setError(err instanceof Error ? err.message : String(err));
      }
    };

    fetchUser();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  useEffect(() => {
    const loadProvinces = async () => {
      const fetchedProvinces = await geoService.provinces();
      setProvinces(fetchedProvinces);
    };
    loadProvinces();
  }, []);

  const provincesFormatted = () => provinces.map((val) => val.name);

  const form = useForm({
    initialValues: {
      firstName: "",
      middleName: "",
      lastName: "",
      gender: "",
      dob: "",
      email: "",
      phone: "",
      address: {
        streetAddress: "",
        city: "",
        state: "",
        zip: "",
      },
    },
    validate: {
      firstName: (value) => (value.length <= 50 ? null : "First name too long"),
      lastName: (value) => (value.length <= 50 ? null : "Last name too long"),
      gender: (value) =>
        ["MALE", "FEMALE", "OTHER"].includes(value) ? null : "Invalid gender",
      // eslint-disable-next-line
      // @ts-ignore
      dob: (value) =>
        value instanceof Date && value <= new Date() ? null : "Invalid date",
      email: (value) =>
        /^\S+@\S+\.\S+$/.test(value) && value.length <= 100
          ? null
          : "Invalid email",
      phone: (value) =>
        /^\d{3}-\d{3}-\d{4}$/.test(value) ? null : "Invalid phone number",
      address: {
        streetAddress: (value) =>
          value.length <= 100 ? null : "Street address too long",
        city: (value) => (value.length <= 50 ? null : "City name too long"),
        state: (value) =>
          provincesFormatted().includes(value) ? null : "Invalid province",
        zip: (value) =>
          /^[A-Za-z]\d[A-Za-z][ -]?\d[A-Za-z]\d$/.test(value)
            ? null
            : "Invalid postal code",
      },
    },
  });

  useEffect(() => {
    const value = form.values.address.state;
    const province = provinces.find(
      (provinceObj) => provinceObj.name === value
    );
    setSelectedProvince(province ? province.code : "");
  }, [form.values.address.state, provinces]);

  useEffect(() => {
    const fetchCities = async () => {
      if (form.values.address.city) {
        setCityDataLoading(true);
        const results = await geoService.getCities(
          form.values.address.city,
          selectedProvince
        );
        setCityData(results.map((city) => city.name));
        setCityDataLoading(false);
      }
    };

    fetchCities();
  }, [form.values.address.city, selectedProvince]);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    const isValid = form.validate();
    if (!isValid.hasErrors) {
      const values = form.values;
      const newUser: User = {
        firstName: values.firstName,
        middleName: values.middleName,
        lastName: values.lastName,
        gender: values.gender as Gender,
        dob: new Date(values.dob).toISOString().split("T")[0],
        email: values.email,
        phone: values.phone,
        address: values.address,
      };

      if (user?.userId) {
        await userService.updateStudent(user.userId, newUser);
        setIsEditing(false);
        window.location.reload();
      }
    }
  };

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  if (!user) {
    return <Loader className="loading" />;
  }

  const handleRoleUpdate = async () => {
    try {
      await userService.updateUserRoles(userId!, roles);
      setNotification({
        type: "success",
        message: "Roles updated successfully.",
      });
      setRoleModalOpened(false);
    } catch {
      setNotification({
        type: "error",
        message: "Failed to update roles. Please try again.",
      });
    }
  };

  return (
    <div className="user-profile">
      <h1>User Profile</h1>
      {isEditing ? (
        <form onSubmit={handleSubmit} className="form-container">
          <TextInput
            label="First Name"
            placeholder="John"
            required
            {...form.getInputProps("firstName")}
          />
          <TextInput
            label="Middle Name"
            placeholder="Z."
            {...form.getInputProps("middleName")}
          />
          <TextInput
            label="Last Name"
            placeholder="Pork"
            required
            {...form.getInputProps("lastName")}
          />
          <Select
            label="Gender"
            required
            data={["MALE", "FEMALE", "OTHER"]}
            {...form.getInputProps("gender")}
          />
          <DateInput
            rightSection={<Calendar />}
            label="Date of Birth"
            placeholder="January 1, 2000"
            required
            maxDate={new Date()}
            {...form.getInputProps("dob")}
          />
          <TextInput
            label="Email"
            placeholder="john.doe@example.com"
            required
            {...form.getInputProps("email")}
          />
          <TextInput
            label="Phone Number"
            placeholder="123-123-1234"
            required
            {...form.getInputProps("phone")}
          />
          <TextInput
            label="Street Address"
            placeholder="123 Main Street"
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
            required
            {...form.getInputProps("address.zip")}
          />
          <div className="button-container">
            <Button type="submit">Update User</Button>
            <Button onClick={() => setIsEditing(false)}>Cancel</Button>
          </div>
        </form>
      ) : (
        <div className="user-details">
          <p>
            <strong>Name:</strong> {user.firstName} {user.middleName}{" "}
            {user.lastName}
          </p>
          <p>
            <strong>Gender:</strong> {user.gender}
          </p>
          <p>
            <strong>Date of Birth:</strong> {user.dob}
          </p>
          <p>
            <strong>Email:</strong> {user.email}
          </p>
          <p>
            <strong>Phone:</strong> {user.phone}
          </p>
          <p>
            <strong>Address:</strong> {user.address.streetAddress},{" "}
            {user.address.city}, {user.address.state} {user.address.zip}
          </p>
          <div className="button-container">
            <Button onClick={() => navigate("/users")} className="back-button">
              Back to User List
            </Button>
            <Button onClick={() => setIsEditing(true)} className="edit-button">
              Edit User
            </Button>
            <Button onClick={() => setRoleModalOpened(true)}>
              Change Roles
            </Button>
          </div>
          <Modal
            opened={roleModalOpened}
            onClose={() => setRoleModalOpened(false)}
            title="Change Roles"
          >
            <MultiSelect
              label="Select Roles"
              data={["STAFF", "CLIENT", "ADMIN", "STUDENT"]}
              value={roles}
              onChange={setRoles}
            />
            <Button mt="md" onClick={handleRoleUpdate}>
              Save Roles
            </Button>
          </Modal>
          {notification && (
            <Notification
              color={notification.type === "success" ? "teal" : "red"}
            >
              {notification.message}
            </Notification>
          )}
        </div>
      )}
    </div>
  );
};

export default UserProfile;
