package com.example.isedolclipbackend.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/test")
    public String viewTest(Model model) {
        model.addAttribute("attr1", 123);
        model.addAttribute("attr2", "한글테스트");

        return "test";
    }
}
