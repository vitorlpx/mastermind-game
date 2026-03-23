package com.br.mastermind.api.infra.security.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey; 
import io.jsonwebtoken.security.Keys;

import java.util.Date;

@Service
public class JwtUtil {
  
  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private Long expiration;

  private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String generateToken(String name, String email) {
    try {
      String jwt = Jwts.builder()
      .subject(email)
      .claim("name", name)
      .signWith(getSecretKey())
      .expiration(new Date(System.currentTimeMillis() + expiration))
      .compact();
      return jwt;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao gerar token.", e);
    }
  }

  private Claims extractClaims(String token) {
    Claims claims = Jwts.parser()
      .verifyWith(getSecretKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
    return claims;
  }

  public String extractName(String token) {
    return extractClaims(token).get("name", String.class);
  }

  public String extractEmail(String token) {
    return extractClaims(token).getSubject();
  }

  public boolean isTokenValid(String token) {
    try {
      Jwts.parser()
          .verifyWith(getSecretKey())
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (Exception e) {
        return false;
    }
  }

}
