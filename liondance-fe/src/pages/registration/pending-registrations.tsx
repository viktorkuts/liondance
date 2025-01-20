// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-nocheck
import { useEffect, useState } from "react";
import axiosInstance from "../../utils/axiosInstance.ts";
import { Student } from "@/models/Users.ts";
import "./PendingRegistrations.css";
import StudentDetailsOverlay from "../../components/StudentDetailsOverlay.tsx";

function PendingRegistrations() {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const [selectedStudent, setSelectedStudent] = useState<Student | null>(null);

  useEffect(() => {
    const fetchPendingStudents = async () => {
      try {
        const response = await axiosInstance.get<Student[]>(
          "/students/status?statuses=PENDING"
        );
        setStudents(response.data);
        console.log(response.data);
        setLoading(false);
      } catch (error) {
        setError("Failed to fetch pending registrations. " + error);
        setLoading(false);
      }
    };

    fetchPendingStudents();
  }, []);

  const handleRowClick = async (userId: string) => {
    try {
      const response = await axiosInstance.get<Student>(
        `/students/pending/${userId}`
      );
      setSelectedStudent(response.data);
    } catch (err) {
      console.error("Error fetching student details:", err);
    }
  };

  const closeOverlay = (refresh?: boolean) => {
    if (refresh) {
      setStudents(
        students.filter((student) => student.userId !== selectedStudent?.userId)
      );
    }
    setSelectedStudent(null);
  };

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
              <th>Date of Birth</th>
            </tr>
          </thead>
          <tbody>
            {students.map((student) => (
              <tr
                key={student.userId}
                onClick={() => handleRowClick(student.userId)}
                style={{ cursor: "pointer" }}
              >
                <td>
                  {student.firstName} {student.lastName}
                </td>
                <td>{student.email}</td>
                <td>{new Date(student.dob).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {selectedStudent && (
        <StudentDetailsOverlay
          student={selectedStudent}
          onClose={closeOverlay}
        />
      )}
    </div>
  );
}

export default PendingRegistrations;
