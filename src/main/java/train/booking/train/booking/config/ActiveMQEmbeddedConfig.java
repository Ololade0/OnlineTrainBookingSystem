package train.booking.train.booking.config;

import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMQEmbeddedConfig {

    @Bean
    public BrokerService broker() throws Exception {
        BrokerService broker = new BrokerService();
        broker.addConnector("vm://localhost"); // in-JVM, no TCP
        broker.setPersistent(false); // messages in memory only
        broker.setUseJmx(false);
        broker.start();
        return broker;
    }
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
}
