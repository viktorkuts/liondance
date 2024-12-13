import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Loader, Modal, Button, Autocomplete, Select, TextInput } from '@mantine/core';
import userService from '../services/userService';
import geoService from '@/services/geoService';
import { Gender, RegistrationStatus, Student, Address } from "@/models/Users.ts";
import './studentProfile.css';
import { useForm } from "@mantine/form";
import { Province } from "@/types/geo";
import {Calendar} from "react-feather";
import {DateInput} from "@mantine/dates";

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
    joinDate?: string;
    registrationStatus?: RegistrationStatus;
    parentFirstName?: string;
    parentMiddleName?: string;
    parentLastName?: string;
    parentEmail?: string;
    parentPhone?: string;
}

const StudentProfile: React.FC = () => {
    const { studentId } = useParams<{ studentId: string }>();
    const [student, setStudent] = useState<StudentResponseModel | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [modalOpened, setModalOpened] = useState(false);
    const navigate = useNavigate();
    const [provinces, setProvinces] = useState<Province[]>(geoService.provincesCache);
    const [cityData, setCityData] = useState<string[]>([]);
    const [cityDataLoading, setCityDataLoading] = useState<boolean>(false);
    const [selectedProvince, setSelectedProvince] = useState<string>(student?.address?.state ?? "");

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
                    parentFirstName: data.parentFirstName,
                    parentMiddleName: data.parentMiddleName,
                    parentLastName: data.parentLastName,
                    parentEmail: data.parentEmail,
                    parentPhone: data.parentPhone,
                });
            } catch (err: unknown) {
                if (err instanceof Error) {
                    setError(err.message);
                } else {
                    setError(String(err));
                }
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

    const provincesFormatted = () => {
        return provinces.map((val) => {
            return val.name;
        });
    };

    const form = useForm({
        mode: "controlled",
        initialValues: {
            firstName: student?.firstName ?? "",
            middleName: student?.middleName ?? "",
            lastName: student?.lastName ?? "",
            gender: student?.gender ?? "",
            dob: student?.dob ?? "",
            email: student?.email ?? "",
            phone: student?.phone ?? "",
            address: {
                streetAddress: student?.address?.streetAddress ?? "",
                city: student?.address?.city ?? "",
                state: student?.address?.state ?? "",
                zip: student?.address?.zip ?? "",
            },
            parentFirstName: student?.parentFirstName ?? "",
            parentMiddleName: student?.parentMiddleName ?? "",
            parentLastName: student?.parentLastName ?? "",
            parentEmail: student?.parentEmail ?? "",
            parentPhone: student?.parentPhone ?? "",
        },

        validate: {
            firstName: (value) => (value.length > 0 ? null : "Field is required"),
            lastName: (value) => (value.length > 0 ? null : "Field is required"),
            gender: (value) => (value.length > 0 ? null : "Field is required"),
            // eslint-disable-next-line
            // @ts-ignore
            dob: (value) => (value instanceof Date && value <= new Date() ? null : "Invalid date of birth"),
            email: (value) => (/^\S+@\S+$/.test(value) ? null : "Invalid email format"),
            address: {
                streetAddress: (value) => (value.length > 0 ? null : "Field is required"),
                state: (value) => (value.length > 0 ? null : "Field is required"),
                city: (value) => (value.length > 0 ? null : "Field is required"),
                zip: (value) => /^[A-Za-z]\d[A-Za-z][ -]?\d[A-Za-z]\d$/.test(value) ? null : "Invalid postal code",
            },
        },
    });

    form.watch("address.state", ({ value }) => {
        const province = provinces.find((provinceObj) => provinceObj.name === value);
        setSelectedProvince(province ? province.code : "");
    });

    form.watch("address.city", ({ value }) => {
        setCityDataLoading(true);
        const fetchCities = async () => {
            const results = await geoService.getCities(value, selectedProvince);
            setCityData(results.map((city) => city.name));
            setCityDataLoading(false);
        };
        fetchCities();
    });

    const handleSubmit = async () => {
        const values = form.getValues();
        const newStudent: Student = {
            firstName: values.firstName,
            middleName: values.middleName,
            lastName: values.lastName,
            gender: values.gender as Gender,
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

        if (student?.userId) {
            userService.updateStudent(student.userId, newStudent);
        }
    };

    if (error) {
        return <div className="error">Error: {error}</div>;
    }

    if (!student) {
        return <Loader />;
    }

    return (
     <div className="student-profile">
         <h1>Student Profile</h1>
         <div className="student-details">
             <p><strong>Name:</strong> {student.firstName} {student.middleName} {student.lastName}</p>
             <p><strong>Gender:</strong> {student.gender}</p>
             <p><strong>Date of Birth:</strong> {student.dob}</p>
             <p><strong>Email:</strong> {student.email}</p>
             <p><strong>Phone:</strong> {student.phone}</p>
             <p><strong>Address:</strong> {student.address.streetAddress}, {student.address.city}, {student.address.state} {student.address.zip}</p>
         </div>
         <button onClick={() => navigate('/students')} className="back-button">Back to Student List</button>
         <Button onClick={() => setModalOpened(true)} className="edit-button">Edit Student</Button>
         <Modal opened={modalOpened} onClose={() => setModalOpened(false)} title="Edit Student Profile">
             <div>
                 <h1>Update Student Information</h1>
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
                  placeholder="514-123-1234"
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
                 <Button onClick={async () => { await handleSubmit(); setModalOpened(false); window.location.reload(); }}>Update Student</Button>
                 <Button onClick={() => setModalOpened(false)}>Cancel</Button>
             </div>
         </Modal>
     </div>
    );
};

export default StudentProfile;