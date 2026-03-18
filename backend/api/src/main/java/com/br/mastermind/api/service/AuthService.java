package com.br.mastermind.api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.br.mastermind.api.dto.AuthResponseDTO;
import com.br.mastermind.api.dto.LoginRequestDTO;
import com.br.mastermind.api.dto.RegisterRequestDTO;
import com.br.mastermind.api.entity.User;
import com.br.mastermind.api.infra.security.config.JwtUtil;
import com.br.mastermind.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  
  public AuthResponseDTO register(RegisterRequestDTO request) {

    if(userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new RuntimeException("Email já cadastrado");
    }

    User user = new User();

    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    userRepository.save(user);

    String token = jwtUtil.generateToken(request.getName(), request.getEmail());

    return new AuthResponseDTO(token);
  }

  public AuthResponseDTO login(LoginRequestDTO request) {

    User user = userRepository.findByEmail(request.getEmail())
      .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));
        
    boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());

    if(!isMatch) {
      throw new IllegalArgumentException("Credenciais inválidas");
    }

    String token = jwtUtil.generateToken(user.getName(), user.getEmail());
    
    return new AuthResponseDTO(token);
  }

}
