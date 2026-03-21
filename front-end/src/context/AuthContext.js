import React, { createContext, useState, useContext, useEffect } from "react";
import Keycloak from "keycloak-js";

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [keycloak, setKeycloak] = useState(null);
  const [authenticated, setAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null);

  useEffect(() => {
    console.log("[AuthContext] === Starting Keycloak initialization ===");

    // Check if we're coming back from Keycloak with an auth code
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');
    const sessionState = urlParams.get('session_state');
    
    if (code || sessionState) {
      console.log("[AuthContext] ✅ Detected return from Keycloak - allowing flow to continue");
    }

    const keycloakUrl = process.env.REACT_APP_KEYCLOAK_URL || "http://localhost:8080";
    const realm = process.env.REACT_APP_KEYCLOAK_REALM || "crm-realm";
    const clientId = process.env.REACT_APP_KEYCLOAK_CLIENT_ID || "crm-frontend";

    console.log("[AuthContext] Config:", { keycloakUrl, realm, clientId });

    try {
      const kc = new Keycloak({
        url: keycloakUrl,
        realm: realm,
        clientId: clientId
      });

      // Expose Keycloak instance for API interceptor access
      window.keycloak = kc;

      console.log("[AuthContext] Keycloak instance created successfully");

      let refreshInterval;

      // Callback para quando a autenticação é bem-sucedida (após login)
      const onAuthSuccess = (isAuthenticated) => {
        console.log("[AuthContext] onAuthSuccess called, authenticated:", isAuthenticated);
        setKeycloak(kc);
        setAuthenticated(isAuthenticated);

        if (isAuthenticated) {
          // Save token to localStorage for API requests
          try {
            if (kc.token) {
              localStorage.setItem('token', kc.token);
              console.log("[AuthContext] ✅ Token saved to localStorage");
              console.log("[AuthContext] Token length:", kc.token.length);
              console.log("[AuthContext] Token starts with:", kc.token.substring(0, 20) + "...");

              // Keep a global reference for API calls
              window.keycloak = kc;
              
              // Verify token was actually saved
              const savedToken = localStorage.getItem('token');
              console.log("[AuthContext] Verification - Token in storage:", savedToken ? "✅ YES" : "❌ NO");
              console.log("[AuthContext] Token matches:", savedToken === kc.token ? "✅ YES" : "❌ NO");
            } else {
              console.warn("[AuthContext] ⚠️  kc.token is not available yet!");
            }
          } catch (e) {
            console.error("[AuthContext] ❌ Failed to save token:", e.message);
          }
          
          console.log("[AuthContext] User IS authenticated, loading profile...");
          kc.loadUserProfile()
            .then((profile) => {
              console.log("[AuthContext] ✅ Profile loaded:", profile.username);
              setUser({
                id: profile.id,
                username: profile.username,
                email: profile.email,
                firstName: profile.firstName,
                lastName: profile.lastName,
                roles: kc.realmAccess?.roles || []
              });
            })
            .catch((err) => {
              console.error("[AuthContext] ❌ Failed to load profile:", err);
              setUser(null);
            });

          kc.onTokenExpired = () => {
            console.log("[AuthContext] ⏰ Token expired, attempting refresh...");
            kc.updateToken(30).then(() => {
              // Update token in localStorage on refresh
              if (kc.token) {
                localStorage.setItem('token', kc.token);
                console.log("[AuthContext] ✅ Token refreshed");
              }
            }).catch(() => {
              console.error("[AuthContext] ❌ Token refresh failed");
              setAuthenticated(false);
              localStorage.removeItem('token');
            });
          };

          refreshInterval = setInterval(() => {
            kc.updateToken(70).then(() => {
              if (kc.token) {
                localStorage.setItem('token', kc.token);
              }
            }).catch(() => {
              console.error("[AuthContext] ❌ Periodic token refresh failed");
            });
          }, 60000);
        } else {
          console.log("[AuthContext] ❌ User NOT authenticated");
          localStorage.removeItem('token');
        }

        console.log("[AuthContext] Setting loading = false");
        setLoading(false);
      };

      // Try to initialize Keycloak
      kc.init({
        onLoad: "check-sso",
        checkLoginIframe: false,
        pkceMethod: "S256"
      })
        .then((auth) => {
          console.log("[AuthContext] ✅ Keycloak init SUCCESS! authenticated:", auth);
          console.log("[AuthContext] Token:", kc.token ? "✅ Present" : "❌ Missing");
          console.log("[AuthContext] RefreshToken:", kc.refreshToken ? "✅ Present" : "❌ Missing");
          
          // Clean up URL params after successful init
          if (auth && window.location.search) {
            console.log("[AuthContext] Cleaning up URL parameters");
            window.history.replaceState({}, document.title, window.location.pathname);
          }
          
          onAuthSuccess(auth);

          // Setup event listeners
          kc.onAuthRefresh = () => {
            console.log("[AuthContext] onAuthRefresh fired - token refreshed");
          };

          kc.onAuthLogout = () => {
            console.log("[AuthContext] onAuthLogout fired");
            setAuthenticated(false);
            localStorage.removeItem('token');
            setUser(null);
          };

          kc.onTokenExpired = () => {
            console.log("[AuthContext] onTokenExpired fired");
          };
        })
        .catch((error) => {
          console.error("[AuthContext] KEYCLOAK INIT FAILED ");
          console.error("[AuthContext] Error:", error.message);
          setLoading(false);
        });

      return () => {
        console.log("[AuthContext] Cleanup called");
        if (refreshInterval) clearInterval(refreshInterval);
      };
    } catch (error) {
      console.error("[AuthContext]  Exception during init:", error);
      setLoading(false);
    }
  }, []);

  const login = () => {
    console.log("[AuthContext] login() called");
    if (keycloak) {
      console.log("[AuthContext] Redirecting to Keycloak login...");
      keycloak.login({ 
        redirectUri: window.location.origin + "/" 
      });
    } else {
      console.error("[AuthContext] keycloak not ready yet!");
    }
  };

  const logout = () => {
    console.log("[AuthContext] logout() called");
    localStorage.removeItem('token');
    keycloak?.logout({ redirectUri: window.location.origin + "/login" });
  };

  const hasRole = (role) => {
    return user?.roles?.includes(role) || false;
  };

  const getToken = () => {
    return keycloak?.token;
  };

  console.log("[AuthContext] Render: loading=" + loading + ", authenticated=" + authenticated + ", user=" + (user?.username || "none"));

  if (loading) {
    return (
      <div className="loading" style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh", flexDirection: "column" }}>
        <div className="spinner"></div>
        <p style={{ marginTop: "20px", color: "#666" }}>Carregando autenticação...</p>
      </div>
    );
  }

  return (
    <AuthContext.Provider
      value={{
        keycloak,
        authenticated,
        user,
        login,
        logout,
        hasRole,
        getToken
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
