//package train.booking.train.booking.config;
//
//import jakarta.jms.ConnectionFactory;
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.apache.activemq.RedeliveryPolicy;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jms.annotation.EnableJms;
//import org.springframework.jms.core.JmsTemplate;
//
//@Configuration
//@EnableJms
//public class JmsConfig {
//
//    @Bean
//    public ActiveMQConnectionFactory activeMQConnectionFactory() {
//        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
//
//        RedeliveryPolicy policy = new RedeliveryPolicy();
//        policy.setMaximumRedeliveries(3);
//        policy.setInitialRedeliveryDelay(1000);
//        policy.setRedeliveryDelay(2000);
//
//        factory.setRedeliveryPolicy(policy);
//        return factory;
//    }
//
//    @Bean
//    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
//        return new JmsTemplate(connectionFactory);
//    }
//
//}
