import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Button, Title, MultiSelect } from "@mantine/core";
import { useUserService } from "../services/userService";
import { Student } from "../models/Users";
import { useTranslation } from "react-i18next";
import "./studentList.css";
import "./loader.css";

const StudentList: React.FC = () => {
  const { t } = useTranslation();
  const studentService = useUserService();
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(true);
  const [statuses, setStatuses] = useState<string[]>([]);

  useEffect(() => {
    setLoading(true);
    if (statuses.length > 0) {
      studentService.getStudentsByStatuses(statuses).then((data) => {
        setStudents(data);
        setLoading(false);
      });
    } else {
      studentService.getAllStudents().then((data) => {
        setStudents(data);
        setLoading(false);
      });
    }
  }, [statuses, studentService]);

  return (
    <div className="user-list">
      <Title order={1}>{t('Student List')}</Title>
      <MultiSelect
        data={[t('ACTIVE'), t('INACTIVE')]}
        value={statuses}
        onChange={setStatuses}
        placeholder={t('Select statuses')}
        label={t('Filter by Status')}
      />
      {loading ? (
        <div className="custom-loader"></div>
      ) : students.length === 0 ? (
        <p className="no-data">{t('No students found.')}</p>
      ) : (
        <table className="users-table">
          <thead>
            <tr>
              <th>#</th>
              <th>{t('Name')}</th>
              <th>{t('Email')}</th>
              <th>{t('Date of Birth')}</th>
              <th>{t('Status')}</th>
              <th>{t('Actions')}</th>
            </tr>
          </thead>
          <tbody>
            {students.map((student, index) => (
              <tr key={student.userId}>
                <td>{index + 1}</td>
                <td>
                  {student.firstName} {student.middleName} {student.lastName}
                </td>
                <td>{student.email}</td>
                <td>{new Date(student.dob).toLocaleDateString()}</td>
                <td>{student.registrationStatus}</td>
                <td>
                  <Link to={`/student-profile/${student.userId}`}>
                    <Button className="view-profile-button" variant="outline">
                      {t('View Profile')}
                    </Button>
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default StudentList;
