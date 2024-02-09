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
        columnList.add(new Column("Umirovljenik", "retiree", "id"));

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

        List<String> items = new ArrayList<>();


        dataList.add(new Data("Ime:", "firstName","", "","","text", "true", items));;
        dataList.add(new Data("Prezime:", "lastName","", "","","text", "true", items));;
        dataList.add(new Data("Spol:", "gender","", "","","text", "false",Employee.GENDER ));;
        dataList.add(new Data("OIB:", "oib","", "","","text", "true", items));;
        dataList.add(new Data("JMBG:", "jmbg","", "","","text", "true", items));;
        dataList.add(new Data("Datum rođenja:", "dateOfBirth","", "","","date", "true", items));;
        dataList.add(new Data("Adresa:", "address","", "","","text", "true", items));;
        dataList.add(new Data("Grad:", "city","", "","","text", "true", items));;
        dataList.add(new Data("Osobni broj osiguranika HZMO:", "hzmoInsuranceNumber","", "","","text", "true", items));;
        dataList.add(new Data("Najviša stručna sprema:", "highestProfessionalQualification","", "","","text", "true", items));;
        dataList.add(new Data("Grad:", "highestLevelOfEducation","", "","","text", "true", items));;

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

            List<String> items = new ArrayList<>();


            dataList.add(new Data("Ime:", "firstName","", "","","text", "true", items));;
            dataList.add(new Data("Prezime:", "lastName","", "","","text", "true", items));;
            dataList.add(new Data("Spol:", "gender","", "","","text", "false",Employee.GENDER ));;
            dataList.add(new Data("OIB:", "oib","", "","","text", "true", items));;
            dataList.add(new Data("JMBG:", "jmbg","", "","","text", "true", items));;
            dataList.add(new Data("Datum rođenja:", "dateOfBirth","", "","","date", "true", items));;
            dataList.add(new Data("Umirovljenik:", "retiree","", "","","checkbox", "false", items));;

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
