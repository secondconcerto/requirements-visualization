package com.app.requirements.visualization.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "index";
    }

    @GetMapping("/visualize")
    public String visualize() {
        return "visualize";
    }

}
