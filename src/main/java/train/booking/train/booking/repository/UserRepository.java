package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.RoleType;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByActivationToken(String activationToken);

    boolean existsByEmail(String email);

    boolean existsByIdNumber(String idNumber);
    @EntityGraph(attributePaths = "roleHashSet")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM users u JOIN u.roleHashSet r WHERE r.roleType = :roleType")
    Page<User> findAllByRoleType(@Param("roleType") RoleType roleType, Pageable pageable);
//    @Query("SELECT DISTINCT u FROM User u JOIN u.roleHashSet r WHERE r.roleType <>'USER_ROLE'")
//    Page<User>findAllwithAtLeastOneNonUserRole(Pageable pageable);
@Query("SELECT DISTINCT u FROM users u JOIN u.roleHashSet r WHERE r.roleType <> 'USER_ROLE'")
Page<User> findAllWithAtLeastOneNonUserRole(Pageable pageable);

    @Query("SELECT DISTINCT u FROM users u JOIN u.roleHashSet r " +
            "WHERE r.roleType <> 'USER_ROLE' AND (" +
            "LOWER(u.firstName) LIKE %:query% OR " +
            "LOWER(u.lastName) LIKE %:query% OR " +
            "LOWER(u.email) LIKE %:query% OR " +
            "LOWER(u.idNumber) LIKE %:query%)")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);

}
