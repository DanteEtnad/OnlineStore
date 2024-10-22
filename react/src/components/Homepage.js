import React from 'react';
import { Container, Button, Row, Col, Card } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import './Homepage.css'; // 引入样式文件

function Home() {
    const navigate = useNavigate();

    // 跳转到登录页面
    const handleLoginClick = () => {
        navigate('/login');
    };

    // 跳转到注册页面
    const handleRegisterClick = () => {
        navigate('/register');
    };

    // 跳转到产品预览页面
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

                {/* 快捷入口按钮 */}
                <div className="home-buttons">
                    <Button variant="primary" onClick={handleLoginClick} className="home-button">
                        Login
                    </Button>
                    <Button variant="success" onClick={handleRegisterClick} className="home-button">
                        Register
                    </Button>
                </div>

                {/* 平台特色 */}
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

                {/* 产品预览 */}
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
