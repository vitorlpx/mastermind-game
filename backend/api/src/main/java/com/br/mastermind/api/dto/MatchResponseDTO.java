package com.br.mastermind.api.dto;

import java.util.UUID;

import com.br.mastermind.api.enums.MatchStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponseDTO {
 
  private UUID matchId;
  private MatchStatus status;

}
