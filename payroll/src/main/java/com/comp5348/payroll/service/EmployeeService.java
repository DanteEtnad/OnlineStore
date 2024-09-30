package com.comp5348.payroll.service;

import com.comp5348.payroll.dto.EmployeeDTO;
import com.comp5348.payroll.errors.EmployeeNotFoundException;
import com.comp5348.payroll.model.Employee;
import com.comp5348.payroll.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;

    EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<EmployeeDTO> findAll() {
        List<Employee> employees = repository.findAll();
        ArrayList<EmployeeDTO> employeeDTOs = new ArrayList<>();
        for (Employee employee : employees) {
            employeeDTOs.add(new EmployeeDTO(employee));
        }
        return employeeDTOs;
    }

    public EmployeeDTO newEmployee(String name, String role) {
        Employee newEmployee = new Employee();
        newEmployee.setName(name);
        newEmployee.setRole(role);
        return new EmployeeDTO(repository.save(newEmployee));
    }

    public EmployeeDTO findById(Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        return new EmployeeDTO(employee);
    }

    public EmployeeDTO updateEmployeeDetails(EmployeeDTO employeeDTO) {
        Employee employee = repository.findById(employeeDTO.id)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeDTO.id));
        employee.setName(employeeDTO.name);
        employee.setRole(employeeDTO.role);
        return new EmployeeDTO(repository.save(employee));
    }

    public void deleteEmployee(Long id) {
        repository.deleteById(id);
    }

    public void deleteAllEmployees() {
        repository.deleteAll();
    }
}
