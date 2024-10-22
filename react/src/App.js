import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Home from './components/Homepage';
import Login from './components/Login';
import Register from './components/Register';
import Store from './components/Store';
import Payment from './components/Payment'; // 引入 Payment 组件

function App() {
    return (
        <AuthProvider>
            <div className="App">
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/store" element={<Store />} />
                    <Route path="/payment" element={<Payment />} /> {/* 添加 payment 路由 */}
                </Routes>
            </div>
        </AuthProvider>
    );
}

export default App;
