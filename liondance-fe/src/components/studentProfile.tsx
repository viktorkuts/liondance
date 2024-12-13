import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button, Loader, Text, Title } from '@mantine/core';
import userService from '../services/userService';
import './studentProfile.css';
import './modalAnimation.css';
import {Gender, RegistrationStatus, Student, Address} from "@/models/Users.ts";

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
    const [isEditing, setIsEditing] = useState(false);
    const [updatedStudent, setUpdatedStudent] = useState<StudentResponseModel | null>(null);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchStudent = async () => {
            try {
                const data = await userService.getStudentProfile(studentId!);
                setStudent(data);
                setUpdatedStudent(data);
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

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setUpdatedStudent((prevState) => ({
            ...prevState!,
            [name]: value,
        }));
    };

    const handleAddressChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setUpdatedStudent((prevState) => ({
            ...prevState!,
            address: {
                ...prevState!.address,
                [name]: value,
            },
        }));
    };

    const handleUpdateStudent = async () => {
        if (!updatedStudent || !studentId) return;
        try {
            await userService.updateStudent(studentId, updatedStudent as unknown as Student);
            setIsEditing(false);
            setStudent(updatedStudent);
        } catch (error) {
            console.error("Error updating student:", error);
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
            <Title order={1}>
                {student.firstName} {student.middleName} {student.lastName}
            </Title>
            <Text size="lg">Gender: {student.gender}</Text>
            <Text size="lg">Date of Birth: {student.dob}</Text>
            <Text size="lg">Email: {student.email}</Text>
            {student.phone && <Text size="lg">Phone: {student.phone}</Text>}
            <Text size="lg">
                Address: {student.address.streetAddress}, {student.address.city}, {student.address.state}, {student.address.zip}
            </Text>
            <div className="button-container">
                <Button variant="outline" onClick={() => setIsEditing(!isEditing)}>
                    {isEditing ? "Cancel" : "Edit Student"}
                </Button>
                <Button variant="outline" onClick={() => navigate('/students')}>
                    Back to Student List
                </Button>
            </div>
            {isEditing && (
                <>
                    <div className="modal-overlay"></div>
                    <div className="modal">
                        <div className="modal-content">
                            <h2>Update Student</h2>
                            <input
                                type="text"
                                name="firstName"
                                value={updatedStudent!.firstName}
                                onChange={handleChange}
                                placeholder="First Name"
                            />
                            <input
                                type="text"
                                name="middleName"
                                value={updatedStudent!.middleName}
                                onChange={handleChange}
                                placeholder="Middle Name"
                            />
                            <input
                                type="text"
                                name="lastName"
                                value={updatedStudent!.lastName}
                                onChange={handleChange}
                                placeholder="Last Name"
                            />
                            <input
                                type="email"
                                name="email"
                                value={updatedStudent!.email}
                                onChange={handleChange}
                                placeholder="Email"
                            />
                            <input
                                type="date"
                                name="dob"
                                value={updatedStudent!.dob}
                                onChange={handleChange}
                                placeholder="Date of Birth"
                            />
                            <input
                                type="text"
                                name="gender"
                                value={updatedStudent!.gender}
                                onChange={handleChange}
                                placeholder="Gender"
                            />
                            <input
                                type="text"
                                name="phone"
                                value={updatedStudent!.phone}
                                onChange={handleChange}
                                placeholder="Phone"
                            />
                            <input
                                type="text"
                                name="street"
                                value={updatedStudent!.address.streetAddress}
                                onChange={handleAddressChange}
                                placeholder="Street Address"
                            />
                            <input
                                type="text"
                                name="city"
                                value={updatedStudent!.address.city}
                                onChange={handleAddressChange}
                                placeholder="City"
                            />
                            <input
                                type="text"
                                name="state"
                                value={updatedStudent!.address.state}
                                onChange={handleAddressChange}
                                placeholder="State"
                            />
                            <input
                                type="text"
                                name="zip"
                                value={updatedStudent!.address.zip}
                                onChange={handleAddressChange}
                                placeholder="Zip"
                            />
                            <button onClick={handleUpdateStudent}>Confirm</button>
                            <button onClick={() => setIsEditing(false)}>Cancel</button>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
};

export default StudentProfile;