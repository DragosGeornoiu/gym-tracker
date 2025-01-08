import { useEffect, useState } from "react";
import { Table } from "../components/table/Table";
import { fetchMuscles, fetchExercises, fetchCheckins } from "../utils/dataService";
import { createColumnHelper } from "@tanstack/react-table";

const columnHelper = createColumnHelper<any>();

const CheckinsPage = () => {
  const [checkins, setCheckins] = useState<any[]>([]);
  const [exercises, setExercises] = useState<any[]>([]);
  const [muscles, setMuscles] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const fetchedCheckins = await fetchCheckins();
        const fetchedExercises = await fetchExercises();
        const fetchedMuscles = await fetchMuscles();
        
        setCheckins(fetchedCheckins);
        setExercises(fetchedExercises);
        setMuscles(fetchedMuscles);
      } catch (error) {
        console.error("Error fetching data:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);


  const getExerciseSummary = (exercise: any, exerciseData: any) => {
    const warmupSets = exercise.sets.filter((set: any) => set.isWarmup).length;
    const workingSets = exercise.sets.filter((set: any) => !set.isWarmup).length;

    return (
        <div className="exercise-summary">
        <div className="exercise-name">{exerciseData.name || "Unnamed Exercise"}</div>
        <div>
            <span className="tooltip">
            <span className="warmup-sets">{warmupSets} WS</span>
            <span className="tooltip-text">{`${warmupSets} Warmup Sets`}</span>
            </span>
            <span className="separator">-</span>
            <span className="tooltip">
            <span className="working-sets">{workingSets} WKS</span>
            <span className="tooltip-text">{`${workingSets} Work Sets`}</span>
            </span>
        </div>
        </div>
    );
    };
  

  // Helper function to generate muscle summary
  const getMuscleSummary = (checkinExercises: any[], allExercises: any[], muscles: any[]) => {
    const muscleMap = muscles.reduce((map, muscle) => {
      map[muscle.id] = muscle.name;
      return map;
    }, {} as Record<string, string>);
  
    const muscleNames = new Set(
      checkinExercises
        .map((checkinExercise) => {
          const exercise = allExercises.find((ex) => ex.id === checkinExercise.exerciseId);
          if (exercise) {
            return muscleMap[exercise.muscleId];
          }
          return null;
        })
        .filter(Boolean)
    );
  
    return (
      <div className="muscle-summary">
        {Array.from(muscleNames).map((name) => (
          <div key={name} className="muscle-name">{name}</div> // Adding appropriate class for each muscle
        ))}
      </div>
    );
  };
  

  // Process checkins data for Table
  const preprocessCheckinsData = (checkins: any[]) => {
    return checkins.map((checkin) => {
      const exerciseColumns = checkin.exercises.map((exercise: any, index: number) => {
        const exerciseData = exercises.find((e: any) => e.id === exercise.exerciseId) || { name: `Exercise ${exercise.exerciseId}` };
        
        return {
          header: `Exercise ${index + 1}`,
          accessor: `exercise_${index}`,
          cell: () => getExerciseSummary(exercise, exerciseData),
        };
      });

      return {
        date: checkin.date,
        exercises: exerciseColumns,
        muscleSummary: getMuscleSummary(checkin.exercises, exercises, muscles),
      };
    });
  };

  const processedData = preprocessCheckinsData(checkins);

  const columns = [
    columnHelper.accessor("date", {
      header: "Date",
      cell: (info) => info.getValue(),
    }),
    columnHelper.accessor("muscleSummary", {
      header: "Muscle Summary",
      cell: (info) => info.getValue(),
    }),
    ...processedData[0]?.exercises || [], // Add exercise columns dynamically
  ];

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h1>Checkins Page</h1>
      <Table
        data={processedData}
        setData={setCheckins}
        columns={columns}
      />
    </div>
  );
};

export default CheckinsPage;
