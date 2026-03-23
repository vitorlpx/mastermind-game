package com.br.mastermind.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.br.mastermind.api.dto.RankingResponseDTO;
import com.br.mastermind.api.entity.User;
import com.br.mastermind.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankingService {
  
  private final UserRepository userRepository;

  public List<RankingResponseDTO> getRanking() {
    List<User> users = userRepository.findAllByOrderByBestScoreDesc();
    log.info(">>> INFO: Recuperando ranking de usuários: {}", users.size());

    List<RankingResponseDTO> ranking = users.stream().map(user -> new RankingResponseDTO(
      user.getName(),
      user.getEmail(),
      user.getBestScore() != null ? user.getBestScore() : 0
    )).collect(Collectors.toList());
    log.info(">>> INFO: Ranking formatado para resposta: {}", ranking.size());

    return ranking;
  }

}
