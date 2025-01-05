import { MouseEvent } from 'react';

export const EditCell = ({ row, table }) => {
  const meta = table.options.meta;

  
  const setEditedRows = (e: MouseEvent<HTMLButtonElement>) => {
    const elName = e.currentTarget.name;

    if (!row) {
        console.error("Row is undefined or null!");
        return;
    }

    meta?.setEditedRows((old: []) => {
        const newState = {
            ...old,
            [row.id]: !old[row.id],  // Toggle the state of the row by its ID
        };
        return newState;
    });

    if (elName !== 'edit') {
        meta?.revertData(row.index, e.currentTarget.name === 'cancel');
    }
};

  return (
    <div className="edit-cell-container">
      {meta?.editedRows[row.id] ? (
        <div className="edit-cell">
          <button onClick={setEditedRows} name="cancel">
            X
          </button>{' '}
          <button onClick={setEditedRows} name="done">
            ✔
          </button>
        </div>
      ) : (
        <button onClick={setEditedRows} name="edit">
          ✐
        </button>
      )}
    </div>
  );
};
