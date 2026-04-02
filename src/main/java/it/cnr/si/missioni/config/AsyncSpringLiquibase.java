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

import it.cnr.si.missioni.config.db.Constants;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StopWatch;

import java.util.concurrent.Executor;

/**
 * SpringLiquibase che aggiorna il database in modo asincrono in sviluppo,
 * sincrono in produzione.
 * Riscritta per Spring Boot 3 / Java 21.
 */
public class AsyncSpringLiquibase extends SpringLiquibase {

    private final Logger log = LoggerFactory.getLogger(AsyncSpringLiquibase.class);

    private final Executor executor;
    private final Environment env;

    public AsyncSpringLiquibase(Executor executor, Environment env) {
        this.executor = executor;
        this.env = env;
    }

    @Override
    public void afterPropertiesSet() throws LiquibaseException {
        if (!env.matchesProfiles(Constants.SPRING_PROFILE_NO_LIQUIBASE)) {
            // In sviluppo o heroku esegue in modo asincrono
            if (env.matchesProfiles(Constants.SPRING_PROFILE_DEVELOPMENT) ||
                    env.matchesProfiles(Constants.SPRING_PROFILE_HEROKU)) {
                executor.execute(() -> {
                    try {
                        log.warn("Starting Liquibase asynchronously, your database might not be ready at startup!");
                        initDb();
                    } catch (LiquibaseException e) {
                        log.error("Liquibase could not start correctly, your database is NOT ready: {}", e.getMessage(), e);
                    }
                });
            } else {
                // In produzione esegue in modo sincrono
                log.debug("Starting Liquibase synchronously");
                initDb();
            }
        } else {
            log.debug("Liquibase is disabled");
        }
    }

    protected void initDb() throws LiquibaseException {
        StopWatch watch = new StopWatch();
        watch.start();
        super.afterPropertiesSet();
        watch.stop();
        log.debug("Started Liquibase in {} ms", watch.getTotalTimeMillis());
    }
}