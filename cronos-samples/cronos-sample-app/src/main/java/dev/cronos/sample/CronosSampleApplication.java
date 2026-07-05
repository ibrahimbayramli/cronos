package dev.cronos.sample;

import dev.cronos.sample.config.SampleJobProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(SampleJobProperties.class)
public class CronosSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CronosSampleApplication.class, args);
    }
}
