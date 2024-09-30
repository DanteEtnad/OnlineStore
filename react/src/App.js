import React, { Component } from "react";
import { Link, Route, Routes } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";

import EmployeesList from "./components/employees-list.component";
import Employee from "./components/employee.component";
import AddEmployee from "./components/add-employee.component";

class App extends Component {
  render() {
    return (
      <div>
        <nav className="navbar navbar-expand navbar-dark bg-dark">
          <a href="/employees" className="navbar-brand">
            Payroll
          </a>
          <div className="navbar-nav mr-auto">
            <li className="nav-item">
              <Link to={"/employees"} className="nav-link">
                Employees
              </Link>
            </li>
            <li className="nav-item">
              <Link to={"/add"} className="nav-link">
                Add Employee
              </Link>
            </li>
          </div>
        </nav>

        <div className="container mt-3">
          <Routes>
            <Route path="/" element={<EmployeesList />} />
            <Route path="/employees" element={<EmployeesList />} />
            <Route path="/add" element={<AddEmployee />} />
            <Route path="/employees/:id" element={<Employee />} />
          </Routes>
        </div>
      </div>
    );
  }
}

export default App;
