package it.cnr.si.missioni.service;


import java.util.Iterator;
import java.util.List;

import javax.persistence.OptimisticLockException;

import it.cnr.si.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoImpegni;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.RimborsoImpegniRepository;
import it.cnr.si.missioni.repository.RimborsoMissioneDettagliRepository;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.proxy.json.object.Impegno;
import it.cnr.si.missioni.util.proxy.json.object.ImpegnoGae;
import it.cnr.si.missioni.util.proxy.json.object.Voce;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoGaeService;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoService;
import it.cnr.si.missioni.util.proxy.json.service.VoceService;

/**
 * Service class for managing users.
 */
@Service
public class RimborsoImpegniService {

    private final Logger log = LoggerFactory.getLogger(RimborsoImpegniService.class);

    @Autowired
    private RimborsoImpegniRepository rimborsoImpegniRepository;

    @Autowired
    private ImpegnoGaeService impegnoGaeService;

    @Autowired
    private ImpegnoService impegnoService;

    @Autowired
    private VoceService voceService;

    @Autowired
    private RimborsoMissioneService rimborsoMissioneService;

    @Autowired
    private RimborsoMissioneDettagliRepository rimborsoMissioneDettagliRepository;

	@Autowired
	private CRUDComponentSession crudServiceBean;

	@Autowired
	private SecurityService securityService;


	@Transactional(readOnly = true)
    public List<RimborsoImpegni> getRimborsoImpegni(Long idRimborso) throws ComponentException {
    	RimborsoMissione rimborsoMissione = (RimborsoMissione)crudServiceBean.findById( RimborsoMissione.class, idRimborso);
		
		if (rimborsoMissione != null){
			List<RimborsoImpegni> lista = rimborsoImpegniRepository.getRimborsoImpegni(rimborsoMissione);
			return lista;
		}
		return null;
    }

    private void validaCRUD(RimborsoImpegni rimborsoImpegni) {
		RimborsoMissione rimborsoMissione = rimborsoImpegni.getRimborsoMissione();
    	if (StringUtils.isEmpty(rimborsoImpegni.getEsercizioOriginaleObbligazione()) ||
    			StringUtils.isEmpty(rimborsoImpegni.pgObbligazione) ){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Dati degli impegni incompleti.");
    	}
			if (!StringUtils.isEmpty(rimborsoImpegni.getRimborsoMissione().getGae())){
				ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(rimborsoImpegni.getRimborsoMissione().getCdsSpesa(), rimborsoImpegni.getRimborsoMissione().getUoSpesa(), rimborsoImpegni.getEsercizioOriginaleObbligazione(), rimborsoImpegni.getPgObbligazione(), rimborsoImpegni.getRimborsoMissione().getGae());
				if (impegnoGae == null){
					rimborsoMissione.setGae(null);
					rimborsoMissione.setToBeUpdated();
//					throw new AwesomeException(CodiciErrore.ERRGEN, "L'impegno indicato "+ rimborsoImpegni.getEsercizioOriginaleObbligazione() + "-" + rimborsoImpegni.getPgObbligazione() +" non corrisponde con la GAE "+ rimborsoImpegni.getRimborsoMissione().getGae()+" indicata oppure non esiste");
				} else {
					if (!StringUtils.isEmpty(rimborsoImpegni.getRimborsoMissione().getVoce())){
						if (!impegnoGae.getCdElementoVoce().equals(rimborsoImpegni.getRimborsoMissione().getVoce())){
							rimborsoMissione.setVoce(null);
							rimborsoMissione.setToBeUpdated();
//							throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": L'impegno indicato "+rimborsoImpegni.getEsercizioOriginaleObbligazione() + "-" + rimborsoImpegni.getPgObbligazione() +" non corrisponde con la voce di Bilancio indicata."+rimborsoImpegni.getRimborsoMissione().getVoce());
						}
					}
					rimborsoImpegni.setCdCdsObbligazione(impegnoGae.getCdCds());
					rimborsoImpegni.setEsercizioObbligazione(impegnoGae.getEsercizio());
					rimborsoImpegni.setVoce(impegnoGae.getCdElementoVoce());
					
				}
			} else {
				Impegno impegno = impegnoService.loadImpegno(rimborsoImpegni.getRimborsoMissione().getCdsSpesa(), rimborsoImpegni.getRimborsoMissione().getUoSpesa(), rimborsoImpegni.getEsercizioOriginaleObbligazione(), rimborsoImpegni.getPgObbligazione());
				if (impegno == null){
					throw new AwesomeException(CodiciErrore.ERRGEN, "L'impegno indicato "+ rimborsoImpegni.getEsercizioOriginaleObbligazione() + "-" + rimborsoImpegni.getPgObbligazione() +" non esiste");
				} else {
					if (!StringUtils.isEmpty(rimborsoImpegni.getRimborsoMissione().getVoce())){
						if (!impegno.getCdElementoVoce().equals(rimborsoImpegni.getRimborsoMissione().getVoce())){
							rimborsoMissione.setVoce(null);
							rimborsoMissione.setToBeUpdated();
//							throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": L'impegno indicato "+rimborsoImpegni.getEsercizioOriginaleObbligazione() + "-" + rimborsoImpegni.getPgObbligazione() +" non corrisponde con la voce di Bilancio indicata."+rimborsoImpegni.getRimborsoMissione().getVoce());
						}
					} else {
						rimborsoImpegni.setVoce(impegno.getCdElementoVoce());
					}
					rimborsoImpegni.setVoce(impegno.getCdElementoVoce());
					rimborsoImpegni.setCdCdsObbligazione(impegno.getCdCds());
					rimborsoImpegni.setEsercizioObbligazione(impegno.getEsercizio());
					
				}
			}
			Voce voce = voceService.loadVoce(rimborsoImpegni.getEsercizioObbligazione(), rimborsoImpegni.getVoce());
			if (voce != null){
				rimborsoImpegni.setDsVoce(voce.getDs_elemento_voce());
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": L'impegno indicato "+rimborsoImpegni.getEsercizioOriginaleObbligazione() + "-" + rimborsoImpegni.getPgObbligazione() +" è collegato ad una voce di Bilancio "+rimborsoImpegni.getVoce()+ " per la quale non è previsto l'utilizzo per le missioni");
			}
			

			List<RimborsoImpegni> lista = rimborsoImpegniRepository.getRimborsoImpegni(rimborsoImpegni.getRimborsoMissione(), rimborsoImpegni.getEsercizioOriginaleObbligazione(), rimborsoImpegni.getPgObbligazione());
			if (lista != null && !lista.isEmpty()){
				if (rimborsoImpegni.getId() != null){
					for (RimborsoImpegni rimbImp : lista){
						if (!rimborsoImpegni.getId().equals(rimbImp.getId())){
							throw new AwesomeException(CodiciErrore.ERRGEN, "Impegno già esistente.");
						}
					}
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN, "Impegno già esistente.");
				}
			}
			if (rimborsoMissione.isToBeUpdated()){
				rimborsoMissione = (RimborsoMissione)crudServiceBean.modificaConBulk( rimborsoMissione);
				rimborsoImpegni.setRimborsoMissione(rimborsoMissione);
			}
    }

	@Transactional(propagation = Propagation.REQUIRED)
    public RimborsoImpegni createRimborsoImpegni(RimborsoImpegni rimborsoImpegni)  throws ComponentException{
    	rimborsoImpegni.setUser(securityService.getCurrentUserLogin());
    	rimborsoImpegni.setStato(Costanti.STATO_INSERITO);
    	RimborsoMissione rimborso = (RimborsoMissione)crudServiceBean.findById( RimborsoMissione.class, rimborsoImpegni.getRimborsoMissione().getId());
    	controlloOperazione(rimborso);

    	rimborsoImpegni.setRimborsoMissione(rimborso);
    	rimborsoImpegni.setToBeCreated();
		validaCRUD(rimborsoImpegni);
		rimborsoImpegni = (RimborsoImpegni)crudServiceBean.creaConBulk(rimborsoImpegni);
    	log.debug("Created Information for rimborsoImpegni: {}", rimborsoImpegni);
    	return rimborsoImpegni;
    }

	protected void controlloOperazione(RimborsoMissione rimborso) {
    	rimborso = (RimborsoMissione)crudServiceBean.findById( RimborsoMissione.class, rimborso.getId());
		
		if (rimborso != null && !rimborso.isMissioneDaValidare() && !rimborso.isMissioneInserita()){
    		throw new AwesomeException(CodiciErrore.ERRGEN, "La missione si trova in uno stato in cui non è possibile effettuare l'operazione.");
    	}
	}

	public void cancellaRimborsoImpegni(
			RimborsoMissione rimborsoMissione)
			throws ComponentException {
		List<RimborsoImpegni> listaRimborsoImpegni = rimborsoImpegniRepository.getRimborsoImpegni(rimborsoMissione);
		if (listaRimborsoImpegni != null && !listaRimborsoImpegni.isEmpty()){
			for (Iterator<RimborsoImpegni> iterator = listaRimborsoImpegni.iterator(); iterator.hasNext();){
				RimborsoImpegni rimborsoImpegni = iterator.next();
				cancellaRimborsoImpegni(rimborsoImpegni);
		    }
		}
	}
	
    @Transactional(propagation = Propagation.REQUIRED)
	public void deleteRimborsoImpegni(Long idRimborsoImpegni) throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
    	RimborsoImpegni rimborsoImpegni = (RimborsoImpegni)crudServiceBean.findById( RimborsoImpegni.class, idRimborsoImpegni);
    	if (rimborsoImpegni.getRimborsoMissione() != null){
        	controlloOperazione(rimborsoImpegni.getRimborsoMissione());
    	}

		//effettuo controlli di validazione operazione CRUD
		if (rimborsoImpegni != null){
			List<RimborsoMissioneDettagli> lista = rimborsoMissioneDettagliRepository.getRimborsoMissioneDettagli(new Long(rimborsoImpegni.getId().toString()));
			if (lista != null && !lista.isEmpty()){
				 throw new AwesomeException(CodiciErrore.ERRGEN, "Operazione non possibile. Esistono dettagli con l'impegno "+rimborsoImpegni.getEsercizioOriginaleObbligazione() + "-" + rimborsoImpegni.getPgObbligazione() +" valorizzato.");
			}

			cancellaRimborsoImpegni(rimborsoImpegni);
		}
	}

	private void cancellaRimborsoImpegni(RimborsoImpegni rimborsoImpegni) throws ComponentException {
		rimborsoImpegni.setToBeUpdated();
		rimborsoImpegni.setStato(Costanti.STATO_ANNULLATO);
		crudServiceBean.modificaConBulk( rimborsoImpegni);
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public RimborsoImpegni updateRimborsoImpegni(RimborsoImpegni rimborsoImpegni)  throws AwesomeException,
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {

    	RimborsoImpegni rimborsoImpegniDB = (RimborsoImpegni)crudServiceBean.findById( RimborsoImpegni.class, rimborsoImpegni.getId());

		if (rimborsoImpegniDB==null)
			throw new AwesomeException(CodiciErrore.ERRGEN, "Rimborso Impegni da aggiornare inesistente.");
		
    	if (rimborsoImpegniDB.getRimborsoMissione() != null){
        	controlloOperazione(rimborsoImpegniDB.getRimborsoMissione());
    	}
		rimborsoImpegniDB.setCdCdsObbligazione(rimborsoImpegni.getCdCdsObbligazione());
		rimborsoImpegniDB.setEsercizioObbligazione(rimborsoImpegni.getEsercizioObbligazione());
		rimborsoImpegniDB.setEsercizioOriginaleObbligazione(rimborsoImpegni.getEsercizioOriginaleObbligazione());
		rimborsoImpegniDB.setPgObbligazione(rimborsoImpegni.getPgObbligazione());
		validaCRUD(rimborsoImpegniDB);
		
		rimborsoImpegniDB.setToBeUpdated();


		rimborsoImpegniDB = (RimborsoImpegni)crudServiceBean.modificaConBulk( rimborsoImpegniDB);
    	
    	log.debug("Updated Information for RimborsoImpegni: {}", rimborsoImpegniDB);
    	return rimborsoImpegni;
    }
    

}
