import React from 'react';
import { Container, Button, Row, Col, Card } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import './Homepage.css'; // Import stylesheet

function Home() {
    const navigate = useNavigate();

    // Navigate to the login page
    const handleLoginClick = () => {
        navigate('/login');
    };

    // Navigate to the registration page
    const handleRegisterClick = () => {
        navigate('/register');
    };

    // Navigate to the product preview page
    const handleProductPreviewClick = () => {
        navigate('/products');
    };

    return (
        <Container className="home-container">
            <div className="home-content">
                <h1>Welcome to Our Online Store</h1>
                <p>
                    Discover a variety of products tailored to your needs. Our platform offers the best deals and exclusive discounts just for you. Sign up now and start shopping today!
                </p>

                {/* Quick access buttons */}
                <div className="home-buttons">
                    <Button variant="primary" onClick={handleLoginClick} className="home-button">
                        Login
                    </Button>
                    <Button variant="success" onClick={handleRegisterClick} className="home-button">
                        Register
                    </Button>
                </div>

                {/* Platform features */}
                <h2>Platform Features</h2>
                <Row className="features-section">
                    <Col md={4}>
                        <Card className="feature-card">
                            <Card.Body>
                                <Card.Title>Exclusive Discounts</Card.Title>
                                <Card.Text>
                                    Enjoy amazing discounts available only for registered users.
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col md={4}>
                        <Card className="feature-card">
                            <Card.Body>
                                <Card.Title>Fast Delivery</Card.Title>
                                <Card.Text>
                                    Get your products delivered quickly and securely to your doorstep.
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col md={4}>
                        <Card className="feature-card">
                            <Card.Body>
                                <Card.Title>Top-rated Products</Card.Title>
                                <Card.Text>
                                    Choose from a wide range of high-quality, top-rated products.
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>

                {/* Product previews */}
                <h2>Product Previews</h2>
                <p>
                    Check out some of our popular products before signing up. Click the button below to explore our collection.
                </p>
                <Button variant="info" onClick={handleProductPreviewClick} className="home-button explore-button">
                    Explore Products
                </Button>
            </div>
        </Container>
    );
}

export default Home;
