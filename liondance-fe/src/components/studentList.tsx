import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Button, Title, MultiSelect } from "@mantine/core";
import { useUserService } from "../services/userService";
import { Student } from "../models/Users";
import { useTranslation } from "react-i18next";
import "./studentList.css";

const StudentList: React.FC = () => {
  const { t } = useTranslation();
  const studentService = useUserService();
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(true);
  const [statuses, setStatuses] = useState<string[]>([]);
  
  useEffect(() => {
    let isMounted = true;

    const fetchStudents = async () => {
      setLoading(true);
      try {
        let data;
        if (statuses.length > 0) {
          data = await studentService.getStudentsByStatuses(statuses);
        } else {
          data = await studentService.getAllStudents();
        }
        if (isMounted) setStudents(data);
      } catch (error) {
        console.error("Failed to fetch students", error);
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    fetchStudents();

    return () => {
      isMounted = false; 
    };
  }, [statuses]);

  return (
    <div className="user-list">
      <Title order={1}>{t('Student List')}</Title>
      <Link to={`/add-new-student`}>
        <Button className="view-profile-button" variant="outline">
          {t('Add New Student')}
        </Button>
      </Link>
      <MultiSelect
        data={[t('ACTIVE'), t('INACTIVE')]}
        value={statuses}
        onChange={setStatuses}
        placeholder={t('Select statuses')}
        label={t('Filter by Status')}
      />
      {loading ? (
        <p className="no-data">{t('Loading...')}</p>
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
              <th>{t('Registration Status')}</th>
              <th>{t('Actions')}</th>
            </tr>
          </thead>
          <tbody>
            {students.map((student, index) => (
              <tr key={student.userId}>
                <td>{index + 1}</td>
                <td>{student.firstName} {student.middleName} {student.lastName}</td>
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
