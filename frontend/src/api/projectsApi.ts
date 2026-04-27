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

export const getImageUrl = (imageUrl: string): string => {
  if (imageUrl.startsWith('http')) return imageUrl;
  return `${BACKEND_URL}${imageUrl}`;
};

export interface Project {
  id: number;
  name: string;
  imageUrl: string;
  description: string | null;
  userId: number;
  repoUrl?: string;
  documentation?: string;
  createdAt?: string;
}

export interface CreateProjectRequest {
  name: string;
  description?: string;
  image?: File;
  repoUrl?: string;
}

export const projectsApi = {
  getAll: async (): Promise<Project[]> => {
    const response = await axios.get<Project[]>(API_URL, getAuthHeaders());
    return response.data.map(project => ({
      ...project,
      imageUrl: getImageUrl(project.imageUrl)
    }));
  },

  getById: async (id: number): Promise<Project> => {
    const response = await axios.get<Project>(`${API_URL}/${id}`, getAuthHeaders());
    const project = response.data;
    return {
      ...project,
      imageUrl: getImageUrl(project.imageUrl)
    };
  },

  create: async (request: CreateProjectRequest): Promise<Project> => {
    const formData = new FormData();
    formData.append('name', request.name);
    if (request.description) formData.append('description', request.description);
    if (request.repoUrl) formData.append('repoUrl', request.repoUrl);
    if (request.image) formData.append('image', request.image);

    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {};
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    const response = await axios.post<Project>(API_URL, formData, { headers });
    const project = response.data;
    return {
      ...project,
      imageUrl: getImageUrl(project.imageUrl)
    };
  },

  update: async (id: number, request: Partial<CreateProjectRequest>): Promise<Project> => {
    const formData = new FormData();
    if (request.name) formData.append('name', request.name);
    if (request.description !== undefined) formData.append('description', request.description || '');
    if (request.repoUrl !== undefined) formData.append('repoUrl', request.repoUrl || '');
    if (request.image) formData.append('image', request.image);

    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {};
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    const response = await axios.put<Project>(`${API_URL}/${id}`, formData, { headers });
    const project = response.data;
    return {
      ...project,
      imageUrl: getImageUrl(project.imageUrl)
    };
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