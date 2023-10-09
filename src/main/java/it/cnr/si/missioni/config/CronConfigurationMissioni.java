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

import it.cnr.si.missioni.service.CronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Profile("!showcase")
@Configuration
@EnableScheduling
public class CronConfigurationMissioni {
    @Value("${cron.comunicaDati.active}")
    private boolean cronComunicaDatiActive;

    @Value("${cron.comunicaDatiVecchiaScrivania.active}")
    private boolean cronComunicaDatiVecchiaScrivaniaActive;

    @Value("${cron.evictCache.active}")
    private boolean cronEvictCacheActive;

    @Value("${cron.loadCache.active}")
    private boolean cronLoadCacheActive;

    @Value("${cron.verifyStep.active}")
    private boolean cronVerifyStepActive;

    @Value("${cron.happysign.active}")
    private boolean cronHappySignActive;

    @Autowired(required = false)
    private CronService cronService;



    @Scheduled(cron = "${cron.comunicaDati.cronExpression}")
    public void cronComunicaDati() throws Exception {
        if (cronComunicaDatiActive) {
            cronService.comunicaDatiRimborsoSigla();
        }
    }

    @Scheduled(cron = "${cron.comunicaDatiVecchiaScrivania.cronExpression}")
    public void cronComunicaDatiVecchiaScrivania() throws Exception {
        if (cronComunicaDatiVecchiaScrivaniaActive) {
            cronService.verificaFlussoEComunicaDatiRimborsoSigla();
        }
    }

    @Scheduled(cron = "${cron.verifyStep.cronExpression}")
    public void cronVerifyStep() throws Exception {
        if (cronVerifyStepActive)
            cronService.verifyStep();
    }

    @Scheduled(cron = "${cron.evictCache.cronExpression}")
    public void evictCache() throws Exception {
        if (cronEvictCacheActive) {
            cronService.evictCache();
            cronService.evictCacheTerzoCompenso();
            evictCacheAce();
        }
    }

    public void evictCacheAce() {
        cronService.evictCachePersone();
        cronService.evictCacheRuoli();
        cronService.evictCacheGrant();
        cronService.evictCacheAccount();
        cronService.evictCacheDirettore();
        cronService.evictCacheIdSede();
    }

    @Scheduled(cron = "${cron.loadCache.cronExpression}")
    public void loadCache() throws Exception {
        if (cronLoadCacheActive)
            cronService.loadCache();
    }
    @Scheduled(cron = "${cron.happysign.cronExpression}")
    public void cronHappysign() throws Exception {
        if (cronHappySignActive) {
            cronService.verificaFirmeHappySign();
        }
    }
}
