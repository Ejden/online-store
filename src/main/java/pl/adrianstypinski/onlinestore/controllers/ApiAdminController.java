package pl.adrianstypinski.onlinestore.controllers;

import lombok.extern.slf4j.Slf4j;
import pl.adrianstypinski.onlinestore.datamodel.basket.Basket;
import pl.adrianstypinski.onlinestore.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.adrianstypinski.onlinestore.services.ShoppingService;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;
import pl.adrianstypinski.onlinestore.datamodel.user.User;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("admin/api/v1")
public class ApiAdminController {
    private final DataService dataService;
    private final ShoppingService shoppingService;

    @Autowired
    public ApiAdminController(DataService dataService, ShoppingService shoppingService) {
        this.dataService = dataService;
        this.shoppingService = shoppingService;
    }

    @GetMapping("/users")
    public Iterable<User.UserDto> findAll() {
        Set<User.UserDto> users = new HashSet<>();
        dataService.getAllUsers().forEach(user -> users.add(user.toUserDto()));
        return users;
    }

    @GetMapping("users/{userId}/products")
    public Iterable<ProductItem.ProductItemDto> getUserOffers(@PathVariable long userId) {
        return dataService.getAllProductItemsBySellerId(userId);
    }

    @PostMapping("users/{userId}")
    public ProductItem.ProductItemDto addProductItemToSeller(@PathVariable long userId,
                                                                   @RequestBody ProductItem productItem) {
        return dataService.addProductItemToSeller(productItem, userId);
    }

    @PostMapping("users/products/")
    public Iterable<ProductItem.ProductItemDto> addOnStock(@RequestBody List<ProductItem> productItemsToAdd) {
        return dataService.addToStock(productItemsToAdd);
    }
}
