package com.project.makecake.controller.backoffice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/api/back-office/register-menu-option")
    public String registerMenuOption() {return "index";}

    @GetMapping("/api/back-office/register-by-table")
    public String registerByTable(){
        return "registerByTable";
    }

    @GetMapping("/api/admincake")
    public String admincake() { return "admincake"; }

    @GetMapping("/api/back-office/add-order-form")
    public String addOrderFormPage() { return "addOrderFormPage"; }
}
