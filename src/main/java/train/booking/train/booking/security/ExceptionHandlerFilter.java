package train.booking.train.booking.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;
import train.booking.train.booking.exceptions.APIError;

import java.io.IOException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (JwtException exception) {
            logError(request, exception);
            setErrorResponse(HttpStatus.BAD_REQUEST, response, exception);
        } catch (InsufficientAuthenticationException exception) {
            logError(request, exception);
            setErrorResponse(HttpStatus.FORBIDDEN, response, new RuntimeException("Access Denied: Authentication Required."));
        } catch (RuntimeException exception) {
            logError(request, exception);
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, exception);
        }
    }

    private void logError(HttpServletRequest request, Throwable exception) {
        // Log the request URI and headers
        String requestUri = request.getRequestURI();
        StringBuilder headers = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(headerName ->
                headers.append(headerName).append(": ").append(request.getHeader(headerName)).append(", ")
        );

        // Log the error
        System.err.printf("Error occurred while processing request to %s: %s%nHeaders: [%s]%n",
                requestUri, exception.getMessage(), headers.toString());
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable exception) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        APIError apiError = new APIError(status, exception);
        try {
            String jsonOutput = apiError.convertToJson();
            response.getWriter().write(jsonOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}