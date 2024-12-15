import React from 'react'
import {Routes, Route} from 'react-router-dom'
import ExercisesPage from './pages/ExercisesPage'
import MusclesPage from './pages/MusclesPage'
import NotFoundPage from './pages/NotFoundPage'
import HomePage from './pages/HomePage'

const App = () => {
  return (
    <>
      <Routes>
        <Route path='/' element={<HomePage />} />
        <Route path='/muscles' element={<MusclesPage />} />
        <Route path='/exercises' element={<ExercisesPage />} />
        <Route path='/notFound' element={<NotFoundPage />} />

      </Routes>
    </>    
  )
}

export default App