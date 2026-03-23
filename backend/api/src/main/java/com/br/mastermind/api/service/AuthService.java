package com.br.mastermind.api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.br.mastermind.api.dto.AuthResponseDTO;
import com.br.mastermind.api.dto.LoginRequestDTO;
import com.br.mastermind.api.dto.RegisterRequestDTO;
import com.br.mastermind.api.entity.User;
import com.br.mastermind.api.infra.exception.EmailAlreadyExistsException;
import com.br.mastermind.api.infra.exception.InvalidCredentialsException;
import com.br.mastermind.api.infra.security.util.JwtUtil;
import com.br.mastermind.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  
  public AuthResponseDTO register(RegisterRequestDTO request) {

    if(userRepository.findByEmail(request.getEmail()).isPresent()) {
      log.error(">>> ERROR: Tentativa de registro com email já existente: {}", request.getEmail());
      throw new EmailAlreadyExistsException("E-mail já cadastrado.");
    }

    User user = new User();

    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    userRepository.save(user);
    log.info(">>> INFO: Novo usuário registrado: {}", request.getEmail());

    String token = jwtUtil.generateToken(request.getName(), request.getEmail());
    log.info(">>> INFO: Token gerado para usuário {}", request.getEmail());

    return new AuthResponseDTO(token);
  }

  public AuthResponseDTO login(LoginRequestDTO request) {

    User user = userRepository.findByEmail(request.getEmail())
    .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas."));
    log.info(">>> INFO: Tentativa de login para usuário {}: {}", request.getEmail(), user.getName());
        
    boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
    log.info(">>> INFO: Senha fornecida para usuário {}: {}", request.getEmail(), user.getName());

    if(!isMatch) {
      log.error(">>> ERROR: Senha incorreta para usuário {}: {}", request.getEmail(), user.getName());
      throw new InvalidCredentialsException("Credenciais inválidas.");
    }

    String token = jwtUtil.generateToken(user.getName(), user.getEmail());
    log.info(">>> INFO: Token gerado para usuário {}", request.getEmail());
    
    return new AuthResponseDTO(token);
  }

}
