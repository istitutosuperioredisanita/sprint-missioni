package it.cnr.si.missioni.config.db;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import it.cnr.si.missioni.config.AsyncSpringLiquibase;
import liquibase.integration.spring.SpringLiquibase;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.auditing.DateTimeProvider;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.Executor;

@Configuration
public abstract class DatabaseConfiguration {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final Environment env;

    protected DatabaseConfiguration(Environment env) {
        this.env = env;
    }

    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
    public Server h2TCPServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers");
    }

    @Bean
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module();
    }

    protected SpringLiquibase buildLiquibase(
            javax.sql.DataSource dataSource,
            LiquibaseProperties liquibaseProperties,
            String changeLogPath,
            boolean async
    ) {
        SpringLiquibase liquibase;

        if (async) {
            liquibase = new AsyncSpringLiquibase(new Executor() {
                @Override
                public void execute(Runnable command) {
                    command.run();
                }
            }, env);
        } else {
            liquibase = new SpringLiquibase();
        }

        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLogPath);

        if (liquibaseProperties.getContexts() != null &&
                !liquibaseProperties.getContexts().isEmpty()) {
            liquibase.setContexts(String.join(",", liquibaseProperties.getContexts()));
        }

        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());

        if (env.matchesProfiles(Constants.SPRING_PROFILE_NO_LIQUIBASE)) {
            liquibase.setShouldRun(false);
            log.debug("Liquibase disabled by profile {}", Constants.SPRING_PROFILE_NO_LIQUIBASE);
        } else {
            liquibase.setShouldRun(liquibaseProperties.isEnabled());
            log.debug("Configuring Liquibase with changelog {}", changeLogPath);
        }

        return liquibase;
    }
}