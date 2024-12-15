import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Loader, Button, Autocomplete, Select, TextInput } from '@mantine/core';
import userService from '../services/userService';
import geoService from '@/services/geoService';
import { Gender, Address, Student } from "@/models/Users.ts";
import './studentProfile.css';
import { useForm } from "@mantine/form";
import { Province } from "@/types/geo";
import { Calendar } from "react-feather";
import { DateInput } from "@mantine/dates";

interface StudentResponseModel {
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

const StudentProfile: React.FC = () => {
  const { studentId } = useParams<{ studentId: string }>();
  const [student, setStudent] = useState<StudentResponseModel | null>(null);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const [provinces, setProvinces] = useState<Province[]>(geoService.provincesCache);
  const [cityData, setCityData] = useState<string[]>([]);
  const [cityDataLoading, setCityDataLoading] = useState<boolean>(false);
  const [isEditing, setIsEditing] = useState(false);
  const [selectedProvince, setSelectedProvince] = useState<string>("");

  useEffect(() => {
    const fetchStudent = async () => {
      try {
        const data = await userService.getStudentProfile(studentId!);
        setStudent(data);
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

    fetchStudent();
  }, [studentId]);

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
      gender: (value) => (["MALE", "FEMALE", "OTHER"].includes(value) ? null : "Invalid gender"),
      // eslint-disable-next-line
      // @ts-ignore
      dob: (value) => (value instanceof Date && value <= new Date() ? null : "Invalid date"),
      email: (value) => (/^\S+@\S+\.\S+$/.test(value) && value.length <= 100 ? null : "Invalid email"),
      phone: (value) => (/^\d{3}-\d{3}-\d{4}$/.test(value) ? null : "Invalid phone number"),
      address: {
        streetAddress: (value) => (value.length <= 100 ? null : "Street address too long"),
        city: (value) => (value.length <= 50 ? null : "City name too long"),
        state: (value) => (provincesFormatted().includes(value) ? null : "Invalid province"),
        zip: (value) => /^[A-Za-z]\d[A-Za-z][ -]?\d[A-Za-z]\d$/.test(value) ? null : "Invalid postal code",
      },
    },
  });

  useEffect(() => {
    const value = form.values.address.state;
    const province = provinces.find((provinceObj) => provinceObj.name === value);
    setSelectedProvince(province ? province.code : "");
  }, [form.values.address.state, provinces]);

  useEffect(() => {
    const fetchCities = async () => {
      if (form.values.address.city) {
        setCityDataLoading(true);
        const results = await geoService.getCities(form.values.address.city, selectedProvince);
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
      const newStudent: Student = {
        firstName: values.firstName,
        middleName: values.middleName,
        lastName: values.lastName,
        gender: values.gender as Gender,
        dob: new Date(values.dob).toISOString().split('T')[0],
        email: values.email,
        phone: values.phone,
        address: values.address,
      };

      if (student?.userId) {
        await userService.updateStudent(student.userId, newStudent);
        setIsEditing(false);
        window.location.reload();
      }
    }
  };

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  if (!student) {
    return <Loader className="loading" />;
  }

  return (
    <div className="student-profile">
      <h1>Student Profile</h1>
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
            <Button type="submit">Update Student</Button>
            <Button onClick={() => setIsEditing(false)}>Cancel</Button>
          </div>
        </form>
      ) : (
        <div className="student-details">
          <p><strong>Name:</strong> {student.firstName} {student.middleName} {student.lastName}</p>
          <p><strong>Gender:</strong> {student.gender}</p>
          <p><strong>Date of Birth:</strong> {student.dob}</p>
          <p><strong>Email:</strong> {student.email}</p>
          <p><strong>Phone:</strong> {student.phone}</p>
          <p><strong>Address:</strong> {student.address.streetAddress}, {student.address.city}, {student.address.state} {student.address.zip}</p>
          <div className="button-container">
            <Button onClick={() => navigate('/students')} className="back-button">Back to Student List</Button>
            <Button onClick={() => setIsEditing(true)} className="edit-button">Edit Student</Button>
          </div>
        </div>
      )}
    </div>
  );
};

export default StudentProfile;