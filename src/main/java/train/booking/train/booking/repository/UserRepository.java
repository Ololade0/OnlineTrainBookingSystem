package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByActivationToken(String activationToken);

    boolean existsByEmail(String email);

    boolean existsByIdNumber(String idNumber);
    @EntityGraph(attributePaths = "roleHashSet")
    Optional<User> findUserByEmail(String email);



}
