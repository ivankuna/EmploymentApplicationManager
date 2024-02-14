package hr.betaSoft.controller;

import hr.betaSoft.exception.EmployeeNotFoundException;
import hr.betaSoft.model.Employee;
import hr.betaSoft.security.exception.UserNotFoundException;
import hr.betaSoft.security.secModel.User;
import hr.betaSoft.security.secService.UserService;
import hr.betaSoft.security.userdto.UserDto;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.tools.Column;
import hr.betaSoft.tools.Data;
import hr.betaSoft.tools.DeviceDetector;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.text.SimpleDateFormat;
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


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @GetMapping("/employees/show")
    public String showEmployees(Model model, HttpServletRequest request) {

        DeviceDetector deviceDetector = new DeviceDetector();
        boolean isMobile = deviceDetector.isMobileDevice(request);

        List<Column> columnList = new ArrayList<>();

        if (isMobile) {
            columnList.add(new Column("Prezime", "lastName", "id"));
            columnList.add(new Column("Ime", "firstName", "id"));
            columnList.add(new Column("Datum", "dateOfSignUpSent", "id"));
            columnList.add(new Column("Status", "signUpSent", "id"));
        } else {
//            columnList.add(new Column("ID", "id", "id"));
            columnList.add(new Column("Prezime", "lastName", "id"));
            columnList.add(new Column("Ime", "firstName", "id"));
            columnList.add(new Column("Datum", "dateOfSignUpSent", "id"));
            columnList.add(new Column("Status", "signUpSent", "id"));
//            columnList.add(new Column("Spol", "gender", "id"));
//            columnList.add(new Column("OIB", "oib", "id"));
//            columnList.add(new Column("JMBG", "jmbg", "id"));
//            columnList.add(new Column("Datum rođenja", "dateOfBirth", "id"));
//            columnList.add(new Column("Adresa", "address", "id"));
//            columnList.add(new Column("Grad", "city", "id"));
//            columnList.add(new Column("Poslana prijava", "signUpSent", "id"));
//            columnList.add(new Column("Poslana odjava", "signOutSent", "id"));
//            columnList.add(new Column("Datum slanja prijave", "dateOfSignUpSent", "id"));
//            columnList.add(new Column("Datum slanja odjave", "dateOfSignOutSent", "id"));
//            columnList.add(new Column("Osobni broj osiguranika HZMO", "city", "id"));
//            columnList.add(new Column("Najviša stručna sprema", "highestProfessionalQualification", "id"));
//            columnList.add(new Column("Naziv najviše završene škole", "highestLevelOfEducation", "id"));
//            columnList.add(new Column("Radno mjesto", "employmentPosition", "id"));
//            columnList.add(new Column("Ugovor o radu", "employmentContract", "id"));
//            columnList.add(new Column("Razlog - na određeno", "reasonForDefinite", "id"));
//            columnList.add(new Column("Radno vrijeme", "workingHours", "id"));
//            columnList.add(new Column("Sati nepuno", "hoursForPartTime", "id"));
//            columnList.add(new Column("Datum prijave", "dateOfSignUp", "id"));
//            columnList.add(new Column("Datum odjave - za određeno", "dateOfSignOut", "id"));
//            columnList.add(new Column("Iznos osnovne plaće", "basicSalary", "id"));
//            columnList.add(new Column("Bruto/Neto", "salaryType", "id"));
//            columnList.add(new Column("Strani državljanin", "foreignNational", "id"));
//            columnList.add(new Column("Radna dozvola vrijedi do", "expiryDateOfWorkPermit", "id"));
//            columnList.add(new Column("Umirovljenik", "retiree", "id"));
//            columnList.add(new Column("Mlađi od 30 godina", "youngerThanThirty", "id"));
//            columnList.add(new Column("Prvo zaposlenje", "firstEmployment", "id"));
//            columnList.add(new Column("Invalid", "disability", "id"));
//            columnList.add(new Column("Napomena", "note", "id"));
        }

        List<Employee> employeeList = employeeService.findByUser(userService.getAuthenticatedUser());
        model.addAttribute("dataList", employeeList);

        model.addAttribute("title", "Popis radnika");
        model.addAttribute("dodajNaziv", "Dodaj radnika");
        model.addAttribute("path", "/employees");
        model.addAttribute("addLink", "/employees/new");
        model.addAttribute("updateLink", "/employees/update/{id}");
        model.addAttribute("deleteLink", "/employees/delete/{id}");
        model.addAttribute("columnList", columnList);

        return "table";
    }

    @GetMapping("/employees/new")
    public String showAddForm(Model model) {

        Employee employee = (Employee) model.getAttribute("employee");

        if (employee != null) {
            model.addAttribute("class", employee);
        } else {
            model.addAttribute("class", new Employee());
        }

        model.addAttribute("dataList", defineDataList());
        model.addAttribute("title", "Radnik");
        model.addAttribute("dataId", "id");
        model.addAttribute("btnName", "Spremi");
        model.addAttribute("path_save", "/employees/save");
        model.addAttribute("path_show", "/employees/show");

        return "form";
    }

    @GetMapping("/employees/update/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            Employee employee = employeeService.findById(id);
            Employee tempEmployee = (Employee) model.getAttribute("employee");

            if (tempEmployee != null) {
                model.addAttribute("class", tempEmployee);
            } else {
                model.addAttribute("class", employee);
            }

            model.addAttribute("dataList", defineDataList());
            model.addAttribute("title", "Radnik");
            model.addAttribute("dataId", "id");
            model.addAttribute("btnName", "Ažuriraj");
            model.addAttribute("path_save", "/employees/save");
            model.addAttribute("path_show", "/employees/show");
            return "form";
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/employees/show";
        }
    }

    @PostMapping("/employees/save")
    public String addGrad(@ModelAttribute("employee") Employee employee, Model model, RedirectAttributes ra) {

        if (!employeeService.checkOib(employee.getOib())) {
            ra.addFlashAttribute("employee", employee);
            ra.addFlashAttribute("message", "Neispravan unos OIB-a.");

            if (employee.getId() != null) {
                return "redirect:/employees/update/" + employee.getId();
            }
            return "redirect:/employees/new";
        }

        employee.setUser(userService.getAuthenticatedUser());
        employeeService.saveEmployee(employee);

        return "redirect:/employees/show";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes ra) {

        try {
            employeeService.deleteEmployee(id);
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/employees/show";
    }

    @GetMapping("employees/user/update/{id}")
    public String showEditUser(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            List<Data> dataList = new ArrayList<>();

            List<String> items = new ArrayList<>();

            dataList.add(new Data("Naziv tvrtke:", "company","", "","","text", "true", items));;
            dataList.add(new Data("OIB:", "oib","", "","","text", "true", items));;
            dataList.add(new Data("Adresa:", "address","", "","","text", "true", items));;
            dataList.add(new Data("Grad:", "city","", "","","text", "true", items));;
            dataList.add(new Data("Ime:", "firstName","", "","","text", "true", items));;
            dataList.add(new Data("Prezime:", "lastName","", "","","text", "true", items));;
            dataList.add(new Data("Telefon:", "telephone","", "","","text", "true", items));;
            dataList.add(new Data("e-mail:", "email","", "","","text", "true", items));;
            dataList.add(new Data("e-mail za prijavu:", "emailToSend","", "","","text", "true", items));;

            UserDto user = userService.convertEntityToDto(userService.findById(id));

            model.addAttribute("class", user);
            model.addAttribute("dataList", dataList);
            model.addAttribute("title", "Korisnik");
            model.addAttribute("dataId", "id");
            model.addAttribute("btnName", "Ažuriraj");
            model.addAttribute("path_save", "/employees/user/save");
            model.addAttribute("path_show", "/employees");
            return "form";
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/employees";
        }
    }
    @PostMapping("/employees/user/save")
    public String addUser(@ModelAttribute("userDto") UserDto userDto, BindingResult result, Model model, RedirectAttributes ra) {

        User usernameExists = userService.findByUsername(userDto.getUsername());

        if (usernameExists != null) {
            ra.addFlashAttribute("userDto", userDto);
            ra.addFlashAttribute("message", "Već postoji račun registriran s tim korisničkim imenom.");
            return "redirect:/users/new";
        }

        if (result.hasErrors()) {
            return showAddForm(model);
        }

        if (userDto.getId() != null) {
            userDto.setUsername(userService.findById(userDto.getId()).getUsername());
            userDto.setPassword(userService.findById(userDto.getId()).getPassword());
        }

        userService.saveUser(userDto);
        return "redirect:/employees";
    }

    private List<Data> defineDataList() {

        List<Data> dataList = new ArrayList<>();

        List<String> items = new ArrayList<>();

        dataList.add(new Data("Ime:", "firstName","", "","","text", "true", items));;
        dataList.add(new Data("Prezime:", "lastName","", "","","text", "true", items));;
        dataList.add(new Data("Spol:", "gender","", "","","text", "false",Employee.GENDER ));;
        dataList.add(new Data("OIB:", "oib","", "","","text", "false", items));;
        dataList.add(new Data("Datum rođenja:", "dateOfBirth","", "","","date", "false", items));;
        dataList.add(new Data("Adresa:", "address","", "","","text", "false", items));;
        dataList.add(new Data("Grad:", "city","", "","","text", "false", items));;
        dataList.add(new Data("Najviša stručna sprema:", "highestProfessionalQualification","", "","","text", "false",Employee.PROFESSIONAL_QUALIFICATION));;
        dataList.add(new Data("Naziv najviše završene škole:", "highestLevelOfEducation","", "","","text", "false", items));;
        dataList.add(new Data("Radno mjesto:", "employmentPosition","", "","","text", "false", items));;
        dataList.add(new Data("Mjesto rada - Grad:", "cityOfEmployment","", "","","text", "false", items));;
        dataList.add(new Data("Potrebna stručna sprema:", "requiredProfessionalQualifications","", "","","text", "false",Employee.PROFESSIONAL_QUALIFICATION));;
        dataList.add(new Data("Ugovor o radu:", "employmentContract","", "","","text", "false",Employee.EMPLOYMENT_CONTRACT));;
        dataList.add(new Data("Razlog - na određeno:", "reasonForDefinite","", "","","text", "false", items));;
        dataList.add(new Data("Radno vrijeme:", "workingHours","", "","","text", "false",Employee.WORKING_HOURS));;
        dataList.add(new Data("Sati nepuno:", "hoursForPartTime","", "","","number", "false", items));;
        dataList.add(new Data("Neradni dan(i) u tjednu:", "nonWorkingDays","", "","","type", "false", items));;
        dataList.add(new Data("Datum prijave:", "dateOfSignUp","", "","","date", "false", items));;
        dataList.add(new Data("Datum odjave:", "dateOfSignOut","", "","","date", "false", items));;
        dataList.add(new Data("Iznos osnovne plaće:", "basicSalary","", "","","myDecimal", "false", items));;
        dataList.add(new Data("Bruto / Neto:", "salaryType","", "","","text", "false",Employee.SALARY_TYPE));;
        dataList.add(new Data("Strani državljanin:", "foreignNational","", "","","checkbox", "false", items));;
        dataList.add(new Data("Radna dozvola vrijedi do:", "expiryDateOfWorkPermit","", "","","date", "false", items));;
        dataList.add(new Data("Umirovljenik:", "retiree","", "","","checkbox", "false", items));;
        dataList.add(new Data("Mlađi od 30 godina:", "youngerThanThirty","", "","","checkbox", "false", items));;
        dataList.add(new Data("Prvo zaposlenje:", "firstEmployment","", "","","checkbox", "false", items));;
        dataList.add(new Data("Invalid:", "disability","", "","","checkbox", "false", items));;
        dataList.add(new Data("Napomena:", "note","", "","","text", "false", items));;

        return dataList;
    }
}
