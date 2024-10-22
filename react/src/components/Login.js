import React, { useState } from 'react';
import { Form, Button, Alert, Modal } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import StoreDataService from "../services/store.service"; // 导入用户服务用于API请求
import './Login.css'; // 导入样式

function Login() {
    const [username, setUsername] = useState(''); // 存储用户名
    const [password, setPassword] = useState(''); // 存储密码
    const [errorMessage, setErrorMessage] = useState(''); // 错误消息
    const [submitted, setSubmitted] = useState(false); // 跟踪表单是否提交成功
    const [showModal, setShowModal] = useState(false); // 控制模态框的显示
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
                const { status, message } = response.data;

                // 如果后端返回错误状态
                if (status === "error") {
                    setErrorMessage(message || "Login failed, please try again.");
                    setSubmitted(false);
                } else {
                    console.log("Login successful:", response.data);

                    // 设置提交状态并清除错误消息
                    setSubmitted(true);
                    setErrorMessage("");

                    // 显示成功模态框
                    setShowModal(true);
                }
            })
            .catch(e => {
                console.error("Login error:", e.response?.data || e.message);
                setErrorMessage(e.response?.data?.message || "Login failed, please try again later.");
                setSubmitted(false); // 确保在失败的情况下，`submitted` 不会是 true
            });
    };

    // 关闭模态框并重定向到主页或其他页面
    const handleModalClose = () => {
        setShowModal(false);
        navigate('/'); // 跳转到主页
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

                {/* 模态框显示登录成功 */}
                <Modal show={showModal} onHide={handleModalClose}>
                    <Modal.Header closeButton>
                        <Modal.Title>Login Successful</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        Welcome back, {username}!
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="primary" onClick={handleModalClose}>
                            OK
                        </Button>
                    </Modal.Footer>
                </Modal>
            </div>
        </div>
    );
}

export default Login;
