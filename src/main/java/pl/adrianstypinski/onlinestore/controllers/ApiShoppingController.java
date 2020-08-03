package pl.adrianstypinski.onlinestore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.adrianstypinski.onlinestore.Services.ShoppingService;
import pl.adrianstypinski.onlinestore.datamodel.basket.Basket;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCart;

@RestController()
@RequestMapping("shopping-services")
public class ApiShoppingController {
    private final ShoppingService shoppingService;

    @Autowired
    public ApiShoppingController(ShoppingService shoppingService) {
        this.shoppingService = shoppingService;
    }

    @GetMapping("user/{userId}/basket")
    public Basket.BasketDto getBasket(@PathVariable long userId) {
        return shoppingService.getBasketByUserId(userId)
                .map(Basket::toBasketDto)
                .orElse(null);
    }

    @PostMapping("user/{userId}/basket/products")
    public Basket.BasketDto addProductToBasket(@PathVariable long userId, @RequestBody ProductCart productCart) {
        return shoppingService.addProductToBasket(userId, productCart)
                .map(Basket::toBasketDto)
                .orElse(getBasket(userId));
    }

    @DeleteMapping("user/{userId}/basket/products")
    public Basket.BasketDto deleteProductFromBasket(@PathVariable long userId, @RequestBody ProductCart productCart) {
        return shoppingService.deleteProductFromBasket(userId, productCart)
                .map(Basket::toBasketDto)
                .orElse(getBasket(userId));
    }
}
