//package train.booking.train.booking.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.config.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    @Value("${cors.allowed.origins}")
//    private String [] allowedOrigins;
//
//    public void addCorsMapping(CorsRegistry registry){
//        registry.addMapping("/api/v1/auth/**")
//                .allowedOrigins(allowedOrigins)
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//
//    }
//}
