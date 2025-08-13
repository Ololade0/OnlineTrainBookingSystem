package train.booking.train.booking.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Set status code
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("{\"error\": \"Access Denied\"}");
        out.flush();
    }
}
