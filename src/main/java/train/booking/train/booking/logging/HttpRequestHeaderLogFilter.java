package train.booking.train.booking.logging;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import static train.booking.train.booking.logging.LoggingConstants.*;


@Slf4j
public class HttpRequestHeaderLogFilter extends OncePerRequestFilter {
    private static final String SEPARATOR = " | ";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            MDC.put(X_REQUEST_ID, getRequestId());
            if (StringUtils.isNotBlank(getSessionId(httpServletRequest))) {
                MDC.put(X_SESSION_ID, getSessionId(httpServletRequest));
            }

            MDC.put(X_FORMATTED_HEADERS, formatAllHeaders());
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
            log.warn("Error in doFilter occurred while adding requestId and sessionId");
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    private String formatAllHeaders() {
        String formattedHeaders = "";
        if (MDC.get(X_REQUEST_ID) != null) formattedHeaders += MDC.get(X_REQUEST_ID);
        if (MDC.get(X_SESSION_ID) != null) formattedHeaders += SEPARATOR + MDC.get(X_SESSION_ID);

        return "[" + formattedHeaders + "]";
    }

    private String getSessionId(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(X_SESSION_ID);
    }

    private String getRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
