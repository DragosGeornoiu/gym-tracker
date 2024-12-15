import React from 'react'
import { useFetchData } from '../utils/useFetchData'
import { fetchMuscles } from '../utils/dataService'
import {Muscle} from '../types'
 
function MusclesPage() {
  const {data: muscles, loading, error} = useFetchData<Muscle[]>(fetchMuscles)

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error.message}</p>;

  console.log(muscles);
  return (
    <div>
      <h1>MusclesPage</h1>
      {muscles?.map(muscle => (
        <div key={muscle.id}>
          <h2>{muscle.name}</h2>
        </div>
      ))}
    </div>
  )
}

export default MusclesPage