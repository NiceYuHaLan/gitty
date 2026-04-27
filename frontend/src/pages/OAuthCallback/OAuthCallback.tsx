import { useEffect, useRef } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import './OAuthCallback.css';

export function OAuthCallback() {
  const [searchParams] = useSearchParams();
  const { login } = useAuth();
  const navigate = useNavigate();
  const hasProcessed = useRef(false);

  useEffect(() => {
    if (hasProcessed.current) {
      return;
    }

    const token = searchParams.get('token');
    const username = searchParams.get('username');
    const userId = searchParams.get('userId');

    console.log('OAuth Callback:', { token, username, userId });

    if (token && username && userId) {
      hasProcessed.current = true; 
      
      login(token, { username, userId: parseInt(userId) });
      
      console.log('Login successful, redirecting to dashboard');
      
      navigate('/');
    } else {
      hasProcessed.current = true; 
      
      console.error('OAuth failed: missing parameters');
      navigate('/login?error=oauth_failed');
    }
  }, [searchParams]);

  return (
    <div className="oauth-callback">
      <div className="loading-spinner">
        <div className="spinner"></div>
        <p>Завершение входа через GitHub...</p>
      </div>
    </div>
  );
}