package F4A.web.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NeonDataSourceConfigurationTests {

    @Test
    void convertsNeonPostgresUriToJdbcAndExtractsCredentials() {
        var settings = NeonDataSourceConfiguration.resolve(
                null,
                null,
                null,
                "\"postgresql://app%40user:p%2Bass@ep-example-pooler.us-east-2.aws.neon.tech/store?sslmode=require\""
        );

        assertEquals(
                "jdbc:postgresql://ep-example-pooler.us-east-2.aws.neon.tech/store?sslmode=require",
                settings.jdbcUrl()
        );
        assertEquals("app@user", settings.username());
        assertEquals("p+ass", settings.password());
    }

    @Test
    void preservesExistingJdbcUrlAndSeparateCredentials() {
        var settings = NeonDataSourceConfiguration.resolve(
                "jdbc:postgresql://localhost:5444/store",
                "local-user",
                "local-pass",
                null
        );

        assertEquals("jdbc:postgresql://localhost:5444/store", settings.jdbcUrl());
        assertEquals("local-user", settings.username());
        assertEquals("local-pass", settings.password());
    }
}
