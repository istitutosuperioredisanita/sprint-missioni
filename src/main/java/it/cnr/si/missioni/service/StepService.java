package it.cnr.si.missioni.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.web.filter.MissioneFilter;

@Service
public class StepService {

	@Autowired
    private OrdineMissioneService ordineMissioneService;
	
	@Autowired
    private RimborsoMissioneService rimborsoMissioneService;
	
	public void verifyStep() {
		MissioneFilter filtro = new MissioneFilter();
		filtro.setStato(Costanti.STATO_CONFERMATO);
		filtro.setDaCron("S");
	}

}
