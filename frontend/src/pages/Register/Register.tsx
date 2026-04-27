import './Register.css';
import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { authApi } from '../../api/authApi';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';  // ← ← ← Добавили

export function Register() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      toast.error('Пароли не совпадают');  
      return;
    }

    if (password.length < 6) {
      toast.error('Пароль должен быть не менее 6 символов');  
      return;
    }

    setLoading(true);

    try {
      const response = await authApi.register({ username, password, email: email || undefined });
      login(response.token, { username: response.username, userId: response.userId });
      toast.success('Аккаунт успешно создан!');
      navigate('/');
    } catch (err: any) {
      toast.error(err.response?.status === 400 
        ? 'Пользователь с таким именем уже существует' 
        : 'Ошибка регистрации. Попробуйте позже.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-page">
      <div className="register-container">
        <div className="register-header">
          <h1>Gitty</h1>
          <p>Создайте аккаунт</p>
        </div>

        <form onSubmit={handleSubmit} className="register-form">
          {error && <div className="error-message">{error}</div>}

          <div className="form-group">
            <label htmlFor="username">Имя пользователя</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Придумайте имя пользователя"
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Email (необязательно)</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="example@mail.com"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Пароль</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Придумайте пароль"
              required
              minLength={6}
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Подтвердите пароль</label>
            <input
              type="password"
              id="confirmPassword"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Повторите пароль"
              required
              disabled={loading}
            />
          </div>

          <button type="submit" className="btn-submit" disabled={loading}>
            {loading ? 'Регистрация...' : 'Зарегистрироваться'}
          </button>
        </form>

        <div className="register-footer">
          <p>Уже есть аккаунт? <a href="/login">Войти</a></p>
        </div>
      </div>
    </div>
  );
}