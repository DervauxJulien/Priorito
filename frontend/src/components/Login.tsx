// ===========================================================
// Composant Login
// -------------------
// Page de connexion pour l'application.
// Permet :
// 1. Connexion classique avec username + password
// 2. Connexion via Google OAuth2
// ===========================================================

import { useState, FormEvent } from "react";
import api from "../api/api";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useAuth } from "../context/AuthContext";

export default function Login() {
  // ===========================================================
  // Auth context pour stocker le token dans le state global
  // ===========================================================
  const { setToken } = useAuth();

  // ===========================================================
  // States locaux pour gérer le formulaire et les erreurs
  // ===========================================================
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  // ===========================================================
  // Gestion du login classique
  // - Empêche le submit par défaut
  // - Appel API /auth/login
  // - Stocke refreshToken en localStorage
  // - Met à jour accessToken dans le contexte global
  // - Affiche erreur si login invalide
  // ===========================================================
  const handleLogin = async (e: FormEvent) => {
    e.preventDefault();
    try {
      const res = await api.post("/auth/login", { username, password });
      localStorage.setItem("refreshToken", res.data.refreshToken);
      setToken(res.data.accessToken);
    } catch {
      setError("Identifiants incorrects");
    }
  };

  // ===========================================================
  // Connexion OAuth2 via Google
  // - Redirection vers Spring Security
  // ===========================================================
  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  // ===========================================================
  // Rendu JSX
  // ===========================================================
  return (
    <Card className="p-8 w-full max-w-md shadow-lg">
      <h2 className="text-2xl font-bold mb-6 text-center">Se connecter</h2>

      {/* 🔹 Login classique */}
      <form onSubmit={handleLogin} className="flex flex-col gap-4">
        <Input
          placeholder="Username"
          value={username}
          onChange={e => setUsername(e.target.value)}
        />
        <Input
          placeholder="Password"
          type="password"
          value={password}
          onChange={e => setPassword(e.target.value)}
        />
        <Button type="submit">Connexion</Button>

        {/* Affichage message d'erreur */}
        {error && <p className="text-red-500 text-sm mt-2">{error}</p>}
      </form>

      {/* 🔹 Login via Google */}
      <div className="mt-6 flex flex-col items-center gap-2">
        <p className="text-gray-500 text-sm">ou</p>
        <Button
          variant="outline"
          onClick={handleGoogleLogin}
          className="w-full"
        >
          Se connecter avec Google
        </Button>
      </div>
    </Card>
  );
}
