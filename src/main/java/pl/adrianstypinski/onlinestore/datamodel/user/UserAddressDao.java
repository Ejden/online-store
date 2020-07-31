package pl.adrianstypinski.onlinestore.datamodel.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserAddressDao extends CrudRepository<UserAddress, UUID> {
}
