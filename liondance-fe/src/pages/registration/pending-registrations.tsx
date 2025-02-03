// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-nocheck
import { useEffect, useState } from "react";
import { useAxiosInstance } from "../../utils/axiosInstance.ts";
import { Student } from "@/models/Users.ts";
import "./PendingRegistrations.css";
import StudentDetailsOverlay from "../../components/StudentDetailsOverlay.tsx";
import { useTranslation } from "react-i18next";

function PendingRegistrations() {
  const { t } = useTranslation();
  const axiosInstance = useAxiosInstance();
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
        setLoading(false);
      } catch (error) {
        setError(t("Failed to fetch pending registrations. ") + error);
        setLoading(false);
      }
    };

    fetchPendingStudents();
  }, [axiosInstance, t]);

  const handleRowClick = async (userId: string) => {
    try {
      const response = await axiosInstance.get<Student>(
        `/students/pending/${userId}`
      );
      setSelectedStudent(response.data);
    } catch (err) {
      console.error(t("Error fetching student details:"), err);
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

  if (loading) return <div className="loading">{t("Loading...")}</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="pending-registrations">
      <h1>{t("Pending Registrations")}</h1>
      {students.length === 0 ? (
        <p className="no-data">{t("No pending registrations found.")}</p>
      ) : (
        <table className="students-table">
          <thead>
            <tr>
              <th>{t("Name")}</th>
              <th>{t("Email")}</th>
              <th>{t("Date of Birth")}</th>
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
