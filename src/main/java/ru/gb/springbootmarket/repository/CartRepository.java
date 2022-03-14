package ru.gb.springbootmarket.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gb.springbootmarket.dto.Cart;

@Repository
public interface CartRepository extends CrudRepository<Cart, String> {
}
