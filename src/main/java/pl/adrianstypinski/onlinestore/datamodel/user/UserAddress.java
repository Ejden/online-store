package pl.adrianstypinski.onlinestore.datamodel.user;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "user_addresses")
@Data
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String country;
    private String postCode;
    private String city;
    private String street;
    private String houseNumber;
    private String flatNumber;

    public UserAddress() {

    }
}
