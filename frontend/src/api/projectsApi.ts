import axios from 'axios';

const API_URL = `${import.meta.env.VITE_API_URL}/api/projects`;
const BACKEND_URL = import.meta.env.VITE_API_URL;

const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  return { headers };
};

export const getImageUrl = (imageUrl: string): string => {
  if (imageUrl?.startsWith('http')) return imageUrl;
  return `${BACKEND_URL}${imageUrl}`;
};

export interface Project {
  id: number;
  name: string;
  imageUrl?: string;
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
  // image убрали
}

export const projectsApi = {
  getAll: async (): Promise<Project[]> => {
    const response = await axios.get<Project[]>(API_URL, getAuthHeaders());
    return response.data.map(project => ({
      ...project,
      imageUrl: project.imageUrl ? getImageUrl(project.imageUrl) : undefined,
    }));
  },

  getById: async (id: number): Promise<Project> => {
    const response = await axios.get<Project>(`${API_URL}/${id}`, getAuthHeaders());
    const project = response.data;
    return {
      ...project,
      imageUrl: project.imageUrl ? getImageUrl(project.imageUrl) : undefined,
    };
  },

  create: async (request: CreateProjectRequest): Promise<Project> => {
    // Отправляем JSON, а не FormData
    const response = await axios.post<Project>(API_URL, request, getAuthHeaders());
    const project = response.data;
    return {
      ...project,
      imageUrl: project.imageUrl ? getImageUrl(project.imageUrl) : undefined,
    };
  },

  update: async (id: number, request: Partial<CreateProjectRequest>): Promise<Project> => {
    const response = await axios.put<Project>(`${API_URL}/${id}`, request, getAuthHeaders());
    const project = response.data;
    return {
      ...project,
      imageUrl: project.imageUrl ? getImageUrl(project.imageUrl) : undefined,
    };
  },

    syncCommits: async (projectId: number): Promise<{ synced: number }> => {
      const response = await axios.post<{ synced: number }>(
        `${API_URL}/${projectId}/sync`,
        {},
        getAuthHeaders()
      );
      return response.data;
    },

  delete: async (id: number): Promise<void> => {
    await axios.delete(`${API_URL}/${id}`, getAuthHeaders());
  },
};