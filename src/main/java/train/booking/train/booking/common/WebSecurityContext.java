package train.booking.train.booking.common;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.User;

import java.util.Optional;

@Component
public class WebSecurityContext {

    public Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    public Optional<User> getPrincipal() {
        if (getAuthentication().isPresent() && getAuthentication().get() instanceof AnonymousAuthenticationToken)
            return Optional.empty();
        return getAuthentication().map(authentication -> (User) authentication.getPrincipal());
    }

    public Optional<User> getUser() {
        return getPrincipal().map(principal -> User.builder()
                .id(principal.getId())
                .email(principal.getUsername())
                .role(principal.getAuthorities()
                        .stream().map(authority -> Role.valueOf(authority.getAuthority()))
                        .findFirst()
                        .orElse(Role.ROLE_USER)
                ).build()
        );
    }

    public User getUserOrElseThrow() {
        return getUser().orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Authentication is null."));
    }

    public Long getAuthenticatedUserId() {
        return getUserOrElseThrow().getId();
    }

    public String getUserForLog() {
        if (getUser().isPresent())
            return String.valueOf(getUser().get().getId());
        return "unknown";
    }

    public Role getUserRole(){
        return getUserOrElseThrow().getRole();
    }

}
