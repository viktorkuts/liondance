import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Button, Title, Input } from "@mantine/core";
import { useUserService } from "@/services/userService";
import { Client } from "@/models/Users";
import { useTranslation } from "react-i18next";
import "./clientList.css";

const ClientList: React.FC = () => {
  const { t } = useTranslation();
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
      <Title order={1}>{t('Client List')}</Title>
      <Input
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        placeholder={t('Search by name')}
        className="searchInput"
      />
      {filteredClients.length === 0 ? (
        <p className="noData">{t('No clients found.')}</p>
      ) : (
        <table className="clientsTable">
          <thead>
            <tr>
              <th>#</th>
              <th>{t('Name')}</th>
              <th>{t('Email')}</th>
              <th>{t('Phone')}</th>
              <th>{t('Actions')}</th>
            </tr>
          </thead>
          <tbody>
            {filteredClients.map((client, index) => (
              <tr key={client.userId}>
                <td>{index + 1}</td>
                <td>
                  {client.firstName} {client.middleName} {client.lastName}
                </td>
                <td>{client.email}</td>
                <td>{client.phone}</td>
                <td>
                  <Link to={`/client-profile/${client.userId}`}>
                    <Button className="viewProfileButton" variant="outline">
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

export default ClientList;