package hr.betaSoft.controller;

import hr.betaSoft.exception.EmployeeNotFoundException;
import hr.betaSoft.model.Employee;
import hr.betaSoft.security.secService.UserService;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.tools.Column;
import hr.betaSoft.tools.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EmployeeController {

    private EmployeeService employeeService;

    private UserService userService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, UserService userService) {
        this.employeeService = employeeService;
        this.userService = userService;
    }

    @GetMapping("/employees")
    public String showEmployees(Model model) {

        List<Column> columnList = new ArrayList<>();

        columnList.add(new Column("ID", "id", "id"));
        columnList.add(new Column("Ime", "firstName", "id"));
        columnList.add(new Column("Prezime", "lastName", "id"));
        columnList.add(new Column("Datum rođenja", "dateOfBirth", "id"));

        List<Employee> employeeList = employeeService.findByUser(userService.getAuthenticatedUser());
        model.addAttribute("dataList", employeeList);

        model.addAttribute("title", "Popis radnika");
        model.addAttribute("addLink", "/employees/new");
        model.addAttribute("updateLink", "/employees/update/{id}");
        model.addAttribute("deleteLink", "/employees/delete/{id}");
        model.addAttribute("columnList", columnList);

        return "table";
    }

    @GetMapping("/employees/new")
    public String showAddForm(Model model) {

        List<Data> dataList = new ArrayList<>();

        dataList.add(new Data("Ime:", "firstName","", "","","text"));;
        dataList.add(new Data("Prezime:", "lastName","", "","","text"));;
        dataList.add(new Data("OIB:", "oib","", "","","text"));;
        dataList.add(new Data("Datum rođenja:", "dateOfBirth","", "","","date"));;

        model.addAttribute("class", new Employee());

        model.addAttribute("dataList", dataList);
        model.addAttribute("title", "Radnik");
        model.addAttribute("dataId", "id");
        model.addAttribute("btnName", "Spremi");
        model.addAttribute("path", "/employees");

        return "form";
    }

    @GetMapping("/employees/update/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            List<Data> dataList = new ArrayList<>();

            dataList.add(new Data("Ime:", "firstName","", "","","text"));;
            dataList.add(new Data("Prezime:", "lastName","", "","","text"));;
            dataList.add(new Data("OIB:", "oib","", "","","text"));;
            dataList.add(new Data("Datum rođenja:", "dateOfBirth","", "","","date"));;

            Employee employee = employeeService.findById(id);

            model.addAttribute("class", employee);
            model.addAttribute("dataList", dataList);
            model.addAttribute("title", "Radnik");
            model.addAttribute("dataId", "id");
            model.addAttribute("btnName", "Ažuriraj");
            model.addAttribute("path", "/employees");
            return "form";
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/employees";
        }
    }

    @PostMapping("/employees/save")
    public String addGrad(@ModelAttribute("employee") Employee employee) {

        employee.setUser(userService.getAuthenticatedUser());
        employeeService.saveEmployee(employee);

        return "redirect:/employees";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes ra) {

        try {
            employeeService.deleteEmployee(id);
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/employees";
    }
}
