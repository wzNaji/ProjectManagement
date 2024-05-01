package com.boefcity.projectmanagement.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;

@Configuration
    public class JacksonConfig {
        @Bean
        @Primary
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer());
            mapper.registerModule(module);
            return mapper;
        }
}
