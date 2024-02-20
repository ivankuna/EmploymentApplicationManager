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
import hr.betaSoft.tools.SendMail;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
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
            columnList.add(new Column("S", "signUpSent", "id"));
            columnList.add(new Column("Prezime", "lastName", "id"));
            columnList.add(new Column("Ime", "firstName", "id"));
            columnList.add(new Column("Datum", "dateOfSignUpSent", "id"));
            columnList.add(new Column("Vrijeme", "timeOfSignUpSent", "id"));
        } else {
            columnList.add(new Column("Prezime", "lastName", "id"));
            columnList.add(new Column("Ime", "firstName", "id"));
            columnList.add(new Column("OIB", "oib", "id"));
            columnList.add(new Column("Grad", "city", "id"));
            columnList.add(new Column("Adresa", "address", "id"));
            columnList.add(new Column("Datum", "dateOfSignUpSent", "id"));
            columnList.add(new Column("Vrijeme", "timeOfSignUpSent", "id"));
            columnList.add(new Column("Status", "signUpSent", "id"));
        }

        User authenticatedUser = userService.getAuthenticatedUser();
        List<Employee> employeeListTemp = employeeService.findByUser(authenticatedUser);
        List<Employee> employeeList = new ArrayList<>();

        if (!authenticatedUser.isShowAllApplications()) {
            for (Employee employee : employeeListTemp) {
                if (!employee.isSignUpSent()) {
                    employeeList.add(employee);
                }
            }
        } else {
            employeeList = employeeListTemp;
        }



        model.addAttribute("title", "PRIJAVA RADNIKA");
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", employeeList);
        model.addAttribute("addBtnText", "Novi nalog");
        model.addAttribute("path", "/employees");
        model.addAttribute("addLink", "/employees/new");
        model.addAttribute("sendLink", "/employees/send/{id}");
        model.addAttribute("pdfLink", "/employees/pdf/{id}");
        model.addAttribute("updateLink", "/employees/update/{id}");
        model.addAttribute("deleteLink", "/employees/delete/{id}");
        model.addAttribute("showLink", "");
        model.addAttribute("script", "/js/script-table-employees.js");


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
        model.addAttribute("title", "Nalog za prijavu");
        model.addAttribute("dataId", "id");
        model.addAttribute("pathSave", "/employees/save");
        model.addAttribute("pathShow", "/employees/show");
        model.addAttribute("sendLink", "/employees/send/{id}");
        model.addAttribute("pathSaveSend", "/employees/send");
        model.addAttribute("script", "/js/script-form-employees.js");

        return "form";
    }

    @GetMapping("/employees/update/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            Employee employee = employeeService.findById(id);
            Employee tempEmployee = (Employee) model.getAttribute("employee");

            String pathSave = "/employees/save";
            String sendLink = "/employees/send/{id}";
            String pathSaveSend = "/employees/send";

            if (tempEmployee != null) {
                model.addAttribute("class", tempEmployee);
            } else {
                model.addAttribute("class", employee);
            }

            if (employee.isSignUpSent()) {
                pathSave = "";
                sendLink = "";
                pathSaveSend = "";
            }

            model.addAttribute("dataList", defineDataList());
            model.addAttribute("title", "Nalog za prijavu");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", pathSave);
            model.addAttribute("pathShow", "/employees/show");
            model.addAttribute("sendLink", sendLink);
            model.addAttribute("pathSaveSend", pathSaveSend);
            model.addAttribute("script", "/js/script-form-employees.js");
            return "form";
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/employees/show";
        }
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
            UserDto user = userService.convertEntityToDto(userService.findById(id));

            model.addAttribute("class", user);
            model.addAttribute("dataList", UserController.defineDataList(true));
            model.addAttribute("title", "Postavke");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "/employees/user/save");
            model.addAttribute("pathShow", "/employees");
            model.addAttribute("sendLink", "");
            model.addAttribute("pathSaveSend", "");
            model.addAttribute("script", "/js/script-form-users.js");
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
            userDto.setCompany(userService.findById(userDto.getId()).getCompany());
            userDto.setOib(userService.findById(userDto.getId()).getOib());
        }

        userService.saveUser(userDto);
        return "redirect:/employees";
    }

    private List<Data> defineDataList() {

        List<Data> dataList = new ArrayList<>();

        List<String> items = new ArrayList<>();

        dataList.add(new Data("OIB *", "oib", "", "", "", "text", "true", "", items));
        ;
        dataList.add(new Data("Ime *", "firstName", "", "", "", "text", "true", "", items));
        ;
        dataList.add(new Data("Prezime *", "lastName", "", "", "", "text", "true", "", items));
        ;
        dataList.add(new Data("Spol *", "gender", "", "", "", "text", "false", "", Employee.GENDER));
        ;
        dataList.add(new Data("Datum rođenja *", "dateOfBirth", "", "", "", "date", "false", "", items));
        ;
        dataList.add(new Data("Adresa *", "address", "", "", "", "text", "false", "true", items));
        ;
        dataList.add(new Data("Grad i poštanski broj *", "city", "", "", "", "text", "false", "", items));
        ;
        dataList.add(new Data("Stvarna stručna sprema *", "professionalQualification", "", "", "", "text", "false", "", Employee.PROFESSIONAL_QUALIFICATION));
        ;
        dataList.add(new Data("Naziv najviše završene škole", "highestLevelOfEducation", "", "", "", "text", "false", "", items));
        ;
        dataList.add(new Data("IBAN - tekući račun - redovni", "ibanRegular", "", "", "", "text", "false", "", items));
        ;
        dataList.add(new Data("IBAN - tekući račun - zaštićeni", "ibanProtected", "", "", "", "text", "false", "", items));
        ;
        dataList.add(new Data("Radno mjesto *", "employmentPosition", "", "", "", "text", "false", "", items));
        ;
        dataList.add(new Data("Mjesto rada - Grad *", "cityOfEmployment", "", "", "", "text", "false", "", items));
        ;
        dataList.add(new Data("Potrebna stručna sprema *", "requiredProfessionalQualification", "", "", "", "text", "false", "", Employee.PROFESSIONAL_QUALIFICATION));
        ;
        dataList.add(new Data("Ugovor o radu *", "employmentContract", "", "", "", "text", "false", "true", Employee.EMPLOYMENT_CONTRACT));
        ;
        dataList.add(new Data("Razlog - na određeno *", "reasonForDefinite", "", "", "", "text", "false", "", items));
        ;
        dataList.add(new Data("Dodatni rad *", "additionalWork", "", "", "", "checkbox", "false", "", items));
        ;
        dataList.add(new Data("Dodatni rad - sati *", "additionalWorkHours", "", "", "", "number", "false", "", items));
        ;
        dataList.add(new Data("Radno vrijeme *", "workingHours", "", "", "", "text", "false", "", Employee.WORKING_HOURS));
        ;
        dataList.add(new Data("Sati nepuno *", "hoursForPartTime", "", "", "", "number", "false", "", items));
        ;
        dataList.add(new Data("Neradni dan(i) u tjednu *", "nonWorkingDays", "", "", "", "text", "false", "", items));
        ;
        dataList.add(new Data("Datum prijave *", "dateOfSignUp", "", "", "", "date", "false", "", items));
        ;
        dataList.add(new Data("Datum odjave - za određeno *", "dateOfSignOut", "", "", "", "date", "false", "", items));
        ;
        dataList.add(new Data("Bruto / Neto *", "salaryType", "", "", "", "text", "false", "", Employee.SALARY_TYPE));
        ;
        dataList.add(new Data("Iznos osnovne plaće *", "basicSalary", "", "", "", "myDecimal", "false", "", items));
        ;
        dataList.add(new Data("Strani državljanin *", "foreignNational", "", "", "", "checkbox", "false", "", items));
        ;
        dataList.add(new Data("Radna dozvola vrijedi do *", "expiryDateOfWorkPermit", "", "", "", "date", "false", "", items));
        ;
        dataList.add(new Data("Umirovljenik *", "retiree", "", "", "", "checkbox", "false", "", items));
        ;
        dataList.add(new Data("Mlađi od 30 godina *", "youngerThanThirty", "", "", "", "checkbox", "false", "", items));
        ;
        dataList.add(new Data("Prvo zaposlenje *", "firstEmployment", "", "", "", "checkbox", "false", "", items));
        ;
        dataList.add(new Data("Invalid *", "disability", "", "", "", "checkbox", "false", "", items));
        ;
        dataList.add(new Data("Napomena", "note", "", "", "", "text", "false", "", items));
        ;

        return dataList;
    }

    @GetMapping("/employees/send/{id}")
    public String sendEmployeeMail(@PathVariable Long id, RedirectAttributes ra) {

        try {

            Employee employeeToSignUp = employeeService.findById(id);
            List<String> emptyAttributes = employeeToSignUp.hasEmptyAttributes();

            if (!emptyAttributes.isEmpty()) {
                StringBuilder message = new StringBuilder();
                message.append("Popunite sva obavezna polja u nalogu radnika: ");
                for (String emptyAttribute : emptyAttributes) {
                    message.append(" * " + emptyAttribute);
                }
                ra.addFlashAttribute("message", message);
                return "redirect:/employees/update/" + id;
            }
            if (employeeToSignUp.isSignUpSent()) {
                ra.addFlashAttribute("message", "Nalog za prijavu radnika je već poslan.");
                return "redirect:/employees/update/" + id;
            }

            String recipient = employeeToSignUp.getUser().getEmailToSend();
            SendMail.sendMail(recipient, "Nalog za prijava radnika", employeeToSignUp.toString());
            ra.addFlashAttribute("successMessage", "Nalog za prijavu radnika je poslan.");

            Date date = new Date(Calendar.getInstance().getTime().getTime());

            ZoneId zoneId = ZoneId.of("Europe/Zagreb");
            ZonedDateTime currentTime = ZonedDateTime.now(zoneId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String time = currentTime.format(formatter);

            employeeToSignUp.setSignUpSent(true);
            employeeToSignUp.setDateOfSignUpSent(date);
            employeeToSignUp.setTimeOfSignUpSent(time);

            employeeService.saveEmployee(employeeToSignUp);

        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/employees/show";
    }

    @PostMapping("/employees/save")
    public String addEmployee(@ModelAttribute("employee") Employee employee, Model model, RedirectAttributes ra) {

        if (!employeeService.checkOib(employee.getOib())) {
            ra.addFlashAttribute("employee", employee);
            ra.addFlashAttribute("message", "Neispravan unos OIB-a.");

            if (employee.getId() != null) {
                return "redirect:/employees/update/" + employee.getId();
            }
            return "redirect:/employees/new";
        }

        if (employee.getId() != null) {
            Employee tempEmployee = employeeService.findById(employee.getId());
            employee.setSignUpSent(tempEmployee.isSignUpSent());
            employee.setDateOfSignUpSent(tempEmployee.getDateOfSignUpSent());
            employee.setTimeOfSignUpSent(tempEmployee.getTimeOfSignUpSent());
            employee.setSignOutSent(tempEmployee.isSignOutSent());
            employee.setDateOfSignOutSent(tempEmployee.getDateOfSignOutSent());
            employee.setTimeOfSignOutSent(tempEmployee.getTimeOfSignOutSent());
        }

        employee.setUser(userService.getAuthenticatedUser());
        employeeService.saveEmployee(employee);

        return "redirect:/employees/show";
    }


    @PostMapping("/employees/send")
    public String addEmployeeSend(@ModelAttribute("employee") Employee employee, Model model, RedirectAttributes ra) {

        if (!employeeService.checkOib(employee.getOib())) {
            ra.addFlashAttribute("employee", employee);
            ra.addFlashAttribute("message", "Neispravan unos OIB-a.");

            if (employee.getId() != null) {
                return "redirect:/employees/update/" + employee.getId();
            }
            return "redirect:/employees/new";
        }

        if (employee.getId() != null) {
            Employee tempEmployee = employeeService.findById(employee.getId());
            employee.setSignUpSent(tempEmployee.isSignUpSent());
            employee.setDateOfSignUpSent(tempEmployee.getDateOfSignUpSent());
            employee.setTimeOfSignUpSent(tempEmployee.getTimeOfSignUpSent());
            employee.setSignOutSent(tempEmployee.isSignOutSent());
            employee.setDateOfSignOutSent(tempEmployee.getDateOfSignOutSent());
            employee.setTimeOfSignOutSent(tempEmployee.getTimeOfSignOutSent());
        }

        employee.setUser(userService.getAuthenticatedUser());
        employeeService.saveEmployee(employee);

        return "redirect:/employees/send/" + employee.getId();
    }
    @GetMapping("/employees/pdf/{id}")
    public String showEmployessPdf(@PathVariable Long id, RedirectAttributes ra) {
        try {

            ra.addFlashAttribute("message", "Create PDF for employees_id = " + id.toString()) ;

        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/employees/show";
    }

}
