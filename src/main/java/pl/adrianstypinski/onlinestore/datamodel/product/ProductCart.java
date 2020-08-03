package pl.adrianstypinski.onlinestore.datamodel.product;

import lombok.Data;
import pl.adrianstypinski.onlinestore.datamodel.basket.Basket;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class ProductCart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID privateId;

    @ManyToOne
    private Basket basket;
    @ManyToOne
    private ProductItem productItem;
    private int quantity;
    private int totalPrice;

    public ProductCart() {
    }

    public ProductCart(ProductItem productItem, int quantity) {
        this.productItem = productItem;
        this.quantity = Math.max(quantity, 0);
        calculateTotalPrice();
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
        calculateTotalPrice();
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }

    public ProductItem getProductItem() {
        return productItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void removeProduct(int quantity) {
        if (quantity > 0) {
            this.quantity = (this.quantity - quantity > 0) ? quantity : 0;
            calculateTotalPrice();
        }
    }

    public void addProduct(int quantity) {
        if (quantity > 0) {
            this.quantity += quantity;
            calculateTotalPrice();
        }
    }

    private void calculateTotalPrice() {
        totalPrice = (quantity > 0) ? quantity * productItem.getPrice() : 0;
    }

    public ProductCartDto toProductCartDto() {
        ProductCartDto productCartDto = new ProductCartDto();

        productCartDto.setProductItemDto(productItem.toProductItemDto());
        productCartDto.setQuantity(quantity);
        productCartDto.setTotalPrice(totalPrice);

        return productCartDto;
    }

    @Data
    public static class ProductCartDto {
        private ProductItem.ProductItemDto productItemDto;
        private int quantity;
        private int totalPrice;
    }
}
