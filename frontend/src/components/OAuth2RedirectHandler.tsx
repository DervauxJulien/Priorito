// ===========================================================
// Composant OAuth2RedirectHandler
// -------------------------------
// Gestion du callback après authentification OAuth2 (ex: Google).
// - Récupère le token envoyé par le backend via query params
// - Stocke le token dans localStorage et contexte global
// - Redirige l'utilisateur vers le dashboard ou login
// ===========================================================

import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext"; // contexte global pour auth

export default function OAuth2RedirectHandler() {
  // ===========================================================
  // Hooks React
  // - useSearchParams : récupère les paramètres de l'URL
  // - useNavigate : navigation programmatique
  // - setToken : fonction du contexte pour stocker le token
  // ===========================================================
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { setToken } = useAuth();

  // ===========================================================
  // useEffect pour traiter le token à l'arrivée sur ce composant
  // ===========================================================
  useEffect(() => {
    const token = searchParams.get("token"); // récupère token depuis l'URL

    if (token) {
      localStorage.setItem("accessToken", token);
      // Met à jour le contexte global (pour accès depuis tous les composants)
      setToken(token);
      navigate("/tasks");
    } else {
      navigate("/login");
    }
  }, [searchParams, navigate, setToken]);

  return <p>Connexion en cours...</p>;
}
