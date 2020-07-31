package pl.adrianstypinski.onlinestore.Services;

import pl.adrianstypinski.onlinestore.Basket;

public interface ShoppingService {
    Basket.BasketDto buyItemsFromBasket(long userId, Basket basket);
}
