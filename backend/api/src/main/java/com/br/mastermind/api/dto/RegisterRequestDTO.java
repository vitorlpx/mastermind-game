package com.br.mastermind.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

  @NotBlank(message = "O nome é obrigatório")
  private String name;

  @Email
  @NotBlank(message = "O email é obrigatório")
  private String email;

  @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere special")
  @NotBlank(message = "A senha é obrigatória")
  private String password;

}
