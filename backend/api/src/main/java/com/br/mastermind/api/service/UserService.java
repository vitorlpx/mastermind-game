package com.br.mastermind.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.br.mastermind.api.infra.exception.ResourceNotFoundException;
import com.br.mastermind.api.repository.UserRepository;
import com.br.mastermind.api.repository.MatchRepository;
import com.br.mastermind.api.dto.MatchHistoryDTO;
import com.br.mastermind.api.dto.UserProfileDTO;
import com.br.mastermind.api.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final MatchRepository matchRepository;

  public List<MatchHistoryDTO> getMatchHistory(String email) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));

    return matchRepository.findByUserId(user.getId())
      .stream()
        .map(match -> new MatchHistoryDTO(
          match.getScore(),
          match.getStatus(),
          match.getStartedAt(),
          match.getFinishedAt()))
        .collect(Collectors.toList());
  }

  public UserProfileDTO getProfile(String email) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));

    return new UserProfileDTO(user.getName(), user.getBestScore() != null ? user.getBestScore() : 0);
  }

}
