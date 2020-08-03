package pl.adrianstypinski.onlinestore.datamodel.basket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductCart;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;
import pl.adrianstypinski.onlinestore.datamodel.user.User;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID privateId;

    @NaturalId
    private long basketId;

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private User user;
    @OneToMany(mappedBy = "basket", fetch = FetchType.EAGER)
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
        productCarts.forEach(productCart -> productCart.setBasket(this));
        calculateToPay();
    }

    public void addProducts(List<ProductCart> productCarts) {
        this.productCarts.addAll(productCarts);
        productCarts.forEach(productCart -> productCart.setBasket(this));
        calculateToPay();
    }

    public void calculateToPay() {
        toPay = 0;
        productCarts.forEach(productCart -> toPay += productCart.getTotalPrice());
    }

    public void addProductToBasket(ProductCart productCartToAdd) {
        Optional<ProductCart> productCart = productCarts.stream()
                .filter(prodCart -> prodCart.getProductItem().getProductId()  == productCartToAdd.getProductItem().getProductId())
                .findFirst();

        productCartToAdd.setBasket(this);

        if (productCart.isPresent()) {
            productCart.get().addProduct(productCartToAdd.getQuantity());
        } else {
            productCarts.add(productCartToAdd);
        }

        addToPay(productCartToAdd.getProductItem(), productCartToAdd.getQuantity());
    }

    @SuppressWarnings("unused")
    public void removeProductFromBasket(ProductCart productCart) {
        this.productCarts.removeIf(p -> p.getProductItem().getPrivateId().equals(productCart.getPrivateId()));
        calculateToPay();
    }

    public void removeProductFromBasket(long productId) {
        this.productCarts.removeIf(p -> p.getProductItem().getProductId() == productId);
        calculateToPay();
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

    @SuppressWarnings("unused")
    public boolean isEmpty() {
        return productCarts.isEmpty();
    }

    public BasketDto toBasketDto() {
        List<ProductCart.ProductCartDto> prodCartsDto = productCarts.stream()
                .map(ProductCart::toProductCartDto)
                .collect(Collectors.toList());

        return new BasketDto(user.getUserId(), prodCartsDto, toPay);
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class BasketDto {
        private long userId;
        private List<ProductCart.ProductCartDto> productCarts;
        private int toPay;
    }
}
