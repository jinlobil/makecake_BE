package com.project.makecake.controller.backoffice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BOHomeController {
    @GetMapping("/back-office/register-menu-option")
    public String addMenuAndOption() {return "index";}

    @GetMapping("/back-office/cake-img")
    public String getCakeImages() { return "admincake"; }

    @GetMapping("/back-office/order-form")
    public String addOrderFormPage() { return "addOrderFormPage"; }
}
