package hr.betaSoft.controller;

import hr.betaSoft.model.Employee;
import hr.betaSoft.security.service.UserService;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.tools.FormTracker;
import hr.betaSoft.tools.OibRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class OibController {

    private EmployeeService employeeService;

    private UserService userService;

    @Autowired
    public OibController(EmployeeService employeeService, UserService userService) {
        this.employeeService = employeeService;
        this.userService = userService;
    }

    @PostMapping("/employees/check-oib")
    public String checkOib(@RequestBody OibRequest oibRequest) {

        String oib = oibRequest.getOib();
        Employee tempEmployee = employeeService.findFirstByOibAndUser(oib, userService.getAuthenticatedUser());
        String url = "";
        if (tempEmployee != null && tempEmployee.getId() != null) {
            if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
                if (tempEmployee.isFromUpdate()) {
                    url = "/employees/new-update/" + oib;
                } else {
                    url = "/employees/update/" + tempEmployee.getId();
                }
            } else {
                url = "/employees/update/" + tempEmployee.getId();
            }
        }
        return url;
    }
}