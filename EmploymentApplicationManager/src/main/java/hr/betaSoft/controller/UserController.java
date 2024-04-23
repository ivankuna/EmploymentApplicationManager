package hr.betaSoft.controller;

import hr.betaSoft.security.exception.UserNotFoundException;
import hr.betaSoft.security.model.User;
import hr.betaSoft.security.service.UserService;
import hr.betaSoft.security.userdto.UserDto;
import hr.betaSoft.tools.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/show")
    public String showUsers(Model model, HttpServletRequest request) {

        DeviceDetector deviceDetector = new DeviceDetector();
        boolean isMobile = deviceDetector.isMobileDevice(request);

        List<Column> columnList = new ArrayList<>();

        if (isMobile) {
            columnList.add(new Column("Tvrtka", "company", "id",""));
            columnList.add(new Column("Korisnik", "username", "id",""));
            columnList.add(new Column("Licenca", "dateOfUserAccountExpiry", "id",""));
        } else {
            columnList.add(new Column("ID", "id", "id",""));
            columnList.add(new Column("Korisničko ime", "username", "id",""));
            columnList.add(new Column("OIB", "oib", "id",""));
            columnList.add(new Column("Naziv tvrtke", "company", "id",""));
            columnList.add(new Column("Adresa", "address", "id",""));
            columnList.add(new Column("Grad", "city", "id",""));
            columnList.add(new Column("Osoba", "name", "id",""));
            columnList.add(new Column("Telefon", "telephone", "id",""));
            columnList.add(new Column("e-mail korisnika", "email", "id",""));
            columnList.add(new Column("e-mail primatelja naloga", "emailToSend", "id",""));
            columnList.add(new Column("Prikaz svih naloga", "showAllApplications", "id",""));
            columnList.add(new Column("Datum licence", "dateOfUserAccountExpiry", "id",""));
        }

        List<User> userList = userService.findAll();

        model.addAttribute("title", "Popis korisnika");
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", userList);
        model.addAttribute("addBtnText", "Novi korisnik");
        model.addAttribute("path", "/users");
        model.addAttribute("addLink", "/users/new");
        model.addAttribute("sendLink", "");
        model.addAttribute("pdfLink", "");
        model.addAttribute("updateLink", "/users/update/{id}");
        model.addAttribute("deleteLink", "/users/delete/{id}");
        model.addAttribute("showLink", "");
        model.addAttribute("tableName", "users");
        model.addAttribute("script", "/js/script-table-users.js");

        return "table";
    }

    @GetMapping("/users/select")
    public String showUsersForSelection(Model model, HttpServletRequest request) {

        DeviceDetector deviceDetector = new DeviceDetector();
        boolean isMobile = deviceDetector.isMobileDevice(request);

        List<Column> columnList = new ArrayList<>();

        if (isMobile) {
            columnList.add(new Column("Tvrtka", "company", "id",""));
            columnList.add(new Column("Korisnik", "username", "id",""));
            columnList.add(new Column("Licenca", "dateOfUserAccountExpiry", "id",""));

        } else {
            columnList.add(new Column("ID", "id", "id",""));
            columnList.add(new Column("Korisničko ime", "username", "id",""));
            columnList.add(new Column("OIB", "oib", "id",""));
            columnList.add(new Column("Naziv tvrtke", "company", "id",""));
            columnList.add(new Column("Adresa", "address", "id",""));
            columnList.add(new Column("Grad", "city", "id",""));
            columnList.add(new Column("Osoba", "name", "id",""));
            columnList.add(new Column("Telefon", "telephone", "id",""));
            columnList.add(new Column("e-mail korisnika", "email", "id",""));
            columnList.add(new Column("e-mail primatelja naloga", "emailToSend", "id",""));
            columnList.add(new Column("Prikaz svih naloga", "showAllApplications", "id",""));
            columnList.add(new Column("Datum licence", "dateOfUserAccountExpiry", "id",""));
        }

        List<User> userList = userService.findAll();

        model.addAttribute("title", "Popis korisnika");
        model.addAttribute("columnList", columnList);
        model.addAttribute("path", "/users");
        model.addAttribute("dataList", userList);
        model.addAttribute("updateLink", "/users/employees/show/{id}");
        model.addAttribute("tableName", "users");
        model.addAttribute("script", "/js/script-table-users.js");
        model.addAttribute("showLink", "");
        model.addAttribute("pdfLink", "");
        model.addAttribute("deleteLink", "");

        return "table";
    }

    @GetMapping("/users/new")
    public String showAddForm(Model model) {

        UserDto userDto = (UserDto) model.getAttribute("userDto");

        if (userDto != null) {
            model.addAttribute("class", userDto);
        } else {
            model.addAttribute("class", new UserDto());
        }
        List<String> dummyHidden = new ArrayList<>();
        model.addAttribute("dataList", defineDataList(false, false));
        model.addAttribute("hiddenList", dummyHidden);
        model.addAttribute("title", "Korisnik");
        model.addAttribute("dataId", "id");
        model.addAttribute("pathSave", "/users/save");
        model.addAttribute("path", "/users/show");
        model.addAttribute("sendLink", "");
        model.addAttribute("pathSaveSend", "");
        model.addAttribute("script", "/js/script-form-users.js");
        return "form";
    }

    @GetMapping("users/update/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            UserDto user = userService.convertEntityToDto(userService.findById(id));
            List<String> dummyHidden = new ArrayList<>();
            model.addAttribute("class", user);
            model.addAttribute("dataList", defineDataList(true, false));
            model.addAttribute("hiddenList", dummyHidden);
            model.addAttribute("title", "Korisnik");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "/users/save");
            model.addAttribute("path", "/users/show");
            model.addAttribute("sendLink", "");
            model.addAttribute("pathSaveSend", "");
            model.addAttribute("script", "/js/script-form-users.js");
            return "form";
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/users/show";
        }
    }

    @PostMapping("/users/save")
    public String addUser(@ModelAttribute("userDto") UserDto userDto, BindingResult result, Model model, RedirectAttributes ra) {

        User usernameExists = userService.findByUsername(userDto.getUsername());

        if (usernameExists != null) {
            ra.addFlashAttribute("userDto", userDto);
            ra.addFlashAttribute("message", "Već postoji račun registriran s tim korisničkim imenom.");

            if (userDto.getId() != null) {
                return "redirect:/users/update/" + userDto.getId();
            }
            return "redirect:/users/new";
        }

        if (!OibHandler.checkOib(userDto.getOib())) {
            ra.addFlashAttribute("userDto", userDto);
            ra.addFlashAttribute("message", "Neispravan unos OIB-a.");

            if (userDto.getId() != null) {
                return "redirect:/users/update/" + userDto.getId();
            }
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
        return "redirect:/users/show";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {

        try {
            if (id == 1) {
                ra.addFlashAttribute("message", "Nije moguće obrisati administratora!");
            } else if (userService.checkIfEmployeeUnderUserExist(id)) {
                ra.addFlashAttribute("message", "Nije moguće obrisati korisnika s unešenim radnicima!");
            } else {
                userService.deleteUser(id);
            }
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users/show";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "access-denied";
    }

    @GetMapping("/users/pdf/{id}")
    public String showUsersPdf(@PathVariable Long id, RedirectAttributes ra) {

        try {
            ra.addFlashAttribute("message", "Create PDF for user_id = " + id.toString());
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/users/show";
    }

    public static List<Data> defineDataList(boolean update, boolean emplUpdate) {

        String fieldStatus = emplUpdate ? "false" : "true";

        List<Data> dataList = new ArrayList<>();

        List<String> items = new ArrayList<>();

        dataList.add(new Data("1.","Naziv tvrtke *", "company", "", "", "", "text", "true", fieldStatus, items,"false"));
        ;
        dataList.add(new Data("2.","OIB *", "oib", "", "", "", "text", "true", fieldStatus, items,"false"));
        ;
        dataList.add(new Data("3.","Adresa *", "address", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("4.","Poštanski broj i grad *", "city", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("5.","Ime *", "firstName", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("6.","Prezime *", "lastName", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("7.","Telefon *", "telephone", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("8.","e-mail korisnika *", "email", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("9.","e-mail primatelja naloga *", "emailToSend", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("10.","Datum isteka roka korisničkog računa", "dateOfUserAccountExpiry", "", "", "", "date-pick", "true", fieldStatus, items,"false"));
        ;
        dataList.add(new Data("11.","Prikaz svih naloga", "showAllApplications", "", "", "", "checkbox", "false", "", items,"false"));
        ;
        if (!update) {
            dataList.add(new Data("12.","Korisničko ime *", "username", "", "", "", "text", "true", "", items,"false"));
            ;
            dataList.add(new Data("13.","Lozinka *", "password", "", "", "", "text", "true", "", items,"false"));
            ;
        }
        return dataList;
    }
}