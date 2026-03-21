import React from "react";
import { Link } from "react-router-dom";
import "./Error500.css";

export default function Error500() {
  return (
    <div className="error500-page">
      <div className="error500-card">
        <h1>500</h1>
        <p>Erro interno no servidor.</p>
        <div className="error500-actions">
          <Link className="btn-primary" to="/">Voltar ao inicio</Link>
          <Link className="btn-secondary" to="/login">Login</Link>
        </div>
      </div>
    </div>
  );
}
