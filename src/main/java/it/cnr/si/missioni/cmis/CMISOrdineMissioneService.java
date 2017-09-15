package it.cnr.si.missioni.cmis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.flows.FlowResubmitType;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAnticipo;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.service.DatiIstitutoService;
import it.cnr.si.missioni.service.OrdineMissioneAnticipoService;
import it.cnr.si.missioni.service.OrdineMissioneAutoPropriaService;
import it.cnr.si.missioni.service.PrintOrdineMissioneAnticipoService;
import it.cnr.si.missioni.service.PrintOrdineMissioneAutoPropriaService;
import it.cnr.si.missioni.service.PrintOrdineMissioneService;
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
public class CMISOrdineMissioneService {
	private static final Log logger = LogFactory.getLog(MissioniCMISService.class);

	public static final String PROPERTY_TIPOLOGIA_DOC = "wfcnr:tipologiaDOC";
	public static final String PROPERTY_TIPOLOGIA_DOC_SPECIFICA = "wfcnr:tipologiaDocSpecifica";
	public static final String PROPERTY_TIPOLOGIA_DOC_MISSIONI = "cnrmissioni:tipologiaDocumentoMissione";

	@Autowired
	private DatiIstitutoService datiIstitutoService;

    @Autowired
    private Environment env;

	@Autowired
	private PrintOrdineMissioneService printOrdineMissioneService;

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
	private PrintOrdineMissioneAnticipoService printOrdineMissioneAnticipoService;
	
	@Autowired
	private PrintOrdineMissioneAutoPropriaService printOrdineMissioneAutoPropriaService;
	
	@Autowired
	private OrdineMissioneAnticipoService ordineMissioneAnticipoService;

	@Autowired
	private OrdineMissioneAutoPropriaService ordineMissioneAutoPropriaService;

	@Autowired
	private MissioniCMISService missioniCMISService;

	@Autowired
	private AccountService accountService;

	public CMISOrdineMissione create(Principal principal, OrdineMissione ordineMissione) throws ComponentException{
		if (ordineMissione != null){
			CMISOrdineMissione cmisOrdineMissione = new CMISOrdineMissione();
			caricaDatiDerivati(principal, ordineMissione);
			OrdineMissioneAnticipo anticipo = null;
			OrdineMissioneAutoPropria autoPropria = null;
			if (ordineMissione != null){
				anticipo = ordineMissioneAnticipoService.getAnticipo(principal, new Long(ordineMissione.getId().toString()));
				if (anticipo != null){
					ordineMissione.setRichiestaAnticipo("S");
				} else {
					ordineMissione.setRichiestaAnticipo("N");
				}
				autoPropria = ordineMissioneAutoPropriaService.getAutoPropria(principal, new Long(ordineMissione.getId().toString()));
				if (autoPropria != null){
					ordineMissione.setUtilizzoAutoPropria("S");
				} else {
					ordineMissione.setUtilizzoAutoPropria("N");
				}
			}

			String username = principal.getName();
			
			Account account = accountService.loadAccountFromRest(ordineMissione.getUid());
			Progetto progetto = progettoService.loadModulo(ordineMissione.getPgProgetto(), ordineMissione.getAnno(), null);
			Voce voce = voceService.loadVoce(ordineMissione);
			Gae gae = gaeService.loadGae(ordineMissione);
			UnitaOrganizzativa uoCompetenza = null;
			if (ordineMissione.getUoCompetenza() != null){
				uoCompetenza = unitaOrganizzativaService.loadUo(ordineMissione.getUoCompetenza(), null, ordineMissione.getAnno());
			}
			UnitaOrganizzativa uoSpesa = unitaOrganizzativaService.loadUo(ordineMissione.getUoSpesa(), null, ordineMissione.getAnno());
			UnitaOrganizzativa uoRich = unitaOrganizzativaService.loadUo(ordineMissione.getUoRich(), null, ordineMissione.getAnno());
			String descrImpegno = ""; 
			BigDecimal dispImpegno = null;
			if (ordineMissione.getPgObbligazione() != null){
				if (gae != null){
					ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(ordineMissione);
					descrImpegno = impegnoGae.getDsObbligazione();
					dispImpegno = impegnoGae.getDisponibilitaImpegno();
				} else {
					Impegno impegno = impegnoService.loadImpegno(ordineMissione);
					descrImpegno = impegno.getDsObbligazione();
					dispImpegno = impegno.getDisponibilitaImpegno();
				}
			}

			String uoCompetenzaPerFlusso = Utility.replace(ordineMissione.getUoCompetenza(), ".", "");
			String uoSpesaPerFlusso = Utility.replace(ordineMissione.getUoSpesa(), ".", "");
			String uoRichPerFlusso = Utility.replace(ordineMissione.getUoRich(), ".", "");
			Uo uoDatiSpesa = uoService.recuperoUo(uoSpesaPerFlusso);
			String userNameFirmatario = null;
			String userNameFirmatarioSpesa = null;
			userNameFirmatario = recuperoDirettore(uoRichPerFlusso, ordineMissione.getAnno());
			
			if (uoDatiSpesa != null && uoDatiSpesa.getFirmaSpesa() != null && uoDatiSpesa.getFirmaSpesa().equals("N")){
				if (uoCompetenzaPerFlusso != null){
					userNameFirmatarioSpesa = recuperoDirettore(uoCompetenzaPerFlusso, ordineMissione.getAnno());
				} else {
					userNameFirmatarioSpesa = userNameFirmatario;
				}
			} else {
				userNameFirmatarioSpesa = recuperoDirettore(uoSpesaPerFlusso, ordineMissione.getAnno());
			}
			
			GregorianCalendar dataScadenzaFlusso = new GregorianCalendar();
			dataScadenzaFlusso.setTime(DateUtils.getCurrentTime());
			dataScadenzaFlusso.add(Calendar.DATE, 14);
			dataScadenzaFlusso.add(Calendar.MONTH, 1);


			cmisOrdineMissione.setAnno(ordineMissione.getAnno().toString());
			cmisOrdineMissione.setNumero(ordineMissione.getNumero().toString());
			cmisOrdineMissione.setAnticipo(ordineMissione.getRichiestaAnticipo().equals("S") ? "true" : "false");
			cmisOrdineMissione.setAutoPropriaFlag(ordineMissione.getUtilizzoAutoPropria().equals("S") ? "true" : "false");
			cmisOrdineMissione.setCapitolo(voce == null ? "" : ordineMissione.getVoce());
			cmisOrdineMissione.setDescrizioneCapitolo(voce == null ? "" : voce.getDs_elemento_voce());
			cmisOrdineMissione.setDescrizioneGae(gae == null ? "" : gae.getDs_linea_attivita());
			cmisOrdineMissione.setDescrizioneImpegno(descrImpegno);
			cmisOrdineMissione.setDescrizioneModulo(progetto == null ? "" : progetto.getDs_progetto());
			cmisOrdineMissione.setDescrizioneUoOrdine(uoRich == null ? "" : uoRich.getDs_unita_organizzativa());
			cmisOrdineMissione.setDescrizioneUoSpesa(uoSpesa == null ? "" : uoSpesa.getDs_unita_organizzativa());
			cmisOrdineMissione.setDescrizioneUoCompetenza(uoCompetenza == null ? "" : uoCompetenza.getDs_unita_organizzativa());
			cmisOrdineMissione.setDisponibilita(Utility.nvl(dispImpegno));
			cmisOrdineMissione.setGae(gae == null ? "" : gae.getCd_linea_attivita());
			cmisOrdineMissione.setImpegnoAnnoCompetenza(ordineMissione.getEsercizioObbligazione() == null ? null : new Long(ordineMissione.getEsercizioObbligazione()));
			cmisOrdineMissione.setImpegnoAnnoResiduo(ordineMissione.getEsercizioOriginaleObbligazione() == null ? null : new Long(ordineMissione.getEsercizioOriginaleObbligazione()));
			cmisOrdineMissione.setImpegnoNumero(ordineMissione.getPgObbligazione());
			cmisOrdineMissione.setImportoMissione(ordineMissione.getImportoPresunto() == null ? null : Utility.nvl(ordineMissione.getImportoPresunto()));
			cmisOrdineMissione.setModulo(progetto == null ? "" : progetto.getCd_progetto());
			cmisOrdineMissione.setNoleggioFlag(ordineMissione.getUtilizzoAutoNoleggio().equals("S") ? "true" : "false");
			cmisOrdineMissione.setTrattamento(ordineMissione.decodeTrattamento());
			cmisOrdineMissione.setNote(ordineMissione.getNote() == null ? "" : ordineMissione.getNote());
			cmisOrdineMissione.setNoteSegreteria(ordineMissione.getNoteSegreteria() == null ? "" : ordineMissione.getNoteSegreteria());
			cmisOrdineMissione.setOggetto(ordineMissione.getOggetto());
			cmisOrdineMissione.setPriorita(ordineMissione.getPriorita());
			cmisOrdineMissione.setTaxiFlag(ordineMissione.getUtilizzoTaxi().equals("S") ? "true" : "false");
			cmisOrdineMissione.setAutoServizioFlag(ordineMissione.getUtilizzoAutoServizio().equals("S") ? "true" : "false");
			cmisOrdineMissione.setPersonaSeguitoFlag(ordineMissione.getPersonaleAlSeguito().equals("S") ? "true" : "false");
			cmisOrdineMissione.setUoOrdine(uoRichPerFlusso);
			cmisOrdineMissione.setUoSpesa(uoSpesaPerFlusso);
			cmisOrdineMissione.setUoCompetenza(uoCompetenzaPerFlusso == null ? "" : uoCompetenzaPerFlusso);
			cmisOrdineMissione.setUserNameFirmatarioSpesa(userNameFirmatarioSpesa);
			cmisOrdineMissione.setUserNamePrimoFirmatario(userNameFirmatario);
			cmisOrdineMissione.setUserNameResponsabileModulo("");
			cmisOrdineMissione.setUsernameResponsabileGruppo("");
			cmisOrdineMissione.setUsernameRichiedente(username);
			cmisOrdineMissione.setUsernameUtenteOrdine(ordineMissione.getUid());
			cmisOrdineMissione.setValidazioneModulo(StringUtils.isEmpty(ordineMissione.getResponsabileGruppo()) ? "false" : "true");
			cmisOrdineMissione.setValidazioneSpesa(impostaValidazioneSpesa(userNameFirmatario, userNameFirmatarioSpesa));
			cmisOrdineMissione.setWfDescription("Ordine di Missione n. "+ordineMissione.getNumero()+" di "+account.getCognome() + " "+account.getNome());
			cmisOrdineMissione.setWfDueDate(DateUtils.getDateAsString(dataScadenzaFlusso.getTime(), DateUtils.PATTERN_DATE_FOR_DOCUMENTALE));
			cmisOrdineMissione.setDestinazione(ordineMissione.getDestinazione());
			cmisOrdineMissione.setMissioneEsteraFlag(ordineMissione.getTipoMissione().equals("E") ? "true" : "false");
			cmisOrdineMissione.setDataInizioMissione(DateUtils.getDateAsString(ordineMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
			cmisOrdineMissione.setDataFineMissione(DateUtils.getDateAsString(ordineMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
			cmisOrdineMissione.setFondi(ordineMissione.getDecodeFondi());
			if (autoPropria != null){
				cmisOrdineMissione.setPrimoMotivoAutoPropria(Utility.nvl(autoPropria.getUtilizzoMotiviIspettivi(),"N").equals("N") ? "" : CMISOrdineMissione.PRIMO_MOTIVO_UTILIZZO_AUTO_PROPRIA);
				cmisOrdineMissione.setSecondoMotivoAutoPropria(Utility.nvl(autoPropria.getUtilizzoMotiviUrgenza(),"N").equals("N") ? "" : CMISOrdineMissione.SECONDO_MOTIVO_UTILIZZO_AUTO_PROPRIA);
				cmisOrdineMissione.setTerzoMotivoAutoPropria(Utility.nvl(autoPropria.getUtilizzoMotiviTrasporto(),"N").equals("N") ? "" : CMISOrdineMissione.TERZO_MOTIVO_UTILIZZO_AUTO_PROPRIA);
			} else {
				cmisOrdineMissione.setPrimoMotivoAutoPropria("");
				cmisOrdineMissione.setSecondoMotivoAutoPropria("");
				cmisOrdineMissione.setTerzoMotivoAutoPropria("");
			}
			if (!StringUtils.isEmpty(ordineMissione.getResponsabileGruppo())){
				cmisOrdineMissione.setUsernameResponsabileGruppo(ordineMissione.getResponsabileGruppo());
				cmisOrdineMissione.setUserNameResponsabileModulo(ordineMissione.getResponsabileGruppo());
			}

			return cmisOrdineMissione;
		}
		return null;
	}

	private String recuperoDirettore(String uo, Integer anno) {
		String userNameFirmatario;
		if (isDevProfile()){
			userNameFirmatario = recuperoUidDirettoreUo(uo);
		} else {
			DatiIstituto dati = datiIstitutoService.getDatiIstituto(uo, anno);
			if (dati != null && dati.getResponsabile() != null){
				userNameFirmatario = dati.getResponsabile();
			} else {
				userNameFirmatario = accountService.getDirector(uo);		
			}
		}
		if (StringUtils.isEmpty(userNameFirmatario)){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non è stato possibile recuperare il direttore per la uo "+uo);
		}
		return userNameFirmatario;
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
	
	private void caricaDatiDerivati(Principal principal, OrdineMissione ordineMissione) {
		if (ordineMissione != null){
			DatiIstituto dati = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), ordineMissione.getAnno());
			if (dati == null){
				dati = datiIstitutoService.creaDatiIstitutoOrdine(principal, ordineMissione.getUoSpesa(), ordineMissione.getAnno());
			}
			ordineMissione.setDatiIstituto(dati);
			if (ordineMissione.getDatiIstituto() == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non esistono i dati per istituto per il codice "+ordineMissione.getCdsSpesa()+" nell'anno "+ordineMissione.getAnno());
			}
		}
	}

	@Transactional(readOnly = true)
	public Document salvaStampaOrdineMissioneSuCMIS(Principal principal, byte[] stampa, OrdineMissione ordineMissione) {
		CMISOrdineMissione cmisOrdineMissione = create(principal, ordineMissione);
		return salvaStampaOrdineMissioneSuCMIS(principal, stampa, ordineMissione, cmisOrdineMissione);
	}
	
	public CmisPath createFolderOrdineMissione(OrdineMissione ordineMissione){
		CmisPath cmisPath = missioniCMISService.getBasePath();
		cmisPath = missioniCMISService.createFolderIfNotPresent(cmisPath, ordineMissione.getUoRich());
		cmisPath = missioniCMISService.createFolderIfNotPresent(cmisPath, "Ordini di Missione");
		cmisPath = missioniCMISService.createFolderIfNotPresent(cmisPath, "Anno "+ordineMissione.getAnno());
		cmisPath = createLastFolderIfNotPresent(cmisPath, ordineMissione);
		return cmisPath;
	}

	public CmisPath createLastFolderIfNotPresent(CmisPath cmisPath, OrdineMissione ordineMissione){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		String name = ordineMissione.constructCMISNomeFile();
		String folderName = name;
		folderName = missioniCMISService.sanitizeFolderName(folderName);
		metadataProperties.put(PropertyIds.OBJECT_TYPE_ID, OrdineMissione.CMIS_PROPERTY_MAIN);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename(name));
		metadataProperties.put(PropertyIds.NAME, missioniCMISService.sanitizeFilename(ordineMissione.constructCMISNomeFile()));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename(name));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_NUMERO, ordineMissione.getNumero());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_ANNO, ordineMissione.getAnno());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_ID, ordineMissione.getId());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_UID, ordineMissione.getUid());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_MODULO, ordineMissione.getModulo());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_OGGETTO, ordineMissione.getOggetto());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_DESTINAZIONE, ordineMissione.getDestinazione());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_NOTE, ordineMissione.getNote());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_NOTE_SEGRETERIA, ordineMissione.getNoteSegreteria());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_DATA_INIZIO, DateUtils.getDate(ordineMissione.getDataInizioMissione()));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_DATA_FINE, DateUtils.getDate(ordineMissione.getDataFineMissione()));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_DATA_INSERIMENTO, DateUtils.getDate(ordineMissione.getDataInserimento()));
		List<String> aspectsToAdd = new ArrayList<String>();
		aspectsToAdd.add(MissioniCMISService.ASPECT_TITLED);
		aspectsToAdd.add(CMISMissioniAspect.ORDINE_MISSIONE_ASPECT.value());
		cmisPath = missioniCMISService.createFolderIfNotPresent(cmisPath, metadataProperties, aspectsToAdd, folderName);
		return cmisPath;
	}
	
	
	private Document salvaStampaOrdineMissioneSuCMIS(Principal principal,
			byte[] stampa, OrdineMissione ordineMissione,
			CMISOrdineMissione cmisOrdineMissione) {
		InputStream streamStampa = new ByteArrayInputStream(stampa);
		CmisPath cmisPath = createFolderOrdineMissione(ordineMissione);
		Map<String, Object> metadataProperties = createMetadataForFileOrdineMissione(principal.getName(), cmisOrdineMissione);
		try{
			Document node = missioniCMISService.restoreSimpleDocument(
					metadataProperties,
					streamStampa,
					MimeTypes.PDF.mimetype(),
					ordineMissione.getFileName(), 
					cmisPath);
			missioniCMISService.addAspect(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ORDINE.value());
			missioniCMISService.makeVersionable(node);
			return node;
		} catch (Exception e) {
			if (e.getCause() instanceof CmisConstraintException)
				throw new AwesomeException(CodiciErrore.ERRGEN, "CMIS - File ["+ordineMissione.getFileName()+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!");
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

	public Map<String, Object> createMetadataForFileOrdineMissione(String currentLogin, CMISOrdineMissione cmisOrdineMissione){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(PropertyIds.OBJECT_TYPE_ID, OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename("Ordine Missione - anno "+cmisOrdineMissione.getAnno()+" numero "+cmisOrdineMissione.getNumero()));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename("Ordine di Missione"));
		metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, OrdineMissione.CMIS_PROPERTY_NAME_DOC_ORDINE);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, OrdineMissione.CMIS_PROPERTY_NAME_TIPODOC_ORDINE);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, OrdineMissione.CMIS_PROPERTY_NAME_TIPODOC_ORDINE);

		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_ANNO_IMPEGNO_COMP, cmisOrdineMissione.getImpegnoAnnoCompetenza());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_ANNO_IMPEGNO_RES, cmisOrdineMissione.getImpegnoAnnoResiduo());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_ANTICIPO, cmisOrdineMissione.getAnticipo().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_AUTO_PROPRIA, cmisOrdineMissione.getAutoPropriaFlag().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_CAPITOLO, cmisOrdineMissione.getCapitolo());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE, cmisOrdineMissione.getOggetto());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_CAPITOLO, cmisOrdineMissione.getDescrizioneCapitolo());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_GAE, cmisOrdineMissione.getDescrizioneGae());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_IMPEGNO, cmisOrdineMissione.getDescrizioneImpegno());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_MODULO, cmisOrdineMissione.getDescrizioneModulo());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_UO_ORDINE, cmisOrdineMissione.getDescrizioneUoOrdine());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_UO_SPESA, cmisOrdineMissione.getDescrizioneUoSpesa());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DISPONIBILITA_IMPEGNO, cmisOrdineMissione.getDisponibilita());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_GAE, cmisOrdineMissione.getGae());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_IMPORTO_MISSIONE, cmisOrdineMissione.getImportoMissione());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_MODULO, cmisOrdineMissione.getModulo());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_TRATTAMENTO, cmisOrdineMissione.getTrattamento());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_NOLEGGIO, cmisOrdineMissione.getNoleggioFlag().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_NOTE, cmisOrdineMissione.getNote());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_NOTE_SEGRETERIA, cmisOrdineMissione.getNoteSegreteria());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_NUMERO_IMPEGNO, cmisOrdineMissione.getImpegnoNumero());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_TAXI, cmisOrdineMissione.getTaxiFlag().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_PERSONA_SEGUITO, cmisOrdineMissione.getPersonaSeguitoFlag().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_AUTO_SERVIZIO, cmisOrdineMissione.getAutoServizioFlag().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESTINAZIONE, cmisOrdineMissione.getDestinazione());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_ESTERA_FLAG, cmisOrdineMissione.getMissioneEsteraFlag().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DATA_INIZIO_MISSIONE, cmisOrdineMissione.getDataInizioMissione());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DATA_FINE_MISSIONE, cmisOrdineMissione.getDataFineMissione());
		
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_UO_ORDINE, cmisOrdineMissione.getUoOrdine());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_UO_SPESA, cmisOrdineMissione.getUoSpesa());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_FIRMA_SPESA, cmisOrdineMissione.getUserNameFirmatarioSpesa());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_FIRMA_UO, cmisOrdineMissione.getUserNamePrimoFirmatario());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_ORDINE, cmisOrdineMissione.getUsernameUtenteOrdine());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_RESPONSABILE_MODULO, cmisOrdineMissione.getUsernameResponsabileGruppo());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_FONDI, cmisOrdineMissione.getFondi());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_AUTO_PROPRIA_PRIMO_MOTIVO, cmisOrdineMissione.getPrimoMotivoAutoPropria());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_AUTO_PROPRIA_SECONDO_MOTIVO, cmisOrdineMissione.getSecondoMotivoAutoPropria());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_AUTO_PROPRIA_TERZO_MOTIVO, cmisOrdineMissione.getTerzoMotivoAutoPropria());
		
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_RICHIEDENTE, cmisOrdineMissione.getUsernameRichiedente());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_VALIDAZIONE_MODULO, StringUtils.isEmpty(cmisOrdineMissione.getUsernameResponsabileGruppo()) ? false : true);
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_VALIDAZIONE_SPESA, cmisOrdineMissione.getValidazioneSpesa().equals("true"));
		return metadataProperties;
	}
	
	public void avviaFlusso(Principal principal, OrdineMissione ordineMissione) {
		String username = principal.getName();
		byte[] stampa = printOrdineMissioneService.printOrdineMissione(ordineMissione, username);
		CMISOrdineMissione cmisOrdineMissione = create(principal, ordineMissione);
		Document documento = salvaStampaOrdineMissioneSuCMIS(principal, stampa, ordineMissione, cmisOrdineMissione);
		OrdineMissioneAnticipo anticipo = ordineMissioneAnticipoService.getAnticipo(principal, new Long(ordineMissione.getId().toString()));
		OrdineMissioneAutoPropria autoPropria = ordineMissioneAutoPropriaService.getAutoPropria(principal, new Long(ordineMissione.getId().toString()), true);
		Document documentoAnticipo = null;
		List<CMISFileAttachment> allegati = new ArrayList<>();
		if (anticipo != null){
			anticipo.setOrdineMissione(ordineMissione);
			documentoAnticipo = creaDocumentoAnticipo(username, anticipo);
			List<CMISFileAttachment> allegatiAnticipo = getAttachmentsAnticipo(ordineMissione, new Long(anticipo.getId().toString()));
			if (allegatiAnticipo != null && !allegatiAnticipo.isEmpty()){
				allegati.addAll(allegatiAnticipo);
			}
		}
		
		Document documentoAutoPropria = null;
		if (autoPropria != null){
			autoPropria.setOrdineMissione(ordineMissione);
			documentoAutoPropria = creaDocumentoAutoPropria(username, autoPropria);
		}

		String nodeRefFirmatario = missioniCMISService.recuperoNodeRefUtente(cmisOrdineMissione.getUserNamePrimoFirmatario());

		StringWriter stringWriter = new StringWriter();
		JsonFactory jsonFactory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(jsonFactory); 
		try {
			JsonGenerator jGenerator = jsonFactory.createJsonGenerator(stringWriter);
			jGenerator.writeStartObject();
			jGenerator.writeStringField("assoc_bpm_assignee_added" , nodeRefFirmatario);
			jGenerator.writeStringField("assoc_bpm_assignee_removed" , "");
			StringBuilder nodeRefs = new StringBuilder();
			if (ordineMissione.isStatoNonInviatoAlFlusso()){
				aggiungiDocumento(documento, nodeRefs);
				aggiungiDocumento(documentoAnticipo, nodeRefs);
				aggiungiAllegati(allegati, nodeRefs);
				jGenerator.writeStringField("prop_bpm_comment" , "");
				jGenerator.writeStringField("prop_bpm_percentComplete" , "0");
			}

			aggiungiDocumento(documentoAutoPropria, nodeRefs);

			jGenerator.writeStringField("assoc_packageItems_added" , nodeRefs.toString());
			jGenerator.writeStringField("assoc_packageItems_removed" , "");

			jGenerator.writeStringField("prop_cnrmissioni_descrizioneOrdine" , cmisOrdineMissione.getOggetto());
			jGenerator.writeStringField("prop_cnrmissioni_note" , cmisOrdineMissione.getNote());
			jGenerator.writeStringField("prop_cnrmissioni_noteSegreteria" , cmisOrdineMissione.getNoteSegreteria());
			jGenerator.writeStringField("prop_bpm_workflowDescription" , cmisOrdineMissione.getWfDescription());
			jGenerator.writeStringField("prop_bpm_workflowDueDate" , cmisOrdineMissione.getWfDueDate());
			jGenerator.writeStringField("prop_bpm_status" , "Not Yet Started");
			jGenerator.writeStringField("prop_wfcnr_groupName" , "GENERICO");
			jGenerator.writeStringField("prop_wfcnr_wfCounterIndex" , "");
			jGenerator.writeStringField("prop_wfcnr_wfCounterId" , "");
			jGenerator.writeStringField("prop_wfcnr_wfCounterAnno" , "");
			jGenerator.writeStringField("prop_bpm_workflowPriority" , cmisOrdineMissione.getPriorita());
			jGenerator.writeStringField("prop_cnrmissioni_validazioneSpesaFlag" , cmisOrdineMissione.getValidazioneSpesa());
			jGenerator.writeStringField("prop_cnrmissioni_missioneConAnticipoFlag" , cmisOrdineMissione.getAnticipo());
			jGenerator.writeStringField("prop_cnrmissioni_validazioneModuloFlag" , StringUtils.isEmpty(cmisOrdineMissione.getUserNameResponsabileModulo()) ? "false": "true");
			jGenerator.writeStringField("prop_cnrmissioni_userNameUtenteOrdineMissione" , cmisOrdineMissione.getUsernameUtenteOrdine());
			jGenerator.writeStringField("prop_cnrmissioni_userNameRichiedente" , cmisOrdineMissione.getUsernameRichiedente());
			jGenerator.writeStringField("prop_cnrmissioni_userNameResponsabileModulo" , cmisOrdineMissione.getUserNameResponsabileModulo());
			jGenerator.writeStringField("prop_cnrmissioni_userNamePrimoFirmatario" , cmisOrdineMissione.getUserNamePrimoFirmatario());
			jGenerator.writeStringField("prop_cnrmissioni_userNameFirmatarioSpesa" , cmisOrdineMissione.getUserNameFirmatarioSpesa());
			jGenerator.writeStringField("prop_cnrmissioni_userNameAmministrativo1" , "");
			jGenerator.writeStringField("prop_cnrmissioni_userNameAmministrativo2" , "");
			jGenerator.writeStringField("prop_cnrmissioni_userNameAmministrativo3" , "");
			jGenerator.writeStringField("prop_cnrmissioni_uoOrdine" , cmisOrdineMissione.getUoOrdine());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneUoOrdine" , cmisOrdineMissione.getDescrizioneUoOrdine());
			jGenerator.writeStringField("prop_cnrmissioni_uoSpesa" , cmisOrdineMissione.getUoSpesa());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneUoSpesa" , cmisOrdineMissione.getDescrizioneUoSpesa());
			jGenerator.writeStringField("prop_cnrmissioni_uoCompetenza" , cmisOrdineMissione.getUoCompetenza());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneUoCompetenza" , cmisOrdineMissione.getDescrizioneUoCompetenza());
			jGenerator.writeStringField("prop_cnrmissioni_autoPropriaFlag" , cmisOrdineMissione.getAutoPropriaFlag());
			jGenerator.writeStringField("prop_cnrmissioni_noleggioFlag" , cmisOrdineMissione.getNoleggioFlag());
			jGenerator.writeStringField("prop_cnrmissioni_taxiFlag" , cmisOrdineMissione.getTaxiFlag());
			jGenerator.writeStringField("prop_cnrmissioni_servizioFlagOk" , cmisOrdineMissione.getAutoServizioFlag());
			jGenerator.writeStringField("prop_cnrmissioni_personaSeguitoFlagOk" , cmisOrdineMissione.getPersonaSeguitoFlag());
			jGenerator.writeStringField("prop_cnrmissioni_capitolo" , cmisOrdineMissione.getCapitolo());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneCapitolo" , cmisOrdineMissione.getDescrizioneCapitolo());
			jGenerator.writeStringField("prop_cnrmissioni_modulo" , cmisOrdineMissione.getModulo());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneModulo" , cmisOrdineMissione.getDescrizioneModulo());
			jGenerator.writeStringField("prop_cnrmissioni_gae" , cmisOrdineMissione.getGae());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneGae" , cmisOrdineMissione.getDescrizioneGae());
			jGenerator.writeStringField("prop_cnrmissioni_impegnoAnnoResiduo" , cmisOrdineMissione.getImpegnoAnnoResiduo() == null ? "": cmisOrdineMissione.getImpegnoAnnoResiduo().toString());
			jGenerator.writeStringField("prop_cnrmissioni_impegnoAnnoCompetenza" , cmisOrdineMissione.getImpegnoAnnoCompetenza() == null ? "": cmisOrdineMissione.getImpegnoAnnoCompetenza().toString());
			jGenerator.writeStringField("prop_cnrmissioni_impegnoNumeroOk" , cmisOrdineMissione.getImpegnoNumero() == null ? "": cmisOrdineMissione.getImpegnoNumero().toString());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneImpegno" , cmisOrdineMissione.getDescrizioneImpegno());
			jGenerator.writeStringField("prop_cnrmissioni_importoMissione" , cmisOrdineMissione.getImportoMissione() == null ? "": cmisOrdineMissione.getImportoMissione().toString());
			jGenerator.writeStringField("prop_cnrmissioni_disponibilita" , cmisOrdineMissione.getDisponibilita() == null ? "": cmisOrdineMissione.getDisponibilita().toString());
			jGenerator.writeStringField("prop_cnrmissioni_missioneEsteraFlag" , cmisOrdineMissione.getMissioneEsteraFlag());
			jGenerator.writeStringField("prop_cnrmissioni_destinazione" , cmisOrdineMissione.getDestinazione());
			jGenerator.writeStringField("prop_cnrmissioni_dataInizioMissione" , cmisOrdineMissione.getDataInizioMissione());
			jGenerator.writeStringField("prop_cnrmissioni_dataFineMissione" , cmisOrdineMissione.getDataFineMissione());
			jGenerator.writeStringField("prop_cnrmissioni_trattamento" , cmisOrdineMissione.getTrattamento());
			jGenerator.writeStringField("prop_cnrmissioni_competenzaResiduo" , cmisOrdineMissione.getFondi());
			jGenerator.writeStringField("prop_cnrmissioni_autoPropriaPrimoMotivo" , cmisOrdineMissione.getPrimoMotivoAutoPropria());
			jGenerator.writeStringField("prop_cnrmissioni_autoPropriaSecondoMotivo" , cmisOrdineMissione.getSecondoMotivoAutoPropria());
			jGenerator.writeStringField("prop_cnrmissioni_autoPropriaTerzoMotivo" , cmisOrdineMissione.getTerzoMotivoAutoPropria());
			if (ordineMissione.isStatoInviatoAlFlusso() && !StringUtils.isEmpty(ordineMissione.getIdFlusso())){
				jGenerator.writeStringField("prop_bpm_comment" , "AVANZAMENTO");
				jGenerator.writeStringField("prop_wfcnr_reviewOutcome" , FlowResubmitType.RESTART_FLOW.operation());
				jGenerator.writeStringField("prop_transitions" , "Next");
			}
			jGenerator.writeEndObject();
			jGenerator.close();
		} catch (IOException e) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: "+e);
		}

		if (ordineMissione.isStatoNonInviatoAlFlusso()){
			try {
				Response responsePost = missioniCMISService.startFlowOrdineMissione(stringWriter);
				TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
				HashMap<String,Object> mapRichiedente = mapper.readValue(responsePost.getStream(), typeRef); 
				String idFlusso = null;

				String text = mapRichiedente.get("persistedObject").toString();
				String patternString1 = "id=(activiti\\$[0-9]+)";

				Pattern pattern = Pattern.compile(patternString1);
				Matcher matcher = pattern.matcher(text);
				if (matcher.find())
					idFlusso = matcher.group(1);
				ordineMissione.setIdFlusso(idFlusso);
				ordineMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
				if (anticipo != null){
					anticipo.setIdFlusso(idFlusso);
				}
			} catch (AwesomeException e) {
				throw e;
			} catch (Exception e) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
			}
		} else {
			if (ordineMissione.isStatoInviatoAlFlusso() && !StringUtils.isEmpty(ordineMissione.getIdFlusso())){
				ResultFlows result = getFlowsOrdineMissione(ordineMissione.getIdFlusso());
				if (!StringUtils.isEmpty(result.getTaskId())){
					missioniCMISService.restartFlow(stringWriter, result);
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Task Id del flusso non trovato.");
				}
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Stato di invio al flusso non valido.");
			}
		}
	}
	private void aggiungiDocumento(Document documentoAnticipo,
			StringBuilder nodeRefs) {
		if (documentoAnticipo != null){
			if (nodeRefs.length() > 0){
				 nodeRefs.append(",");
			}
			nodeRefs.append((String)documentoAnticipo.getPropertyValue(MissioniCMISService.ALFCMIS_NODEREF));
		 }
	}

	private void aggiungiAllegati(List<CMISFileAttachment> allegati,
			StringBuilder nodeRefs) {
		if (allegati != null && !allegati.isEmpty()){
			for (CMISFileAttachment cmisFileAttachment : allegati){
				if (nodeRefs.length() > 0){
					 nodeRefs.append(",");
				}
				nodeRefs.append(cmisFileAttachment.getNodeRef());
			}
		 }
	}

	public ContentStream getContentStreamOrdineMissione(OrdineMissione ordineMissione) throws ComponentException{
		String id = getNodeRefOrdineMissione(ordineMissione);
		if (id != null){
			return missioniCMISService.recuperoContentFileFromObjectID(id);
		}
		return null;
	}
	
	public ContentStream getContentStreamOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException{
		String id = getNodeRefOrdineMissioneAutoPropria(ordineMissioneAutoPropria);
		if (id != null){
			return missioniCMISService.recuperoContentFileFromObjectID(id);
		}
		return null;
	}
	
	public ContentStream getContentStreamOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo) throws ComponentException{
		String id = getNodeRefOrdineMissioneAnticipo(ordineMissioneAnticipo);
		if (id != null){
			return missioniCMISService.recuperoContentFileFromObjectID(id);
		}
		return null;
	}
	
	public String getNodeRefOrdineMissione(OrdineMissione ordineMissione) throws ComponentException{
		Folder node = recuperoFolderOrdineMissione(ordineMissione);
		if (node == null){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati all'Ordine di Missione. ID Ordine di Missione:"+ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		}
		String folder = (String) node.getPropertyValue(PropertyIds.OBJECT_ID); 
		StringBuilder query = new StringBuilder("select doc.cmis:objectId from cmis:document doc ");
		query.append(" join missioni_ordine_attachment:ordine ordine on doc.cmis:objectId = ordine.cmis:objectId ");
		query.append(" where IN_FOLDER(doc, '").append(folder).append("')");
		ItemIterable<QueryResult> results = missioniCMISService.search(query);
		if (results.getTotalNumItems() == 0)
			return null;
		else if (results.getTotalNumItems() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files ordini di missione aventi l'ID :"+ ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		} else {
			for (QueryResult nodeFile : results) {
				String file = nodeFile.getPropertyValueById(PropertyIds.OBJECT_ID);
				return file;
			}
		}
		return null;
	}
	
	public String getNodeRefOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException{
		OrdineMissione ordineMissione = ordineMissioneAutoPropria.getOrdineMissione();
		Folder node = recuperoFolderOrdineMissione(ordineMissione);
		if (node != null){
			String folder = (String) node.getPropertyValue(PropertyIds.OBJECT_ID); 
			StringBuilder query = new StringBuilder("select doc.cmis:objectId from cmis:document doc ");
			query.append(" join missioni_ordine_attachment:uso_auto_propria auto_propria on doc.cmis:objectId = auto_propria.cmis:objectId ");
			query.append(" where IN_FOLDER(doc, '").append(folder).append("')");
			ItemIterable<QueryResult> results = missioniCMISService.search(query);
			if (results.getTotalNumItems() == 0)
				return null;
			else if (results.getTotalNumItems() > 1){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files  di richiesta di auto propria per l'ordine di missione con ID :"+ ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
			} else {
				for (QueryResult nodeFile : results) {
					String file = nodeFile.getPropertyValueById(PropertyIds.OBJECT_ID);
					return file;
				}
			}
		}
		return null;
	}
	
	public String getNodeRefOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo) {
		OrdineMissione ordineMissione = ordineMissioneAnticipo.getOrdineMissione();
		Folder node = recuperoFolderOrdineMissione(ordineMissione);
		if (node == null){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti di richiesta anticipo collegati all'Ordine di Missione. ID Ordine di Missione:"+ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		}
		String folder = (String) node.getPropertyValue(PropertyIds.OBJECT_ID); 
		StringBuilder query = new StringBuilder("select doc.cmis:objectId from cmis:document doc ");
		query.append(" join missioni_ordine_attachment:richiesta_anticipo anticipo on doc.cmis:objectId = anticipo.cmis:objectId ");
		query.append(" where IN_FOLDER(doc, '").append(folder).append("')");
		ItemIterable<QueryResult> results = missioniCMISService.search(query);
		if (results.getTotalNumItems() == 0)
			return null;
		else if (results.getTotalNumItems() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files  di richiesta anticipo per l'ordine di missione con ID :"+ ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		} else {
			for (QueryResult nodeFile : results) {
				String file = nodeFile.getPropertyValueById(PropertyIds.OBJECT_ID);
				return file;
			}
		}
		return null;
	}
	
	public Folder recuperoFolderOrdineMissione(OrdineMissione ordineMissione){
		StringBuilder query = new StringBuilder("select miss.cmis:objectId from missioni:main as miss "
				+ " join missioni_commons_aspect:ordine_missione ordine on miss.cmis:objectId = ordine.cmis:objectId");
		query.append(" where miss.missioni:id = ").append(ordineMissione.getId());

		ItemIterable<QueryResult> resultsFolder = missioniCMISService.search(query);
		if (resultsFolder.getTotalNumItems() == 0)
			return null;
		else if (resultsFolder.getTotalNumItems() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' ordini di missione aventi l'ID :"+ ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		} else {
			for (QueryResult queryResult : resultsFolder) {
				return (Folder) missioniCMISService.getNodeByNodeRef((String) queryResult.getPropertyValueById(PropertyIds.OBJECT_ID));
			}
		}
		return null;
	}
	
    public void annullaFlusso(OrdineMissione ordineMissione)  {
    	try {
    		abortFlowOrdineMissione(ordineMissione);
    	} catch (AwesomeException e) {
    		throw e;
    	}
		ordineMissione.setStatoFlusso(Costanti.STATO_ANNULLATO);
    }

	public ResultFlows getFlowsOrdineMissione(String idFlusso){
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

	private void abortFlowOrdineMissione(OrdineMissione ordineMissione)  {
    	ResultFlows result = getFlowsOrdineMissione(ordineMissione.getIdFlusso());
    	if (!StringUtils.isEmpty(result.getTaskId())){
        		StringWriter stringWriter = new StringWriter();
        		createJsonForAbortFlowOrdineMissione(stringWriter);
        		missioniCMISService.abortFlow(stringWriter, result);
    	} else {
    		throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Task Id del flusso non trovato.");
    	}
    }

	public QueryResult recuperoFlusso(String idFlusso){
		StringBuilder query = new StringBuilder("select parametriFlusso.*, flussoMissioni.* from cmis:document as t ");
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

	private StringWriter createJsonForAbortFlowOrdineMissione(StringWriter stringWriter) {
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
	public Map<String, Object> createMetadataForFileOrdineMissioneAnticipo(String currentLogin, OrdineMissioneAnticipo anticipo){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(PropertyIds.OBJECT_TYPE_ID, OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename("Anticipo per l'Ordine Missione - anno "+anticipo.getOrdineMissione().getAnno()+" numero "+anticipo.getOrdineMissione().getNumero()));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename("Anticipo Ordine di Missione"));
		metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, OrdineMissioneAnticipo.CMIS_PROPERTY_NAME_DOC_ANTICIPO);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, OrdineMissioneAnticipo.CMIS_PROPERTY_NAME_TIPODOC_ANTICIPO);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, OrdineMissioneAnticipo.CMIS_PROPERTY_NAME_TIPODOC_ANTICIPO);
		return metadataProperties;
	}
	
	public Map<String, Object> createMetadataForFileOrdineMissioneAutoPropria(String currentLogin, OrdineMissioneAutoPropria autoPropria){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(PropertyIds.OBJECT_TYPE_ID, OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename("Richiesta Uso Auto Propria per l'Ordine Missione - anno "+autoPropria.getOrdineMissione().getAnno()+" numero "+autoPropria.getOrdineMissione().getNumero()));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename("Richiesta Uso Auto Propria Ordine di Missione"));
		metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, OrdineMissioneAutoPropria.CMIS_PROPERTY_NAME_DOC_AUTO_PROPRIA);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, OrdineMissioneAutoPropria.CMIS_PROPERTY_NAME_TIPODOC_AUTO_PROPRIA);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, OrdineMissioneAutoPropria.CMIS_PROPERTY_NAME_TIPODOC_AUTO_PROPRIA);
		return metadataProperties;
	}
	
	@Transactional(readOnly = true)
	public Map<String, Object> createMetadataForFileOrdineMissioneAllegati(String currentLogin, String fileName, String tipoAllegato){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(PropertyIds.OBJECT_TYPE_ID, OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename(fileName));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename(fileName));
		metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, OrdineMissione.CMIS_PROPERTY_NAME_DOC_ALLEGATO);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, tipoAllegato);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, tipoAllegato);
		return metadataProperties;
	}

	@Transactional(readOnly = true)
    public Document salvaStampaAutoPropriaSuCMIS(String currentLogin, byte[] stampa,
			OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException {
		InputStream streamStampa = new ByteArrayInputStream(stampa);
		CmisPath cmisPath = createFolderOrdineMissione(ordineMissioneAutoPropria.getOrdineMissione());
		Map<String, Object> metadataProperties = createMetadataForFileOrdineMissioneAutoPropria(currentLogin, ordineMissioneAutoPropria);
		try{
			Document node = missioniCMISService.restoreSimpleDocument(
					metadataProperties,
					streamStampa,
					MimeTypes.PDF.mimetype(),
					ordineMissioneAutoPropria.getFileName(), 
					cmisPath);
			missioniCMISService.addAspect(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_USO_AUTO_PROPRIA.value());
			missioniCMISService.makeVersionable(node);
			return node;
		} catch (Exception e) {
			if (e.getCause() instanceof CmisConstraintException)
				throw new ComponentException("CMIS - File ["+ordineMissioneAutoPropria.getFileName()+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!",e);
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")",e);
		}
	}

	@Transactional(readOnly = true)
	public Document salvaStampaAnticipoSuCMIS(String currentLogin, byte[] stampa,
			OrdineMissioneAnticipo ordineMissioneAnticipo) throws ComponentException {
		InputStream streamStampa = new ByteArrayInputStream(stampa);
		CmisPath cmisPath = createFolderOrdineMissione(ordineMissioneAnticipo.getOrdineMissione());
		Map<String, Object> metadataProperties = createMetadataForFileOrdineMissioneAnticipo(currentLogin, ordineMissioneAnticipo);
		try {
			Document node = missioniCMISService.restoreSimpleDocument(metadataProperties, streamStampa,
					MimeTypes.PDF.mimetype(), ordineMissioneAnticipo.getFileName(), cmisPath);
			missioniCMISService.addAspect(node,
					CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_RICHIESTA_ANTICIPO.value());
			missioniCMISService.makeVersionable(node);
			return node;
		} catch (Exception e) {
			if (e.getCause() instanceof CmisConstraintException)
				throw new ComponentException("CMIS - File [" + ordineMissioneAnticipo.getFileName()
						+ "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!",e);
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale ("
					+ Utility.getMessageException(e) + ")",e);
		}
	}
	private Document creaDocumentoAnticipo(String username, OrdineMissioneAnticipo ordineMissioneAnticipo) throws AwesomeException, ComponentException{
		byte[] print = printOrdineMissioneAnticipoService.printOrdineMissioneAnticipo(ordineMissioneAnticipo, username);
		return salvaStampaAnticipoSuCMIS(username, print, ordineMissioneAnticipo);
	}
	private Document creaDocumentoAutoPropria(String username, OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws AwesomeException, ComponentException{
		byte[] print = printOrdineMissioneAutoPropriaService.printOrdineMissioneAutoPropria(ordineMissioneAutoPropria, username);
		return salvaStampaAutoPropriaSuCMIS(username, print, ordineMissioneAutoPropria);
	}

	public List<CMISFileAttachment> getAttachmentsAnticipo(OrdineMissione ordineMissione, Long idAnticipo) {
		ItemIterable<QueryResult> documents = getAttachmentsAnticipo(ordineMissione);
		if (documents != null){
	        List<CMISFileAttachment> lista = new ArrayList<CMISFileAttachment>();
	        for (QueryResult object : documents){
	        	CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
	        	cmisFileAttachment.setNomeFile(object.getPropertyValueById(PropertyIds.NAME));
	        	cmisFileAttachment.setId(object.getPropertyValueById(PropertyIds.OBJECT_ID));
	        	cmisFileAttachment.setNodeRef(object.getPropertyValueById(MissioniCMISService.ALFCMIS_NODEREF));
	        	cmisFileAttachment.setIdMissione(idAnticipo);
	        	lista.add(cmisFileAttachment);
	        }
	        return lista;
		}
		return Collections.<CMISFileAttachment>emptyList();
	}
		
	public List<CMISFileAttachment> getAttachmentsOrdineMissione(OrdineMissione ordineMissione, Long idOrdineMissione) {
		ItemIterable<QueryResult> documents = getDocumentsOrdineMissione(ordineMissione);
		if (documents != null){
	        List<CMISFileAttachment> lista = new ArrayList<CMISFileAttachment>();
	        for (QueryResult object : documents){
	        	CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
	        	cmisFileAttachment.setNomeFile(object.getPropertyValueById(PropertyIds.NAME));
	        	cmisFileAttachment.setId(object.getPropertyValueById(PropertyIds.OBJECT_ID));
	        	cmisFileAttachment.setNodeRef(object.getPropertyValueById(MissioniCMISService.ALFCMIS_NODEREF));
	        	cmisFileAttachment.setIdMissione(idOrdineMissione);
	        	lista.add(cmisFileAttachment);
	        }
	        return lista;
		}
		return Collections.<CMISFileAttachment>emptyList();
	}
		
	public ItemIterable<QueryResult> getAttachmentsAnticipo(OrdineMissione ordineMissione) {
		Folder folder = recuperoFolderOrdineMissione(ordineMissione);
		if (folder != null){
			return getDocuments(folder, OrdineMissione.ATTACHMENT_ALLEGATO_ANTICIPO);
		}
		return null;
	}

	public ItemIterable<QueryResult> getDocumentsOrdineMissione(OrdineMissione ordineMissione) {
		Folder folder = recuperoFolderOrdineMissione(ordineMissione);
		if (folder != null){
			return getDocuments(folder, OrdineMissione.ATTACHMENT_ALLEGATO_ORDINE_MISSIONE);
		}
		return null;
	}

	public ItemIterable<QueryResult> getDocuments(Folder node, String tipoFile){
		String folder = (String) node.getPropertyValue(PropertyIds.OBJECT_ID);
		StringBuilder query = new StringBuilder("select doc.cmis:objectId, doc.alfcmis:nodeRef, doc.cmis:name from cmis:document doc ");
		query.append(" join "+OrdineMissione.ORDINE_MISSIONE_ATTACHMENT_QUERY_CMIS+ tipoFile
		+" tipoFile on doc.cmis:objectId = tipoFile.cmis:objectId");
		query.append(" where IN_FOLDER(doc, '").append(folder).append("')");
		ItemIterable<QueryResult> results = missioniCMISService.search(query);
		return results;
	}

	public CMISFileAttachment uploadAttachmentAnticipo(Principal principal, OrdineMissione ordineMissione, Long idAnticipo, InputStream inputStream, String name, MimeTypes mimeTypes){
		Document doc = salvaAllegatoAnticipoCMIS(principal, ordineMissione, inputStream, name, mimeTypes);
		if (doc != null){
			CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
			cmisFileAttachment.setId(doc.getId());
			cmisFileAttachment.setNomeFile(name);
	        cmisFileAttachment.setIdMissione(idAnticipo);
			return cmisFileAttachment;
		}
		return null;
	}
	
	private Document salvaAllegatoAnticipoCMIS(Principal principal,
			OrdineMissione ordineMissione, InputStream stream, String fileName,MimeTypes mimeTypes) {
		
		CmisPath cmisPath = buildFolderOrdineMissione(ordineMissione);

		Map<String, Object> metadataProperties = createMetadataForFileOrdineMissioneAllegati(principal.getName(), fileName, OrdineMissione.CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO_ANTICIPO);
		try{
			Document node = missioniCMISService.restoreSimpleDocument(
					metadataProperties,
					stream,
					mimeTypes.mimetype(),
					fileName, 
					cmisPath);
			missioniCMISService.addAspect(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ALLEGATI_ANTICIPO.value());
			missioniCMISService.makeVersionable(node);
			return node;
		} catch (Exception e) {
			if (e.getCause() instanceof CmisConstraintException)
				throw new ComponentException("CMIS - File ["+fileName+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!",e);
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")",e);
		}
	}

	public CmisPath buildFolderOrdineMissione(OrdineMissione ordineMissione) {
		Folder folder = (Folder) recuperoFolderOrdineMissione(ordineMissione);
		CmisPath cmisPath;
		if (folder == null){
			cmisPath = createFolderOrdineMissione(ordineMissione);
		} else {
			cmisPath = CmisPath.construct(folder.getPath());
		}
		return cmisPath;
	}

	public CMISFileAttachment uploadAttachmentOrdineMissione(Principal principal, OrdineMissione ordineMissione, Long idOrdineMissione, InputStream inputStream, String name, MimeTypes mimeTypes){
		Document doc = salvaAllegatoOrdineMissioneCMIS(principal, ordineMissione, inputStream, name, mimeTypes);
		if (doc != null){
			CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
			cmisFileAttachment.setId(doc.getId());
			cmisFileAttachment.setNomeFile(name);
	        cmisFileAttachment.setIdMissione(idOrdineMissione);
			return cmisFileAttachment;
		}
		return null;
	}

	private Document salvaAllegatoOrdineMissioneCMIS(Principal principal,
			OrdineMissione ordineMissione, InputStream stream, String fileName,MimeTypes mimeTypes) {
		
		CmisPath cmisPath = buildFolderOrdineMissione(ordineMissione);

		Map<String, Object> metadataProperties = createMetadataForFileOrdineMissioneAllegati(principal.getName(), fileName, OrdineMissione.CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO);
		try{
			Document node = missioniCMISService.restoreSimpleDocument(
					metadataProperties,
					stream,
					mimeTypes.mimetype(),
					fileName, 
					cmisPath);
			missioniCMISService.addAspect(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ALLEGATI.value());
			missioniCMISService.makeVersionable(node);
			return node;
		} catch (Exception e) {
			if (e.getCause() instanceof CmisConstraintException)
				throw new ComponentException("CMIS - File ["+fileName+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!",e);
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")",e);
		}
	}
}
