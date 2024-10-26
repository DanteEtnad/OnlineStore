import React, { useState } from 'react';
import { Form, Button, Alert, Modal } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import StoreDataService from "../services/store.service"; // Import API request service
import './Register.css'; // Import styles

function Register() {
    const [username, setUsername] = useState(''); // Store username
    const [email, setEmail] = useState(''); // Store email
    const [password, setPassword] = useState(''); // Store password
    const [errorMessage, setErrorMessage] = useState(''); // Error message
    const [submitted, setSubmitted] = useState(false); // Track form submission success
    const [showModal, setShowModal] = useState(false); // Control modal display
    const navigate = useNavigate(); // For page navigation

    // Handle form submission
    const handleRegister = (e) => {
        e.preventDefault();

        // Validate if fields are empty
        if (!username || !email || !password) {
            setErrorMessage("All fields are required!");
            return;
        }

        // Construct user data
        const userData = {
            name: username,
            email,
            password,
        };

        // Send registration request
        StoreDataService.register(userData.name, userData.email, userData.password)
            .then(response => {
                const { status, message } = response.data;

                if (status === "success") {
                    console.log("Registration successful:", response.data);
                    setSubmitted(true);
                    setErrorMessage(""); // Clear error message

                    // Show success modal
                    setShowModal(true);
                } else {
                    // Display error message if returned
                    setErrorMessage(message || "Registration failed, please try again later.");
                    setSubmitted(false); // Ensure success message is not displayed
                }
            })
            .catch(e => {
                // Handle registration error and ensure readable error messages are displayed
                const errorResponse = e.response?.data;
                const errorMsg = typeof errorResponse === 'string' ? errorResponse : "Registration failed, please try again later.";
                setErrorMessage(errorMsg);
                setSubmitted(false); // Ensure success message is not displayed
                console.error("Registration error:", e.response?.data || e.message);
            });
    };

    // Close modal and navigate to login page
    const handleModalClose = () => {
        setShowModal(false);
        navigate('/login'); // Navigate to login page
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
                                type="email" // Use "email" type to validate email format
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

                {/* Modal displaying successful registration */}
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
