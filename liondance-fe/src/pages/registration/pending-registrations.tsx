import React, { useEffect, useState } from "react";
import axiosInstance from "@/utils/axiosInstance";
import { Student } from "@/models/Users";
import "./PendingRegistrations.css";

function PendingRegistrations() {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");

  useEffect(() => {
    const fetchPendingStudents = async () => {
      try {
        const response = await axiosInstance.get<Student[]>(
          "/students/status?statuses=PENDING"
        );
        setStudents(response.data);
        setLoading(false);
      } catch (error) {
        setError("Failed to fetch pending registrations. " + error);
        setLoading(false);
      }
    };

    fetchPendingStudents();
  }, []);

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="pending-registrations">
      <h1>Pending Registrations</h1>
      {students.length === 0 ? (
        <p className="no-data">No pending registrations found.</p>
      ) : (
        <table className="students-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Gender</th>
              <th>Date of Birth</th>
            </tr>
          </thead>
          <tbody>
            {students.map((student) => (
              <tr key={student.userId}>
                <td>
                  {student.firstName} {student.lastName}
                </td>
                <td>{student.email}</td>
                <td>{student.gender}</td>
                <td>{new Date(student.dob).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default PendingRegistrations;