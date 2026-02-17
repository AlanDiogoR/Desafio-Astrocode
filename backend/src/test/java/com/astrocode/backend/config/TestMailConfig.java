package com.astrocode.backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.mock;

@Configuration
public class TestMailConfig {

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    JavaMailSender javaMailSender() {
        return mock(JavaMailSender.class);
    }
}
