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
    public ResultFlows aggiornaOrdineMissioneFlowsNewTransaction(Principal principal, OrdineMissione ordineMissione) throws Exception {
    	return aggiornaOrdineMissioneFlows(principal, ordineMissione);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaRimborsoMissioneFlowsNewTransaction(Principal principal, RimborsoMissione rimborsoMissione) throws Exception {
    	return aggiornaRimborsoMissioneFlows(principal, rimborsoMissione);
    }

	public ResultFlows aggiornaOrdineMissioneFlows(Principal principal, OrdineMissione ordineMissione)
			throws ComponentException, Exception {
		if (ordineMissione.isStatoInviatoAlFlusso() && !ordineMissione.isMissioneDaValidare()){
			ResultFlows result = retrieveDataFromFlows(ordineMissione);
			if (result.isApprovato()){
				log.info("Trovato in Scrivania Digitale un ordine di missione con id {} della uo {}, anno {}, numero {} approvato.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
				ordineMissioneService.aggiornaOrdineMissioneApprovato(principal, ordineMissione);
				return result;
			} else if (result.isAnnullato()){
    			log.info("Trovato in Scrivania Digitale un ordine di missione con id {} della uo {}, anno {}, numero {} annullato.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
				ordineMissioneService.aggiornaOrdineMissioneAnnullato(principal, ordineMissione);
				return result;
			} else if (result.isStateReject()){
    			log.info("Trovato in Scrivania Digitale un ordine di missione con id {} della uo {}, anno {}, numero {} respinto.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
				ordineMissioneService.aggiornaOrdineMissioneRespinto(principal, result, ordineMissione);
		    	return result;
			}
		}
		return null;
	}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaRimborsoMissioneFlows(Principal principal, RimborsoMissione rimborsoMissione) throws ComponentException, Exception {
    	if (rimborsoMissione.isStatoInviatoAlFlusso() && !rimborsoMissione.isMissioneDaValidare()){
    		ResultFlows result = retrieveDataFromFlows(rimborsoMissione);
    		if (result.isApprovato()){
    			log.info("Trovato in Scrivania Digitale un rimborso missione con id {} della uo {}, anno {}, numero {} approvato.", rimborsoMissione.getId(), rimborsoMissione.getUoRich(), rimborsoMissione.getAnno(), rimborsoMissione.getNumero());
    			rimborsoMissioneService.aggiornaRimborsoMissioneApprovato(principal, rimborsoMissione);
				return result;
    		} else if (result.isAnnullato()){
    			log.info("Trovato in Scrivania Digitale un rimborso missione con id {} della uo {}, anno {}, numero {} annullato.", rimborsoMissione.getId(), rimborsoMissione.getUoRich(), rimborsoMissione.getAnno(), rimborsoMissione.getNumero());
				rimborsoMissioneService.aggiornaRimborsoMissioneAnnullato(principal, rimborsoMissione);
				return result;
			} else if (result.isStateReject()){
    			log.info("Trovato in Scrivania Digitale un rimborso missione con id {} della uo {}, anno {}, numero {} respinto.", rimborsoMissione.getId(), rimborsoMissione.getUoRich(), rimborsoMissione.getAnno(), rimborsoMissione.getNumero());
				rimborsoMissioneService.aggiornaRimborsoMissioneRespinto(principal, result, rimborsoMissione);
		    	return result;
			}
    	}
    	return null;
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
