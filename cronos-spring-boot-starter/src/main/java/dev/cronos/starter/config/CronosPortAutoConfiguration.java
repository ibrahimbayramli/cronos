package dev.cronos.starter.config;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(TomcatServletWebServerFactory.class)
@ConditionalOnProperty(prefix = "cronos", name = "port")
@RequiredArgsConstructor
public class CronosPortAutoConfiguration {

    private static final int DEFAULT_SERVER_PORT = 8080;

    private final CronosProperties cronosProperties;
    private final ConfigurableEnvironment environment;

    @Bean
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> cronosAdditionalPortCustomizer() {
        return factory -> {
            Integer cronosPort = cronosProperties.getPort();
            if (cronosPort == null) {
                return;
            }

            Integer serverPort = environment.getProperty("server.port", Integer.class, DEFAULT_SERVER_PORT);
            if (cronosPort.equals(serverPort)) {
                return;
            }

            if (!CronosServerEnvironmentPostProcessor.hasUserDefinedProperty(environment, "server.port")) {
                return;
            }

            Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
            connector.setPort(cronosPort);
            factory.addAdditionalTomcatConnectors(connector);
        };
    }
}
