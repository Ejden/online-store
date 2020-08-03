package pl.adrianstypinski.onlinestore.datamodel.basket;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface BasketDao extends CrudRepository<Basket, UUID> {
    Optional<Basket> findByUser_UserId(long userId);
}
