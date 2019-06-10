package it.cnr.si.missioni.service;

import java.io.Serializable;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.cmis.ResultFlows;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.Costanti;

@Service
public class FlowsService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneService.class);

    @Autowired
    private Environment env;

    @Autowired
	OrdineMissioneService ordineMissioneService;
	
    @Autowired
	AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;
	
    @Autowired
	RimborsoMissioneService rimborsoMissioneService;
	
    @Autowired
	CMISOrdineMissioneService cmisOrdineMissioneService;
	
	@Autowired
	private CRUDComponentSession crudServiceBean;
	
    @Autowired
	CMISRimborsoMissioneService cmisRimborsoMissioneService;
	
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaOrdineMissioneFlowsNewTransaction(Principal principal, Serializable idOrdineMissione) throws Exception {
    	return aggiornaOrdineMissioneFlows(principal, idOrdineMissione);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaAnnullamentoOrdineMissioneFlowsNewTransaction(Principal principal, Serializable idAnnullamentoMissione) throws Exception {
    	return aggiornaAnnullamentoOrdineMissioneFlows(principal, idAnnullamentoMissione);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaRimborsoMissioneFlowsNewTransaction(Principal principal, Serializable idRimborsoMissione) throws Exception {
    	return aggiornaRimborsoMissioneFlows(principal, idRimborsoMissione);
    }

	public ResultFlows aggiornaOrdineMissioneFlows(Principal principal, Serializable idOrdineMissione)
			throws ComponentException, Exception {
		if (idOrdineMissione != null){
			OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, idOrdineMissione);
			if (ordineMissione.isStatoInviatoAlFlusso() && !ordineMissione.isMissioneDaValidare()){
				ResultFlows result = retrieveDataFromFlows(ordineMissione);
				if (result == null){
					return null;
				}
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
		}
		return null;
	}

	public ResultFlows aggiornaAnnullamentoOrdineMissioneFlows(Principal principal, Serializable idAnnullamento)
			throws ComponentException, Exception {
		if (idAnnullamento != null){
			AnnullamentoOrdineMissione annullamento = (AnnullamentoOrdineMissione)crudServiceBean.findById(principal, AnnullamentoOrdineMissione.class, idAnnullamento);
			if (annullamento.isStatoInviatoAlFlusso() && !annullamento.isMissioneDaValidare()){
				ResultFlows result = retrieveDataFromFlows(annullamento);
				if (result == null){
					return null;
				}
				if (result.isApprovato()){
					OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, annullamento.getOrdineMissione().getId());
					log.info("Trovato in Scrivania Digitale un annullamento ordine di missione con id {} della uo {}, anno {}, numero {} approvato.", annullamento.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
					annullamentoOrdineMissioneService.aggiornaAnnullamentoMissioneApprovato(principal, annullamento, ordineMissione);
					return result;
				} else if (result.isAnnullato()){
					OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, annullamento.getOrdineMissione().getId());
	    			log.info("Trovato in Scrivania Digitale un annullamento ordine di missione con id {} della uo {}, anno {}, numero {} annullato.", annullamento.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
	    			annullamentoOrdineMissioneService.aggiornaAnnullamentoOrdineMissioneAnnullato(principal, annullamento);
					return result;
				} else if (result.isStateReject()){
					OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, annullamento.getOrdineMissione().getId());
	    			log.info("Trovato in Scrivania Digitale un annullamento ordine di missione con id {} della uo {}, anno {}, numero {} respinto.", annullamento.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
	    			annullamentoOrdineMissioneService.aggiornaAnnullamentoRespinto(principal, result, annullamento);
			    	return result;
				}
			}
		}
		return null;
	}

	private boolean isDevProfile(){
   		if (env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT)) {
   			return true;
   		}
   		return false;
	}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaRimborsoMissioneFlows(Principal principal, Serializable idRimborsoMissione) throws ComponentException, Exception {
		if (idRimborsoMissione != null){
			RimborsoMissione rimborsoMissione = (RimborsoMissione)crudServiceBean.findById(principal, RimborsoMissione.class, idRimborsoMissione);
	    	if (rimborsoMissione.isStatoInviatoAlFlusso() && !rimborsoMissione.isMissioneDaValidare()){
	    		ResultFlows result = retrieveDataFromFlows(rimborsoMissione);
				if (result == null){
					return null;
				}
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
//	    	} else {
//	    		rimborsoMissioneService.ribaltaMissione(principal, rimborsoMissione);
	    	}
		}
    	return null;
    }

	private ResultFlows retrieveDataFromFlows(OrdineMissione ordineMissione)
			throws ComponentException {
		ResultFlows result = cmisOrdineMissioneService.getFlowsOrdineMissione(ordineMissione.getIdFlusso());
		return result;
	}

	private ResultFlows retrieveDataFromFlows(AnnullamentoOrdineMissione annullamento)
			throws ComponentException {
		ResultFlows result = cmisOrdineMissioneService.getFlowsOrdineMissione(annullamento.getIdFlusso());
		return result;
	}

	private ResultFlows retrieveDataFromFlows(RimborsoMissione rimborsoMissione)
			throws ComponentException {
		ResultFlows result = cmisRimborsoMissioneService.getFlowsRimborsoMissione(rimborsoMissione.getIdFlusso());
		return result;
	}
}
