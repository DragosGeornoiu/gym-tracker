import React, { useState, useEffect } from "react";
import { fetchMuscles, saveMuscles } from "../utils/dataService";
import { Muscle } from "../types";
import { Table } from "../components/table/Table";
import { createColumnHelper } from "@tanstack/react-table";
import { TableCell } from "../components/table/TableCell";
import { EditCell } from "../components/table/EditCell";
import RemoveCell from "../components/table/RemoveCell"; 
import AddItemPopup from "../components/popup/AddItemPopup"; 

const columnHelper = createColumnHelper<Muscle>();

function MusclesPage() {
  const [muscles, setMuscles] = useState<Muscle[]>([]);
  const [originalMuscles, setOriginalMuscles] = useState<Muscle[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const [saving, setSaving] = useState(false);
  const [showPopup, setShowPopup] = useState(false); 
  const [confirmDelete, setConfirmDelete] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await fetchMuscles();
        setMuscles(data);
        setOriginalMuscles(data); 
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
      await saveMuscles(muscles);
      setOriginalMuscles(muscles); 
      alert("Changes saved successfully!");
    } catch (err) {
      console.error(err);
      alert("Failed to save changes. Please try again.");
    } finally {
      setSaving(false);
    }
  };

  const hasUnsavedChanges = JSON.stringify(muscles) !== JSON.stringify(originalMuscles);

  // Function to handle adding a new muscle via the popup
  const handleAddMuscle = (newMuscle: { [key: string]: string }) => {
    const newId = (muscles.length + 1).toString(); //todo this is not a good strategy
    const muscleToAdd = { id: newId, name: newMuscle.name };
    setMuscles((prevMuscles) => [...prevMuscles, muscleToAdd]);
  };

  // Define handleRemoveMuscle function here so it's in scope for the column
  const handleRemoveMuscle = (id: string) => {
    setConfirmDelete(id); 
  };

  // Function to actually remove the muscle after confirmation
  const handleConfirmRemoveMuscle = () => {
    if (confirmDelete) {
      setMuscles((prevMuscles) => prevMuscles.filter((muscle) => muscle.id !== confirmDelete));
      setConfirmDelete(null); 
    }
  };

  // Function to cancel the removal operation
  const handleCancelRemoveMuscle = () => {
    setConfirmDelete(null); 
  };

  // Now define the columns after handleRemoveMuscle is in scope
  const columns = [
    columnHelper.accessor("id", {
      header: "Muscle ID",
      cell: TableCell,
      meta: {
        type: "number",
        editable: false,
      },
    }),
    columnHelper.accessor("name", {
      header: "Muscle Name",
      cell: TableCell,
      meta: {
        type: "text",
        editable: true,
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
          onRemove={handleRemoveMuscle}
        />
      ),
    }),
  ];

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error.message}</p>;

  return (
    <div className="muscles-page-container">
      <h1>MusclesPage</h1>
      <div className="table-and-button-container">
        <Table data={muscles} setData={setMuscles} columns={columns} />
        <button
          className="save-changes-button"
          onClick={handleSaveChanges}
          disabled={!hasUnsavedChanges || saving}
        >
          {saving ? "Saving..." : "Save Changes"}
        </button>
        <button
          className="add-muscle-button"
          onClick={() => setShowPopup(true)} 
        >
          Add New Muscle
        </button>
      </div>

      {showPopup && (
        <AddItemPopup
          fields={[{ name: "name", label: "Muscle Name", type: "text" }]} 
          onAdd={handleAddMuscle} 
          onClose={() => setShowPopup(false)} 
        />
      )}

      {confirmDelete && (
        <div className="confirmation-dialog">
          <p>Are you sure you want to remove this muscle?</p>
          <button onClick={handleConfirmRemoveMuscle}>Yes, Remove</button>
          <button onClick={handleCancelRemoveMuscle}>Cancel</button>
        </div>
      )}
    </div>
  );
}

export default MusclesPage;
