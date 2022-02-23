package it.cnr.si.missioni.service;

import java.io.Serializable;


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
    public void verifyStepAmministrativoNewTransaction(Serializable idOrdineMissione) throws Exception {
    	verifyStepAmministrativo(idOrdineMissione);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void verifyStepRespGruppoNewTransaction(Serializable idOrdineMissione) throws Exception {
    	verifyStepRespGruppo(idOrdineMissione);
    }


	public void verifyStepAmministrativo(Serializable idOrdineMissione)
			throws ComponentException, Exception {
		if (idOrdineMissione != null){
			OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById( OrdineMissione.class, idOrdineMissione);
			ordineMissioneService.verifyStepAmministrativo(ordineMissione);
		}
	}

	public void verifyStepRespGruppo(Serializable idOrdineMissione)
			throws ComponentException, Exception {
		if (idOrdineMissione != null){
			OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById( OrdineMissione.class, idOrdineMissione);
			ordineMissioneService.verifyStepRespGruppo(ordineMissione);
		}
	}

}
