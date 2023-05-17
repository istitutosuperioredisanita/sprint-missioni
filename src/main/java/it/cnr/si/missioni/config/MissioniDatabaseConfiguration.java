/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import it.cnr.si.config.DatabaseConfiguration;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

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
}
