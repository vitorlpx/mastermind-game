package com.br.mastermind.api.entity;

import java.time.LocalDateTime;

import com.br.mastermind.api.enums.MatchStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "matches")
@Entity(name = "Match")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Match {
  
  @Id()
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private java.util.UUID secretCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(columnDefinition = "jsonb", nullable = false)
  private String attempts;

  @Enumerated(EnumType.STRING)
  private MatchStatus status;

  private Integer score;
  private String responseExpected;

  private LocalDateTime startedAt;
  private LocalDateTime finishedAt;  
  
  @PrePersist
  public void prePersist() {
    this.startedAt = LocalDateTime.now();
    this.secretCode = java.util.UUID.randomUUID();
    this.status = MatchStatus.IN_PROGRESS;
    this.attempts = "[]";
  }

}
