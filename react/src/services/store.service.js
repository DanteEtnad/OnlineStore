import http from "../http-common"; // 从配置文件中导入 Axios 的实例

class StoreDataService {

    // 获取所有产品信息
    getAllProducts() {
        return http.get("/store/products");
    }

    // 用户登录
    login(name, password) {
        // 将用户名和密码通过 URL 查询参数的方式发送
        const params = new URLSearchParams();
        params.append('name', name);
        params.append('password', password);

        return http.post("/store/login?" + params.toString());
    }

    // 用户注册
    register(name, email, password) {
        // 将用户名、邮箱和密码通过 URL 查询参数的方式发送
        const params = new URLSearchParams();
        params.append('name', name);
        params.append('email', email);
        params.append('password', password);

        return http.post("/store/register?" + params.toString());
    }

    // 用户下单
    placeOrder(customerId, productId, quantity) {
        // 将产品ID和数量通过 URL 查询参数发送
        const params = new URLSearchParams();
        params.append('productId', productId);
        params.append('quantity', quantity);

        // 确保使用反引号``进行字符串拼接
        return http.post(`/store/${customerId}/order?${params.toString()}`);
    }

// 创建支付发票
    createPayment(customerId, orderId, fromAccountId) {
        const params = new URLSearchParams();
        params.append('fromAccountId', fromAccountId);

        // 同样确保使用反引号``进行拼接
        return http.post(`/store/${customerId}/${orderId}/payment?${params.toString()}`);
    }

    getPaymentStatus(bankTransferId) {
        // 通过 GET 请求查询支付状态
        return http.get(`/store/paymentStatus/${bankTransferId}`);
    }

    // 处理退款
    refundOrder(customerId, orderId, fromAccountId) {
        // 将退款信息通过 URL 查询参数发送
        const params = new URLSearchParams();
        params.append('fromAccountId', fromAccountId);

        return http.post(`/store/${customerId}/${orderId}/refund?` + params.toString());
    }

    // 检查订单状态
    checkOrderStatus(customerId, orderId) {
        // 直接通过 POST 请求发送检查订单状态
        return http.post(`/store/${customerId}/checkOrderStatus/${orderId}`);
    }

    getCustomerOrders(customerId) {
        return http.get(`/bank/customer/${customerId}`);
    }

    allocateWarehouseForOrder(orderId) {
        // 通过 GET 请求调用 allocate API 检查库存
        return http.get(`/order-allocation/allocate/${orderId}`);
    }


}


export default new StoreDataService();
