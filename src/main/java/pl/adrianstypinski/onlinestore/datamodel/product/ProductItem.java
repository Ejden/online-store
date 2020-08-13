package pl.adrianstypinski.onlinestore.datamodel.product;

import lombok.Data;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import pl.adrianstypinski.onlinestore.datamodel.user.User;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "product_items")
@NaturalIdCache
public class ProductItem {
    private static long publicId = 1000000000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID privateId;

    @ManyToOne()
    private User seller;

    @NaturalId
    private long productId;

    @ManyToOne
    private ProductCategory productCategory;

    private String imageSource;

    public ProductItem() {
        onStock = 0;
    }

    private String name;
    private String description;
    private int onStock;
    private int price;

    public static long getPublicId() {
        return publicId++;
    }

    public ProductItemDto toProductItemDto() {
        ProductItemDto productItemDto = new ProductItemDto();

        productItemDto.setProductId(productId);
        productItemDto.setSellerId(seller.getUserId());
        productItemDto.setCategoryName(productCategory.getCategoryName());
        productItemDto.setImageSource(imageSource);
        productItemDto.setName(name);
        productItemDto.setDescription(description);
        productItemDto.setPrice(price);
        productItemDto.setOnStock(onStock);

        return productItemDto;
    }

    public int addToStock(int toAdd) {
        if (toAdd > 0) {
            onStock += toAdd;
        }

        return onStock;
    }

    public int removeFromStock(int toRemove) {
        if (toRemove > 0) {
            if (onStock >= toRemove) {
                onStock -= toRemove;
            }
        }

        return onStock;
    }

    @Data
    public static class ProductItemDto {
        private long productId;
        private long sellerId;
        private String categoryName;
        private String imageSource;
        private String name;
        private String description;
        private int onStock;
        private int price;
    }
}
