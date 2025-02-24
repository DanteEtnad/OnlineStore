import React, { useState, useEffect } from 'react';
import StoreDataService from '../services/store.service'; // Import service
import { Card, Button, InputGroup, FormControl, Container, Row, Col, Modal } from 'react-bootstrap';
import './Store.css';
import { useAuth } from '../context/AuthContext'; // Import authentication context to get user information
import { useNavigate } from 'react-router-dom'; // Import navigation hook for redirects

function Store() {
    const { user } = useAuth(); // Get information of the currently logged-in user
    const [products, setProducts] = useState([]);
    const [quantities, setQuantities] = useState({});
    const [showModal, setShowModal] = useState(false);
    const [orderDetails, setOrderDetails] = useState({});
    const [stockError, setStockError] = useState(false); // To control stock error modal
    const navigate = useNavigate(); // For navigating to the payment page

    // Get all product data
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

    // Handle quantity change
    const handleQuantityChange = (productId, newQuantity) => {
        setQuantities(prevQuantities => ({
            ...prevQuantities,
            [productId]: newQuantity > 0 ? newQuantity : 1,
        }));
    };

    // Place an order by calling the placeOrder function in store.service.js
    const handleBuy = (productId) => {
        const quantity = quantities[productId];
        if (!user) {
            alert('User is not logged in');
            return;
        }

        StoreDataService.placeOrder(user.customerId, productId, quantity)
            .then(response => {
                if (response.data.status === 'success') {
                    const { orderId, productName, totalAmount } = response.data.data; // Destructure order information
                    const selectedProduct = products.find(product => product.productId === productId);

                    // Call stock allocation API to check if enough stock is available
                    StoreDataService.allocateWarehouseForOrder(orderId)
                        .then(allocateResponse => {
                            if (allocateResponse.data.status === 'success') {
                                // If stock is sufficient, navigate to payment page
                                setOrderDetails({
                                    orderId, // Order ID
                                    productName, // Product name
                                    productId, // Product ID
                                    quantity, // Quantity
                                    totalAmount, // Total amount
                                    customerId: user.customerId, // Customer ID
                                    price: selectedProduct.price,
                                });
                                setShowModal(true); // Show modal
                            } else {
                                // If stock is insufficient, display error modal
                                setStockError(true);
                            }
                        })
                        .catch(error => {
                            console.error('Allocation error:', error);
                            setStockError(true); // Show stock error modal
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
    const handleCloseStockError = () => setStockError(false); // Close stock error modal

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
