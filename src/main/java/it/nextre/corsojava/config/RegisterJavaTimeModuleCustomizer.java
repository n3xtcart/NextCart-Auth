package it.nextre.corsojava.config;


import com.fasterxml.jackson.databind.ObjectMapper;


import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.jackson.ObjectMapperCustomizer;

import jakarta.inject.Singleton;

@Singleton
public class RegisterJavaTimeModuleCustomizer implements ObjectMapperCustomizer {
    @Override
    public void customize(ObjectMapper mapper) {
        mapper.registerModule(new JavaTimeModule());
    }
}