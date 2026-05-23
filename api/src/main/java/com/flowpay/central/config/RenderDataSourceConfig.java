package com.flowpay.central.config;

import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;
import java.net.URISyntaxException;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(name = "DATABASE_URL")
@ConditionalOnMissingBean(DataSource.class)
public class RenderDataSourceConfig {

    @Bean
    @Primary
    public DataSource renderDataSource(Environment environment) {
        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (!StringUtils.hasText(databaseUrl)) {
            throw new IllegalStateException("DATABASE_URL nao informado.");
        }

        URI uri = toUri(databaseUrl.trim());
        String jdbcUrl = buildJdbcUrl(uri, environment);

        String username = environment.getProperty("DATABASE_USER");
        String password = environment.getProperty("DATABASE_PASSWORD");

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            String[] userInfo = parseUserInfo(uri.getUserInfo());
            username = StringUtils.hasText(username) ? username : userInfo[0];
            password = StringUtils.hasText(password) ? password : userInfo[1];
        }

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalStateException(
                    "Nao foi possivel obter usuario/senha do banco. Configure DATABASE_USER e DATABASE_PASSWORD.");
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    private URI toUri(String databaseUrl) {
        String normalized = databaseUrl.startsWith("postgres://")
                ? databaseUrl.replaceFirst("^postgres://", "postgresql://")
                : databaseUrl;
        try {
            return new URI(normalized);
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("DATABASE_URL invalido: " + databaseUrl, ex);
        }
    }

    private String buildJdbcUrl(URI uri, Environment environment) {
        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String path = uri.getPath();
        String database = StringUtils.hasText(path) ? path.substring(1) : "postgres";
        String sslMode = environment.getProperty("RENDER_DB_SSLMODE", "require");
        return "jdbc:postgresql://" + host + ":" + port + "/" + database + "?sslmode=" + sslMode;
    }

    private String[] parseUserInfo(String userInfo) {
        if (!StringUtils.hasText(userInfo)) {
            return new String[]{null, null};
        }
        String[] parts = userInfo.split(":", 2);
        String user = parts.length > 0 ? parts[0] : null;
        String pass = parts.length > 1 ? parts[1] : null;
        return new String[]{user, pass};
    }
}
