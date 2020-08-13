package pl.adrianstypinski.onlinestore.services;

import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;
import pl.adrianstypinski.onlinestore.datamodel.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataService {
    // == USERS ==
    Iterable<User> getAllUsers();

    Optional<User> getUserByPrivateId(UUID id);

    Optional<User> getUserByUserId(long id);

    Iterable<User> addUsers(Iterable<User> users);

    User addUser(User user);

    void deleteUser(UUID id);

    // == PRODUCTS ==
    Iterable<ProductItem.ProductItemDto> getAllProductItemsBySellerId(long id);

    Iterable<ProductItem.ProductItemDto> getAllProductItemsDtoByCategoryId(long categoryId, int page, int size);

    ProductItem saveProductItem(ProductItem productItem);

    Iterable<ProductItem> getAllProductItems();

    Optional<ProductItem> getProductItem(UUID id);

    Optional<ProductItem> getProductItemByProductId(long id);

    Iterable<ProductItem> addProductItems(Iterable<ProductItem> productItems);

    ProductItem.ProductItemDto addProductItemToSeller(ProductItem productItem, long sellerPublicId);

    Iterable<ProductItem.ProductItemDto> addToStock(List<ProductItem> productsToAdd);

    void deleteProductItem(UUID id);

}
