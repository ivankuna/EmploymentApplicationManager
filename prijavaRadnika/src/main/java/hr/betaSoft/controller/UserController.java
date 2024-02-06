package hr.betaSoft.controller;

import hr.betaSoft.security.exception.UserNotFoundException;
import hr.betaSoft.security.secModel.User;
import hr.betaSoft.security.secService.UserService;
import hr.betaSoft.security.userdto.UserDto;
import hr.betaSoft.tools.Column;
import hr.betaSoft.tools.Data;
import jakarta.validation.Valid;
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

    @GetMapping("/login")
    public String loginForm(Model model) {

        if (userService.countUsers() != 0) {
            return "login";
        }
        return showRegistrationForm(model);
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user) {

        userService.saveUser(user);
        return "redirect:/login?success";
    }

    @GetMapping("/users")
    public String showUsers(Model model) {

        List<Column> columnList = new ArrayList<>();

        columnList.add(new Column("ID", "id", "id"));
        columnList.add(new Column("Korisničko ime", "username", "id"));
        columnList.add(new Column("OIB", "oib", "id"));
        columnList.add(new Column("Naziv tvrtke", "company", "id"));
        columnList.add(new Column("Adresa", "address", "id"));
        columnList.add(new Column("Naziv grada", "city", "id"));
        columnList.add(new Column("Osoba", "name", "id"));
        columnList.add(new Column("Telefon", "telephone", "id"));
        columnList.add(new Column("E-Mail", "email", "id"));
        columnList.add(new Column("E-Mail za slanje", "emailToSend", "id"));

        List<User> userList = userService.findAll();
        model.addAttribute("dataList", userList);

        model.addAttribute("title", "Popis korisnika");
        model.addAttribute("addLink", "/users/new");
        model.addAttribute("updateLink", "/users/update/{id}");
        model.addAttribute("deleteLink", "/users/delete/{id}");
        model.addAttribute("columnList", columnList);

        return "table";
    }

    @GetMapping("/users/new")
    public String showAddForm(Model model) {

        List<Data> dataList = new ArrayList<>();

        dataList.add(new Data("Korisničko ime:", "username","", "",""));;
        dataList.add(new Data("OIB:", "oib","", "",""));;
        dataList.add(new Data("Naziv tvrtke:", "company","", "",""));;
        dataList.add(new Data("Adresa:", "address","", "",""));;
        dataList.add(new Data("Naziv grada:", "city","", "",""));;
        dataList.add(new Data("Ime:", "firstName","", "",""));;
        dataList.add(new Data("Prezime:", "lastName","", "",""));;
        dataList.add(new Data("Telefon:", "telephone","", "",""));;
        dataList.add(new Data("E-Mail:", "email","", "",""));;
        dataList.add(new Data("E-Mail za slanje:", "emailToSend","", "",""));;
        dataList.add(new Data("Lozinka:", "password","", "",""));;

        UserDto userDto = (UserDto) model.getAttribute("userDto");

        if (userDto != null) {
            model.addAttribute("class", userDto);
        } else {
            model.addAttribute("class", new UserDto());
        }

        model.addAttribute("dataList", dataList);
        model.addAttribute("title", "Korisnik");
        model.addAttribute("dataId", "id");
        model.addAttribute("btnName", "Prihvati");
        model.addAttribute("path", "/users");

        return "form";
    }

    @GetMapping("users/update/{id}")
    public String showEditForm(@PathVariable("id") long id, Model model, RedirectAttributes ra) {

        try {
            List<Data> dataList = new ArrayList<>();

            dataList.add(new Data("OIB:", "oib","", "",""));;
            dataList.add(new Data("Naziv tvrtke:", "company","", "",""));;
            dataList.add(new Data("Adresa:", "address","", "",""));;
            dataList.add(new Data("Naziv grada:", "city","", "",""));;
            dataList.add(new Data("Ime:", "firstName","", "",""));;
            dataList.add(new Data("Prezime:", "lastName","", "",""));;
            dataList.add(new Data("Telefon:", "telephone","", "",""));;
            dataList.add(new Data("E-Mail:", "email","", "",""));;
            dataList.add(new Data("E-Mail za slanje:", "emailToSend","", "",""));;

            UserDto user = userService.convertEntityToDto(userService.findById(id));

            model.addAttribute("class", user);
            model.addAttribute("dataList", dataList);
            model.addAttribute("title", "Korisnik");
            model.addAttribute("dataId", "id");
            model.addAttribute("btnName", "Prihvati");
            model.addAttribute("path", "/users");
            return "form";
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }
    }

    @PostMapping("/users/save")
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
        return "redirect:/users";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {

        return "access-denied";
    }
}
