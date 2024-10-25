import React, { createContext, useState, useContext, useEffect } from 'react';
import { Toast, ToastContainer } from 'react-bootstrap';
import { v4 as uuidv4 } from 'uuid';
import { useAuth } from '../context/AuthContext';

const ToastContext = createContext();

export const ToastProvider = ({ children }) => {
    const { user } = useAuth();
    const [toasts, setToasts] = useState([]);
    const maxNotifications = 5;

    const addToast = (message) => {
        setToasts((prevToasts) => [
            ...prevToasts,
            { id: uuidv4(), message }
        ]);
    };

    useEffect(() => {
        if (!user?.customerId) return;

        const notificationEventSource = new EventSource(`http://localhost:8080/api/email/notifications/${user.customerId}`);

        notificationEventSource.onmessage = (event) => {
            const notificationData = event.data;
            console.log("New notification received:", notificationData);

            try {
                const parsedNotifications = JSON.parse(notificationData);

                if (Array.isArray(parsedNotifications) && parsedNotifications.length > maxNotifications) {
                    console.log(`Ignoring long notification array with ${parsedNotifications.length} items.`);
                    return;
                }

                parsedNotifications.forEach((notification) => addToast(notification));
            } catch (error) {
                console.error("Failed to parse notification:", error);
            }
        };

        notificationEventSource.onclose = () => {
            console.log("Notification connection closed by server");
        };

        notificationEventSource.onerror = (error) => {
            console.error("Error with notification SSE connection:", error);
            notificationEventSource.close();
        };

        return () => notificationEventSource.close();
    }, [user?.customerId]);

    const removeToast = (id) => {
        setToasts((prevToasts) => prevToasts.filter((toast) => toast.id !== id));
    };

    return (
        <ToastContext.Provider value={{ addToast }}>
            {children}
            <ToastContainer
                position="bottom-end"
                className="p-3"
                style={{
                    position: 'fixed', // 固定位置
                    bottom: '20px',
                    right: '20px',
                    zIndex: 1050 // 确保 Toast 在最前层
                }}
            >
                {toasts.slice(0, 3).map((toast) => (
                    <Toast
                        key={toast.id}
                        onClose={() => removeToast(toast.id)}
                        show={true}
                        delay={5000}
                        autohide
                    >
                        <Toast.Header>
                            <strong className="me-auto">Notification</strong>
                            <small>Just now</small>
                        </Toast.Header>
                        <Toast.Body>{toast.message}</Toast.Body>
                    </Toast>
                ))}
            </ToastContainer>
        </ToastContext.Provider>
    );
};

export const useToast = () => useContext(ToastContext);
