import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Button, Title } from "@mantine/core";
import { useUserService } from "@/services/userService";
import { Client } from "@/models/Users";
import "./clientList.css";

const ClientList: React.FC = () => {
  const userService = useUserService();
  const [clients, setClients] = useState<Client[]>([]);

  useEffect(() => {
    userService.getAllClients().then(setClients);
  }, [userService]);

  return (
    <div className="user-list">
      <Title order={1}>Client List</Title>
      {clients.length === 0 ? (
        <p className="no-data">No clients found.</p>
      ) : (
        <table className="users-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {clients.map((client) => (
              <tr key={client.userId}>
                <td>
                  {client.firstName} {client.middleName} {client.lastName}
                </td>
                <td>{client.email}</td>
                <td>{client.phone}</td>
                <td>
                  <Link to={`/client-profile/${client.userId}`}>
                    <Button className="view-profile-button" variant="outline">
                      View Profile
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

export default ClientList;
