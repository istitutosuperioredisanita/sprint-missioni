package it.cnr.si.missioni.cmis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.flows.FlowResubmitType;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAnticipo;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.service.DatiIstitutoService;
import it.cnr.si.missioni.service.PrintRimborsoMissioneService;
import it.cnr.si.missioni.service.UoService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.object.Gae;
import it.cnr.si.missioni.util.proxy.json.object.Impegno;
import it.cnr.si.missioni.util.proxy.json.object.ImpegnoGae;
import it.cnr.si.missioni.util.proxy.json.object.Progetto;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;
import it.cnr.si.missioni.util.proxy.json.object.Voce;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.missioni.util.proxy.json.service.GaeService;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoGaeService;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoService;
import it.cnr.si.missioni.util.proxy.json.service.ProgettoService;
import it.cnr.si.missioni.util.proxy.json.service.UnitaOrganizzativaService;
import it.cnr.si.missioni.util.proxy.json.service.VoceService;

@Service
public class CMISRimborsoMissioneService {
	public static final String 
			PROPERTY_TIPOLOGIA_DOC = "wfcnr:tipologiaDOC",
			PROPERTY_TIPOLOGIA_DOC_SPECIFICA = "wfcnr:tipologiaDocSpecifica",
			PROPERTY_TIPOLOGIA_DOC_MISSIONI = "cnrmissioni:tipologiaDocumentoMissione";

	@Autowired
	private DatiIstitutoService datiIstitutoService;

    @Autowired
    private Environment env;

	@Autowired
	private UnitaOrganizzativaService unitaOrganizzativaService;

	@Autowired
	private GaeService gaeService;

	@Autowired
	private ProgettoService progettoService;

	@Autowired
	private ImpegnoService impegnoService;

	@Autowired
	private ImpegnoGaeService impegnoGaeService;

	@Autowired
	private VoceService voceService;

	@Autowired
	private UoService uoService;

	@Autowired
	private MissioniCMISService missioniCMISService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CRUDComponentSession<OrdineMissione> crudServiceBean;
	
	@Autowired
	private PrintRimborsoMissioneService printRimborsoMissioneService;

	public List<CMISFileAttachment> getAttachmentsDetail(Principal principal, Long idDettagliorimborso) throws ComponentException{
		ItemIterable<CmisObject> children = getAttachmentsDetailRimborso(principal, idDettagliorimborso);
		if (children != null){
	        List<CMISFileAttachment> lista = new ArrayList<CMISFileAttachment>();
	        for (CmisObject object : children){
	        	Document doc = (Document)object;
	        	CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
	        	cmisFileAttachment.setNomeFile(doc.getPropertyValue(PropertyIds.NAME));
	        	cmisFileAttachment.setId(doc.getId());
	        	cmisFileAttachment.setIdMissione(idDettagliorimborso);
	        	lista.add(cmisFileAttachment);
	        }
	        return lista;
		}
		return null;
	}
		
	public ItemIterable<CmisObject> getAttachmentsDetailRimborso(Principal principal, Long idDettagliorimborso) throws ComponentException{
		Folder folder = getFolderDettaglioRimborso(idDettagliorimborso);
		if (folder != null){
	        ItemIterable<CmisObject> children = ((Folder) folder).getChildren();
	        return children;
		}
		return null;
	}
		
	public void deleteFolderRimborsoMissioneDettaglio(RimborsoMissioneDettagli dettaglio) throws ComponentException{
		Folder folder = getFolderDettaglioRimborso(new Long (dettaglio.getId().toString()));
		if (folder != null){
        	missioniCMISService.deleteNode(folder);
		}
	}
	
	public void deleteFolderRimborsoMissione(RimborsoMissione rimborso) throws ComponentException{
		Folder folder = getFolderRimborso(rimborso);
		if (folder != null){
        	missioniCMISService.deleteNode(folder);
		}
	}
	
	public CMISRimborsoMissione create(Principal principal, RimborsoMissione rimborsoMissione) throws ComponentException{
		CMISRimborsoMissione cmisRimborsoMissione = new CMISRimborsoMissione();
		caricaDatiDerivati(principal, rimborsoMissione);

		if (rimborsoMissione != null && rimborsoMissione.getOrdineMissione() != null){
			OrdineMissione ordineMissione = rimborsoMissione.getOrdineMissione();
	    	OrdineMissione ordineMissioneDB = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, ordineMissione.getId());
	    	if (ordineMissioneDB != null){
	    		ordineMissione = ordineMissioneDB;
	    	}
		}

		String username = principal.getName();
		
		Account account = accountService.loadAccountFromRest(rimborsoMissione.getUid());
		Progetto progetto = progettoService.loadModulo(rimborsoMissione.getPgProgetto(), rimborsoMissione.getAnno(), null);
		Voce voce = voceService.loadVoce(rimborsoMissione);
		Gae gae = gaeService.loadGae(rimborsoMissione);
		UnitaOrganizzativa uoCompetenza = null;
		if (rimborsoMissione.getUoCompetenza() != null){
			uoCompetenza = unitaOrganizzativaService.loadUo(rimborsoMissione.getUoCompetenza(), null, rimborsoMissione.getAnno());
		}
		UnitaOrganizzativa uoSpesa = unitaOrganizzativaService.loadUo(rimborsoMissione.getUoSpesa(), null, rimborsoMissione.getAnno());
		UnitaOrganizzativa uoRich = unitaOrganizzativaService.loadUo(rimborsoMissione.getUoRich(), null, rimborsoMissione.getAnno());
		String descrImpegno = ""; 
		BigDecimal dispImpegno = null;
		if (rimborsoMissione.getPgObbligazione() != null){
			if (gae != null){
				ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(rimborsoMissione);
				descrImpegno = impegnoGae.getDsObbligazione();
				dispImpegno = impegnoGae.getDisponibilitaImpegno();
			} else {
				Impegno impegno = impegnoService.loadImpegno(rimborsoMissione);
				descrImpegno = impegno.getDsObbligazione();
				dispImpegno = impegno.getDisponibilitaImpegno();
			}
		}

		String uoCompetenzaPerFlusso = Utility.replace(rimborsoMissione.getUoCompetenza(), ".", "");
		String uoSpesaPerFlusso = Utility.replace(rimborsoMissione.getUoSpesa(), ".", "");
		String uoRichPerFlusso = Utility.replace(rimborsoMissione.getUoRich(), ".", "");
		Uo uoDatiSpesa = uoService.recuperoUo(uoSpesaPerFlusso);
		String userNameFirmatario = null;
		String userNameFirmatarioSpesa = null;
		if (isDevProfile()){
			userNameFirmatario = recuperoUidDirettoreUo(uoRichPerFlusso);
		} else {
			userNameFirmatario = accountService.getDirector(uoRichPerFlusso);		
		}
		
		if (uoDatiSpesa != null && uoDatiSpesa.getFirmaSpesa() != null && uoDatiSpesa.getFirmaSpesa().equals("N")){
			if (uoCompetenzaPerFlusso != null){
				if (isDevProfile()){
					userNameFirmatarioSpesa = recuperoUidDirettoreUo(uoCompetenzaPerFlusso);
				} else {
					userNameFirmatarioSpesa = accountService.getDirector(uoCompetenzaPerFlusso);
				}
			} else {
				userNameFirmatarioSpesa = userNameFirmatario;
			}
		} else {
			if (isDevProfile()){
				userNameFirmatarioSpesa = recuperoUidDirettoreUo(uoSpesaPerFlusso);
			} else {
				userNameFirmatarioSpesa = accountService.getDirector(uoSpesaPerFlusso);
			}
		}
		
		GregorianCalendar dataScadenzaFlusso = new GregorianCalendar();
		dataScadenzaFlusso.setTime(DateUtils.getCurrentTime());
		dataScadenzaFlusso.add(Calendar.DATE, 14);
		dataScadenzaFlusso.add(Calendar.MONTH, 1);


		cmisRimborsoMissione.setAnno(rimborsoMissione.getAnno().toString());
		cmisRimborsoMissione.setNumero(rimborsoMissione.getNumero().toString());
		cmisRimborsoMissione.setCapitolo(rimborsoMissione.getVoce());
		cmisRimborsoMissione.setDescrizioneCapitolo(voce == null ? "" : voce.getDs_elemento_voce());
		cmisRimborsoMissione.setDescrizioneGae(gae == null ? "" : gae.getDs_linea_attivita());
		cmisRimborsoMissione.setDescrizioneImpegno(descrImpegno);
		cmisRimborsoMissione.setDescrizioneUoOrdine(uoRich == null ? "" : uoRich.getDs_unita_organizzativa());
		cmisRimborsoMissione.setDescrizioneUoSpesa(uoSpesa == null ? "" : uoSpesa.getDs_unita_organizzativa());
		cmisRimborsoMissione.setDescrizioneUoCompetenza(uoCompetenza == null ? "" : uoCompetenza.getDs_unita_organizzativa());
		cmisRimborsoMissione.setDisponibilita(dispImpegno);
		cmisRimborsoMissione.setGae(gae == null ? "" : gae.getCd_linea_attivita());
		cmisRimborsoMissione.setImpegnoAnnoCompetenza(rimborsoMissione.getEsercizioObbligazione() == null ? null : new Long(rimborsoMissione.getEsercizioObbligazione()));
		cmisRimborsoMissione.setImpegnoAnnoResiduo(rimborsoMissione.getEsercizioOriginaleObbligazione() == null ? null : new Long(rimborsoMissione.getEsercizioOriginaleObbligazione()));
		cmisRimborsoMissione.setImpegnoNumero(rimborsoMissione.getPgObbligazione());
		cmisRimborsoMissione.setNoleggioFlag(rimborsoMissione.getUtilizzoAutoNoleggio().equals("S") ? "true" : "false");
		cmisRimborsoMissione.setNote(rimborsoMissione.getNote() == null ? "" : rimborsoMissione.getNote());
		cmisRimborsoMissione.setOggetto(rimborsoMissione.getOggetto());
		cmisRimborsoMissione.setTaxiFlag(rimborsoMissione.getUtilizzoTaxi().equals("S") ? "true" : "false");
		cmisRimborsoMissione.setUoOrdine(uoRichPerFlusso);
		cmisRimborsoMissione.setUoSpesa(uoSpesaPerFlusso);
		cmisRimborsoMissione.setUoCompetenza(uoCompetenzaPerFlusso == null ? "" : uoCompetenzaPerFlusso);
		cmisRimborsoMissione.setUserNameFirmatarioSpesa(userNameFirmatarioSpesa);
		cmisRimborsoMissione.setUserNamePrimoFirmatario(userNameFirmatario);
		cmisRimborsoMissione.setUserNameResponsabileModulo("");
		cmisRimborsoMissione.setUsernameRichiedente(username);
		cmisRimborsoMissione.setAnticipoRicevuto(rimborsoMissione.getAnticipoRicevuto().equals("S") ? "true" : "false");
		cmisRimborsoMissione.setAnnoMandato(rimborsoMissione.getAnticipoAnnoMandato() == null ? "" : rimborsoMissione.getAnticipoAnnoMandato().toString());
		cmisRimborsoMissione.setNumeroMandato(rimborsoMissione.getAnticipoNumeroMandato() == null ? "" : rimborsoMissione.getAnticipoNumeroMandato().toString());
		cmisRimborsoMissione.setImportoMandato(rimborsoMissione.getAnticipoImporto() == null ? "" : rimborsoMissione.getAnticipoImporto().toString());
		cmisRimborsoMissione.setUsernameUtenteOrdine(rimborsoMissione.getUid());
		cmisRimborsoMissione.setUsernameUtenteOrdine(rimborsoMissione.getUid());
		cmisRimborsoMissione.setValidazioneSpesa(impostaValidazioneSpesa(userNameFirmatario, userNameFirmatarioSpesa));
		cmisRimborsoMissione.setWfDescription("Rimborso Missione n. "+rimborsoMissione.getNumero()+" di "+account.getCognome() + " "+account.getNome());
		cmisRimborsoMissione.setWfDueDate(DateUtils.getDateAsString(dataScadenzaFlusso.getTime(), DateUtils.PATTERN_DATE_FOR_DOCUMENTALE));
		cmisRimborsoMissione.setDestinazione(rimborsoMissione.getDestinazione());
		cmisRimborsoMissione.setTrattamento(rimborsoMissione.decodeTrattamento());
		cmisRimborsoMissione.setMissioneEsteraFlag(rimborsoMissione.getTipoMissione().equals("E") ? "true" : "false");
		cmisRimborsoMissione.setDataInizioMissione(DateUtils.getDateAsString(rimborsoMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
		cmisRimborsoMissione.setDataFineMissione(DateUtils.getDateAsString(rimborsoMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
		if (rimborsoMissione.getDataInizioEstero() != null){
			cmisRimborsoMissione.setDataInizioEstero(DateUtils.getDateAsString(rimborsoMissione.getDataInizioEstero(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
		}
		if (rimborsoMissione.getDataFineEstero() != null){
			cmisRimborsoMissione.setDataFineEstero(DateUtils.getDateAsString(rimborsoMissione.getDataFineEstero(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
		}
		cmisRimborsoMissione.setIdOrdineMissione(rimborsoMissione.getOrdineMissione() == null ? "" :rimborsoMissione.getOrdineMissione().getId().toString());
		cmisRimborsoMissione.setWfOrdineMissione(rimborsoMissione.getOrdineMissione() == null || rimborsoMissione.getOrdineMissione().getIdFlusso() == null ? "" : rimborsoMissione.getOrdineMissione().getIdFlusso());
		return cmisRimborsoMissione;
	}
	
	private boolean isDevProfile(){
   		if (env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT)) {
   			return true;
   		}
   		return false;
	}
	private String impostaValidazioneSpesa(String userNameFirmatario, String userNameFirmatarioSpesa){
		if (userNameFirmatario != null && userNameFirmatarioSpesa != null && userNameFirmatario.equals(userNameFirmatarioSpesa)){
			return "false";
		}
		return "true";
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	private void caricaDatiDerivati(Principal principal, RimborsoMissione rimborsoMissione) throws ComponentException {
		if (rimborsoMissione != null){
			DatiIstituto dati = datiIstitutoService.getDatiIstituto(rimborsoMissione.getCdsSpesa(), rimborsoMissione.getAnno());
			if (dati == null){
				dati = datiIstitutoService.creaDatiIstitutoOrdine(principal, rimborsoMissione.getCdsSpesa(), rimborsoMissione.getAnno());
			}
			rimborsoMissione.setDatiIstituto(dati);
			if (rimborsoMissione.getDatiIstituto() == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non esistono i dati per istituto per il codice "+rimborsoMissione.getCdsSpesa()+" nell'anno "+rimborsoMissione.getAnno());
			}
		}
	}

	@Transactional(readOnly = true)
	public Document salvaStampaRimborsoMissioneSuCMIS(Principal principal, byte[] stampa, RimborsoMissione rimborsoMissione) throws ComponentException {
		CMISRimborsoMissione cmisRimborsoMissione = create(principal, rimborsoMissione);
		return salvaStampaRimborsoMissioneSuCMIS(principal, stampa, rimborsoMissione, cmisRimborsoMissione);
	}
	
	public CmisPath createFolderRimborsoMissione(RimborsoMissione rimborsoMissione){
		CmisPath cmisPath = missioniCMISService.getBasePath();
		cmisPath = missioniCMISService.createFolderIfNotPresent(cmisPath, rimborsoMissione.getUoRich());
		cmisPath = missioniCMISService.createFolderIfNotPresent(cmisPath, "Rimborso Missione");
		cmisPath = missioniCMISService.createFolderIfNotPresent(cmisPath, "Anno "+rimborsoMissione.getAnno());
		cmisPath = createLastFolderIfNotPresent(cmisPath, rimborsoMissione);
		return cmisPath;
	}

	public CmisPath createLastFolderIfNotPresent(CmisPath cmisPath, RimborsoMissione rimborsoMissione){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		String name = rimborsoMissione.constructCMISNomeFile();
		String folderName = name;
		folderName = missioniCMISService.sanitizeFolderName(folderName);
		metadataProperties.put(PropertyIds.OBJECT_TYPE_ID, OrdineMissione.CMIS_PROPERTY_MAIN);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename(name));
		metadataProperties.put(PropertyIds.NAME, missioniCMISService.sanitizeFilename(rimborsoMissione.constructCMISNomeFile()));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename(name));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_NUMERO, rimborsoMissione.getNumero());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_ANNO, rimborsoMissione.getAnno());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_ID, rimborsoMissione.getId());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_UID, rimborsoMissione.getUid());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_OGGETTO, rimborsoMissione.getOggetto());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_DESTINAZIONE, rimborsoMissione.getDestinazione());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_NOTE, rimborsoMissione.getNote());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_DATA_INIZIO, rimborsoMissione.getDataInizioMissione());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_DATA_FINE, rimborsoMissione.getDataFineMissione());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_DATA_INSERIMENTO, rimborsoMissione.getDataInserimento());
		metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_NAME_DATA_INIZIO_MISSIONE_ESTERO, rimborsoMissione.getDataInizioEstero());
		metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_NAME_DATA_FINE_MISSIONE_ESTERO, rimborsoMissione.getDataFineEstero());
		metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_NAME_ID_ORDINE_MISSIONE, rimborsoMissione.getOrdineMissione().getId());
		List<String> aspectsToAdd = new ArrayList<String>();
		aspectsToAdd.add(MissioniCMISService.ASPECT_TITLED);
		aspectsToAdd.add(CMISMissioniAspect.RIMBORSO_MISSIONE_ASPECT.value());
		cmisPath = missioniCMISService.createFolderIfNotPresent(cmisPath, metadataProperties, aspectsToAdd, folderName);
		return cmisPath;
	}
	
	private CmisPath createLastFolderDettaglioIfNotPresent(CmisPath cmisPath, RimborsoMissioneDettagli dettaglio){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		String name = dettaglio.constructCMISNomeFile();
		String folderName = name;
		folderName = missioniCMISService.sanitizeFolderName(folderName);
		metadataProperties.put(PropertyIds.OBJECT_TYPE_ID, RimborsoMissioneDettagli.CMIS_PROPERTY_MAIN);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename(name));
		metadataProperties.put(PropertyIds.NAME, missioniCMISService.sanitizeFilename(name));
		metadataProperties.put(RimborsoMissioneDettagli.CMIS_PROPERTY_ID_DETTAGLIO_RIMBORSO, dettaglio.getId());
		metadataProperties.put(RimborsoMissioneDettagli.CMIS_PROPERTY_CD_TIPO_SPESA_DETTAGLIO_RIMBORSO_MISSIONE, dettaglio.getCdTiSpesa());
		metadataProperties.put(RimborsoMissioneDettagli.CMIS_PROPERTY_DS_TIPO_SPESA_DETTAGLIO_RIMBORSO_MISSIONE, dettaglio.getDsTiSpesa());
		metadataProperties.put(RimborsoMissioneDettagli.CMIS_PROPERTY_DATA_SPESA_DETTAGLIO_RIMBORSO_MISSIONE, dettaglio.getDataSpesa());
		metadataProperties.put(RimborsoMissioneDettagli.CMIS_PROPERTY_RIGA_DETTAGLIO_RIMBORSO_MISSIONE, dettaglio.getRiga());
		List<String> aspectsToAdd = new ArrayList<String>();
		aspectsToAdd.add(MissioniCMISService.ASPECT_TITLED);
		cmisPath = missioniCMISService.createFolderIfNotPresent(cmisPath, metadataProperties, aspectsToAdd, folderName);
		return cmisPath;
	}
	
	private Document salvaStampaRimborsoMissioneSuCMIS(Principal principal,
			byte[] stampa, RimborsoMissione rimborsoMissione,
			CMISRimborsoMissione cmisRimborsoMissione) {
		InputStream streamStampa = new ByteArrayInputStream(stampa);
		CmisPath cmisPath = createFolderRimborsoMissione(rimborsoMissione);
		Map<String, Object> metadataProperties = createMetadataForFileRimborsoMissione(principal.getName(), cmisRimborsoMissione);
		try{
			Document node = missioniCMISService.restoreSimpleDocument(
					metadataProperties,
					streamStampa,
					MimeTypes.PDF.mimetype(),
					rimborsoMissione.getFileName(), 
					cmisPath);
			missioniCMISService.addAspect(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_RIMBORSO.value());
			missioniCMISService.makeVersionable(node);
			return node;
		} catch (Exception e) {
			if (e.getCause() instanceof CmisConstraintException)
				throw new AwesomeException(CodiciErrore.ERRGEN, "CMIS - File ["+rimborsoMissione.getFileName()+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!");
			throw new AwesomeException(CodiciErrore.ERRGEN, "CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")");
		}
	}

	private Folder getFolderRimborso(RimborsoMissione rimborso){
		StringBuffer query = new StringBuffer("select rim.cmis:objectId from missioni:main missioni join missioni_commons_aspect:rimborso_missione rim on missioni.cmis:objectId = rim.cmis:objectId ");
		query.append(" where missioni.missioni:id = ").append(rimborso.getId());
		ItemIterable<QueryResult> resultsFolder = missioniCMISService.search(query);
		if (resultsFolder.getTotalNumItems() == 0)
			return null;
		else if (resultsFolder.getTotalNumItems() > 1){
			throw new AwesomeException("Errore di sistema, esistono sul documentale piu' cartelle per lo stesso rimborso missione.  Anno:"+ rimborso.getAnno()+ " cds:" +rimborso.getCdsRich() +" numero:"+rimborso.getNumero());
		} else {
			for (QueryResult queryResult : resultsFolder) {
				return (Folder) missioniCMISService.getNodeByNodeRef((String) queryResult.getPropertyValueById(PropertyIds.OBJECT_ID));
			}
		}
		return null;
	}
	
	private Folder getFolderDettaglioRimborso(Long idDettagliorimborso){
		StringBuffer query = new StringBuffer("select cmis:objectId from missioni_rimborso_dettaglio:main ");
		query.append(" where missioni_rimborso_dettaglio:id = ").append(idDettagliorimborso);
		ItemIterable<QueryResult> resultsFolder = missioniCMISService.search(query);
		if (resultsFolder.getTotalNumItems() == 0)
			return null;
		else if (resultsFolder.getTotalNumItems() > 1){
			throw new AwesomeException("Errore di sistema, esistono sul documentale piu' cartelle per lo stesso dettaglio di rimborso missione.  Id:"+ idDettagliorimborso);
		} else {
			for (QueryResult queryResult : resultsFolder) {
				return (Folder) missioniCMISService.getNodeByNodeRef((String) queryResult.getPropertyValueById(PropertyIds.OBJECT_ID));
			}
		}
		return null;
	}
	
	public CMISFileAttachment uploadAttachmentDetail(Principal principal, RimborsoMissioneDettagli rimborsoMissioneDettagli, InputStream inputStream, String name, MimeTypes mimeTypes){
		Document doc = salvaAllegatoRimborsoMissioneDettaglioCMIS(principal, rimborsoMissioneDettagli, inputStream, name, mimeTypes);
		if (doc != null){
			CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
			cmisFileAttachment.setId(doc.getId());
			cmisFileAttachment.setNomeFile(name);
	        cmisFileAttachment.setIdMissione(new Long(rimborsoMissioneDettagli.getId().toString()));
			return cmisFileAttachment;
		}
		return null;
	}
	
	private Document salvaAllegatoRimborsoMissioneDettaglioCMIS(Principal principal,
			RimborsoMissioneDettagli dettaglio, InputStream stream, String fileName,MimeTypes mimeTypes) {
		
		Folder folder = (Folder) getFolderRimborso(dettaglio.getRimborsoMissione());
		CmisPath cmisPath = null;
		if (folder == null){
			cmisPath = createFolderRimborsoMissione(dettaglio.getRimborsoMissione());
		} else {
			cmisPath = CmisPath.construct(folder.getPath());
		}

		cmisPath = createLastFolderDettaglioIfNotPresent(cmisPath, dettaglio);

		Map<String, Object> metadataProperties = createMetadataForFileRimborsoMissioneDettaglio(principal.getName(), dettaglio, fileName, RimborsoMissione.CMIS_PROPERTY_NAME_TIPODOC_SCONTRINO);
		try{
			Document node = missioniCMISService.restoreSimpleDocument(
					metadataProperties,
					stream,
					mimeTypes.mimetype(),
					fileName, 
					cmisPath);
			missioniCMISService.addAspect(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_SCONTRINI.value());
			missioniCMISService.makeVersionable(node);
			return node;
		} catch (Exception e) {
			if (e.getCause() instanceof CmisConstraintException)
				throw new AwesomeException(CodiciErrore.ERRGEN, "CMIS - File ["+fileName+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!");
			throw new AwesomeException(CodiciErrore.ERRGEN, "CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")");
		}
	}

	private String recuperoUidDirettoreUo(String codiceUo){
		Uo uo = uoService.recuperoUo(codiceUo);
		return recuperoUidDirettoreUo(codiceUo, uo);
	}

	private String recuperoUidDirettoreUo(String codiceUo, Uo uo) {
		if (uo != null && uo.getCodiceUo() != null && uo.getCodiceUo().equals(codiceUo)){
			return uo.getUidDirettore();
		}
		return null;
	}


	public Map<String, Object> createMetadataForFileRimborsoMissioneDettaglio(String currentLogin, RimborsoMissioneDettagli dettaglio, String fileName, String tipoDocumento){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(PropertyIds.OBJECT_TYPE_ID, RimborsoMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename(fileName));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename(fileName));
		metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, OrdineMissione.CMIS_PROPERTY_NAME_DOC_ALLEGATO);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, tipoDocumento);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, tipoDocumento);

		return metadataProperties;
	}
	public Map<String, Object> createMetadataForFileRimborsoMissione(String currentLogin, CMISRimborsoMissione cmisRimborsoMissione){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(PropertyIds.OBJECT_TYPE_ID, RimborsoMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename("Rimborso Missione - anno "+cmisRimborsoMissione.getAnno()+" numero "+cmisRimborsoMissione.getNumero()));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename("Rimborso di Missione"));
		metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, OrdineMissione.CMIS_PROPERTY_NAME_DOC_ORDINE);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, RimborsoMissione.CMIS_PROPERTY_VALUE_TIPODOC_RIMBORSO);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, RimborsoMissione.CMIS_PROPERTY_VALUE_TIPODOC_RIMBORSO);

		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_ANNO_IMPEGNO_COMP, cmisRimborsoMissione.getImpegnoAnnoCompetenza());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_ANNO_IMPEGNO_RES, cmisRimborsoMissione.getImpegnoAnnoResiduo());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_CAPITOLO, cmisRimborsoMissione.getCapitolo());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE, cmisRimborsoMissione.getOggetto());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_CAPITOLO, cmisRimborsoMissione.getDescrizioneCapitolo());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_GAE, cmisRimborsoMissione.getDescrizioneGae());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_IMPEGNO, cmisRimborsoMissione.getDescrizioneImpegno());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_UO_ORDINE, cmisRimborsoMissione.getDescrizioneUoOrdine());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_UO_SPESA, cmisRimborsoMissione.getDescrizioneUoSpesa());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DISPONIBILITA_IMPEGNO, cmisRimborsoMissione.getDisponibilita());
//		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DUE_DATE, cmisOrdineMissione.getWfDueDate());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_GAE, cmisRimborsoMissione.getGae());
//		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_IMPORTO_MISSIONE, cmisRimborsoMissione.getImportoMissione());
//		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_NOLEGGIO, cmisRimborsoMissione.getNoleggioFlag().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_NOTE, cmisRimborsoMissione.getNote());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_NUMERO_IMPEGNO, cmisRimborsoMissione.getImpegnoNumero());
//		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_PRIORITY, cmisOrdineMissione.getPriorita());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_TAXI, cmisRimborsoMissione.getTaxiFlag().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESTINAZIONE, cmisRimborsoMissione.getDestinazione());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_ESTERA_FLAG, cmisRimborsoMissione.getMissioneEsteraFlag().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DATA_INIZIO_MISSIONE, cmisRimborsoMissione.getDataInizioMissione());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DATA_FINE_MISSIONE, cmisRimborsoMissione.getDataFineMissione());
		metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_FLOW_ID_FLOW_ORDINE, cmisRimborsoMissione.getWfOrdineMissione());
		metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_FLOW_ANTICIPO_RICEVUTO, cmisRimborsoMissione.getAnticipoRicevuto().equals("true"));
		metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_FLOW_ANNO_MANDATO, cmisRimborsoMissione.getAnnoMandato().equals("") ? null : new Integer(cmisRimborsoMissione.getAnnoMandato()));
		metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_FLOW_NUMERO_MANDATO, cmisRimborsoMissione.getNumeroMandato().equals("") ? null : new Integer(cmisRimborsoMissione.getNumeroMandato()));
		metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_FLOW_IMPORTO_MANDATO, cmisRimborsoMissione.getImportoMandato().equals("") ? null : new Float(cmisRimborsoMissione.getImportoMandato()));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_TRATTAMENTO, cmisRimborsoMissione.getTrattamento());

		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_UO_ORDINE, cmisRimborsoMissione.getUoOrdine());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_UO_SPESA, cmisRimborsoMissione.getUoSpesa());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_FIRMA_SPESA, cmisRimborsoMissione.getUserNameFirmatarioSpesa());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_FIRMA_UO, cmisRimborsoMissione.getUserNamePrimoFirmatario());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_ORDINE, cmisRimborsoMissione.getUsernameUtenteOrdine());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_RICHIEDENTE, cmisRimborsoMissione.getUsernameRichiedente());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_VALIDAZIONE_SPESA, cmisRimborsoMissione.getValidazioneSpesa().equals("true"));
		return metadataProperties;
	}
	
	
	@Transactional(readOnly = true)
	public void avviaFlusso(Principal principal, RimborsoMissione rimborsoMissione) throws ComponentException {
		String username = principal.getName();
		byte[] stampa = printRimborsoMissioneService.printRimborsoMissione(rimborsoMissione, username);
		CMISRimborsoMissione cmisRimborsoMissione = create(principal, rimborsoMissione);
		Document documento = salvaStampaRimborsoMissioneSuCMIS(principal, stampa, rimborsoMissione, cmisRimborsoMissione);
		StringBuffer nodeRefs = new StringBuffer();
		if (rimborsoMissione.isStatoNonInviatoAlFlusso()){
			String nodeRefFirmatario = missioniCMISService.recuperoNodeRefUtente(cmisRimborsoMissione.getUserNamePrimoFirmatario());

			StringWriter stringWriter = new StringWriter();
			JsonFactory jsonFactory = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(jsonFactory); 
			try {
				 JsonGenerator jGenerator = jsonFactory.createJsonGenerator(stringWriter);
				 jGenerator.writeStartObject();
				 jGenerator.writeStringField("assoc_bpm_assignee_added" , nodeRefFirmatario);
				 jGenerator.writeStringField("assoc_bpm_assignee_removed" , "");
				 aggiungiDocumento(documento, nodeRefs);
				 
				 jGenerator.writeStringField("assoc_packageItems_added" , nodeRefs.toString());
				 jGenerator.writeStringField("assoc_packageItems_removed" , "");
				 jGenerator.writeStringField("prop_bpm_comment" , "");
				 jGenerator.writeStringField("prop_cnrmissioni_descrizioneOrdine" , cmisRimborsoMissione.getOggetto());
				 jGenerator.writeStringField("prop_cnrmissioni_note" , cmisRimborsoMissione.getNote());
				 jGenerator.writeStringField("prop_bpm_workflowDescription" , cmisRimborsoMissione.getWfDescription());
				 jGenerator.writeStringField("prop_bpm_sendEMailNotifications" , "false");
				 jGenerator.writeStringField("prop_bpm_workflowDueDate" , cmisRimborsoMissione.getWfDueDate());
				 jGenerator.writeStringField("prop_bpm_percentComplete" , "0");
				 jGenerator.writeStringField("prop_bpm_status" , "Not Yet Started");
				 jGenerator.writeStringField("prop_bpm_workflowPriority" , Utility.nvl(cmisRimborsoMissione.getPriorita(),Costanti.PRIORITA_MEDIA));
				 jGenerator.writeStringField("prop_wfcnr_groupName" , "GENERICO");
				 jGenerator.writeStringField("prop_wfcnr_wfCounterIndex" , "");
				 jGenerator.writeStringField("prop_wfcnr_wfCounterId" , "");
				 jGenerator.writeStringField("prop_wfcnr_wfCounterAnno" , "");
				 jGenerator.writeStringField("prop_cnrmissioni_validazioneSpesaFlag" , cmisRimborsoMissione.getValidazioneSpesa());
				 jGenerator.writeStringField("prop_cnrmissioni_userNameUtenteOrdineMissione" , cmisRimborsoMissione.getUsernameUtenteOrdine());
				 jGenerator.writeStringField("prop_cnrmissioni_userNameRichiedente" , cmisRimborsoMissione.getUsernameRichiedente());
				 jGenerator.writeStringField("prop_cnrmissioni_userNamePrimoFirmatario" , cmisRimborsoMissione.getUserNamePrimoFirmatario());
				 jGenerator.writeStringField("prop_cnrmissioni_userNameFirmatarioSpesa" , cmisRimborsoMissione.getUserNameFirmatarioSpesa());
				 jGenerator.writeStringField("prop_cnrmissioni_uoOrdine" , cmisRimborsoMissione.getUoOrdine());
				 jGenerator.writeStringField("prop_cnrmissioni_descrizioneUoOrdine" , cmisRimborsoMissione.getDescrizioneUoOrdine());
				 jGenerator.writeStringField("prop_cnrmissioni_uoSpesa" , cmisRimborsoMissione.getUoSpesa());
				 jGenerator.writeStringField("prop_cnrmissioni_descrizioneUoSpesa" , cmisRimborsoMissione.getDescrizioneUoSpesa());
				 jGenerator.writeStringField("prop_cnrmissioni_uoCompetenza" , cmisRimborsoMissione.getUoCompetenza());
				 jGenerator.writeStringField("prop_cnrmissioni_descrizioneUoCompetenza" , cmisRimborsoMissione.getDescrizioneUoCompetenza());
				 jGenerator.writeStringField("prop_cnrmissioni_capitolo" , cmisRimborsoMissione.getCapitolo());
				 jGenerator.writeStringField("prop_cnrmissioni_descrizioneCapitolo" , cmisRimborsoMissione.getDescrizioneCapitolo());
				 jGenerator.writeStringField("prop_cnrmissioni_gae" , cmisRimborsoMissione.getGae());
				 jGenerator.writeStringField("prop_cnrmissioni_descrizioneGae" , cmisRimborsoMissione.getDescrizioneGae());
				 jGenerator.writeStringField("prop_cnrmissioni_impegnoAnnoResiduo" , cmisRimborsoMissione.getImpegnoAnnoResiduo() == null ? "": cmisRimborsoMissione.getImpegnoAnnoResiduo().toString());
				 jGenerator.writeStringField("prop_cnrmissioni_impegnoAnnoCompetenza" , cmisRimborsoMissione.getImpegnoAnnoCompetenza() == null ? "": cmisRimborsoMissione.getImpegnoAnnoCompetenza().toString());
				 jGenerator.writeStringField("prop_cnrmissioni_impegnoNumero" , cmisRimborsoMissione.getImpegnoNumero() == null ? "": cmisRimborsoMissione.getImpegnoNumero().toString());
				 jGenerator.writeStringField("prop_cnrmissioni_descrizioneImpegno" , cmisRimborsoMissione.getDescrizioneImpegno());
				 jGenerator.writeStringField("prop_cnrmissioni_importoMissione" , cmisRimborsoMissione.getImportoMissione() == null ? "": cmisRimborsoMissione.getImportoMissione().toString());
				 jGenerator.writeStringField("prop_cnrmissioni_disponibilita" , cmisRimborsoMissione.getDisponibilita() == null ? "": cmisRimborsoMissione.getDisponibilita().toString());
				 jGenerator.writeStringField("prop_cnrmissioni_missioneEsteraFlag" , cmisRimborsoMissione.getMissioneEsteraFlag());
				 jGenerator.writeStringField("prop_cnrmissioni_destinazione" , cmisRimborsoMissione.getDestinazione());
				 jGenerator.writeStringField("prop_cnrmissioni_dataInizioMissione" , cmisRimborsoMissione.getDataInizioMissione());
				 jGenerator.writeStringField("prop_cnrmissioni_dataFineMissione" , cmisRimborsoMissione.getDataFineMissione());
				 jGenerator.writeStringField("prop_cnrmissioni_trattamento" , cmisRimborsoMissione.getTrattamento());
				 jGenerator.writeStringField("prop_cnrmissioni_dataInizioEstero" , cmisRimborsoMissione.getDataInizioEstero());
				 jGenerator.writeStringField("prop_cnrmissioni_dataFineEstero" , cmisRimborsoMissione.getDataFineEstero());
				 jGenerator.writeStringField("prop_cnrmissioni_anticipoRicevuto" , cmisRimborsoMissione.getAnticipoRicevuto());
				 jGenerator.writeStringField("prop_cnrmissioni_annoMandato" , cmisRimborsoMissione.getAnnoMandato());
				 jGenerator.writeStringField("prop_cnrmissioni_numeroMandato" , cmisRimborsoMissione.getNumeroMandato());
				 jGenerator.writeStringField("prop_cnrmissioni_importoMandato" , cmisRimborsoMissione.getImportoMandato());
				 jGenerator.writeStringField("prop_cnrmissioni_wfOrdineDaRimborso" , cmisRimborsoMissione.getWfOrdineMissione());
				jGenerator.writeEndObject();
				jGenerator.close();
			} catch (IOException e) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: "+e);
			}
			
			try {
				Response responsePost = missioniCMISService.startFlowRimborsoMissione(stringWriter);
				TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
				HashMap<String,Object> mapRichiedente = mapper.readValue(responsePost.getStream(), typeRef); 
				String idFlusso = null;
				
				String text = mapRichiedente.get("persistedObject").toString();
				String patternString1 = "id=(activiti\\$[0-9]+)";

				Pattern pattern = Pattern.compile(patternString1);
				Matcher matcher = pattern.matcher(text);
				if (matcher.find())
					idFlusso = matcher.group(1);
				rimborsoMissione.setIdFlusso(idFlusso);
				rimborsoMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
			} catch (AwesomeException e) {
				throw e;
			} catch (Exception e) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
			}
		} else {
			if (rimborsoMissione.isStatoInviatoAlFlusso() && !StringUtils.isEmpty(rimborsoMissione.getIdFlusso())){
		        aggiungiAllegatiDettagli(principal, rimborsoMissione, nodeRefs);
				avanzaFlusso(rimborsoMissione, nodeRefs);
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Stato di invio al flusso non valido.");
			}
		}
	}

	public void aggiungiAllegatiDettagli(Principal principal, RimborsoMissione rimborsoMissione, StringBuffer nodeRefs)
			throws ComponentException {
		for (RimborsoMissioneDettagli dettaglio : rimborsoMissione.getRimborsoMissioneDettagli()){
			ItemIterable<CmisObject> children = getAttachmentsDetailRimborso(principal, new Long (dettaglio.getId().toString()));				
		    for (CmisObject object : children){
		    	Document doc = (Document)object;
				aggiungiDocumento(doc, nodeRefs);
		    }
		}
	}
	private void aggiungiDocumento(Document documentoAnticipo,
			StringBuffer nodeRefs) {
		if (documentoAnticipo != null){
			if (nodeRefs.length() > 0){
				 nodeRefs.append(",");
			}
			 nodeRefs.append((String)documentoAnticipo.getPropertyValue(MissioniCMISService.ALFCMIS_NODEREF));
		 }
	}

	public String getNodeRefRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException{
		Folder node = recuperoFolderRimborsoMissione(rimborsoMissione);
		if (node == null){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati al Rimborso di Missione. ID Rimborso di Missione:"+rimborsoMissione.getId()+", Anno:"+rimborsoMissione.getAnno()+", Numero:"+rimborsoMissione.getNumero());
		}
		String folder = (String) node.getPropertyValue(PropertyIds.OBJECT_ID); 
		StringBuffer query = new StringBuffer("select doc.cmis:objectId from cmis:document doc ");
		query.append(" join missioni_rimborso_attachment:rimborso rimborso on doc.cmis:objectId = rimborso.cmis:objectId ");
		query.append(" where IN_FOLDER(doc, '").append(folder).append("')");
		ItemIterable<QueryResult> results = missioniCMISService.search(query);
		if (results.getTotalNumItems() == 0)
			return null;
		else if (results.getTotalNumItems() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files di rimborso di missione aventi l'ID :"+ rimborsoMissione.getId()+", Anno:"+rimborsoMissione.getAnno()+", Numero:"+rimborsoMissione.getNumero());
		} else {
			for (QueryResult nodeFile : results) {
				String file = nodeFile.getPropertyValueById(PropertyIds.OBJECT_ID);
				return file;
			}
		}
		return null;
	}
	
	public Folder recuperoFolderRimborsoMissione(RimborsoMissione rimborsoMissione)throws ComponentException{
		StringBuffer query = new StringBuffer("select miss.cmis:objectId from missioni:main as miss "
				+ " join missioni_commons_aspect:rimborso_missione rimborso on miss.cmis:objectId = rimborso.cmis:objectId");
		query.append(" where miss.missioni:id = ").append(rimborsoMissione.getId());

		ItemIterable<QueryResult> resultsFolder = missioniCMISService.search(query);
		if (resultsFolder.getTotalNumItems() == 0)
			return null;
		else if (resultsFolder.getTotalNumItems() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' rimborsi di missione aventi l'ID :"+ rimborsoMissione.getId()+", Anno:"+rimborsoMissione.getAnno()+", Numero:"+rimborsoMissione.getNumero());
		} else {
			for (QueryResult queryResult : resultsFolder) {
				return (Folder) missioniCMISService.getNodeByNodeRef((String) queryResult.getPropertyValueById(PropertyIds.OBJECT_ID));
			}
		}
		return null;
	}
	
//    @Transactional(readOnly = true)
//    public void uploadAllegatoRimborsoMissione(Principal principal, RimborsoMissione rimborsoMissione, InputStream uploadedAllegatoInputStream) throws AwesomeException, ComponentException {
//    	CmisPath cmisPath = createFolderRimborsoMissione(rimborsoMissione);
//    	Map<String, Object> metadataProperties = new HashMap<String, Object>();
//    	try{
//    		Document node = missioniCMISService.restoreSimpleDocument(
//    				metadataProperties,
//    				uploadedAllegatoInputStream,
//    				MimeTypes.PDF.mimetype(),
//    				fileName, 
//    				cmisPath);
//    		missioniCMISService.addAspect(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_ALLEGATI.value());
//    		missioniCMISService.makeVersionable(node);
//    	} catch (Exception e) {
//    		if (e.getCause() instanceof CmisConstraintException)
//    			throw new AwesomeException("CMIS - File ["+fileName+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!");
//    		throw new AwesomeException("CMIS - Errore nella registrazione del file sul Documentale (" + Utility.getMessageException(e) + ")");
//    	}
//    }
//
	private void avanzaFlusso(RimborsoMissione rimborsoMissione, StringBuffer nodeRefs) throws AwesomeException {
    	avanzaFlusso(rimborsoMissione, nodeRefs, FlowResubmitType.RESTART_FLOW);
    }

    public void annullaFlusso(RimborsoMissione rimborsoMissione) throws AwesomeException {
    	avanzaFlusso(rimborsoMissione, null, FlowResubmitType.ABORT_FLOW);
    	rimborsoMissione.setStatoFlusso(Costanti.STATO_ANNULLATO);
    }

    private void avanzaFlusso(RimborsoMissione rimborsoMissione, StringBuffer nodeRefs, FlowResubmitType step) throws AwesomeException {
    	try {
    		restartFlowRimborsoMissione(rimborsoMissione, nodeRefs, step);
    	} catch (AwesomeException e) {
    		throw e;
    	}
    }
	public ResultFlows getFlowsRimborsoMissione(String idFlusso)throws AwesomeException{
		String fieldStato = "wfcnr:statoFlusso"; 
		String fieldTaskId = "wfcnr:taskId"; 
		String fieldComment = "cnrmissioni:commento"; 
		QueryResult result = recuperoFlusso(idFlusso);
		if (result != null){
			ResultFlows flows = new ResultFlows();
			flows.setState((String) result.getPropertyValueById(fieldStato));
			flows.setComment((String) result.getPropertyValueById(fieldComment));
			flows.setTaskId((String) result.getPropertyValueById(fieldTaskId));
			return flows;
		}
		return null;
	}

	public void restartFlowRimborsoMissione(RimborsoMissione rimborsoMissione, StringBuffer nodeRefs, FlowResubmitType step) throws AwesomeException {
    	ResultFlows result = getFlowsRimborsoMissione(rimborsoMissione.getIdFlusso());
    	if (!StringUtils.isEmpty(result.getTaskId())){
    		if (step.equals(FlowResubmitType.ABORT_FLOW.operation())){
        		StringWriter stringWriter = createJsonForAbortFlowOrdineMissione();
        		missioniCMISService.abortFlow(stringWriter, result);
    		} else {
    			StringWriter stringWriter = createJsonForRestartFlowOrdineMissione(nodeRefs);
        		missioniCMISService.restartFlow(stringWriter, result);
    		}
    	} else {
    		throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Task Id del flusso non trovato.");
    	}
    }

	public QueryResult recuperoFlusso(String idFlusso)throws AwesomeException{
		StringBuffer query = new StringBuffer("select parametriFlusso.*, flussoMissioni.* from cmis:document as t ");
		query.append( "inner join wfcnr:parametriFlusso as parametriFlusso on t.cmis:objectId = parametriFlusso.cmis:objectId ");
		query.append(" inner join cnrmissioni:parametriFlussoMissioni as flussoMissioni on t.cmis:objectId = flussoMissioni.cmis:objectId ");
		query.append(" where parametriFlusso.wfcnr:wfInstanceId = '").append(idFlusso).append("'");

		ItemIterable<QueryResult> resultsFolder = missioniCMISService.search(query);
		if (resultsFolder.getTotalNumItems() == 0){
			return null;
		} else {
			for (QueryResult queryResult : resultsFolder) {
				return queryResult;
			}
		}
		return null;
	}

	private StringWriter createJsonForRestartFlowOrdineMissione(StringBuffer nodeRefs) {
		StringWriter stringWriter = new StringWriter();
		try {
			JsonFactory jsonFactory = new JsonFactory();
			JsonGenerator jGenerator = jsonFactory.createJsonGenerator(stringWriter);
			jGenerator.writeStartObject();
			if (nodeRefs != null){
				jGenerator.writeStringField("assoc_packageItems_added" , nodeRefs.toString());
			}
			jGenerator.writeStringField("prop_bpm_comment" , "AVANZAMENTO");
			jGenerator.writeStringField("prop_wfcnr_reviewOutcome" , FlowResubmitType.RESTART_FLOW.operation());
			jGenerator.writeStringField("prop_transitions" , "Next");
			jGenerator.writeEndObject();
			jGenerator.close();
		} catch (IOException e) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di scrittura dei file del flusso per l'avanzamento del documentale. Errore: "+e);
		}
		return stringWriter;
	}

	private StringWriter createJsonForAbortFlowOrdineMissione() {
		StringWriter stringWriter = new StringWriter();
		try {
			JsonFactory jsonFactory = new JsonFactory();
			JsonGenerator jGenerator = jsonFactory.createJsonGenerator(stringWriter);
			jGenerator.writeStartObject();
			jGenerator.writeStringField("bpm_comment" , "AVANZAMENTO");
			jGenerator.writeStringField("wfcnr_reviewOutcome" , FlowResubmitType.ABORT_FLOW.operation());
			jGenerator.writeEndObject();
			jGenerator.close();
		} catch (IOException e) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di scrittura dei file del flusso per l'avanzamento del documentale. Errore: "+e);
		}
		return stringWriter;
	}
	public ContentStream getContentStreamRimborsoMissione(RimborsoMissione rimborsoMissione) throws Exception{
		String id = getNodeRefRimborsoMissione(rimborsoMissione);
		if (id != null){
			return missioniCMISService.recuperoContentFileFromObjectID(id);
		}
		return null;
	}
	
}
