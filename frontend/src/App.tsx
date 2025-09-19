// ===========================================================
// Composant principal de l'application React
// Définit toutes les routes avec React Router v6
// Utilise PrivateRoute pour sécuriser certaines pages
// AppLayout fournit la structure globale (Navbar + main)
// ===========================================================

import { Routes, Route, Navigate } from "react-router-dom";
import Signup from "./components/Signup";
import Login from "./components/Login";
import Tasks from "./components/Tasks";
import AdminDashboard from "./components/AdminDashboard";
import PrivateRoute from "./components/PrivateRoute";
import { useAuth } from "./context/AuthContext";
import AppLayout from "./components/AppLayout";
import OAuth2RedirectHandler from "./components/OAuth2RedirectHandler";

export default function App() {
  const { isLoggedIn } = useAuth();

  return (
    <Routes>
      {/* Page d'accueil / login / signup */}
      <Route
        path="/"
        element={
          isLoggedIn ? (
            // Redirige vers le tableau de bord si déjà connecté
            <Navigate to="/tasks" replace />
          ) : (
            <AppLayout>
              <div className="flex items-center justify-center mt-6">
                <p>Venez vous vider la tête et organiser votre journée !</p>
              </div>
              <div className="flex items-center justify-center min-h-screen">
                <div className="flex flex-col md:flex-row gap-6 w-full max-w-4xl">
                  <Signup /> 
                  <div className="self-center">ou</div>
                  <Login /> 
                </div>
              </div>
            </AppLayout>
          )
        }
      />

      {/* Redirection OAuth2 Google après login */}
      <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler />} />

      {/* Tableau de bord utilisateur (PrivateRoute protège l'accès) */}
      <Route
        path="/tasks"
        element={
          <PrivateRoute>
            <AppLayout>
              <Tasks /> 
            </AppLayout>
          </PrivateRoute>
        }
      />

      {/* Dashboard admin (accès réservé aux rôles ADMIN) */}
      <Route
        path="/admin"
        element={
          <PrivateRoute roles={["ADMIN"]}>
            <AppLayout>
              <AdminDashboard /> 
            </AppLayout>
          </PrivateRoute>
        }
      />

      {/* Route par défaut : redirige selon l'état de connexion */}
      <Route path="*" element={<Navigate to={isLoggedIn ? "/tasks" : "/"} replace />} />
    </Routes>
  );
}
