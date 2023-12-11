package com.example.springwebsockets.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    @GetMapping("/")
    public String showChatPage() {
        return "index";
    }

    @GetMapping("/chat")
    public String showChatPageWithUsername(Model model) {
        String username = (String) model.getAttribute("savedUsername");
        model.addAttribute("savedUsername", username);
        return "chat";
    }
}


