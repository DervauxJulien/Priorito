// ===========================================================
// Composant AppLayout
// -------------------
// Composant de layout global pour l'application.
// - Contient la Navbar et un container pour le contenu principal.
// - Permet de centraliser le style global et la structure de la page.
// ===========================================================

import * as React from "react";
import Navbar from "./ui/Navbar";

// Props : children = contenu dynamique injecté dans le layout
export default function AppLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen flex flex-col bg-gray-50 dark:bg-gray-900">
      {/* Navbar toujours présente en haut */}
      <Navbar />

      {/* Main content */}
      <main className="flex-1 p-4">
        {children} {/* Contenu injecté par les pages */}
      </main>
    </div>
  );
}
