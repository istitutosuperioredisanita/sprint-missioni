package it.cnr.si.missioni.amq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.cnr.si.missioni.amq.domain.TypeMissione;
import it.cnr.si.missioni.amq.domain.TypeTipoMissione;
import it.cnr.si.missioni.service.AnnullamentoOrdineMissioneService;
import it.cnr.si.missioni.service.OrdineMissioneService;
import it.cnr.si.missioni.service.RimborsoMissioneService;
import it.cnr.si.missioni.util.data.DataQueue;
import it.cnr.si.missioni.util.data.Queue;

@Component
public class ResendQueueService {
	private static final Logger LOGGER  = LoggerFactory.getLogger(RabbitMQService.class);

	@Autowired
    private OrdineMissioneService ordineMissioneService;

    @Autowired
    private RimborsoMissioneService rimborsoMissioneService;

    @Autowired
    private AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;

    public void resendQueue(DataQueue dataQueue){
    	if (dataQueue != null){
    	    LOGGER.info("Esiste Coda");
    		for (Queue queue : dataQueue.getQueues()){
    			switch (queue.getTipo()) {
				case "ORDINE":
					ordineMissioneService.popolaCoda(queue.getId());
					break;
				case "ANNULLAMENTO":
					annullamentoOrdineMissioneService.popolaCoda(queue.getId());
					break;
				case "RIMBORSO":
					rimborsoMissioneService.popolaCoda(queue.getId());
					break;
				}
    		}
    	}
    }
}
