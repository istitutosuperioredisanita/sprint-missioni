package it.cnr.si.missioni.service;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.io.InputStream;
import java.math.BigDecimal;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.RimborsoMissioneDettagliRepository;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.SecurityUtils;

/**
 * Service class for managing users.
 */
@Service
public class RimborsoMissioneDettagliService {

    private final Logger log = LoggerFactory.getLogger(RimborsoMissioneDettagliService.class);

    @Autowired
    private RimborsoMissioneDettagliRepository rimborsoMissioneDettagliRepository;

    @Autowired
    private RimborsoMissioneService rimborsoMissioneService;

    @Autowired
    private MissioniCMISService missioniCMISService;

    @Autowired
    private CMISRimborsoMissioneService cmisRimborsoMissioneService;

	@Autowired
	private CRUDComponentSession crudServiceBean;


	@Transactional(readOnly = true)
    public CMISFileAttachment uploadAllegato(Principal principal, Long idRimborsoMissioneDettagli, InputStream inputStream, String name, MimeTypes mimeTypes) throws ComponentException {
    	RimborsoMissioneDettagli dettaglio = (RimborsoMissioneDettagli)crudServiceBean.findById(principal, RimborsoMissioneDettagli.class, idRimborsoMissioneDettagli);
		if (dettaglio!= null){
			rimborsoMissioneService.controlloOperazioniCRUDDaGui(dettaglio.getRimborsoMissione());
			CMISFileAttachment attachment = cmisRimborsoMissioneService.uploadAttachmentDetail(principal, dettaglio, inputStream, name, mimeTypes);
			return attachment;
		}
		return null;
	}
	
	@Transactional(readOnly = true)
    public List<CMISFileAttachment> getAttachments(Principal principal, Long idRimborsoMissioneDettagli) throws ComponentException {
		if (idRimborsoMissioneDettagli!= null){
			List<CMISFileAttachment> lista = cmisRimborsoMissioneService.getAttachmentsDetail(principal, idRimborsoMissioneDettagli);
			return lista;
		}
		return null;
    }

    @Transactional(readOnly = true)
    public List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli(Principal principal, Long idRimborsoMissione) throws ComponentException {
    	RimborsoMissione rimborsoMissione = (RimborsoMissione)crudServiceBean.findById(principal, RimborsoMissione.class, idRimborsoMissione);
		
		if (rimborsoMissione!= null){
			List<RimborsoMissioneDettagli> lista = rimborsoMissioneDettagliRepository.getRimborsoMissioneDettagli(rimborsoMissione);
			return lista;
		}
		return null;
    }

    private void validaCRUD(Principal principal, RimborsoMissioneDettagli rimborsoMissioneDettagli) {
//    	if (StringUtils.isEmpty(ordineMissioneAutoPropria.getCartaCircolazione()) ||
//    			StringUtils.isEmpty(ordineMissioneAutoPropria.getTarga())  ||
//    			StringUtils.isEmpty(ordineMissioneAutoPropria.getPolizzaAssicurativa())  ||
//    			StringUtils.isEmpty(ordineMissioneAutoPropria.getMarca())  ||
//    			StringUtils.isEmpty(ordineMissioneAutoPropria.getModello()) ){
//			throw new AwesomeException(CodiciErrore.ERRGEN, "Dati dell'auto propria non esistenti o incompleti.");
//    	}
//    	if (StringUtils.isEmpty(ordineMissioneAutoPropria.getDataRilascioPatente()) ||
//    			StringUtils.isEmpty(ordineMissioneAutoPropria.getDataScadenzaPatente())  ||
//    			StringUtils.isEmpty(ordineMissioneAutoPropria.getEntePatente())  ||
//    			StringUtils.isEmpty(ordineMissioneAutoPropria.getNumeroPatente())){
//			throw new AwesomeException(CodiciErrore.ERRGEN, "Dati della patente non esistenti o incompleti.");
//    	}
	}

	@Transactional(propagation = Propagation.REQUIRED)
    public RimborsoMissioneDettagli createRimborsoMissioneDettagli(Principal principal, RimborsoMissioneDettagli rimborsoMissioneDettagli)  throws AwesomeException, 
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {
		rimborsoMissioneDettagli.setUid(principal.getName());
		rimborsoMissioneDettagli.setUser(principal.getName());
		rimborsoMissioneDettagli.setStato(Costanti.STATO_INSERITO);
		if (rimborsoMissioneDettagli.getTiSpesaDiaria() == null){
			rimborsoMissioneDettagli.setTiSpesaDiaria("S");
		}
		RimborsoMissione rimborsoMissione = (RimborsoMissione)crudServiceBean.findById(principal, RimborsoMissione.class, rimborsoMissioneDettagli.getRimborsoMissione().getId());
		if (rimborsoMissione != null){
			rimborsoMissioneService.controlloOperazioniCRUDDaGui(rimborsoMissione);
		}
		rimborsoMissioneDettagli.setRimborsoMissione(rimborsoMissione);
    	Long maxRiga = rimborsoMissioneDettagliRepository.getMaxRigaDettaglio(rimborsoMissione);
    	if (maxRiga == null ){
    		maxRiga = new Long(0);
    	}
    	maxRiga = maxRiga + 1;
    	rimborsoMissioneDettagli.setRiga(maxRiga);
    	rimborsoMissioneDettagli.setToBeCreated();
		impostaImportoDivisa(rimborsoMissioneDettagli);
		validaCRUD(principal, rimborsoMissioneDettagli);
		rimborsoMissioneDettagli = (RimborsoMissioneDettagli)crudServiceBean.creaConBulk(principal, rimborsoMissioneDettagli);
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Created Information for RimborsoMissioneDettagli: {}", rimborsoMissioneDettagli);
    	return rimborsoMissioneDettagli;
    }


    @Transactional(propagation = Propagation.REQUIRED)
	public void cancellaRimborsoMissioneDettagli(Principal principal,
			RimborsoMissione rimborsoMissione, Boolean deleteDocument)
			throws ComponentException {
		List<RimborsoMissioneDettagli> listaRimborsoMissioneDettagli = rimborsoMissioneDettagliRepository.getRimborsoMissioneDettagli(rimborsoMissione);
		if (listaRimborsoMissioneDettagli != null && !listaRimborsoMissioneDettagli.isEmpty()){
			for (Iterator<RimborsoMissioneDettagli> iterator = listaRimborsoMissioneDettagli.iterator(); iterator.hasNext();){
				RimborsoMissioneDettagli dettaglio = iterator.next();
				cancellaRimborsoMissioneDettagli(principal, dettaglio, deleteDocument);
		    }
		}
	}
	
    @Transactional(propagation = Propagation.REQUIRED)
	public void deleteRimborsoMissioneDettagli(Principal principal, Long idRimborsoMissioneDettagli) throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
    	RimborsoMissioneDettagli rimborsoMissioneDettagli = (RimborsoMissioneDettagli)crudServiceBean.findById(principal, RimborsoMissioneDettagli.class, idRimborsoMissioneDettagli);

		//effettuo controlli di validazione operazione CRUD
		if (rimborsoMissioneDettagli != null){
			rimborsoMissioneService.controlloOperazioniCRUDDaGui(rimborsoMissioneDettagli.getRimborsoMissione());
			cancellaRimborsoMissioneDettagli(principal, rimborsoMissioneDettagli, true);
		}
	}

    @Transactional(propagation = Propagation.REQUIRED)
	private void cancellaRimborsoMissioneDettagli(Principal principal, RimborsoMissioneDettagli rimborsoMissioneDettagli, Boolean deleteDocument) throws ComponentException {
    	rimborsoMissioneDettagli.setToBeUpdated();
    	rimborsoMissioneDettagli.setStato(Costanti.STATO_ANNULLATO);
		crudServiceBean.modificaConBulk(principal, rimborsoMissioneDettagli);
		if (deleteDocument){
			cmisRimborsoMissioneService.deleteFolderRimborsoMissioneDettaglio(rimborsoMissioneDettagli);
		}
	}

	private void impostaImportoDivisa(RimborsoMissioneDettagli rimborsoMissioneDettagli){
		if (rimborsoMissioneDettagli.getCambio().compareTo(BigDecimal.ONE) == 0){
			rimborsoMissioneDettagli.setImportoDivisa(rimborsoMissioneDettagli.getImportoEuro());
		}
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public RimborsoMissioneDettagli updateRimborsoMissioneDettagli(Principal principal, RimborsoMissioneDettagli rimborsoMissioneDettagli)  throws AwesomeException, 
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {

    	RimborsoMissioneDettagli rimborsoMissioneDettagliDB = (RimborsoMissioneDettagli)crudServiceBean.findById(principal, RimborsoMissioneDettagli.class, rimborsoMissioneDettagli.getId());

		if (rimborsoMissioneDettagliDB==null)
			throw new AwesomeException(CodiciErrore.ERRGEN, "Dettaglio Rimborso Missione da aggiornare inesistente.");
		rimborsoMissioneService.controlloOperazioniCRUDDaGui(rimborsoMissioneDettagli.getRimborsoMissione());
		
		rimborsoMissioneDettagliDB.setCdTiPasto(rimborsoMissioneDettagli.getCdTiPasto());
		rimborsoMissioneDettagliDB.setCdTiSpesa(rimborsoMissioneDettagli.getCdTiSpesa());
		rimborsoMissioneDettagliDB.setDataSpesa(rimborsoMissioneDettagli.getDataSpesa());
		rimborsoMissioneDettagliDB.setDsSpesa(rimborsoMissioneDettagli.getDsSpesa());
		rimborsoMissioneDettagliDB.setTiSpesaDiaria(rimborsoMissioneDettagli.getTiSpesaDiaria());
		rimborsoMissioneDettagliDB.setDsTiSpesa(rimborsoMissioneDettagli.getDsTiSpesa());
		rimborsoMissioneDettagliDB.setNote(rimborsoMissioneDettagli.getNote());
		rimborsoMissioneDettagliDB.setFlSpesaAnticipata(rimborsoMissioneDettagli.getFlSpesaAnticipata());
		rimborsoMissioneDettagliDB.setKmPercorsi(rimborsoMissioneDettagli.getKmPercorsi());
		rimborsoMissioneDettagliDB.setCambio(rimborsoMissioneDettagli.getCambio());
		rimborsoMissioneDettagliDB.setCdDivisa(rimborsoMissioneDettagli.getCdDivisa());
		rimborsoMissioneDettagliDB.setImportoEuro(rimborsoMissioneDettagli.getImportoEuro());
		impostaImportoDivisa(rimborsoMissioneDettagliDB);
		
		rimborsoMissioneDettagliDB.setToBeUpdated();

		rimborsoMissioneDettagliDB = (RimborsoMissioneDettagli)crudServiceBean.modificaConBulk(principal, rimborsoMissioneDettagliDB);
    	
    	log.debug("Updated Information for Dettaglio Rimborso Missione: {}", rimborsoMissioneDettagliDB);
    	return rimborsoMissioneDettagli;
    }
    
}
