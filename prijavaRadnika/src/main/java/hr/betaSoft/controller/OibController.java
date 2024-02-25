package hr.betaSoft.controller;

import hr.betaSoft.model.Employee;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.tools.OibRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class OibController {


    private EmployeeService employeeService;

    @Autowired
    public OibController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/employees/check-oib")
    public String checkOib(@RequestBody OibRequest oibRequest, @ModelAttribute("employee") Employee employee, RedirectAttributes ra) {
        String oib = oibRequest.getOib();
        Employee tempEmployee = employeeService.findByOib(oib);
        String url = "";
        if (tempEmployee != null && tempEmployee.getId() != null) {
            url = "/employees/update/" + tempEmployee.getId();
        }
        return url;
    }

}
