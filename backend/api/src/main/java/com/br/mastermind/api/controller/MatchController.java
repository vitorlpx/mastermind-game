package com.br.mastermind.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.mastermind.api.service.UserService;
import com.br.mastermind.api.dto.MatchHistoryDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

  private final UserService userService;

  @GetMapping("/history")
  public ResponseEntity<List<MatchHistoryDTO>> getHistory(Authentication authentication) {
    List<MatchHistoryDTO> history = userService.getMatchHistory(authentication.getName());
    return ResponseEntity.status(HttpStatus.OK).body(history);
  }

}
