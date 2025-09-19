// ===========================================================
// Composant Tasks
// -----------------
// Affiche la liste des tâches de l'utilisateur connecté.
// Permet :
// - Ajouter une nouvelle tâche
// - Modifier une tâche existante
// - Supprimer une tâche
// - Marquer une tâche comme terminée / non terminée
// - Filtrer par statut (terminé / à faire) et trier
// ===========================================================

import { useEffect, useState } from "react";
import { Card, CardContent, CardDescription, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import api from "../api/api";
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select";
import TaskForm from "./TaskForm";


// ===========================================================
// Interfaces / Types
// ===========================================================
interface Task {
  id: number;
  title: string;
  description: string;
  completed: boolean;
  priority: number;
  createdAt: string;
}

// ===========================================================
// Fonction utilitaire
// -------------------
// Nettoie les caractères potentiellement dangereux
// (prévention XSS simple)
// ===========================================================
function sanitizeInput(input: string) {
  return input.replace(/[<>]/g, "");
}

// ===========================================================
// Composant principal
// ===========================================================
export default function Tasks() {
  // -------------------------
  // États locaux
  // -------------------------
  const [tasks, setTasks] = useState<Task[]>([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [priority, setPriority] = useState(2);
  const [sortBy, setSortBy] = useState<"priority" | "title" | "date">("priority");
  const [showCompleted, setShowCompleted] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);

  // -------------------------
  // Fetch des tâches depuis l'API
  // -------------------------
  const fetchTasks = async () => {
    try {
      const res = await api.get<Task[]>("/tasks");
      setTasks(res.data);
    } catch (error) {
      console.error("Erreur fetchTasks :", error);
    }
  };

  // Au montage du composant, récupérer les tâches
  useEffect(() => {
    fetchTasks();
  }, []);

  // -------------------------
  // Filtrage et tri des tâches à afficher
  // -------------------------
  const tasksToDisplay = [...tasks]
    .filter((task) => (showCompleted ? task.completed : !task.completed))
    .sort((a, b) => {
      if (sortBy === "priority") return a.priority - b.priority;
      if (sortBy === "title") return a.title.localeCompare(b.title);
      if (sortBy === "date") return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
      return 0;
    });

  // -------------------------
  // Ajouter une tâche
  // -------------------------
  const addTask = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title) return;

    await api.post("/tasks", {
      title: sanitizeInput(title.slice(0, 100)),
      description: sanitizeInput(description.slice(0, 255)),
      priority,
    });

    // Reset formulaire
    setTitle("");
    setDescription("");
    setPriority(2);
    fetchTasks();
  };

  // -------------------------
  // Toggle completed / non-completed
  // -------------------------
  const toggleComplete = async (task: Task, e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    await api.put(`/tasks/${task.id}`, {
      ...task,
      completed: !task.completed,
    });
    setTasks((prev) => prev.map((t) => (t.id === task.id ? { ...t, completed: !t.completed } : t)));
  };

  // -------------------------
  // Mettre à jour une tâche
  // -------------------------
  const updateTask = async () => {
    if (!editingTask) return;

    await api.put(`/tasks/${editingTask.id}`, {
      title: sanitizeInput(editingTask.title.slice(0, 100)),
      description: sanitizeInput(editingTask.description.slice(0, 255)),
      completed: editingTask.completed,
      priority: editingTask.priority,
    });

    setEditingTask(null);
    fetchTasks();
  };

  // -------------------------
  // Supprimer une tâche
  // -------------------------
  const deleteTask = async (id: number) => {
    await api.delete(`/tasks/${id}`);
    setTasks((prev) => prev.filter((t) => t.id !== id));
  };

  // ===========================================================
  // Rendu JSX
  // ===========================================================
  return (
    <div className="p-6 max-w-3xl mx-auto">
      <h2 className="text-3xl font-bold mb-6">Mes tâches</h2>
      <Card className="mb-6">
        <CardContent>
          <TaskForm
            title={title}
            description={description}
            priority={priority}
            onTitleChange={setTitle}
            onDescriptionChange={setDescription}
            onPriorityChange={setPriority}
            onSubmit={addTask}
            submitLabel="Ajouter"
          />
        </CardContent>
      </Card>
      <div className="mb-4 flex gap-2 items-center">
        <Button type="button" onClick={() => setShowCompleted((prev) => !prev)}>
          {showCompleted ? "Voir tâches à faire" : "Voir tâches terminées"}
        </Button>
      </div>
      <div className="mb-4 flex gap-2 items-center">
        <span>Trier par :</span>
        <Select
          value={sortBy}
          onValueChange={(val) => setSortBy(val as "priority" | "title" | "date")}
        >
          <SelectTrigger className="w-40">
            <SelectValue placeholder="Trier par" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="priority">Priorité</SelectItem>
            <SelectItem value="title">Titre</SelectItem>
            <SelectItem value="date">Date</SelectItem>
          </SelectContent>
        </Select>
      </div>
      <div className="flex flex-col gap-4 w-full">
        {tasksToDisplay.map((task) => (
          <Card key={task.id} className="w-full flex flex-col">
            <CardContent className="flex-1">
              {/* Date + bouton modifier / sauvegarder */}
              <div className="flex justify-between items-center mt-2">
                <p className="text-xs text-gray-400 mt-2">
                  {new Date(task.createdAt).toLocaleDateString()}
                </p>

                {editingTask?.id === task.id ? (
                  <Button variant="outline" onClick={updateTask}>
                    💾 Sauvegarder
                  </Button>
                ) : (
                  <Button
                    type="button"
                    variant="secondary"
                    onClick={() => setEditingTask(task)}
                  >
                    ✏️ Modifier
                  </Button>
                )}
              </div>
              {editingTask?.id === task.id ? (
                <TaskForm
                  title={editingTask.title}
                  description={editingTask.description}
                  priority={editingTask.priority}
                  onTitleChange={(val) => setEditingTask({ ...editingTask, title: val })}
                  onDescriptionChange={(val) => setEditingTask({ ...editingTask, description: val })}
                  onPriorityChange={(val) => setEditingTask({ ...editingTask, priority: val })}
                  onSubmit={(e) => {
                    e.preventDefault();
                    updateTask();
                  }}
                  submitLabel="💾 Sauvegarder"
                />
              ) : (
                <>
                  <h3
                    className={`text-lg font-medium ${task.completed ? "line-through text-gray-400" : ""}`}
                  >
                    {task.title}{" "}
                    <span className="text-sm text-gray-500">
                      ({task.priority === 1 ? "Haute" : task.priority === 2 ? "Moyenne" : "Basse"})
                    </span>
                  </h3>
                  <CardDescription className="line-clamp-3 break-words">
                    {task.description}
                  </CardDescription>
                </>
              )}
            </CardContent>
            <CardFooter className="flex gap-2 justify-end">
              <Button type="button" variant="outline" onClick={(e) => toggleComplete(task, e)}>
                {task.completed ? "Marquer incomplet" : "Terminé"}
              </Button>
              <Button type="button" variant="destructive" onClick={() => deleteTask(task.id)}>
                Supprimer
              </Button>
            </CardFooter>
          </Card>
        ))}
      </div>
    </div>
  );
}
