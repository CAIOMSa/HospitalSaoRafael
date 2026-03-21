import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Login.css";

export default function Login() {
  const { login, authenticated, loading } = useAuth();
  const navigate = useNavigate();

  console.log("[Login] Rendering Login page. authenticated=" + authenticated);

  // Redirect to dashboard if already authenticated
  useEffect(() => {
    if (!loading && authenticated) {
      console.log("[Login] User already authenticated, redirecting to dashboard");
      navigate("/", { replace: true });
    }
  }, [authenticated, loading, navigate]);

  const handleLogin = () => {
    console.log("[Login] User clicked login button");
    login();
  };

  if (loading) {
    return (
      <div className="login-container">
        <div className="login-box">
          <div className="spinner"></div>
          <p style={{ marginTop: "20px", color: "#666" }}>Carregando autenticação...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="login-container">
      <div className="login-box">
        <h1>CRM São Rafael</h1>
        <p style={{ color: authenticated ? "green" : "red", fontSize: "14px", marginBottom: "20px" }}>
          Status: {authenticated ? " Autenticado" : " Não autenticado"}
        </p>
        <button onClick={handleLogin} className="login-button">
          Entrar com Keycloak
        </button>
        <p style={{ fontSize: "12px", color: "#999", marginTop: "20px" }}>
           Abra o DevTools (F12) e veja a aba Console para logs detalhados
        </p>
      </div>
    </div>
  );
}
