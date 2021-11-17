package com.app.requirements.visualization.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResultController {

    @GetMapping("/result")
    public String result(Model model) {
        model.asMap();
        return "result";
    }
}
