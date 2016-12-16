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

    @Autowired
    private CronService cronService;
    
	@Scheduled(cron = "${cron.comunicaDati.cronExpression}")
    public void cronComunicaDati() throws Exception {
    	if (cronComunicaDatiActive)
    		cronService.comunicaDati(new GenericPrincipal("cronMissioni"));
    }
}
