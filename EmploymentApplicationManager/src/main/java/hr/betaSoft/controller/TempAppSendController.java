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
import java.util.*;
import java.util.stream.Collectors;

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

        updateNumSignUp();
        updateNumUpdate();
        updateNumSignOut();

        return "redirect:/users";
    }

    private void updateNumSignUp() {

        List<User> users = userService.findAll();
        List<Employee> employees;

        for (User user : users) {
            employees = employeeService.findByUserAndFromSignUp(user, true);

            for (Employee employee : employees) {
                if (employee.isFromSignUp() && !employee.isSignUpSent()) {
                    employee.setSignUpSent(true);
                    employee.setDateOfSignUpSent(employee.getDateOfSignUp());
                }
            }

            Map<Integer, Integer> countPerYear = new HashMap<>();
            List<Integer> numOfSentAppsPerYear = new ArrayList<>();

            for (Employee employee : employees) {
                Date dateOfSignUpSent = employee.getDateOfSignUpSent();
                Integer numSignUp = employee.getNumSignUp();

                if (dateOfSignUpSent != null && numSignUp != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateOfSignUpSent);
                    int year = calendar.get(Calendar.YEAR);

                    countPerYear.put(year, countPerYear.getOrDefault(year, 0) + 1);
                }
            }

            List<Integer> sortedYears = countPerYear.keySet().stream().sorted().collect(Collectors.toList());
            for (Integer year : sortedYears) {
                numOfSentAppsPerYear.add(countPerYear.get(year));
            }

            int currentYear = sortedYears.isEmpty() ? 2020 : sortedYears.get(0);
            for (int num : numOfSentAppsPerYear) {
                countPerYear.put(currentYear, num);
                currentYear++;
            }

            employees = sortEmployeeListByDateOfSignUp(employees);

            for (Employee employee : employees) {
                if (employee.getNumSignUp() == null) {
                    Date dateOfSignUpSent = employee.getDateOfSignUpSent();
                    if (dateOfSignUpSent != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dateOfSignUpSent);
                        int year = calendar.get(Calendar.YEAR);

                        int currentNumSignUp = countPerYear.getOrDefault(year, 0);
                        employee.setNumSignUp(currentNumSignUp + 1);

                        countPerYear.put(year, currentNumSignUp + 1);
                    }
                }
            }

            for (Employee employee : employees) {
                employeeService.saveEmployee(employee);
            }

            for (Employee employee : employees) {
                System.out.println("Employee ID: " + employee.getId() + ", numSignUp: " + employee.getNumSignUp() + ", dateOfSignUpSent: " + employee.getDateOfSignUpSent());
            }
        }
    }

    private void updateNumUpdate() {

        List<User> users = userService.findAll();
        List<Employee> employees;

        for (User user : users) {
            employees = employeeService.findByUserAndFromUpdate(user, true);

            for (Employee employee : employees) {
                if (employee.isFromUpdate() && !employee.isUpdateSent()) {
                    employee.setUpdateSent(true);
                    employee.setDateOfUpdateSent(employee.getDateOfUpdateReal());
                }
            }

            Map<Integer, Integer> countPerYear = new HashMap<>();
            List<Integer> numOfSentAppsPerYear = new ArrayList<>();

            for (Employee employee : employees) {
                Date dateOfUpdateSent = employee.getDateOfUpdateSent();
                Integer numUpdate = employee.getNumUpdate();

                if (dateOfUpdateSent != null && numUpdate != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateOfUpdateSent);
                    int year = calendar.get(Calendar.YEAR);

                    countPerYear.put(year, countPerYear.getOrDefault(year, 0) + 1);
                }
            }

            List<Integer> sortedYears = countPerYear.keySet().stream().sorted().collect(Collectors.toList());
            for (Integer year : sortedYears) {
                numOfSentAppsPerYear.add(countPerYear.get(year));
            }

            int currentYear = sortedYears.isEmpty() ? 2020 : sortedYears.get(0);
            for (int num : numOfSentAppsPerYear) {
                countPerYear.put(currentYear, num);
                currentYear++;
            }

            employees = sortEmployeeListByDateOfUpdate(employees);

            for (Employee employee : employees) {
                if (employee.getNumUpdate() == null) {
                    Date dateOfUpdateSent = employee.getDateOfUpdateSent();
                    if (dateOfUpdateSent != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dateOfUpdateSent);
                        int year = calendar.get(Calendar.YEAR);

                        int currentNumUpdate = countPerYear.getOrDefault(year, 0);
                        employee.setNumUpdate(currentNumUpdate + 1);

                        countPerYear.put(year, currentNumUpdate + 1);
                    }
                }
            }

            for (Employee employee : employees) {
                employeeService.saveEmployee(employee);
            }

            for (Employee employee : employees) {
                System.out.println("Employee ID: " + employee.getId() + ", numUpdate: " + employee.getNumUpdate() + ", dateOfUpdateSent: " + employee.getDateOfUpdateSent());
            }
        }
    }

    private void updateNumSignOut() {

        List<User> users = userService.findAll();
        List<Employee> employees;

        for (User user : users) {
            employees = employeeService.findByUserAndFromSignOut(user, true);

            for (Employee employee : employees) {
                if (employee.isFromSignOut() && !employee.isSignOutSent()) {
                    employee.setSignOutSent(true);
                    employee.setDateOfSignOutSent(employee.getDateOfSignOutReal());
                }
            }

            Map<Integer, Integer> countPerYear = new HashMap<>();
            List<Integer> numOfSentAppsPerYear = new ArrayList<>();

            for (Employee employee : employees) {
                Date dateOfSignOutSent = employee.getDateOfSignOutSent();
                Integer numSignOut = employee.getNumSignOut();

                if (dateOfSignOutSent != null && numSignOut != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateOfSignOutSent);
                    int year = calendar.get(Calendar.YEAR);

                    countPerYear.put(year, countPerYear.getOrDefault(year, 0) + 1);
                }
            }

            List<Integer> sortedYears = countPerYear.keySet().stream().sorted().collect(Collectors.toList());
            for (Integer year : sortedYears) {
                numOfSentAppsPerYear.add(countPerYear.get(year));
            }

            int currentYear = sortedYears.isEmpty() ? 2020 : sortedYears.get(0); // Example starting year, adjust as needed
            for (int num : numOfSentAppsPerYear) {
                countPerYear.put(currentYear, num);
                currentYear++;
            }

            employees = sortEmployeeListByDateOfSignOut(employees);

            for (Employee employee : employees) {
                if (employee.getNumSignOut() == null) {
                    Date dateOfSignOutSent = employee.getDateOfSignOutSent();
                    if (dateOfSignOutSent != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dateOfSignOutSent);
                        int year = calendar.get(Calendar.YEAR);

                        int currentNumSignOut = countPerYear.getOrDefault(year, 0);
                        employee.setNumSignOut(currentNumSignOut + 1);

                        countPerYear.put(year, currentNumSignOut + 1);
                    }
                }
            }

            for (Employee employee : employees) {
                employeeService.saveEmployee(employee);
            }

            for (Employee employee : employees) {
                System.out.println("Employee ID: " + employee.getId() + ", numSignOut: " + employee.getNumSignOut() + ", dateOfSignOutSent: " + employee.getDateOfSignOutSent());
            }
        }
    }

    public List<Employee> sortEmployeeListByDateOfSignUp(List<Employee> employees) {
        return employees.stream()
                .sorted(Comparator.comparing(
                        e -> Optional.ofNullable(e.getDateOfSignUpSent()).orElse(Date.valueOf(LocalDate.MIN))
                ))
                .collect(Collectors.toList());
    }

    public List<Employee> sortEmployeeListByDateOfUpdate(List<Employee> employees) {
        return employees.stream()
                .sorted(Comparator.comparing(
                        e -> Optional.ofNullable(e.getDateOfUpdateSent()).orElse(Date.valueOf(LocalDate.MIN))
                ))
                .collect(Collectors.toList());
    }

    public List<Employee> sortEmployeeListByDateOfSignOut(List<Employee> employees) {
        return employees.stream()
                .sorted(Comparator.comparing(
                        e -> Optional.ofNullable(e.getDateOfSignOutSent()).orElse(Date.valueOf(LocalDate.MIN))
                ))
                .collect(Collectors.toList());
    }
}