package dev.cronos.starter.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class CronosServerEnvironmentPostProcessorTest {

    private final CronosServerEnvironmentPostProcessor processor = new CronosServerEnvironmentPostProcessor();

    @Test
    void appliesCronosPortWhenServerPortIsNotConfigured() {
        ConfigurableEnvironment environment = new MockEnvironment()
                .withProperty(CronosServerEnvironmentPostProcessor.CRONOS_PORT_KEY, "9090");

        processor.postProcessEnvironment(environment, new SpringApplication());

        assertThat(environment.getProperty(CronosServerEnvironmentPostProcessor.SERVER_PORT_KEY)).isEqualTo("9090");
    }

    @Test
    void keepsExplicitServerPortWhenBothAreConfigured() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty(CronosServerEnvironmentPostProcessor.CRONOS_PORT_KEY, "9090");
        environment.getPropertySources().addFirst(
                new org.springframework.core.env.MapPropertySource(
                        "Config resource [application.yml]",
                        java.util.Map.of(CronosServerEnvironmentPostProcessor.SERVER_PORT_KEY, 8080)));

        processor.postProcessEnvironment(environment, new SpringApplication());

        assertThat(environment.getProperty(CronosServerEnvironmentPostProcessor.SERVER_PORT_KEY)).isEqualTo("8080");
    }

    @Test
    void ignoresMissingCronosPort() {
        ConfigurableEnvironment environment = new MockEnvironment();

        processor.postProcessEnvironment(environment, new SpringApplication());

        assertThat(environment.getProperty(CronosServerEnvironmentPostProcessor.SERVER_PORT_KEY)).isNull();
    }
}
