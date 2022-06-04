package com.example.isedolclipbackend.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/after-login")
    public String afterlogin(Model model, String code) {

        model.addAttribute("code", code);

        return "afterlogin";
    }
}
