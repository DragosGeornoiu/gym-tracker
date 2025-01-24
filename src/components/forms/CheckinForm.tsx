import React, { useState } from "react";
import { Checkin, Exercise, Set } from "./types";

type CheckinFormProps = {
  initialCheckin?: Checkin;
  allExercises: Exercise[];
  onSave: (checkin: Checkin) => void;
  onCancel: () => void;
};

const CheckinForm: React.FC<CheckinFormProps> = ({
  initialCheckin,
  allExercises,
  onSave,
  onCancel,
}) => {
  const [checkin, setCheckin] = useState<Checkin>(
    initialCheckin || { date: "", exercises: [] }
  );

  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCheckin({ ...checkin, date: e.target.value });
  };

  const handleAddExercise = () => {
    setCheckin({
      ...checkin,
      exercises: [
        ...checkin.exercises,
        { exerciseId: "", sets: [] },
      ],
    });
  };

  const handleExerciseChange = (
    index: number,
    exerciseId: string
  ) => {
    const updatedExercises = [...checkin.exercises];
    updatedExercises[index].exerciseId = exerciseId;
    setCheckin({ ...checkin, exercises: updatedExercises });
  };

  const handleAddSet = (exerciseIndex: number) => {
    const updatedExercises = [...checkin.exercises];
    updatedExercises[exerciseIndex].sets.push({
      isWarmup: false,
      weight: 0,
      reps: 0,
    });
    setCheckin({ ...checkin, exercises: updatedExercises });
  };

  const handleSetChange = (
    exerciseIndex: number,
    setIndex: number,
    updatedSet: Set
  ) => {
    const updatedExercises = [...checkin.exercises];
    updatedExercises[exerciseIndex].sets[setIndex] = updatedSet;
    setCheckin({ ...checkin, exercises: updatedExercises });
  };

  const handleRemoveExercise = (index: number) => {
    const updatedExercises = [...checkin.exercises];
    updatedExercises.splice(index, 1);
    setCheckin({ ...checkin, exercises: updatedExercises });
  };

  const handleRemoveSet = (exerciseIndex: number, setIndex: number) => {
    const updatedExercises = [...checkin.exercises];
    updatedExercises[exerciseIndex].sets.splice(setIndex, 1);
    setCheckin({ ...checkin, exercises: updatedExercises });
  };

  const handleSave = () => {
    onSave(checkin);
  };

  return (
    <div className="checkin-form">
      <h2>{initialCheckin ? "Edit Checkin" : "Add Checkin"}</h2>
      <label>
        Date:
        <input
          type="date"
          value={checkin.date}
          onChange={handleDateChange}
        />
      </label>
      <div className="exercises-section">
        <h3>Exercises</h3>
        {checkin.exercises.map((exercise, exerciseIndex) => (
          <div key={exerciseIndex} className="exercise-item">
            <select
              value={exercise.exerciseId}
              onChange={(e) =>
                handleExerciseChange(exerciseIndex, e.target.value)
              }
            >
              <option value="">Select Exercise</option>
              {allExercises.map((ex) => (
                <option key={ex.id} value={ex.id.toString()}>
                  {ex.name}
                </option>
              ))}
            </select>
            <button onClick={() => handleRemoveExercise(exerciseIndex)}>
              Remove Exercise
            </button>

            <div className="sets-section">
              <h4>Sets</h4>
              {exercise.sets.map((set, setIndex) => (
                <div key={setIndex} className="set-item">
                  <label>
                    Warmup:
                    <input
                      type="checkbox"
                      checked={set.isWarmup}
                      onChange={(e) =>
                        handleSetChange(exerciseIndex, setIndex, {
                          ...set,
                          isWarmup: e.target.checked,
                        })
                      }
                    />
                  </label>
                  <label>
                    Weight:
                    <input
                      type="number"
                      value={set.weight}
                      onChange={(e) =>
                        handleSetChange(exerciseIndex, setIndex, {
                          ...set,
                          weight: parseFloat(e.target.value) || 0,
                        })
                      }
                    />
                  </label>
                  <label>
                    Reps:
                    <input
                      type="number"
                      value={set.reps}
                      onChange={(e) =>
                        handleSetChange(exerciseIndex, setIndex, {
                          ...set,
                          reps: parseInt(e.target.value) || 0,
                        })
                      }
                    />
                  </label>
                  <button
                    onClick={() => handleRemoveSet(exerciseIndex, setIndex)}
                  >
                    Remove Set
                  </button>
                </div>
              ))}
              <button onClick={() => handleAddSet(exerciseIndex)}>
                Add Set
              </button>
            </div>
          </div>
        ))}
        <button onClick={handleAddExercise}>Add Exercise</button>
      </div>
      <div className="form-actions">
        <button onClick={handleSave}>Save</button>
        <button onClick={onCancel}>Cancel</button>
      </div>
    </div>
  );
};

export default CheckinForm;