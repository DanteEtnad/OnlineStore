import React, { createContext, useState, useContext } from 'react';

// 创建 AuthContext
const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    // 从 localStorage 加载用户数据，确保页面刷新后仍保持登录状态
    const [user, setUser] = useState(() => {
        const storedUser = localStorage.getItem('user');
        return storedUser ? JSON.parse(storedUser) : null;
    });

    // 登录函数，将用户数据保存到状态和 localStorage
    const login = (userData) => {
        setUser(userData);
        localStorage.setItem('user', JSON.stringify(userData));
    };

    // 注销函数，清除状态和 localStorage 中的用户数据
    const logout = () => {
        setUser(null);
        localStorage.removeItem('user');
    };

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

// 自定义 Hook 用于在其他组件中轻松访问 AuthContext
export const useAuth = () => useContext(AuthContext);
