package com.br.mastermind.api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // <-- importar o RequestBody do Spring
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.mastermind.api.dto.GuessRequestDTO;
import com.br.mastermind.api.dto.GuessResponseDTO;
import com.br.mastermind.api.dto.MatchResponseDTO;
import com.br.mastermind.api.dto.StartMatchRequestDTO;
import com.br.mastermind.api.service.GameService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

  private final GameService gameService;

  @PostMapping("/start")
  public ResponseEntity<MatchResponseDTO> startMatch(@RequestBody StartMatchRequestDTO request, Authentication authentication) {
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(gameService.startMatch(authentication.getName(), request.getDifficulty()));
  }
  
  @PostMapping("/guess/{matchId}")
  public ResponseEntity<GuessResponseDTO> submitGuess(@RequestBody @Valid GuessRequestDTO guess, @PathVariable String matchId, Authentication authentication) {
    GuessResponseDTO response = gameService.submitGuess(matchId, guess);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
