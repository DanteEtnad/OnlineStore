import React, { useState } from 'react';
import { Form, Button, Alert, Modal } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import StoreDataService from "../services/store.service"; // 导入API请求服务
import './Register.css'; // 导入样式

function Register() {
    const [username, setUsername] = useState(''); // 存储用户名
    const [email, setEmail] = useState(''); // 存储邮箱
    const [password, setPassword] = useState(''); // 存储密码
    const [errorMessage, setErrorMessage] = useState(''); // 错误消息
    const [submitted, setSubmitted] = useState(false); // 跟踪表单是否提交成功
    const [showModal, setShowModal] = useState(false); // 控制模态框显示
    const navigate = useNavigate(); // 用于页面跳转

    // 处理表单提交
    const handleRegister = (e) => {
        e.preventDefault();

        // 验证字段是否为空
        if (!username || !email || !password) {
            setErrorMessage("All fields are required!");
            return;
        }

        // 构造用户数据
        const userData = {
            name: username,
            email,
            password,
        };

        // 发送注册请求
        StoreDataService.register(userData.name, userData.email, userData.password)
            .then(response => {
                const { status, message } = response.data;

                if (status === "success") {
                    console.log("Registration successful:", response.data);
                    setSubmitted(true);
                    setErrorMessage(""); // 清空错误消息

                    // 显示成功模态框
                    setShowModal(true);
                } else {
                    // 如果返回的是错误信息，则显示错误信息
                    setErrorMessage(message || "Registration failed, please try again later.");
                    setSubmitted(false); // 确保不会显示注册成功
                }
            })
            .catch(e => {
                // 处理注册错误，确保仅显示可读的错误消息
                const errorResponse = e.response?.data;
                const errorMsg = typeof errorResponse === 'string' ? errorResponse : "Registration failed, please try again later.";
                setErrorMessage(errorMsg);
                setSubmitted(false); // 确保不会显示注册成功
                console.error("Registration error:", e.response?.data || e.message);
            });
    };

    // 关闭模态框并跳转到登录页面
    const handleModalClose = () => {
        setShowModal(false);
        navigate('/login'); // 跳转到登录页面
    };

    return (
        <div className="register-wrapper">
            <div className="register-form-container">
                {submitted ? (
                    <div>
                        <h4 className="register-title">Registration successful!</h4>
                    </div>
                ) : (
                    <Form onSubmit={handleRegister} className="register-form">
                        {errorMessage && (
                            <Alert variant="danger" className="register-alert">
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

                        <Form.Group controlId="formBasicEmail" className="mb-3">
                            <Form.Label>Email</Form.Label>
                            <Form.Control
                                type="email" // 使用 "email" 类型来验证邮箱格式
                                placeholder="Enter email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                isInvalid={!!errorMessage && !email}
                            />
                            <Form.Control.Feedback type="invalid">
                                Email is required.
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

                        <Button variant="success" type="submit" className="register-button w-100">
                            Register
                        </Button>
                    </Form>
                )}

                {/* 模态框显示注册成功 */}
                <Modal show={showModal} onHide={handleModalClose}>
                    <Modal.Header closeButton>
                        <Modal.Title>Registration Successful</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        Your account has been created successfully!
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

export default Register;
