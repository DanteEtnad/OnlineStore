package com.comp5348.payroll.config;

import com.comp5348.payroll.controller.EmployeeController;
import com.comp5348.payroll.model.Employee;
import com.comp5348.payroll.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(EmployeeController employeeController, EmployeeRepository repository) {
        return args -> {
            // Check if there are already two employees in the system
            if (repository.count() < 2) {
                log.info("Preloading " + repository.save(new Employee("Bilbo Baggins", "burglar")));
                log.info("Preloading " + repository.save(new Employee("Frodo Baggins", "thief")));
            } else {
                log.info("Two or more employees already exist. Skipping data preload.");
            }
        };
    }
}