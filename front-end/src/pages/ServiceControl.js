import React, { useState } from 'react';
import Navbar from '../components/Navbar';
import './ServiceControl.css';

function ServiceControl() {
  const [services, setServices] = useState([
    { 
      id: 'crm-core', 
      name: 'CRM Core', 
      status: 'running', 
      port: 8081,
      description: 'API principal do sistema CRM'
    },
    { 
      id: 'python-ai', 
      name: 'Python AI Service', 
      status: 'running', 
      port: 8000,
      description: 'Serviço de IA e Analytics'
    },
    { 
      id: 'keycloak', 
      name: 'Keycloak', 
      status: 'running', 
      port: 8080,
      description: 'Servidor de autenticação'
    },
    { 
      id: 'postgres', 
      name: 'PostgreSQL', 
      status: 'running', 
      port: 5432,
      description: 'Banco de dados principal'
    },
    { 
      id: 'redis', 
      name: 'Redis', 
      status: 'running', 
      port: 6379,
      description: 'Cache e armazenamento em memória'
    },
    { 
      id: 'rabbitmq', 
      name: 'RabbitMQ', 
      status: 'running', 
      port: 5672,
      description: 'Sistema de mensageria'
    },
    { 
      id: 'minio', 
      name: 'MinIO', 
      status: 'running', 
      port: 9000,
      description: 'Armazenamento de arquivos'
    },
    { 
      id: 'prometheus', 
      name: 'Prometheus', 
      status: 'running', 
      port: 9090,
      description: 'Monitoramento e métricas'
    },
    { 
      id: 'grafana', 
      name: 'Grafana', 
      status: 'running', 
      port: 3001,
      description: 'Visualização de métricas'
    },
  ]);

  const handleServiceAction = async (serviceId, action) => {
    setServices(services.map(service => {
      if (service.id === serviceId) {
        return { ...service, status: action === 'start' ? 'running' : 'stopped' };
      }
      return service;
    }));

    // Aqui você faria a chamada real para a API
    console.log(`${action} service: ${serviceId}`);
  };

  const handleStartAll = () => {
    setServices(services.map(service => ({ ...service, status: 'running' })));
  };

  const handleStopAll = () => {
    setServices(services.map(service => ({ ...service, status: 'stopped' })));
  };

  const runningCount = services.filter(s => s.status === 'running').length;

  return (
    <div>
      <Navbar />
      <div className="container">
        <div className="service-control-header">
          <div>
            <h1>Controle de Serviços</h1>
            <p>Gerencie o status de cada serviço do sistema</p>
          </div>
          <div className="bulk-actions">
            <button className="btn btn-success" onClick={handleStartAll}>
              ▶️ Iniciar Todos
            </button>
            <button className="btn btn-danger" onClick={handleStopAll}>
              ⏹️ Parar Todos
            </button>
          </div>
        </div>

        <div className="card">
          <div className="services-summary">
            <div className="summary-item">
              <span className="summary-label">Total de Serviços</span>
              <span className="summary-value">{services.length}</span>
            </div>
            <div className="summary-item">
              <span className="summary-label">Em Execução</span>
              <span className="summary-value running">{runningCount}</span>
            </div>
            <div className="summary-item">
              <span className="summary-label">Parados</span>
              <span className="summary-value stopped">{services.length - runningCount}</span>
            </div>
          </div>
        </div>

        <div className="services-list">
          {services.map((service) => (
            <div key={service.id} className="service-item card">
              <div className="service-info">
                <div className="service-main">
                  <h3>{service.name}</h3>
                  <span className={`status-badge ${service.status === 'running' ? 'status-online' : 'status-offline'}`}>
                    {service.status === 'running' ? '● Running' : '● Stopped'}
                  </span>
                </div>
                <p className="service-description">{service.description}</p>
                <div className="service-details">
                  <span>🔌 Porta: {service.port}</span>
                  <span>🆔 ID: {service.id}</span>
                </div>
              </div>
              <div className="service-actions">
                {service.status === 'stopped' ? (
                  <button 
                    className="btn btn-success"
                    onClick={() => handleServiceAction(service.id, 'start')}
                  >
                    ▶️ Iniciar
                  </button>
                ) : (
                  <>
                    <button 
                      className="btn btn-warning"
                      onClick={() => handleServiceAction(service.id, 'restart')}
                    >
                      🔄 Reiniciar
                    </button>
                    <button 
                      className="btn btn-danger"
                      onClick={() => handleServiceAction(service.id, 'stop')}
                    >
                      ⏹️ Parar
                    </button>
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default ServiceControl;
