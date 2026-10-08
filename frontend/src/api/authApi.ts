import { apiClient } from './client';
import type { LoginRequest, RegisterRequest } from '../types/auth'; // I will add these types to auth.ts

export const authApi = {
  login: async (data: LoginRequest) => {
    const response = await apiClient.post<{ token: string }>('/api/v1/auth/login', data);
    return response.data;
  },

  register: async (data: RegisterRequest) => {
    const response = await apiClient.post<string>('/api/v1/auth/register', data);
    return response.data;
  },
};
