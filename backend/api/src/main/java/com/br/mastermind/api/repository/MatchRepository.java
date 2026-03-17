package com.br.mastermind.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.mastermind.api.entity.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

  List<Match> findByUserId(Long userId);

}
