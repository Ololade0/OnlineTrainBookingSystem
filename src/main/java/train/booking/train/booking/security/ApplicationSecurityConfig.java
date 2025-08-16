package train.booking.train.booking.security;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import train.booking.train.booking.security.jwt.JwtAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig {

    private final UnAuthorizedEntryPoint unAuthorizedEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${spring.profiles.active:dev}")
    private String appEnv;


    public ApplicationSecurityConfig(UnAuthorizedEntryPoint unAuthorizedEntryPoint,
                                     JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.unAuthorizedEntryPoint = unAuthorizedEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/api/v1/auth/schedule/schedule-route",
                            "/",
                            "/api/v1/auth/login",
                            "/api/v1/auth/admin/register-superadmin",
                            "/api/v1/auth/user/register",
                            "/api/v1/auth/notification/activate",
                            "/api/v1/auth/role/create-role"
                    ).permitAll();
                    if ("dev".equalsIgnoreCase(appEnv)) {
                        auth.requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**"
                        ).permitAll();
                    } else if ("staging".equalsIgnoreCase(appEnv) || "prod".equalsIgnoreCase(appEnv)) {
                        // Staging & Prod → Swagger restricted to ADMIN_ROLE & SUPERADMIN_ROLE
                        auth.requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**"
                        ).hasAnyAuthority("ADMIN_ROLE", "SUPERADMIN_ROLE");
                    }

                    // ✅ Everything else requires authentication
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unAuthorizedEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // ✅ Add JWT and Exception filters
        http.addFilterBefore(jwtAuthenticationFilterBean(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(exceptionHandlerFilterBean(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilterBean() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public ExceptionHandlerFilter exceptionHandlerFilterBean() {
        return new ExceptionHandlerFilter();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
