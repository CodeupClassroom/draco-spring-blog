package com.codeup.dracospringblog.controllers;

import com.codeup.dracospringblog.models.Coffee;
import com.codeup.dracospringblog.repositories.CoffeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CoffeeController {
    private final CoffeeRepository coffeeRepository;

    public CoffeeController(CoffeeRepository coffeeRepository){
        this.coffeeRepository = coffeeRepository;
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
