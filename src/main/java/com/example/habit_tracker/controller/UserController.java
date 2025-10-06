package com.example.habit_tracker.controller;

import com.example.habit_tracker.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        userService.register(username, password);
        model.addAttribute("msg", "登録完了しました。ログインしてください。");
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}