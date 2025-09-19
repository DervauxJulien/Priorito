// ===========================================================
// Contexte de thème (Dark Mode)
// ---------------------------
// Gère :
// - L'état du mode sombre (darkMode)
// - La fonction pour basculer entre clair/sombre
// ===========================================================

import { createContext, useContext, useState, ReactNode, useEffect } from "react";

// ===========================================================
// Type du contexte
// ===========================================================
interface ThemeContextType {
  darkMode: boolean;          // true = mode sombre activé, false = mode clair
  toggleDarkMode: () => void; // fonction pour changer le mode
}

// ===========================================================
// Création du contexte
// ===========================================================
const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

// ===========================================================
// Provider
// -----------------------------------------------------------
// Fournit le contexte aux composants enfants via <ThemeProvider>
// ===========================================================
export const ThemeProvider = ({ children }: { children: ReactNode }) => {
  const [darkMode, setDarkMode] = useState(false); // état du mode sombre

  // ===========================================================
  // Effet pour appliquer le mode sombre au <html>
  // -----------------------------------------------------------
  // Ajoute ou retire la classe "dark" sur l'élément racine
  // pour activer le dark mode via Tailwind ou CSS
  // ===========================================================
  useEffect(() => {
    document.documentElement.classList.toggle("dark", darkMode);
  }, [darkMode]);

  // ===========================================================
  // Bascule le mode sombre / clair
  // ===========================================================
  const toggleDarkMode = () => setDarkMode(prev => !prev);

  // ===========================================================
  // Fournit le contexte aux enfants
  // ===========================================================
  return (
    <ThemeContext.Provider value={{ darkMode, toggleDarkMode }}>
      {children}
    </ThemeContext.Provider>
  );
};

// ===========================================================
// Hook pour accéder facilement au contexte
// -----------------------------------------------------------
// Exemple d'utilisation :
// const { darkMode, toggleDarkMode } = useTheme();
// ===========================================================
export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) throw new Error("useTheme must be used within ThemeProvider");
  return context;
};
