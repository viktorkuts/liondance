import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Button, Title, Text, Loader } from "@mantine/core";
import userService from "../services/userService";
import { User } from "../models/Users";
import "./userProfile.css";
import "./modalAnimation.css";

const UserProfile: React.FC = () => {
  const { userId } = useParams<{ userId: string }>();
  const navigate = useNavigate();
  const [user, setUser] = useState<User | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [updatedUser, setUpdatedUser] = useState<User | null>(null);

  useEffect(() => {
    if (userId) {
      userService.getUserProfile(userId).then((data) => {
        setUser(data);
        setUpdatedUser(data);
      });
    }
  }, [userId]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setUpdatedUser((prevState) => ({
      ...prevState!,
      [name]: value,
    }));
  };

  const handleAddressChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setUpdatedUser((prevState) => ({
      ...prevState!,
      address: {
        ...prevState!.address,
        [name]: value,
      },
    }));
  };

  const handleUpdateUser = async () => {
    if (!updatedUser || !userId) return;
    try {
      await userService.updateUser(userId, updatedUser);
      setIsEditing(false);
      setUser(updatedUser);
    } catch (error) {
      console.error("Error updating user:", error);
    }
  };

  if (!user) {
    return <Loader />;
  }

  return (
    <div className="user-profile">
      <Title order={1}>
        {user.firstName} {user.middleName} {user.lastName}
      </Title>
      <Text size="lg">Gender: {user.gender}</Text>
      <Text size="lg">Date of Birth: {user.dob}</Text>
      <Text size="lg">Email: {user.email}</Text>
      {user.phone && <Text size="lg">Phone: {user.phone}</Text>}
      <Text size="lg">
        Address: {user.address.streetAddress}, {user.address.city},{" "}
        {user.address.state}, {user.address.zip}
      </Text>
      <div className="button-container">
        <Button variant="outline" onClick={() => setIsEditing(!isEditing)}>
          {isEditing ? "Cancel" : "Edit Profile"}
        </Button>
        <Button variant="outline" onClick={() => navigate("/users")}>
          Back to User List
        </Button>
      </div>
      {isEditing && (
        <>
          <div className="modal-overlay"></div>
          <div className="modal">
            <div className="modal-content">
              <h2>Update User</h2>
              <input
                type="text"
                name="firstName"
                value={updatedUser!.firstName}
                onChange={handleChange}
                placeholder="First Name"
              />
              <input
                type="text"
                name="middleName"
                value={updatedUser!.middleName}
                onChange={handleChange}
                placeholder="Middle Name"
              />
              <input
                type="text"
                name="lastName"
                value={updatedUser!.lastName}
                onChange={handleChange}
                placeholder="Last Name"
              />
              <input
                type="email"
                name="email"
                value={updatedUser!.email}
                onChange={handleChange}
                placeholder="Email"
              />
              <input
                type="date"
                name="dob"
                value={updatedUser!.dob}
                onChange={handleChange}
                placeholder="Date of Birth"
              />
              <input
                type="text"
                name="gender"
                value={updatedUser!.gender}
                onChange={handleChange}
                placeholder="Gender"
              />
              <input
                type="text"
                name="phone"
                value={updatedUser!.phone}
                onChange={handleChange}
                placeholder="Phone"
              />
              <input
                type="text"
                name="streetAddress"
                value={updatedUser!.address.streetAddress}
                onChange={handleAddressChange}
                placeholder="Street Address"
              />
              <input
                type="text"
                name="city"
                value={updatedUser!.address.city}
                onChange={handleAddressChange}
                placeholder="City"
              />
              <input
                type="text"
                name="state"
                value={updatedUser!.address.state}
                onChange={handleAddressChange}
                placeholder="State"
              />
              <input
                type="text"
                name="zip"
                value={updatedUser!.address.zip}
                onChange={handleAddressChange}
                placeholder="Zip"
              />
              <button onClick={handleUpdateUser}>Confirm</button>
              <button onClick={() => setIsEditing(false)}>Cancel</button>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default UserProfile;
