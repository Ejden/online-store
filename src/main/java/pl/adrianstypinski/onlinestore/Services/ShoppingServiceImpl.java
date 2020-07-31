package pl.adrianstypinski.onlinestore.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.adrianstypinski.onlinestore.Basket;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCart;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;
import pl.adrianstypinski.onlinestore.datamodel.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingServiceImpl implements ShoppingService {
    private DataService dataService;

    @Autowired
    public ShoppingServiceImpl(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public Basket.BasketDto buyItemsFromBasket(long userId, Basket basket) {
        Basket.BasketDto boughtDto = null;
        Optional<User> user = dataService.getUserByUserId(userId);

        if (user.isPresent()) {
            User u = user.get();
            basket.setUser(u);
            List<ProductCart> newProductCarts = new ArrayList<>();

            basket.getProductCarts().forEach(productCartFromUser -> {
                Optional<ProductItem> optionalProductItem = dataService
                        .getProductItemByProductId(productCartFromUser.getProductItem().getProductId());

                optionalProductItem.ifPresent(productItem -> {
                    if (productItem.getOnStock() >= productCartFromUser.getQuantity()) {
                        newProductCarts.add(new ProductCart(productItem, productCartFromUser.getQuantity()));
                        productItem.removeFromStock(productCartFromUser.getQuantity());
                        dataService.saveProductItem(productItem);
                    }
                });
            });
            basket.setProductCarts(newProductCarts);

            boughtDto = basket.toBasketDto();
        }

        return boughtDto;
    }
}
