package train.booking.train.booking.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import train.booking.train.booking.model.User;
import train.booking.train.booking.service.UserService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String[] allowedEndpoints = {"/api/v1/auth"};

    private final AuthJwtService jwtService;
    private final UserService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (isAllowedEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            handleJwtException(e, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User userDetails = this.userDetailsService.loadByUsername(userEmail);

            try {

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                handleJwtException(e, response);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAllowedEndpoint(HttpServletRequest request) {
        for (String endpoint : allowedEndpoints) {
            if (request.getRequestURI().contains(endpoint)) {
                return true;
            }
        }
        return false;
    }

    private void returnErrorMessageWhenJWTEmpty(HttpServletResponse response) throws IOException {
        response.setStatus(response.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Authorization token is missing. Please include a valid authorization token in the request header\" }");
    }

    private void returnErrorMessage(HttpServletResponse response) throws IOException {
        response.setStatus(response.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{ \"message\":\"Proceed to change password\",\"firstTimeLoggedIn\":true }");
    }

    private void handleJwtException(Exception exception, HttpServletResponse response) throws IOException {
        log.error("JWT exception occurred");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String errorMessage = switch (exception.getClass().getSimpleName()) {
            case "UnsupportedJwtException" ->
                    "Jwt format does not match the expected format expected by the application";
            case "ExpiredJwtException" -> "JWT token has expired";
            case "MalformedJwtException" -> "JWT token is malformed";
            case "SignatureException" -> "JWT token signature is invalid";
            default -> "Invalid JWT token";
        };
        response.getWriter().write(String.format("{ \"error\":  \"%s\" }", errorMessage));
    }
}

