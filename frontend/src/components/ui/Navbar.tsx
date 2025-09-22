// src/components/Navbar.tsx
import { useState, useEffect } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/context/AuthContext";
import { useTheme } from "@/context/ThemeContext";

export default function Navbar() {
  const { isLoggedIn, role, logout } = useAuth();
  const { darkMode, toggleDarkMode } = useTheme();
  const [menuOpen, setMenuOpen] = useState(false);
  const [mounted, setMounted] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => setMounted(true), []);

  const hideAuthMenu = location.pathname === "/";

  // Couleur texte burger
  const burgerColorClass = darkMode ? "text-white" : "text-gray-900";

  return (
    <nav className="w-full bg-white dark:bg-gray-800 px-6 py-4 flex justify-between items-center shadow">
      {/* Nom du site */}
      <Link
        to={isLoggedIn ? "/tasks" : "/"}
        className="text-xl font-bold text-gray-900 dark:text-white"
        onClick={() => setMenuOpen(false)}
      >
        PrioritoApp
      </Link>

      {/* Desktop menu */}
      <div className="hidden md:flex items-center gap-4">
        {mounted && (
          <Button variant="outline" size="sm" onClick={toggleDarkMode}>
            {darkMode ? "☀️" : "🌙"}
          </Button>
        )}

        {!hideAuthMenu && isLoggedIn && role === "ADMIN" && (
          <Button
            variant="secondary"
            size="sm"
            onClick={() => navigate("/admin")}
          >
            Dashboard Admin
          </Button>
        )}
        {!hideAuthMenu && isLoggedIn && (
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
      {!hideAuthMenu && (
        <div className="md:hidden flex items-center gap-2">
          {mounted && (
            <Button variant="outline" size="sm" onClick={toggleDarkMode}>
              {darkMode ? "☀️" : "🌙"}
            </Button>
          )}
          <button
            onClick={() => setMenuOpen(!menuOpen)}
            className={`focus:outline-none ${burgerColorClass}`}
          >
            ☰
          </button>
        </div>
      )}

      {/* Mobile menu dropdown */}
      {menuOpen && !hideAuthMenu && (
        <div className="absolute top-16 right-6 bg-white dark:bg-gray-800 rounded shadow-md flex flex-col p-4 gap-2 md:hidden">
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
