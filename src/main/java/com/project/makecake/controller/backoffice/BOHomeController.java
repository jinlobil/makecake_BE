package com.project.makecake.controller.backoffice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BOHomeController {
    @GetMapping("/back-office/register-menu-option")
    public String addMenuAndOption() {return "bo_add_menu_and_option";}

    @GetMapping("/back-office/cake-img")
    public String getCakeImages() { return "bo_cake_img"; }

    @GetMapping("/back-office/order-form")
    public String addOrderFormPage() { return "bo_add_order_form"; }
}
