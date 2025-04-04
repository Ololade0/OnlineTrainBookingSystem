package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import train.booking.train.booking.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
}
