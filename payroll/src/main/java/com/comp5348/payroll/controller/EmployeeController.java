package com.comp5348.payroll.controller;

import com.comp5348.payroll.dto.EmployeeDTO;
import com.comp5348.payroll.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public List<EmployeeDTO> findAll() {
        return employeeService.findAll();
    }

    @PostMapping("/employees")
    public EmployeeDTO newEmployee(@RequestBody EmployeeDTO newEmployee) {
        return employeeService.newEmployee(newEmployee.name, newEmployee.role);
    }

    @GetMapping("/employees/{id}")
    public EmployeeDTO one(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @PutMapping("/employees/{id}")
    public EmployeeDTO updateEmployeeDetails(@RequestBody EmployeeDTO newEmployeeInfo, @PathVariable Long id) {
        newEmployeeInfo.id = id;
        return employeeService.updateEmployeeDetails(newEmployeeInfo);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/employees")
    public ResponseEntity<?> deleteAllEmployees() {
        try {
            employeeService.deleteAllEmployees();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}