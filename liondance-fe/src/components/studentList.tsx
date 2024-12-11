import React, { useEffect, useState } from 'react';
import { Title } from '@mantine/core';
import studentService from '../services/userService';
import { Student } from '../models/Users';
import './userList.css';
import './loader.css';

const StudentList: React.FC = () => {
 const [students, setStudents] = useState<Student[]>([]);
 const [loading, setLoading] = useState(true);

 useEffect(() => {
  studentService.getAllStudents().then(data => {
   setStudents(data);
   setLoading(false);
  });
 }, []);

 return (
  <div className="user-list">
   <Title order={1}>Student List</Title>
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
       <td>
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