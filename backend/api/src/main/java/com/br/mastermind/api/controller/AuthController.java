package com.br.mastermind.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // <-- importar o RequestBody do Spring
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.mastermind.api.dto.AuthResponseDTO;
import com.br.mastermind.api.dto.LoginRequestDTO;
import com.br.mastermind.api.dto.RegisterRequestDTO;
import com.br.mastermind.api.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
  
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid RegisterRequestDTO request) {
    authService.register(request);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
    return ResponseEntity.ok(authService.login(request));
  }

}
