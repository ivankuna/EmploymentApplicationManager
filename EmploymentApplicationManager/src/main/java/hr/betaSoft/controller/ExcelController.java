package hr.betaSoft.controller;

import hr.betaSoft.model.Employee;
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
import java.util.List;

@RestController
public class ExcelController {

    private ExcelService excelService;

    private EmployeeService employeeService;

    @Autowired
    public ExcelController(EmployeeService employeeService, ExcelService excelService) {
        this.employeeService = employeeService;
        this.excelService = excelService;
    }

    @GetMapping("/users/download-excel")
    public ResponseEntity<FileSystemResource> downloadExcel(@RequestParam String appType) {
        try {
            List<Employee> employeeList = employeeService.getEmployeeList(appType);
            String filename = excelService.generateExcel(employeeList, appType);

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
}
