package com.example.ex2_gestioncompte.controller;

import com.example.ex2_gestioncompte.DTO.RegisterDto;
import com.example.ex2_gestioncompte.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        userService.registerUser(registerDto);
        return ResponseEntity.ok("Utilisateur enregistré avec succès");
    }
}

