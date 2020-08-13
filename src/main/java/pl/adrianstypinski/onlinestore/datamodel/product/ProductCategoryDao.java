package pl.adrianstypinski.onlinestore.datamodel.product;


import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProductCategoryDao extends CrudRepository<ProductCategory, UUID> {
}
