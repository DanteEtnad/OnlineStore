import React, { useState } from 'react';
import { useLocation } from 'react-router-dom';
import { Button, Container, Form, Alert } from 'react-bootstrap';
import StoreDataService from '../services/store.service'; // 导入服务
import './Payment.css';

function Payment() {
    const location = useLocation();
    const { orderId, productId, quantity, productName, customerId } = location.state || {}; // 从路由状态中获取订单信息
    const [accountId, setAccountId] = useState('');
    const [paymentStatus, setPaymentStatus] = useState('');
    const [showPostPaymentOptions, setShowPostPaymentOptions] = useState(false); // 控制退款和配送选项的显示

    // 处理支付
    const handlePayment = () => {
        if (!accountId) {
            alert('Please enter your account ID.');
            return;
        }

        StoreDataService.createPayment(orderId, accountId) // 确保传递正确的参数
            .then(response => {
                if (response.data.status === 'success') {
                    setPaymentStatus('Payment successful!');
                    setShowPostPaymentOptions(true); // 显示退款和配送选项
                } else {
                    setPaymentStatus(`Payment failed: ${response.data.message}`);
                }
            })
            .catch(error => {
                setPaymentStatus(`Payment error: ${error.message}`);
            });
    };

    // 处理退款请求
    const handleRefund = () => {
        StoreDataService.refundOrder(orderId, accountId)
            .then(response => {
                if (response.data.status === 'success') {
                    alert('Refund successful!');
                } else {
                    alert('Refund failed: ' + response.data.message);
                }
            })
            .catch(error => {
                alert('Refund error: ' + error.message);
            });
    };

    // 处理配送请求
    const handleDelivery = () => {
        alert('Order is being delivered!');
        // 这里可以添加调用配送API的逻辑
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
                    <Form.Label>Product ID:</Form.Label>
                    <span>{productId}</span>
                </div>
                <div className="mb-3 d-flex justify-content-between">
                    <Form.Label>Product Name:</Form.Label>
                    <span>{productName}</span>
                </div>
                <div className="mb-3 d-flex justify-content-between">
                    <Form.Label>Quantity:</Form.Label>
                    <span>{quantity}</span>
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
                {paymentStatus && <Alert variant="info" className="mt-3">{paymentStatus}</Alert>}

                {/* 显示退款和配送选项 */}
                {showPostPaymentOptions && (
                    <div className="mt-4">
                        <Button variant="danger" onClick={handleRefund} className="mr-2">
                            Request Refund
                        </Button>
                        <Button variant="primary" onClick={handleDelivery}>
                            Deliver Order
                        </Button>
                    </div>
                )}
            </Form>
        </Container>
    );
}

export default Payment;
