package train.booking.train.booking.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseCodes;
import train.booking.train.booking.dto.response.ResponseUtil;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        BaseResponse res = ResponseUtil.response(
                ResponseCodes.OPERATION_NOT_PERMITTED,
                "Access denied: You don’t have permission to perform this action",
                null
        );

        response.getWriter().write(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(res));
    }
}
