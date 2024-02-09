package hr.betaSoft.service;

import hr.betaSoft.model.Employee;
import hr.betaSoft.repository.EmployeeRepository;
import hr.betaSoft.security.secModel.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public void deleteEmployee(Long id) {

        Employee employee = employeeRepository.findById(id).orElse(null);

        if (employee != null) {
            employeeRepository.delete(employee);
        }
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

    @Override
    public boolean checkOib(String oib) {
        if (oib == null || oib.length() != 11) {
            return false;
        }
        char[] chars = oib.toCharArray();
        int a = 10;
        for (int i = 0; i < 10; i++) {
            char c = chars[i];
            if (c < '0' || c > '9') {
                return false;
            }
            a = a + (c - '0');
            a = a % 10;
            if (a == 0) {
                a = 10;
            }
            a *= 2;
            a = a % 11;
        }
        int control = 11 - a;
        control = control % 10;
        return (control == (chars[10] - '0'));
    }
}
