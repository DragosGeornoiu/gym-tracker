export type Muscle = {
    id: number;
    name: string;
  };
  
  export type Exercise = {
    id: number;
    name: string;
    muscleId: number;
  };
  
export type Set = {
    isWarmup: boolean;
    weight: number;
    reps: number;
}

  export type Checkin = {
    date: string;
    exercises: {
      exerciseId: string;
      sets: Set[];
    }[];
  };
  