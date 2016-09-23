package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.proxy.json.object.Cdr;
import it.cnr.si.missioni.util.proxy.json.object.Gae;
import it.cnr.si.missioni.util.proxy.json.object.Impegno;
import it.cnr.si.missioni.util.proxy.json.object.ImpegnoGae;
import it.cnr.si.missioni.util.proxy.json.object.Progetto;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;
import it.cnr.si.missioni.util.proxy.json.service.CdrService;
import it.cnr.si.missioni.util.proxy.json.service.GaeService;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoGaeService;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoService;
import it.cnr.si.missioni.util.proxy.json.service.ProgettoService;
import it.cnr.si.missioni.util.proxy.json.service.UnitaOrganizzativaService;

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

	@Autowired
	UnitaOrganizzativaService unitaOrganizzativaService;
	
	@Autowired
	CdrService cdrService;
	
	@Autowired
	ImpegnoGaeService impegnoGaeService;
	
	@Autowired
	ImpegnoService impegnoService;
	
	@Autowired
	GaeService gaeService;
	
	@Autowired
	ProgettoService progettoService;
	
    @Inject
    private DatiIstitutoService datiIstitutoService;

    @Transactional(propagation = Propagation.REQUIRED)
    public RimborsoMissione createOrdineMissione(Principal principal, RimborsoMissione rimborsoMissione)  throws AwesomeException, 
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {
    	controlloDatiObbligatoriDaGUI(rimborsoMissione);
    	inizializzaCampiPerInserimento(principal, rimborsoMissione);
		validaCRUD(principal, rimborsoMissione);
		rimborsoMissione = (RimborsoMissione)crudServiceBean.creaConBulk(principal, rimborsoMissione);
    	log.debug("Created Information for User: {}", rimborsoMissione);
    	return rimborsoMissione;
    }

    @Transactional(readOnly = true)
    private void inizializzaCampiPerInserimento(Principal principal,
    		RimborsoMissione rimborsoMissione) throws ComponentException,
    		PersistencyException, BusyResourceException {
    	rimborsoMissione.setUidInsert(principal.getName());
    	rimborsoMissione.setUser(principal.getName());
    	Integer anno = recuperoAnno(rimborsoMissione);
    	rimborsoMissione.setAnno(anno);
    	rimborsoMissione.setNumero(datiIstitutoService.getNextPG(principal, rimborsoMissione.getCdsRich(), anno , Costanti.TIPO_RIMBORSO_MISSIONE));
    	if (StringUtils.isEmpty(rimborsoMissione.getTrattamento())){
    		rimborsoMissione.setTrattamento("R");
    	}
    	if (StringUtils.isEmpty(rimborsoMissione.getUtilizzoAutoNoleggio())){
    		rimborsoMissione.setUtilizzoAutoNoleggio("N");
    	}
    	if (StringUtils.isEmpty(rimborsoMissione.getUtilizzoTaxi())){
    		rimborsoMissione.setUtilizzoTaxi("N");
    	}
    	
    	aggiornaValidazione(rimborsoMissione);
    	
    	rimborsoMissione.setStato(Costanti.STATO_INSERITO);
    	rimborsoMissione.setStatoFlusso(Costanti.STATO_INSERITO);
    	rimborsoMissione.setToBeCreated();
    }

	private Integer recuperoAnno(RimborsoMissione rimborsoMissione) {
		if (rimborsoMissione.getDataInserimento() == null){
			rimborsoMissione.setDataInserimento(new Date());
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(rimborsoMissione.getDataInserimento());
		Integer anno = 	calendar.get(Calendar.YEAR);
		return anno;
	}

    private void controlloDatiObbligatoriDaGUI(RimborsoMissione rimborsoMissione){
		if (rimborsoMissione != null){
			if (StringUtils.isEmpty(rimborsoMissione.getCdsRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Cds Richiedente");
			} else if (StringUtils.isEmpty(rimborsoMissione.getCdsSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Cds Spesa");
			} else if (StringUtils.isEmpty(rimborsoMissione.getUoRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Uo Richiedente");
			} else if (StringUtils.isEmpty(rimborsoMissione.getUoSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Uo Spesa");
			} else if (StringUtils.isEmpty(rimborsoMissione.getDataInizioMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Data Inizio Missione");
			} else if (StringUtils.isEmpty(rimborsoMissione.getDataFineMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Data Fine Missione");
			} else if (StringUtils.isEmpty(rimborsoMissione.getDatoreLavoroRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Datore di Lavoro Richiedente");
			} else if (StringUtils.isEmpty(rimborsoMissione.getDestinazione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Destinazione");
			} else if (StringUtils.isEmpty(rimborsoMissione.getComuneResidenzaRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Comune di Residenza del Richiedente");
			} else if (StringUtils.isEmpty(rimborsoMissione.getIndirizzoResidenzaRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Indirizzo di Residenza del Richiedente");
			} else if (StringUtils.isEmpty(rimborsoMissione.getOggetto())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Oggetto");
			} else if (StringUtils.isEmpty(rimborsoMissione.getTipoMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Tipo Missione");
			} else if (StringUtils.isEmpty(rimborsoMissione.getModpag())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Modalità di Pagamento");
			} else if (StringUtils.isEmpty(rimborsoMissione.getAnticipoRicevuto())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Anticipo Ricevuto");
			} else if (StringUtils.isEmpty(rimborsoMissione.getSpeseTerziRicevute())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Altri anticipi ricevuti");
			} 
			if (rimborsoMissione.isMissioneEstera()){
				if (StringUtils.isEmpty(rimborsoMissione.getNazione())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Nazione");
				} 
				if (StringUtils.isEmpty(rimborsoMissione.getTrattamento())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Trattamento");
				} 
			}
		}
    }

	private void aggiornaValidazione(RimborsoMissione rimborsoMissione) {
		Uo uo = uoService.recuperoUoSigla(rimborsoMissione.getUoRich());
		if (Utility.nvl(uo.getOrdineDaValidare(),"N").equals("N")){
			rimborsoMissione.setValidato("S");
		} else {
			rimborsoMissione.setValidato("N");
		}
	}

    @Transactional(propagation = Propagation.REQUIRED)
	private void validaCRUD(Principal principal, RimborsoMissione rimborsoMissione) throws AwesomeException {
		if (rimborsoMissione != null){
			controlloCampiObbligatori(rimborsoMissione); 
			controlloCongruenzaDatiInseriti(principal, rimborsoMissione);
			controlloDatiFinanziari(principal, rimborsoMissione);
		}
	}

    @Transactional(propagation = Propagation.REQUIRED)
	private void controlloDatiFinanziari(Principal principal, RimborsoMissione rimborsoMissione) throws AwesomeException {
    	UnitaOrganizzativa uo = unitaOrganizzativaService.loadUo(rimborsoMissione.getUoSpesa(), rimborsoMissione.getCdsSpesa(), rimborsoMissione.getAnno());
    	if (uo == null){
    		throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": La UO "+ rimborsoMissione.getUoSpesa() + " non è corretta rispetto al CDS "+rimborsoMissione.getCdsSpesa());
    	}
		if (!StringUtils.isEmpty(rimborsoMissione.getCdrSpesa())){
			Cdr cdr = cdrService.loadCdr(rimborsoMissione.getCdrSpesa(), rimborsoMissione.getUoSpesa());
			if (cdr == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": Il CDR "+ rimborsoMissione.getCdrSpesa() + " non è corretto rispetto alla UO "+rimborsoMissione.getUoSpesa());
			}
		}
		if (!StringUtils.isEmpty(rimborsoMissione.getPgProgetto())){
			Progetto progetto = progettoService.loadModulo(rimborsoMissione.getPgProgetto(), rimborsoMissione.getAnno(), rimborsoMissione.getUoSpesa());
			if (progetto == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": Il modulo indicato non è corretto rispetto alla UO "+rimborsoMissione.getUoSpesa());
			}
		}
		if (!StringUtils.isEmpty(rimborsoMissione.getGae())){
			if (StringUtils.isEmpty(rimborsoMissione.getCdrSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile indicare la GAE senza il centro di responsabilità");
			}
			Gae gae = gaeService.loadGae(rimborsoMissione);
			if (gae == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, "La GAE "+ rimborsoMissione.getGae()+" indicata non esiste");
			} else {
				boolean progettoCdrIndicato = false;
				if (!StringUtils.isEmpty(rimborsoMissione.getPgProgetto()) && !StringUtils.isEmpty(gae.getPg_progetto())){
					progettoCdrIndicato = true;
					if (gae.getPg_progetto().compareTo(rimborsoMissione.getPgProgetto()) != 0){
						throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": La GAE indicata "+ rimborsoMissione.getGae()+" non corrisponde al modulo indicato.");
					}
				}
				if (!StringUtils.isEmpty(rimborsoMissione.getCdrSpesa())){
					progettoCdrIndicato = true;
					if (!gae.getCd_centro_responsabilita().equals(rimborsoMissione.getCdrSpesa()) ){
						throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": La GAE indicata "+ rimborsoMissione.getGae()+" non corrisponde con il CDR "+rimborsoMissione.getCdrSpesa() +" indicato.");
					}
				}
				if (!progettoCdrIndicato){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": Non è possibile indicare solo La GAE senza il modulo o il CDR.");
				}
			}
		}

		if (!StringUtils.isEmpty(rimborsoMissione.getPgObbligazione())){
			if (StringUtils.isEmpty(rimborsoMissione.getEsercizioOriginaleObbligazione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Oltre al numero dell'impegno è necessario indicare anche l'anno dell'impegno");
			}
			if (!StringUtils.isEmpty(rimborsoMissione.getGae())){
				ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(rimborsoMissione);
				if (impegnoGae == null){
					throw new AwesomeException(CodiciErrore.ERRGEN, "L'impegno indicato "+ rimborsoMissione.getEsercizioOriginaleObbligazione() + "-" + rimborsoMissione.getPgObbligazione() +" non corrisponde con la GAE "+ rimborsoMissione.getGae()+" indicata oppure non esiste");
				} else {
					if (!StringUtils.isEmpty(rimborsoMissione.getVoce())){
						if (!impegnoGae.getCdElementoVoce().equals(rimborsoMissione.getVoce())){
							throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": L'impegno indicato "+rimborsoMissione.getEsercizioOriginaleObbligazione() + "-" + rimborsoMissione.getPgObbligazione() +" non corrisponde con la voce di Bilancio indicata."+rimborsoMissione.getVoce());
						}
					}
					rimborsoMissione.setCdCdsObbligazione(impegnoGae.getCdCds());
					rimborsoMissione.setEsercizioObbligazione(impegnoGae.getEsercizio());
				}
			} else {
				Impegno impegno = impegnoService.loadImpegno(rimborsoMissione);
				if (impegno == null){
					throw new AwesomeException(CodiciErrore.ERRGEN, "L'impegno indicato "+ rimborsoMissione.getEsercizioOriginaleObbligazione() + "-" + rimborsoMissione.getPgObbligazione() +" non esiste");
				} else {
					if (!StringUtils.isEmpty(rimborsoMissione.getVoce())){
						if (!impegno.getCdElementoVoce().equals(rimborsoMissione.getVoce())){
							throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": L'impegno indicato "+rimborsoMissione.getEsercizioOriginaleObbligazione() + "-" + rimborsoMissione.getPgObbligazione() +" non corrisponde con la voce di Bilancio indicata."+rimborsoMissione.getVoce());
						}
					}
					rimborsoMissione.setCdCdsObbligazione(impegno.getCdCds());
					rimborsoMissione.setEsercizioObbligazione(impegno.getEsercizio());
				}
			}
		} else {
			if (!StringUtils.isEmpty(rimborsoMissione.getEsercizioOriginaleObbligazione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Oltre all'anno dell'impegno è necessario indicare anche il numero dell'impegno");
			}
			rimborsoMissione.setCdCdsObbligazione(null);
			rimborsoMissione.setEsercizioObbligazione(null);
		}
    
    }
	
    private void controlloCongruenzaDatiInseriti(Principal principal, RimborsoMissione rimborsoMissione) throws AwesomeException {
		if (rimborsoMissione.getDataFineMissione().before(rimborsoMissione.getDataFineMissione())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI+": La data di fine missione non può essere precedente alla data di inizio missione");
		}
		if (!StringUtils.isEmpty(rimborsoMissione.getNoteUtilizzoTaxiNoleggio())){
			if (rimborsoMissione.getUtilizzoTaxi().equals("N") && rimborsoMissione.getUtilizzoAutoNoleggio().equals("N")){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI+": Non è possibile indicare le note all'utilizzo del taxi o dell'auto a noleggio se non si è scelto il loro utilizzo");
			}
		}
		if (StringUtils.isEmpty(rimborsoMissione.getIdFlusso()) &&  rimborsoMissione.isStatoInviatoAlFlusso()){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile avere lo stato Inviato al flusso e non avere l'ID del flusso");
		} 
	}
	
	private void controlloCampiObbligatori(RimborsoMissione rimborsoMissione) {
		if (!rimborsoMissione.isToBeCreated()){
			controlloDatiObbligatoriDaGUI(rimborsoMissione);
		}
		if (StringUtils.isEmpty(rimborsoMissione.getAnno())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Anno");
		} else if (StringUtils.isEmpty(rimborsoMissione.getUid())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utente");
		} else if (StringUtils.isEmpty(rimborsoMissione.getUtilizzoTaxi())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utilizzo del Taxi");
		} else if (StringUtils.isEmpty(rimborsoMissione.getUtilizzoAutoNoleggio())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utilizzo auto a noleggio");
		} else if (StringUtils.isEmpty(rimborsoMissione.getStato())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Stato");
		} else if (StringUtils.isEmpty(rimborsoMissione.getValidato())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Validato");
		} else if (StringUtils.isEmpty(rimborsoMissione.getNumero())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Numero");
		}
	}
    
}
