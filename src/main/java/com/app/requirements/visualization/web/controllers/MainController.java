package com.app.requirements.visualization.web.controllers;

import com.app.requirements.visualization.web.dto.UserStoryFormDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String visualize(Model model) {
        model.addAttribute("story", new UserStoryFormDto());
        return "visualize";
    }

    @GetMapping("/result")
    public String result() {
        return "result";
    }

}
