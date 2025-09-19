// ===========================================================
// Composant PrivateRoute
// ----------------------
// Route protégée pour sécuriser certaines pages selon l'authentification et le rôle.
// - Vérifie si l'utilisateur est connecté
// - Vérifie si l'utilisateur a le rôle requis 
// - Redirige vers login ou tableau de bord si accès non autorisé
// ===========================================================

import { JSX } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

interface PrivateRouteProps {
  children: JSX.Element; // Composant enfant à rendre si autorisé
  roles?: string[];      
}

export default function PrivateRoute({ children, roles }: PrivateRouteProps) {
  // ===========================================================
  // Récupère l'état auth depuis le contexte global
  // - isLoggedIn : booléen si l'utilisateur est connecté
  // - role : rôle de l'utilisateur connecté
  // ===========================================================
  const { isLoggedIn, role } = useAuth();

  // ===========================================================
  // 1️⃣ Non connecté → redirection vers login
  // ===========================================================
  if (!isLoggedIn) return <Navigate to="/" replace />;

  // ===========================================================
  // 2️⃣ Récupération du rôle en cours → loading si pas encore défini
  // ===========================================================
  if (roles && role === null) {
    return (
      <div className="flex justify-center items-center h-full">
        Chargement...
      </div>
    );
  }

  // ===========================================================
  // 3️⃣ Accès refusé si rôle non autorisé → redirection vers /tasks
  // ===========================================================
  if (roles && (!role || !roles.includes(role))) return <Navigate to="/tasks" replace />;

  // ===========================================================
  // 4️⃣ Accès autorisé → rend l'enfant
  // ===========================================================
  return children;
}
