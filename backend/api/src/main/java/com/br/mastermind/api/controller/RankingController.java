package com.br.mastermind.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.mastermind.api.dto.RankingResponseDTO;
import com.br.mastermind.api.service.RankingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {
  
  private final RankingService rankingService;

  @GetMapping()
  public ResponseEntity<List<RankingResponseDTO>> getRanking() {
    List<RankingResponseDTO> ranking = rankingService.getRanking();
    return ResponseEntity.status(HttpStatus.OK).body(ranking);
  }

}
