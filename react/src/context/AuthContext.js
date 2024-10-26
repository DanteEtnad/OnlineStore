import React, { createContext, useState, useContext } from 'react';

// Create AuthContext
const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    // Load user data from localStorage to maintain login state after page refresh
    const [user, setUser] = useState(() => {
        const storedUser = localStorage.getItem('user');
        return storedUser ? JSON.parse(storedUser) : null;
    });

    // Login function to save user data to state and localStorage
    const login = (userData) => {
        setUser(userData);
        localStorage.setItem('user', JSON.stringify(userData));
    };

    // Logout function to clear user data from state and localStorage
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

// Custom hook for easy access to AuthContext in other components
export const useAuth = () => useContext(AuthContext);
