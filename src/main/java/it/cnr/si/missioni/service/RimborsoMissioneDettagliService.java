package it.cnr.si.missioni.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;

import javax.persistence.OptimisticLockException;

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
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoImpegni;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.RimborsoMissioneDettagliRepository;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.service.ValidaDettaglioRimborsoService;

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
	private DatiIstitutoService datiIstitutoService;

	@Autowired
	private OrdineMissioneService ordineMissioneService;

	@Autowired
	private MissioniCMISService missioniCMISService;

	@Autowired
	private ValidaDettaglioRimborsoService validaDettaglioRimborsoService;

	@Autowired
	private CMISRimborsoMissioneService cmisRimborsoMissioneService;


	@Autowired
	private RimborsoImpegniService rimborsoImpegniService;
	 
	@Autowired
	private CRUDComponentSession crudServiceBean;

	@Transactional(readOnly = true)
	public CMISFileAttachment uploadAllegato(Principal principal, Long idRimborsoMissioneDettagli,
			InputStream inputStream, String name, MimeTypes mimeTypes) throws ComponentException {
		RimborsoMissioneDettagli dettaglio = getRimborsoMissioneDettaglio(principal, idRimborsoMissioneDettagli);
		if (dettaglio != null) {
			rimborsoMissioneService.controlloAllegatoDettaglioModificabile(dettaglio.getRimborsoMissione());
			CMISFileAttachment attachment = cmisRimborsoMissioneService.uploadAttachmentDetail(principal, dettaglio,
					inputStream, name, mimeTypes);
			return attachment;
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<CMISFileAttachment> getAttachments(Principal principal, Long idRimborsoMissioneDettagli)
			throws ComponentException {
		if (idRimborsoMissioneDettagli != null) {
			List<CMISFileAttachment> lista = cmisRimborsoMissioneService.getAttachmentsDetail(principal, idRimborsoMissioneDettagli);
			return lista;
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli(Principal principal, Long idRimborsoMissione)
			throws ComponentException {
		RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById(principal,
				RimborsoMissione.class, idRimborsoMissione);

		if (rimborsoMissione != null) {
			List<RimborsoMissioneDettagli> lista = rimborsoMissioneDettagliRepository
					.getRimborsoMissioneDettagli(rimborsoMissione);
			return lista;
		}
		return null;
	}

	private void validaCRUD(Principal principal, RimborsoMissioneDettagli rimborsoMissioneDettagli)  throws ComponentException {
		RimborsoMissione rimborsoMissione = rimborsoMissioneDettagli.getRimborsoMissione();
		if (rimborsoMissioneDettagli.getKmPercorsi() != null){
	    	if (rimborsoMissione.getOrdineMissione() != null){
	        	OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, rimborsoMissione.getOrdineMissione().getId());
	        	if (ordineMissione != null){
	    			OrdineMissioneAutoPropria autoPropria = ordineMissioneService.getAutoPropria(ordineMissione);
	    			if (autoPropria != null && !Utility.nvl(autoPropria.utilizzoMotiviIspettivi,"N").equals("S")){
	    				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile utilizzare il rimborso kilometrico perchè in fase d'ordine di missione non è stata scelta per la richiesta auto propria il motivo di ispezione, verifica e controlli.");
	    			}
	        	}
	    	}
		} else {
//			if (rimborsoMissioneDettagli.isDettaglioPasto()){
//				GregorianCalendar calFrom = DateUtils.getDate(rimborsoMissione.getDataInizioMissione());
//				GregorianCalendar calTo = DateUtils.getDate(rimborsoMissione.getDataFineMissione());
//				if (!((calFrom.get(GregorianCalendar.YEAR) < calTo.get(GregorianCalendar.YEAR)) || 
//					(calFrom.get(GregorianCalendar.MONTH) < calTo.get(GregorianCalendar.MONTH))||
//					(calFrom.get(GregorianCalendar.DAY_OF_MONTH) < calTo.get(GregorianCalendar.DAY_OF_MONTH))||
//					(calTo.get(GregorianCalendar.HOUR_OF_DAY) - calFrom.get(GregorianCalendar.HOUR_OF_DAY) > 8) || 
//					(calTo.get(GregorianCalendar.HOUR_OF_DAY) - calFrom.get(GregorianCalendar.HOUR_OF_DAY) == 7 &&
//					calTo.get(GregorianCalendar.MINUTE) - calFrom.get(GregorianCalendar.MINUTE) >= 0))){
//					return true;
//				}
//			}
		}
		validaDettaglioRimborsoService.valida(rimborsoMissioneDettagli);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public RimborsoMissioneDettagli createRimborsoMissioneDettagli(Principal principal,
			RimborsoMissioneDettagli rimborsoMissioneDettagli) throws AwesomeException, ComponentException,
			OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {
		rimborsoMissioneDettagli.setUid(principal.getName());
		rimborsoMissioneDettagli.setUser(principal.getName());
		rimborsoMissioneDettagli.setStato(Costanti.STATO_INSERITO);
		if (rimborsoMissioneDettagli.getTiSpesaDiaria() == null) {
			rimborsoMissioneDettagli.setTiSpesaDiaria("S");
		}
		RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById(principal,
				RimborsoMissione.class, rimborsoMissioneDettagli.getRimborsoMissione().getId());
		if (rimborsoMissione != null) {
			rimborsoMissioneService.controlloOperazioniCRUDDaGui(rimborsoMissione);
		}
		rimborsoMissioneDettagli.setRimborsoMissione(rimborsoMissione);
		Long maxRiga = rimborsoMissioneDettagliRepository.getMaxRigaDettaglio(rimborsoMissione);
		if (maxRiga == null) {
			maxRiga = new Long(0);
		}
		maxRiga = maxRiga + 1;
		rimborsoMissioneDettagli.setRiga(maxRiga);
		rimborsoMissioneDettagli.setToBeCreated();
		controlloDatiObbligatoriDaGui(rimborsoMissioneDettagli);
		impostaImportoDivisa(rimborsoMissioneDettagli);
		validaCRUD(principal, rimborsoMissioneDettagli);
		aggiornaDatiImpegni(principal, rimborsoMissioneDettagli);

		rimborsoMissioneDettagli = (RimborsoMissioneDettagli) crudServiceBean.creaConBulk(principal,
				rimborsoMissioneDettagli);
		log.debug("Created Information for RimborsoMissioneDettagli: {}", rimborsoMissioneDettagli);
		return rimborsoMissioneDettagli;
	}

	public void aggiornaDatiImpegni(Principal principal, RimborsoMissioneDettagli rimborsoMissioneDettagli) {
		if (rimborsoMissioneDettagli.getIdRimborsoImpegni() != null) {
			RimborsoImpegni rimborsoImpegni = (RimborsoImpegni)crudServiceBean.findById(principal, RimborsoImpegni.class, rimborsoMissioneDettagli.getIdRimborsoImpegni());
			if (rimborsoMissioneDettagli.getIdRimborsoImpegni() != null) {
				rimborsoMissioneDettagli.setCdCdsObbligazione(rimborsoImpegni.getCdCdsObbligazione());
				rimborsoMissioneDettagli.setEsercizioObbligazione(rimborsoImpegni.getEsercizioObbligazione());
				rimborsoMissioneDettagli.setEsercizioOriginaleObbligazione(rimborsoImpegni.getEsercizioOriginaleObbligazione());
				rimborsoMissioneDettagli.setPgObbligazione(rimborsoImpegni.getPgObbligazione());
				rimborsoMissioneDettagli.setVoce(rimborsoImpegni.getVoce());
				rimborsoMissioneDettagli.setDsVoce(rimborsoImpegni.getDsVoce());
			}
		} else {
			rimborsoMissioneDettagli.setCdCdsObbligazione(null);
			rimborsoMissioneDettagli.setEsercizioObbligazione(null);
			rimborsoMissioneDettagli.setEsercizioOriginaleObbligazione(null);
			rimborsoMissioneDettagli.setPgObbligazione(null);
			rimborsoMissioneDettagli.setVoce(null);
			rimborsoMissioneDettagli.setDsVoce(null);
		}
	}

	private void controlloDatiObbligatoriDaGui(RimborsoMissioneDettagli dettaglio) {
		if (dettaglio != null) {
			if (StringUtils.isEmpty(dettaglio.getDataSpesa())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Data Spesa");
			} else if (StringUtils.isEmpty(dettaglio.getImportoEuro())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Importo Euro");
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void cancellaRimborsoMissioneDettagli(Principal principal, RimborsoMissione rimborsoMissione,
			Boolean deleteDocument) throws ComponentException {
		List<RimborsoMissioneDettagli> listaRimborsoMissioneDettagli = rimborsoMissioneDettagliRepository
				.getRimborsoMissioneDettagli(rimborsoMissione);
		if (listaRimborsoMissioneDettagli != null && !listaRimborsoMissioneDettagli.isEmpty()) {
			for (Iterator<RimborsoMissioneDettagli> iterator = listaRimborsoMissioneDettagli.iterator(); iterator
					.hasNext();) {
				RimborsoMissioneDettagli dettaglio = iterator.next();
				cancellaRimborsoMissioneDettagli(principal, dettaglio, deleteDocument);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteRimborsoMissioneDettagli(Principal principal, Long idRimborsoMissioneDettagli)
			throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException,
			BusyResourceException {
		RimborsoMissioneDettagli rimborsoMissioneDettagli = getRimborsoMissioneDettaglio(principal,
				idRimborsoMissioneDettagli);

		// effettuo controlli di validazione operazione CRUD
		if (rimborsoMissioneDettagli != null) {
			rimborsoMissioneService.controlloOperazioniCRUDDaGui(rimborsoMissioneDettagli.getRimborsoMissione());
			cancellaRimborsoMissioneDettagli(principal, rimborsoMissioneDettagli, true);
		}
	}

	public RimborsoMissioneDettagli getRimborsoMissioneDettaglio(Principal principal, Long idRimborsoMissioneDettagli) {
		RimborsoMissioneDettagli rimborsoMissioneDettagli = (RimborsoMissioneDettagli) crudServiceBean
				.findById(principal, RimborsoMissioneDettagli.class, idRimborsoMissioneDettagli);
		return rimborsoMissioneDettagli;
	}

	private void cancellaRimborsoMissioneDettagli(Principal principal,
			RimborsoMissioneDettagli rimborsoMissioneDettagli, Boolean deleteDocument) throws ComponentException {
		rimborsoMissioneDettagli.setToBeUpdated();
		rimborsoMissioneDettagli.setStato(Costanti.STATO_ANNULLATO);
		crudServiceBean.modificaConBulk(principal, rimborsoMissioneDettagli);
		if (deleteDocument) {
			cmisRimborsoMissioneService.deleteFolderRimborsoMissioneDettaglio(rimborsoMissioneDettagli);
		}
	}

	private void impostaImportoDivisa(RimborsoMissioneDettagli rimborsoMissioneDettagli) {
		if (rimborsoMissioneDettagli.getCambio().compareTo(BigDecimal.ONE) == 0) {
			rimborsoMissioneDettagli.setImportoDivisa(rimborsoMissioneDettagli.getImportoEuro());
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public RimborsoMissioneDettagli updateRimborsoMissioneDettagli(Principal principal,
			RimborsoMissioneDettagli rimborsoMissioneDettagli) throws AwesomeException, ComponentException,
			OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {

		RimborsoMissioneDettagli rimborsoMissioneDettagliDB = (RimborsoMissioneDettagli) crudServiceBean
				.findById(principal, RimborsoMissioneDettagli.class, rimborsoMissioneDettagli.getId());
		if (rimborsoMissioneDettagliDB == null)
			throw new AwesomeException(CodiciErrore.ERRGEN, "Dettaglio Rimborso Missione da aggiornare inesistente.");
		RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById(principal,
				RimborsoMissione.class, rimborsoMissioneDettagli.getRimborsoMissione().getId());
		if (rimborsoMissione != null && !rimborsoMissioneDettagli.isModificaSoloDatiFinanziari(rimborsoMissioneDettagliDB)) {
			rimborsoMissioneService.controlloOperazioniCRUDDaGui(rimborsoMissione);
		}
		rimborsoMissioneDettagli.setRimborsoMissione(rimborsoMissione);

		controlloDatiObbligatoriDaGui(rimborsoMissioneDettagli);

		rimborsoMissioneDettagliDB.setCdTiPasto(rimborsoMissioneDettagli.getCdTiPasto());
		rimborsoMissioneDettagliDB.setCdTiSpesa(rimborsoMissioneDettagli.getCdTiSpesa());
		rimborsoMissioneDettagliDB.setTiCdTiSpesa(rimborsoMissioneDettagli.getTiCdTiSpesa());
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
		rimborsoMissioneDettagliDB.setDsNoGiustificativo(rimborsoMissioneDettagli.getDsNoGiustificativo());
		rimborsoMissioneDettagliDB.setLocalitaSpostamento(rimborsoMissioneDettagli.getLocalitaSpostamento());
		rimborsoMissioneDettagliDB.setIdRimborsoImpegni(rimborsoMissioneDettagli.getIdRimborsoImpegni());
		impostaImportoDivisa(rimborsoMissioneDettagliDB);

		rimborsoMissioneDettagliDB.setToBeUpdated();

		validaCRUD(principal, rimborsoMissioneDettagliDB);

		aggiornaDatiImpegni(principal, rimborsoMissioneDettagliDB);

		rimborsoMissioneDettagliDB = (RimborsoMissioneDettagli) crudServiceBean.modificaConBulk(principal,
				rimborsoMissioneDettagliDB);

		log.debug("Updated Information for Dettaglio Rimborso Missione: {}", rimborsoMissioneDettagliDB);
		return rimborsoMissioneDettagliDB;
	}

}
