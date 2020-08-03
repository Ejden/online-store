package pl.adrianstypinski.onlinestore.Services;

import DataGenerator.Generator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.collection.internal.PersistentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.adrianstypinski.onlinestore.datamodel.basket.Basket;
import pl.adrianstypinski.onlinestore.datamodel.basket.BasketDao;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCart;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCartDao;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItemDao;
import pl.adrianstypinski.onlinestore.datamodel.user.User;
import pl.adrianstypinski.onlinestore.datamodel.user.UserAddressDao;
import pl.adrianstypinski.onlinestore.datamodel.user.UserDao;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataServiceImpl implements DataService {
    private final UserDao userDao;
    private final UserAddressDao userAddressDao;
    private final ProductItemDao productItemDao;
    private final ProductCartDao productCartDao;
    private final BasketDao basketDao;

    @Autowired
    public DataServiceImpl(UserDao userDao,
                           UserAddressDao userAddressDao,
                           ProductItemDao productItemDao,
                           ProductCartDao productCartDao,
                           BasketDao basketDao) {
        this.userDao = userDao;
        this.userAddressDao = userAddressDao;
        this.productItemDao = productItemDao;
        this.productCartDao = productCartDao;
        this.basketDao = basketDao;
    }

    // == USERS ==
    @Override
    public Iterable<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public Optional<User> getUserByPrivateId(UUID id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> getUserByUserId(long id) {
        return userDao.findByUserId(id);
    }

    @Override
    public User addUser(User user) {
        return userDao.save(user);
    }

    @Override
    public Iterable<User> addUsers(Iterable<User> users) {
        return userDao.saveAll(users);
    }

    @Override
    public void deleteUser(UUID id) {
        userDao.deleteById(id);
    }

    @Override
    public Iterable<ProductItem.ProductItemDto> getAllProductItemsBySellerId(long id) {
        Iterable<ProductItem> productItems = productItemDao.getAllByUserPublicId(id);
        Set<ProductItem.ProductItemDto> productItemsDto = new HashSet<>();

        productItems.forEach(productItem -> {
            ProductItem.ProductItemDto itemDto = productItem.toProductItemDto();
            productItemsDto.add(itemDto);
        });

        return productItemsDto;
    }

    // == PRODUCT ITEMS ==
    @Override
    public Iterable<ProductItem> getAllProductItems() {
        return productItemDao.findAll();
    }

    @Override
    public Optional<ProductItem> getProductItem(UUID id) {
        return productItemDao.findById(id);
    }

    @Override
    public Optional<ProductItem> getProductItemByProductId(long id) {
        return productItemDao.findByProductId(id);
    }

    @Override
    public Iterable<ProductItem> addProductItems(Iterable<ProductItem> productItems) {
        return productItemDao.saveAll(productItems);
    }

    @Override
    public ProductItem saveProductItem(ProductItem productItem) {
        return productItemDao.save(productItem);
    }

    @Override
    public ProductItem.ProductItemDto addProductItemToSeller(ProductItem productItem, long sellerPublicId) {
        Optional<User> user = getUserByUserId(sellerPublicId);

        ProductItem.ProductItemDto productItemDto = new ProductItem.ProductItemDto();

        if (user.isPresent()) {
            productItem.setSeller(user.get());
            productItem.setProductId(ProductItem.getPublicId());

            productItemDto = productItem.toProductItemDto();

            productItemDao.save(productItem);
        }

        return productItemDto;
    }

    @Override
    public Iterable<ProductItem.ProductItemDto> addToStock(List<ProductItem> productsToAdd) {
        List<ProductItem.ProductItemDto> productItemDtos = new ArrayList<>();
        productsToAdd.forEach(productToAdd -> {
            Optional<ProductItem> productItem = getProductItemByProductId(productToAdd.getProductId());
            productItem.ifPresent(p -> {
                p.addToStock(productToAdd.getOnStock());
                productItemDao.save(p);
                productItemDtos.add(p.toProductItemDto());
            });
        });

        return productItemDtos;
    }

    @Override
    public void deleteProductItem(UUID id) {
        productItemDao.deleteById(id);
    }

    // == BASKET ==
    public Optional<Basket> getBasket(UUID id) {
        return basketDao.findById(id);
    }

    // == OTHER METHODS ==
    @EventListener(ApplicationReadyEvent.class)
    public void fillDB() {
        try {
            Iterable<User> users = Generator.createFakeUsers(1000);
            addUsers(users);

            for (int i = 0; i < 10; i++) {
                Iterable<ProductItem> productItems = Generator.createProductItems(1000, users);
                addProductItems(productItems);

            }

            Basket basket = Generator.createFakeBasket();

            Optional<User> user = userDao.findByUserId(basket.getUser().getUserId());
            List<ProductCart> productCarts = basket.getProductCarts().stream()
                    .map(productCart -> {
                        Optional<ProductItem> productItem = productItemDao
                                .findByProductId(productCart.getProductItem().getProductId());
                        return productItem.map(item -> new ProductCart(item, productCart.getQuantity())).orElse(null);
                    })
                    .collect(Collectors.toList());

            if (user.isPresent()) {
                Basket b = new Basket(user.get());
                b.addProducts(productCarts);
                basketDao.save(b);
                productCartDao.saveAll(b.getProductCarts());
            }

        } catch (Exception e) {
            log.error("Error creating fake data " + e.getCause());
        }
    }
}
