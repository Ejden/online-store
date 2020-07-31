package pl.adrianstypinski.onlinestore.datamodel.user;

import lombok.Data;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
@NaturalIdCache
public class User {
    private static long naturalId = 10000000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID privateId;

    @NaturalId
    @Column(nullable = false, updatable = false, unique = true, length = 8)
    private long userId;
    private String firstName;
    private String secondName;
    private String lastName;
    private String phone;

    @OneToOne(cascade = CascadeType.PERSIST)
    private UserAddress address;
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "seller")
    private List<ProductItem> sellingItems;

    public User () {

    }

    public User(String firstName, String secondName, String lastName, String phone) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public static long getNaturalId() {
        return naturalId++;
    }
}
