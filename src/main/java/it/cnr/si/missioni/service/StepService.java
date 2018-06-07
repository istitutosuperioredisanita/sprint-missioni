package it.cnr.si.missioni.service;

import java.io.Serializable;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.web.filter.MissioneFilter;

@Service
public class StepService {

	@Autowired
    private OrdineMissioneService ordineMissioneService;
	
	@Autowired
    private RimborsoMissioneService rimborsoMissioneService;
	
	@Autowired
	private CRUDComponentSession crudServiceBean;
	
	public void verifyStep() {
		MissioneFilter filtro = new MissioneFilter();
		filtro.setStato(Costanti.STATO_CONFERMATO);
		filtro.setDaCron("S");
	}
	
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void verifyStepAmministrativoNewTransaction(Principal principal, Serializable idOrdineMissione) throws Exception {
    	verifyStepAmministrativo(principal, idOrdineMissione);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void verifyStepRespGruppoNewTransaction(Principal principal, Serializable idOrdineMissione) throws Exception {
    	verifyStepRespGruppo(principal, idOrdineMissione);
    }


	public void verifyStepAmministrativo(Principal principal, Serializable idOrdineMissione)
			throws ComponentException, Exception {
		if (idOrdineMissione != null){
			OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, idOrdineMissione);
			ordineMissioneService.verifyStepAmministrativo(principal, ordineMissione);
		}
	}

	public void verifyStepRespGruppo(Principal principal, Serializable idOrdineMissione)
			throws ComponentException, Exception {
		if (idOrdineMissione != null){
			OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, idOrdineMissione);
			ordineMissioneService.verifyStepRespGruppo(principal, ordineMissione);
		}
	}

}
