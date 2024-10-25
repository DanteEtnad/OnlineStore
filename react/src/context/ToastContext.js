import React, { createContext, useState, useContext, useEffect } from 'react';
import { Toast, ToastContainer } from 'react-bootstrap';

// 创建 ToastContext
const ToastContext = createContext();

// 创建 ToastProvider，用于封装通知逻辑和 SSE 连接
export const ToastProvider = ({ children }) => {
    const [toasts, setToasts] = useState([]); // 用于存储多个 Toast

    // 函数用于检查是否是 JSON 数据
    const isJson = (str) => {
        try {
            JSON.parse(str);
            return true;
        } catch (e) {
            return false;
        }
    };

    // 函数用于添加新的 Toast 到队列中
    const addToast = (message) => {
        setToasts((prevToasts) => [
            ...prevToasts,
            { id: Date.now(), message }
        ]);
    };

    // SSE 连接处理通知
    const connectNotificationSSE = (customerId) => {
        const notificationEventSource = new EventSource(`http://localhost:8080/api/email/notifications/${customerId}`);

        // 处理通知的 SSE
        notificationEventSource.onmessage = (event) => {
            const notificationData = event.data;
            console.log("New notification received:", notificationData); // 确保数据被正确接收
            if (isJson(notificationData)) {
                const parsedNotifications = JSON.parse(notificationData);
                parsedNotifications.forEach((notification) => {
                    addToast(notification); // 每个新通知弹出一个 Toast
                });
            }
        };

        // SSE连接关闭处理
        notificationEventSource.onclose = () => {
            console.log("Notification connection closed by server");
        };

        // 错误处理
        notificationEventSource.onerror = (error) => {
            console.error("Error with notification SSE connection:", error);
            notificationEventSource.close();
        };
    };

    // 删除已显示的 Toast
    const removeToast = (id) => {
        setToasts((prevToasts) => prevToasts.filter((toast) => toast.id !== id));
    };

    return (
        <ToastContext.Provider value={{ connectNotificationSSE }}>
            {children}
            <ToastContainer position="bottom-end" className="p-3">
                {toasts.map((toast) => (
                    <Toast
                        key={toast.id}
                        onClose={() => removeToast(toast.id)}
                        show={true} // Always show each toast
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

// 创建自定义 Hook 来使用 ToastContext
export const useToast = () => {
    return useContext(ToastContext);
};
