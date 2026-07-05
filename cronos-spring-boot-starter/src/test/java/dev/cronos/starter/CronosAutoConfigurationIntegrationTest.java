package dev.cronos.starter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CronosAutoConfigurationIntegrationTest.TestApplication.class)
@AutoConfigureMockMvc
class CronosAutoConfigurationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoadsWithScheduledJobDiscovery() {
        assertThat(TestApplication.HEARTBEAT_COUNT).isNotNegative();
    }

    @Test
    void servesEmbeddedDashboard() throws Exception {
        mockMvc.perform(get("/cronos/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cronos Dashboard")));
    }

    @Test
    void redirectsDashboardRootWithoutTrailingSlash() throws Exception {
        mockMvc.perform(get("/cronos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cronos/"));
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
