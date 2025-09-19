// ===========================================================
// Composant AdminDashboard
// ------------------------
// Tableau de bord réservé aux administrateurs.
// Permet de :
// 1. Lister tous les utilisateurs
// 2. Sélectionner un utilisateur pour voir ses tâches
// 3. Supprimer un utilisateur (sauf admin)
// 4. Supprimer les tâches d'un utilisateur
// ===========================================================

import { useEffect, useState } from "react";
import api from "../api/api";
import { Card, CardHeader, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

// ===========================================================
// Types TypeScript pour les données récupérées depuis l'API
// ===========================================================
interface Task {
  id: number;
  title: string;
  description: string;
  completed: boolean;
  priority: number;
  userId: number;
}

interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  tasks?: Task[];
}

// ===========================================================
// Composant principal
// ===========================================================
export default function AdminDashboard() {
  // ===========================================================
  // States
  // - users : liste des utilisateurs récupérée depuis l'API
  // - selectedUserId : utilisateur actuellement sélectionné pour affichage des tâches
  // ===========================================================
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);

  // ===========================================================
  // Récupère la liste des utilisateurs depuis le backend
  // ===========================================================
  const fetchUsers = async () => {
    const res = await api.get<User[]>("/admin/users");
    console.log(res.data); // debug / monitoring
    setUsers(res.data);
  };

  // ===========================================================
  // useEffect pour fetch initial
  // ===========================================================
  useEffect(() => {
    fetchUsers();
  }, []);

  // ===========================================================
  // Utilisateur sélectionné
  // ===========================================================
  const selectedUser = users.find(u => u.id === selectedUserId) || null;

  // ===========================================================
  // Supprime un utilisateur
  // - Met à jour la liste localement
  // - Déselectionne si l'utilisateur supprimé était sélectionné
  // ===========================================================
  const deleteUser = async (id: number) => {
    await api.delete(`/admin/users/${id}`);
    setUsers(prev => prev.filter(u => u.id !== id));
    if (selectedUserId === id) setSelectedUserId(null);
  };

  // ===========================================================
  // Supprime une tâche pour l'utilisateur sélectionné
  // - Met à jour le state local pour refléter le changement
  // ===========================================================
  const deleteTask = async (taskId: number) => {
    if (!selectedUser) return;
    await api.delete(`/admin/tasks/${taskId}`);
    setUsers(prev =>
      prev.map(u =>
        u.id === selectedUser.id
          ? { ...u, tasks: u.tasks?.filter(t => t.id !== taskId) }
          : u
      )
    );
  };

  // ===========================================================
  // Rendu JSX
  // ===========================================================
  return (
    <div className="p-6 max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-6">

      {/* --------------------------------------------------------
          Liste des utilisateurs
      -------------------------------------------------------- */}
      <Card className="col-span-1">
        <CardHeader>
          <h3 className="text-lg font-bold">Utilisateurs</h3>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col gap-2">
            {users.map(user => (
              <div
                key={user.id}
                className={`flex justify-between items-center p-2 border rounded cursor-pointer ${
                  selectedUserId === user.id ? "bg-gray-100 dark:bg-gray-800" : ""
                }`}
              >
                <span onClick={() => setSelectedUserId(user.id)}>{user.username}</span>
                <Button
                  size="sm"
                  variant="destructive"
                  onClick={() => deleteUser(user.id)}
                  disabled={user.role === "ADMIN"} 
                >
                  Supprimer
                </Button>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* --------------------------------------------------------
          Détails de l’utilisateur sélectionné
      -------------------------------------------------------- */}
      <Card className="col-span-2">
        <CardHeader>
          <h3 className="text-lg font-bold">
            {selectedUser ? `Tâches de ${selectedUser.username}` : "Sélectionne un utilisateur"}
          </h3>
        </CardHeader>
        <CardContent>
          {selectedUser ? (
            <div className="flex flex-col gap-2">
              {selectedUser.tasks?.map(task => (
                <div
                  key={task.id}
                  className="flex justify-between items-center p-2 border rounded"
                >
                  <span>{task.title}</span>
                  <Button
                    size="sm"
                    variant="destructive"
                    onClick={() => deleteTask(task.id)}
                  >
                    Supprimer
                  </Button>
                </div>
              ))}
              {selectedUser.tasks?.length === 0 && <p>Aucune tâche.</p>}
            </div>
          ) : (
            <p>Sélectionne un utilisateur pour voir ses tâches</p>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
