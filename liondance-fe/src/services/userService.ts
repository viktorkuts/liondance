import axiosInstance from '../utils/axiosInstance';

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


export default {
  getAllUsers,
  getUserProfile,
  getPendingStudentById,
};