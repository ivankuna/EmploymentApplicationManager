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

            // Map to store count of non-null numSignUp values per year
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

            // Sort the years and add the counts to numOfSentAppsPerYear list
            List<Integer> sortedYears = countPerYear.keySet().stream().sorted().collect(Collectors.toList());
            for (Integer year : sortedYears) {
                numOfSentAppsPerYear.add(countPerYear.get(year));
            }

            // Initialize countPerYear with the starting values from numOfSentAppsPerYear
            int currentYear = sortedYears.isEmpty() ? 2020 : sortedYears.get(0); // Example starting year, adjust as needed
            for (int num : numOfSentAppsPerYear) {
                countPerYear.put(currentYear, num);
                currentYear++;
            }

            // Sort the employees list by dateOfSignUpSent
            employees = sortEmployeeListByDateOfSignUp(employees);

            // Assign numSignUp values to employees
            for (Employee employee : employees) {
                if (employee.getNumSignUp() == null) {
                    Date dateOfSignUpSent = employee.getDateOfSignUpSent();
                    if (dateOfSignUpSent != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dateOfSignUpSent);
                        int year = calendar.get(Calendar.YEAR);

                        // Get the current numSignUp value for the year and assign it to the employee
                        int currentNumSignUp = countPerYear.getOrDefault(year, 0);
                        employee.setNumSignUp(currentNumSignUp + 1);

                        // Increment the numSignUp value in the map
                        countPerYear.put(year, currentNumSignUp + 1);
                    }
                }
            }

            // Save updated employees back to the database
            for (Employee employee : employees) {
                employeeService.saveEmployee(employee);
            }

            // Print the result for verification
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

            // Map to store count of non-null numUpdate values per year
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

            // Sort the years and add the counts to numOfSentAppsPerYear list
            List<Integer> sortedYears = countPerYear.keySet().stream().sorted().collect(Collectors.toList());
            for (Integer year : sortedYears) {
                numOfSentAppsPerYear.add(countPerYear.get(year));
            }

            // Initialize countPerYear with the starting values from numOfSentAppsPerYear
            int currentYear = sortedYears.isEmpty() ? 2020 : sortedYears.get(0); // Example starting year, adjust as needed
            for (int num : numOfSentAppsPerYear) {
                countPerYear.put(currentYear, num);
                currentYear++;
            }

            // Sort the employees list by dateOfUpdateSent
            employees = sortEmployeeListByDateOfUpdate(employees);

            // Assign numUpdate values to employees
            for (Employee employee : employees) {
                if (employee.getNumUpdate() == null) {
                    Date dateOfUpdateSent = employee.getDateOfUpdateSent();
                    if (dateOfUpdateSent != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dateOfUpdateSent);
                        int year = calendar.get(Calendar.YEAR);

                        // Get the current numUpdate value for the year and assign it to the employee
                        int currentNumUpdate = countPerYear.getOrDefault(year, 0);
                        employee.setNumUpdate(currentNumUpdate + 1);

                        // Increment the numUpdate value in the map
                        countPerYear.put(year, currentNumUpdate + 1);
                    }
                }
            }

            // Save updated employees back to the database
            for (Employee employee : employees) {
                employeeService.saveEmployee(employee);
            }

            // Print the result for verification
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

            // Map to store count of non-null numSignOut values per year
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

            // Sort the years and add the counts to numOfSentAppsPerYear list
            List<Integer> sortedYears = countPerYear.keySet().stream().sorted().collect(Collectors.toList());
            for (Integer year : sortedYears) {
                numOfSentAppsPerYear.add(countPerYear.get(year));
            }

            // Initialize countPerYear with the starting values from numOfSentAppsPerYear
            int currentYear = sortedYears.isEmpty() ? 2020 : sortedYears.get(0); // Example starting year, adjust as needed
            for (int num : numOfSentAppsPerYear) {
                countPerYear.put(currentYear, num);
                currentYear++;
            }

            // Sort the employees list by dateOfSignOutSent
            employees = sortEmployeeListByDateOfSignOut(employees);

            // Assign numSignOut values to employees
            for (Employee employee : employees) {
                if (employee.getNumSignOut() == null) {
                    Date dateOfSignOutSent = employee.getDateOfSignOutSent();
                    if (dateOfSignOutSent != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dateOfSignOutSent);
                        int year = calendar.get(Calendar.YEAR);

                        // Get the current numSignOut value for the year and assign it to the employee
                        int currentNumSignOut = countPerYear.getOrDefault(year, 0);
                        employee.setNumSignOut(currentNumSignOut + 1);

                        // Increment the numSignOut value in the map
                        countPerYear.put(year, currentNumSignOut + 1);
                    }
                }
            }

            // Save updated employees back to the database
            for (Employee employee : employees) {
                employeeService.saveEmployee(employee);
            }

            // Print the result for verification
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