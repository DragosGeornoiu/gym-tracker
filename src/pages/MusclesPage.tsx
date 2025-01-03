import React, { useState, useEffect } from "react";
import { fetchMuscles, saveMuscles } from "../utils/dataService";
import { Muscle } from "../types";
import { Table } from "../components/table/Table";
import { createColumnHelper } from "@tanstack/react-table";
import { TableCell } from "../components/table/TableCell";
import { EditCell } from "../components/table/EditCell";

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
      </div>
    </div>
  );
}

export default MusclesPage;
