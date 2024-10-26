import React, { useState } from 'react';
import { Form, Button, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import StoreDataService from "../services/store.service"; // Import user service for API requests
import { useAuth } from '../context/AuthContext'; // Use useAuth instead of AuthContext
import './Login.css'; // Import stylesheet

function Login() {
    const [username, setUsername] = useState(''); // Store username
    const [password, setPassword] = useState(''); // Store password
    const [errorMessage, setErrorMessage] = useState(''); // Error message
    const [submitted, setSubmitted] = useState(false); // Track if form submission was successful
    const { login } = useAuth(); // Get login function from useAuth
    const navigate = useNavigate(); // For page redirection

    // Handle form submission
    const handleLogin = (e) => {
        e.preventDefault();

        // Validate if fields are empty
        if (!username || !password) {
            setErrorMessage("All fields are required!");
            return;
        }

        // Send login request
        StoreDataService.login(username, password)
            .then(response => {
                if (response.data.status === 'error') {
                    setErrorMessage(response.data.message);
                } else {
                    console.log("Login successful:", response.data);
                    setSubmitted(true);
                    setErrorMessage("");

                    // Ensure complete user data (e.g., customerId) is saved in AuthContext
                    const userData = response.data.data; // Assuming response.data.data contains customerId and other user information
                    login(userData); // Here, data now contains customerId and name

                    // Navigate to the store page
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
