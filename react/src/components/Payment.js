import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Button, Container, Form, Alert, Spinner } from 'react-bootstrap';
import StoreDataService from '../services/store.service'; // 导入服务
import './Payment.css';

function Payment() {
    const location = useLocation();
    const navigate = useNavigate(); // 用于页面跳转
    const { orderId, productId, quantity, productName, price, totalAmount, customerId } = location.state || {}; // 从路由状态中获取订单信息
    const [accountId, setAccountId] = useState(''); // 用户输入的账户ID
    const [paymentStatus, setPaymentStatus] = useState(''); // 支付状态信息
    const [bankTransferId, setBankTransferId] = useState(null); // 用于存储支付的 bankTransferId
    const [isPending, setIsPending] = useState(false); // 控制是否显示 pending 状态的转圈

    // 处理支付
    const handlePayment = () => {
        if (!accountId) {
            alert('Please enter your account ID.');
            return;
        }

        // 调用服务方法发起支付请求
        StoreDataService.createPayment(customerId, orderId, accountId) // 确保传递正确的参数
            .then(response => {
                if (response.data.status === 'success') {
                    const transferId = response.data.data;
                    setBankTransferId(transferId); // 存储 transferId
                    setIsPending(true); // 设置为 pending 状态
                    setPaymentStatus('Payment initiated. Checking status...');
                    pollPaymentStatus(transferId); // 开始轮询支付状态
                } else {
                    setPaymentStatus(`Payment failed: ${response.data.message}`);
                }
            })
            .catch(error => {
                setPaymentStatus(`Payment error: ${error.message}`);
            });
    };

    // 轮询支付状态函数
    const pollPaymentStatus = (transferId) => {
        const intervalId = setInterval(() => {
            StoreDataService.getPaymentStatus(transferId)
                .then(response => {
                    if (response.data.status === 'success') {
                        const status = response.data.data;
                        if (status === 'completed') {
                            setPaymentStatus('Payment completed successfully!');
                            setIsPending(false); // 停止显示转圈
                            clearInterval(intervalId); // 停止轮询
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
        }, 3000); // 每3秒检查一次状态
    };

    // 处理退款请求
    const handleRefund = () => {
        StoreDataService.refundOrder(customerId, orderId, accountId) // 确保传递 customerId, orderId 和 accountId
            .then(response => {
                const { message } = response.data;
                if (message === 'Refund initiated successfully.') {
                    alert('Refund successful!');
                    // 跳转到商店页面
                    navigate('/store');
                } else {
                    alert('Refund failed: ' + message);
                }
            })
            .catch(error => {
                alert('Refund error: ' + error.message);
            });
    };

    // 处理订单确认
    const handleConfirm = () => {
        StoreDataService.checkOrderStatus(customerId, orderId)
            .then(response => {
                const { message } = response.data;
                if (response.data.status === 'order changed to paid') {
                    alert(message);
                    // 跳转到 MyDelivery 页面
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
                {isPending && <Spinner animation="border" className="mt-3" />} {/* 显示转圈 */}
                {paymentStatus && <Alert variant="info" className="mt-3">{paymentStatus}</Alert>}

                {/* 显示退款和确认订单选项，只在支付成功后显示 */}
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
