// ===========================================================
// Contexte d'authentification
// ---------------------------
// Gère :
// - Le token JWT (accessToken)
// - Le rôle de l'utilisateur
// - L'état de connexion
// - La déconnexion globale
// - Fournit un hook `useAuth` pour y accéder facilement
// ===========================================================

import { createContext, useContext, useState, ReactNode, useEffect } from "react";
import { jwtDecode } from "jwt-decode";

// ===========================================================
// Types JWT et contexte
// ===========================================================
interface JwtPayload {
  sub: string;   
  role: string; 
  exp: number;  
}

interface AuthContextType {
  role: string | null;       // rôle courant (ADMIN / USER)
  isLoggedIn: boolean;       // booléen connecté/non
  logout: () => void;        // fonction pour déconnexion
  setToken: (token: string) => void; // met à jour le token et le rôle
  token: string | null;      // JWT actuel
  loading: boolean;          // indique si le contexte est en train de charger
}

// ===========================================================
// Création du contexte
// ===========================================================
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// ===========================================================
// Provider
// -----------------------------------------------------------
// Fournit le contexte à l'application entière via <AuthProvider>
// ===========================================================
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [token, setTokenState] = useState<string | null>(localStorage.getItem("accessToken"));
  const [role, setRole] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  // ===========================================================
  // Décode le rôle à partir du JWT
  // -----------------------------------------------------------
  // Retourne 'ADMIN', 'USER', ou null si problème
  // ===========================================================
  const decodeRole = (jwt: string | null) => {
    if (!jwt) return null;
    try {
      const decoded: JwtPayload = jwtDecode<JwtPayload>(jwt);
      return decoded.role?.toUpperCase() || null;
    } catch (err) {
      console.error("Erreur de décodage JWT :", err);
      return null;
    }
  };

  // ===========================================================
  // Met à jour le token et le rôle associé
  // ===========================================================
  const setToken = (newToken: string) => {
    localStorage.setItem("accessToken", newToken);
    setTokenState(newToken);
    setRole(decodeRole(newToken));
  };

  // ===========================================================
  // Déconnexion
  // -----------------------------------------------------------
  // Supprime token + refreshToken et réinitialise le contexte
  // Redirige vers la page de login
  // ===========================================================
  const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    setTokenState(null);
    setRole(null);
    window.location.href = "/";
  };

  // ===========================================================
  // Effet pour mettre à jour le rôle quand le token change
  // ===========================================================
  useEffect(() => {
    setRole(decodeRole(token));
    setLoading(false);
  }, [token]);

  // ===========================================================
  // Fournit le contexte aux enfants
  // ===========================================================
  return (
    <AuthContext.Provider value={{ role, isLoggedIn: !!token, logout, setToken, token, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

// ===========================================================
// Hook pour accéder facilement au contexte
// -----------------------------------------------------------
// Exemple d'utilisation :
// const { isLoggedIn, role, logout } = useAuth();
// ===========================================================
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within AuthProvider");
  return context;
};
