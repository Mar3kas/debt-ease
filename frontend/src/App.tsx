import './App.css';
import logo from './logo.svg';
import React from 'react';
import { useGet } from './services/api-service';
import { IDebtor } from './shared/models/Debtor/interface';
import { HomePage } from './pages/Home';
import { SnackbarType } from './Components/Snackbar';

function App() {
  const { data, loading, error } = useGet<IDebtor[]>('creditor/{username}/debtcases', { username: "finserv" });
  return (
    <div>
      <HomePage openSnackbar={function (message: string, type: SnackbarType): void {
      }} />
    </div>
  );
}

export default App;