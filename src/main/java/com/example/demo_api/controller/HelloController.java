package com.example.demo_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(){
        return "Hello from Spring Boot on AWS EC2 !";
    }

    @GetMapping("/version")
    public String version(){
        return "v1.0.0";
    }

}