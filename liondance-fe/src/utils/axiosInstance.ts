import axios from "axios";

const axiosInstance = axios.create({
  baseURL:
    (/^\d+$/.test(window.location.host.split(".")[0])
      ? window.location.host.split(".")[0] + import.meta.env.BACKEND_URL
      : import.meta.env.BACKEND_URL) + "/api/v1",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
});

export default axiosInstance;
