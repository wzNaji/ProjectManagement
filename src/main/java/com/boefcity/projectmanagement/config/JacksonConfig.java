package com.boefcity.projectmanagement.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;

/*
 * Configuration klasse til tilpasning af Jacksons adfærd i Spring Boot.
 * Denne klasse er ansvarlig for at konfigurere Jacksons ObjectMapper til at bruge
 * brugerdefinerede serializers for bestemte datatyper.
 */
@Configuration
    public class JacksonConfig {

    /*
     Primær bean for ObjectMapper for at sikre, at den bruges som standard i hele applikationen.
     Denne metode konfigurerer ObjectMapper til at bruge en brugerdefineret serialiser for LocalDateTime.

      'return' Konfigureret ObjectMapper-instans.
     */
        @Bean
        @Primary
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();

            // Tilføjer den custom LocalDateTime serializer til module.
            module.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer());

            // Registrerer modulet med ObjectMapper.
            mapper.registerModule(module);
            return mapper;
        }
}
