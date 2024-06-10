package hr.betaSoft.repository;

import hr.betaSoft.model.Employee;
import hr.betaSoft.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findById(long id);

    List<Employee> findByUser(User user);

    Employee findByOib(String oib);

    Employee findFirstByOib(String oib);

    Employee findFirstByOibAndUser(String oib, User user);
    Employee findFirstByOibOrderByDateOfUpdateDesc(String oib);

    Employee findFirstByOibOrderByDateOfUpdateRealDesc(String oib);

    List<Employee> findByUserAndSignUpSent(User user, boolean signUpSent);

    List<Employee> findByUserAndSignOutSent(User user, boolean signOutSent);

    List<Employee> findByUserAndUpdateSent(User user, boolean updateSent);

    List<Employee> findByUserAndFromSignUp(User user, boolean isFromSignUp);

    List<Employee> findByUserAndFromSignOut(User user, boolean isFromSignOut);

    List<Employee> findByUserAndFromUpdate(User user, boolean isFromUpdate);

    List<Employee> findByUserAndSignUpSentAndFromSignUp(User user, boolean signUpSent, boolean isFromSignUp);

    List<Employee> findByUserAndSignOutSentAndFromSignOut(User user, boolean signOutSent, boolean isFromSignOut);

    List<Employee> findByUserAndUpdateSentAndFromUpdate(User user, boolean updateSent, boolean isFromUpdate);

    List<Employee> findBySignUpSent(boolean signUpSent);

    List<Employee> findBySignOutSent(boolean signOutSent);

    List<Employee> findByUpdateSent(boolean updateSent);
}