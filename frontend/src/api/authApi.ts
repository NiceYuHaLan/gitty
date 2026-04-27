import axios from 'axios';

const API_URL = `${import.meta.env.VITE_API_URL}/api/auth`;

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email?: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  userId: number;
}

export const authApi = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await axios.post(`${API_URL}/login`, credentials);
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await axios.post(`${API_URL}/register`, data);
    return response.data;
  },
};