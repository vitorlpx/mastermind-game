package com.br.mastermind.api.dto;

import com.br.mastermind.api.enums.MatchStatus;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuessResponseDTO {
  
  private List<String> feedback;
  private int score;
  private MatchStatus status;

}
