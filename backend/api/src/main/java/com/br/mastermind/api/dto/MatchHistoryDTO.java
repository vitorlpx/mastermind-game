package com.br.mastermind.api.dto;

import java.time.LocalDateTime;

import com.br.mastermind.api.enums.MatchStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchHistoryDTO {

  private Integer score;
  private MatchStatus status;
  private LocalDateTime startedAt;
  private LocalDateTime finishedAt;

}