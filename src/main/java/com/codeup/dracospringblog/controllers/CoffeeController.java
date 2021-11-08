package com.codeup.dracospringblog.controllers;

import com.codeup.dracospringblog.models.Coffee;
import com.codeup.dracospringblog.repositories.CoffeeRepository;
import com.codeup.dracospringblog.services.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CoffeeController {
    private final CoffeeRepository coffeeRepository;
    private final EmailService emailService;

    public CoffeeController(CoffeeRepository coffeeRepository, EmailService emailService){
        this.coffeeRepository = coffeeRepository;
        this.emailService = emailService;
    }

    @GetMapping("/coffee")
    public String coffeeInfo(){
        return "coffees/coffee";
    }

    @GetMapping("/coffee/{roast}")
    public String roastSelection(@PathVariable String roast, Model model){
       model.addAttribute("selections", coffeeRepository.findByRoast(roast));
        return "coffees/coffee";
    }

    @PostMapping("/coffee")
    public String newsletterSignup(@RequestParam(name="email") String email, Model model){
        model.addAttribute("email", email);
        emailService.prepareAndSend(email, "Thank you for signing up for our coffee newsletter!");
        return "/coffees/coffee";
    }

    @GetMapping("/coffee/create")
    public String showCreateCoffeeForm(Model model){
        model.addAttribute("coffee", new Coffee());
        return "/coffees/create";
    }

    @PostMapping("/coffee/create")
    public String createCoffee(@ModelAttribute Coffee coffee){
        coffeeRepository.save(coffee);
        return "redirect:/coffee";
    }
}
