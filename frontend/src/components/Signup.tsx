// ===========================================================
// Composant Signup
// ----------------
// Page d'inscription pour l'application.
// Permet de créer un nouvel utilisateur puis de le connecter automatiquement.
// ===========================================================

import { useState, FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useAuth } from "../context/AuthContext";

export default function Signup() {
  // ===========================================================
  // Hooks React et contexte global
  // ===========================================================
  const navigate = useNavigate();       
  const { setToken } = useAuth();       
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState(""); 

  // ===========================================================
  // Gestion de l'inscription
  // - Empêche le submit par défaut
  // - Appelle /auth/signup puis /auth/login pour récupérer tokens
  // - Stocke refreshToken en localStorage
  // - Met à jour accessToken dans le contexte global
  // - Redirige vers /tasks
  // - Affiche un message en cas d'erreur
  // ===========================================================
  const handleSignup = async (e: FormEvent) => {
    e.preventDefault();
    try {
      await api.post("/auth/signup", { username, email, password });
      const res = await api.post("/auth/login", { username, password });

      localStorage.setItem("refreshToken", res.data.refreshToken);
      setToken(res.data.accessToken);

      navigate("/tasks");
    } catch (err: any) {
      setMessage(err.response?.data || "Erreur inscription");
    }
  };

  // ===========================================================
  // Rendu JSX
  // ===========================================================
  return (
    <Card className="p-8 w-full max-w-md shadow-lg">
      <h2 className="text-2xl font-bold mb-6 text-center">S'inscrire</h2>

      <form onSubmit={handleSignup} className="flex flex-col gap-4">
        <Input
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          minLength={3}
        />
        <Input
          placeholder="Email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <Input
          placeholder="Password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          minLength={6}
        />

        <Button type="submit" className="mt-2">S'inscrire</Button>
        {message && (
          <p className="text-sm mt-2 text-center text-gray-700 dark:text-gray-200">
            {message}
          </p>
        )}
      </form>
    </Card>
  );
}
