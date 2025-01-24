import { Muscle, Exercise, Checkin } from "../types";


const GITHUB_API_BASE_URL = "https://api.github.com";
const REPO_OWNER = "DragosGeornoiu";
const REPO_NAME = "gym-tracker";
const MUSCLES_JSON = "data/muscles.json";
const EXERCISES_JSON = "data/exercises.json";
const CHECKINS_JSON = "data/checkins-2025.json";
const TOKEN = ""; // TODO: This should be injected or managed securely

// Generic fetch function
async function fetchData<T>(url: string): Promise<T> {
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Failed to fetch ${url}: ${response.statusText}`);
  }
  return response.json() as T;
}

// Fetch muscles from the JSON file
export async function fetchMuscles(): Promise<Muscle[]> {
  return fetchData<Muscle[]>("../../data/muscles.json");
}

export async function fetchExercises(): Promise<Exercise[]> {
  const muscles = await fetchMuscles();
  const exercises = await fetchData<Exercise[]>("../../data/exercises.json");

  return exercises.map((exercise) => ({
    ...exercise,
    muscleName: muscles.find((muscle) => muscle.id === exercise.muscleId)?.name || "Unknown Muscle",
  }));
}

export async function fetchCheckins(): Promise<Checkin[]> {
  return fetchData<Checkin[]>("../../data/checkins-2025.json");
}

// Retrieve the SHA of a file in the GitHub repository
export async function getFileSHA(filePath: string): Promise<string> {
  const url = `${GITHUB_API_BASE_URL}/repos/${REPO_OWNER}/${REPO_NAME}/contents/${filePath}`;
  const response = await fetch(url, {
    headers: {
      Authorization: `Bearer ${TOKEN}`,
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch file SHA for ${filePath}: ${response.statusText}`);
  }

  const data = await response.json();
  return data.sha;
}

// Save data to the GitHub repository
export async function saveData(filePath: string, data: any[], commitMessage: string): Promise<void> {
  const sha = await getFileSHA(filePath);

  const url = `${GITHUB_API_BASE_URL}/repos/${REPO_OWNER}/${REPO_NAME}/contents/${filePath}`;
  const response = await fetch(url, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${TOKEN}`,
    },
    body: JSON.stringify({
      message: commitMessage,
      content: btoa(JSON.stringify(data, null, 2)), // Base64 encode JSON
      sha,
    }),
  });

  if (!response.ok) {
    throw new Error(`Failed to save data to ${filePath}: ${response.statusText}`);
  }
}

// Save exercises back to the GitHub repository
export async function saveExercises(exercises: Exercise[]): Promise<void> {
  const formattedExercises = exercises.map(({ muscleName, ...rest }) => rest); // Exclude muscleName
  await saveData(EXERCISES_JSON, formattedExercises, "Update exercises.json via app");
}

// Save muscles back to the GitHub repository
export async function saveMuscles(muscles: Muscle[]): Promise<void> {
  await saveData(MUSCLES_JSON, muscles, "Update muscles.json via app");
}

export async function saveCheckins(checkins: Checkin[]): Promise<void> {
  // Fetch the existing checkins
  const existingCheckins = await fetchCheckins();

  // Add the new checkin to the list
  existingCheckins.push(checkins[0]);

  // Sort by date in ascending order (oldest first)
  existingCheckins.sort((a, b) => {
    const dateA = new Date(a.date);
    const dateB = new Date(b.date);
    return dateB.getTime() - dateA.getTime(); // Compare dates
  });

  // Save the updated checkins back to GitHub
  await saveData(CHECKINS_JSON, existingCheckins, "Add new checkin to checkins-2025.json");
}