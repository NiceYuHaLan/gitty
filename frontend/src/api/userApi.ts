import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  };
};

export interface UserProfile {
  id: number;
  username: string;
  email: string | null;
  githubId: string | null;
  githubAvatar: string | null;
  provider: string | null;
  token?: string;
}

export const userApi = {
  getCurrentUser: async (): Promise<UserProfile> => {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('No token');
    }
    
    const response = await axios.get(`${API_URL}/api/user/me`, getAuthHeaders());
    return response.data;
  },

  updateProfile: async (data: Partial<UserProfile>): Promise<UserProfile> => {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('No token');
    }
    
    const response = await axios.put(`${API_URL}/api/user/me`, data, getAuthHeaders());
    return response.data;
  },
};