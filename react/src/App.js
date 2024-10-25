import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import Home from './components/Homepage';
import Login from './components/Login';
import Register from './components/Register';
import Store from './components/Store';
import Payment from './components/Payment';
import MyDelivery from './components/MyDelivery'

function App() {
    return (
        <AuthProvider>
            <ToastProvider>
            <div className="App">
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/store" element={<Store />} />
                    <Route path="/payment" element={<Payment />} />
                    <Route path="/my-delivery" element={<MyDelivery />} />
                </Routes>
            </div>
            </ToastProvider>
        </AuthProvider>
    );
}

export default App;
