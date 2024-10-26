import React, { useState, useEffect } from 'react';
import { Card, Container, Row, Col, Modal, Button } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext'; // To get user information
import './MyDelivery.css'; // Customize CSS styles as needed

function MyDelivery() {
    const { user } = useAuth(); // Get the information of the currently logged-in user
    const [customerId, setCustomerId] = useState(null); // To store customerId
    const [orders, setOrders] = useState([]); // Initialize as an empty array to avoid map errors
    const [error, setError] = useState(null); // To handle error messages
    const [showModal, setShowModal] = useState(false); // Control modal visibility
    const [lastOrderUpdateTime, setLastOrderUpdateTime] = useState(Date.now()); // Record the last update time for orders

    let orderTimer = null; // Timer to track order update timeout

    // Check if there is a saved customerId in localStorage when the component loads
    useEffect(() => {
        if (user && user.customerId) {
            setCustomerId(user.customerId);
            localStorage.setItem('customerId', user.customerId); // Save customerId to localStorage
        } else {
            const storedCustomerId = localStorage.getItem('customerId');
            if (storedCustomerId) {
                setCustomerId(storedCustomerId); // Restore customerId if it exists in localStorage
            }
        }
    }, [user]);

    // Function to reset the order update timer
    const resetOrderTimer = () => {
        clearTimeout(orderTimer);
        orderTimer = setTimeout(() => {
            setShowModal(true); // Show modal if no order update within 30 seconds
        }, 30000); // Trigger after 30 seconds
    };

    // Function to reset the order update timer and refresh the SSE connection
    const refreshOrderConnection = () => {
        // Clear existing timer
        clearTimeout(orderTimer);
        setShowModal(false); // Close modal

        // Simulate reloading the page to force order data refresh
        const newCustomerId = customerId; // Save current customerId
        setCustomerId(null); // Temporarily clear customerId to force useEffect to re-run
        setTimeout(() => setCustomerId(newCustomerId), 0); // Reset customerId to trigger SSE reconnection
    };

    // Use effect to handle order logic only
    useEffect(() => {
        if (customerId) {
            const orderEventSource = new EventSource(`http://localhost:8080/api/bank/customer/${customerId}`);

            orderEventSource.onmessage = (event) => {
                const receivedData = event.data;
                if (receivedData) {
                    const parsedData = JSON.parse(receivedData);
                    console.log("Received orders:", parsedData);
                    setOrders(parsedData);
                    setLastOrderUpdateTime(Date.now()); // Update last order update time
                    resetOrderTimer(); // Reset order update timer
                } else {
                    console.log("Non-JSON data received:", receivedData);
                }
            };

            // Handle SSE connection closure
            orderEventSource.onclose = () => {
                console.log("Order connection closed by server");
                setShowModal(true); // Show disconnection prompt
            };

            // Error handling
            orderEventSource.onerror = (error) => {
                console.error("Error with order SSE connection:", error);
                setError('Error receiving data from server.');
                setShowModal(true); // Show disconnection prompt
                orderEventSource.close();
            };

            // Clean up SSE connection and timer
            return () => {
                orderEventSource.close();
                clearTimeout(orderTimer); // Clear order timer
            };
        }
    }, [customerId]);

    return (
        <Container className="my-delivery-container mt-4">
            <h2>Order List for Customer {customerId || 'N/A'}</h2>
            {error && <p className="error-message">{error}</p>}
            <Row>
                {orders && orders.length > 0 ? (
                    orders.map(order => (
                        <Col key={order.orderId} sm={12} md={6} lg={4} className="mb-4">
                            <Card className="h-100">
                                <Card.Body>
                                    <Card.Title>Order ID: {order.orderId}</Card.Title>
                                    <Card.Text>
                                        <strong>Status:</strong> {order.status}<br />
                                        <strong>Order Date:</strong> {new Date(order.orderDate).toLocaleString()}<br />
                                        <strong>Quantity:</strong> {order.quantity}<br />
                                        <strong>Total Amount:</strong> ${order.totalAmount.toFixed(2)}<br />
                                        <strong>Bank Transfer ID:</strong> {order.bankTransferId || 'N/A'}<br />
                                        <strong>Product ID:</strong> {order.productId}
                                    </Card.Text>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))
                ) : (
                    <p>No orders found for this customer.</p>
                )}
            </Row>

            {/* Disconnection prompt modal */}
            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Connection Lost</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    It has been 30 seconds with no updates for orders. The connection seems to be lost. Would you like to refresh the page to reconnect?
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>Close</Button>
                    <Button variant="primary" onClick={refreshOrderConnection}>Refresh</Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
}

export default MyDelivery;
