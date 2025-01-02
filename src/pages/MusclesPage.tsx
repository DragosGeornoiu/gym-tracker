import React, { useState, useEffect } from "react";
import { fetchMuscles } from "../utils/dataService";
import { Muscle } from "../types";
import { Table } from "../components/table/Table";
import { createColumnHelper } from '@tanstack/react-table';
import { TableCell } from '../components/table/TableCell';
import { EditCell } from '../components/table/EditCell';

const columnHelper = createColumnHelper<Muscle>();

const columns = [
  columnHelper.accessor('id', {
    header: 'Muscle ID',
    cell: TableCell,
    meta: {
      type: 'number',
      editable: false,
    },
  }),
  columnHelper.accessor('name', {
    header: 'Muscle Name',
    cell: TableCell,
    meta: {
      type: 'text',
      editable: true,
    },
  }),
  columnHelper.display({
    id: 'edit',
    cell: EditCell,
  }),
];

function MusclesPage() {
  const [muscles, setMuscles] = useState<Muscle[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  // Fetch muscles on component mount
  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await fetchMuscles();
        setMuscles(data); 
      } catch (err) {
        setError(err as Error); 
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error.message}</p>;

  return (
    <div>
      <h1>MusclesPage</h1>
      <Table data={muscles} setData={setMuscles} columns={columns} />
    </div>
  );
}

export default MusclesPage;
