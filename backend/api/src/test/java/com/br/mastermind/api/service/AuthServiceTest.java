package com.br.mastermind.api.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.br.mastermind.api.dto.AuthResponseDTO;
import com.br.mastermind.api.dto.LoginRequestDTO;
import com.br.mastermind.api.dto.RegisterRequestDTO;
import com.br.mastermind.api.entity.User;
import com.br.mastermind.api.infra.exception.EmailAlreadyExistsException;
import com.br.mastermind.api.infra.exception.InvalidCredentialsException;
import com.br.mastermind.api.infra.security.util.JwtUtil;
import com.br.mastermind.api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private AuthService authService;

  @Test
  void shouldRegisterUserSuccessfully() {
    // Arrange
    RegisterRequestDTO request = new RegisterRequestDTO();
    request.setName("Test User");
    request.setEmail("test@test.com");
    request.setPassword("password123");

    when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mocked-jwt-token");

    // Act
    AuthResponseDTO response = authService.register(request);

    // Assert
    assertNotNull(response);
    assertNotNull(response.getToken());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    // Arrange
    RegisterRequestDTO request = new RegisterRequestDTO();
    request.setName("Carlos");
    request.setEmail("carlos@gmail.com");
    request.setPassword("password123");

    User existingUser = new User();
    existingUser.setEmail("carlos@gmail.com");

    when(userRepository.findByEmail("carlos@gmail.com"))
      .thenReturn(Optional.of(existingUser));

    // Act + Assert
    assertThrows(EmailAlreadyExistsException.class, () -> {
      authService.register(request);
    });

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void shouldLoginUserSuccessfully() {
    // Arrange
    LoginRequestDTO request = new LoginRequestDTO();
    request.setEmail("vitor@gmail.com");
    request.setPassword("password123");

    User existingUser = new User();

    existingUser.setName("Vitor");
    existingUser.setEmail("vitor@gmail.com");
    existingUser.setPassword("encodedPassword");

    when(userRepository.findByEmail("vitor@gmail.com"))
      .thenReturn(Optional.of(existingUser));

    when(passwordEncoder.matches("password123", "encodedPassword"))
      .thenReturn(true);

    when(jwtUtil.generateToken("Vitor", "vitor@gmail.com"))
      .thenReturn("mocked-jwt-token");

    // Act
    AuthResponseDTO response = authService.login(request);

    // Assert
    assertNotNull(response);
    assertNotNull(response.getToken());

    verify(userRepository).findByEmail("vitor@gmail.com");
    verify(passwordEncoder).matches("password123", "encodedPassword");
    verify(jwtUtil).generateToken("Vitor", "vitor@gmail.com");
  }
  
  @Test
  void shouldThrowExceptionWhenLoginWithInvalidCredentials() {
    // Arrange
    LoginRequestDTO request = new LoginRequestDTO();
    request.setEmail("carlos@gmail.com");
    request.setPassword("wrongpassword");

    User existingUser = new User();
    existingUser.setEmail("carlos@gmail.com");
    existingUser.setPassword("encodedPassword");
 
    // Act + Assert
    when(userRepository.findByEmail("carlos@gmail.com"))
      .thenReturn(Optional.of(existingUser));
    when(passwordEncoder.matches("wrongpassword", "encodedPassword"))
      .thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> {
      authService.login(request);
    });
  }

}
