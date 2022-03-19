package ru.gb.springbootmarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.gb.springbootmarket.model.Baner;

import java.util.List;

@Repository
public interface BanerRepository extends JpaRepository<Baner, Long> {

    @Query("select b from Baner b where b.imageUrl = :imageUrl")
    List<Baner> findAllByImageUrl(String imageUrl);
}
