import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Button, Title, MultiSelect } from '@mantine/core';
import studentService from '../services/userService';
import { Student } from '../models/Users';
import './studentList.css';
import './loader.css';

const StudentList: React.FC = () => {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(true);
  const [statuses, setStatuses] = useState<string[]>([]);

  useEffect(() => {
    setLoading(true);
    if (statuses.length > 0) {
      studentService.getStudentsByStatuses(statuses).then(data => {
        setStudents(data);
        setLoading(false);
      });
    } else {
      studentService.getAllStudents().then(data => {
        setStudents(data);
        setLoading(false);
      });
    }
  }, [statuses]);

  return (
    <div className="user-list">
      <Title order={1}>Student List</Title>
      <MultiSelect
        data={['ACTIVE', 'INACTIVE']}
        value={statuses}
        onChange={setStatuses}
        placeholder="Select statuses"
        label="Filter by Status"
      />
      {loading ? (
        <div className="custom-loader"></div>
      ) : students.length === 0 ? (
        <p className="no-data">No students found.</p>
      ) : (
        <table className="users-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Gender</th>
              <th>Date of Birth</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {students.map(student => (
              <tr key={student.userId}>
                <td>{student.firstName} {student.middleName} {student.lastName}</td>
                <td>{student.email}</td>
                <td>{student.gender}</td>
                <td>{new Date(student.dob).toLocaleDateString()}</td>
                <td>{student.registrationStatus}</td>
                <td>
                  <Link to={`/student-profile/${student.userId}`}>
                    <Button className="view-profile-button" variant="outline">View Profile</Button>
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