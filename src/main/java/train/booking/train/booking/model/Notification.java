package train.booking.train.booking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;@Setter
@Getter
@Builder
@ToString
@Entity(name = "notifications")
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

