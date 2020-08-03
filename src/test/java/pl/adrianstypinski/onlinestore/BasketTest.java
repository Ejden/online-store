package pl.adrianstypinski.onlinestore;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.adrianstypinski.onlinestore.datamodel.basket.Basket;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCart;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;
import pl.adrianstypinski.onlinestore.datamodel.user.User;

import java.util.UUID;

public class BasketTest {
    private static Basket basket;

    @BeforeAll
    static void initializeInstances() {
        User user = new User();
        user.setFirstName("Tom");
        user.setLastName("Adams");
        user.setPrivateId(new UUID(439239329L, 28329239832L));
        user.setUserId(39353323L);
        basket = new Basket(user);
    }

    @Test
    void shouldAddOneItemToBasket() {
        ProductItem productItem = new ProductItem();
        productItem.setName("Onions");
        productItem.setPrivateId(new UUID(32489324L, 983498234L));
        productItem.setProductId(9039402930L);

        ProductCart productCart = new ProductCart(productItem, 1);

        basket.addProductToBasket(productCart);

        Assertions.assertEquals(1, basket.getNumberOfProducts());
        Assertions.assertEquals(productItem.getPrice(), basket.getToPay());
    }

    @Test
    void shouldCheckTotalPriceOfBasketWithOneItem() {
        ProductItem productItem = new ProductItem();
        productItem.setName("Onions");
        productItem.setPrivateId(new UUID(32489324L, 983498234L));
        productItem.setProductId(9039402930L);
        productItem.setPrice(3223);

        ProductCart productCart = new ProductCart(productItem, 1);

        basket.addProductToBasket(productCart);

        Assertions.assertEquals(productItem.getPrice(), basket.getToPay());
    }

    @Test
    void shouldCheckTotalPriceOfBasketWithTwoItems() {
        ProductItem productItem2 = new ProductItem();
        productItem2.setName("Cucumber");
        productItem2.setPrivateId(new UUID(32489324L, 983498234L));
        productItem2.setProductId(9039402940L);
        productItem2.setPrice(20);

        ProductCart productCart1 = new ProductCart(productItem2, 2);

        basket.addProductToBasket(productCart1);

        Assertions.assertEquals(3263, basket.getToPay());
    }
}
