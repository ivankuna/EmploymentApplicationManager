package hr.betaSoft.controller;

import hr.betaSoft.model.Employee;
import hr.betaSoft.security.model.User;
import hr.betaSoft.security.service.UserService;
import hr.betaSoft.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TempAppSendController {

    private EmployeeService employeeService;

    private UserService userService;

    @Autowired
    public TempAppSendController(EmployeeService employeeService, UserService userService) {
        this.employeeService = employeeService;
        this.userService = userService;
    }

    @PostMapping("users/update-apps")
    private String tempAppSendController() {

        List<User> users = userService.findAll();
        List<Employee> employees;

        int currentYear = LocalDate.now().getYear();
        LocalDate tempFirstDayOfYear = LocalDate.of(currentYear, 1, 1);
        Date firstDayOfYear = Date.valueOf(tempFirstDayOfYear);

        for (User user : users) {

            employees = employeeService.findByUser(user);

            for (Employee employee : employees) {
                if (employee.isFromSignUp() && !employee.isSignUpSent()) {
                    employee.setSignUpSent(true);
                    employee.setDateOfSignUpSent(employee.getDateOfSignUp());
                } else if (employee.isFromUpdate() && !employee.isUpdateSent()) {
                    employee.setUpdateSent(true);
                    employee.setDateOfUpdateSent(employee.getDateOfUpdateReal());
                } else if (employee.isFromSignOut() && !employee.isSignOutSent()) {
                    employee.setSignOutSent(true);
                    employee.setDateOfSignOutSent(employee.getDateOfSignOutReal());
                }
            }

            List<Integer> signUpAppNums = new ArrayList<>();
            List<Integer> updateAppNums = new ArrayList<>();
            List<Integer> signOutAppNums = new ArrayList<>();

            for (Employee employee : employees) {
                if (employee.isFromSignUp() && employee.getNumSignUp() != null && employee.getDateOfSignUpSent().after(firstDayOfYear)) {
                    signUpAppNums.add(employee.getNumSignUp());
                } else if (employee.isFromUpdate() && employee.getNumUpdate() != null && employee.getDateOfUpdateSent().after(firstDayOfYear)) {
                    updateAppNums.add(employee.getNumUpdate());
                } else if (employee.isFromSignOut() && employee.getNumSignOut() != null && employee.getDateOfSignOutSent().after(firstDayOfYear)) {
                    signOutAppNums.add(employee.getNumSignOut());
                }
            }

            int signUpCounter = 1;
            int updateCounter = 1;
            int signOutCounter = 1;

            for (Employee employee : employees) {
                if (employee.isFromSignUp() && employee.getNumSignUp() == null) {
                    while(true) {
                        if (signUpAppNums.contains(signUpCounter)) {
                            signUpCounter++;
                        } else {
                            break;
                        }
                    }
                    employee.setNumSignUp(signUpCounter);
                    signUpCounter++;
                } else if (employee.isFromUpdate() && employee.getNumUpdate() == null) {
                    while(true) {
                        if (updateAppNums.contains(updateCounter)) {
                            updateCounter++;
                        } else {
                            break;
                        }
                    }
                    employee.setNumUpdate(updateCounter);
                    updateCounter++;
                } else if (employee.isFromSignOut() && employee.getNumSignOut() == null) {
                    while(true) {
                        if (signOutAppNums.contains(signOutCounter)) {
                            signOutCounter++;
                        } else {
                            break;
                        }
                    }
                    employee.setNumSignOut(signOutCounter);
                    signOutCounter++;
                }
                employeeService.saveEmployee(employee);
            }
        }

        return "redirect:/users";
    }
}
