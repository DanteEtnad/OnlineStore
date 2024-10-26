import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Button, Container, Form, Alert, Spinner } from 'react-bootstrap';
import StoreDataService from '../services/store.service'; // Import service
import './Payment.css';

function Payment() {
    const location = useLocation();
    const navigate = useNavigate(); // For page navigation
    const { orderId, productId, quantity, productName, price, totalAmount, customerId } = location.state || {}; // Retrieve order information from route state
    const [accountId, setAccountId] = useState(''); // User-entered account ID
    const [paymentStatus, setPaymentStatus] = useState(''); // Payment status message
    const [bankTransferId, setBankTransferId] = useState(null); // Store bankTransferId of the payment
    const [isPending, setIsPending] = useState(false); // Controls whether to show pending spinner

    // Handle payment
    const handlePayment = () => {
        if (!accountId) {
            alert('Please enter your account ID.');
            return;
        }

        // Call service method to initiate payment request
        StoreDataService.createPayment(customerId, orderId, accountId) // Ensure correct parameters are passed
            .then(response => {
                if (response.data.status === 'success') {
                    const transferId = response.data.data;
                    setBankTransferId(transferId); // Store transferId
                    setIsPending(true); // Set to pending state
                    setPaymentStatus('Payment initiated. Checking status...');
                    pollPaymentStatus(transferId); // Start polling payment status
                } else {
                    setPaymentStatus(`Payment failed: ${response.data.message}`);
                }
            })
            .catch(error => {
                setPaymentStatus(`Payment error: ${error.message}`);
            });
    };

    // Function to poll payment status
    const pollPaymentStatus = (transferId) => {
        const intervalId = setInterval(() => {
            StoreDataService.getPaymentStatus(transferId)
                .then(response => {
                    if (response.data.status === 'success') {
                        const status = response.data.data;
                        if (status === 'completed') {
                            setPaymentStatus('Payment completed successfully!');
                            setIsPending(false); // Stop showing spinner
                            clearInterval(intervalId); // Stop polling
                        } else if (status === 'failed') {
                            setPaymentStatus('Payment failed.');
                            setIsPending(false);
                            clearInterval(intervalId);
                        } else {
                            setPaymentStatus('Payment is still pending...');
                        }
                    }
                })
                .catch(error => {
                    setPaymentStatus(`Error checking payment status: ${error.message}`);
                    clearInterval(intervalId);
                });
        }, 3000); // Check status every 3 seconds
    };

    // Handle refund request
    const handleRefund = () => {
        StoreDataService.refundOrder(customerId, orderId, accountId) // Ensure customerId, orderId, and accountId are passed
            .then(response => {
                const { message } = response.data;
                if (message === 'Refund initiated successfully.') {
                    alert('Refund successful!');
                    // Navigate to the store page
                    navigate('/store');
                } else {
                    alert('Refund failed: ' + message);
                }
            })
            .catch(error => {
                alert('Refund error: ' + error.message);
            });
    };

    // Handle order confirmation
    const handleConfirm = () => {
        StoreDataService.checkOrderStatus(customerId, orderId)
            .then(response => {
                const { message } = response.data;
                if (response.data.status === 'order changed to paid') {
                    alert(message);
                    // Navigate to MyDelivery page
                    navigate('/my-delivery');
                } else {
                    alert('Order confirmation failed: ' + message);
                }
            })
            .catch(error => {
                alert('Order confirmation error: ' + error.message);
            });
    };

    return (
        <Container className="payment-container mt-4">
            <h2>Payment Information</h2>
            <Form>
                <div className="mb-3 d-flex justify-content-between">
                    <Form.Label>Order ID:</Form.Label>
                    <span>{orderId}</span>
                </div>
                <div className="mb-3 d-flex justify-content-between">
                    <Form.Label>Product Name:</Form.Label>
                    <span>{productName}</span>
                </div>
                <div className="mb-3 d-flex justify-content-between">
                    <Form.Label>Product ID:</Form.Label>
                    <span>{productId}</span>
                </div>
                <div className="mb-3 d-flex justify-content-between">
                    <Form.Label>Quantity:</Form.Label>
                    <span>{quantity}</span>
                </div>
                <div className="mb-3 d-flex justify-content-between">
                    <Form.Label>Unit Price:</Form.Label>
                    <span>${price.toFixed(2)}</span>
                </div>
                <div className="mb-3 d-flex justify-content-between">
                    <Form.Label>Total Amount:</Form.Label>
                    <span>${totalAmount.toFixed(2)}</span>
                </div>
                <div className="mb-3 d-flex justify-content-between">
                    <Form.Label>Customer ID:</Form.Label>
                    <span>{customerId}</span>
                </div>
                <Form.Group controlId="formAccountId">
                    <Form.Label>Account ID</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="Enter your account ID"
                        value={accountId}
                        onChange={(e) => setAccountId(e.target.value)}
                    />
                </Form.Group>
                <Button variant="success" onClick={handlePayment} className="mt-3">
                    Pay Now
                </Button>
                {isPending && <Spinner animation="border" className="mt-3" />} {/* Show spinner */}
                {paymentStatus && <Alert variant="info" className="mt-3">{paymentStatus}</Alert>}

                {/* Show refund and confirm order options only after successful payment */}
                {bankTransferId && paymentStatus === 'Payment completed successfully!' && (
                    <div className="mt-4">
                        <Button variant="danger" onClick={handleRefund} className="mr-2">
                            Request Refund
                        </Button>
                        <Button variant="primary" onClick={handleConfirm}>
                            Confirm Order
                        </Button>
                    </div>
                )}
            </Form>
        </Container>
    );
}

export default Payment;
