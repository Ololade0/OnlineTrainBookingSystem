package train.booking.train.booking.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class OpenApiConfig {
    private static final String API_KEY = "AuthenticationToken";
    @Value("${train.swagger.title}")
    private String title;
    @Value("${train.swagger.description}")
    private String description;
    @Value("${server.port}")
    private String port;
    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    public OpenAPI customOpenAPI() {
        String server = switch (profile) {
            case "local" -> String.format("http://localhost:%s/", port);
            default -> "";
        };

        return new OpenAPI()
                .servers(List.of(new Server().url(server)))
                .components(new Components()
                        .addSecuritySchemes(API_KEY, globalAuthorizationTokenKey()))
                .info(new Info()
                        .title(title)
                        .description(description))
                .security(Collections.singletonList(new SecurityRequirement().addList(API_KEY)));
    }

    private SecurityScheme globalAuthorizationTokenKey() {
        return new SecurityScheme()
                .name("Authorization")
                .description("Add Bearer token -- Remember to include \"Bearer\" prefix")
                .in(SecurityScheme.In.HEADER)
                .type(SecurityScheme.Type.APIKEY);
    }
}

