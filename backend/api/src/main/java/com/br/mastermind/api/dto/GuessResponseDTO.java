package com.br.mastermind.api.dto;

import com.br.mastermind.api.enums.MatchStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuessResponseDTO {
  
  private int hits;
  private int score;
  private MatchStatus status;

}
