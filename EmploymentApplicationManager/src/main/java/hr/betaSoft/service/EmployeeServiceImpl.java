package hr.betaSoft.service;

import hr.betaSoft.model.Employee;
import hr.betaSoft.repository.EmployeeRepository;
import hr.betaSoft.security.model.User;
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
    public Employee findByOib(String oib) {
        return employeeRepository.findByOib(oib);
    }

    @Override
    public Employee findFirstByOib(String oib) {
        return employeeRepository.findFirstByOib(oib);
    }

    @Override
    public Employee findFirstByOibAndUser(String oib, User user) {
        return employeeRepository.findFirstByOibAndUser(oib, user);
    }

    public Employee findFirstByOibOrderByDateOfUpdateDesc(String oib) {
        return employeeRepository.findFirstByOibOrderByDateOfUpdateDesc(oib);
    }

    @Override
    public List<Employee> findByUserAndSignUpSent(User user, boolean signUpSent) {
        return employeeRepository.findByUserAndSignUpSent(user, signUpSent);
    }

    @Override
    public List<Employee> findByUserAndSignOutSent(User user, boolean signOutSent) {
        return employeeRepository.findByUserAndSignOutSent(user, signOutSent);
    }

    @Override
    public List<Employee> findByUserAndUpdateSent(User user, boolean updateSent) {
        return employeeRepository.findByUserAndUpdateSent(user, updateSent);
    }

    @Override
    public List<Employee> findByUserAndFromSignUp(User user, boolean isFromSignUp) {
        return employeeRepository.findByUserAndFromSignUp(user, isFromSignUp);
    }

    @Override
    public List<Employee> findByUserAndFromSignOut(User user, boolean isFromSignOut) {
        return employeeRepository.findByUserAndFromSignOut(user, isFromSignOut);
    }

    @Override
    public List<Employee> findByUserAndFromUpdate(User user, boolean isFromUpdate) {
        return employeeRepository.findByUserAndFromUpdate(user, isFromUpdate);
    }

    @Override
    public List<Employee> findByUserAndSignUpSentAndFromSignUp(User user, boolean signUpSent, boolean isFromSignUp) {
        return employeeRepository.findByUserAndSignUpSentAndFromSignUp(user, signUpSent, isFromSignUp);
    }

    @Override
    public List<Employee> findByUserAndSignOutSentAndFromSignOut(User user, boolean signOutSent, boolean isFromSignOut) {
        return employeeRepository.findByUserAndSignOutSentAndFromSignOut(user, signOutSent, isFromSignOut);
    }

    @Override
    public List<Employee> findByUserAndUpdateSentAndFromUpdate(User user, boolean updateSent, boolean isFromUpdate) {
        return employeeRepository.findByUserAndUpdateSentAndFromUpdate(user, updateSent, isFromUpdate);
    }
}