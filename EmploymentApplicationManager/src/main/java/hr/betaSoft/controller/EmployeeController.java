package hr.betaSoft.controller;

import hr.betaSoft.exception.EmployeeNotFoundException;
import hr.betaSoft.model.Employee;
import hr.betaSoft.security.exception.UserNotFoundException;
import hr.betaSoft.security.model.User;
import hr.betaSoft.security.service.UserService;
import hr.betaSoft.security.userdto.UserDto;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.tools.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Controller
public class EmployeeController {

    private User currentUserForAdminUpdate;

    private EmployeeService employeeService;

    private UserService userService;

    private ExcelService excelService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, UserService userService, ExcelService excelService) {
        this.employeeService = employeeService;
        this.userService = userService;
        this.excelService = excelService;
    }

    public EmployeeController() {

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
        String statusField = "";
        String datumField = "";
        String vrijemeField = "";
        String datumApp = "";
        String appNum = "";

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            statusField = "signUpSent";
            datumField = "dateOfSignUpSent";
            vrijemeField = "timeOfSignUpSent";
            datumApp = "dateOfSignUp";
            appNum = "numSignUp";
        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            statusField = "signOutSent";
            datumField = "dateOfSignOutSent";
            vrijemeField = "timeOfSignOutSent";
            datumApp = "dateOfSignOutReal";
            appNum = "numSignOut";
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            statusField = "updateSent";
            datumField = "dateOfUpdateSent";
            vrijemeField = "timeOfUpdateSent";
            datumApp = "dateOfUpdateReal";
            appNum = "numUpdate";
        } else {
            System.out.println("Error: Class EmployeeController line: 89");
        }

        List<Column> columnList = new ArrayList<>();

        if (isMobile) {
            columnList.add(new Column("Prezime", "lastName", "id", statusField));
            columnList.add(new Column("Ime", "firstName", "id", statusField));
            columnList.add(new Column("Poslano", datumField, "id", statusField));
            columnList.add(new Column("S", statusField, "id", statusField));

        } else {
            columnList.add(new Column("Prezime", "lastName", "id", statusField));
            columnList.add(new Column("Ime", "firstName", "id", statusField));
            columnList.add(new Column("OIB", "oib", "id", statusField));
            columnList.add(new Column("Broj naloga", appNum, "id", statusField));
            columnList.add(new Column("Datum naloga", datumApp, "id", statusField));
            columnList.add(new Column("Datum slanja", datumField, "id", statusField));
            columnList.add(new Column("Vrijeme slanja", vrijemeField, "id", statusField));
        }

        User authenticatedUser = userService.getAuthenticatedUser();
        List<Employee> employeeList = new ArrayList<>();

        if (!authenticatedUser.isShowAllApplications()) {
            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                employeeList = employeeService.findByUserAndSignUpSentAndFromSignUp(authenticatedUser, false, true);
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                employeeList = employeeService.findByUserAndSignOutSentAndFromSignOut(authenticatedUser, false, true);
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                employeeList = employeeService.findByUserAndUpdateSentAndFromUpdate(authenticatedUser, false, true);
            }
        } else {
            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                employeeList = employeeService.findByUserAndFromSignUp(authenticatedUser, true);
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                employeeList = employeeService.findByUserAndFromSignOut(authenticatedUser, true);
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                employeeList = employeeService.findByUserAndFromUpdate(authenticatedUser, true);
            }
        }

        model.addAttribute("dataList", employeeList);

        String title = "";

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            title = "Nalozi za prijavu radnika";
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            title = "Nalozi za promjenu podataka";
        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            title = "Nalozi za odjavu radnika";
        }

        model.addAttribute("title", title);
        model.addAttribute("columnList", columnList);
        model.addAttribute("addLink", "/employees/new");
        model.addAttribute("addBtnText", "Novi nalog");
        model.addAttribute("path", "/employees");
        model.addAttribute("sendLink", "/employees/appsend/{id}");
        model.addAttribute("pdfLink", "/employees/pdf/{id}");
        model.addAttribute("deleteLink", "/employees/delete/{id}");
        model.addAttribute("updateLink", "/employees/update/{id}");
        model.addAttribute("showLink", "");
        model.addAttribute("tableName", "employees");
        model.addAttribute("script", "/js/table-employees.js");

        return "table";
    }

    @GetMapping("/users/employees/show/{id}")
    public String showEmployeesToAdmin(@PathVariable("id") Long id, Model model, HttpServletRequest request) {

        DeviceDetector deviceDetector = new DeviceDetector();
        boolean isMobile = deviceDetector.isMobileDevice(request);

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            List<Employee> employeeList = employeeService.findByUserAndSignUpSent(userService.findById(id), true);
            model.addAttribute("dataList", employeeList);

        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            List<Employee> employeeList = employeeService.findByUserAndSignOutSent(userService.findById(id), true);
            model.addAttribute("dataList", employeeList);

        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            List<Employee> employeeList = employeeService.findByUserAndUpdateSent(userService.findById(id), true);
            model.addAttribute("dataList", employeeList);
        }

        String statusField = "";
        String dateField = "";
        String timeField = "";
        String dateApp = "";
        String appNum = "";

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            statusField = "signUpSent";
            dateField = "dateOfSignUpSent";
            timeField = "timeOfSignUpSent";
            dateApp = "dateOfSignUp";
            appNum = "numSignUp";
        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            statusField = "signOutSent";
            dateField = "dateOfSignOutSent";
            timeField = "timeOfSignOutSent";
            dateApp = "dateOfSignOutReal";
            appNum = "numSignOut";
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            statusField = "updateSent";
            dateField = "dateOfUpdateSent";
            timeField = "timeOfUpdateSent";
            dateApp = "dateOfUpdateReal";
            appNum = "numUpdate";
        } else {
            System.out.println("Error: EmployeeController.showEmployeesToAdmin");
        }

        List<Column> columnList = new ArrayList<>();

        if (isMobile) {
            columnList.add(new Column("Prezime", "lastName", "id", statusField));
            columnList.add(new Column("Ime", "firstName", "id", statusField));
            columnList.add(new Column("Broj", appNum, "id", statusField));
            columnList.add(new Column("Datum", dateApp, "id", statusField));
            columnList.add(new Column("Poslano", dateField, "id", statusField));
        } else {
            columnList.add(new Column("Tvrtka", "company", "id", statusField));
            columnList.add(new Column("Prezime", "lastName", "id", statusField));
            columnList.add(new Column("Ime", "firstName", "id", statusField));
            columnList.add(new Column("OIB", "oib", "id", statusField));
            columnList.add(new Column("Broj naloga", appNum, "id", statusField));
            columnList.add(new Column("Datum naloga", dateApp, "id", statusField));
            columnList.add(new Column("Datum slanja", dateField, "id", statusField));
            columnList.add(new Column("Vrijeme slanja", timeField, "id", statusField));
        }

        String title = "";

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            title = "Nalozi za prijavu radnika";
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            title = "Nalozi za promjenu podataka";
        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            title = "Nalozi za odjavu radnika";
        }

        model.addAttribute("title", title);
        model.addAttribute("columnList", columnList);
        model.addAttribute("path", "/users/select");
        model.addAttribute("tableName", "employees");
        model.addAttribute("script", "/js/table-users.js");
        model.addAttribute("showLink", "");
        model.addAttribute("updateLink", "/users/employees/pdf/{id}");
        model.addAttribute("pdfLink", "");
        model.addAttribute("deleteLink", "");

        return "table-apps";
    }

    @GetMapping("/users/employees/show-specific")
    public String showActiveAppsToAdmin(@RequestParam String appType, Model model) {

        FormTracker.setFormId(FormTracker.getSHOW_ALL());

        List<Employee> employeeList = employeeService.getEmployeeList(appType);

        List<Column> columnList = new ArrayList<>();

        Column finalColumn = switch (appType) {
            case "activeApps" -> new Column("Datum prijave", "dateOfSignUp", "id", "statusField");
            case "expiryApps" -> new Column("Datum isteka radne dozvole", "expiryDateOfWorkPermit", "id", "statusField");
            case "fixedTermApps" -> new Column("Datum odjave - iz Prijave", "dateOfSignOut", "id", "statusField");
            default -> throw new IllegalStateException("Unexpected value: " + appType);
        };
        columnList.add(new Column("Tvrtka", "company", "id", "statusField"));
        columnList.add(new Column("Prezime", "lastName", "id", "statusField"));
        columnList.add(new Column("Ime", "firstName", "id", "statusField"));
        columnList.add(new Column("OIB", "oib", "id", "statusField"));
        columnList.add(finalColumn);

        String title = switch (appType) {
            case "activeApps" -> "Popis radnika u radnom odnosu";
            case "expiryApps" -> "Izvještaj o isteku radne dozvole";
            case "fixedTermApps" -> "Izvještaj o isteku rada na određeno";
            default -> throw new IllegalStateException("Unexpected value: " + appType);
        };

        model.addAttribute("title", title);
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", employeeList);
        model.addAttribute("path", "/users");
        model.addAttribute("excel", "/users/download-excel?appType=" + appType);
        model.addAttribute("excelBtnText", "Export");
        model.addAttribute("tableName", "employees");
        model.addAttribute("script", "/js/table-users.js");
        model.addAttribute("showLink", "");
        model.addAttribute("updateLink", "");
        model.addAttribute("pdfLink", "");
        model.addAttribute("deleteLink", "");

        return "table-apps";
    }

    @GetMapping("/users/employees/show-all")
    public String showAllAppsToAdmin(Model model, HttpServletRequest request) {

        FormTracker.setFormId(FormTracker.getSHOW_ALL());

        DeviceDetector deviceDetector = new DeviceDetector();
        boolean isMobile = deviceDetector.isMobileDevice(request);

        List<Employee> employeeList = employeeService.returnAllApps();

        List<Column> columnList = new ArrayList<>();

        if (isMobile) {
            columnList.add(new Column("Tvrtka", "company", "idApp", "statusField"));
            columnList.add(new Column("Prezime", "lastName", "idApp", "statusField"));
            columnList.add(new Column("Ime", "firstName", "idApp", "statusField"));
            columnList.add(new Column("Broj", "numApp", "idApp", "statusField"));
            columnList.add(new Column("Datum", "dateAppReal", "idApp", "statusField"));
        } else {
            columnList.add(new Column("Tvrtka", "company", "idApp", "statusField"));
            columnList.add(new Column("Prezime", "lastName", "idApp", "statusField"));
            columnList.add(new Column("Ime", "firstName", "idApp", "statusField"));
            columnList.add(new Column("OIB", "oib", "idApp", "statusField"));
            columnList.add(new Column("Broj naloga", "numApp", "idApp", "statusField"));
            columnList.add(new Column("Datum slanja", "dateAppReal", "idApp", "statusField"));
            columnList.add(new Column("Vrijeme slanja", "timeApp", "idApp", "statusField"));
        }

        model.addAttribute("title", "Pregled svih naloga");
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", employeeList);
        model.addAttribute("path", "/users");
        model.addAttribute("excel", "/users/download-excel?appType=allApps");
        model.addAttribute("excelBtnText", "Export");
        model.addAttribute("tableName", "employees");
        model.addAttribute("script", "/js/table-users.js");
        model.addAttribute("showLink", "");
        model.addAttribute("updateLink", "/users/employees/temp-update-as-admin/{id}");
        model.addAttribute("pdfLink", "");
        model.addAttribute("deleteLink", "");

        return "table-apps";
    }

    @GetMapping("/users/employees/show-all-not-sent")
    public String showAllNotSentAppsToAdmin(Model model, HttpServletRequest request) {

        FormTracker.setFormId(FormTracker.getSHOW_NOT_SENT());

        DeviceDetector deviceDetector = new DeviceDetector();
        boolean isMobile = deviceDetector.isMobileDevice(request);

        List<Employee> employeeList = employeeService.returnPendingApps();

        List<Column> columnList = new ArrayList<>();

        if (isMobile) {
            columnList.add(new Column("Tvrtka", "company", "id", "statusField"));
            columnList.add(new Column("Prezime", "lastName", "id", "statusField"));
            columnList.add(new Column("Ime", "firstName", "id", "statusField"));
            columnList.add(new Column("Vrsta naloga", "numApp", "id", "statusField"));
        } else {
            columnList.add(new Column("Tvrtka", "company", "id", "statusField"));
            columnList.add(new Column("Prezime", "lastName", "id", "statusField"));
            columnList.add(new Column("Ime", "firstName", "id", "statusField"));
            columnList.add(new Column("OIB", "oib", "id", "statusField"));
            columnList.add(new Column("Vrsta naloga", "numApp", "id", "statusField"));
        }

        model.addAttribute("title", "Pregled svih naloga u pripremi");
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", employeeList);
        model.addAttribute("path", "/users");
        model.addAttribute("excel", "/users/download-excel?appType=pendingApps");
        model.addAttribute("excelBtnText", "Export");
        model.addAttribute("tableName", "employees");
        model.addAttribute("script", "/js/table-users.js");
        model.addAttribute("showLink", "");
        model.addAttribute("updateLink", "/users/employees/update-as-admin/{id}");
        model.addAttribute("pdfLink", "");
        model.addAttribute("deleteLink", "");

        return "table-apps";
    }

    @GetMapping("/users/employees/temp-update-as-admin/{id}")
    public String tempUpdateAppAsAdmin(@PathVariable("id") String idApp) {

        Long id = 0L;

        List<String> charList = Arrays.asList(idApp.split("-"));

        id = Long.valueOf(charList.get(0));

        return "redirect:/users/employees/update-as-admin/" + id;
    }

    @GetMapping("/users/employees/update-as-admin/{id}")
    public String updateAppAsAdmin(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {

            String path = "";

            if (FormTracker.getFormId() == FormTracker.getSHOW_ALL()) {
                path = "/users/employees/show-all";
            } else if (FormTracker.getFormId() == FormTracker.getSHOW_NOT_SENT()) {
                path = "/users/employees/show-all-not-sent";
            }

            Employee employee = employeeService.findById(id);

            currentUserForAdminUpdate = employee.getUser();

            model.addAttribute("class", employee);
            List<Data> dataList = defineDataListForAdmin(true);
            model.addAttribute("dataList", dataList);
            List<String> hiddenList = getEmployeeColumnFieldsNotInDataList(dataList);
            model.addAttribute("hiddenList", hiddenList);
            model.addAttribute("title", "Ažuriranje podataka naloga");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "/users/employees/save-as-admin");
            model.addAttribute("path", path);
            model.addAttribute("sendLink", "");
            model.addAttribute("script", "/js/form-employees.js");

            return "form";
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/employees/show";
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/users/employees/show-not-sent/{id}")
    public String showNotSentAppToAdmin(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            Employee employee = employeeService.findById(id);

            model.addAttribute("class", employee);
            List<Data> dataList = defineDataListForAdmin(false);
            model.addAttribute("dataList", dataList);
            List<String> hiddenList = getEmployeeColumnFieldsNotInDataList(dataList);
            model.addAttribute("hiddenList", hiddenList);
            model.addAttribute("title", "Nalog u pripremi");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "");
            model.addAttribute("path", "/users/employees/show-all-not-sent");
            model.addAttribute("sendLink", "");
            model.addAttribute("script", "/js/sent-form-employees.js");

            return "form";
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/employees/show";
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/employees/new")
    public String showAddForm(Model model) throws IllegalAccessException {

        Employee employee = (Employee) model.getAttribute("employee");

        String title = "";
        String script = "";

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            title = "Nalog za prijavu";
            script = "/js/form-employees.js";
        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            title = "Nalog za odjavu";
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            title = "Nalog za promjenu";
        }

        if (employee != null) {
            model.addAttribute("class", employee);
        } else {
            model.addAttribute("class", new Employee());
        }

        List<Data> dataList = defineDataList(0);
        model.addAttribute("dataList", dataList);

        List<String> hiddenList = getEmployeeColumnFieldsNotInDataList(dataList);

        model.addAttribute("hiddenList", hiddenList);
        model.addAttribute("title", title);
        model.addAttribute("dataId", "id");
        model.addAttribute("pathSave", "/employees/save");
        model.addAttribute("path", "/employees/show");
        model.addAttribute("sendLink", "/employees/appsend");
        model.addAttribute("script", script);

        return "form";
    }

    @GetMapping("/employees/new-update/{oib}")
    public String showAddFilledForm(@PathVariable("oib") String oib, Model model) throws IllegalAccessException {

        Employee employee = new Employee();
        Employee tempEmployee = employeeService.findFirstByOibOrderByDateOfUpdateDesc(oib);
        if (tempEmployee != null) {
            employee.setOib(tempEmployee.getOib());
            employee.setFirstName(tempEmployee.getFirstName());
            employee.setLastName(tempEmployee.getLastName());
            employee.setDateOfSignUp(tempEmployee.getDateOfSignUp());
            employee.setDateOfUpdate(tempEmployee.getDateOfUpdateReal());
            employee.setDateOfSignOut(tempEmployee.getDateOfSignOut());
        }

        String title = "";
        String script = "";

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            title = "Nalog za prijavu";
            script = "/js/form-employees.js";
        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            title = "Nalog za odjavu";
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            title = "Nalog za promjenu";
        }

        model.addAttribute("class", employee);
        List<Data> dataList = defineDataList(0);
        model.addAttribute("dataList", dataList);

        List<String> hiddenList = getEmployeeColumnFieldsNotInDataList(dataList);

        model.addAttribute("hiddenList", hiddenList);
        model.addAttribute("title", title);
        model.addAttribute("dataId", "id");
        model.addAttribute("pathSave", "/employees/save");
        model.addAttribute("path", "/employees/show");
        model.addAttribute("sendLink", "/employees/appsend");
        model.addAttribute("script", script);

        return "form";
    }

    @GetMapping("/employees/update/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            Employee employee = employeeService.findById(id);

            Employee tempDateEmployee = employeeService.findFirstByOibOrderByDateOfUpdateDesc(employee.getOib());

            employee.setDateOfUpdate(tempDateEmployee.getDateOfUpdateReal());

            Employee tempEmployee = (Employee) model.getAttribute("employee");

            String pathSave = "";
            String sendLink = "";
            String title = "";
            String script = "/js/sent-form-employees.js";

            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                pathSave = employee.isSignUpSent() ? "" : "/employees/save";
                sendLink = employee.isSignUpSent() ? "" : "/employees/appsend";
                script = employee.isSignUpSent() ? "/js/sent-form-employees.js" : "/js/form-employees.js";
                title = "Nalog za prijavu";
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                pathSave = employee.isSignOutSent() ? "" : "/employees/save";
                sendLink = employee.isSignOutSent() ? "" : "/employees/appsend";
                title = "Nalog za odjavu";
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                pathSave = employee.isUpdateSent() ? "" : "/employees/save";
                sendLink = employee.isUpdateSent() ? "" : "/employees/appsend";
                title = "Nalog za promjenu";
            }

            if (tempEmployee != null) {
                model.addAttribute("class", tempEmployee);
            } else {
                model.addAttribute("class", employee);
            }

            List<Data> dataList = defineDataList(id);
            model.addAttribute("dataList", dataList);
            List<String> hiddenList = getEmployeeColumnFieldsNotInDataList(dataList);
            model.addAttribute("hiddenList", hiddenList);
            model.addAttribute("title", title);
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", pathSave);
            model.addAttribute("path", "/employees/show");
            model.addAttribute("sendLink", sendLink);
            model.addAttribute("script", script);

            return "form";
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/employees/show";
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/employees/save")
    public String addEmployee(@ModelAttribute("employee") Employee employee, RedirectAttributes ra) {

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            employee.setFromSignUp(true);
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            employee.setFromUpdate(true);
        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            employee.setFromSignOut(true);
        }

        if (!OibHandler.checkOib(employee.getOib())) {
            ra.addFlashAttribute("employee", employee);
            ra.addFlashAttribute("message", "Neispravan unos OIB-a.");

            if (employee.getId() != null) {
                return "redirect:/employees/update/" + employee.getId();
            }
            return "redirect:/employees/new";
        }

        if (employee.getId() == null) {
            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                employee.setFromSignUp(true);
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                employee.setFromUpdate(true);
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                employee.setFromSignOut(true);
            }
        }

        employee.setUser(userService.getAuthenticatedUser());
        employeeService.saveEmployee(employee);
        return "redirect:/employees/show";
    }

    @PostMapping("/users/employees/save-as-admin")
    public String saveUpdateAppAsAdmin(@ModelAttribute("employee") Employee employee, BindingResult result, Model model, RedirectAttributes ra) {

        String path = "";

        if (FormTracker.getFormId() == FormTracker.getSHOW_ALL()) {
            path = "/users/employees/show-all";
        } else if (FormTracker.getFormId() == FormTracker.getSHOW_NOT_SENT()) {
            path = "/users/employees/show-all-not-sent";
        }

        employee.setUser(currentUserForAdminUpdate);

        employeeService.saveEmployee(employee);

//        return "redirect:" + path;
        // Vratiti view koji zatvara tab
        model.addAttribute("path", path);
        return "close-tab";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes ra) {

        Employee tempEmployee = employeeService.findById(id);

        String message = "Nije moguće obrisati poslani nalog!";

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            if (tempEmployee.isSignUpSent()) {
                ra.addFlashAttribute("message", message);
                return "redirect:/employees/show";
            }
        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            if (tempEmployee.isSignOutSent()) {
                ra.addFlashAttribute("message", message);
                return "redirect:/employees/show";
            }
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            if (tempEmployee.isUpdateSent()) {
                ra.addFlashAttribute("message", message);
                return "redirect:/employees/show";
            }
        }

        int counter = 0;

        if (tempEmployee.isFromSignUp()) {
            counter++;
        }
        if (tempEmployee.isFromSignOut()) {
            counter++;
        }
        if (tempEmployee.isFromUpdate()) {
            counter++;
        }
        if (counter > 1) {
            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                tempEmployee.setFromSignUp(false);
                employeeService.saveEmployee(tempEmployee);
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                tempEmployee.setFromSignOut(false);
                employeeService.saveEmployee(tempEmployee);
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                tempEmployee.setFromUpdate(false);
                employeeService.saveEmployee(tempEmployee);
            }
            return "redirect:/employees/show";
        }
        try {
            employeeService.deleteEmployee(id);
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/employees/show";
    }

    @PostMapping("/employees/appsend")
    public String addEmployeeSend(@ModelAttribute("employee") Employee employee, Model model, RedirectAttributes ra) {

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            employee.setFromSignUp(true);
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            employee.setFromUpdate(true);
        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            employee.setFromSignOut(true);
        }

        if (!OibHandler.checkOib(employee.getOib())) {
            ra.addFlashAttribute("employee", employee);
            ra.addFlashAttribute("message", "Neispravan unos OIB-a.");
            if (employee.getId() != null) {
                return "redirect:/employees/update/" + employee.getId();
            }
            return "redirect:/employees/new";
        }
        if (employee.getId() == null) {
            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                employee.setFromSignUp(true);
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                employee.setFromUpdate(true);
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                employee.setFromSignOut(true);
            }
        }

        employee.setUser(userService.getAuthenticatedUser());
        employeeService.saveEmployee(employee);

        return "redirect:/employees/appsend/" + employee.getId();
    }

    @GetMapping("/employees/appsend/{id}")
    public String sendEmployeeMail(@PathVariable Long id, Model model, RedirectAttributes ra, HttpServletResponse response) {

        Employee employeeToSend = new Employee();

        boolean success = true;

        String messageTag = switch (FormTracker.getFormId()) {
            case 1 -> "prijavu";
            case 2 -> "promjenu podataka";
            case 3 -> "odjavu";
            default -> "";
        };

        try {
            employeeToSend = employeeService.findById(id);
            List<String> emptyAttributes = employeeToSend.hasEmptyAttributes(FormTracker.getFormId());

            if (!emptyAttributes.isEmpty()) {
                StringBuilder message = new StringBuilder();
                message.append("Popunite sva obavezna polja u nalogu radnika: ");
                for (String emptyAttribute : emptyAttributes) {
                    message.append(" * " + emptyAttribute);
                }
                ra.addFlashAttribute("message", message);
                return "redirect:/employees/update/" + id;
            }

            Date date = new Date(Calendar.getInstance().getTime().getTime());

            ZoneId zoneId = ZoneId.of("Europe/Zagreb");
            ZonedDateTime currentTime = ZonedDateTime.now(zoneId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String time = currentTime.format(formatter);

            List<Employee> employees = userService.getAuthenticatedUser().getEmployees();

            int counter = 0;

            LocalDate currentDate = LocalDate.now();
            LocalDate firstDayOfYear = LocalDate.of(currentDate.getYear(), 1, 1);
            Date firstDayOfYearSql = Date.valueOf(firstDayOfYear);

            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                employeeToSend.setSignUpSent(true);
                employeeToSend.setDateOfSignUpSent(date);
                employeeToSend.setTimeOfSignUpSent(time);
                for (Employee employee : employees) {
                    if (employee.isSignUpSent() && !employee.equals(employeeToSend) && employee.getDateOfSignUpSent().after(firstDayOfYearSql)) {
                        counter++;
                    }
                }
                employeeToSend.setNumSignUp(counter + 1);
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                employeeToSend.setSignOutSent(true);
                employeeToSend.setDateOfSignOutSent(date);
                employeeToSend.setTimeOfSignOutSent(time);
                for (Employee employee : employees) {
                    if (employee.isSignOutSent() && !employee.equals(employeeToSend) && employee.getDateOfSignOutSent().after(firstDayOfYearSql)) {
                        counter++;
                    }
                }
                employeeToSend.setNumSignOut(counter + 1);
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                employeeToSend.setUpdateSent(true);
                employeeToSend.setDateOfUpdateSent(date);
                employeeToSend.setTimeOfUpdateSent(time);
                for (Employee employee : employees) {
                    if (employee.isUpdateSent() && !employee.equals(employeeToSend) && employee.getDateOfUpdateSent().after(firstDayOfYearSql)) {
                        counter++;
                    }
                }
                employeeToSend.setNumUpdate(counter + 1);
            }

            String employeeName = employeeToSend.getFirstName() + " " + employeeToSend.getLastName();
            String mailRecipient = employeeToSend.getUser().getEmailToSend();
            String companyName = employeeToSend.getUser().getCompany();
            String mailSubject = companyName + "  Nalog za " + messageTag + " radnika: " + employeeName;
            String mailText = "Poštovani," + " \n" +
                    "" + " \n" +
                    "U privitku Vam šaljemo Nalog za " + messageTag + " radnika: " + employeeName + " \n" +
                    "" + " \n" +
                    "Ova poruka je automatski generirana te Vas molimo da na nju ne odgovarate." + " \n" +
                    "" + " \n" +
                    "" + " \n" +
                    employeeToSend.getUser().getCompany() + " \n" +
                    employeeToSend.getUser().getAddress() + " \n" +
                    employeeToSend.getUser().getCity() + " \n";

            String pdfFilePath = createAppPdf(id, model, ra, response);
            SendMail.sendMail(mailRecipient, mailSubject, mailText, pdfFilePath);
            ra.addFlashAttribute("successMessage", "Nalog za " + messageTag + " radnika je poslan.");
        } catch (Exception e) {
            success = false;
            ra.addFlashAttribute("message", "Slanje naloga nije uspjelo. Kontaktirajte podršku.");
        }

        if (success) {
            employeeService.saveEmployee(employeeToSend);
        }

        return "redirect:/employees/show";
    }

    @GetMapping("employees/user/update/{id}")
    public String showEditUser(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            UserDto user = userService.convertEntityToDto(userService.findById(id));

            model.addAttribute("class", user);
            model.addAttribute("dataList", UserController.defineDataList(true, true));
            model.addAttribute("title", "Postavke");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "/employees/user/save");
            model.addAttribute("path", "/employees");
            model.addAttribute("sendLink", "");
            model.addAttribute("script", "/js/form-users.js");
            return "form";
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/employees";
        }
    }

    @PostMapping("/employees/user/save")
    public String addUser(@ModelAttribute("userDto") UserDto userDto, BindingResult result, Model model, RedirectAttributes ra) {

        if (result.hasErrors()) {
            ra.addFlashAttribute("userDto", userDto);
            ra.addFlashAttribute("message", "Greška prilikom spremanja promjene podataka. Kontaktirajte podršku.");

            if (userDto.getId() != null) {
                return "redirect:/employees/user/update/" + userDto.getId();
            }
            return "redirect:/employees";
        }

        if (userDto.getId() != null) {
            userDto.setUsername(userService.findById(userDto.getId()).getUsername());
            userDto.setPassword(userService.findById(userDto.getId()).getPassword());
            userDto.setCompany(userService.findById(userDto.getId()).getCompany());
            userDto.setOib(userService.findById(userDto.getId()).getOib());
            userDto.setDateOfUserAccountExpiry(userService.findById(userDto.getId()).getDateOfUserAccountExpiry());
        }

        userService.saveUser(userDto);
        return "redirect:/employees";
    }

    @GetMapping("/employees/html/{id}")
    public String showEmployeeHtml(@PathVariable("id") Long id, Model model, RedirectAttributes ra, HttpServletResponse response) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");

            Employee employee = employeeService.findById(id);

            String title = "";
            String appOrder = "";
            String appDate = "";
            String appOrderDate = "";
            String name = "";
            String year = "";

            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                title = "NALOG - PRIJAVA RADNIKA - HZMO - HZZO";
                Date dateOfSignUpSent = employee.getDateOfSignUpSent();
                year = new SimpleDateFormat("yyyy").format(dateOfSignUpSent);
                appOrder = "1-" + employee.getNumSignUp() + "-" + year;
                appDate = "Datum: " + sdf.format(employee.getDateOfSignUpSent()) + " Vrijeme: " + employee.getTimeOfSignUpSent();
                name = "Prijava-" + id + "-" + employee.getNumSignUp();
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                title = "NALOG - PROMJENA PODATAKA RADNIKA - HZMO - HZZO";
                Date dateOfUpdateSent = employee.getDateOfUpdateSent();
                year = new SimpleDateFormat("yyyy").format(dateOfUpdateSent);
                appOrder = "2-" + employee.getNumUpdate() + "-" + year;
                appDate = "Datum: " + sdf.format(employee.getDateOfUpdateSent()) + " Vrijeme: " + employee.getTimeOfUpdateSent();
                name = "Promjena-" + id + "-" + employee.getNumUpdate();
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                title = "NALOG - ODJAVA RADNIKA - HZMO - HZZO";
                Date dateOfSignOutSent = employee.getDateOfSignOutSent();
                year = new SimpleDateFormat("yyyy").format(dateOfSignOutSent);
                appOrder = "3-" + employee.getNumSignOut() + "-" + year;
                appDate = "Datum: " + sdf.format(employee.getDateOfSignOutSent()) + " Vrijeme: " + employee.getTimeOfSignOutSent();
                name = "Odjava-" + id + "-" + employee.getNumSignOut();
            }

            appOrderDate = appOrder + " " + appDate;
            model.addAttribute("name", name);
            model.addAttribute("title", title);
            model.addAttribute("appOrder", appOrder);
            model.addAttribute("appDate", appDate);
            model.addAttribute("appOrderDate", appOrderDate);
            model.addAttribute("companyName", employee.getUser().getCompany());
            model.addAttribute("companyOib", employee.getUser().getOib());

            model.addAttribute("class", employee);
            List<Data> dataList = defineDataList(id);
            model.addAttribute("dataList", dataList);

            return "app-html";

        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "page-not-found";
    }

    public void showPdf(Long id, Model model, RedirectAttributes ra, HttpServletResponse response) {

        String message = "";

        try {

            File pdfDir = new File("pdf");

            if (!pdfDir.exists()) {
                boolean dirCreated = pdfDir.mkdir();
                if (!dirCreated) {
                    message = "Slanje naloga nije uspjelo. Kontaktirajte podršku.";
                }
            }

            String pdfFilePath = createAppPdf(id, model, ra, response);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=" + pdfFilePath);

            File pdfFile = new File(pdfFilePath);
            FileInputStream fileInputStream = new FileInputStream(pdfFile);

            IOUtils.copy(fileInputStream, response.getOutputStream());

            fileInputStream.close();
            response.getOutputStream().flush();

        } catch (Exception e) {
            ra.addFlashAttribute("message", message.isEmpty() ? e.getMessage() : message);
        }
    }

    @GetMapping("/employees/pdf/{id}")
    public void showEmployeePdf(@PathVariable("id") Long id, Model model, RedirectAttributes ra, HttpServletResponse response) {
        showPdf(id, model, ra, response);
    }

    @GetMapping("/users/employees/show-all/pdf/{id}")
    public void showEmployeePdfForUser(@PathVariable("id") String idApp, Model model, RedirectAttributes ra, HttpServletResponse response) {

        Long id = 0L;
        Integer appType = 0;

        List<String> charList = Arrays.asList(idApp.split("-"));

        id = Long.valueOf(charList.get(0));
        appType = Integer.valueOf(charList.get(1));

        if (appType == 1) {
            FormTracker.setFormId(FormTracker.getSIGN_UP());
        } else if (appType == 2) {
            FormTracker.setFormId(FormTracker.getUPDATE());
        } else if (appType == 3) {
            FormTracker.setFormId(FormTracker.getSIGN_OUT());
        }

        showPdf(id, model, ra, response);
    }

    @GetMapping("/users/employees/pdf/{id}")
    public void showEmployeePdfForUser(@PathVariable("id") Long id, Model model, RedirectAttributes ra, HttpServletResponse response) {
        showPdf(id, model, ra, response);
    }

    public String createAppPdf(Long id, Model model, RedirectAttributes ra, HttpServletResponse response) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");

            Employee employee = employeeService.findById(id);

            String title = "";
            String appOrder = "";
            String appDate = "";
            String appOrderDate = "";
            String name = "";
            String year = "";

            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                title = "NALOG - PRIJAVA RADNIKA - HZMO - HZZO";
                Date dateOfSignUpSent = employee.getDateOfSignUpSent();
                year = new SimpleDateFormat("yyyy").format(dateOfSignUpSent);
                appOrder = "1-" + employee.getNumSignUp() + "-" + year;
                appDate = "Datum: " + sdf.format(employee.getDateOfSignUpSent()) + " Vrijeme: " + employee.getTimeOfSignUpSent();
                name = "Prijava-" + id + "-" + employee.getNumSignUp();
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                title = "NALOG - PROMJENA PODATAKA RADNIKA - HZMO - HZZO";
                Date dateOfUpdateSent = employee.getDateOfUpdateSent();
                year = new SimpleDateFormat("yyyy").format(dateOfUpdateSent);
                appOrder = "2-" + employee.getNumUpdate() + "-" + year;
                appDate = "Datum: " + sdf.format(employee.getDateOfUpdateSent()) + " Vrijeme: " + employee.getTimeOfUpdateSent();
                name = "Promjena-" + id + "-" + employee.getNumUpdate();
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                title = "NALOG - ODJAVA RADNIKA - HZMO - HZZO";
                Date dateOfSignOutSent = employee.getDateOfSignOutSent();
                year = new SimpleDateFormat("yyyy").format(dateOfSignOutSent);
                appOrder = "3-" + employee.getNumSignOut() + "-" + year;
                appDate = "Datum: " + sdf.format(employee.getDateOfSignOutSent()) + " Vrijeme: " + employee.getTimeOfSignOutSent();
                name = "Odjava-" + id + "-" + employee.getNumSignOut();
            }

            appOrderDate = appOrder + " " + appDate;
            model.addAttribute("name", name);
            model.addAttribute("title", title);
            model.addAttribute("appOrder", appOrder);
            model.addAttribute("appDate", appDate);
            model.addAttribute("appOrderDate", appOrderDate);
            model.addAttribute("companyName", employee.getUser().getCompany());
            model.addAttribute("companyOib", employee.getUser().getOib());

            model.addAttribute("class", employee);
            List<Data> dataList = defineDataList(id);
            model.addAttribute("dataList", dataList);

            String htmlContent = renderHtml(model);
            String pdfFilePath = "pdf/" + name + ".pdf";
            HtmlToPdfConverter.convertHtmlContentToPdf(htmlContent, pdfFilePath);

            return pdfFilePath;

        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "ERROR";
    }

    @Autowired
    private TemplateEngine templateEngine;

    private String renderHtml(Model model) {
        Context context = new Context();
        context.setVariables(model.asMap());
        return templateEngine.process("app-html", context);
    }

    private List<Data> defineDataList(long id) {

        List<Data> dataList = new ArrayList<>();
        List<String> items = new ArrayList<>();
        String fieldEnabled = "true";
        String fieldStatus = "true";
        if (id != 0L) {
            if (FormTracker.getFormId() == FormTracker.getSIGN_UP() && employeeService.findById(id).isSignUpSent()) {
                fieldStatus = "false";
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT() && employeeService.findById(id).isSignOutSent()) {
                fieldStatus = "false";
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE() && employeeService.findById(id).isUpdateSent()) {
                fieldStatus = "false";
            }
        }
        if (id != 0L) {
            if (fieldStatus == "true") {
                if (FormTracker.getFormId() == FormTracker.getSIGN_UP() && employeeService.findById(id).isSignUpSent()) {
                    fieldEnabled = "false";
                } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT() && employeeService.findById(id).isSignUpSent()) {
                    fieldEnabled = "false";
                } else if (FormTracker.getFormId() == FormTracker.getUPDATE() && employeeService.findById(id).isSignUpSent()) {
                    fieldEnabled = "false";
                }
            } else {
                fieldEnabled = "false";
            }
        }

        if (FormTracker.getFormId() == FormTracker.getSHOW_ALL()) {
            fieldEnabled = "false";
        }

        dataList.add(new Data("1.", "OIB *", "oib", "", "", "", "number-input", "true", fieldEnabled, items, "false"));
        ;
        dataList.add(new Data("2.", "Ime *", "firstName", "", "", "", "text", "true", fieldEnabled, items, "false"));
        ;
        dataList.add(new Data("3.", "Prezime *", "lastName", "", "", "", "text", "true", fieldEnabled, items, "false"));
        ;
        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {

            dataList.add(new Data("4.", "Spol *", "gender", "", "", "", "text", "false", fieldEnabled, Employee.GENDER, "false"));
            ;
            dataList.add(new Data("5.", "Datum rođenja *", "dateOfBirth", "", "", "", "date-input", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("6.", "Adresa *", "address", "", "", "", "text", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("7.", "Poštanski broj i grad *", "city", "", "", "", "text", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("8.", "Stvarna stručna sprema *", "professionalQualification", "", "", "", "text", "false", fieldEnabled, Employee.PROFESSIONAL_QUALIFICATION, "false"));
            ;
            dataList.add(new Data("9.", "Naziv najviše završene škole", "highestLevelOfEducation", "", "", "", "text", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("10.", "IBAN - tekući račun - redovni", "ibanRegular", "", "", "", "text", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("11.", "IBAN - tekući račun - zaštićeni", "ibanProtected", "", "", "", "text", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("12.", "Radno mjesto *", "employmentPosition", "", "", "", "text", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("13.", "Mjesto rada - Grad *", "cityOfEmployment", "", "", "", "text", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("14.", "Potrebna stručna sprema *", "requiredProfessionalQualification", "", "", "", "text", "false", fieldEnabled, Employee.PROFESSIONAL_QUALIFICATION, "false"));
            ;
            dataList.add(new Data("15.", "Ugovor o radu *", "employmentContract", "", "", "", "text", "false", fieldEnabled, Employee.EMPLOYMENT_CONTRACT, "false"));
            ;
            dataList.add(new Data("16.", "Razlog - na određeno *", "reasonForDefinite", "", "", "", "text", "false", fieldEnabled, Employee.REASON_FOR_DEFINITE, "false"));
            ;
            dataList.add(new Data("17.", "Dodatni rad *", "additionalWork", "", "", "", "checkbox", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("17a.", "Dodatni rad - sati *", "additionalWorkHours", "", "", "", "number-input", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("18.", "Radno vrijeme *", "workingHours", "", "", "", "text", "false", fieldEnabled, Employee.WORKING_HOURS, "false"));
            ;
            dataList.add(new Data("18a.", "Sati nepuno *", "hoursForPartTime", "", "", "", "number-input", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("19.", "Neradni dan(i) u tjednu *", "nonWorkingDays", "", "", "", "text", "false", fieldEnabled, Employee.NON_WORKING_DAYS, "true"));
            ;
            dataList.add(new Data("20.", "Datum prijave *", "dateOfSignUp", "", "", "", "date-pick", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("21.", "Datum odjave - za određeno *", "dateOfSignOut", "", "", "", "date-pick", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("22.", "Bruto / Neto *", "salaryType", "", "", "", "text", "false", fieldEnabled, Employee.SALARY_TYPE, "false"));
            ;
            dataList.add(new Data("22a.", "Iznos osnovne plaće *", "basicSalary", "", "", "", "number-input", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("23.", "Strani državljanin *", "foreignNational", "", "", "", "checkbox", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("23a.", "Radna dozvola vrijedi do *", "expiryDateOfWorkPermit", "", "", "", "date-pick", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("24.", "Umirovljenik *", "retiree", "", "", "", "checkbox", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("25.", "Mlađi od 30 godina *", "youngerThanThirty", "", "", "", "checkbox", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("26.", "Prvo zaposlenje *", "firstEmployment", "", "", "", "checkbox", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("27.", "Invalid *", "disability", "", "", "", "checkbox", "false", fieldEnabled, items, "false"));
            ;
            dataList.add(new Data("28.", "Napomena", "noteSignUp", "", "", "", "text", "false", fieldEnabled, items, "false"));
            ;
        } else {
            dataList.add(new Data("4.", "Datum prijave - iz Prijave *", "dateOfSignUp", "", "", "", "date-pick", "false", "false", items, "false"));
            ;
            dataList.add(new Data("5.", "Datum zadnje promjene *", "dateOfUpdate", "", "", "", "date-pick", "false", "false", items, "false"));
            ;
        }
        if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            dataList.add(new Data("6.", "Datum odjave - iz Prijave *", "dateOfSignOut", "", "", "", "date-pick", "false", "false", items, "false"));
            ;
            dataList.add(new Data("7.", "Datum odjave - stvarni *", "dateOfSignOutReal", "", "", "", "date-pick", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("8.", "Razlog odjave *", "reasonForSignOut", "", "", "", "text", "false", fieldStatus, Employee.REASON_FOR_SIGN_OUT, "false"));
            ;
            dataList.add(new Data("9.", "Napomena", "noteSignOut", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            dataList.add(new Data("6.", "Datum promjene  *", "dateOfUpdateReal", "", "", "", "date-pick", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("7.", "Razlog promjene *", "reasonForUpdate", "", "", "", "text", "false", fieldStatus, Employee.REASON_FOR_UPDATE, "true"));
            ;
            dataList.add(new Data("8.", "Napomena", "noteUpdate", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
        }
        return dataList;
    }

    private List<Data> defineDataListForAdmin(boolean update) {

        List<Data> dataList = new ArrayList<>();
        List<String> items = new ArrayList<>();

        String enabled = update ? "true" : "false";

        // SIGN UP DATA
        dataList.add(new Data("1.", "OIB *", "oib", "", "", "", "number-input", "true", "false", items, "false"));
        ;
        dataList.add(new Data("2.", "Ime *", "firstName", "", "", "", "text", "true", enabled, items, "false"));
        ;
        dataList.add(new Data("3.", "Prezime *", "lastName", "", "", "", "text", "true", enabled, items, "false"));
        ;
        dataList.add(new Data("4.", "Spol *", "gender", "", "", "", "text", "false", enabled, Employee.GENDER, "false"));
        ;
        dataList.add(new Data("5.", "Datum rođenja *", "dateOfBirth", "", "", "", "date-input", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("6.", "Adresa *", "address", "", "", "", "text", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("7.", "Poštanski broj i grad *", "city", "", "", "", "text", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("8.", "Stvarna stručna sprema *", "professionalQualification", "", "", "", "text", "false", enabled, Employee.PROFESSIONAL_QUALIFICATION, "false"));
        ;
        dataList.add(new Data("9.", "Naziv najviše završene škole", "highestLevelOfEducation", "", "", "", "text", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("10.", "IBAN - tekući račun - redovni", "ibanRegular", "", "", "", "text", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("11.", "IBAN - tekući račun - zaštićeni", "ibanProtected", "", "", "", "text", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("12.", "Radno mjesto *", "employmentPosition", "", "", "", "text", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("13.", "Mjesto rada - Grad *", "cityOfEmployment", "", "", "", "text", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("14.", "Potrebna stručna sprema *", "requiredProfessionalQualification", "", "", "", "text", "false", enabled, Employee.PROFESSIONAL_QUALIFICATION, "false"));
        ;
        dataList.add(new Data("15.", "Ugovor o radu *", "employmentContract", "", "", "", "text", "false", enabled, Employee.EMPLOYMENT_CONTRACT, "false"));
        ;
        dataList.add(new Data("16.", "Razlog - na određeno *", "reasonForDefinite", "", "", "", "text", "false", enabled, Employee.REASON_FOR_DEFINITE, "false"));
        ;
        dataList.add(new Data("17.", "Dodatni rad *", "additionalWork", "", "", "", "checkbox", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("17a.", "Dodatni rad - sati *", "additionalWorkHours", "", "", "", "number-input", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("18.", "Radno vrijeme *", "workingHours", "", "", "", "text", "false", enabled, Employee.WORKING_HOURS, "false"));
        ;
        dataList.add(new Data("18a.", "Sati nepuno *", "hoursForPartTime", "", "", "", "number-input", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("19.", "Neradni dan(i) u tjednu *", "nonWorkingDays", "", "", "", "text", "false", enabled, Employee.NON_WORKING_DAYS, "true"));
        ;
        dataList.add(new Data("20.", "Datum prijave *", "dateOfSignUp", "", "", "", "date-pick", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("21.", "Datum odjave - za određeno *", "dateOfSignOut", "", "", "", "date-pick", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("22.", "Bruto / Neto *", "salaryType", "", "", "", "text", "false", enabled, Employee.SALARY_TYPE, "false"));
        ;
        dataList.add(new Data("22a.", "Iznos osnovne plaće *", "basicSalary", "", "", "", "number-input", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("23.", "Strani državljanin *", "foreignNational", "", "", "", "checkbox", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("23a.", "Radna dozvola vrijedi do *", "expiryDateOfWorkPermit", "", "", "", "date-pick", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("24.", "Umirovljenik *", "retiree", "", "", "", "checkbox", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("25.", "Mlađi od 30 godina *", "youngerThanThirty", "", "", "", "checkbox", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("26.", "Prvo zaposlenje *", "firstEmployment", "", "", "", "checkbox", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("27.", "Invalid *", "disability", "", "", "", "checkbox", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("28.", "Napomena", "noteSignUp", "", "", "", "text", "false", enabled, items, "false"));
        ;
        // SIGN OUT DATA
        dataList.add(new Data("29.", "Datum prijave - iz Prijave *", "dateOfSignUp", "", "", "", "date-pick", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("30.", "Datum zadnje promjene *", "dateOfUpdate", "", "", "", "date-pick", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("31.", "Datum odjave - iz Prijave *", "dateOfSignOut", "", "", "", "date-pick", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("32.", "Datum odjave - stvarni *", "dateOfSignOutReal", "", "", "", "date-pick", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("33.", "Razlog odjave *", "reasonForSignOut", "", "", "", "text", "false", enabled, Employee.REASON_FOR_SIGN_OUT, "false"));
        ;
        dataList.add(new Data("34.", "Napomena", "noteSignOut", "", "", "", "text", "false", enabled, items, "false"));
        ;
        // UPDATE DATA
        dataList.add(new Data("35.", "Datum promjene  *", "dateOfUpdateReal", "", "", "", "date-pick", "false", enabled, items, "false"));
        ;
        dataList.add(new Data("36.", "Razlog promjene *", "reasonForUpdate", "", "", "", "text", "false", enabled, Employee.REASON_FOR_UPDATE, "true"));
        ;
        dataList.add(new Data("37.", "Napomena", "noteUpdate", "", "", "", "text", "false", enabled, items, "false"));
        ;

        return dataList;
    }

    public static List<String> getEmployeeColumnFieldsNotInDataList(List<Data> dataList) throws IllegalAccessException {
        List<String> employeeColumnFieldsNotInDataList = new ArrayList<>();
        Field[] employeeFields = Employee.class.getDeclaredFields();

        for (Field employeeField : employeeFields) {
            Annotation[] annotations = employeeField.getDeclaredAnnotations();
            boolean isColumn = false;
            boolean isTemporal = false;
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().getSimpleName().equals("Column")) {
                    isColumn = true;
                }
                if (annotation.annotationType().getSimpleName().equals("Temporal")) {
                    isTemporal = true;
                }
            }
            if (isColumn || isTemporal) {
                boolean found = false;
                for (Data data : dataList) {
                    if (data.getField().equals(employeeField.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    employeeColumnFieldsNotInDataList.add(employeeField.getName());
                }
            }
        }
        return employeeColumnFieldsNotInDataList;
    }
}