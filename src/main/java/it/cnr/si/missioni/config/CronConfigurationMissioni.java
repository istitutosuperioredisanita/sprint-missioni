package it.cnr.si.missioni.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import it.cnr.jada.GenericPrincipal;
import it.cnr.si.missioni.service.CronService;

@Configuration
@EnableScheduling
public class CronConfigurationMissioni {
    @Value("${cron.comunicaDati.active}")
    private boolean cronComunicaDatiActive;

    @Value("${cron.evictCache.active}")
    private boolean cronEvictCacheActive;

    @Value("${cron.loadCache.active}")
    private boolean cronLoadCacheActive;

    @Value("${cron.verifyStep.active}")
    private boolean cronVerifyStepActive;

    @Autowired
    private CronService cronService;
    
	@Scheduled(cron = "${cron.comunicaDati.cronExpression}")
    public void cronComunicaDati() throws Exception {
    	if (cronComunicaDatiActive)
    		cronService.verificaFlussoEComunicaDatiRimborsoSigla(new GenericPrincipal("cronMissioni"));
    }

	@Scheduled(cron = "${cron.verifyStep.cronExpression}")
    public void cronVerifyStep() throws Exception {
    	if (cronVerifyStepActive)
    		cronService.verifyStep(new GenericPrincipal("cronMissioni"));
    }

	@Scheduled(cron = "${cron.evictCache.cronExpression}")
    public void evictCache() throws Exception {
    	if (cronEvictCacheActive)
    		cronService.evictCache();
    }

	@Scheduled(cron = "${cron.loadCache.cronExpression}")
    public void loadCache() throws Exception {
    	if (cronLoadCacheActive)
    		cronService.loadCache();
    }
}
