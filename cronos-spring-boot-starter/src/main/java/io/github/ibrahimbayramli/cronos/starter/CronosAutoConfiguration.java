package io.github.ibrahimbayramli.cronos.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.github.ibrahimbayramli.cronos.starter.config.CronosProperties;

@AutoConfiguration
@ConditionalOnProperty(prefix = "cronos", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(CronosProperties.class)
@EntityScan(basePackages = "io.github.ibrahimbayramli.cronos.core.domain")
@EnableJpaRepositories(basePackages = "io.github.ibrahimbayramli.cronos.starter.persistence")
@ComponentScan(basePackages = "io.github.ibrahimbayramli.cronos.starter")
@EnableAspectJAutoProxy
public class CronosAutoConfiguration {
}
