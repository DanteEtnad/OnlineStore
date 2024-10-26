import http from "../http-common"; // Import Axios instance from configuration file

class StoreDataService {

    // Retrieve all product information
    getAllProducts() {
        return http.get("/store/products");
    }

    // User login
    login(name, password) {
        // Send username and password as URL query parameters
        const params = new URLSearchParams();
        params.append('name', name);
        params.append('password', password);

        return http.post("/store/login?" + params.toString());
    }

    // User registration
    register(name, email, password) {
        // Send username, email, and password as URL query parameters
        const params = new URLSearchParams();
        params.append('name', name);
        params.append('email', email);
        params.append('password', password);

        return http.post("/store/register?" + params.toString());
    }

    // Place an order
    placeOrder(customerId, productId, quantity) {
        // Send product ID and quantity as URL query parameters
        const params = new URLSearchParams();
        params.append('productId', productId);
        params.append('quantity', quantity);

        // Ensure backticks `` are used for string interpolation
        return http.post(`/store/${customerId}/order?${params.toString()}`);
    }

    // Create payment invoice
    createPayment(customerId, orderId, fromAccountId) {
        const params = new URLSearchParams();
        params.append('fromAccountId', fromAccountId);

        // Also ensure backticks `` are used for interpolation
        return http.post(`/store/${customerId}/${orderId}/payment?${params.toString()}`);
    }

    // Get payment status
    getPaymentStatus(bankTransferId) {
        // Query payment status via GET request
        return http.get(`/store/paymentStatus/${bankTransferId}`);
    }

    // Process a refund
    refundOrder(customerId, orderId, fromAccountId) {
        // Send refund information as URL query parameters
        const params = new URLSearchParams();
        params.append('fromAccountId', fromAccountId);

        return http.post(`/store/${customerId}/${orderId}/refund?` + params.toString());
    }

    // Check order status
    checkOrderStatus(customerId, orderId) {
        // Directly send check order status via POST request
        return http.post(`/store/${customerId}/checkOrderStatus/${orderId}`);
    }

    // Retrieve customer orders
    getCustomerOrders(customerId) {
        return http.get(`/bank/customer/${customerId}`);
    }

    // Allocate stock for an order
    allocateWarehouseForOrder(orderId) {
        // Call allocate API to check stock via GET request
        return http.get(`/order-allocation/allocate/${orderId}`);
    }
}

export default new StoreDataService();
