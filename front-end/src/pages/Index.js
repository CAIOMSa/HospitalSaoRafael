import React from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Index.css";

export default function Index() {
  const { user } = useAuth();

  return (
    <div className="index-page">
      <div className="index-hero">
        <h1>Bem-vindo ao CRM São Rafael</h1>
        <p className="index-subtitle">
          Olá, <strong>{user?.preferred_username || 'Usuário'}</strong>! O que deseja fazer hoje?
        </p>
      </div>

      <div className="index-features">
        <Link to="/dashboard" className="feature-card">
          <div className="feature-icon">📊</div>
          <h3>Dashboard</h3>
          <p>Visualize métricas e estatísticas do sistema</p>
        </Link>

        <Link to="/customers" className="feature-card">
          <div className="feature-icon">👥</div>
          <h3>Clientes</h3>
          <p>Gerencie o cadastro de clientes</p>
        </Link>

        <Link to="/services" className="feature-card">
          <div className="feature-icon">⚙️</div>
          <h3>Serviços</h3>
          <p>Controle os serviços do sistema</p>
        </Link>

        <Link to="/health" className="feature-card">
          <div className="feature-icon">💚</div>
          <h3>Health Check</h3>
          <p>Monitore a saúde dos serviços</p>
        </Link>
      </div>

      <div className="index-info">
        <p>Sistema de Gerenciamento de Relacionamento com Clientes</p>
      </div>
    </div>
  );
}
