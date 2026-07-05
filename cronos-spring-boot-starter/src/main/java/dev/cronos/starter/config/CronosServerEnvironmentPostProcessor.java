package dev.cronos.starter.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Applies {@code cronos.port} to {@code server.port} when the host app has not
 * explicitly configured its own HTTP port.
 */
public class CronosServerEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    static final String CRONOS_PORT_KEY = "cronos.port";
    static final String SERVER_PORT_KEY = "server.port";
    static final String PROPERTY_SOURCE_NAME = "cronosServerPort";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Integer cronosPort = environment.getProperty(CRONOS_PORT_KEY, Integer.class);
        if (cronosPort == null) {
            return;
        }

        if (hasUserDefinedProperty(environment, SERVER_PORT_KEY)) {
            return;
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put(SERVER_PORT_KEY, cronosPort);
        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
    }

    static boolean hasUserDefinedProperty(ConfigurableEnvironment environment, String key) {
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (isApplicationPropertySource(propertySource.getName()) && propertySource.containsProperty(key)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isApplicationPropertySource(String name) {
        return name.startsWith("Config resource")
                || name.startsWith("applicationConfig:")
                || name.equals("TestPropertySource")
                || name.equals("Inlined Test Properties")
                || name.equals("Inline properties");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
