package com.iunin.cloud.oauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public void hello(){
        System.out.println("hello");
    }

    /*public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String secret = encoder.encode("123456");
        System.out.println(secret);
    }*/
}
