import { MouseEvent } from 'react';

export const EditCell = ({ row, table }) => {
  const meta = table.options.meta;

  
  const setEditedRows = (e: MouseEvent<HTMLButtonElement>) => {
    console.log("setEditedRows called with event:", e);
    const elName = e.currentTarget.name;
    console.log("Element name:", elName);

    if (!row) {
        console.error("Row is undefined or null!");
        return;
    }

    // Log meta object and row
    console.log("Meta object:", meta);
    console.log("Row object:", row);

    meta?.setEditedRows((old: []) => {
        console.log("Old edited rows:", old);  // Log old state of edited rows
        console.log("Row ID:", row.id);  // Log the row ID to make sure it's defined
        const newState = {
            ...old,
            [row.id]: !old[row.id],  // Toggle the state of the row by its ID
        };
        console.log("Updated edited rows state:", newState);
        return newState;
    });

    if (elName !== 'edit') {
        console.log("Reverting data because element name is not 'edit'");
        meta?.revertData(row.index, e.currentTarget.name === 'cancel');
    }
};

  // console.log("setEditedRows" + row.id)

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
