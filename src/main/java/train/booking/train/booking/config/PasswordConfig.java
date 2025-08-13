package train.booking.train.booking.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import org.springframework.context.event.ContextRefreshedEvent;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PasswordConfig implements ApplicationListener<ContextRefreshedEvent> {

//    @Bean
//    private BCryptPasswordEncoder bCryptPasswordEncoder(){
//
//        return new BCryptPasswordEncoder();
//    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        if (userRepository.findUserByEmail("adesuyi@gmail.com").isEmpty()){
//            User user = new User("Ololade", "Ola","ololade@gmail.com", bCryptPasswordEncoder().encode("12345"), "12345", "Sabo", RoleType.ROLE_USER);
//            userRepository.save(user);
//        }
    }


}
