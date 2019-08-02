package app.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

/**
 * author: Szymon Roziewski on 29.07.19 13:08
 * email: szymon.roziewski@gmail.com
 */
@Configuration
@ComponentScan
public class AppConfig {
    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("vm://localhost");
    }

}
