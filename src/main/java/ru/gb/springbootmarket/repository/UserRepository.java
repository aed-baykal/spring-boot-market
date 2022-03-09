package ru.gb.springbootmarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.gb.springbootmarket.model.MarketUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<MarketUser, Long> {

    Optional<MarketUser> findByLogin(String login);

    @Query("FROM MarketUser u JOIN FETCH u.authorities")
    List<MarketUser> findAllFetchAuthority();
}
