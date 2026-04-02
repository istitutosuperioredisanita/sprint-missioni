package it.cnr.si.missioni.config.db;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "it.cnr.si.missioni.repository")
@EnableJpaAuditing(
        auditorAwareRef = "springSecurityAuditorAware",
        dateTimeProviderRef = "auditingDateTimeProvider"
)
@EnableTransactionManagement
public class MissioniDatabaseConfiguration extends DatabaseConfiguration {

    private static final String MISSIONI_CHANGELOG = "classpath:config/liquibase/master.xml";

    public MissioniDatabaseConfiguration(Environment env) {
        super(env);
    }

    @Bean
    public SpringLiquibase liquibase(
            DataSource dataSource,
            LiquibaseProperties liquibaseProperties
    ) {
        return buildLiquibase(dataSource, liquibaseProperties, MISSIONI_CHANGELOG, false);
    }
}