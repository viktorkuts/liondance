import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Button, Title, Input } from "@mantine/core";
import { useUserService } from "@/services/userService";
import { Client } from "@/models/Users";
import "./clientList.css";

const ClientList: React.FC = () => {
  const userService = useUserService();
  const [clients, setClients] = useState<Client[]>([]);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    userService.getAllClients().then((data) => {
      setClients(data);
    });
  }, [userService]);

  const filteredClients = clients.filter(client =>
    `${client.firstName} ${client.middleName || ""} ${client.lastName}`
      .toLowerCase()
      .includes(searchTerm.toLowerCase())
  );

  return (
    <div className="clientListContainer">
      <Title order={1}>Client List</Title>
      <Input
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        placeholder="Search by name"
        className="searchInput"
      />
      {filteredClients.length === 0 ? (
        <p className="noData">No clients found.</p>
      ) : (
        <table className="clientsTable">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredClients.map((client) => (
              <tr key={client.userId}>
                <td>
                  {client.firstName} {client.middleName} {client.lastName}
                </td>
                <td>{client.email}</td>
                <td>{client.phone}</td>
                <td>
                  <Link to={`/client-profile/${client.userId}`}>
                    <Button className="viewProfileButton" variant="outline">
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