package pl.adrianstypinski.onlinestore.datamodel.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDao extends CrudRepository<User, UUID> {
    Optional<User> findByUserId(long id);
}
