import React from 'react'
import {Routes, Route} from 'react-router-dom'
import MusclesPage from './pages/MusclesPage'
import ExercisesPage from './pages/ExercisesPage'
import CheckinsPage from './pages/CheckinsPage'
import AddCheckin from './pages/AddCheckin'
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
        <Route path='/checkins' element={<CheckinsPage />} />
        <Route path='/add-checkin' element={<AddCheckin />} />
        <Route path='/notFound' element={<NotFoundPage />} />

      </Routes>
    </>    
  )
}

export default App