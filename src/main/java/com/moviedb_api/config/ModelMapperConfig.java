package com.moviedb_api.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.modelmapper.ModelMapper;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Custom converter for birthDate
        Converter<String, LocalDate> stringToLocalDateConverter = new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(MappingContext<String, LocalDate> context) {
                String source = context.getSource();
                if (source != null && !source.isEmpty()) {
                    try {
                        // Parse the date string in UTC format (YYYY-MM-DD)
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        return LocalDate.parse(source, formatter);
                    } catch (DateTimeParseException e) {
                        System.err.println("Error parsing date: " + source);
                        e.printStackTrace();
                    }
                }
                return null; // Return null if the source is null or empty
            }
        };

        // Register your custom converter
        modelMapper.addConverter(stringToLocalDateConverter);

        return modelMapper;
    }
}