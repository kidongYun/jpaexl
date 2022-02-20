package com.kian.yun.jpaexl.config;

import com.kian.yun.jpaexl.domain.SimplePersistenceManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public SimplePersistenceManager getPersistenceManager() {
        return SimplePersistenceManager.getInstance();
    }
}
