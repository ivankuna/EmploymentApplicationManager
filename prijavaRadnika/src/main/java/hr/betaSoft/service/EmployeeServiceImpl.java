package hr.betaSoft.service;

import hr.betaSoft.model.Employee;
import hr.betaSoft.repository.EmployeeRepository;
import hr.betaSoft.security.secModel.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public List<Employee> findAll() {

        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(long id) {

        return employeeRepository.findById(id);
    }

    @Override
    public List<Employee> findByUser(User user) {

        return employeeRepository.findByUser(user);
    }

}
