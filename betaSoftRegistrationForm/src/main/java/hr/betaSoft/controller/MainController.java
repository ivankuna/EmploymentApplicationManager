package hr.betaSoft.controller;


import hr.betaSoft.security.model.User;
import hr.betaSoft.security.service.UserService;
import hr.betaSoft.security.userdto.UserDto;
import hr.betaSoft.tools.FormTracker;
import jakarta.servlet.http.HttpSession;
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

    @GetMapping("/")
    public String rootForm(Model model) {
        if (userService.countUsers() != 0) {
            return "login";
        }
        return showRegistrationForm(model);
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
    public String authorization(HttpSession session, Model model) {

        if (userService.getAuthenticatedUser().getDateOfUserAccountExpiry().before(new Date(System.currentTimeMillis()))) {
            model.addAttribute("accExpired", true);
            model.addAttribute("message", "Rok vašeg korisničkog računa je istekao!");
            return "login";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        session.setAttribute("formId", FormTracker.getSIGN_UP());

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
    public String showUserMenuForm(Model model) {
        return "employees";
    }

    @GetMapping("/empsignup")
    public String employeeSignUp(HttpSession session) {
        session.setAttribute("formId", FormTracker.getSIGN_UP());
        return "redirect:/employees/show";
    }

    @GetMapping("/empupdate")
    public String employeeUpdate(HttpSession session) {
        session.setAttribute("formId", FormTracker.getUPDATE());
        return "redirect:/employees/show";
    }

    @GetMapping("/empsignout")
    public String employeeSignOut(HttpSession session) {
        session.setAttribute("formId", FormTracker.getSIGN_OUT());
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
    public String selectSignUp(HttpSession session) {
        session.setAttribute("formId", FormTracker.getSIGN_UP());

        return "redirect:/users/select";
    }

    @GetMapping("/users/select/update")
    public String selectUpdate(HttpSession session) {
        session.setAttribute("formId", FormTracker.getUPDATE());
        return "redirect:/users/select";
    }
    @GetMapping("/users/select/sign-out")
    public String selectSignOut(HttpSession session) {
        session.setAttribute("formId", FormTracker.getSIGN_OUT());
        return "redirect:/users/select";
    }
}