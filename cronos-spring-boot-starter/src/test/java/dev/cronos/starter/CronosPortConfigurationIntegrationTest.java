package dev.cronos.starter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = CronosAutoConfigurationIntegrationTest.TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "cronos.port=0"
)
@AutoConfigureMockMvc
class CronosPortConfigurationIntegrationTest {

    @LocalServerPort
    private int localServerPort;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void bindsCronosPortToServerWhenServerPortIsNotConfigured() throws Exception {
        assertThat(localServerPort).isPositive();
        mockMvc.perform(get("/cronos/api/health"))
                .andExpect(status().isOk());
    }
}
