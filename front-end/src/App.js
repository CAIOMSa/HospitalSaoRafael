import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import Navbar from "./components/Navbar";
import Login from "./pages/Login";
import Index from "./pages/Index";
import Dashboard from "./pages/Dashboard";
import Customers from "./pages/Customers";
import ServiceControl from "./pages/ServiceControl";
import HealthCheck from "./pages/HealthCheck";
import Debug from "./pages/Debug";
import Error500 from "./pages/Error500";
import "./App.css";

const PrivateRoute = ({ children }) => {
  const { authenticated, loading } = useAuth();

  console.log("[PrivateRoute] Checking: authenticated=" + authenticated + ", loading=" + loading);

  if (loading) {
    console.log("[PrivateRoute] Still loading, showing spinner...");
    return (
      <div className="loading" style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh" }}>
        <div className="spinner"></div>
      </div>
    );
  }

  if (!authenticated) {
    console.log("[PrivateRoute] Not authenticated, redirecting to /login");
    return <Navigate to="/login" replace />;
  }

  console.log("[PrivateRoute] Authenticated! Rendering protected route");
  return children;
};

function AppContent() {
  const { authenticated, loading } = useAuth();
  
  console.log("[App] Rendering AppContent with routes - authenticated:", authenticated, "loading:", loading);

  // Show loading spinner while checking authentication
  if (loading) {
    return (
      <div className="loading" style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh", flexDirection: "column" }}>
        <div className="spinner"></div>
        <p style={{ marginTop: "20px", color: "#666" }}>Verificando autenticação...</p>
      </div>
    );
  }

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/debug" element={<Debug />} />
      <Route path="/error/500" element={<Error500 />} />
      <Route
        path="/"
        element={
          <PrivateRoute>
            <Navbar />
            <Index />
          </PrivateRoute>
        }
      />
      <Route
        path="/dashboard"
        element={
          <PrivateRoute>
            <Navbar />
            <Dashboard />
          </PrivateRoute>
        }
      />
      <Route
        path="/customers"
        element={
          <PrivateRoute>
            <Navbar />
            <Customers />
          </PrivateRoute>
        }
      />
      <Route
        path="/services"
        element={
          <PrivateRoute>
            <Navbar />
            <ServiceControl />
          </PrivateRoute>
        }
      />
      <Route
        path="/health"
        element={
          <PrivateRoute>
            <Navbar />
            <HealthCheck />
          </PrivateRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default function App() {
  console.log("[App] Main App component rendering");

  return (
    <Router>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </Router>
  );
}
