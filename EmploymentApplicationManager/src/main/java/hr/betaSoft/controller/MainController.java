package hr.betaSoft.controller;


import hr.betaSoft.security.model.User;
import hr.betaSoft.security.service.UserService;
import hr.betaSoft.security.userdto.UserDto;
import hr.betaSoft.tools.FormTracker;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class MainController {

    private UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            User loggedUser = userService.findByUsername(username);
            long id = loggedUser.getId();
            return id;
        }
        return 0L;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        if (userService.countUsers() != 0) {
            return "login";
        }
        return showRegistrationForm(model);
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (userService.countUsers() != 0) {
            return "redirect:/login";
        }
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user) {
        userService.saveUser(user);
        return "redirect:/login?success";
    }

    @GetMapping("/authorization")
    public String authorization(Model model) {

        if (userService.getAuthenticatedUser().getDateOfUserAccountExpiry().before(new Date(System.currentTimeMillis()))) {
            model.addAttribute("accExpired", true);
            model.addAttribute("message", "Rok vašeg korisničkog računa je istekao!");
            return "login";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        FormTracker.setFormId(FormTracker.getSIGN_UP());

        List<GrantedAuthority> authorityList = new ArrayList<>(authorities);
        if (authorityList.get(0).getAuthority().equals("ROLE_ADMIN")) {
            return "redirect:/users";
        } else {
            return "redirect:/employees";
        }
    }

    @GetMapping("/users")
    public String showAdminMenuForm() {
        return "users";
    }

    @GetMapping("/employees")
    public String showUserMenuForm() {
        return "employees";
    }

    @GetMapping("/empsignup")
    public String employeeSignUp() {
        FormTracker.setFormId(FormTracker.getSIGN_UP());
        return "redirect:/employees/show";
    }

    @GetMapping("/empupdate")
    public String employeeUpdate() {
        FormTracker.setFormId(FormTracker.getUPDATE());
        return "redirect:/employees/show";
    }

    @GetMapping("/empsignout")
    public String employeeSignOut() {
        FormTracker.setFormId(FormTracker.getSIGN_OUT());
        return "redirect:/employees/show";
    }

    @GetMapping("/settings")
    public String companySettings() {

        long id = getCurrentUserId();
        if (id != 0L) {
            return "redirect:/employees/user/update/" + id;
        } else {
            return "page-not-found";
        }
    }

    @GetMapping("/users/select/sign-up")
    public String selectSignUp() {
        FormTracker.setFormId(FormTracker.getSIGN_UP());
        return "redirect:/users/select";
    }

    @GetMapping("/users/select/update")
    public String selectUpdate() {
        FormTracker.setFormId(FormTracker.getUPDATE());
        return "redirect:/users/select";
    }
    @GetMapping("/users/select/sign-out")
    public String selectSignOut() {
        FormTracker.setFormId(FormTracker.getSIGN_OUT());
        return "redirect:/users/select";
    }
}