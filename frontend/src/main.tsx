import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext.tsx'
import { Toaster } from 'react-hot-toast'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <Toaster
          position="top-right"
          reverseOrder={false}  
          gutter={12}  
          toastOptions={{
            duration: 3000,  
            style: {
              background: '#16213e',
              color: '#fff',
              border: '1px solid #2a2a4a',
              borderRadius: '12px',
              fontSize: '14px',
              fontWeight: '500',
              padding: '12px 16px',
              maxWidth: '400px',
            },
            success: {
              iconTheme: {
                primary: '#6366f1',
                secondary: '#fff',
              },
              style: {
                border: '1px solid #6366f1',
              },
            },
            error: {
              iconTheme: {
                primary: '#ef4444',
                secondary: '#fff',
              },
              style: {
                border: '1px solid #ef4444',
              },
            },
          }}
        />
        <App />
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>,
)