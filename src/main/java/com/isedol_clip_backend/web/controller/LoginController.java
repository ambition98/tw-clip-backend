package com.isedol_clip_backend.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class LoginController {

    @GetMapping("/after-login")
    public String afterlogin(Model model, String code) {

        model.addAttribute("code", code);

        return "afterlogin";
    }
}
