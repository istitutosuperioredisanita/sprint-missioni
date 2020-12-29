package it.cnr.si.missioni.config;

import it.cnr.si.missioni.util.Costanti;
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

    @Value("${cron.comunicaDatiVecchiaScrivania.active}")
    private boolean cronComunicaDatiVecchiaScrivaniaActive;

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
    	if (cronComunicaDatiActive){
            cronService.comunicaDatiRimborsoSigla(new GenericPrincipal(Costanti.USER_MISSIONI));
        }
    }

    @Scheduled(cron = "${cron.comunicaDatiVecchiaScrivania.cronExpression}")
    public void cronComunicaDatiVecchiaScrivania() throws Exception {
        if (cronComunicaDatiVecchiaScrivaniaActive){
            cronService.verificaFlussoEComunicaDatiRimborsoSigla(new GenericPrincipal(Costanti.USER_MISSIONI));
        }
    }

    @Scheduled(cron = "${cron.verifyStep.cronExpression}")
    public void cronVerifyStep() throws Exception {
    	if (cronVerifyStepActive)
    		cronService.verifyStep(new GenericPrincipal("app.missioni"));
    }

	@Scheduled(cron = "${cron.evictCache.cronExpression}")
    public void evictCache() throws Exception {
    	if (cronEvictCacheActive){
    		cronService.evictCache();
    		cronService.evictCacheTerzoCompenso();
            cronService.evictCachePersone();
    	}
    }

	@Scheduled(cron = "${cron.loadCache.cronExpression}")
    public void loadCache() throws Exception {
    	if (cronLoadCacheActive)
    		cronService.loadCache();
    }
}
