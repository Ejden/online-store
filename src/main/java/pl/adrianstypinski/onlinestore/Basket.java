package pl.adrianstypinski.onlinestore;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCart;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;
import pl.adrianstypinski.onlinestore.datamodel.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Basket {
    private User user;
    private List<ProductCart> productCarts;
    private int toPay;

    public Basket(User user) {
        this.user = user;
        productCarts = new ArrayList<>();
        toPay = 0;
    }

    public Basket () {
        productCarts = new ArrayList<>();
        toPay = 0;
    }

    public List<ProductCart> getProductCarts() {
        return Collections.unmodifiableList(productCarts);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setProductCarts(List<ProductCart> productCarts) {
        this.productCarts = productCarts;
        calculateToPay();
    }

    private void calculateToPay() {
        productCarts.forEach(productCart -> toPay += productCart.getTotalPrice());
    }

    public void addProductToBasket(ProductCart productCartToAdd) {
        Optional<ProductCart> productCart = productCarts.stream()
                .filter(prodCart -> prodCart.getProductItem().getProductId()  == productCartToAdd.getProductItem().getProductId())
                .findFirst();

        if (productCart.isPresent()) {
            productCart.get().addProduct(productCartToAdd.getQuantity());
        } else {
            productCarts.add(productCartToAdd);
        }

        addToPay(productCartToAdd.getProductItem(), productCartToAdd.getQuantity());
    }

    private void addToPay(ProductItem productItem, int quantity) {
        this.toPay += productItem.getPrice() * quantity;
    }

    public int getToPay() {
        return toPay;
    }

    public int getNumberOfProducts() {
        return productCarts.size();
    }

    public BasketDto toBasketDto() {
        List<ProductCart.ProductCartDto> prodCartsDto = productCarts.stream()
                .map(ProductCart::toProductCartDto)
                .collect(Collectors.toList());

        return new BasketDto(user.getUserId(), prodCartsDto, toPay);
    }

    @Data
    @AllArgsConstructor
    public static class BasketDto {
        private long userId;
        private List<ProductCart.ProductCartDto> productCarts;
        private int toPay;

        public BasketDto() {

        }
    }
}
