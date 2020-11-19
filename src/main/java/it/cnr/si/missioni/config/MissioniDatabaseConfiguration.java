package it.cnr.si.missioni.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import it.cnr.si.config.DatabaseConfiguration;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@EnableJpaRepositories("it.cnr.si.missioni.repository")
@EnableTransactionManagement
public class MissioniDatabaseConfiguration extends DatabaseConfiguration {

    private final Logger log = LoggerFactory.getLogger(MissioniDatabaseConfiguration.class);

    private Environment environment;

	public SpringLiquibase liquibase(DataSource dataSource,
			DataSourceProperties dataSourceProperties,
			LiquibaseProperties liquibaseProperties) {
        log.debug("Configuring Liquibase");
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
//        if (!environment.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT)) {
            liquibase.setChangeLog("classpath:config/liquibase/master.xml");
//        } else {
//            liquibase.setChangeLog("classpath:config/liquibase/masterNoLiquibase.xml");
//        }
        liquibase.setContexts("development, production");
        return liquibase;
	}

	@Bean
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }
/*
    @Bean
    public ServletRegistrationBean dispatcherRegistration() {
        return new ServletRegistrationBean<>(dispatcherServlet());
    }

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new LoggableDispatcherServlet();
    }*/
}
