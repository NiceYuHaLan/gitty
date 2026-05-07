import axios from 'axios';

const API_URL = `${import.meta.env.VITE_API_URL}/api/projects`;
const BACKEND_URL = import.meta.env.VITE_API_URL;

const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  const headers: Record<string, string> = {};
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  return { headers };
};

export interface Project {
  id: number;
  name: string;
  description: string | null;
  userId: number;
  repoUrl?: string;
  documentation?: string;
  createdAt?: string;
}

export interface CreateProjectRequest {
  name: string;
  description?: string;
  repoUrl?: string;
}

export const projectsApi = {
  getAll: async (): Promise<Project[]> => {
    const response = await axios.get<Project[]>(API_URL, getAuthHeaders());
    return response.data;
  },

  getById: async (id: number): Promise<Project> => {
    const response = await axios.get<Project>(`${API_URL}/${id}`, getAuthHeaders());
    return response.data;
  },

  create: async (request: CreateProjectRequest): Promise<Project> => {
    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    const response = await axios.post<Project>(API_URL, request, { headers });
    return response.data;
  },

  update: async (id: number, request: Partial<CreateProjectRequest>): Promise<Project> => {
    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    const response = await axios.put<Project>(`${API_URL}/${id}`, request, { headers });
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {};
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }
    await axios.delete(`${API_URL}/${id}`, { headers });
  }
};