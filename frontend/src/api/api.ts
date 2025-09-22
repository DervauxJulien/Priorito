import axios from "axios";
import { toast } from "react-toastify";


const API_URL = import.meta.env.VITE_API_URL;
console.log("API_URL:", API_URL);
// ===========================================================
// Instance Axios centralisée
// - Permet d'avoir un point unique pour configurer les headers,
//   les interceptors, et gérer les tokens.
// ===========================================================
const api = axios.create({
  baseURL: API_URL,
});

// ===========================================================
// Fonction logout centralisée
// - Supprime les tokens du localStorage
// - Redirige l'utilisateur vers la page de login
// ===========================================================
export function logout() {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("role");
  window.location.href = "/"; 
}

// ===========================================================
// Interceptor pour ajouter le token à chaque requête
// - Récupère accessToken depuis localStorage
// - Ajoute Authorization header si token présent
// ===========================================================
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ===========================================================
// Interceptor pour gérer les réponses
// - Rafraîchissement automatique du token si 401 (JWT expiré)
// - Gestion des erreurs 403 et connexion serveur
// ===========================================================
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Ne pas gérer le refresh pour login/signup
    if (
      originalRequest.url?.includes("/auth/login") ||
      originalRequest.url?.includes("/auth/signup")
    ) {
      return Promise.reject(error);
    }

    // 401 = token expiré ou invalide
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      const refreshToken = localStorage.getItem("refreshToken");

      if (refreshToken) {
        try {
          // Appel au backend pour rafraîchir le token
          const res = await axios.post(`${API_URL}/auth/refresh`, { refreshToken });

          // Mise à jour des tokens
          localStorage.setItem("accessToken", res.data.accessToken);
          localStorage.setItem("refreshToken", res.data.refreshToken);

          // Mise à jour des headers pour la requête originale
          api.defaults.headers.common["Authorization"] = `Bearer ${res.data.accessToken}`;
          originalRequest.headers["Authorization"] = `Bearer ${res.data.accessToken}`;

          return api(originalRequest); // réessaye la requête originale

        } catch (err) {
          logout(); // si refresh échoue, déconnecte
          return Promise.reject(err);
        }
      } else {
        logout(); // pas de refreshToken => logout
      }
    }

    // 403 = pas la permission
    if (error.response?.status === 403) {
      toast.error("Vous n'avez pas la permission pour cette action.");
    }

    // Pas de réponse serveur (ex: backend down)
    if (!error.response) {
      toast.error("Impossible de contacter le serveur");
    }

    return Promise.reject(error);
  }
);

export default api;
