package com.llu1ts.shopapp;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@SpringBootApplication
@Configuration
public class ShopAppApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(ShopAppApplication.class, args);
    }


    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
