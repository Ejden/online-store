package pl.adrianstypinski.onlinestore;

import lombok.extern.slf4j.Slf4j;
import pl.adrianstypinski.onlinestore.Services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.adrianstypinski.onlinestore.Services.ShoppingService;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;
import pl.adrianstypinski.onlinestore.datamodel.user.User;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class APIController {
    private DataService dataService;
    private ShoppingService shoppingService;

    @Autowired
    public APIController(DataService dataService, ShoppingService shoppingService) {
        this.dataService = dataService;
        this.shoppingService = shoppingService;
    }

    @GetMapping("/all")
    public Iterable<User> findAll() {
        return dataService.getAllUsers();
    }

    @GetMapping("{userId}/products")
    public Iterable<ProductItem.ProductItemDto> getUserOffers(@PathVariable long userId) {
        return dataService.getAllProductItemsBySellerId(userId);
    }

    @PostMapping("{userId}")
    public ProductItem.ProductItemDto addProductItemToSeller(@PathVariable long userId,
                                                                   @RequestBody ProductItem productItem) {
        return dataService.addProductItemToSeller(productItem, userId);
    }

    @PostMapping("{userId}/buy")
    public Basket.BasketDto buy(@PathVariable long userId, @RequestBody Basket basket) {
        return shoppingService.buyItemsFromBasket(userId, basket);
    }

    @PostMapping("users/{userId}/products/{productId}")
    public Iterable<ProductItem.ProductItemDto> addOnStock(@PathVariable long userId, @PathVariable long productId,
                                                           @RequestBody List<ProductItem> productItemsToAdd) {
        return dataService.addToStock(productItemsToAdd);
    }
}
