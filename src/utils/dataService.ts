import { Muscle, Exercise, Workout } from "../types";

export async function fetchData<T>(url: string): Promise<T> {
    const response = await fetch(url);
    if(!response.ok) {
        throw new Error('Failed to fetch ${url}: #{response.statusText}');
    }

    const data = await response.json();
    return data as T;
}

export async function fetchMuscles(): Promise<Muscle[]> {
    return fetchData<Muscle[]>('../../data/muscles.json');
}