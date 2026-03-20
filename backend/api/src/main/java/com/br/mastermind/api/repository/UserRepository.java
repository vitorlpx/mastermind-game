package com.br.mastermind.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.mastermind.api.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  
  Optional<User> findByEmail(String email);

  List<User> findAllByOrderByBestScoreDesc();

}
