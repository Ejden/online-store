package datagenerator;

import com.github.javafaker.Faker;
import pl.adrianstypinski.onlinestore.datamodel.basket.Basket;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCart;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;
import pl.adrianstypinski.onlinestore.datamodel.user.User;
import pl.adrianstypinski.onlinestore.datamodel.user.UserAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

    public static Iterable<ProductItem> createProductItems(Iterable<User> sellers) {
        List<ProductItem> items = new ArrayList<>();

        sellers.forEach(seller -> {
            ProductItem productItem = new ProductItem();
            productItem.setProductId(ProductItem.getPublicId());
            productItem.setName(faker.food().ingredient());
            productItem.setDescription(faker.lorem().sentence());
            productItem.setPrice(faker.number().numberBetween(1, 10000));
            productItem.addToStock(faker.number().numberBetween(0, 500));
            productItem.setSeller(seller);

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