import logo from './logo.svg';
import './App.css';
import React from 'react';
import { useGet } from './services/api-service';
import { IDebtor } from './shared/models/Debtor/interface';

function App() {
  const { data, loading, error } = useGet<IDebtor[]>('creditor/{id}/debtcases', { id: 1 });
  //console.log(data);
  {data && data.map(debtor => (
    console.log(debtor)
  ))}

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error.message}</div>;
  }

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;