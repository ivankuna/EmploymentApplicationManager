package hr.betaSoft.controller;

import hr.betaSoft.exception.EmployeeNotFoundException;
import hr.betaSoft.model.Employee;
import hr.betaSoft.security.exception.UserNotFoundException;
import hr.betaSoft.security.secModel.User;
import hr.betaSoft.security.secService.UserService;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
        boolean isAdmin = false;
        String statusField = "signUpSent";

        if (userService.getAuthenticatedUser().getId() == UserIdTracker.getADMIN_ID()) {
            isAdmin = true;
        } else {
            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                statusField = "signUpSent";
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                statusField = "signOutSent";
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                statusField = "updateSent";
            }
        }

        List<Column> columnList = new ArrayList<>();

        if (isMobile) {
            if (!isAdmin) {
                columnList.add(new Column("", statusField, "id", statusField));
            }
            columnList.add(new Column("Prezime", "lastName", "id", statusField));
            columnList.add(new Column("Ime", "firstName", "id", statusField));
            if (!isAdmin) {
                columnList.add(new Column("Poslano", "dateOfSignUpSent", "id", statusField));
            }
        } else {
            // OBRATI PAŽNJU!
            // dateOfSignUp/dateOfSignOut/dateOfUpdate I dateOfUpdateSentReal/dateOfSignOutSentReal SU RUČNO UPISANI U NALOGE
            // dateOfSignUpSent/dateOfSignOutSent/dateOfUpdateSent I timeOfSignUpSent/timeOfSignOutSent/timeOfUpdateSent SU GENERIRANI PRI SLANJU NALOGA
            //// KOLONE ZA PRIJAVU ////
            if (isAdmin) {
                columnList.add(new Column("Pr", "fromSignUp", "id", statusField));
                columnList.add(new Column("Po", "fromUpdate", "id", statusField));
                columnList.add(new Column("Od", "fromSignOut", "id", statusField));

            } else {
                columnList.add(new Column("Status", statusField, "id", statusField));
            }

            columnList.add(new Column("OIB", "oib", "id", statusField));
            columnList.add(new Column("Ime", "firstName", "id", statusField));
            columnList.add(new Column("Prezime", "lastName", "id", statusField));

            if (isAdmin) {
                columnList.add(new Column("m.Pr", "signUpSent", "id", statusField));
                columnList.add(new Column("m.Po", "updateSent", "id", statusField));
                columnList.add(new Column("m.Od", "signOutSent", "id", statusField));
            }

//            //// SVE KOLONE ////
//            columnList.add(new Column("Spol", "gender", "id"));
//            columnList.add(new Column("Datum rođenja", "dateOfBirth", "id"));
//            columnList.add(new Column("Adresa", "address", "id"));
//            columnList.add(new Column("Grad", "city", "id"));
//            columnList.add(new Column("Stvarna stručna sprema", "professionalQualification", "id"));
//            columnList.add(new Column("Naziv najviše završene škole", "highestLevelOfEducation", "id"));
//            columnList.add(new Column("IBAN - tekući račun - redovni", "ibanRegular", "id"));
//            columnList.add(new Column("IBAN - tekući račun - zaštićeni", "ibanProtected", "id"));
//            columnList.add(new Column("Radno mjesto", "employmentPosition", "id"));
//            columnList.add(new Column("Mjesto rada - Grad", "cityOfEmployment", "id"));
//            columnList.add(new Column("Potrebna stručna sprema", "requiredProfessionalQualification", "id"));
//            columnList.add(new Column("Ugovor o radu", "employmentContract", "id"));
//            columnList.add(new Column("Razlog - na određeno", "reasonForDefinite", "id"));
//            columnList.add(new Column("Dodatni radi", "additionalWork", "id"));
//            columnList.add(new Column("Dodatni rad - sati", "additionalWorkHours", "id"));
//            columnList.add(new Column("Radno vrijeme", "workingHours", "id"));
//            columnList.add(new Column("Sati nepuno", "hoursForPartTime", "id"));
//            columnList.add(new Column("Neradni dan(i) u tjednu", "nonWorkingDays", "id"));
//            columnList.add(new Column("Datum prijave", "dateOfSignUp", "id"));
//            columnList.add(new Column("Datum odjave - za određeno", "dateOfSignOut", "id"));
//            columnList.add(new Column("Iznos osnovne plaće", "basicSalary", "id"));
//            columnList.add(new Column("Bruto / Neto", "salaryType", "id"));
//            columnList.add(new Column("Strani državljanin", "foreignNational", "id"));
//            columnList.add(new Column("Radna dozvola vrijedi do", "expiryDateOfWorkPermit", "id"));
//            columnList.add(new Column("Umirovljenik", "retiree", "id"));
//            columnList.add(new Column("Mlađi od 30 godina", "youngerThanThirty", "id"));
//            columnList.add(new Column("Prvo zaposlenje", "firstEmployment", "id"));
//            columnList.add(new Column("Invalid", "disability", "id"));
//            columnList.add(new Column("Napomena", "noteSignUp", "id"));


//
//            //// KOLONE ZA ODJAVU ////
//            columnList.add(new Column("Status", statusField, "id"));
//            columnList.add(new Column("OIB", "oib", "id"));
//            columnList.add(new Column("Ime", "firstName", "id"));
//            columnList.add(new Column("Prezime", "lastName", "id"));
//            columnList.add(new Column("Datum prijave", "dateOfSignUp", "id"));
//            columnList.add(new Column("Datum zadnje promjene", "dateOfUpdate", "id"));
//            columnList.add(new Column("Datum odjave - iz Prijave", "dateOfSignOut", "id"));
//            columnList.add(new Column("Datum odjave - stvarni", "dateOfSignOutSentReal", "id"));
//            columnList.add(new Column("Razlog odjave", "reasonForSignOut", "id"));
//            columnList.add(new Column("Napomena", "noteSignOut", "id"));
//
//            //// KOLONE ZA PROMJENU ////
//            columnList.add(new Column("Status", statusField, "id"));
//            columnList.add(new Column("OIB", "oib", "id"));
//            columnList.add(new Column("Ime", "firstName", "id"));
//            columnList.add(new Column("Prezime", "lastName", "id"));
//            columnList.add(new Column("Datum prijave", "dateOfSignUp", "id"));
//            columnList.add(new Column("Datum zadnje promjene", "dateOfUpdate", "id"));
//            columnList.add(new Column("Datum promjene", "dateOfUpdateSentReal", "id"));
//            columnList.add(new Column("Razlog promjene", "reasonForUpdate", "id"));
//            columnList.add(new Column("Napomena", "noteSignOut", "id"));

//            //// RAČUNALNO GENERIRANE KOLONE ZA DATUME I VRIJEME ////
//            columnList.add(new Column("Datum prijave", "dateOfSignUpSent", "id"));
//            columnList.add(new Column("Vrijeme prijave", "timeOfSignUpSent", "id"));
//            columnList.add(new Column("Datum odjave", "dateOfSignOutSent", "id"));
//            columnList.add(new Column("Vrijeme odjave", "timeOfSignOutSent", "id"));
//            columnList.add(new Column("Datum promjene", "dateOfUpdateSent", "id"));
//            columnList.add(new Column("Vrijeme promjene", "timeOfUpdateSent", "id"));
        }

        User authenticatedUser = userService.findById(UserIdTracker.getUserId());

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

        if (userService.getAuthenticatedUser().getId() == UserIdTracker.getADMIN_ID()) {
            model.addAttribute("dataList", employeeList);
        } else {
            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                List<Employee> employeeListFromSignUp = new ArrayList<>();
                for (Employee employee : employeeList) {
                    if (employee.isFromSignUp()) {
                        employeeListFromSignUp.add(employee);
                    }
                }
                model.addAttribute("dataList", employeeListFromSignUp);
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                List<Employee> employeeListFromSignOut = new ArrayList<>();
                for (Employee employee : employeeList) {
                    if (employee.isFromSignOut()) {
                        employeeListFromSignOut.add(employee);
                    }
                }
                model.addAttribute("dataList", employeeListFromSignOut);
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                List<Employee> employeeListFromUpdate = new ArrayList<>();
                for (Employee employee : employeeList) {
                    if (employee.isFromUpdate()) {
                        employeeListFromUpdate.add(employee);
                    }
                }
                model.addAttribute("dataList", employeeListFromUpdate);
            }
        }

        String title = "";
        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            title = "Nalozi za prijavu radnika";
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            title = "Nalozi za promjenu podataka";
        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            title = "Nalozi za odjavu radnika";
        }
        if (isAdmin) {
            title = "Evidencija radnika " + authenticatedUser.getCompany();
        }

        model.addAttribute("title", title);
        model.addAttribute("columnList", columnList);

        // AKO JE ADMIN NEMA GUMBA ZA DODAVANJE
        if (!isAdmin) {
            model.addAttribute("addLink", "/employees/new");
            model.addAttribute("addBtnText", "Novi nalog");
        }

        String path = userService.getAuthenticatedUser().getId().equals(UserIdTracker.getADMIN_ID()) ? "/redirect" : "/employees";
        model.addAttribute("path", path);

        model.addAttribute("sendLink", "/employees/send/{id}");

        if (isAdmin) {
            model.addAttribute("pdfLink", "");
            model.addAttribute("deleteLink", "");
        } else {
            model.addAttribute("pdfLink", "/employees/pdf/{id}");
            model.addAttribute("deleteLink", "/employees/delete/{id}");
        }
        model.addAttribute("updateLink", "/employees/update/{id}");
        model.addAttribute("showLink", "");
        model.addAttribute("tableName", "employees");
        model.addAttribute("script", "/js/script-table-employees.js");

        return "table";
    }

    @GetMapping("/redirect")
    public String redirect() {
        UserIdTracker.setUserId(UserIdTracker.getADMIN_ID());
        return "redirect:/users/show";
    }

    @GetMapping("/employees/new")
    public String showAddForm(Model model) throws IllegalAccessException {

        Employee employee = (Employee) model.getAttribute("employee");

        String title = "";
        String script = "";

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            title = "Nalog za prijavu";
            script = "/js/script-form-employees.js";
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
        model.addAttribute("sendLink", "/employees/send");
        model.addAttribute("script", script);

        return "form";
    }


    @GetMapping("/employees/update/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            Employee employee = employeeService.findById(id);
            Employee tempEmployee = (Employee) model.getAttribute("employee");

            String pathSave = "";
            String sendLink = "";
            String title = "";
            String script = "";
            boolean appSend = false;
            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                pathSave = employee.isSignUpSent() ? "" : "/employees/save";
                sendLink = employee.isSignUpSent() ? "" : "/employees/send";
                script = "/js/script-form-employees.js";
                title = "Nalog za prijavu";
                if (employee.isSignUpSent()) {
                    appSend = true;
                }
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                pathSave = employee.isSignOutSent() ? "" : "/employees/save";
                sendLink = employee.isSignOutSent() ? "" : "/employees/send";
                title = "Nalog za odjavu";
                if (employee.isSignOutSent()) {
                    appSend = true;
                }
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                pathSave = employee.isUpdateSent() ? "" : "/employees/save";
                sendLink = employee.isUpdateSent() ? "" : "/employees/send";
                title = "Nalog za promjenu";
                if (employee.isUpdateSent()) {
                    appSend = true;
                }
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
            if (!appSend) {
                model.addAttribute("script", script);
            }
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

        if (checkOibExists(employee)) {
            ra.addFlashAttribute("employee", employee);
            ra.addFlashAttribute("message", "Već postoji radnik unešen s tim OIB-om.");

            if (employee.getId() != null) {
                return "redirect:/employees/update/" + employee.getId();
            }
            return "redirect:/employees/new";
        } else if (!OibHandler.checkOib(employee.getOib())) {
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

        employee.setUser(userService.findById(UserIdTracker.getUserId()));
        employeeService.saveEmployee(employee);
        return "redirect:/employees/show";
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

    @PostMapping("/employees/send")
    public String addEmployeeSend(@ModelAttribute("employee") Employee employee, Model model, RedirectAttributes ra) {

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            employee.setFromSignUp(true);
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            employee.setFromUpdate(true);
        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            employee.setFromSignOut(true);
        }

        if (checkOibExists(employee)) {
            ra.addFlashAttribute("employee", employee);
            ra.addFlashAttribute("message", "Već postoji radnik unešen s tim OIB-om.");
            if (employee.getId() != null) {
                return "redirect:/employees/update/" + employee.getId();
            }
            return "redirect:/employees/new";
        } else if (!OibHandler.checkOib(employee.getOib())) {
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

        employee.setUser(userService.findById(UserIdTracker.getUserId()));

        employeeService.saveEmployee(employee);

        return "redirect:/employees/send/" + employee.getId();
    }


    @GetMapping("/employees/send/{id}")
    public String sendEmployeeMail(@PathVariable Long id, Model model, RedirectAttributes ra, HttpServletResponse response) {

        String messageTag = switch (FormTracker.getFormId()) {
            case 1 -> "prijavu";
            case 2 -> "promjenu podataka";
            case 3 -> "odjavu";
            default -> "";
        };

        try {

            Employee employeeToSend = employeeService.findById(id);
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

            List<Employee> employees = userService.findById(UserIdTracker.getUserId()).getEmployees();
            int counter = 0;

            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                employeeToSend.setSignUpSent(true);
                employeeToSend.setDateOfSignUpSent(date);
                employeeToSend.setTimeOfSignUpSent(time);
                for (Employee employee : employees) {
                    if (employee.isSignUpSent() && !employee.equals(employeeToSend)) {
                        counter++;
                    }
                }
                employeeToSend.setNumSignUp(counter + 1);
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                employeeToSend.setSignOutSent(true);
                employeeToSend.setDateOfSignOutSent(date);
                employeeToSend.setTimeOfSignOutSent(time);
                for (Employee employee : employees) {
                    if (employee.isSignOutSent() && !employee.equals(employeeToSend)) {
                        counter++;
                    }
                }
                employeeToSend.setNumSignOut(counter + 1);
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                employeeToSend.setUpdateSent(true);
                employeeToSend.setDateOfUpdateSent(date);
                employeeToSend.setTimeOfUpdateSent(time);
                employeeToSend.setDateOfUpdate(employeeToSend.getDateOfUpdateReal());
                for (Employee employee : employees) {
                    if (employee.isUpdateSent() && !employee.equals(employeeToSend)) {
                        counter++;
                    }
                }
                employeeToSend.setNumUpdate(counter + 1);
            }

            employeeService.saveEmployee(employeeToSend);

            String employeeName = employeeToSend.getFirstName() + " " + employeeToSend.getLastName();
            String mailRecipient = employeeToSend.getUser().getEmailToSend();
            String companyName = employeeToSend.getUser().getCompany();
            String mailSubject = companyName + "  Nalog za " + messageTag + " radnika: " + employeeName;
//            String mailText = employeeToSend.toString();
            String mailText = "Poštovani," + " \n" +
                    "" + " \n" +
                    "U privitku vam šaljemo Nalog za " + messageTag + " radnika: " + employeeName + " \n" +
                    "" + " \n" +
                    "Ova poruka je automatski generirana te Vas molimo da na nju ne odgovarate." + " \n" +
                    "" + " \n" +
                    "" + " \n" +
                    employeeToSend.getUser().getCompany() + " \n" +
                    employeeToSend.getUser().getAddress() + " \n" +
                    employeeToSend.getUser().getCity()+ " \n"  ;

                    // ovdje samo kreirati pdf i putanju proslijediti u mail za attachment
                    String pdfFilePath = createAppPdf(id, model, ra, response);

            SendMail.sendMail(mailRecipient, mailSubject, mailText, pdfFilePath);
            ra.addFlashAttribute("successMessage", "Nalog za " + messageTag + " radnika je poslan.");

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
            model.addAttribute("dataList", UserController.defineDataList(true, true));
            model.addAttribute("title", "Postavke");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "/employees/user/save");
            model.addAttribute("path", "/employees");
            model.addAttribute("sendLink", "");
            model.addAttribute("script", "/js/script-form-users.js");
            return "form";
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/employees";
        }
    }

    @PostMapping("/employees/user/save")
    public String addUser(@ModelAttribute("userDto") UserDto userDto, BindingResult result, Model model, RedirectAttributes ra) throws IllegalAccessException {

        User usernameExists = userService.findByUsername(userDto.getUsername());

        if (result.hasErrors()) {
            return showAddForm(model);
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

    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping("/employees/pdf/{id}")
    public void showEmployeeHtml(@PathVariable("id") Long id, Model model, RedirectAttributes ra, HttpServletResponse response) {

        try {

//            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
//
//            Employee employee = employeeService.findById(id);
//
//            String title = "";
//            String appOrder = "";
//            String appDate = "";
//            String appOrderDate = "";
//            String name = "";
//            String year = "";
//            boolean appSend = false;
//            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
//                title = "NALOG - PRIJAVA RADNIKA - HZMO - HZZO";
//                Date dateOfSignUpSent = employee.getDateOfSignUpSent();
//                year = new SimpleDateFormat("yyyy").format(dateOfSignUpSent);
//                appOrder = "Nalog: 1-" + employee.getNumSignUp() + "-" + year;
//                appDate = "Datum: " + sdf.format(employee.getDateOfSignUpSent()) + " Vrijeme: " + employee.getTimeOfSignUpSent();
//                name = "Prijava-" + id.toString() + "-" + employee.getNumSignUp();
//            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
//                title = "NALOG - ODJAVA RADNIKA - HZMO - HZZO";
//                Date dateOfSignOutSent = employee.getDateOfSignOutSent();
//                year = new SimpleDateFormat("yyyy").format(dateOfSignOutSent);
//                appOrder = "Nalog: 3-" + employee.getNumSignOut() + "-" + year;
//                appDate = "Datum: " + sdf.format(employee.getDateOfSignOutSent()) + " Vrijeme: " + employee.getTimeOfSignOutSent();
//                name = "Odjava-" + id.toString() + "-" + employee.getNumSignOut();
//            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
//                title = "NALOG - PROMJENA PODATAKA RADNIKA - HZMO - HZZO";
//                Date dateOfUpdateSent = employee.getDateOfUpdateSent();
//                year = new SimpleDateFormat("yyyy").format(dateOfUpdateSent);
//                appOrder = "Nalog: 2-" + employee.getNumUpdate() + "-" + year;
//                appDate = "Datum: " + sdf.format(employee.getDateOfUpdateSent()) + " Vrijeme: " + employee.getTimeOfUpdateSent();
//                name = "Promjena-" + id.toString() + "-" + employee.getNumUpdate();
//
//            }
//
//            appOrderDate = appOrder + " " + appDate;
//            model.addAttribute("name", name);
//            model.addAttribute("title", title);
//            model.addAttribute("appOrder", appOrder);
//            model.addAttribute("appDate", appDate);
//            model.addAttribute("appOrderDate", appOrderDate);
//            model.addAttribute("companyName", employee.getUser().getCompany());
//            model.addAttribute("companyOib", employee.getUser().getOib());
//
//            model.addAttribute("class", employee);
//            List<Data> dataList = defineDataList(id);
//            model.addAttribute("dataList", dataList);
//
//            // dalje je za PDF
//
//            String htmlContent = renderHtml(model);
//            String pdfFilePath = "prijavaRadnika/pdf/" + name + ".pdf";
//            HtmlToPdfConverter.convertHtmlContentToPdf(htmlContent, pdfFilePath);

            String pdfFilePath = createAppPdf(id, model, ra, response);

            // Postavi odgovarajuće zaglavlje
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=" + pdfFilePath);

            // Učitaj generirani PDF dokument
            File pdfFile = new File(pdfFilePath);
            FileInputStream fileInputStream = new FileInputStream(pdfFile);

            // Kopiraj PDF sadržaj u odgovor
            IOUtils.copy(fileInputStream, response.getOutputStream());

            // Zatvori tokove
            fileInputStream.close();
            response.getOutputStream().flush();

        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            boolean appSend = false;
            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                title = "NALOG - PRIJAVA RADNIKA - HZMO - HZZO";
                Date dateOfSignUpSent = employee.getDateOfSignUpSent();
                year = new SimpleDateFormat("yyyy").format(dateOfSignUpSent);
                appOrder = "Nalog: 1-" + employee.getNumSignUp() + "-" + year;
                appDate = "Datum: " + sdf.format(employee.getDateOfSignUpSent()) + " Vrijeme: " + employee.getTimeOfSignUpSent();
                name = "Prijava-" + id.toString() + "-" + employee.getNumSignUp();
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                title = "NALOG - ODJAVA RADNIKA - HZMO - HZZO";
                Date dateOfSignOutSent = employee.getDateOfSignOutSent();
                year = new SimpleDateFormat("yyyy").format(dateOfSignOutSent);
                appOrder = "Nalog: 3-" + employee.getNumSignOut() + "-" + year;
                appDate = "Datum: " + sdf.format(employee.getDateOfSignOutSent()) + " Vrijeme: " + employee.getTimeOfSignOutSent();
                name = "Odjava-" + id.toString() + "-" + employee.getNumSignOut();
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                title = "NALOG - PROMJENA PODATAKA RADNIKA - HZMO - HZZO";
                Date dateOfUpdateSent = employee.getDateOfUpdateSent();
                year = new SimpleDateFormat("yyyy").format(dateOfUpdateSent);
                appOrder = "Nalog: 2-" + employee.getNumUpdate() + "-" + year;
                appDate = "Datum: " + sdf.format(employee.getDateOfUpdateSent()) + " Vrijeme: " + employee.getTimeOfUpdateSent();
                name = "Promjena-" + id.toString() + "-" + employee.getNumUpdate();

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

            // dalje je za PDF

            String htmlContent = renderHtml(model);
            String pdfFilePath = "prijavaRadnika/pdf/" + name + ".pdf";
            HtmlToPdfConverter.convertHtmlContentToPdf(htmlContent, pdfFilePath);

            return pdfFilePath;


        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "ERROR";
    }


    private String renderHtml(Model model) {
        Context context = new Context();
        context.setVariables(model.asMap());
        return templateEngine.process("app-html", context);
    }


    private boolean checkOibExists(Employee employee) {

        Employee tempEmployee = employeeService.findByOib(employee.getOib());
        boolean oibExist = false;
        if (tempEmployee != null && employee.getId() != tempEmployee.getId()) {
            oibExist = true;
        }
        return oibExist;
    }

    private List<Data> defineDataList(long id) {

        List<Data> dataList = new ArrayList<>();
        List<String> items = new ArrayList<>();
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
        dataList.add(new Data("1.", "OIB *", "oib", "", "", "", "text", "true", fieldStatus, items, "false"));
        ;
        dataList.add(new Data("2.", "Ime *", "firstName", "", "", "", "text", "true", fieldStatus, items, "false"));
        ;
        dataList.add(new Data("3.", "Prezime *", "lastName", "", "", "", "text", "true", fieldStatus, items, "false"));
        ;
        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {

            dataList.add(new Data("4.", "Spol *", "gender", "", "", "", "text", "false", fieldStatus, Employee.GENDER, "false"));
            ;
            dataList.add(new Data("5.", "Datum rođenja *", "dateOfBirth", "", "", "", "date", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("6.", "Adresa *", "address", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("7.", "Poštanski broj i grad *", "city", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("8.", "Stvarna stručna sprema *", "professionalQualification", "", "", "", "text", "false", fieldStatus, Employee.PROFESSIONAL_QUALIFICATION, "false"));
            ;
            dataList.add(new Data("9.", "Naziv najviše završene škole", "highestLevelOfEducation", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("10.", "IBAN - tekući račun - redovni", "ibanRegular", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("11.", "IBAN - tekući račun - zaštićeni", "ibanProtected", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("12.", "Radno mjesto *", "employmentPosition", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("13.", "Mjesto rada - Grad *", "cityOfEmployment", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("14.", "Potrebna stručna sprema *", "requiredProfessionalQualification", "", "", "", "text", "false", fieldStatus, Employee.PROFESSIONAL_QUALIFICATION, "false"));
            ;
            dataList.add(new Data("15.", "Ugovor o radu *", "employmentContract", "", "", "", "text", "false", fieldStatus, Employee.EMPLOYMENT_CONTRACT, "false"));
            ;
            dataList.add(new Data("16.", "Razlog - na određeno *", "reasonForDefinite", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("17.", "Dodatni rad *", "additionalWork", "", "", "", "checkbox", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("17a.", "Dodatni rad - sati *", "additionalWorkHours", "", "", "", "myDecimal", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("18.", "Radno vrijeme *", "workingHours", "", "", "", "text", "false", fieldStatus, Employee.WORKING_HOURS, "false"));
            ;
            dataList.add(new Data("18a.", "Sati nepuno *", "hoursForPartTime", "", "", "", "myDecimal", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("19.", "Neradni dan(i) u tjednu *", "nonWorkingDays", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("20.", "Datum prijave *", "dateOfSignUp", "", "", "", "date", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("21.", "Datum odjave - za određeno *", "dateOfSignOut", "", "", "", "date", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("22.", "Bruto / Neto *", "salaryType", "", "", "", "text", "false", fieldStatus, Employee.SALARY_TYPE, "false"));
            ;
            dataList.add(new Data("22a.", "Iznos osnovne plaće *", "basicSalary", "", "", "", "myDecimal", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("23.", "Strani državljanin *", "foreignNational", "", "", "", "checkbox", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("23a.", "Radna dozvola vrijedi do *", "expiryDateOfWorkPermit", "", "", "", "date", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("24.", "Umirovljenik *", "retiree", "", "", "", "checkbox", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("25.", "Mlađi od 30 godina *", "youngerThanThirty", "", "", "", "checkbox", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("26.", "Prvo zaposlenje *", "firstEmployment", "", "", "", "checkbox", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("27.", "Invalid *", "disability", "", "", "", "checkbox", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("28.", "Napomena", "noteSignUp", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;

        } else {
            dataList.add(new Data("4.", "Datum prijave *", "dateOfSignUp", "", "", "", "date", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("5.", "Datum zadnje promjene *", "dateOfUpdate", "", "", "", "date", "false", fieldStatus, items, "false"));
            ;
        }
        if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            dataList.add(new Data("6.", "Datum odjave - iz Prijave *", "dateOfSignOut", "", "", "", "date", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("7.", "Datum odjave - stvarni *", "dateOfSignOutReal", "", "", "", "date", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("8.", "Razlog odjave *", "reasonForSignOut", "", "", "", "text", "false", fieldStatus, Employee.REASON_FOR_SIGN_OUT, "false"));
            ;
            dataList.add(new Data("9.", "Napomena", "noteSignOut", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            dataList.add(new Data("6.", "Datum promjene  *", "dateOfUpdateReal", "", "", "", "date", "false", fieldStatus, items, "false"));
            ;
            dataList.add(new Data("7.", "Razlog promjene *", "reasonForUpdate", "", "", "", "text", "false", fieldStatus, Employee.REASON_FOR_UPDATE, "true"));
            ;
            dataList.add(new Data("8.", "Napomena", "noteUpdate", "", "", "", "text", "false", fieldStatus, items, "false"));
            ;
        }
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