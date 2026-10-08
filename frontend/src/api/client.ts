import axios, { AxiosError } from 'axios';
import { useAuthStore } from '../store/useAuthStore';

// Create base instance
export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to attach JWT token
apiClient.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token;
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle global errors (e.g., 401 Unauthorized)
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Clear token and user if unauthorized
      useAuthStore.getState().logout();
      // We can also redirect to login by emitting an event or relying on React Router, 
      // but Zustand state change will trigger re-render of protected routes automatically.
    }
    return Promise.reject(error);
  }
);
