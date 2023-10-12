package com.example.clint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ClintApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClintApplication.class, args);
    }

}
