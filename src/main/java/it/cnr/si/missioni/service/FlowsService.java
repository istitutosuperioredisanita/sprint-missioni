package it.cnr.si.missioni.service;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.cmis.ResultFlows;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.web.filter.MissioneFilter;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;

@Service
public class FlowsService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneService.class);

    @Autowired
	OrdineMissioneService ordineMissioneService;
	
    @Autowired
	RimborsoMissioneService rimborsoMissioneService;
	
    @Autowired
	CMISOrdineMissioneService cmisOrdineMissioneService;
	
    @Autowired
	CMISRimborsoMissioneService cmisRimborsoMissioneService;
	
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aggiornaOrdiniMissioneFlowsNewTransaction(Principal principal, OrdineMissione ordineMissione) throws Exception {
    	aggiornaOrdineMissioneFlows(principal, ordineMissione);
    }

	public void aggiornaOrdineMissioneFlows(Principal principal, OrdineMissione ordineMissione)
			throws ComponentException, Exception {
		if (ordineMissione.isStatoInviatoAlFlusso() && !ordineMissione.isMissioneDaValidare()){
			ResultFlows result = retrieveDataFromFlows(ordineMissione);
			if (result.isApprovato()){
				log.info("Trovato Ordine di missione con id {} della uo {}, anno {}, numero {} approvato.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
				ordineMissioneService.aggiornaOrdineMissioneApprovato(principal, ordineMissione);
			}
		}
	}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aggiornaRimborsiMissioneFlows(Principal principal) {
    	try {
    		log.info("Cron per Aggiornamenti Rimborso Missione");
    		RimborsoMissioneFilter filtroRimborso = new RimborsoMissioneFilter();
    		filtroRimborso.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
    		filtroRimborso.setValidato("S");
    		List<RimborsoMissione> listaRimborsiMissione = rimborsoMissioneService.getRimborsiMissione(principal, filtroRimborso, false, true);
    		if (listaRimborsiMissione != null){
    			for (RimborsoMissione rimborsoMissione : listaRimborsiMissione){
    				if (rimborsoMissione.isStatoInviatoAlFlusso() && !rimborsoMissione.isMissioneDaValidare()){
    					ResultFlows result = retrieveDataFromFlows(rimborsoMissione);
    					if (result.isApprovato()){
    						log.info("Trovato Rimborso missione con id {} della uo {}, anno {}, numero {} approvato.", rimborsoMissione.getId(), rimborsoMissione.getUoRich(), rimborsoMissione.getAnno(), rimborsoMissione.getNumero());
    						rimborsoMissioneService.aggiornaRimborsoMissioneApprovato(principal, rimborsoMissione);
    					}
    				}
    			}
    		}
    	} catch (Exception e) {
    		log.error("Errore nell'aggiornamento del rimborso missione con i dati del flusso "+Utility.getMessageException(e) );
    	}
    }

	private ResultFlows retrieveDataFromFlows(OrdineMissione ordineMissione)
			throws ComponentException {
		ResultFlows result = cmisOrdineMissioneService.getFlowsOrdineMissione(ordineMissione.getIdFlusso());
		return result;
	}

	private ResultFlows retrieveDataFromFlows(RimborsoMissione rimborsoMissione)
			throws ComponentException {
		ResultFlows result = cmisRimborsoMissioneService.getFlowsRimborsoMissione(rimborsoMissione.getIdFlusso());
		return result;
	}

}
