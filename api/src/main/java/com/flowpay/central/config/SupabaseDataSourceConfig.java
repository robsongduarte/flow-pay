package com.flowpay.central.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Locale;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
@Profile("supabase")
public class SupabaseDataSourceConfig {

    @Bean
    @Primary
    public DataSource supabaseDataSource(Environment environment) {
        String mode = lowerTrim(getProperty(environment, "direct", "SUPABASE_MODE", "flowpay.supabase.mode"));
        if (!"direct".equals(mode) && !"pooler".equals(mode)) {
            throw new IllegalStateException("SUPABASE_MODE invalido. Use 'direct' ou 'pooler'.");
        }

        String password = getProperty(environment, null, "SUPABASE_DB_PASSWORD");
        if (!StringUtils.hasText(password)) {
            throw new IllegalStateException("SUPABASE_DB_PASSWORD deve ser informado.");
        }

        String projectRef = lowerTrim(getProperty(
                environment,
                null,
                "SUPABASE_PROJECT_REF",
                "flowpay.supabase.project-ref"
        ));
        String database = getProperty(environment, "postgres", "SUPABASE_DB_NAME", "flowpay.supabase.db-name");
        String sslMode = getProperty(environment, "require", "SUPABASE_SSLMODE", "flowpay.supabase.sslmode");

        String jdbcUrl = getProperty(environment, null, "SUPABASE_DB_URL");
        String username = getProperty(environment, null, "SUPABASE_DB_USER");

        if (!StringUtils.hasText(jdbcUrl)) {
            jdbcUrl = buildJdbcUrl(environment, mode, projectRef, database, sslMode);
        }

        if (!StringUtils.hasText(username)) {
            username = buildUsername(environment, mode, projectRef);
        } else if ("pooler".equals(mode) && !username.contains(".")) {
            if ("postgres".equalsIgnoreCase(username) && StringUtils.hasText(projectRef)) {
                username = "postgres." + projectRef;
            } else {
                throw new IllegalStateException("""
                        Para SUPABASE_MODE=pooler, o usuario deve seguir o formato 'postgres.<project_ref>' \
                        ou informar SUPABASE_PROJECT_REF para gerar automaticamente.
                        """);
            }
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    private String buildJdbcUrl(
            Environment environment,
            String mode,
            String projectRef,
            String database,
            String sslMode
    ) {
        String host;
        String port;
        if ("direct".equals(mode)) {
            host = lowerTrim(getProperty(
                    environment,
                    null,
                    "SUPABASE_DIRECT_HOST",
                    "flowpay.supabase.direct.host"
            ));
            if (!StringUtils.hasText(host)) {
                if (!StringUtils.hasText(projectRef)) {
                    throw new IllegalStateException(
                            "Informe SUPABASE_DIRECT_HOST ou SUPABASE_PROJECT_REF para modo direct.");
                }
                host = "db." + projectRef + ".supabase.co";
            }
            port = getProperty(environment, "5432", "SUPABASE_DIRECT_PORT", "flowpay.supabase.direct.port");
        } else {
            host = lowerTrim(getProperty(
                    environment,
                    null,
                    "SUPABASE_POOLER_HOST",
                    "flowpay.supabase.pooler.host"
            ));
            if (!StringUtils.hasText(host)) {
                String region = lowerTrim(getProperty(environment, "us-east-1", "SUPABASE_REGION"));
                host = "aws-0-" + region + ".pooler.supabase.com";
            }
            port = getProperty(environment, "5432", "SUPABASE_POOLER_PORT", "flowpay.supabase.pooler.port");
        }
        return "jdbc:postgresql://" + host + ":" + port + "/" + database + "?sslmode=" + sslMode;
    }

    private String buildUsername(Environment environment, String mode, String projectRef) {
        if ("direct".equals(mode)) {
            return getProperty(environment, "postgres", "SUPABASE_DIRECT_USER", "flowpay.supabase.direct.user");
        }

        String poolerUser = getProperty(environment, null, "SUPABASE_POOLER_USER", "flowpay.supabase.pooler.user");
        if (StringUtils.hasText(poolerUser)) {
            return poolerUser;
        }
        if (!StringUtils.hasText(projectRef)) {
            throw new IllegalStateException(
                    "Informe SUPABASE_POOLER_USER ou SUPABASE_PROJECT_REF para modo pooler.");
        }
        return "postgres." + projectRef;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String getProperty(Environment environment, String defaultValue, String... keys) {
        for (String key : keys) {
            String candidate = trim(environment.getProperty(key));
            if (StringUtils.hasText(candidate)) {
                return candidate;
            }
        }
        return defaultValue;
    }

    private String lowerTrim(String value) {
        String normalized = trim(value);
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }
}
