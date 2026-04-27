import './TopBar.css';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';

interface TopBarProps {
  onAddProject?: () => void;
  onOpenProfile?: () => void;
}

export function TopBar({ onAddProject, onOpenProfile }: TopBarProps) {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
    setIsMenuOpen(false);
  };

  const handleProfileClick = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const closeMenu = () => setIsMenuOpen(false);

  return (
    <header className="topbar">
      <div className="topbar-left">
        <h1 className="app-title" onClick={() => navigate('/')} style={{ cursor: 'pointer' }}>
          Gitty
        </h1>
      </div>
      
      <div className="topbar-right">
        {isAuthenticated ? (
          <div className="profile-menu">
            <span className="username">{user?.username}</span>
            <div className="profile-icon-wrapper" onClick={handleProfileClick}>
              <div className="profile-icon">
                <img 
                  src={`https://ui-avatars.com/api/?name=${user?.username}&background=6366f1&color=fff`} 
                  alt="Profile" 
                />
              </div>
              
              {isMenuOpen && (
                <div className="profile-dropdown">
                  <div className="dropdown-item" onClick={() => {
                    closeMenu();
                    onOpenProfile?.();
                  }}>
                    <svg className="dropdown-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                      <circle cx="12" cy="7" r="4" />
                    </svg>
                    <span>Профиль</span>
                  </div>
                  <div className="dropdown-divider" />
                  <div className="dropdown-item" onClick={handleLogout}>
                    <svg className="dropdown-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                      <polyline points="16 17 21 12 16 7" />
                      <line x1="21" y1="12" x2="9" y2="12" />
                    </svg>
                    <span>Выйти</span>
                  </div>
                </div>
              )}
            </div>
          </div>
        ) : (
          <button className="login-btn" onClick={() => navigate('/login')}>
            Войти
          </button>
        )}
      </div>
      
      {isMenuOpen && (
        <div className="menu-overlay" onClick={closeMenu} />
      )}
    </header>
  );
}