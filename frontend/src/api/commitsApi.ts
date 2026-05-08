import axios from 'axios';

const API_URL = `${import.meta.env.VITE_API_URL}/api/commits`;

const getAuthHeaders = () => ({ headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } });

export interface Commit {
  id: number;
  sha: string;
  authorName: string;
  authorEmail: string;
  commitDate: string;
  message: string;
  url: string;
  processed: boolean;
  analysis?: {
    summary: string;
    sentiment: string;
    tags: string;
    risks: string;
  };
}

export const commitsApi = {
  getByProject: async (projectId: number): Promise<Commit[]> => {
    const response = await axios.get<Commit[]>(`${API_URL}/project/${projectId}`, getAuthHeaders());
    return response.data;
  },
  sync: async (projectId: number): Promise<{ synced: number }> => {
    const response = await axios.post<{ synced: number }>(
      `${import.meta.env.VITE_API_URL}/api/projects/${projectId}/sync`,
      {},
      getAuthHeaders()
    );
    return response.data;
  },
};