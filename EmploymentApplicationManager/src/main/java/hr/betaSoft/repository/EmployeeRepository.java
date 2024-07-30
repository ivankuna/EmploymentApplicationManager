package hr.betaSoft.repository;

import hr.betaSoft.model.Employee;
import hr.betaSoft.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    List<Employee> findByFromSignUpAndSignUpSent(boolean isFromSignUp, boolean signUpSent);

    List<Employee> findByFromUpdateAndUpdateSent(boolean isFromUpdate, boolean updateSent);

    List<Employee> findByFromSignOutAndSignOutSent(boolean isFromSignOut, boolean signOutSent);

    @Query("SELECT e FROM Employee e JOIN e.user u WHERE e.signUpSent = ?1 AND e.signOutSent = ?2 ORDER BY u.company ASC, e.lastName ASC")
    List<Employee> findBySignUpSentAndSignOutSentOrderedByCompanyAndLastName(boolean signUpSent, boolean signOutSent);

    @Query("SELECT e FROM Employee e JOIN e.user u WHERE e.signUpSent = ?1 AND e.signOutSent = ?2 AND e.foreignNational = ?3 ORDER BY u.company ASC, e.lastName ASC")
    List<Employee> findBySignUpSentAndSignOutSentAndForeignNationalOrderedByCompanyAndLastName(boolean signUpSent, boolean signOutSent, boolean foreignNational);

    @Query("SELECT e FROM Employee e JOIN e.user u WHERE e.signUpSent = ?1 AND e.signOutSent = ?2 AND e.employmentContract = ?3 ORDER BY u.company ASC, e.lastName ASC")
    List<Employee> findBySignUpSentAndSignOutSentAndEmploymentContractOrderedByCompanyAndLastName(boolean signUpSent, boolean signOutSent, String employmentContract);
}