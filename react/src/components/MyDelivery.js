import React, { useState, useEffect } from 'react';
import { Card, Container, Row, Col, Modal, Button } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext'; // 用于获取用户信息
import './MyDelivery.css'; // 你可以根据需要设计 CSS 样式

function MyDelivery() {
    const { user } = useAuth(); // 获取当前登录用户的信息
    const [customerId, setCustomerId] = useState(null); // 用于保存 customerId
    const [orders, setOrders] = useState([]); // 初始化为空数组，避免 map 错误
    const [error, setError] = useState(null); // 用于处理错误信息
    const [showModal, setShowModal] = useState(false); // 控制弹窗显示
    const [lastOrderUpdateTime, setLastOrderUpdateTime] = useState(Date.now()); // 记录最后一次订单的更新时间

    let orderTimer = null; // 用于订单更新超时的计时器

    // 在组件加载时检查 localStorage 中是否有保存的 customerId
    useEffect(() => {
        if (user && user.customerId) {
            setCustomerId(user.customerId);
            localStorage.setItem('customerId', user.customerId); // 将 customerId 保存到 localStorage
        } else {
            const storedCustomerId = localStorage.getItem('customerId');
            if (storedCustomerId) {
                setCustomerId(storedCustomerId); // 如果 localStorage 中有保存的 customerId，则恢复该值
            }
        }
    }, [user]);

    // 函数用于重置订单更新计时器
    const resetOrderTimer = () => {
        clearTimeout(orderTimer);
        orderTimer = setTimeout(() => {
            setShowModal(true); // 显示弹窗，表示30秒内没有更新订单
        }, 30000); // 30秒没有更新则触发
    };

    // 函数用于重置订单更新计时器并刷新 SSE 连接
    const refreshOrderConnection = () => {
        // 清除现有计时器
        clearTimeout(orderTimer);
        setShowModal(false); // 关闭弹窗

        // 模拟重新加载页面，强制刷新订单数据
        const newCustomerId = customerId; // 保存当前 customerId
        setCustomerId(null); // 暂时清除 customerId，强制 useEffect 重新运行
        setTimeout(() => setCustomerId(newCustomerId), 0); // 重新设置 customerId，触发 useEffect 重新连接 SSE
    };

    // 使用 effect 只处理订单逻辑
    useEffect(() => {
        if (customerId) {
            const orderEventSource = new EventSource(`http://localhost:8080/api/bank/customer/${customerId}`);

            orderEventSource.onmessage = (event) => {
                const receivedData = event.data;
                if (receivedData) {
                    const parsedData = JSON.parse(receivedData);
                    console.log("Received orders:", parsedData);
                    setOrders(parsedData);
                    setLastOrderUpdateTime(Date.now()); // 更新最后的订单更新时间
                    resetOrderTimer(); // 重置订单更新计时器
                } else {
                    console.log("Non-JSON data received:", receivedData);
                }
            };

            // SSE连接关闭处理
            orderEventSource.onclose = () => {
                console.log("Order connection closed by server");
                setShowModal(true); // 显示订单连接断开提示
            };

            // 错误处理
            orderEventSource.onerror = (error) => {
                console.error("Error with order SSE connection:", error);
                setError('Error receiving data from server.');
                setShowModal(true); // 显示连接中断提示
                orderEventSource.close();
            };

            // 清除 SSE 连接和计时器
            return () => {
                orderEventSource.close();
                clearTimeout(orderTimer); // 清除订单计时器
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

            {/* 连接中断提示弹窗 */}
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
