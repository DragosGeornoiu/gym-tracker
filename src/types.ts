export type Muscle = {
  id: number;
  name: string;
};

export type Exercise = {
  id: number;
  name: string;
  muscleId: number;
  muscleName?: string; // Optional for displaying muscle name
};

export type Set = {
  isWarmup: boolean;
  weight: number;
  reps: number;
};

export type Checkin = {
  date: string;
  exercises: {
    exerciseId: string;
    sets: Set[];
  }[];
};
