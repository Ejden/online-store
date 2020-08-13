package pl.adrianstypinski.onlinestore.datamodel.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductItemDao extends PagingAndSortingRepository<ProductItem, UUID> {

    @Query("SELECT p FROM ProductItem p INNER JOIN User u ON p.seller.userId = u.userId WHERE u.userId = :userId")
    Iterable<ProductItem> getAllByUserPublicId(@Param("userId") long userId);

    Optional<ProductItem> findByProductId(long productId);

    List<ProductItem> getAllByProductCategory_CategoryId(long productCategoryId, Pageable pageable);
}
