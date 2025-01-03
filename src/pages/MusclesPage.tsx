import React, { useState, useEffect } from "react";
import { fetchMuscles, saveMuscles } from "../utils/dataService";
import { Muscle } from "../types";
import { Table } from "../components/table/Table";
import { createColumnHelper } from "@tanstack/react-table";
import { TableCell } from "../components/table/TableCell";
import { EditCell } from "../components/table/EditCell";
import AddItemPopup from "../components/popup/AddItemPopup"; // Import the AddItemPopup component

const columnHelper = createColumnHelper<Muscle>();

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
];

function MusclesPage() {
  const [muscles, setMuscles] = useState<Muscle[]>([]);
  const [originalMuscles, setOriginalMuscles] = useState<Muscle[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const [saving, setSaving] = useState(false);
  const [showPopup, setShowPopup] = useState(false); // State to control the visibility of the popup

  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await fetchMuscles();
        setMuscles(data);
        setOriginalMuscles(data); // Save the original state for comparison
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
      setOriginalMuscles(muscles); // Update the original state after saving
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
    const newId = (muscles.length + 1).toString(); // Generate a new ID (simple approach)
    const muscleToAdd = { id: newId, name: newMuscle.name };
    setMuscles((prevMuscles) => [...prevMuscles, muscleToAdd]);
  };

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
          onClick={() => setShowPopup(true)} // Open the popup to add a new muscle
        >
          Add New Muscle
        </button>
      </div>

      {/* Render the AddItemPopup if showPopup is true */}
      {showPopup && (
        <AddItemPopup
          fields={[{ name: "name", label: "Muscle Name", type: "text" }]} // Define the fields for the popup
          onAdd={handleAddMuscle} // Handle the muscle addition
          onClose={() => setShowPopup(false)} // Close the popup
        />
      )}
    </div>
  );
}

export default MusclesPage;
