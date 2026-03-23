package com.br.mastermind.api.service;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.stereotype.Service;

import com.br.mastermind.api.dto.GuessRequestDTO;
import com.br.mastermind.api.dto.GuessResponseDTO;
import com.br.mastermind.api.dto.MatchResponseDTO;
import com.br.mastermind.api.entity.Match;
import com.br.mastermind.api.entity.User;
import com.br.mastermind.api.enums.MatchDifficulty;
import com.br.mastermind.api.enums.MatchStatus;
import com.br.mastermind.api.infra.exception.ResourceNotFoundException;
import com.br.mastermind.api.repository.MatchRepository;
import com.br.mastermind.api.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameService {

  private static final List<String> COLOR_POOL_EASY = List.of(
    "RED", "BLUE", "GREEN", "YELLOW", "ORANGE", "PURPLE"
  );

  private static final List<String> COLOR_POOL_MEDIUM = List.of(
    "RED", "BLUE", "GREEN", "YELLOW", "ORANGE", "PURPLE", "PINK", "CYAN"
  );

  private static final List<String> COLOR_POOL_HARD = List.of(
    "RED", "BLUE", "GREEN", "YELLOW", "ORANGE", "PURPLE", "PINK", "CYAN", "WHITE", "BROWN"
  );

  private final UserRepository userRepository;
  private final MatchRepository matchRepository;
  private final ObjectMapper objectMapper;

  public MatchResponseDTO startMatch(String email, MatchDifficulty difficulty) {

    // Verificar se o usuário existe
    User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    log.info(">>> INFO: Iniciando nova partida para usuário {}: {}", email, user.getName());

    // Gerar a combinação secreta
    List<String> pool = new ArrayList<>(getColorPoolByDifficulty(difficulty));

    // Criar a partida
    Match match = new Match();

    Collections.shuffle(pool);
    log.info(">>> INFO: Pool de cores definidas para combinação secreta: {}", pool);

    try {
      match.setResponseExpected(objectMapper.writeValueAsString(pool.subList(0, 4)));
      log.info(">>> INFO: Combinação secreta gerada para partida: {}", match.getResponseExpected());
    } catch (JsonProcessingException e) {
      log.error(">>> ERROR: Erro ao processar a combinação secreta para partida: {}", e.getMessage());
      throw new RuntimeException("Erro ao processar a combinação secreta.");
    }

    match.setAttemptCount(0);
    match.setUser(user);
    match.setDifficulty(difficulty);

    matchRepository.save(match);
    log.info(">>> INFO: Nova partida criada para usuário {}: {}", email, user.getName());

    return new MatchResponseDTO(match.getSecretCode(), match.getDifficulty(), match.getStatus());
  }

  @Transactional
  public GuessResponseDTO submitGuess(String matchId, GuessRequestDTO guessRequest) {

    Match match = matchRepository.findBySecretCode(UUID.fromString(matchId))
      .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada"));
    log.info(">>> INFO: Partida encontrada pelo ID {}: {}", matchId, match.getId());

    User user = match.getUser();

    if (!match.getStatus().equals(MatchStatus.IN_PROGRESS)) {
      log.error(">>> ERROR: Partida não está em andamento: {}", match.getId());
      throw new RuntimeException("Partida não está em andamento.");
    }

    validateGuessColors(guessRequest, match.getDifficulty());

    List<String> secretCode;
    try {
      secretCode = objectMapper.readValue(
          match.getResponseExpected(),
          new TypeReference<List<String>>() {
          });
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Erro ao processar a combinação secreta.");
    }
    log.info(">>> INFO: Combinação secreta recuperada para partida {}: {}", match.getId(), secretCode);

    match.setAttemptCount(match.getAttemptCount() + 1);

    try {
      List<List<String>> history = objectMapper.readValue(
          match.getAttempts(),
          new TypeReference<List<List<String>>>() {
          });

      history.add(guessRequest.getColors());
      match.setAttempts(objectMapper.writeValueAsString(history));
      log.info(">>> INFO: Tentativas atualizadas para partida {}: {}", match.getId(), match.getAttempts());
    } catch (JsonProcessingException e) {
      log.error(">>> ERROR: Erro ao atualizar tentativas para partida {}: {}", match.getId(), e.getMessage());
      throw new RuntimeException("Erro ao atualizar tentativas.");
    }

    String[] feedback = new String[4];

    List<String> remainingSecret = new ArrayList<>(secretCode);
    List<String> remainingGuess = new ArrayList<>(guessRequest.getColors());

    for (int i = 0; i < secretCode.size(); i++) {
      if (secretCode.get(i).equals(guessRequest.getColors().get(i))) {
        feedback[i] = "hit";
        remainingSecret.set(i, null);
        remainingGuess.set(i, null);
      }
    }

    for (int i = 0; i < 4; i++) {
      if (feedback[i] != null)
        continue;
      String guessColor = remainingGuess.get(i);
      if (guessColor != null && remainingSecret.contains(guessColor)) {
        feedback[i] = "near";
        remainingSecret.set(remainingSecret.indexOf(guessColor), null);
      } else {
        feedback[i] = "empty";
      }
    }

    int hits = (int) Arrays.stream(feedback).filter(f -> "hit".equals(f)).count();

    if (hits == 4) {
      int remainingAttempts = 10 - match.getAttemptCount();
      int score = remainingAttempts * 100;

      match.setScore(score);
      match.setStatus(MatchStatus.WON);
      match.setFinishedAt(LocalDateTime.now());
      log.info(">>> INFO: Partida {} finalizada com vitória: Score: {}, Tentativas restantes: {}", match.getId(), score,
          remainingAttempts);

      if (score > user.getBestScore()) {
        user.setBestScore(score);
        userRepository.save(user);
        log.info(">>> INFO: Novo recorde para usuário {}: {}", user.getEmail(), user.getBestScore());
      }
    }

    if (match.getAttemptCount() >= 10) {
      match.setStatus(MatchStatus.LOST);
      match.setFinishedAt(LocalDateTime.now());
      log.info(">>> INFO: Partida {} finalizada por atingir o número máximo de tentativas", match.getId());
    }

    matchRepository.save(match);
    log.info(">>> INFO: Partida {} atualizada após tentativa: Status: {}, Tentativas: {}", match.getId(),
        match.getStatus(), match.getAttemptCount());

    return new GuessResponseDTO(Arrays.asList(feedback), match.getScore(), match.getStatus());
  }

  private List<String> getColorPoolByDifficulty(MatchDifficulty difficulty) {
    if (difficulty == null) {
      return COLOR_POOL_EASY;
    }

    return switch (difficulty) {
      case EASY -> COLOR_POOL_EASY;
      case MEDIUM -> COLOR_POOL_MEDIUM;
      case HARD -> COLOR_POOL_HARD;
    };
  }

  private void validateGuessColors(GuessRequestDTO guessRequest, MatchDifficulty difficulty) {
    List<String> colors = guessRequest.getColors();

    if (colors == null || colors.size() != 4) {
      throw new RuntimeException("A tentativa deve conter exatamente 4 cores!");
    }

    boolean hasInvalidValue = colors.stream().anyMatch(color -> color == null || color.isBlank());
    if (hasInvalidValue) {
      throw new RuntimeException("Todas as cores da tentativa devem ser preenchidas!");
    }

    List<String> allowedPool = getColorPoolByDifficulty(difficulty);
    boolean hasInvalidColor = colors.stream().anyMatch(color -> !allowedPool.contains(color));

    if (hasInvalidColor) {
      throw new RuntimeException("A tentativa contém cores inválidas para a dificuldade selecionada!");
    }
  }
}
