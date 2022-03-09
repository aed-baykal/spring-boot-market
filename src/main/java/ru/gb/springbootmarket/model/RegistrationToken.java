package ru.gb.springbootmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "registration_tokens")
@Data
@NoArgsConstructor
public class RegistrationToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column(name = "token")
  private String token;

  @Column(name = "expired_at")
  private LocalDateTime expiredAt;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private MarketUser marketUser;

  public RegistrationToken(String token, LocalDateTime expiredAt, MarketUser marketUser) {
    this.token = token;
    this.expiredAt = expiredAt;
    this.marketUser = marketUser;
  }
}
