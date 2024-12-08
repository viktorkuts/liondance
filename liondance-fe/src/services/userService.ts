import axiosInstance from '../utils/axiosInstance';
import {User} from "@/models/Users.ts";
import {AxiosResponse} from "axios";

const getAllUsers = async () => {
  const response = await axiosInstance.get('/users');
  return response.data;
};

const getUserProfile = async (userId: string) => {
  const response = await axiosInstance.get(`/users/${userId}`);
  return response.data;
};

export const getPendingStudentById = async (userId: string) => {
  const response = await axiosInstance.get(`/students/pending/${userId}`);
  return response.data;
};

export const updateUser = async (
 userId: string,
 user: User
): Promise<AxiosResponse<User>> => {
  return await axiosInstance.put<User>(`/users/${userId}`, user);
};

export default {
  getAllUsers,
  getUserProfile,
  getPendingStudentById,
  
};