package pl.adrianstypinski.onlinestore.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.adrianstypinski.onlinestore.datamodel.basket.Basket;
import pl.adrianstypinski.onlinestore.datamodel.basket.BasketDao;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCart;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCartDao;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItemDao;
import pl.adrianstypinski.onlinestore.datamodel.user.User;
import pl.adrianstypinski.onlinestore.datamodel.user.UserDao;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ShoppingServiceImpl implements ShoppingService {
    private final DataService dataService;
    private final BasketDao basketDao;
    private final ProductItemDao productItemDao;
    private final ProductCartDao productCartDao;
    private final UserDao userDao;

    @Autowired
    public ShoppingServiceImpl(DataService dataService,
                               BasketDao basketDao,
                               UserDao userDao,
                               ProductItemDao productItemDao,
                               ProductCartDao productCartDao) {
        this.productItemDao = productItemDao;
        this.productCartDao = productCartDao;
        this.userDao = userDao;
        this.basketDao = basketDao;
        this.dataService = dataService;
    }

    @Override
    public Basket.BasketDto buyItemsFromBasket(Basket basket) {
        Basket.BasketDto boughtDto = null;
        Optional<User> user = dataService.getUserByUserId(basket.getUser().getUserId());

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

    @Override
    public Optional<Basket> getBasketByUserId(long userId) {
        return basketDao.findByUser_UserId(userId);
    }

    @Override
    public Optional<Basket> addProductToBasket(long userId, ProductCart productCart) {
        // Searching if user exists
        Optional<User> user = userDao.findByUserId(userId);
        Optional<Basket> basket = basketDao.findByUser_UserId(userId);

        // If user exist than we're doing the rest
        if (user.isPresent()) {

            // If user don't have a basket we're creating new one
            if (basket.isEmpty()) {
                basket = Optional.of(new Basket());
            }

            // Getting productItem from database
            Optional<ProductItem> productItem = productItemDao
                    .findByProductId(productCart.getProductItem().getProductId());

            // If product item exists in database
            if (productItem.isPresent()) {
                // Creating new productCart to use data from database instead of data from user
                productCart = new ProductCart(productItem.get(), productCart.getQuantity());

                // Checking if item is already in basket
                Optional<ProductCart> duplicate = basket.get().getProductCarts()
                        .stream()
                        .filter(p -> p.getProductItem().getPrivateId().equals(productItem.get().getPrivateId()))
                        .findFirst();

                if (duplicate.isPresent()) {
                    // Item already exist in basket
                    duplicate.get().updateQuantity(productCart.getQuantity());
                    basket.get().calculateToPay();
                } else {
                    // Item doesn't exists in basket
                    basket.get().addProductToBasket(productCart);
                }

                // Finally save changes to database
                productCartDao.save(productCart);
                basketDao.save(basket.get());
            }
        }

        return basket;
    }

    @Override
    public Optional<Basket> deleteProductFromBasket(long userId, ProductCart productCartFromUser) {
        // Getting basket from database
        Optional<Basket> basket = basketDao.findByUser_UserId(userId);

        basket.ifPresent(b -> {
            b.removeProductFromBasket(productCartFromUser.getProductItem().getProductId());

            productCartDao
                    .removeByProductItem_ProductIdAndBasket_PrivateId(
                            productCartFromUser.getProductItem().getProductId(),
                            basket.get().getPrivateId());

            basketDao.save(basket.get());
        });

        return basket;
    }

    @Override
    public Optional<Basket> updateProductInBasket(long userId, ProductCart productCartFromUser) {
        Optional<User> user = userDao.findByUserId(userId);
        Optional<Basket> basket = basketDao.findByUser_UserId(userId);

        basket.ifPresent(value -> value.getProductCarts().stream()
                .filter(p -> p.getProductItem().getProductId() == productCartFromUser.getProductItem().getProductId())
                .findFirst().ifPresent(p -> {
                    if (productCartFromUser.getQuantity() > 0) {
                        p.updateQuantity(productCartFromUser.getQuantity());
                    } else {
                        value.removeProductFromBasket(p);
                        productCartDao
                                .removeByProductItem_ProductIdAndBasket_PrivateId(
                                        productCartFromUser.getProductItem().getProductId(),
                                        basket.get().getPrivateId()
                                );
                    }

                    basket.get().calculateToPay();

                    basketDao.save(basket.get());
                })
        );

        return basket;
    }
}
