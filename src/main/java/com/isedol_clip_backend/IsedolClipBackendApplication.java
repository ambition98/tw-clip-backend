package com.isedol_clip_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class IsedolClipBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IsedolClipBackendApplication.class, args);
    }
}
