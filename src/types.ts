
export type Muscle = {
    id: number,
    name: string;
};


export type Exercise = {
    id: number,
    name: string,
    muscleIds: number[];
};

export type Workout = {
    name: string,
    
}