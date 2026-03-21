import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

function Navbar() {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const isActive = (path) => {
    return location.pathname === path ? 'active' : '';
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-brand">
          <span className="brand-icon">🏥</span>
          CRM São Rafael
        </Link>
        
        <div className="navbar-menu">
          <Link to="/" className={`nav-link ${isActive('/')}`}>
            🏠 Início
          </Link>
          <Link to="/dashboard" className={`nav-link ${isActive('/dashboard')}`}>
            📊 Dashboard
          </Link>
          <Link to="/health" className={`nav-link ${isActive('/health')}`}>
            🏥 Health Check
          </Link>
          <Link to="/services" className={`nav-link ${isActive('/services')}`}>
            ⚙️ Serviços
          </Link>
          <Link to="/customers" className={`nav-link ${isActive('/customers')}`}>
            👥 Clientes
          </Link>
        </div>

        <div className="navbar-user">
          <div className="user-info">
            <span className="user-name">{user?.firstName || user?.username}</span>
            <span className="user-role">
              {user?.roles?.includes('admin') ? '👑 Admin' : '👤 Usuário'}
            </span>
          </div>
          <button className="btn btn-danger btn-sm" onClick={handleLogout}>
            🚪 Sair
          </button>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
