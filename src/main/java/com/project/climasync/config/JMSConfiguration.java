package com.project.climasync.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import jakarta.jms.ConnectionFactory;

/**
 * Configuration class for JMS (Java Message Service) using ActiveMQ.
 * This class defines beans for setting up the JMS connection, template, 
 * and listener container factory.
 */
@Configuration
@EnableJms
public class JMSConfiguration {

    @Value("${activemq.user}")
    private String activemqUser;

    @Value("${activemq.user}")
    private String activemqPwd;

    @Value("${activemq.url}")
    private String activemqUrl;

    /**
     * Creates and configures the JMS ConnectionFactory.
     *
     * @return the configured {@link ConnectionFactory} instance.
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(activemqUrl);
        factory.setUserName(activemqUser);
        factory.setPassword(activemqPwd);
        return factory;
    }

    /**
     * Creates and configures the JMS template for sending messages.
     *
     * @return the configured {@link JmsTemplate} instance.
     */
    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }

    /**
     * Creates and configures the JMS listener container factory.
     * This factory is used for setting up message listeners.
     *
     * @return the configured {@link DefaultJmsListenerContainerFactory} instance.
     */
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1-1");
        return factory;
    }
}
