import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import ServiceCard from '../components/ServiceCard';
import { healthApi } from '../services/api';
import './HealthCheck.css';

function HealthCheck() {
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [lastCheck, setLastCheck] = useState(new Date());

  useEffect(() => {
    checkAllServices();
    const interval = setInterval(() => {
      checkAllServices();
    }, 30000); // Check every 30 seconds

    return () => clearInterval(interval);
  }, []);

  const checkAllServices = async () => {
    setLoading(true);
    try {
      const response = await healthApi.stack();
      const mapped = response.data.map(s => ({
        name: s.name,
        url: s.url,
        status: s.status || 'offline',
        responseTime: s.responseTimeMs || 0,
        error: s.error,
      }));
      setServices(mapped);
    } catch (error) {
      console.error('Error checking services', error);
    }
    setLastCheck(new Date());
    setLoading(false);
  };

  const getOverallStatus = () => {
    const onlineCount = services.filter(s => s.status === 'online').length;
    const totalCount = services.length;
    
    if (onlineCount === totalCount) return 'healthy';
    if (onlineCount > totalCount / 2) return 'degraded';
    return 'critical';
  };

  const overallStatus = getOverallStatus();

  return (
    <div>
      <Navbar />
      <div className="container">
        <div className="health-header">
          <div>
            <h1>Health Check</h1>
            <p>Monitoramento de serviços em tempo real</p>
          </div>
          <button className="btn btn-primary" onClick={checkAllServices} disabled={loading}>
            {loading ? 'Verificando...' : '🔄 Atualizar'}
          </button>
        </div>

        <div className="card overall-status">
          <div className={`status-indicator ${overallStatus}`}>
            <div className="status-icon">
              {overallStatus === 'healthy' && '✅'}
              {overallStatus === 'degraded' && '⚠️'}
              {overallStatus === 'critical' && '❌'}
            </div>
            <div>
              <h2>Status Geral do Sistema</h2>
              <p>
                {services.filter(s => s.status === 'online').length} de {services.length} serviços online
              </p>
              <small>Última verificação: {lastCheck.toLocaleTimeString()}</small>
            </div>
          </div>
        </div>

        <div className="grid grid-3">
          {services.map((service) => (
            <ServiceCard key={service.name} service={service} />
          ))}
        </div>
      </div>
    </div>
  );
}

export default HealthCheck;
