import React from 'react';
import './ServiceCard.css';

function ServiceCard({ service }) {
  const getStatusColor = (status) => {
    switch (status) {
      case 'online':
        return '#2ecc71';
      case 'offline':
        return '#e74c3c';
      case 'checking':
        return '#f39c12';
      default:
        return '#95a5a6';
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'online':
        return '✅';
      case 'offline':
        return '❌';
      case 'checking':
        return '⏳';
      default:
        return '❓';
    }
  };

  return (
    <div className="service-card">
      <div className="service-header">
        <div className="service-status" style={{ backgroundColor: getStatusColor(service.status) }}>
          {getStatusIcon(service.status)}
        </div>
        <h3>{service.name}</h3>
      </div>
      <div className="service-details">
        <div className="detail-item">
          <span className="detail-label">URL:</span>
          <span className="detail-value">{service.url}</span>
        </div>
        <div className="detail-item">
          <span className="detail-label">Status:</span>
          <span 
            className="detail-value" 
            style={{ color: getStatusColor(service.status), fontWeight: 600 }}
          >
            {service.status}
          </span>
        </div>
        {service.responseTime > 0 && (
          <div className="detail-item">
            <span className="detail-label">Tempo de Resposta:</span>
            <span className="detail-value">{service.responseTime}ms</span>
          </div>
        )}
        {service.error && (
          <div className="error-message">
            <span>⚠️ {service.error}</span>
          </div>
        )}
      </div>
    </div>
  );
}

export default ServiceCard;
