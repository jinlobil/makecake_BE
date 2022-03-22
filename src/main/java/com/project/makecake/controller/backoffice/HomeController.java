package com.project.makecake.controller.backoffice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {
    @GetMapping("/registerByTable")
    public String registerByTable(){
        return "registerByTable";
    }

    @GetMapping("/admincake")
    public String admincake() {return "admincake";}
}
