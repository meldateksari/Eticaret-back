package com.meldateksari.eticaret.auth.controller;

import com.meldateksari.eticaret.auth.dto.AuthResponse;
import com.meldateksari.eticaret.auth.dto.LoginRequestDto;
import com.meldateksari.eticaret.auth.dto.RegisterRequestDto;
import com.meldateksari.eticaret.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequestDto dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequestDto dto) {
        return userService.login(dto);
    }
}
