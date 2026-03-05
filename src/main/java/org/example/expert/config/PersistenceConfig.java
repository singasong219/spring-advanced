package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@Configuration
@EnableJpaAuditing
public class PersistenceConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();

    }

}
