package hr.betaSoft.controller;

import hr.betaSoft.security.exception.UserNotFoundException;
import hr.betaSoft.security.secModel.User;
import hr.betaSoft.security.secService.UserService;
import hr.betaSoft.security.userdto.UserDto;
import hr.betaSoft.tools.Column;
import hr.betaSoft.tools.Data;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/authorization")
    public String authorization() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<GrantedAuthority> authorityList = new ArrayList<>(authorities);

        if (authorityList.get(0).getAuthority().equals("ROLE_ADMIN")) {
            return "redirect:/users";
        } else {
            return "redirect:/employees";
        }
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
        columnList.add(new Column("e-mail", "email", "id"));
        columnList.add(new Column("e-mail za prijavu", "emailToSend", "id"));

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

        dataList.add(new Data("Naziv tvrtke:", "company","", "","","text"));;
        dataList.add(new Data("OIB:", "oib","", "","","text"));;
        dataList.add(new Data("Adresa:", "address","", "","","text"));;
        dataList.add(new Data("Grad:", "city","", "","","text"));;
        dataList.add(new Data("Ime:", "firstName","", "","","text"));;
        dataList.add(new Data("Prezime:", "lastName","", "","","text"));;
        dataList.add(new Data("Telefon:", "telephone","", "","","text"));;
        dataList.add(new Data("e-mail:", "email","", "","","text"));;
        dataList.add(new Data("e-mail za prijavu:", "emailToSend","", "","","text"));;
        dataList.add(new Data("Korisničko ime:", "username","", "","","text"));;
        dataList.add(new Data("Lozinka:", "password","", "","","text"));;

        UserDto userDto = (UserDto) model.getAttribute("userDto");

        if (userDto != null) {
            model.addAttribute("class", userDto);
        } else {
            model.addAttribute("class", new UserDto());
        }

        model.addAttribute("dataList", dataList);
        model.addAttribute("title", "Korisnik");
        model.addAttribute("dataId", "id");
        model.addAttribute("btnName", "Spremi");
        model.addAttribute("path", "/users");

        return "form";
    }

    @GetMapping("users/update/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            List<Data> dataList = new ArrayList<>();

            dataList.add(new Data("Naziv tvrtke:", "company","", "","","text"));;
            dataList.add(new Data("OIB:", "oib","", "","","text"));;
            dataList.add(new Data("Adresa:", "address","", "","","text"));;
            dataList.add(new Data("Grad:", "city","", "","","text"));;
            dataList.add(new Data("Ime:", "firstName","", "","","text"));;
            dataList.add(new Data("Prezime:", "lastName","", "","","text"));;
            dataList.add(new Data("Telefon:", "telephone","", "","","text"));;
            dataList.add(new Data("e-mail:", "email","", "","","text"));;
            dataList.add(new Data("e-mail za prijavu:", "emailToSend","", "","","text"));;

            UserDto user = userService.convertEntityToDto(userService.findById(id));

            model.addAttribute("class", user);
            model.addAttribute("dataList", dataList);
            model.addAttribute("title", "Korisnik");
            model.addAttribute("dataId", "id");
            model.addAttribute("btnName", "Ažuriraj");
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

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            if (id == 1) {
                ra.addFlashAttribute("message", "Nemoguće obrisati administratora!");
            } else {
                userService.deleteUser(id);
            }
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {

        return "access-denied";
    }
}
