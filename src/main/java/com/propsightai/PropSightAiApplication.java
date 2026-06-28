package com.propsightai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PropSightAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropSightAiApplication.class, args);
    }

}
