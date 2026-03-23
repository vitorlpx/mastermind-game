package com.br.mastermind.api.dto;

import com.br.mastermind.api.enums.MatchDifficulty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartMatchRequestDTO {
  
  private MatchDifficulty difficulty;

}
