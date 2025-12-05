package com.web.helpforce.domain.user.controller;

import com.web.helpforce.domain.user.dto.*;
// import com.web.helpforce.user.dto.*;
import com.web.helpforce.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto response = authService.login(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDto> logout(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(LogoutResponseDto.success());
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        SignupResponseDto response = authService.signup(requestDto);
        return ResponseEntity.ok(response);
    }
}