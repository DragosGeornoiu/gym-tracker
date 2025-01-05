import React, { useState, useEffect } from "react";
import { fetchMuscles, fetchExercises, saveExercises } from "../utils/dataService";
import { Exercise, Muscle } from "../types";
import { Table } from "../components/table/Table";
import { createColumnHelper } from "@tanstack/react-table";
import { TableCell } from "../components/table/TableCell";
import { EditCell } from "../components/table/EditCell";
import RemoveCell from "../components/table/RemoveCell"; 
import AddItemPopup from "../components/popup/AddItemPopup"; 

const columnHelper = createColumnHelper<Exercise>();

function ExercisesPage() {
  const [exercises, setExercises] = useState<Exercise[]>([]);
  const [muscles, setMuscles] = useState<Muscle[]>([]);
  const [originalExercises, setOriginalExercises] = useState<Exercise[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const [saving, setSaving] = useState(false);
  const [showPopup, setShowPopup] = useState(false); 
  const [confirmDelete, setConfirmDelete] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const fetchedMuscles = await fetchMuscles();
        const fetchedExercises = await fetchExercises();
        setMuscles(fetchedMuscles);
        setExercises(fetchedExercises);
        setOriginalExercises(fetchedExercises);
      } catch (err) {
        setError(err as Error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleSaveChanges = async () => {
    setSaving(true);
    try {
      await saveExercises(exercises);
      setOriginalExercises(exercises);
      alert("Changes saved successfully!");
    } catch (err) {
      console.error(err);
      alert("Failed to save changes. Please try again.");
    } finally {
      setSaving(false);
    }
  };

  const hasUnsavedChanges = JSON.stringify(exercises) !== JSON.stringify(originalExercises);

  // Handle adding a new exercise via the popup
  const handleAddExercise = (newExercise: { [key: string]: string }) => {
    const newId = (exercises.length + 1).toString(); // todo this is not a good strategy
    const exerciseToAdd = { id: newId, name: newExercise.name, muscleId: newExercise.muscleId };
    setExercises((prevExercises) => [...prevExercises, exerciseToAdd]);
  };

  // Handle removing an exercise
  const handleRemoveExercise = (id: string) => {
    setConfirmDelete(id); 
  };

  const handleConfirmRemoveExercise = () => {
    if (confirmDelete) {
      setExercises((prevExercises) => prevExercises.filter((exercise) => exercise.id !== confirmDelete));
      setConfirmDelete(null); 
    }
  };

  const handleCancelRemoveExercise = () => {
    setConfirmDelete(null); 
  };

  // Columns definition for the exercise table
  const columns = [
    columnHelper.accessor("id", {
      header: "Exercise ID",
      cell: TableCell,
      meta: {
        type: "number",
        editable: false,
      },
    }),
    columnHelper.accessor("name", {
      header: "Exercise Name",
      cell: TableCell,
      meta: {
        type: "text",
        editable: true,
      },
    }),
    columnHelper.accessor("muscleId", {
      header: "Target Muscle",
      cell: TableCell,  // This will use TableCell, which handles the rendering
      meta: {
        type: "select",  // Render as a select dropdown
        options: muscles.map(muscle => ({
          value: muscle.id,
          label: muscle.name
        })),
        // Define the value mapping function to map muscleId to muscle name
        valueMapping: (muscleId: any) => {
          console.log("Muscles available:", muscles); 
          const muscle = muscles.find(m => m.id === muscleId);
          return muscle ? muscle.name : muscleId; // If no muscle found, return the ID itself
        },
      },
    }),
    columnHelper.display({
      id: "edit",
      cell: EditCell,
    }),
    columnHelper.display({
      id: "remove",
      cell: ({ row }) => (
        <RemoveCell
          rowId={row.original.id}
          onRemove={handleRemoveExercise}
        />
      ),
    }),
  ];

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error.message}</p>;

  return (
    <div className="exercises-page-container">
      <h1>Exercises Page</h1>
      <div className="table-and-button-container">
        <Table data={exercises} setData={setExercises} columns={columns} />
        <button
          className="save-changes-button"
          onClick={handleSaveChanges}
          disabled={!hasUnsavedChanges || saving}
        >
          {saving ? "Saving..." : "Save Changes"}
        </button>
        <button
          className="add-exercise-button"
          onClick={() => setShowPopup(true)} 
        >
          Add New Exercise
        </button>
      </div>

      {showPopup && (
        <AddItemPopup
          fields={[
            { name: "name", label: "Exercise Name", type: "text" },
            { name: "muscleId", label: "Target Muscle", type: "select", options: muscles.map(m => ({ value: m.id, label: m.name })) },
          ]}
          onAdd={handleAddExercise} 
          onClose={() => setShowPopup(false)} 
        />
      )}

      {confirmDelete && (
        <div className="confirmation-dialog">
          <p>Are you sure you want to remove this exercise?</p>
          <button onClick={handleConfirmRemoveExercise}>Yes, Remove</button>
          <button onClick={handleCancelRemoveExercise}>Cancel</button>
        </div>
      )}
    </div>
  );
}

export default ExercisesPage;
