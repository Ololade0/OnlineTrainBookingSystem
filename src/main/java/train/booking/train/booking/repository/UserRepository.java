package train.booking.train.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.RoleType;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByActivationToken(String activationToken);

    boolean existsByEmail(String email);

    boolean existsByIdNumber(String idNumber);
    @EntityGraph(attributePaths = "roleHashSet")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM users u JOIN u.roleHashSet r WHERE r.roleType = :roleType")
    Page<User> findAllByRoleType(@Param("roleType") RoleType roleType, Pageable pageable);

}
