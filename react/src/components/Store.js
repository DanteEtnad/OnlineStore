import React, { useState, useEffect } from 'react';
import StoreDataService from '../services/store.service';
import { Card, Button, InputGroup, FormControl, Container, Row, Col } from 'react-bootstrap';
import './Store.css';

function Store() {
    const [products, setProducts] = useState([]);
    const [quantities, setQuantities] = useState({});

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

    const handleQuantityChange = (productId, newQuantity) => {
        setQuantities(prevQuantities => ({
            ...prevQuantities,
            [productId]: newQuantity > 0 ? newQuantity : 1,
        }));
    };

    const handleBuy = (productId) => {
        const quantity = quantities[productId];
        console.log(`Buying ${quantity} of product ${productId}`);
        StoreDataService.placeOrder(1, productId, quantity)  // Assuming customer ID is 1
            .then(response => {
                if (response.data.status === 'success') {
                    alert('Order placed successfully!');
                } else {
                    alert('Order failed');
                }
            })
            .catch(error => {
                console.error('Order error:', error);
            });
    };

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
        </Container>
    );
}

export default Store;
