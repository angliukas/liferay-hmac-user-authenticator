package com.example.hmacauth.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HmacController {

    @GetMapping("/hmac/success")
    public String hmacSuccess() {
        return "redirect:/login";
    }
}
