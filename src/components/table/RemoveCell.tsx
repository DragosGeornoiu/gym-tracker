import React from "react";

interface RemoveCellProps {
  rowId: string;
  onRemove: (id: string) => void;
}

const RemoveCell: React.FC<RemoveCellProps> = ({ rowId, onRemove }) => {
  return (
    <button
      onClick={() => onRemove(rowId)} // Trigger the removal when clicked
      style={{
        backgroundColor: "transparent",
        border: "none",
        color: "red",
        fontSize: "20px",
        cursor: "pointer",
      }}
    >
      ❌ {/* Red "X" icon */}
    </button>
  );
};

export default RemoveCell;
