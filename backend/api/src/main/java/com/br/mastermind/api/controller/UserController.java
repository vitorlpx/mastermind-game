package com.br.mastermind.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.mastermind.api.dto.UserProfileDTO;
import com.br.mastermind.api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
  
  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserProfileDTO> getProfile(Authentication authentication) {
    UserProfileDTO profile = userService.getProfile(authentication.getName());
    return ResponseEntity.status(HttpStatus.OK).body(profile);
  }

}
