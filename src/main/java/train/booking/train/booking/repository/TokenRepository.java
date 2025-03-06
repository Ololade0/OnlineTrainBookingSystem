package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.model.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByAccessToken(String token);

    Optional<Token> findByRefreshToken(String token);

    @Modifying
    @Transactional
    void deleteByAccessToken(String token);

    @Modifying
    @Transactional
    void deleteByRefreshToken(String token);
}
