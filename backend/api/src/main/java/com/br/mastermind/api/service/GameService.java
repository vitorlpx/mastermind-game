package com.br.mastermind.api.service;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.stereotype.Service;

import com.br.mastermind.api.dto.GuessRequestDTO;
import com.br.mastermind.api.dto.GuessResponseDTO;
import com.br.mastermind.api.dto.MatchResponseDTO;
import com.br.mastermind.api.entity.Match;
import com.br.mastermind.api.entity.User;
import com.br.mastermind.api.enums.MatchStatus;
import com.br.mastermind.api.infra.exception.ResourceNotFoundException;
import com.br.mastermind.api.repository.MatchRepository;
import com.br.mastermind.api.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {

  private static final List<String> COLOR_POOL = List.of(
    "RED", "BLUE", "GREEN", "YELLOW"
  );

  private final UserRepository userRepository;
  private final MatchRepository matchRepository;
  private final ObjectMapper objectMapper;

  public MatchResponseDTO startMatch(String email)  {
    
    // Verificar se o usuário existe
    User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    // Gerar a combinação secreta
    List<String> secret = new ArrayList<>(COLOR_POOL);

    Collections.shuffle(secret);

    // Criar a partida
    Match match = new Match();
  
    try {
      match.setResponseExpected(objectMapper.writeValueAsString(secret.subList(0, 4)));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Erro ao processar a combinação secreta");
    }
    
    match.setAttemptCount(0);
    match.setUser(user);

    matchRepository.save(match);

    return new MatchResponseDTO(match.getSecretCode(), match.getStatus()); 
  }

  @Transactional
  public GuessResponseDTO submitGuess(String matchId, GuessRequestDTO guessRequest) {
    
    Match match = matchRepository.findBySecretCode(UUID.fromString(matchId))
      .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada"));

    User user = match.getUser();

    if (!match.getStatus().equals(MatchStatus.IN_PROGRESS)) {
      throw new RuntimeException("Partida não está em andamento");
    }

    List<String> secretCode;
    try {
      secretCode = objectMapper.readValue(
        match.getResponseExpected(),
          new TypeReference<List<String>>(){}
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Erro ao processar a combinação secreta");
    }

    int hits = 0;
    for (int i = 0; i < secretCode.size(); i++) {
      if (secretCode.get(i).equals(guessRequest.getColors().get(i))) {
        hits++;
      }
    }

    match.setAttemptCount(match.getAttemptCount() + 1);

    try {
      List<List<String>> history = objectMapper.readValue(
        match.getAttempts(),
          new TypeReference<List<List<String>>>(){}
        );
      history.add(guessRequest.getColors());
      match.setAttempts(objectMapper.writeValueAsString(history));
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Erro ao atualizar tentativas");
    }

    if (hits == 4) {
      int remainingAttempts = 10 - match.getAttemptCount();
      int score = remainingAttempts * 100;

      match.setScore(score);
      match.setStatus(MatchStatus.WON);
      match.setFinishedAt(LocalDateTime.now());

      if (score > user.getBestScore()) {
        user.setBestScore(score);
        userRepository.save(user);
      }
    }

    if (match.getAttemptCount() >= 10) {
      match.setStatus(MatchStatus.LOST);
      match.setFinishedAt(LocalDateTime.now());
    }

    matchRepository.save(match);

    return new GuessResponseDTO(hits, match.getScore(), match.getStatus());
  }
}
