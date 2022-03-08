package ru.gb.springbootmarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gb.springbootmarket.model.RegistrationToken;
import ru.gb.springbootmarket.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken, Long> {

  @Query("SELECT rt.user FROM RegistrationToken rt WHERE rt.expiredAt > :time AND rt.token = :token")
  Optional<User> findUserByToken(@Param("time") LocalDateTime time, @Param("token") String token);

  RegistrationToken findRegistrationTokenByToken(String token);

  void delete(RegistrationToken registrationToken);
}
