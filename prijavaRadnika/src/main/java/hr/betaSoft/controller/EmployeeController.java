package hr.betaSoft.controller;

import hr.betaSoft.model.Employee;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.tools.Column;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public String showEmployees(Model model) {

        List<Column> columnList = new ArrayList<>();

        columnList.add(new Column("ID", "id", "id"));
        columnList.add(new Column("Ime", "firstName", "id"));
        columnList.add(new Column("Prezime", "lastName", "id"));
        columnList.add(new Column("Datum rođenja", "dateOfBirth", "id"));


        List<Employee> employeeList = employeeService.findAll();
        model.addAttribute("dataList", employeeList);

        model.addAttribute("title", "Popis radnika");
        model.addAttribute("addLink", "/employees/new");
        model.addAttribute("updateLink", "/employees/update/{id}");
        model.addAttribute("deleteLink", "/employees/delete/{id}");
        model.addAttribute("columnList", columnList);

        return "table";
    }
}
