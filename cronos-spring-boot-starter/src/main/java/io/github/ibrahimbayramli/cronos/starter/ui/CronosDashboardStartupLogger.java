package io.github.ibrahimbayramli.cronos.starter.ui;

import io.github.ibrahimbayramli.cronos.starter.config.CronosProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "cronos", name = "ui-enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class CronosDashboardStartupLogger implements ApplicationListener<ApplicationReadyEvent> {

    private final CronosProperties properties;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!(event.getApplicationContext() instanceof WebServerApplicationContext webContext)) {
            return;
        }

        int port = webContext.getWebServer().getPort();
        String uiPath = normalizePath(properties.getUiBasePath());
        String apiPath = properties.getApiBasePath();

        log.info("Cronos dashboard ready at http://localhost:{}{}", port, uiPath);
        log.info("Cronos API ready at http://localhost:{}{}", port, apiPath);
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "/cronos";
        }
        String normalized = path.startsWith("/") ? path : "/" + path;
        if (!normalized.endsWith("/")) {
            normalized = normalized + "/";
        }
        return normalized;
    }
}
