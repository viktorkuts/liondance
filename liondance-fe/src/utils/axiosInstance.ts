import axios from "axios";

const axiosInstance = axios.create({
  baseURL: import.meta.env.BACKEND_URL + "/api/v1",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
});

export default axiosInstance;
