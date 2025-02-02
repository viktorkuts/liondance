import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Button, Title } from "@mantine/core";
import { useUserService } from "../services/userService";
import { User } from "../models/Users";
import { useTranslation } from "react-i18next";
import "./userList.css";
import "./loader.css";

const UserList: React.FC = () => {
  const { t } = useTranslation();
  const userService = useUserService();
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    userService.getAllUsers().then((data) => {
      setUsers(data);
      setLoading(false);
    });
  }, [userService]);

  return (
    <div className="user-list">
      <Title order={1}>{t('User List')}</Title>
      <Link to={`/add-new-user`}>
        <Button className="view-profile-button" variant="outline">{t('Add New User')}</Button>
      </Link>
      {loading ? (
        <div className="custom-loader"></div>
      ) : users.length === 0 ? (
        <p className="no-data">{t('No users found.')}</p>
      ) : (
        <table className="users-table">
          <thead>
            <tr>
              <th>{t('Name')}</th>
              <th>{t('Email')}</th>
              <th>{t('Date of Birth')}</th>
              <th>{t('Actions')}</th>
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
                    <Button className="view-profile-button" variant="outline">{t('View Profile')}</Button>
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
