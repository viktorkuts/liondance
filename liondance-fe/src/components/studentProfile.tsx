import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import userService from '../services/userService';
import './studentProfile.css';

interface StudentResponseModel {
    userId: string;
    firstName: string;
    middleName?: string;
    lastName: string;
    gender: string;
    dob: string;
    email: string;
    phone: string;
    address: {
        street: string;
        city: string;
        state: string;
        zip: string;
    };
}

const StudentProfile: React.FC = () => {
    const { studentId } = useParams<{ studentId: string }>();
    const [student, setStudent] = useState<StudentResponseModel | null>(null);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchStudent = async () => {
            try {
                const data = await userService.getStudentProfile(studentId);
                setStudent(data);
            } catch (err: any) {
                setError(err.message);
            }
        };

        fetchStudent();
    }, [studentId]);

    if (error) {
        return <div className="error">Error: {error}</div>;
    }

    if (!student) {
        return <div className="loading">Loading...</div>;
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
                <p><strong>Address:</strong> {student.address.street}, {student.address.city}, {student.address.state} {student.address.zip}</p>
            </div>
            <button onClick={() => navigate('/students')} className="back-button">Back to Student List</button>
        </div>
    );
};

export default StudentProfile;