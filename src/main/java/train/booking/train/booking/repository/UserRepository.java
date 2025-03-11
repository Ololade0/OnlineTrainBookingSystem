package train.booking.train.booking.repository;

import jakarta.persistence.criteria.From;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.dto.request.UserDTO;
import train.booking.train.booking.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByActivationToken(String activationToken);

    boolean existsByEmail(String email);

    boolean existsByIdNumber(String idNumber);
//  @Query("SELECT new train.booking.train.booking.dto.request.UserDTO(u.email, u.firstName, u.lastName) FROM User u WHERE u.email = :email")
    Optional<User> findUserByEmail(String email);


}
