import { useState, useEffect } from "react";
import { fetchExercises, saveCheckins } from "../utils/dataService"; // Assuming fetchExercises is the function to fetch exercises

const AddCheckinPage = () => {
  const [exercises, setExercises] = useState([]);
  const [checkins, setCheckins] = useState([]);
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [availableExercises, setAvailableExercises] = useState([]); // For storing fetched exercises
  
  useEffect(() => {
    // Fetch exercises when the component mounts
    const loadExercises = async () => {
      const exercisesData = await fetchExercises();
      setAvailableExercises(exercisesData); // Store exercises in availableExercises state
    };
    loadExercises();
  }, []); // Empty dependency array to run only once when the component mounts
  
  const handleSave = async () => {
    if (date && exercises.length > 0) {  // Changed checkinDate to date
      try {
        const newCheckin = {
          date: date,  // Use date instead of checkinDate
          exercises: exercises.map(exercise => ({
            exerciseId: exercise.id.toString(),
            sets: exercise.sets,
          })),
        };
  
        // Call the saveCheckins function to save the data
        await saveCheckins([newCheckin]);
  
        // Optionally handle UI feedback (e.g., success message, reset form, etc.)
        alert('Check-in saved successfully!');
      } catch (error) {
        console.error('Error saving check-in:', error);
        alert('Failed to save check-in.');
      }
    } else {
      alert('Please complete the check-in form.');
    }
  };
  
  // Handle adding new exercise and sets
  const handleAddExercise = () => {
    const newExercise = {
      id: Math.random().toString(36).substr(2, 9), // Mock ID generation for now
      sets: [{
        isWarmup: false,
        weight: "",
        reps: "",
      }],
    };
    setExercises([...exercises, newExercise]);
  };

  // Handle adding/removing sets
  const handleAddSet = (exerciseId) => {
    const updatedExercises = exercises.map(exercise => {
      if (exercise.id === exerciseId) {
        return {
          ...exercise,
          sets: [...exercise.sets, { isWarmup: false, weight: "", reps: "" }],
        };
      }
      return exercise;
    });
    setExercises(updatedExercises);
  };

  const handleRemoveSet = (exerciseId, setIndex) => {
    const updatedExercises = exercises.map(exercise => {
      if (exercise.id === exerciseId) {
        return {
          ...exercise,
          sets: exercise.sets.filter((_, index) => index !== setIndex),
        };
      }
      return exercise;
    });
    setExercises(updatedExercises);
  };

  const handleWarmupChange = (exerciseId, setIndex, value) => {
    const updatedExercises = exercises.map(exercise => {
      if (exercise.id === exerciseId) {
        return {
          ...exercise,
          sets: exercise.sets.map((set, index) =>
            index === setIndex ? { ...set, isWarmup: value } : set
          ),
        };
      }
      return exercise;
    });
    setExercises(updatedExercises);
  };

  return (
    <div className="add-checkin-page">
      <h1>Add Checkin</h1>
      <form>
        <div className="form-field">
          <label>Date</label>
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
          />
        </div>

        {exercises.map((exercise, idx) => (
          <div key={exercise.id} className="exercise-section">
            <h3>Exercise {idx + 1}</h3>

            <div className="form-field">
              <label>Exercise</label>
              <select
                value={exercise.id}
                onChange={(e) => {
                  const updatedExercises = [...exercises];
                  updatedExercises[idx].id = e.target.value;
                  setExercises(updatedExercises);
                }}
              >
                <option value="">Select Exercise</option>
                {availableExercises.map((exerciseOption) => (
                  <option key={exerciseOption.id} value={exerciseOption.id}>
                    {exerciseOption.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="set-rows">
              <table className="set-table">
                <thead>
                  <tr>
                    <th>Weight</th>
                    <th>Reps</th>
                    <th>Is Warmup</th>
                    <th>Remove Set</th>
                  </tr>
                </thead>
                <tbody>
                  {exercise.sets.map((set, setIdx) => (
                    <tr key={setIdx}>
                      <td>
                        <input
                          type="number"
                          min="0"
                          value={set.weight}
                          onChange={(e) => {
                            const updatedExercises = [...exercises];
                            updatedExercises[idx].sets[setIdx].weight = e.target.value;
                            setExercises(updatedExercises);
                          }}
                        />
                      </td>
                      <td>
                        <input
                          type="number"
                          min="0"
                          value={set.reps}
                          onChange={(e) => {
                            const updatedExercises = [...exercises];
                            updatedExercises[idx].sets[setIdx].reps = e.target.value;
                            setExercises(updatedExercises);
                          }}
                        />
                      </td>
                      <td>
                        <label className="warmup-checkbox">
                          <input
                            type="checkbox"
                            checked={set.isWarmup}
                            onChange={(e) =>
                              handleWarmupChange(exercise.id, setIdx, e.target.checked)
                            }
                          />
                        </label>
                      </td>
                      <td>
                        <button
                          className="remove-set-x"
                          type="button"
                          onClick={() => handleRemoveSet(exercise.id, setIdx)}
                        >
                          ✖
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <button
              className="add-set-button"
              type="button"
              onClick={() => handleAddSet(exercise.id)}
            >
              Add Set
            </button>
          </div>
        ))}

        <button
          className="add-exercise-button"
          type="button"
          onClick={handleAddExercise}
        >
          Add Exercise
        </button>

        <button
          className="save-changes-button"
          type="button"
          onClick={handleSave}
        >
          Save Checkin
        </button>
      </form>
    </div>
  );
};

export default AddCheckinPage;
