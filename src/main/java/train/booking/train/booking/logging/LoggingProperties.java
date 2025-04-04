package train.booking.train.booking.logging;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("train.logging")
@Data
public class LoggingProperties {
    private boolean enabled;
    private List<String> excludedPaths;
}
