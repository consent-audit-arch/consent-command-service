package com.tcc.consent_command_service.config;


import org.flywaydb.core.Flyway;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@TestConfiguration
public class TestFlywayConfig {

    @Bean
    public FlywayMigrationInitializer flywayInitializer(DataSource dataSource) throws Exception {
        System.out.println(">>> DATASOURCE URL: " + dataSource.getConnection().getMetaData().getURL());

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .loggers("slf4j")
                .load();

        int migrations = flyway.migrate().migrationsExecuted;
        System.out.println(">>> FLYWAY MIGRATIONS EXECUTED: " + migrations);

        return new FlywayMigrationInitializer(flyway, null);
    }
}
