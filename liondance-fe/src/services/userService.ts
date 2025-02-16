// userService.ts
import {
  Student,
  User,
  RegistrationStatusModel,
  Role,
  Client,
} from "@/models/Users.ts";
import { useAxiosInstance } from "@/utils/axiosInstance";
import { AxiosResponse } from "axios";

export const useUserService = () => {
  const axiosInstance = useAxiosInstance();

  const getAllUsers = async () => {
    const response = await axiosInstance.get("/users");
    return response;
  };

  const getUserProfile = async (userId: string) => {
    const response = await axiosInstance.get(`/users/${userId}`);
    return response.data;
  };

  const getStudentProfile = async (studentId: string) => {
    const response = await axiosInstance.get(`/students/${studentId}`);
    return response.data;
  };

  const getPendingStudentById = async (userId: string) => {
    const response = await axiosInstance.get(`/students/pending/${userId}`);
    return response.data;
  };

  const updateUser = async (
    userId: string,
    user: User
  ): Promise<AxiosResponse<User>> => {
    return await axiosInstance.put<User>(`/users/${userId}`, user);
  };

  const registerStudent = async (
    student: Student
  ): Promise<AxiosResponse<Student>> => {
    return await axiosInstance.post<Student>("/students", student);
  };

  const getAllStudents = async () => {
    const response = await axiosInstance.get("/students");
    return response.data;
  };

  const getStudentsByStatuses = async (
    statuses: string[]
  ): Promise<Student[]> => {
    try {
      const response = await axiosInstance.get<Student[]>("/students/status", {
        params: { statuses },
        paramsSerializer: (params) => {
          return params.statuses
            .map((status: string) => `statuses=${status}`)
            .join("&");
        },
      });
      return response.data;
    } catch (error) {
      console.error("Error fetching students by statuses:", error);
      throw error;
    }
  };

  const updateRegistrationStatus = async (
    userId: string,
    registrationStatusModel: RegistrationStatusModel
  ): Promise<AxiosResponse<Student>> => {
    return await axiosInstance.patch<Student>(
      `/students/${userId}/registration-status`,
      registrationStatusModel
    );
  };

  const updateStudent = async (
    studentId: string,
    student: Student
  ): Promise<AxiosResponse<Student>> => {
    return await axiosInstance.put<Student>(`/students/${studentId}`, student);
  };

  const updateUserRoles = async (
    userId: string,
    roles: Role[]
  ): Promise<AxiosResponse<Role[]>> => {
    return await axiosInstance.patch<Role[]>(`/users/${userId}/role`, {
      roles,
    });
  };

  const userRoles = async (): Promise<AxiosResponse<Role[]>> => {
    return await axiosInstance.get<Role[]>(`/users/current-user/roles`);
  };

  const getSessionUser = async (): Promise<AxiosResponse<User>> => {
    return await axiosInstance.get<User>(`/users/authenticated-user`);
  };

  const getSessionRoles = async (): Promise<AxiosResponse<Role[]>> => {
    return await axiosInstance.get<Role[]>(`/users/authenticated-user/roles`);
  };

  const getAllClients = async () => {
    const response = await axiosInstance.get("/clients");
    return response.data;
  };

  const registerClient = async (
    client: Client
  ): Promise<AxiosResponse<Client>> => {
    return await axiosInstance.post<Student>("/clients", client);
  };

  const getClientProfile = async (clientId: string): Promise<Client> => {
    const response = await axiosInstance.get<Client>(`/clients/${clientId}`);
    return response.data;
  };

  const registerUser = async (user: User): Promise<AxiosResponse<User>> => {
    if (!user.roles) throw "No user roles!";
    return await axiosInstance.post<User>(
      `/api/v1/users?role=${user.roles.map((e) => e.toUpperCase()).toString()}`,
      user
    );
  };

  const linkUserAccount = async (userId: string): Promise<AxiosResponse<User>> => {
    return await axiosInstance.patch<User>(`/users/${userId}/link-account`);
  };

  const subscribeToPromotions = async (
    userId: string,
    isSubscribed: boolean
  ): Promise<User> => {
    const response = await axiosInstance.patch<User>(
      `/users/${userId}/subscription`, 
      { isSubscribed }
    );
    return response.data;
  };

  return {
    getAllUsers,
    getUserProfile,
    getStudentProfile,
    getPendingStudentById,
    updateUser,
    registerStudent,
    getAllStudents,
    getStudentsByStatuses,
    updateRegistrationStatus,
    updateStudent,
    updateUserRoles,
    userRoles,
    getSessionUser,
    getSessionRoles,
    getAllClients,
    registerClient,
    getClientProfile,
    registerUser,
    linkUserAccount,
    subscribeToPromotions,
  };
};