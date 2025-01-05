import React, { useState } from 'react';
import './AddItemPopup.css'; // Make sure you add styles for the popup

// Generic AddItemPopup component
function AddItemPopup({
  fields, // Fields for the form (e.g., name, description, etc.)
  onAdd,  // Function to add the item
  onClose // Function to close the popup
}: {
  fields: { name: string; label: string; type: string; options?: { value: string; label: string }[] }[]; // Support for options in select fields
  onAdd: (newItem: { [key: string]: string }) => void;
  onClose: () => void;
}) {
  const [formData, setFormData] = useState(
    fields.reduce((acc, field) => ({ ...acc, [field.name]: '' }), {})
  );

  // Handle change for each input field
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // Handle form submission (add item)
  const handleAdd = () => {
    if (Object.values(formData).some((val) => !val.trim())) {
      return; // Do not add if any field is empty
    }

    onAdd(formData);
    setFormData(fields.reduce((acc, field) => ({ ...acc, [field.name]: '' }), {})); // Clear form data
    onClose(); // Close the popup
  };

  return (
    <div className="popup">
      <div className="popup-content">
        <h3>Add New Item</h3>
        {fields.map((field) => (
          <div key={field.name} className="form-group">
            <label htmlFor={field.name}>{field.label}</label>
            {field.type === 'select' ? (
              <select
                id={field.name}
                name={field.name}
                value={formData[field.name]}
                onChange={handleChange}
              >
                <option value="">Select {field.label}</option>
                {field.options?.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            ) : (
              <input
                id={field.name}
                name={field.name}
                type={field.type || 'text'}
                value={formData[field.name]}
                onChange={handleChange}
                placeholder={`Enter ${field.label}`}
              />
            )}
          </div>
        ))}
        <div className="popup-actions">
          <button onClick={onClose}>Cancel</button>
          <button onClick={handleAdd} disabled={Object.values(formData).some((val) => !val.trim())}>
            Add
          </button>
        </div>
      </div>
    </div>
  );
}

export default AddItemPopup;
