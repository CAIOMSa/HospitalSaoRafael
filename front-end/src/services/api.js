import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8081/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Request interceptor to add auth token
apiClient.interceptors.request.use(
  (config) => {
    const keycloakToken = window.keycloak?.token;
    const token = keycloakToken || localStorage.getItem('token');
    console.log("[API Request]", {
      url: config.url,
      method: config.method,
      tokenPresent: !!token,
      tokenLength: token ? token.length : 0,
      timestamp: new Date().toISOString()
    });
    
    if (!token) {
      console.warn("[API] ⚠️ No token in localStorage! Checking window...");
      // Try to get token from Keycloak window object if available
      if (window.keycloak && window.keycloak.token) {
        console.log("[API] Found token in window.keycloak.token! Using it...");
        localStorage.setItem('token', window.keycloak.token);
        config.headers.Authorization = `Bearer ${window.keycloak.token}`;
      }
    } else {
      config.headers.Authorization = `Bearer ${token}`;
      console.log("[API] ✅ Authorization header set - Bearer " + token.substring(0, 20) + "...");
    }
    
    return config;
  },
  (error) => {
    console.error("[API] Request interceptor error:", error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => {
    console.log("[API Response] ✅ Success", {
      url: response.config.url,
      status: response.status,
      dataKeys: Object.keys(response.data).length
    });
    return response;
  },
  (error) => {
    console.error("[API Response] ❌ Error", {
      url: error.config?.url,
      status: error.response?.status,
      statusText: error.response?.statusText,
      method: error.config?.method,
      auth: error.config?.headers?.Authorization ? "Present" : "Missing"
    });

    if (error.response?.status === 401) {
      const originalRequest = error.config;
      if (!originalRequest._retry && window.keycloak?.authenticated) {
        originalRequest._retry = true;
        console.log("[API] 401 Unauthorized - attempting token refresh and retry");
        return window.keycloak.updateToken(0)
          .then(() => {
            const refreshedToken = window.keycloak.token;
            if (refreshedToken) {
              localStorage.setItem('token', refreshedToken);
              originalRequest.headers.Authorization = `Bearer ${refreshedToken}`;
              return apiClient(originalRequest);
            }
            return Promise.reject(error);
          })
          .catch((refreshError) => {
            console.error("[API] Token refresh failed:", refreshError);
            localStorage.removeItem('token');
            window.location.href = '/login';
            return Promise.reject(error);
          });
      }

      console.log("[API] 401 Unauthorized - clearing token and redirecting to login");
      try {
        localStorage.removeItem('token');
      } catch (e) {
        console.warn("[API] Error clearing localStorage:", e);
      }
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const healthApi = {
  stack: () => apiClient.get('/health/stack'),
};

export const serviceApi = {
  listServices: () => apiClient.get('/services'),
  getServiceStatus: (serviceName) => apiClient.get(`/services/${serviceName}/status`),
  startService: (serviceName) => apiClient.post(`/services/${serviceName}/start`),
  stopService: (serviceName) => apiClient.post(`/services/${serviceName}/stop`),
  restartService: (serviceName) => apiClient.post(`/services/${serviceName}/restart`),
};

export const customerApi = {
  list: (params) => apiClient.get('/v1/customers', { params }),
  get: (id) => apiClient.get(`/v1/customers/${id}`),
  create: (data) => apiClient.post('/v1/customers', data),
  update: (id, data) => apiClient.put(`/v1/customers/${id}`, data),
  delete: (id) => apiClient.delete(`/v1/customers/${id}`),
};

export const analyticsApi = {
  getCustomerInsights: (customerId) => axios.get(`http://localhost:8000/api/analytics/customers/${customerId}/insights`),
  getPredictions: () => axios.get('http://localhost:8000/api/analytics/predictions'),
};

export default apiClient;
