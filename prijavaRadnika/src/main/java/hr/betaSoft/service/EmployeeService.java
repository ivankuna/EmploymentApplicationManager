package hr.betaSoft.service;

import hr.betaSoft.model.Employee;
import hr.betaSoft.security.secModel.User;

import java.util.List;

public interface EmployeeService {

    void saveEmployee(Employee employee);

    List<Employee> findAll();

    Employee findById(long id);

    List<Employee> findByUser(User user);
}
