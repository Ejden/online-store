package pl.adrianstypinski.onlinestore.services;

import datagenerator.Generator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.adrianstypinski.onlinestore.datamodel.basket.Basket;
import pl.adrianstypinski.onlinestore.datamodel.basket.BasketDao;
import pl.adrianstypinski.onlinestore.datamodel.product.*;
import pl.adrianstypinski.onlinestore.datamodel.user.User;
import pl.adrianstypinski.onlinestore.datamodel.user.UserDao;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class DataServiceImpl implements DataService {
    private final UserDao userDao;
    private final ProductItemDao productItemDao;
    private final ProductCartDao productCartDao;
    private final BasketDao basketDao;
    private final ProductCategoryDao productCategoryDao;

    @Autowired
    public DataServiceImpl(UserDao userDao,
                           ProductItemDao productItemDao,
                           ProductCartDao productCartDao,
                           BasketDao basketDao,
                           ProductCategoryDao productCategoryDao) {
        this.userDao = userDao;
        this.productItemDao = productItemDao;
        this.productCartDao = productCartDao;
        this.basketDao = basketDao;
        this.productCategoryDao = productCategoryDao;
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
    public Iterable<ProductItem.ProductItemDto> getAllProductItemsDtoByCategoryId(long categoryId, int page, int size) {
        page = checkPageNumberValidation(page);
        size = checkSizeNumberValidation(size);

        Pageable pageable = PageRequest.of(page, size);

        Iterable<ProductItem> productItems = productItemDao.getAllByProductCategory_CategoryId(categoryId, pageable);
        return StreamSupport.stream(productItems.spliterator(), false)
                .map(ProductItem::toProductItemDto)
                .collect(Collectors.toList());
    }

    private int checkPageNumberValidation(int page) {
        return Math.max(page, 0);
    }

    private int checkSizeNumberValidation(int size) {
        size = Math.max(size, 0);
        size = Math.min(size, 200);

        return size;
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
            // Saving users to database
            Iterable<User> users = Generator.createFakeUsers(1000);
            addUsers(users);

            // Saving productCategories to database
            List<ProductCategory> productCategories = Generator.createProductCategories();
            productCategoryDao.saveAll(productCategories);

            for (int i = 0; i < 10; i++) {
                Iterable<ProductItem> productItems = Generator.createProductItems(users, productCategories);
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

            log.debug("Fake data successfully created.");

        } catch (Exception e) {
            log.error("Error creating fake data... \n Error massage: \n  " + e.getCause());
        }
    }
}
