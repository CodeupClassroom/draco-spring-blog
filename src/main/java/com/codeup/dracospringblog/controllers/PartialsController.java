package com.codeup.dracospringblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PartialsController {

    @GetMapping("/partials")
    public String showPartials(){
        return "views-lec/partials-test";
    }
}
