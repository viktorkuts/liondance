import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button, Title, Text, Loader } from '@mantine/core';
import userService from '../services/userService.ts';
import { User } from '../models/Users';
import './userProfile.css';

const UserProfile: React.FC = () => {
  const { userId } = useParams<{ userId: string }>();
  const navigate = useNavigate();
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    if (userId) {
      userService.getUserProfile(userId).then(data => setUser(data));
    }
  }, [userId]);

  if (!user) {
    return <Loader />;
  }

  return (
    <div className="user-profile">
      <Title order={1}>{user.firstName} {user.middleName} {user.lastName}</Title>
      <Text size="lg">Gender: {user.gender}</Text>
      <Text size="lg">Date of Birth: {user.dob}</Text>
      <Text size="lg">Email: {user.email}</Text>
      {user.phone && <Text size="lg">Phone: {user.phone}</Text>}
      <Text size="lg">Address: {user.address.streetAddress}, {user.address.city}, {user.address.state}, {user.address.zip}</Text>
      <Button variant="outline" onClick={() => navigate('/users')}>Back to User List</Button>
    </div>
  );
};

export default UserProfile;