package io.github.ibrahimbayramli.cronos.starter.ui;

import io.github.ibrahimbayramli.cronos.starter.config.CronosProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "cronos", name = "ui-enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class CronosDashboardWebConfiguration implements WebMvcConfigurer {

    private final CronosProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uiPath = normalizePath(properties.getUiBasePath());

        registry.addResourceHandler(uiPath + "/**")
                .addResourceLocations("classpath:/static/cronos/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource resource = super.getResource(resourcePath, location);
                        if (resource != null && resource.exists() && resource.isReadable()) {
                            return resource;
                        }
                        return super.getResource("index.html", location);
                    }
                });
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        String uiPath = normalizePath(properties.getUiBasePath());
        registry.addRedirectViewController(uiPath, uiPath + "/");
        registry.addViewController(uiPath + "/").setViewName("forward:" + uiPath + "/index.html");
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "/cronos";
        }
        String normalized = path.startsWith("/") ? path : "/" + path;
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
