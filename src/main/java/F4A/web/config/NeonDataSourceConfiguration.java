package F4A.web.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class NeonDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    HikariDataSource dataSource(Environment environment) {
        String dbUrl = environment.getProperty("DB_URL");
        String username = environment.getProperty("DB_USERNAME");
        String password = environment.getProperty("DB_PASSWORD");

        ConnectionSettings settings = resolve(
                dbUrl,
                username,
                password,
                environment.getProperty("DATABASE_URL")
        );

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(settings.jdbcUrl());
        if (settings.username() != null) {
            dataSource.setUsername(settings.username());
        }
        if (settings.password() != null) {
            dataSource.setPassword(settings.password());
        }
        return dataSource;
    }

    static ConnectionSettings resolve(
            String dbUrl,
            String username,
            String password,
            String databaseUrl
    ) {
        String sourceUrl = firstNonBlank(dbUrl, databaseUrl);
        if (sourceUrl == null) {
            throw new IllegalStateException("Configura DATABASE_URL de Neon o DB_URL");
        }

        sourceUrl = unquote(sourceUrl);
        username = unquote(username);
        password = unquote(password);

        if (sourceUrl.startsWith("jdbc:")) {
            return new ConnectionSettings(sourceUrl, username, password);
        }

        URI uri = URI.create(sourceUrl);
        String scheme = uri.getScheme();
        if (!"postgresql".equalsIgnoreCase(scheme) && !"postgres".equalsIgnoreCase(scheme)) {
            throw new IllegalStateException("DATABASE_URL debe usar PostgreSQL");
        }

        String authority = uri.getRawAuthority();
        if (authority == null) {
            throw new IllegalStateException("DATABASE_URL no contiene un host válido");
        }

        String rawUserInfo = uri.getRawUserInfo();
        if (rawUserInfo != null) {
            int separator = rawUserInfo.indexOf(':');
            if (separator < 0) {
                username = decode(rawUserInfo);
            } else {
                username = decode(rawUserInfo.substring(0, separator));
                password = decode(rawUserInfo.substring(separator + 1));
            }
        }

        String hostAndPort = authority.substring(authority.lastIndexOf('@') + 1);
        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                .append(hostAndPort)
                .append(uri.getRawPath() == null ? "" : uri.getRawPath());
        if (uri.getRawQuery() != null) {
            jdbcUrl.append('?').append(uri.getRawQuery());
        }

        return new ConnectionSettings(jdbcUrl.toString(), username, password);
    }

    private static String firstNonBlank(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return fallback != null && !fallback.isBlank() ? fallback : null;
    }

    private static String unquote(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() >= 2) {
            char first = trimmed.charAt(0);
            char last = trimmed.charAt(trimmed.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return trimmed.substring(1, trimmed.length() - 1);
            }
        }
        return trimmed;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value.replace("+", "%2B"), StandardCharsets.UTF_8);
    }

    record ConnectionSettings(String jdbcUrl, String username, String password) {}
}
