package train.booking.train.booking.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseCodes;
import train.booking.train.booking.dto.response.ResponseUtil;

import java.io.IOException;

@Component
public class UnAuthorizedEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException) throws IOException, IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        BaseResponse res = ResponseUtil.response(
                ResponseCodes.OPERATION_NOT_PERMITTED,
                "Unauthorized: Please login first",
                null
        );

        response.getWriter().write(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(res));
    }
}
