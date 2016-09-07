package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;

import java.security.Principal;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


/**
 * Service class for managing users.
 */
@Service
public class RimborsoMissioneService {

    private final Logger log = LoggerFactory.getLogger(RimborsoMissioneService.class);

	@Autowired
	private CRUDComponentSession crudServiceBean;

	@Autowired
	private UoService uoService;

    @Inject
    private DatiIstitutoService datiIstitutoService;

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissione createOrdineMissione(Principal principal, OrdineMissione ordineMissione)  throws AwesomeException, 
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {
    	controlloDatiObbligatoriDaGUI(ordineMissione);
    	inizializzaCampiPerInserimento(principal, ordineMissione);
		validaCRUD(principal, ordineMissione);
		ordineMissione = (OrdineMissione)crudServiceBean.creaConBulk(principal, ordineMissione);
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Created Information for User: {}", ordineMissione);
    	return ordineMissione;
    }

    @Transactional(readOnly = true)
    private void inizializzaCampiPerInserimento(Principal principal,
    		OrdineMissione ordineMissione) throws ComponentException,
    		PersistencyException, BusyResourceException {
    	ordineMissione.setUidInsert(principal.getName());
    	ordineMissione.setUser(principal.getName());
    	Integer anno = recuperoAnno(ordineMissione);
    	ordineMissione.setAnno(anno);
    	ordineMissione.setNumero(datiIstitutoService.getNextPG(principal, ordineMissione.getCdsRich(), anno , Costanti.TIPO_RIMBORSO_MISSIONE));
    	if (StringUtils.isEmpty(ordineMissione.getTrattamento())){
    		ordineMissione.setTrattamento("R");
    	}
    	if (StringUtils.isEmpty(ordineMissione.getObbligoRientro())){
    		ordineMissione.setObbligoRientro("S");
    	}
    	if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoNoleggio())){
    		ordineMissione.setUtilizzoAutoNoleggio("N");
    	}
    	if (StringUtils.isEmpty(ordineMissione.getUtilizzoTaxi())){
    		ordineMissione.setUtilizzoTaxi("N");
    	}
    	
    	aggiornaValidazione(ordineMissione);
    	
    	ordineMissione.setStato(Costanti.STATO_INSERITO);
    	ordineMissione.setStatoFlusso(Costanti.STATO_INSERITO);
    	ordineMissione.setToBeCreated();
    }

	private Integer recuperoAnno(OrdineMissione ordineMissione) {
		if (ordineMissione.getDataInserimento() == null){
			ordineMissione.setDataInserimento(new Date());
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ordineMissione.getDataInserimento());
		Integer anno = 	calendar.get(Calendar.YEAR);
		return anno;
	}

    private void controlloDatiObbligatoriDaGUI(OrdineMissione ordineMissione){
		if (ordineMissione != null){
			if (StringUtils.isEmpty(ordineMissione.getCdsRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Cds Richiedente");
			} else if (StringUtils.isEmpty(ordineMissione.getCdsSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Cds Spesa");
			} else if (StringUtils.isEmpty(ordineMissione.getUoRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Uo Richiedente");
			} else if (StringUtils.isEmpty(ordineMissione.getUoSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Uo Spesa");
			} else if (StringUtils.isEmpty(ordineMissione.getDataInizioMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Data Inizio Missione");
			} else if (StringUtils.isEmpty(ordineMissione.getDataFineMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Data Fine Missione");
			} else if (StringUtils.isEmpty(ordineMissione.getDataInserimento())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Data Inserimento");
			} else if (StringUtils.isEmpty(ordineMissione.getDatoreLavoroRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Datore di Lavoro Richiedente");
			} else if (StringUtils.isEmpty(ordineMissione.getDestinazione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Destinazione");
			} else if (StringUtils.isEmpty(ordineMissione.getComuneResidenzaRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Comune di Residenza del Richiedente");
			} else if (StringUtils.isEmpty(ordineMissione.getIndirizzoResidenzaRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Indirizzo di Residenza del Richiedente");
			} else if (StringUtils.isEmpty(ordineMissione.getOggetto())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Oggetto");
			} else if (StringUtils.isEmpty(ordineMissione.getPriorita())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Priorità");
			} else if (StringUtils.isEmpty(ordineMissione.getTipoMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Tipo Missione");
			} 
			if (ordineMissione.isMissioneEstera()){
				if (StringUtils.isEmpty(ordineMissione.getNazione())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Nazione");
				} 
				if (StringUtils.isEmpty(ordineMissione.getTrattamento())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Trattamento");
				} 
			}

			if (ordineMissione.isMissioneConGiorniDivervi()){
				if (StringUtils.isEmpty(ordineMissione.getObbligoRientro())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Obbligo di Rientro");
				}
			} 
		}
    }

	private void aggiornaValidazione(OrdineMissione ordineMissione) {
		Uo uo = uoService.recuperoUoSigla(ordineMissione.getUoRich());
		if (Utility.nvl(uo.getOrdineDaValidare(),"N").equals("N")){
			ordineMissione.setValidato("S");
		} else {
			ordineMissione.setValidato("N");
		}
	}

    @Transactional(propagation = Propagation.REQUIRED)
	private void validaCRUD(Principal principal, OrdineMissione ordineMissione) throws AwesomeException {
		if (ordineMissione != null){
//			controlloCampiObbligatori(ordineMissione); 
//			controlloCongruenzaDatiInseriti(principal, ordineMissione);
//			controlloDatiFinanziari(principal, ordineMissione);
		}
	}
    
}
