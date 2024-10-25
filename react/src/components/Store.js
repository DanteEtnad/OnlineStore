import React, { useState, useEffect } from 'react';
import StoreDataService from '../services/store.service'; // 导入服务
import { Card, Button, InputGroup, FormControl, Container, Row, Col, Modal } from 'react-bootstrap';
import './Store.css';
import { useAuth } from '../context/AuthContext'; // 导入认证上下文，用于获取用户信息
import { useNavigate } from 'react-router-dom'; // 导入用于跳转的导航钩子

function Store() {
    const { user } = useAuth(); // 获取当前登录用户的信息
    const [products, setProducts] = useState([]);
    const [quantities, setQuantities] = useState({});
    const [showModal, setShowModal] = useState(false);
    const [orderDetails, setOrderDetails] = useState({});
    const [stockError, setStockError] = useState(false); // 用于控制库存不足的弹窗
    const navigate = useNavigate(); // 用于跳转到支付页面

    // 获取所有产品数据
    useEffect(() => {
        StoreDataService.getAllProducts()
            .then(response => {
                setProducts(response.data);
                const initialQuantities = {};
                response.data.forEach(product => {
                    initialQuantities[product.productId] = 1;
                });
                setQuantities(initialQuantities);
            })
            .catch(e => {
                console.error(e);
            });
    }, []);

    // 处理数量变化
    const handleQuantityChange = (productId, newQuantity) => {
        setQuantities(prevQuantities => ({
            ...prevQuantities,
            [productId]: newQuantity > 0 ? newQuantity : 1,
        }));
    };

    // 调用 store.service.js 中的 placeOrder 函数进行下单
    const handleBuy = (productId) => {
        const quantity = quantities[productId];
        if (!user) {
            alert('User is not logged in');
            return;
        }

        StoreDataService.placeOrder(user.customerId, productId, quantity)
            .then(response => {
                if (response.data.status === 'success') {
                    const { orderId, productName, totalAmount } = response.data.data; // 解构获取订单信息
                    const selectedProduct = products.find(product => product.productId === productId);

                    // 调用库存分配接口，检查库存是否足够
                    StoreDataService.allocateWarehouseForOrder(orderId)
                        .then(allocateResponse => {
                            if (allocateResponse.data.status === 'success') {
                                // 如果库存足够，跳转到支付页面
                                setOrderDetails({
                                    orderId, // 订单 ID
                                    productName, // 产品名称
                                    productId, // 产品 ID
                                    quantity, // 数量
                                    totalAmount, // 总金额
                                    customerId: user.customerId, // 客户 ID
                                    price: selectedProduct.price,
                                });
                                setShowModal(true); // 显示弹窗
                            } else {
                                // 如果库存不足，显示错误弹窗
                                setStockError(true);
                            }
                        })
                        .catch(error => {
                            console.error('Allocation error:', error);
                            setStockError(true); // 显示库存不足弹窗
                        });
                } else {
                    alert('Order failed');
                }
            })
            .catch(error => {
                console.error('Order error:', error);
            });
    };

    const handleCloseModal = () => setShowModal(false);
    const handleCloseStockError = () => setStockError(false); // 关闭库存不足弹窗

    return (
        <Container className="store-container mt-4">
            <Row>
                {products.map(product => (
                    <Col key={product.productId} sm={12} md={6} lg={4} className="mb-4">
                        <Card className="h-100">
                            <Card.Body>
                                <Card.Title>{product.productName}</Card.Title>
                                <Card.Text>{product.description}</Card.Text>
                                <Card.Text>Price: ${product.price.toFixed(2)}</Card.Text>
                                <InputGroup className="mb-3">
                                    <InputGroup.Text>Quantity</InputGroup.Text>
                                    <FormControl
                                        type="number"
                                        value={quantities[product.productId] || 1}
                                        min="1"
                                        onChange={(e) => handleQuantityChange(product.productId, parseInt(e.target.value))}
                                    />
                                </InputGroup>
                                <Button variant="primary" onClick={() => handleBuy(product.productId)} className="w-100">
                                    Buy Now
                                </Button>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>

            {/* Modal for order confirmation */}
            <Modal show={showModal} onHide={handleCloseModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Order Placed Successfully</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p><strong>Order ID:</strong> {orderDetails.orderId}</p>
                    <p><strong>Product Name:</strong> {orderDetails.productName}</p>
                    <p><strong>Product ID:</strong> {orderDetails.productId}</p>
                    <p><strong>Quantity:</strong> {orderDetails.quantity}</p>
                    <p><strong>Price:</strong> {orderDetails.price}</p>
                    <p><strong>Total Amount:</strong> ${orderDetails.totalAmount ? orderDetails.totalAmount.toFixed(2) : 'N/A'}</p>
                    <p><strong>Customer ID:</strong> {orderDetails.customerId}</p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseModal}>Close</Button>
                    <Button variant="primary" onClick={() => navigate('/payment', { state: { ...orderDetails } })}>
                        Proceed to Payment
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Modal for stock error */}
            <Modal show={stockError} onHide={handleCloseStockError}>
                <Modal.Header closeButton>
                    <Modal.Title>Stock Error</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>Sorry, there is not enough stock to complete your order. Please try again later or choose a different product.</p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseStockError}>Close</Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
}

export default Store;
