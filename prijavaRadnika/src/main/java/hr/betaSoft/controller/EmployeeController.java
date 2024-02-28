package hr.betaSoft.controller;

import hr.betaSoft.exception.EmployeeNotFoundException;
import hr.betaSoft.model.Employee;
import hr.betaSoft.pdf.PDFGenerator;
import hr.betaSoft.pdf.PdfAppGenerator;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
            columnList.add(new Column("Prezime", "lastName", "id"));
            columnList.add(new Column("Ime", "firstName", "id"));
            if (!isAdmin) {
                columnList.add(new Column("Datum", "dateOfSignUpSent", "id"));
                columnList.add(new Column("", statusField, "id"));
            }
        } else {
            // OBRATI PAŽNJU!
            // dateOfSignUp/dateOfSignOut/dateOfUpdate I dateOfUpdateSentReal/dateOfSignOutSentReal SU RUČNO UPISANI U NALOGE
            // dateOfSignUpSent/dateOfSignOutSent/dateOfUpdateSent I timeOfSignUpSent/timeOfSignOutSent/timeOfUpdateSent SU GENERIRANI PRI SLANJU NALOGA
            //// KOLONE ZA PRIJAVU ////
            if (isAdmin) {
                columnList.add(new Column("prijava", "fromSignUp", "id"));
                columnList.add(new Column("promjena", "fromUpdate", "id"));
                columnList.add(new Column("odjava", "fromSignOut", "id"));

            } else {
                columnList.add(new Column("Status", statusField, "id"));
            }

            columnList.add(new Column("OIB", "oib", "id"));
            columnList.add(new Column("Ime", "firstName", "id"));
            columnList.add(new Column("Prezime", "lastName", "id"));
            columnList.add(new Column("Spol", "gender", "id"));
            columnList.add(new Column("Datum rođenja", "dateOfBirth", "id"));
            columnList.add(new Column("Adresa", "address", "id"));
            columnList.add(new Column("Grad", "city", "id"));
            if (isAdmin) {
                columnList.add(new Column("mail prijava", "signUpSent", "id"));
                columnList.add(new Column("mail promjena", "updateSent", "id"));
                columnList.add(new Column("mail odjava", "signOutSent", "id"));
            }

//            //// SVE KOLONE ////
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
//
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
//
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
            title = "PRIJAVA RADNIKA";
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            title = "PROMJENA KOD RADNIKA";
        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            title = "ODJAVA RADNIKA";
        }
        if (isAdmin) {
            title = "EVIDENCIJA RADNIKA " + authenticatedUser.getCompany();
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

        if (isMobile && !isAdmin) {
            model.addAttribute("pdfLink", "");
        } else {
            model.addAttribute("pdfLink", "/employees/pdf/{id}");
        }

        model.addAttribute("updateLink", "/employees/update/{id}");
        model.addAttribute("deleteLink", "/employees/delete/{id}");
        model.addAttribute("showLink", "");
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

        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
            title = "Nalog za prijavu";
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
        model.addAttribute("pathShow", "/employees/show");
        model.addAttribute("sendLink", "/employees/send");
        model.addAttribute("script", "/js/script-form-employees.js");

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
            boolean appSend = false;
            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                pathSave = employee.isSignUpSent() ? "" : "/employees/save";
                sendLink = employee.isSignUpSent() ? "" : "/employees/send";
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
            model.addAttribute("pathShow", "/employees/show");
            model.addAttribute("sendLink", sendLink);
            if (!appSend) {
                model.addAttribute("script", "/js/script-form-employees.js");
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
    public String sendEmployeeMail(@PathVariable Long id, RedirectAttributes ra) {

        String messageTag = switch (FormTracker.getFormId()) {
            case 1 -> "prijavu";
            case 2 -> "promjenu";
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

            // NE TREBA VIŠE JER NAKON ŠTO SE POŠALJE VIŠE NEMA GUMBA ZA SLANJE
//            if (employeeToSend.isSignUpSent()) {
//                ra.addFlashAttribute("message", "Nalog za " + messageTag + " radnika je već poslan.");
//                return "redirect:/employees/update/" + id;
//            }

            String recipient = employeeToSend.getUser().getEmailToSend();
            SendMail.sendMail(recipient, "Nalog za " + messageTag + " radnika", employeeToSend.toString());
            ra.addFlashAttribute("successMessage", "Nalog za " + messageTag + " radnika je poslan.");

            Date date = new Date(Calendar.getInstance().getTime().getTime());

            ZoneId zoneId = ZoneId.of("Europe/Zagreb");
            ZonedDateTime currentTime = ZonedDateTime.now(zoneId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String time = currentTime.format(formatter);

            if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
                employeeToSend.setSignUpSent(true);
                employeeToSend.setDateOfSignUpSent(date);
                employeeToSend.setTimeOfSignUpSent(time);
            } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
                employeeToSend.setSignOutSent(true);
                employeeToSend.setDateOfSignOutSent(date);
                employeeToSend.setTimeOfSignOutSent(time);
            } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                employeeToSend.setUpdateSent(true);
                employeeToSend.setDateOfUpdateSent(date);
                employeeToSend.setTimeOfUpdateSent(time);
            }

            employeeService.saveEmployee(employeeToSend);

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
            model.addAttribute("pathShow", "/employees");
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
        }

        userService.saveUser(userDto);
        return "redirect:/employees";
    }

    @GetMapping("/employees/pdf/{id}")
    public void showEmployessPdf(@PathVariable Long id, RedirectAttributes ra, HttpServletResponse response) {

//        try {
//            PdfAppGenerator pdfAppGenerator = new PdfAppGenerator(employeeService);
//            pdfAppGenerator.generateApplication(id);
//
//            // Postavi odgovarajuće zaglavlje
//            response.setContentType("application/pdf");
//            response.setHeader("Content-Disposition", "inline; filename=employee.pdf");
//
//            // Učitaj generisani PDF dokument
//            File pdfFile = new File("pdf/employee.pdf");
//            FileInputStream fileInputStream = new FileInputStream(pdfFile);
//
//            // Kopiraj PDF sadržaj u odgovor
//            IOUtils.copy(fileInputStream, response.getOutputStream());
//
//            // Zatvori tokove
//            fileInputStream.close();
//            response.getOutputStream().flush();
//        } catch (IOException | EmployeeNotFoundException e) {
//            // Handlaj izuzetak
//        }

        try {
            String filePath = "pdf/employee.pdf";

            // Generate the PDF
            PDFGenerator.generatePDF(employeeService.findById(id), filePath);
        } catch (EmployeeNotFoundException e) {
            // Handlaj izuzetak
        }
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

        dataList.add(new Data("OIB *", "oib", "", "", "", "text", "true", fieldStatus, items));
        ;
        dataList.add(new Data("Ime *", "firstName", "", "", "", "text", "true", fieldStatus, items));
        ;
        dataList.add(new Data("Prezime *", "lastName", "", "", "", "text", "true", fieldStatus, items));
        ;
        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {

            dataList.add(new Data("Spol *", "gender", "", "", "", "text", "false", fieldStatus, Employee.GENDER));
            ;
            dataList.add(new Data("Datum rođenja *", "dateOfBirth", "", "", "", "date", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Adresa *", "address", "", "", "", "text", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Poštanski broj i grad *", "city", "", "", "", "text", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Stvarna stručna sprema *", "professionalQualification", "", "", "", "text", "false", fieldStatus, Employee.PROFESSIONAL_QUALIFICATION));
            ;
            dataList.add(new Data("Naziv najviše završene škole", "highestLevelOfEducation", "", "", "", "text", "false", fieldStatus, items));
            ;
            dataList.add(new Data("IBAN - tekući račun - redovni", "ibanRegular", "", "", "", "text", "false", fieldStatus, items));
            ;
            dataList.add(new Data("IBAN - tekući račun - zaštićeni", "ibanProtected", "", "", "", "text", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Radno mjesto *", "employmentPosition", "", "", "", "text", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Mjesto rada - Grad *", "cityOfEmployment", "", "", "", "text", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Potrebna stručna sprema *", "requiredProfessionalQualification", "", "", "", "text", "false", fieldStatus, Employee.PROFESSIONAL_QUALIFICATION));
            ;
            dataList.add(new Data("Ugovor o radu *", "employmentContract", "", "", "", "text", "false", fieldStatus, Employee.EMPLOYMENT_CONTRACT));
            ;
            dataList.add(new Data("Razlog - na određeno *", "reasonForDefinite", "", "", "", "text", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Dodatni rad *", "additionalWork", "", "", "", "checkbox", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Dodatni rad - sati *", "additionalWorkHours", "", "", "", "number", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Radno vrijeme *", "workingHours", "", "", "", "text", "false", fieldStatus, Employee.WORKING_HOURS));
            ;
            dataList.add(new Data("Sati nepuno *", "hoursForPartTime", "", "", "", "number", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Neradni dan(i) u tjednu *", "nonWorkingDays", "", "", "", "text", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Datum prijave *", "dateOfSignUp", "", "", "", "date", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Datum odjave - za određeno *", "dateOfSignOut", "", "", "", "date", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Bruto / Neto *", "salaryType", "", "", "", "text", "false", fieldStatus, Employee.SALARY_TYPE));
            ;
            dataList.add(new Data("Iznos osnovne plaće *", "basicSalary", "", "", "", "myDecimal", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Strani državljanin *", "foreignNational", "", "", "", "checkbox", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Radna dozvola vrijedi do *", "expiryDateOfWorkPermit", "", "", "", "date", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Umirovljenik *", "retiree", "", "", "", "checkbox", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Mlađi od 30 godina *", "youngerThanThirty", "", "", "", "checkbox", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Prvo zaposlenje *", "firstEmployment", "", "", "", "checkbox", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Invalid *", "disability", "", "", "", "checkbox", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Napomena", "noteSignUp", "", "", "", "text", "false", fieldStatus, items));
            ;

        } else {
            dataList.add(new Data("Datum prijave *", "dateOfSignUp", "", "", "", "date", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Datum zadnje promjene *", "dateOfUpdate", "", "", "", "date", "false", fieldStatus, items));
            ;
        }
        if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
            dataList.add(new Data("Datum odjave - iz Prijave *", "dateOfSignOut", "", "", "", "date", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Datum odjave - stvarni *", "dateOfSignOutReal", "", "", "", "date", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Razlog odjave *", "reasonForSignOut", "", "", "", "text", "false", fieldStatus, Employee.REASON_FOR_SIGN_OUT));
            ;
            dataList.add(new Data("Napomena", "noteSignOut", "", "", "", "text", "false", fieldStatus, items));
            ;
        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
            dataList.add(new Data("Datum promjene  *", "dateOfUpdateReal", "", "", "", "date", "false", fieldStatus, items));
            ;
            dataList.add(new Data("Razlog promjene *", "reasonForUpdate", "", "", "", "text", "false", fieldStatus, Employee.REASON_FOR_UPDATE));
            ;
            dataList.add(new Data("Napomena", "noteUpdate", "", "", "", "text", "false", fieldStatus, items));
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