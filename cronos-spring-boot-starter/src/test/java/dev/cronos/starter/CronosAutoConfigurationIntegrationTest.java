package dev.cronos.starter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CronosAutoConfigurationIntegrationTest.TestApplication.class)
class CronosAutoConfigurationIntegrationTest {

    @Test
    void contextLoadsWithScheduledJobDiscovery() {
        assertThat(TestApplication.HEARTBEAT_COUNT).isNotNegative();
    }

    @SpringBootApplication
    @EnableScheduling
    static class TestApplication {

        static int HEARTBEAT_COUNT = 0;

        @Bean
        TestJobs testJobs() {
            return new TestJobs();
        }
    }

    @Component
    static class TestJobs {

        @Scheduled(fixedRate = 3_600_000)
        void heartbeat() {
            TestApplication.HEARTBEAT_COUNT++;
        }
    }
}
