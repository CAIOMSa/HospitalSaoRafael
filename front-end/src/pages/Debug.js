import React, { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";

export default function Debug() {
  const { authenticated, user, keycloak } = useAuth();
  const [tokenInfo, setTokenInfo] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        // Decode JWT manually
        const parts = token.split('.');
        if (parts.length === 3) {
          const decoded = JSON.parse(atob(parts[1]));
          setTokenInfo(decoded);
        }
      } catch (e) {
        console.error("Error decoding token:", e);
      }
    }
  }, []);

  return (
    <div style={{ padding: "20px", fontFamily: "monospace", backgroundColor: "#f5f5f5", minHeight: "100vh" }}>
      <h1>🔍 Debug Page</h1>
      
      <section style={{ marginBottom: "20px", backgroundColor: "white", padding: "15px", borderRadius: "5px" }}>
        <h2>Authentication Status</h2>
        <pre>{JSON.stringify({
          authenticated,
          user: user ? { username: user.username, email: user.email, roles: user.roles } : null,
          keycloakReady: !!keycloak
        }, null, 2)}</pre>
      </section>

      <section style={{ marginBottom: "20px", backgroundColor: "white", padding: "15px", borderRadius: "5px" }}>
        <h2>Local Storage Token</h2>
        <p><strong>Token Present:</strong> {localStorage.getItem('token') ? "✅ YES" : "❌ NO"}</p>
        <p><strong>Token Length:</strong> {localStorage.getItem('token')?.length || "N/A"}</p>
        <p><strong>Token Preview:</strong> {localStorage.getItem('token')?.substring(0, 50)}...</p>
      </section>

      <section style={{ marginBottom: "20px", backgroundColor: "white", padding: "15px", borderRadius: "5px" }}>
        <h2>Token Decoded (JWT Payload)</h2>
        {tokenInfo ? (
          <pre style={{ fontSize: "12px" }}>{JSON.stringify(tokenInfo, null, 2)}</pre>
        ) : (
          <p>No valid token in localStorage</p>
        )}
      </section>

      <section style={{ marginBottom: "20px", backgroundColor: "white", padding: "15px", borderRadius: "5px" }}>
        <h2>Keycloak Object</h2>
        {keycloak ? (
          <pre style={{ fontSize: "12px" }}>{JSON.stringify({
            authenticated: keycloak.authenticated,
            token: keycloak.token ? keycloak.token.substring(0, 50) + "..." : null,
            refreshToken: keycloak.refreshToken ? "✅ Present" : "❌ Missing",
            tokenParsed: keycloak.tokenParsed ? { 
              sub: keycloak.tokenParsed.sub,
              email: keycloak.tokenParsed.email,
              email_verified: keycloak.tokenParsed.email_verified,
              realm_access: keycloak.tokenParsed.realm_access
            } : null
          }, null, 2)}</pre>
        ) : (
          <p>Keycloak not initialized</p>
        )}
      </section>

      <section style={{ marginBottom: "20px", backgroundColor: "white", padding: "15px", borderRadius: "5px" }}>
        <h2>Configuration</h2>
        <pre>{JSON.stringify({
          REACT_APP_API_URL: process.env.REACT_APP_API_URL,
          REACT_APP_KEYCLOAK_URL: process.env.REACT_APP_KEYCLOAK_URL,
          REACT_APP_KEYCLOAK_REALM: process.env.REACT_APP_KEYCLOAK_REALM,
          REACT_APP_KEYCLOAK_CLIENT_ID: process.env.REACT_APP_KEYCLOAK_CLIENT_ID
        }, null, 2)}</pre>
      </section>

      <section style={{ marginBottom: "20px", backgroundColor: "#fff3cd", padding: "15px", borderRadius: "5px" }}>
        <h2>📋 Troubleshooting</h2>
        <ul>
          <li>If "Token Present" is NO after login, check if localStorage is being cleared</li>
          <li>If token is present but API returns 401, check if the token is valid/not expired</li>
          <li>Check Keycloak server logs for auth errors: <code>docker logs crm-keycloak</code></li>
          <li>Verify Keycloak client "crm-frontend" exists in the "crm-realm"</li>
          <li>Check the F12 Console tab for more detailed logs (search for [API] or [AuthContext])</li>
        </ul>
      </section>

      <a href="/" style={{ 
        display: "inline-block", 
        padding: "10px 20px", 
        backgroundColor: "#007bff", 
        color: "white", 
        textDecoration: "none", 
        borderRadius: "5px" 
      }}>← Back to Dashboard</a>
    </div>
  );
}
