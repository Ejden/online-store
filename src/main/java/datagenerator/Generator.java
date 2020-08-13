package datagenerator;

import com.github.javafaker.Faker;
import pl.adrianstypinski.onlinestore.datamodel.basket.Basket;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCart;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCategory;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;
import pl.adrianstypinski.onlinestore.datamodel.user.User;
import pl.adrianstypinski.onlinestore.datamodel.user.UserAddress;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Generator {
    private static final Faker faker = new Faker(Locale.UK);

    public static Iterable<User> createFakeUsers(int quantity) {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            User fakeUser = new User();
            fakeUser.setUserId(User.getNaturalId());
            fakeUser.setFirstName(faker.name().firstName());
            fakeUser.setSecondName(faker.name().firstName());
            fakeUser.setLastName(faker.name().lastName());
            fakeUser.setPhone(faker.number().digits(9));
            UserAddress address = new UserAddress();
            fakeUser.setAddress(address);

            fakeUser.getAddress().setCity(faker.address().cityName());
            fakeUser.getAddress().setCountry(faker.address().country());
            fakeUser.getAddress().setPostCode(faker.address().zipCode());
            fakeUser.getAddress().setHouseNumber(faker.address().streetAddressNumber());
            fakeUser.getAddress().setFlatNumber(faker.address().buildingNumber());
            fakeUser.getAddress().setStreet(faker.address().streetName());

            users.add(fakeUser);
        }

        return users;
    }

    public static List<String> getListOfImgSources() {
        return List.of(
                "https://images.unsplash.com/photo-1518977676601-b53f82aba655?ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80",
                "https://images.unsplash.com/photo-1447175008436-054170c2e979?ixlib=rb-1.2.1&auto=format&fit=crop&w=861&q=80",
                "https://images.unsplash.com/photo-1561155707-3f9e6bb380b7?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=334&q=80",
                "https://images.unsplash.com/photo-1528826007177-f38517ce9a8a?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=358&q=80",
                "https://images.unsplash.com/photo-1506810487030-e7f94a5eef74?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=375&q=80"
        );
    }

    public static List<ProductCategory> createProductCategories() {
        return List.of(
                new ProductCategory(0, "Vegetables And Fruits"),
                new ProductCategory(1, "Dairy"),
                new ProductCategory(2, "Bread"),
                new ProductCategory(3, "Meat"),
                new ProductCategory(4, "Beverages"),
                new ProductCategory(5, "Manufactured Food")
        );
    }

    public static Iterable<ProductItem> createProductItems(Iterable<User> sellers, List<ProductCategory> productCategories) {
        List<ProductItem> items = new ArrayList<>();
        List<String> imgSources = getListOfImgSources();

        AtomicInteger i = new AtomicInteger(0);

        sellers.forEach(seller -> {
            ProductItem productItem = new ProductItem();
            productItem.setProductId(ProductItem.getPublicId());
            productItem.setName(faker.food().ingredient());
            productItem.setDescription(faker.lorem().sentence());
            productItem.setPrice(faker.number().numberBetween(1, 10000));
            productItem.addToStock(faker.number().numberBetween(0, 500));
            productItem.setSeller(seller);
            productItem.setProductCategory(productCategories.get(i.get() % productCategories.size()));
            productItem.setImageSource(imgSources.get(i.get() % imgSources.size()));

            i.incrementAndGet();
            items.add(productItem);
        });

        return items;
    }

    public static Basket createFakeBasket() {
        User user = new User();
        user.setUserId(10000223);

        Basket basket = new Basket();
        basket.setUser(user);

        ProductItem productItem1 = new ProductItem();
        productItem1.setProductId(1000002223);

        ProductItem productItem2 = new ProductItem();
        productItem2.setProductId(1000002224);

        ProductItem productItem3 = new ProductItem();
        productItem3.setProductId(1000002225);

        ProductCart productCart1 = new ProductCart(productItem1, 1);
        ProductCart productCart2 = new ProductCart(productItem2, 2);
        ProductCart productCart3 = new ProductCart(productItem3, 3);

        basket.addProducts(Arrays.asList(productCart1, productCart2, productCart3));

        return basket;
    }
}