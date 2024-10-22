import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Home from './components/Homepage';
import Login from './components/Login';
import Register from './components/Register';
// import ProductList from './components/ProductList'; // 引入产品列表页面
// import ProductDetails from './components/ProductDetails'; // 引入产品详情页面
// import ShoppingCart from './components/ShoppingCart'; // 引入购物车页面
// import OrderHistory from './components/OrderHistory'; // 引入订单历史页面
// import Profile from './components/Profile'; // 引入个人信息页面
// import Checkout from './components/Checkout'; // 引入结账页面

function App() {
  return (
      <AuthProvider>
        <div className="App">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} /> {/* 注册页面 */}
            {/*<Route path="/products" element={<ProductList />} /> /!* 产品列表页面 *!/*/}
            {/*<Route path="/products/:id" element={<ProductDetails />} /> /!* 产品详情页面 *!/*/}
            {/*<Route path="/cart" element={<ShoppingCart />} /> /!* 购物车页面 *!/*/}
            {/*<Route path="/checkout" element={<Checkout />} /> /!* 结账页面 *!/*/}
            {/*<Route path="/orders" element={<OrderHistory />} /> /!* 订单历史页面 *!/*/}
            {/*<Route path="/profile" element={<Profile />} /> /!* 个人信息页面 *!/*/}
          </Routes>
        </div>
      </AuthProvider>
  );
}

export default App;
