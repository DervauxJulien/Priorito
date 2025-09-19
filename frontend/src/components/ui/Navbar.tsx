// src/components/Navbar.tsx
import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/context/AuthContext";
import { useTheme } from "@/context/ThemeContext";

export default function Navbar() {
  const { isLoggedIn, role, logout } = useAuth(); // <-- récupère aussi le role
  const { darkMode, toggleDarkMode } = useTheme();
  const [menuOpen, setMenuOpen] = useState(false);
  const navigate = useNavigate();
  const [mounted, setMounted] = useState(false);

  useEffect(() => setMounted(true), []);

  return (
    <nav className="w-full bg-gray-100 dark:bg-gray-900 px-6 py-4 flex justify-between items-center shadow">
      {/* Nom du site */}
      <Link
        to="/tasks"
        className="text-xl font-bold text-gray-800 dark:text-gray-100"
        onClick={() => setMenuOpen(false)}
      >
        PrioritoApp
      </Link>

      <div className="hidden md:flex items-center gap-4">
        {mounted && (
          <Button variant="outline" size="sm" onClick={toggleDarkMode}>
            {darkMode ? "☀️" : "🌙"}
          </Button>
        )}
        {isLoggedIn && role === "ADMIN" && (
          <Button
            variant="secondary"
            size="sm"
            onClick={() => navigate("/admin")}
          >
            Dashboard Admin
          </Button>
        )}
        {isLoggedIn && (
          <Button
            variant="destructive"
            size="sm"
            onClick={() => {
              logout();
              navigate("/");
            }}
          >
            Déconnexion
          </Button>
        )}
      </div>

      {/* Mobile menu toggle */}
      <div className="md:hidden flex items-center gap-2">
        {mounted && (
          <Button variant="outline" size="sm" onClick={toggleDarkMode}>
            {darkMode ? "☀️" : "🌙"}
          </Button>
        )}
        <button
          onClick={() => setMenuOpen(!menuOpen)}
          className="text-gray-800 dark:text-gray-100 focus:outline-none"
        >
          ☰
        </button>
      </div>

      {/* Mobile menu dropdown */}
      {menuOpen && (
        <div className="absolute top-16 right-6 bg-gray-100 dark:bg-gray-900 rounded shadow-md flex flex-col p-4 gap-2 md:hidden">
          {isLoggedIn && role === "ADMIN" && (
            <Button
              variant="secondary"
              size="sm"
              onClick={() => {
                navigate("/admin");
                setMenuOpen(false);
              }}
            >
              Dashboard Admin
            </Button>
          )}
          {isLoggedIn && (
            <Button
              variant="destructive"
              size="sm"
              onClick={() => {
                logout();
                navigate("/");
                setMenuOpen(false);
              }}
            >
              Déconnexion
            </Button>
          )}
        </div>
      )}
    </nav>
  );
}
