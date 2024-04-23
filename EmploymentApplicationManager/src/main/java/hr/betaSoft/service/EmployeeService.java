package hr.betaSoft.service;

import hr.betaSoft.model.Employee;
import hr.betaSoft.security.model.User;

import java.util.List;

public interface EmployeeService {

    void saveEmployee(Employee employee);

    void deleteEmployee(Long id);

    List<Employee> findAll();

    Employee findById(long id);

    List<Employee> findByUser(User user);

    Employee findByOib(String oib);

    Employee findFirstByOib(String oib);

    Employee findFirstByOibAndUser(String oib, User user);

    Employee findFirstByOibOrderByDateOfUpdateDesc(String oib);

    List<Employee> findByUserAndSignUpSent(User user, boolean signUpSent);
    List<Employee> findByUserAndSignOutSent(User user, boolean signOutSent);

    List<Employee> findByUserAndUpdateSent(User user, boolean updateSent);

    List<Employee> findByUserAndFromSignUp(User user, boolean isFromSignUp);

    List<Employee> findByUserAndFromSignOut(User user, boolean isFromSignOut);

    List<Employee> findByUserAndFromUpdate(User user, boolean isFromUpdate);

    List<Employee> findByUserAndSignUpSentAndFromSignUp(User user, boolean signUpSent, boolean isFromSignUp);

    List<Employee> findByUserAndSignOutSentAndFromSignOut(User user, boolean signOutSent, boolean isFromSignOut);

    List<Employee> findByUserAndUpdateSentAndFromUpdate(User user, boolean updateSent, boolean isFromUpdate);
}