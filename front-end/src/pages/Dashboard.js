import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';
import StatsCard from '../components/StatsCard';
import { customerApi, analyticsApi } from '../services/api';
import './Dashboard.css';

function Dashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    totalCustomers: 0,
    activeCustomers: 0,
    inactiveCustomers: 0,
    pendingTasks: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      const response = await customerApi.list();
      const customers = response.data;
      
      setStats({
        totalCustomers: customers.length,
        activeCustomers: customers.filter(c => c.status === 'ACTIVE').length,
        inactiveCustomers: customers.filter(c => c.status === 'INACTIVE').length,
        pendingTasks: 5 // Mock data
      });
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div>
        <Navbar />
        <div className="loading">
          <div className="spinner"></div>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      <div className="container">
        <div className="dashboard-header">
          <h1>Dashboard</h1>
          <p>Bem-vindo(a), {user?.firstName || user?.username}!</p>
        </div>

        <div className="grid grid-4">
          <StatsCard
            title="Total de Clientes"
            value={stats.totalCustomers}
            icon="👥"
            color="#3498db"
            onClick={() => navigate('/customers')}
          />
          <StatsCard
            title="Clientes Ativos"
            value={stats.activeCustomers}
            icon="✅"
            color="#2ecc71"
          />
          <StatsCard
            title="Clientes Inativos"
            value={stats.inactiveCustomers}
            icon="⏸️"
            color="#e74c3c"
          />
          <StatsCard
            title="Tarefas Pendentes"
            value={stats.pendingTasks}
            icon="📋"
            color="#f39c12"
          />
        </div>

        <div className="grid grid-2">
          <div className="card">
            <div className="card-header">
              <h2 className="card-title">Ações Rápidas</h2>
            </div>
            <div className="quick-actions">
              <button className="action-btn" onClick={() => navigate('/health')}>
                <span className="action-icon">🏥</span>
                <span>Health Check</span>
              </button>
              <button className="action-btn" onClick={() => navigate('/services')}>
                <span className="action-icon">⚙️</span>
                <span>Controle de Serviços</span>
              </button>
              <button className="action-btn" onClick={() => navigate('/customers')}>
                <span className="action-icon">👥</span>
                <span>Gerenciar Clientes</span>
              </button>
            </div>
          </div>

          <div className="card">
            <div className="card-header">
              <h2 className="card-title">Status do Sistema</h2>
            </div>
            <div className="system-status">
              <div className="status-item">
                <span>CRM Core</span>
                <span className="status-badge status-online">Online</span>
              </div>
              <div className="status-item">
                <span>Python Services</span>
                <span className="status-badge status-online">Online</span>
              </div>
              <div className="status-item">
                <span>Database</span>
                <span className="status-badge status-online">Online</span>
              </div>
              <div className="status-item">
                <span>Cache (Redis)</span>
                <span className="status-badge status-online">Online</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
