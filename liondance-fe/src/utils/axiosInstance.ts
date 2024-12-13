import { useAuth0 } from "@auth0/auth0-react";
import axios, { AxiosInstance } from "axios";

export const useAxiosInstance = (): AxiosInstance => {
  const { getAccessTokenSilently } = useAuth0();
  const instance = axios.create({
    baseURL:
    (/^\d+$/.test(window.location.host.split(".")[0])
      ? import.meta.env.BACKEND_URL.replace(
          "://",
          `://${window.location.host.split(".")[0]}.`
        )
      : import.meta.env.BACKEND_URL) + "/api/v1",
    headers: {
      "Content-Type": "application/json",
    },
  });

  instance.interceptors.request.use(async (config) => {
    const token = await getAccessTokenSilently();
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  });

  instance.interceptors.response.use((response) => response);

  return instance;
};
