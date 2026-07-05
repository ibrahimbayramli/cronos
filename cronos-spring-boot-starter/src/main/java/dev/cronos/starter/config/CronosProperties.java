package dev.cronos.starter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "cronos")
public class CronosProperties {

    private boolean enabled = true;

    /**
     * HTTP port for Cronos UI and API. When set and {@code server.port} is not
     * configured, this value is applied to {@code server.port}. When both are
     * set to different values, Cronos is also exposed on this port via an
     * additional Tomcat connector.
     */
    private Integer port;

    private String apiBasePath = "/cronos/api";

    private boolean uiEnabled = true;

    private String uiBasePath = "/cronos";

    private Duration executionRetention = Duration.ofDays(90);

    private int manualTriggerPoolSize = 4;

    private Datasource datasource = new Datasource();

    @Getter
    @Setter
    public static class Datasource {

        private String url = "jdbc:h2:file:./data/cronos;DB_CLOSE_DELAY=-1";

        private String username = "sa";

        private String password = "";

        private String driverClassName = "org.h2.Driver";
    }
}
