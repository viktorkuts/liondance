package com.liondance.liondance_backend.utils;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class HealthcheckController {
    @GetMapping
    public String isOk(){
        return "I'm alive";
    }
}
