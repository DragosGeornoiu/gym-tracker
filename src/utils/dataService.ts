import { Muscle, Exercise, Workout } from "../types";


const GITHUB_API_BASE_URL = "https://api.github.com";
const REPO_OWNER = "DragosGeornoiu";
const REPO_NAME = "gym-tracker";
const MUSCLES_JSON = "data/muscles.json";
const TOKEN = ""; //TODO this should be injected or something

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

export async function getFileSHA(): Promise<string> {
  const url = `${GITHUB_API_BASE_URL}/repos/${REPO_OWNER}/${REPO_NAME}/contents/${MUSCLES_JSON}`;
  const response = await fetch(url, {
    headers: {
      Authorization: `Bearer ${TOKEN}`,
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch file SHA: ${response.statusText}`);
  }

  const data = await response.json();
  return data.sha;
}

// Save updated data to GitHub
export async function saveMuscles(muscles: any[]): Promise<void> {
  const sha = await getFileSHA();

  const url = `${GITHUB_API_BASE_URL}/repos/${REPO_OWNER}/${REPO_NAME}/contents/${MUSCLES_JSON}`;
  const response = await fetch(url, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${TOKEN}`,
    },
    body: JSON.stringify({
      message: "Update muscles.json via app",
      content: btoa(JSON.stringify(muscles, null, 2)), // Base64 encode JSON
      sha,
    }),
  });

  if (!response.ok) {
    throw new Error(`Failed to save data: ${response.statusText}`);
  }
}