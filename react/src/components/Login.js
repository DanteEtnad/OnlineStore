import React, { useState } from 'react';
import { Form, Button, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import StoreDataService from "../services/store.service"; // 导入用户服务用于API请求
import { useAuth } from '../context/AuthContext'; // 使用 useAuth 代替 AuthContext
import './Login.css'; // 导入样式

function Login() {
    const [username, setUsername] = useState(''); // 存储用户名
    const [password, setPassword] = useState(''); // 存储密码
    const [errorMessage, setErrorMessage] = useState(''); // 错误消息
    const [submitted, setSubmitted] = useState(false); // 跟踪表单是否提交成功
    const { login } = useAuth(); // 从 useAuth 中获取 login 函数
    const navigate = useNavigate(); // 用于页面重定向

    // 处理表单提交
    const handleLogin = (e) => {
        e.preventDefault();

        // 验证字段是否为空
        if (!username || !password) {
            setErrorMessage("All fields are required!");
            return;
        }

        // 发送登录请求
        StoreDataService.login(username, password)
            .then(response => {
                if (response.data.status === 'error') {
                    setErrorMessage(response.data.message);
                } else {
                    console.log("Login successful:", response.data);
                    setSubmitted(true);
                    setErrorMessage("");

                    // 确保将完整的用户数据（例如 customerId）保存到 AuthContext
                    login(response.data.data); // 这里的 data 现在包含 customerId 和 name
                    navigate('/store');
                }
            })
            .catch(e => {
                console.error("Login error:", e.response?.data || e.message);
                setErrorMessage(e.response?.data || "Login failed, please try again later.");
            });

    };

    return (
        <div className="login-wrapper">
            <div className="login-form-container">
                {submitted ? (
                    <div>
                        <h4 className="login-title">Login successful!</h4>
                    </div>
                ) : (
                    <Form onSubmit={handleLogin} className="login-form">
                        {errorMessage && (
                            <Alert variant="danger" className="login-alert">
                                {errorMessage}
                            </Alert>
                        )}

                        <Form.Group controlId="formBasicUsername" className="mb-3">
                            <Form.Label>Username</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Enter username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                isInvalid={!!errorMessage && !username}
                            />
                            <Form.Control.Feedback type="invalid">
                                Username is required.
                            </Form.Control.Feedback>
                        </Form.Group>

                        <Form.Group controlId="formBasicPassword" className="mb-3">
                            <Form.Label>Password</Form.Label>
                            <Form.Control
                                type="password"
                                placeholder="Enter password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                isInvalid={!!errorMessage && !password}
                            />
                            <Form.Control.Feedback type="invalid">
                                Password is required.
                            </Form.Control.Feedback>
                        </Form.Group>

                        <Button variant="primary" type="submit" className="login-button w-100">
                            Login
                        </Button>
                    </Form>
                )}
            </div>
        </div>
    );
}

export default Login;
