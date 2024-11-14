package com.moviedb_api.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainWebController {
    @GetMapping("/")
    public String showHomePage() {
        return "main"; // The main page template
    }

    // @GetMapping("/dashboard")
    // public String showDashboard() {
    // return "dashboard"; // points to "templates/dashboard.html"
    // }

}
