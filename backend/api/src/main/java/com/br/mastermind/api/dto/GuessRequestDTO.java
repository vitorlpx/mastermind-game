package com.br.mastermind.api.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuessRequestDTO {
  @NotNull(message = "A lista de cores é obrigatória")
  @Size(min = 4, max = 4, message = "A tentativa deve conter exatamente 4 cores")
  private List<@NotBlank(message = "Cada cor da tentativa deve ser preenchida") String> colors;

}
