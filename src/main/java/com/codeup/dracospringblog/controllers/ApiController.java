package com.codeup.dracospringblog.controllers;

import com.codeup.dracospringblog.services.ParksApiService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiController {

    private final ParksApiService parksApiService;

    public ApiController(ParksApiService parksApiService) {
        this.parksApiService = parksApiService;
    }

    @GetMapping("/api-test")
    @ResponseBody
    public String printAPIInfo() {
        return parksApiService.fetchData();
    }

}
