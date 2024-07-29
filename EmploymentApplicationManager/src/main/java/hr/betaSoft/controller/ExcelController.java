package hr.betaSoft.controller;



import hr.betaSoft.model.Employee;
import hr.betaSoft.security.service.UserService;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.tools.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;



@RestController
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    private EmployeeService employeeService;

    @Autowired
    public ExcelController(EmployeeService employeeService, ExcelService excelService) {
        this.employeeService = employeeService;
        this.excelService = excelService;
    }

    @GetMapping("/users/download-excel")
    public ResponseEntity<FileSystemResource> downloadExcel(@RequestParam boolean allApps) {
        try {
            List<Employee> employeeList = getEmployeeList(allApps);
            String filename = excelService.generateExcel(employeeList, allApps);

            File file = new File(filename);
            FileSystemResource fileResource = new FileSystemResource(file);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));

            return new ResponseEntity<>(fileResource, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<Employee> getEmployeeList(boolean allApps) {
        return allApps ? returnAllApps() : returnPendingApps();
    }

    public List<Employee> returnAllApps() {
        List<Employee> employeeList = new ArrayList<>();

        String year = "";
        String appOrder = "";

        List<Employee> signUpList = employeeService.findBySignUpSent(true);
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

        List<Employee> updateList = employeeService.findByUpdateSent(true);
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

        List<Employee> signOutList = employeeService.findBySignOutSent(true);
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

        List<Employee> signUpList = employeeService.findByFromSignUpAndSignUpSent(true, false);
        for (Employee emp : signUpList) {
            emp.setDateApp(emp.getDateOfSignUp());
            emp.setNumApp("Prijava");
            emp.setStatusField(false);
            employeeList.add(dtoForTable(emp, false));
        }

        List<Employee> updateList = employeeService.findByFromUpdateAndUpdateSent(true, false);
        for (Employee emp : updateList) {
            emp.setDateApp(emp.getDateOfUpdate());
            emp.setNumApp("Promjena");
            emp.setStatusField(false);
            employeeList.add(dtoForTable(emp, false));
        }

        List<Employee> signOutList = employeeService.findByFromSignOutAndSignOutSent(true, false);
        for (Employee emp : signOutList) {
            emp.setDateApp(emp.getDateOfSignOut());
            emp.setNumApp("Odjava");
            emp.setStatusField(false);
            employeeList.add(dtoForTable(emp, false));
        }

        return employeeList;
    }
    private Employee dtoForTable(Employee tempEmployee, boolean isAppSent) {
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
}
