// ===========================================================
// Composant TaskForm
// -----------------
// Formulaire réutilisable pour créer ou éditer une tâche.
// Props :
// - title, description, priority : valeurs actuelles du formulaire
// - onTitleChange, onDescriptionChange, onPriorityChange : callbacks pour mettre à jour le state parent
// - onSubmit : callback à l'envoi du formulaire
// - submitLabel : texte du bouton de validation (optionnel, par défaut "Valider")
// ===========================================================

import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";

interface TaskFormProps {
  title: string;
  description: string;
  priority: number;
  onTitleChange: (val: string) => void;
  onDescriptionChange: (val: string) => void;
  onPriorityChange: (val: number) => void;
  onSubmit: (e: React.FormEvent) => void;
  submitLabel?: string;
}

export default function TaskForm({
  title,
  description,
  priority,
  onTitleChange,
  onDescriptionChange,
  onPriorityChange,
  onSubmit,
  submitLabel = "Valider",
}: TaskFormProps) {
  // ===========================================================
  // Rendu JSX
  // ===========================================================
  return (
    <form onSubmit={onSubmit} className="flex flex-col gap-4 mt-6">
      <Input
        placeholder="Titre"
        value={title}
        maxLength={50}
        onChange={(e) => onTitleChange(e.target.value)}
        className="flex-1"
      />
      <Textarea
        placeholder="Description"
        value={description}
        maxLength={255}
        onChange={(e) => onDescriptionChange(e.target.value)}
        className="flex-1 min-h-[100px]"
      />
      <Select
        value={priority.toString()}
        onValueChange={(val) => onPriorityChange(Number(val))}
      >
        <SelectTrigger className="w-40">
          <SelectValue placeholder="Priorité" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="1">Haute priorité</SelectItem>
          <SelectItem value="2">Moyenne priorité</SelectItem>
          <SelectItem value="3">Basse priorité</SelectItem>
        </SelectContent>
      </Select>
      <Button type="submit">{submitLabel}</Button>
    </form>
  );
}
