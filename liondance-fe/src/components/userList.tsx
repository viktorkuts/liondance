import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Button, Title } from '@mantine/core';
import userService from '../services/userService';
import { User } from '../models/Users';
import './userList.css';
import './loader.css';

const UserList: React.FC = () => {
 const [users, setUsers] = useState<User[]>([]);
 const [loading, setLoading] = useState(true);

 useEffect(() => {
  userService.getAllUsers().then(data => {
   setUsers(data);
   setLoading(false);
  });
 }, []);

 return (
  <div className="user-list">
   <Title order={1}>User List</Title>
      <Link to={`/add-new-user`}>
       <Button className="view-profile-button" variant="outline">Add New User</Button>
      </Link>
   {loading ? (
    <div className="custom-loader"></div>
   ) : users.length === 0 ? (
    <p className="no-data">No users found.</p>
   ) : (
    <table className="users-table">
     <thead>
     <tr>
      <th>Name</th>
      <th>Email</th>
      <th>Date of Birth</th>
      <th>Actions</th>
     </tr>
     </thead>
     <tbody>
     {users.map(user => (
      <tr key={user.userId}>
       <td>{user.firstName} {user.middleName} {user.lastName}</td>
       <td>{user.email}</td>
       <td>{new Date(user.dob).toLocaleDateString()}</td>
       <td>
        <Link to={`/profile/${user.userId}`}>
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

export default UserList;