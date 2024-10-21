import http from "../http-common";

class EmployeeDataService {
  // Get all employees
  getAll() {
    return http.get("/employees");  // 假设你的API端点是 /employees
  }

  // Get a single employee by ID
  get(id) {
    return http.get(`/employees/${id}`);  // 使用动态路径获取指定ID的员工
  }

  // Create a new employee
  create(data) {
    return http.post("/employees", data);  // 提交新员工数据
  }

  // Update an employee by ID
  update(id, data) {
    return http.put(`/employees/${id}`, data);  // 根据ID更新员工信息
  }

  // Delete a single employee by ID
  delete(id) {
    return http.delete(`/employees/${id}`);  // 根据ID删除员工
  }

  // Delete all employees
  deleteAll() {
    return http.delete("/employees");  // 删除所有员工
  }
}

export default new EmployeeDataService();
