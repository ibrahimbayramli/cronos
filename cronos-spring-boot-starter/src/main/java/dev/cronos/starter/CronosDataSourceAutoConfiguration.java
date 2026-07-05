package dev.cronos.starter;

import com.zaxxer.hikari.HikariDataSource;
import dev.cronos.starter.config.CronosProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@AutoConfiguration(before = CronosAutoConfiguration.class)
@ConditionalOnClass(DataSource.class)
@ConditionalOnProperty(prefix = "cronos", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean(DataSource.class)
@EnableConfigurationProperties(CronosProperties.class)
@RequiredArgsConstructor
public class CronosDataSourceAutoConfiguration {

    private final CronosProperties properties;

    @Bean
    public DataSource cronosDataSource() {
        CronosProperties.Datasource datasource = properties.getDatasource();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(datasource.getUrl());
        dataSource.setDriverClassName(datasource.getDriverClassName());
        dataSource.setUsername(datasource.getUsername());
        dataSource.setPassword(datasource.getPassword());
        return dataSource;
    }
}
