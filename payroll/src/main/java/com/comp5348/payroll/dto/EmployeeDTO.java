package com.comp5348.payroll.dto;

import com.comp5348.payroll.model.Employee;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object for Customer.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDTO {
    public Long id;
    public String name;
    public String role;

    public EmployeeDTO(Employee employeeEntity) {
        this.id = employeeEntity.getId();
        this.name = employeeEntity.getName();
        this.role = employeeEntity.getRole();
    }
}