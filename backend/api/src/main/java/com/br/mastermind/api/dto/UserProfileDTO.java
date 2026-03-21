package com.br.mastermind.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileDTO {

  private String name;
  private Integer bestScore;

}
