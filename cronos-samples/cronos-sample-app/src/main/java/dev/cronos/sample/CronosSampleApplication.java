package dev.cronos.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CronosSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CronosSampleApplication.class, args);
    }
}
