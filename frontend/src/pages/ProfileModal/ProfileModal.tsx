import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { userApi, type UserProfile } from '../../api/userApi';
import { Modal } from '../../components/Modal/Modal';
import toast from 'react-hot-toast';
import './ProfileModal.css';

interface ProfileModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export function ProfileModal({ isOpen, onClose }: ProfileModalProps) {
  const { login, logout } = useAuth();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
  });

  useEffect(() => {
    if (isOpen) {
      loadProfile();
    }
  }, [isOpen]);

  const loadProfile = async () => {
    const token = localStorage.getItem('token');
    if (!token) {
      toast.error('Сессия истекла. Войдите снова.');
      onClose();
      return;
    }
    
    try {
      const data = await userApi.getCurrentUser();
      setFormData({
        username: data.username || '',
        email: data.email || '',
      });
    } catch (error: any) {
      if (error.message === 'No token' || error.response?.status === 401) {
        toast.error('Сессия истекла. Войдите снова.');
        logout();
      } else {
        toast.error('Не удалось загрузить профиль');
      }
      onClose();
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    const token = localStorage.getItem('token');
    if (!token) {
      toast.error('Сессия истекла. Войдите снова.');
      logout();
      return;
    }
    
    setLoading(true);

    try {
      const updatedUser = await userApi.updateProfile({
        username: formData.username || undefined,
        email: formData.email || undefined,
      });
      
      if (updatedUser.token) {
        localStorage.setItem('token', updatedUser.token);
        
        const userData = {
          username: updatedUser.username,
          userId: updatedUser.id,
        };
        
        localStorage.setItem('user', JSON.stringify(userData));
        login(updatedUser.token, userData);
      }
      
      toast.success('Профиль обновлён!');
      onClose();
      window.location.reload();
      
    } catch (error: any) {
      if (error.response?.status === 401) {
        toast.error('Сессия истекла. Войдите снова.');
        logout();
      } else {
        toast.error(error.response?.data?.message || 'Ошибка сохранения');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    onClose();
    toast.success('Вы вышли из системы');
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Профиль">
      <form onSubmit={handleSubmit} className="profile-form">
        <div className="form-group">
          <label>Имя пользователя</label>
          <input
            type="text"
            value={formData.username}
            onChange={(e) => setFormData({ ...formData, username: e.target.value })}
          />
        </div>

        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          />
        </div>

        <div className="form-actions">
          <button type="submit" className="btn-save" disabled={loading}>
            {loading ? '...' : 'Сохранить'}
          </button>
          <button type="button" className="btn-logout" onClick={handleLogout}>
            Выйти
          </button>
        </div>
      </form>
    </Modal>
  );
}