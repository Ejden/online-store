package pl.adrianstypinski.onlinestore.Services;

import pl.adrianstypinski.onlinestore.datamodel.basket.Basket;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCart;

import java.util.Optional;

public interface ShoppingService {
    Basket.BasketDto buyItemsFromBasket(Basket basket);

    Optional<Basket> getBasketByUserId(long userId);

    Optional<Basket> addProductToBasket(long userId, ProductCart productCart);

    Optional<Basket> deleteProductFromBasket(long userId, ProductCart productCart);
}
