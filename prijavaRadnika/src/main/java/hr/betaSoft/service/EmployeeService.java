package hr.betaSoft.service;

import hr.betaSoft.model.Employee;

import java.util.List;

public interface EmployeeService {

    List<Employee> findAll();

    Employee findById(long id);
}
