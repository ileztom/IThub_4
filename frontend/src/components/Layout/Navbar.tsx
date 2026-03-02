import React, { useEffect, useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { notificationApi } from '../../api/notificationApi';

const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    if (user) {
      notificationApi.countUnread(user.id)
        .then(setUnreadCount)
        .catch(() => {});
    }
  }, [user]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <NavLink to="/books" className="navbar-brand">📚 Библиотека</NavLink>
      <div className="navbar-links">
        <NavLink to="/books" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>
          Каталог
        </NavLink>
        {isAuthenticated && (
          <>
            <NavLink to="/loans" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>
              Мои книги
            </NavLink>
            <NavLink to="/notifications" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>
              Уведомления
              {unreadCount > 0 && <span className="nav-badge">{unreadCount}</span>}
            </NavLink>
            {(user?.role === 'ADMIN' || user?.role === 'LIBRARIAN') && (
              <NavLink to="/admin" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>
                Управление
              </NavLink>
            )}
            <NavLink to="/profile" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>
              {user?.fullName || user?.username}
            </NavLink>
            <button className="btn-logout" onClick={handleLogout}>Выйти</button>
          </>
        )}
        {!isAuthenticated && (
          <>
            <NavLink to="/login" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>Войти</NavLink>
            <NavLink to="/register" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>Регистрация</NavLink>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
