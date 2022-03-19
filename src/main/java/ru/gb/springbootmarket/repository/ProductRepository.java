package ru.gb.springbootmarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.gb.springbootmarket.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p left join fetch p.category")
    List<Product> findAll();

    @Query("select p from Product p where p.imageUrl = :imageUrl")
    List<Product> findAllByImageUrl(String imageUrl);

    Optional<Product> findByTitle(String title);
}
