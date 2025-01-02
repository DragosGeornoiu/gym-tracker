import React from 'react'
import {Routes, Route} from 'react-router-dom'
import ExercisesPage from './pages/ExercisesPage'
import MusclesPage from './pages/MusclesPage'
import NotFoundPage from './pages/NotFoundPage'
import HomePage from './pages/HomePage'
import { Table} from './components/table/Table'
import './styles.css'; 

const App = () => {
  return (
    <>
      <Routes>
        <Route path='/' element={<HomePage />} />
        <Route path='/muscles' element={<MusclesPage />} />
        <Route path='/exercises' element={<ExercisesPage />} />
        <Route path='/notFound' element={<NotFoundPage />} />
        <Route path='/table' element={<Table />} />

      </Routes>
    </>    
  )
}

export default App