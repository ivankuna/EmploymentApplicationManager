package hr.betaSoft.service;

import hr.betaSoft.model.Employee;
import hr.betaSoft.repository.EmployeeRepository;
import hr.betaSoft.security.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public Employee findFirstByOibOrderByDateOfUpdateRealDesc(String oib) {
        return employeeRepository.findFirstByOibOrderByDateOfUpdateRealDesc(oib);
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

    @Override
    public List<Employee> findBySignUpSent(boolean signUpSent) {
        return employeeRepository.findBySignUpSent(signUpSent);
    }

    @Override
    public List<Employee> findBySignOutSent(boolean signOutSent) {
        return employeeRepository.findBySignOutSent(signOutSent);
    }

    @Override
    public List<Employee> findByUpdateSent(boolean updateSent) {
        return employeeRepository.findByUpdateSent(updateSent);
    }

    @Override
    public List<Employee> findByFromSignUpAndSignUpSent(boolean isFromSignUp, boolean signUpSent) {
        return employeeRepository.findByFromSignUpAndSignUpSent(isFromSignUp, signUpSent);
    }

    @Override
    public List<Employee> findByFromUpdateAndUpdateSent(boolean isFromUpdate, boolean updateSent) {
        return employeeRepository.findByFromUpdateAndUpdateSent(isFromUpdate, updateSent);
    }

    @Override
    public List<Employee> findByFromSignOutAndSignOutSent(boolean isFromSignOut, boolean signOutSent) {
        return employeeRepository.findByFromSignOutAndSignOutSent(isFromSignOut, signOutSent);
    }

    public List<Employee> returnAllApps() {
        List<Employee> employeeList = new ArrayList<>();

        String year = "";
        String appOrder = "";

        List<Employee> signUpList = findBySignUpSent(true);
        for (Employee emp : signUpList) {
            year = new SimpleDateFormat("yyyy").format(emp.getDateOfSignUpSent());
            appOrder = "1-" + emp.getNumSignUp() + "-" + year;
            emp.setNumApp(appOrder);
            emp.setDateApp(emp.getDateOfSignUp());
            emp.setDateAppReal(emp.getDateOfSignUpSent());
            emp.setTimeApp(emp.getTimeOfSignUpSent());
            emp.setStatusField(emp.isSignUpSent());
            emp.setIdApp(emp.getId() + "-1");
            employeeList.add(dtoForTable(emp, true));
        }

        List<Employee> updateList = findByUpdateSent(true);
        for (Employee emp : updateList) {
            year = new SimpleDateFormat("yyyy").format(emp.getDateOfUpdateSent());
            appOrder = "2-" + emp.getNumUpdate() + "-" + year;
            emp.setNumApp(appOrder);
            emp.setDateApp(emp.getDateOfUpdate());
            emp.setDateAppReal(emp.getDateOfUpdateSent());
            emp.setTimeApp(emp.getTimeOfUpdateSent());
            emp.setStatusField(emp.isUpdateSent());
            emp.setIdApp(emp.getId() + "-2");
            employeeList.add(dtoForTable(emp, true));
        }

        List<Employee> signOutList = findBySignOutSent(true);
        for (Employee emp : signOutList) {
            year = new SimpleDateFormat("yyyy").format(emp.getDateOfSignOutSent());
            appOrder = "3-" + emp.getNumSignOut() + "-" + year;
            emp.setNumApp(appOrder);
            emp.setDateApp(emp.getDateOfSignOut());
            emp.setDateAppReal(emp.getDateOfSignOutSent());
            emp.setTimeApp(emp.getTimeOfSignOutSent());
            emp.setStatusField(emp.isSignOutSent());
            emp.setIdApp(emp.getId() + "-3");
            employeeList.add(dtoForTable(emp, true));
        }

        return employeeList;
    }
    public List<Employee> returnPendingApps() {
        List<Employee> employeeList = new ArrayList<>();

        List<Employee> signUpList = findByFromSignUpAndSignUpSent(true, false);
        for (Employee emp : signUpList) {
            emp.setDateApp(emp.getDateOfSignUp());
            emp.setNumApp("Prijava");
            emp.setStatusField(false);
            employeeList.add(dtoForTable(emp, false));
        }

        List<Employee> updateList = findByFromUpdateAndUpdateSent(true, false);
        for (Employee emp : updateList) {
            emp.setDateApp(emp.getDateOfUpdate());
            emp.setNumApp("Promjena");
            emp.setStatusField(false);
            employeeList.add(dtoForTable(emp, false));
        }

        List<Employee> signOutList = findByFromSignOutAndSignOutSent(true, false);
        for (Employee emp : signOutList) {
            emp.setDateApp(emp.getDateOfSignOut());
            emp.setNumApp("Odjava");
            emp.setStatusField(false);
            employeeList.add(dtoForTable(emp, false));
        }

        return employeeList;
    }

    public Employee dtoForTable(Employee tempEmployee, boolean isAppSent) {
        Employee employee = new Employee();

        employee.setId(tempEmployee.getId());
        employee.setUser(tempEmployee.getUser());
        employee.setLastName(tempEmployee.getLastName());
        employee.setFirstName(tempEmployee.getFirstName());
        employee.setOib(tempEmployee.getOib());
        employee.setStatusField(tempEmployee.isStatusField());
        employee.setNumApp(tempEmployee.getNumApp());
        if (isAppSent) {
            employee.setDateApp(tempEmployee.getDateApp());
            employee.setDateAppReal(tempEmployee.getDateAppReal());
            employee.setTimeApp(tempEmployee.getTimeApp());
            employee.setIdApp(tempEmployee.getIdApp());
        }
        return employee;
    }

    @Override
    public List<Employee> findBySignUpSentAndSignOutSentOrderedByCompanyAndLastName(boolean signUpSent, boolean signOutSent) {
        return employeeRepository.findBySignUpSentAndSignOutSentOrderedByCompanyAndLastName(signUpSent, signOutSent);
    }

    @Override
    public List<Employee> findBySignUpSentAndSignOutSentAndForeignNationalOrderedByCompanyAndLastName(boolean signUpSent, boolean signOutSent, boolean foreignNational) {
        return employeeRepository.findBySignUpSentAndSignOutSentAndForeignNationalOrderedByCompanyAndLastName(signUpSent, signOutSent, foreignNational);
    }

    @Override
    public List<Employee> findBySignUpSentAndSignOutSentAndEmploymentContractOrderedByCompanyAndLastName(boolean signUpSent, boolean signOutSent, String employmentContract) {
        return employeeRepository.findBySignUpSentAndSignOutSentAndEmploymentContractOrderedByCompanyAndLastName(signUpSent, signOutSent, employmentContract);
    }

    @Override
    public List<Employee> getEmployeeList(String appType) {

        List<Employee> employeeList;

        employeeList = switch (appType) {
            case "allApps" -> returnAllApps();
            case "pendingApps" -> returnPendingApps();
            case "activeApps" -> findBySignUpSentAndSignOutSentOrderedByCompanyAndLastName(true, false);
            case "expiryApps" -> findBySignUpSentAndSignOutSentAndForeignNationalOrderedByCompanyAndLastName(true, false, true);
            case "fixedTermApps" -> findBySignUpSentAndSignOutSentAndEmploymentContractOrderedByCompanyAndLastName(true, false, "Određeno");
            default -> throw new IllegalStateException("Unexpected value: " + appType);
        };
        return employeeList;
    }
}