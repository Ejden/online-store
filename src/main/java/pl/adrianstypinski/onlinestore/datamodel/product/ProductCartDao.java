package pl.adrianstypinski.onlinestore.datamodel.product;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductCartDao extends CrudRepository<ProductCart, UUID> {
    @Query("SELECT p FROM ProductCart p INNER JOIN Basket b ON p.basket.privateId = b.privateId WHERE b.user.userId = :userId")
    Iterable<ProductCart> findAllByUserId(long userId);

    Optional<ProductCart> findByBasket_User_UserIdAndProductItem_ProductId(long userId, long productId);

    void removeByProductItem_ProductId(long productId);
}
