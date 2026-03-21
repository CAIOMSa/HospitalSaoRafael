import React from 'react';
import './StatsCard.css';

function StatsCard({ title, value, icon, color, onClick }) {
  return (
    <div 
      className="stats-card" 
      style={{ borderLeftColor: color }}
      onClick={onClick}
      role={onClick ? 'button' : 'presentation'}
    >
      <div className="stats-icon" style={{ backgroundColor: color + '20', color: color }}>
        {icon}
      </div>
      <div className="stats-content">
        <h3 className="stats-title">{title}</h3>
        <p className="stats-value">{value}</p>
      </div>
    </div>
  );
}

export default StatsCard;
