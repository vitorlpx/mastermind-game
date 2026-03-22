package com.br.mastermind.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.mockito.junit.jupiter.MockitoExtension;

import com.br.mastermind.api.dto.GuessRequestDTO;
import com.br.mastermind.api.dto.GuessResponseDTO;
import com.br.mastermind.api.dto.MatchResponseDTO;
import com.br.mastermind.api.entity.Match;
import com.br.mastermind.api.entity.User;
import com.br.mastermind.api.enums.MatchStatus;
import com.br.mastermind.api.infra.exception.ResourceNotFoundException;
import com.br.mastermind.api.repository.MatchRepository;
import com.br.mastermind.api.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  @Mock
  private MatchRepository matchRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private GameService gameService;

  @Test
  void shouldStartMatchSuccessfully() throws Exception {
    // Arrange
    User user = new User();

    user.setName("Vitor");
    user.setEmail("vitor@gmail.com");
    user.setBestScore(0);

    when(userRepository.findByEmail("vitor@gmail.com"))
        .thenReturn(Optional.of(user));

    when(objectMapper.writeValueAsString(any()))
        .thenReturn("[\"RED\",\"BLUE\",\"GREEN\",\"YELLOW\"]");

    when(matchRepository.save(any(Match.class)))
        .thenAnswer(invocation -> {
          Match m = invocation.getArgument(0);
          if (m.getStatus() == null)
            m.setStatus(MatchStatus.IN_PROGRESS);
          return m;
        });

    // Act
    MatchResponseDTO response = gameService.startMatch("vitor@gmail.com");

    // Assert
    assertNotNull(response);
    assertEquals(MatchStatus.IN_PROGRESS, response.getStatus());
    verify(matchRepository).save(any(Match.class));
  }

  @Test
  void shouldThrowExceptionWhenMatchNotFound() {
    // Arrange
    when(matchRepository.findBySecretCode(any(UUID.class)))
        .thenReturn(Optional.empty());

    // Act + Assert
    assertThrows(ResourceNotFoundException.class, () -> {
      gameService.submitGuess(
          UUID.randomUUID().toString(),
          new GuessRequestDTO());
    });
  }

  @Test
  void shouldMarkMatchAsWonWhenAllHitsCorrect() throws JsonMappingException, JsonProcessingException {
    // Arrange
    User user = new User();
    user.setName("Vitor");
    user.setEmail("vitor@gmail.com");
    user.setBestScore(0);

    Match match = new Match();
    match.setUser(user);
    match.setStatus(MatchStatus.IN_PROGRESS);
    match.setAttemptCount(2);
    match.setScore(0);
    match.setResponseExpected("[\"RED\",\"BLUE\",\"GREEN\",\"YELLOW\"]");
    match.setAttempts("[]");

    when(matchRepository.findBySecretCode(any(UUID.class)))
        .thenReturn(Optional.of(match));

    when(objectMapper.readValue(
        eq("[\"RED\",\"BLUE\",\"GREEN\",\"YELLOW\"]"),
        any(TypeReference.class)))
        .thenReturn(List.of("RED", "BLUE", "GREEN", "YELLOW"));

    when(objectMapper.readValue(eq("[]"), any(TypeReference.class)))
        .thenReturn(new ArrayList<>());

    when(objectMapper.writeValueAsString(any()))
        .thenReturn("[[\"RED\",\"BLUE\",\"GREEN\",\"YELLOW\"]]");

    when(matchRepository.save(any(Match.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    GuessRequestDTO guess = new GuessRequestDTO();
    guess.setColors(List.of("RED", "BLUE", "GREEN", "YELLOW"));

    GuessResponseDTO response = gameService.submitGuess(
        UUID.randomUUID().toString(), guess);

    // Assert
    assertNotNull(response);
    assertEquals(MatchStatus.WON, response.getStatus());

    assertEquals(700, response.getScore());

    verify(matchRepository, times(1)).save(any(Match.class));
    verify(userRepository, times(1)).save(any(User.class));
  }

}
