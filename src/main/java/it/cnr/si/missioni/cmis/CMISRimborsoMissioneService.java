package it.cnr.si.missioni.cmis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
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
import it.cnr.si.missioni.domain.custom.persistence.Parametri;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.service.DatiIstitutoService;
import it.cnr.si.missioni.service.ParametriService;
import it.cnr.si.missioni.service.PrintRimborsoMissioneService;
import it.cnr.si.missioni.service.RimborsoMissioneDettagliService;
import it.cnr.si.missioni.service.RimborsoMissioneService;
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
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;
import it.cnr.si.missioni.util.proxy.json.object.Voce;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.missioni.util.proxy.json.service.GaeService;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoGaeService;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoService;
import it.cnr.si.missioni.util.proxy.json.service.NazioneService;
import it.cnr.si.missioni.util.proxy.json.service.ProgettoService;
import it.cnr.si.missioni.util.proxy.json.service.UnitaOrganizzativaService;
import it.cnr.si.missioni.util.proxy.json.service.VoceService;

@Service
public class CMISRimborsoMissioneService {
	public static final String PROPERTY_TIPOLOGIA_DOC = "wfcnr:tipologiaDOC";
	public static final String PROPERTY_TIPOLOGIA_DOC_SPECIFICA = "wfcnr:tipologiaDocSpecifica";
	public static final String PROPERTY_TIPOLOGIA_DOC_MISSIONI = "cnrmissioni:tipologiaDocumentoMissione";

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
	private ParametriService parametriService;
	
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

	@Autowired
	private RimborsoMissioneService rimborsoMissioneService;

	@Autowired
	private RimborsoMissioneDettagliService rimborsoMissioneDettagliService;

	@Autowired
	NazioneService nazioneService;
	
	public List<CMISFileAttachment> getAttachmentsDetail(Principal principal, Long idDettagliorimborso) throws ComponentException{
		RimborsoMissioneDettagli dettaglio = rimborsoMissioneDettagliService.getRimborsoMissioneDettaglio(principal, idDettagliorimborso);
		List<CmisObject> children = getChildrenDettaglio(dettaglio);
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
		return Collections.<CMISFileAttachment>emptyList();
	}

	public List<CmisObject> getChildrenDettaglio(RimborsoMissioneDettagli dettaglio) {
		Folder folderDettaglio = getFolderDettaglioRimborso(dettaglio);
		
		if (folderDettaglio != null){
	        List<CmisObject> children = missioniCMISService.recuperoDocumento(folderDettaglio, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_SCONTRINI.value());
	        return children;
		}
		return null;
	}
		
	public Folder getFolderDettaglioRimborso(RimborsoMissioneDettagli dettaglio) throws ComponentException{
		Folder folderRimborso = recuperoFolderRimborsoMissione(dettaglio.getRimborsoMissione());
		if (folderRimborso != null){
			Folder folderDettaglio = null;
			String path = folderRimborso.getPath();
			try {	
				folderDettaglio = (Folder) missioniCMISService.getNodeByPath(path+"/"+dettaglio.constructCMISNomeFile());
			} catch (CmisObjectNotFoundException e){
				return null;
			}
			return folderDettaglio;
		}
		return null;
	}
		
	public void deleteFolderRimborsoMissioneDettaglio(RimborsoMissioneDettagli dettaglio) throws ComponentException{
		Folder folder = getFolderDettaglioRimborso(dettaglio);
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

		String username = "";
//		if (rimborsoMissione.getDatiIstituto() != null && StringUtils.isEmpty(rimborsoMissione.getDatiIstituto().getTipoMailDopoOrdine())){
			username = principal.getName();
//		}
		
		Account account = accountService.loadAccountFromRest(rimborsoMissione.getUid());
		account.setUid(rimborsoMissione.getUid());
		Voce voce = voceService.loadVoce(rimborsoMissione);
		Gae gae = gaeService.loadGae(rimborsoMissione);
		UnitaOrganizzativa uoCompetenza = null;
		LocalDate data = LocalDate.now();
		int anno = data.getYear();

		if (rimborsoMissione.getUoCompetenza() != null){
			uoCompetenza = unitaOrganizzativaService.loadUo(rimborsoMissione.getUoCompetenza(), null, anno);
		}
		UnitaOrganizzativa uoSpesa = unitaOrganizzativaService.loadUo(rimborsoMissione.getUoSpesa(), null, anno);
		UnitaOrganizzativa uoRich = unitaOrganizzativaService.loadUo(rimborsoMissione.getUoRich(), null, anno);
		String descrImpegno = ""; 
		BigDecimal dispImpegno = null;
		if (rimborsoMissione.getPgObbligazione() != null){
			if (gae != null){
				ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(rimborsoMissione);
				if (impegnoGae != null){
					descrImpegno = impegnoGae.getDsObbligazione();
					dispImpegno = impegnoGae.getDisponibilitaImpegno();
				}
			} else {
				Impegno impegno = impegnoService.loadImpegno(rimborsoMissione);
				if (impegno != null){
					descrImpegno = impegno.getDsObbligazione();
					dispImpegno = impegno.getDisponibilitaImpegno();
				}
			}
		}

		String uoCompetenzaPerFlusso = Utility.replace(rimborsoMissione.getUoCompetenza(), ".", "");
		String uoSpesaPerFlusso = Utility.replace(rimborsoMissione.getUoSpesa(), ".", "");
		String uoRichPerFlusso = Utility.replace(rimborsoMissione.getUoRich(), ".", "");
		Uo uoDatiSpesa = uoService.recuperoUo(uoSpesaPerFlusso);
		Uo uoDatiCompetenza = null;
		if (uoCompetenzaPerFlusso != null){
			uoDatiCompetenza = uoService.recuperoUo(uoCompetenzaPerFlusso);
		}
		String userNameFirmatario = null;
		String userNameFirmatarioSpesa = null;
		Boolean usernameImpostati = false;
		if (!Utility.nvl(rimborsoMissione.getCug(),"N").equals("S") && !Costanti.CDS_SAC.equals(rimborsoMissione.getCdsRich())){
			String uoSiglaRich = rimborsoMissione.getUoRich();
			String uoSiglaSpesa = null;
			if (uoDatiSpesa != null && uoDatiSpesa.getFirmaSpesa() != null && uoDatiSpesa.getFirmaSpesa().equals("N")){
				if (StringUtils.hasLength(rimborsoMissione.getUoCompetenza())){
					uoSiglaSpesa = rimborsoMissione.getUoCompetenza();
				} else {
					uoSiglaSpesa = uoSiglaRich;
				}
			} else {
				uoSiglaSpesa = rimborsoMissione.getUoSpesa();
			}
			if (!uoSiglaRich.equals(uoSiglaSpesa) && uoSiglaRich.substring(0,3).equals(uoSiglaSpesa.substring(0,3))){
				UnitaOrganizzativa uoSigla = unitaOrganizzativaService.loadUo(uoSiglaSpesa, null, rimborsoMissione.getAnno());
				if (uoSigla != null && Utility.nvl(uoSigla.getFl_uo_cds()).equals("true")){
					DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(uoSiglaSpesa, rimborsoMissione.getAnno());
					if (Utility.nvl(datiIstituto.getSaltaFirmaUosUoCds(),"N").equals("S")){
						userNameFirmatario = recuperoDirettore(rimborsoMissione, Utility.replace(uoSiglaSpesa, ".", ""), account);
						userNameFirmatarioSpesa = userNameFirmatario;
						usernameImpostati = true;
					}
				}
				uoSigla = unitaOrganizzativaService.loadUo(uoSiglaRich, null, rimborsoMissione.getAnno());
				if (uoSigla != null && Utility.nvl(uoSigla.getFl_uo_cds()).equals("true")){
					DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(uoSiglaRich, rimborsoMissione.getAnno());
					if (Utility.nvl(datiIstituto.getSaltaFirmaUosUoCds(),"N").equals("S")){
						userNameFirmatario = recuperoDirettore(rimborsoMissione, Utility.replace(uoSiglaRich, ".", ""), account);
						userNameFirmatarioSpesa = userNameFirmatario;
						usernameImpostati = true;
					}
				}
			}
		}
		
		if (!usernameImpostati){
			userNameFirmatario = recuperoDirettore(rimborsoMissione, uoRichPerFlusso, account);
			
			Parametri parametri = parametriService.getParametri();
			if (Utility.nvl(rimborsoMissione.getCug(),"N").equals("S") && parametri != null && parametri.getResponsabileCug() != null){
				userNameFirmatarioSpesa = parametri.getResponsabileCug();
			} else  if (uoDatiSpesa != null && uoDatiSpesa.getFirmaSpesa() != null && uoDatiSpesa.getFirmaSpesa().equals("N")){
				if (uoCompetenzaPerFlusso != null){
					if (uoDatiCompetenza != null && uoDatiCompetenza.getFirmaSpesa() != null && uoDatiCompetenza.getFirmaSpesa().equals("N")){
						userNameFirmatarioSpesa = userNameFirmatario;
					} else {
						userNameFirmatarioSpesa = recuperoDirettore(rimborsoMissione, uoCompetenzaPerFlusso, account);
					}
				} else {
					userNameFirmatarioSpesa = userNameFirmatario;
				}
			} else {
				userNameFirmatarioSpesa = recuperoDirettore(rimborsoMissione, uoSpesaPerFlusso, account);
			}
			
		}
		GregorianCalendar dataScadenzaFlusso = new GregorianCalendar();
		dataScadenzaFlusso.setTime(DateUtils.getCurrentTime());
		dataScadenzaFlusso.add(Calendar.DAY_OF_MONTH, 7);

		cmisRimborsoMissione.setAnno(rimborsoMissione.getAnno().toString());
		cmisRimborsoMissione.setNumero(rimborsoMissione.getNumero().toString());
		cmisRimborsoMissione.setCapitolo(voce == null ? "" : rimborsoMissione.getVoce());
		cmisRimborsoMissione.setDescrizioneCapitolo(voce == null ? "" : voce.getDs_elemento_voce());
		cmisRimborsoMissione.setDescrizioneGae(gae == null ? "" : Utility.nvl(gae.getDs_linea_attivita(),""));
		cmisRimborsoMissione.setDescrizioneImpegno(descrImpegno);
		cmisRimborsoMissione.setDescrizioneUoOrdine(uoRich == null ? "" : uoRich.getDs_unita_organizzativa());
		cmisRimborsoMissione.setDescrizioneUoSpesa(uoSpesa == null ? "" : uoSpesa.getDs_unita_organizzativa());
		cmisRimborsoMissione.setDescrizioneUoCompetenza(uoCompetenza == null ? "" : uoCompetenza.getDs_unita_organizzativa());
		cmisRimborsoMissione.setDisponibilita(Utility.nvl(dispImpegno));
		cmisRimborsoMissione.setGae(gae == null ? "" : gae.getCd_linea_attivita());
		cmisRimborsoMissione.setImpegnoAnnoCompetenza(rimborsoMissione.getEsercizioObbligazione() == null ? null : new Long(rimborsoMissione.getEsercizioObbligazione()));
		cmisRimborsoMissione.setImpegnoAnnoResiduo(rimborsoMissione.getEsercizioOriginaleObbligazione() == null ? null : new Long(rimborsoMissione.getEsercizioOriginaleObbligazione()));
		cmisRimborsoMissione.setImpegnoNumero(rimborsoMissione.getPgObbligazione());
		cmisRimborsoMissione.setNoleggioFlag(rimborsoMissione.getUtilizzoAutoNoleggio().equals("S") ? "true" : "false");
		cmisRimborsoMissione.setNote(rimborsoMissione.getNote() == null ? "" : rimborsoMissione.getNote());
		cmisRimborsoMissione.setNoteSegreteria(rimborsoMissione.getNoteSegreteria() == null ? "" : rimborsoMissione.getNoteSegreteria());
		cmisRimborsoMissione.setOggetto(rimborsoMissione.getOggetto());
		cmisRimborsoMissione.setTaxiFlag(rimborsoMissione.getUtilizzoTaxi().equals("S") ? "true" : "false");
		cmisRimborsoMissione.setAutoServizioFlag(rimborsoMissione.getUtilizzoAutoServizio().equals("S") ? "true" : "false");
		cmisRimborsoMissione.setPersonaSeguitoFlag(rimborsoMissione.getPersonaleAlSeguito().equals("S") ? "true" : "false");
		cmisRimborsoMissione.setUoOrdine(uoRichPerFlusso);
		cmisRimborsoMissione.setUoSpesa(uoSpesaPerFlusso);
		cmisRimborsoMissione.setUoCompetenza(uoCompetenzaPerFlusso == null ? "" : uoCompetenzaPerFlusso);
		cmisRimborsoMissione.setUserNameFirmatarioSpesa(userNameFirmatarioSpesa);
		cmisRimborsoMissione.setUserNamePrimoFirmatario(userNameFirmatario);
		cmisRimborsoMissione.setUserNameResponsabileModulo("");
		cmisRimborsoMissione.setUsernameRichiedente(username);
		cmisRimborsoMissione.setNoteAutorizzazioniAggiuntive(rimborsoMissione.getNoteUtilizzoTaxiNoleggio() == null ? "": rimborsoMissione.getNoteUtilizzoTaxiNoleggio());
		cmisRimborsoMissione.setAnticipoRicevuto(rimborsoMissione.getAnticipoRicevuto().equals("S") ? "true" : "false");
		cmisRimborsoMissione.setAnnoMandato(rimborsoMissione.getAnticipoAnnoMandato() == null ? "" : rimborsoMissione.getAnticipoAnnoMandato().toString());
		cmisRimborsoMissione.setNumeroMandato(rimborsoMissione.getAnticipoNumeroMandato() == null ? "" : rimborsoMissione.getAnticipoNumeroMandato().toString());
		cmisRimborsoMissione.setImportoMandato(rimborsoMissione.getAnticipoImporto() == null ? "" : Utility.nvl(rimborsoMissione.getAnticipoImporto()).toString());
		cmisRimborsoMissione.setUsernameUtenteOrdine(rimborsoMissione.getUid());
		cmisRimborsoMissione.setUsernameUtenteOrdine(rimborsoMissione.getUid());
		cmisRimborsoMissione.setValidazioneSpesa(impostaValidazioneSpesa(userNameFirmatario, userNameFirmatarioSpesa));
		cmisRimborsoMissione.setWfDescription("Rimborso Missione n. "+rimborsoMissione.getNumero()+" di "+account.getCognome() + " "+account.getNome());
		cmisRimborsoMissione.setWfDueDate(DateUtils.getDateAsString(dataScadenzaFlusso.getTime(), DateUtils.PATTERN_DATE_FOR_DOCUMENTALE));
		cmisRimborsoMissione.setDestinazione(rimborsoMissione.getDestinazione());
		cmisRimborsoMissione.setTrattamento(rimborsoMissione.decodeTrattamento());
		cmisRimborsoMissione.setDifferenzeOrdineRimborso(rimborsoMissioneService.getDifferenzeRimborsoOrdine(principal, rimborsoMissione));
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
		cmisRimborsoMissione.setTotaleRimborsoMissione(rimborsoMissione.getTotaleRimborso());
		return cmisRimborsoMissione;
	}

	private String recuperoDirettore(RimborsoMissione rimborsoMissione, String uo, Account account) {
		String userNameFirmatario;
		if (isDevProfile()){
			userNameFirmatario = recuperoUidDirettoreUo(uo);
		} else {
			userNameFirmatario = accountService.recuperoDirettore(rimborsoMissione.getAnno(), uo, rimborsoMissione.isMissioneEstera(), account, rimborsoMissione.getDataInizioMissione());
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
	
	private void caricaDatiDerivati(Principal principal, RimborsoMissione rimborsoMissione) throws ComponentException {
		if (rimborsoMissione != null){
			DatiIstituto dati = datiIstitutoService.getDatiIstituto(rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno());
			if (dati == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non esistono i dati per uo per il codice "+rimborsoMissione.getUoSpesa()+" nell'anno "+rimborsoMissione.getAnno());
//				dati = datiIstitutoService.creaDatiIstitutoOrdine(principal, rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno());
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
		Folder node = recuperoFolderRimborsoMissione(rimborsoMissione);
		if (node != null){
			return CmisPath.construct(node.getPath());
		}
		CmisPath cmisPath = missioniCMISService.getBasePath();
		cmisPath = missioniCMISService.createFolderIfNotPresent(cmisPath, rimborsoMissione.getUoSpesa());
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
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_NOTE_SEGRETERIA, rimborsoMissione.getNoteSegreteria());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_DATA_INIZIO, DateUtils.getDate(rimborsoMissione.getDataInizioMissione()));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_DATA_FINE, DateUtils.getDate(rimborsoMissione.getDataFineMissione()));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_NAME_DATA_INSERIMENTO, DateUtils.getDate(rimborsoMissione.getDataInserimento()));
		if (rimborsoMissione.getDataInizioEstero() != null){
			metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_NAME_DATA_INIZIO_MISSIONE_ESTERO, DateUtils.getDate(rimborsoMissione.getDataInizioEstero()));
			metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_NAME_DATA_FINE_MISSIONE_ESTERO, DateUtils.getDate(rimborsoMissione.getDataFineEstero()));
		}
		metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_NAME_ID_ORDINE_MISSIONE, rimborsoMissione.getOrdineMissione().getId());
		metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_NAME_TOT_RIMBORSO_MISSIONE, rimborsoMissione.getTotaleRimborso());
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
		metadataProperties.put(RimborsoMissioneDettagli.CMIS_PROPERTY_DATA_SPESA_DETTAGLIO_RIMBORSO_MISSIONE, DateUtils.getDate(dettaglio.getDataSpesa()));
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
				throw new ComponentException("CMIS - File ["+rimborsoMissione.getFileName()+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!",e);
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")",e);
		}
	}

	private Folder getFolderRimborso(RimborsoMissione rimborso){
		StringBuilder query = new StringBuilder("select rim.cmis:objectId from missioni:main missioni join missioni_commons_aspect:rimborso_missione rim on missioni.cmis:objectId = rim.cmis:objectId ");
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
	
//	private Folder getFolderDettaglioRimborso(Long idDettagliorimborso){
//		StringBuilder query = new StringBuilder("select cmis:objectId from missioni_rimborso_dettaglio:main ");
//		query.append(" where missioni_rimborso_dettaglio:id = ").append(idDettagliorimborso);
//		ItemIterable<QueryResult> resultsFolder = missioniCMISService.search(query);
//		if (resultsFolder.getTotalNumItems() == 0)
//			return null;
//		else if (resultsFolder.getTotalNumItems() > 1){
//			throw new AwesomeException("Errore di sistema, esistono sul documentale piu' cartelle per lo stesso dettaglio di rimborso missione.  Id:"+ idDettagliorimborso);
//		} else {
//			for (QueryResult queryResult : resultsFolder) {
//				return (Folder) missioniCMISService.getNodeByNodeRef((String) queryResult.getPropertyValueById(PropertyIds.OBJECT_ID));
//			}
//		}
//		return null;
//	}
//	
	public String getNodeRefFolderDettaglioRimborso(RimborsoMissioneDettagli dettagliorimborso){
		Folder folder = getFolderDettaglioRimborso(dettagliorimborso);
		if (folder != null){
			return 	folder.getId();
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
		CmisPath cmisPath ;
		if (folder == null){
			cmisPath = createFolderRimborsoMissione(dettaglio.getRimborsoMissione());
		} else {
			cmisPath = CmisPath.construct(folder.getPath());
		}

		cmisPath = createLastFolderDettaglioIfNotPresent(cmisPath, dettaglio);

		Map<String, Object> metadataProperties = createMetadataForFileRimborsoMissioneAllegati(principal.getName(), fileName, RimborsoMissione.CMIS_PROPERTY_NAME_TIPODOC_SCONTRINO);
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
				throw new ComponentException("CMIS - File ["+fileName+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!",e);
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")",e);
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


	private Map<String, Object> createMetadataForFileRimborsoMissioneAllegati(String currentLogin, String fileName, String tipoDocumento){
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
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_GAE, cmisRimborsoMissione.getGae());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_NOTE, cmisRimborsoMissione.getNote());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_NOTE_SEGRETERIA, cmisRimborsoMissione.getNoteSegreteria());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_NOTE_AUTORIZZAZIONI_AGGIUNTIVE, cmisRimborsoMissione.getNoteAutorizzazioniAggiuntive());
		
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_NUMERO_IMPEGNO, cmisRimborsoMissione.getImpegnoNumero());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_TAXI, cmisRimborsoMissione.getTaxiFlag().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_AUTO_SERVIZIO, cmisRimborsoMissione.getAutoServizioFlag().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_PERSONA_SEGUITO, cmisRimborsoMissione.getPersonaSeguitoFlag().equals("true"));
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
		metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_FLOW_TOTALE_RIMBORSO_MISSIONE, cmisRimborsoMissione.getTotaleRimborsoMissione());
		metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_FLOW_DIFFERENZE_ORDINE_RIMBORSO, cmisRimborsoMissione.getDifferenzeOrdineRimborso());

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
		StringBuilder nodeRefs = new StringBuilder();
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
			
			aggiungiAllegatiRimborsoMissione(rimborsoMissione, nodeRefs);
			
			jGenerator.writeStringField("assoc_packageItems_added" , nodeRefs.toString());
			jGenerator.writeStringField("assoc_packageItems_removed" , "");
			if (rimborsoMissione.isStatoNonInviatoAlFlusso()){
				jGenerator.writeStringField("prop_bpm_comment" , "");
				jGenerator.writeStringField("prop_bpm_percentComplete" , "0");
			}
			jGenerator.writeStringField("prop_cnrmissioni_noteAutorizzazioniAggiuntive" , cmisRimborsoMissione.getNoteAutorizzazioniAggiuntive());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneOrdine" , cmisRimborsoMissione.getOggetto());
			jGenerator.writeStringField("prop_cnrmissioni_note" , cmisRimborsoMissione.getNote());
			jGenerator.writeStringField("prop_cnrmissioni_noteSegreteria" , cmisRimborsoMissione.getNoteSegreteria());
			jGenerator.writeStringField("prop_bpm_workflowDescription" , cmisRimborsoMissione.getWfDescription());
			jGenerator.writeStringField("prop_bpm_sendEMailNotifications" , "false");
			jGenerator.writeStringField("prop_bpm_workflowDueDate" , cmisRimborsoMissione.getWfDueDate());
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
			jGenerator.writeStringField("prop_cnrmissioni_userNameAmministrativo1" , "");
			jGenerator.writeStringField("prop_cnrmissioni_userNameAmministrativo2" , "");
			jGenerator.writeStringField("prop_cnrmissioni_userNameAmministrativo3" , "");
			jGenerator.writeStringField("prop_cnrmissioni_uoOrdine" , cmisRimborsoMissione.getUoOrdine());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneUoOrdine" , cmisRimborsoMissione.getDescrizioneUoOrdine());
			jGenerator.writeStringField("prop_cnrmissioni_uoSpesa" , cmisRimborsoMissione.getUoSpesa());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneUoSpesa" , cmisRimborsoMissione.getDescrizioneUoSpesa());
			jGenerator.writeStringField("prop_cnrmissioni_uoCompetenza" , cmisRimborsoMissione.getUoCompetenza());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneUoCompetenza" , cmisRimborsoMissione.getDescrizioneUoCompetenza());
			jGenerator.writeStringField("prop_cnrmissioni_noleggioFlag" , cmisRimborsoMissione.getNoleggioFlag());
			jGenerator.writeStringField("prop_cnrmissioni_taxiFlag" , cmisRimborsoMissione.getTaxiFlag());
			jGenerator.writeStringField("prop_cnrmissioni_servizioFlagOk" , cmisRimborsoMissione.getAutoServizioFlag());
			jGenerator.writeStringField("prop_cnrmissioni_personaSeguitoFlagOk" , cmisRimborsoMissione.getPersonaSeguitoFlag());
			jGenerator.writeStringField("prop_cnrmissioni_capitolo" , cmisRimborsoMissione.getCapitolo());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneCapitolo" , cmisRimborsoMissione.getDescrizioneCapitolo());
			jGenerator.writeStringField("prop_cnrmissioni_gae" , cmisRimborsoMissione.getGae());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneGae" , cmisRimborsoMissione.getDescrizioneGae());
			jGenerator.writeStringField("prop_cnrmissioni_impegnoAnnoResiduo" , cmisRimborsoMissione.getImpegnoAnnoResiduo() == null ? "": cmisRimborsoMissione.getImpegnoAnnoResiduo().toString());
			jGenerator.writeStringField("prop_cnrmissioni_impegnoAnnoCompetenza" , cmisRimborsoMissione.getImpegnoAnnoCompetenza() == null ? "": cmisRimborsoMissione.getImpegnoAnnoCompetenza().toString());
			jGenerator.writeStringField("prop_cnrmissioni_impegnoNumeroOk" , cmisRimborsoMissione.getImpegnoNumero() == null ? "": cmisRimborsoMissione.getImpegnoNumero().toString());
			jGenerator.writeStringField("prop_cnrmissioni_descrizioneImpegno" , cmisRimborsoMissione.getDescrizioneImpegno());
			jGenerator.writeStringField("prop_cnrmissioni_importoMissione" , cmisRimborsoMissione.getImportoMissione() == null ? "": cmisRimborsoMissione.getImportoMissione().toString());
			jGenerator.writeStringField("prop_cnrmissioni_disponibilita" , cmisRimborsoMissione.getDisponibilita() == null ? "": cmisRimborsoMissione.getDisponibilita().toString());
			jGenerator.writeStringField("prop_cnrmissioni_missioneEsteraFlag" , cmisRimborsoMissione.getMissioneEsteraFlag());
			jGenerator.writeStringField("prop_cnrmissioni_destinazione" , cmisRimborsoMissione.getDestinazione());
			jGenerator.writeStringField("prop_cnrmissioni_dataInizioMissione" , cmisRimborsoMissione.getDataInizioMissione());
			jGenerator.writeStringField("prop_cnrmissioni_dataFineMissione" , cmisRimborsoMissione.getDataFineMissione());
			jGenerator.writeStringField("prop_cnrmissioni_trattamento" , cmisRimborsoMissione.getTrattamento());
			jGenerator.writeStringField("prop_cnrmissioni_dataInizioEstero" , cmisRimborsoMissione.getDataInizioEstero() == null ? "" : cmisRimborsoMissione.getDataInizioEstero());
			jGenerator.writeStringField("prop_cnrmissioni_dataFineEstero" , cmisRimborsoMissione.getDataFineEstero() == null ? "" : cmisRimborsoMissione.getDataFineEstero());
			jGenerator.writeStringField("prop_cnrmissioni_anticipoRicevuto" , cmisRimborsoMissione.getAnticipoRicevuto());
			jGenerator.writeStringField("prop_cnrmissioni_annoMandato" , cmisRimborsoMissione.getAnnoMandato());
			jGenerator.writeStringField("prop_cnrmissioni_numeroMandatoOk" , cmisRimborsoMissione.getNumeroMandato());
			jGenerator.writeStringField("prop_cnrmissioni_importoMandato" , cmisRimborsoMissione.getImportoMandato());
			jGenerator.writeStringField("prop_cnrmissioni_wfOrdineDaRimborso" , cmisRimborsoMissione.getWfOrdineMissione());
			jGenerator.writeStringField("prop_cnrmissioni_differenzeOrdineRimborso" , cmisRimborsoMissione.getDifferenzeOrdineRimborso());
			jGenerator.writeStringField("prop_cnrmissioni_totaleRimborsoMissione" , cmisRimborsoMissione.getTotaleRimborsoMissione() == null ? "": cmisRimborsoMissione.getTotaleRimborsoMissione().toString());
			if (rimborsoMissione.isStatoInviatoAlFlusso() && !StringUtils.isEmpty(rimborsoMissione.getIdFlusso())){
				jGenerator.writeStringField("prop_bpm_comment" , "AVANZAMENTO");
				jGenerator.writeStringField("prop_wfcnr_reviewOutcome" , FlowResubmitType.RESTART_FLOW.operation());
				jGenerator.writeStringField("prop_transitions" , "Next");
			}
			jGenerator.writeEndObject();
			jGenerator.close();
		} catch (IOException e) {
			throw new ComponentException("Errore in fase avvio flusso documentale. Errore: "+e,e);
		}

		avviaFlusso(rimborsoMissione, stringWriter, mapper);
	}

	private void aggiungiAllegatiRimborsoMissione(RimborsoMissione rimborsoMissione, StringBuilder nodeRefs) {
		List<CMISFileAttachment> allegatiRimborsoMissione = getAttachmentsRimborsoMissione(rimborsoMissione, new Long(rimborsoMissione.getId().toString()));
		List<String> list = new ArrayList<>();
		List<String> listName = new ArrayList<>();
		if (allegatiRimborsoMissione != null && !allegatiRimborsoMissione.isEmpty()){
			for (CMISFileAttachment cmisFileAttachment : allegatiRimborsoMissione){
				if (nodeRefs.length() > 0){
					 nodeRefs.append(",");
				}
				list.add(cmisFileAttachment.getNodeRef());
				listName.add(cmisFileAttachment.getNomeFile());
				nodeRefs.append(cmisFileAttachment.getNodeRef());
			}
		}
		if (rimborsoMissione.getRimborsoMissioneDettagli() != null && !rimborsoMissione.getRimborsoMissioneDettagli().isEmpty()){
			for (RimborsoMissioneDettagli dettaglio : rimborsoMissione.getRimborsoMissioneDettagli()){
				List<CmisObject> children = getChildrenDettaglio(dettaglio);
				if (children != null){
					for (CmisObject object : children){
				    	Document doc = (Document)object;
				    	String nodeRef = (String)doc.getPropertyValue(MissioniCMISService.ALFCMIS_NODEREF);
				    	String nodeName = (String)doc.getPropertyValue(PropertyIds.NAME);
				    	if (!list.contains(nodeRef) && !listName.contains(nodeName)){
							aggiungiDocumento(nodeRef, nodeRefs);
							list.add(nodeRef);
							listName.add(nodeName);
				    	}
				    }
				} else {
					if (dettaglio.isGiustificativoObbligatorio() && !StringUtils.hasLength(dettaglio.getDsNoGiustificativo())){
						throw new AwesomeException(CodiciErrore.ERRGEN, "Per il dettaglio spesa "+ dettaglio.getDsTiSpesa()+" del "+ DateUtils.getDefaultDateAsString(dettaglio.getDataSpesa())+ " è obbligatorio allegare almeno un giustificativo.");
					}
				}
			}
		}
	}

	private void avviaFlusso(RimborsoMissione rimborsoMissione, StringWriter stringWriter, ObjectMapper mapper) {
		if (rimborsoMissione.isStatoNonInviatoAlFlusso()){
			if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno()).getTipoMailDopoRimborso(),"N").equals("C")){
				rimborsoMissioneService.popolaCoda(rimborsoMissione);
			} else {
				startFlow(rimborsoMissione, stringWriter, mapper);
			}
		} else {
			if (rimborsoMissione.isStatoInviatoAlFlusso() && !StringUtils.isEmpty(rimborsoMissione.getIdFlusso())){
				restartFlow(rimborsoMissione, stringWriter);
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Stato di invio al flusso non valido.");
			}
		}
	}

	private void startFlow(RimborsoMissione rimborsoMissione, StringWriter stringWriter, ObjectMapper mapper) {
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
			throw new ComponentException("Errore in fase avvio flusso documentale. Errore: " + Utility.getMessageException(e) + ".",e);
		}
	}

	private void restartFlow(RimborsoMissione rimborsoMissione, StringWriter stringWriter) {
		ResultFlows result = getFlowsRimborsoMissione(rimborsoMissione.getIdFlusso());
		if (!StringUtils.isEmpty(result.getTaskId())){
			missioniCMISService.restartFlow(stringWriter, result);
		} else {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Task Id del flusso non trovato.");
		}
	}

	public void controlloEsitenzaGiustificativoDettaglio(RimborsoMissione rimborsoMissione)
			throws ComponentException {
		if (rimborsoMissione.getRimborsoMissioneDettagli() != null && !rimborsoMissione.getRimborsoMissioneDettagli().isEmpty()){
			for (RimborsoMissioneDettagli dettaglio : rimborsoMissione.getRimborsoMissioneDettagli()){
				List<CmisObject> children = getChildrenDettaglio(dettaglio);
				if (children == null && dettaglio.isGiustificativoObbligatorio() && StringUtils.isEmpty(dettaglio.getDsNoGiustificativo())){
					throw new AwesomeException(CodiciErrore.ERRGEN, "Per il dettaglio spesa "+ dettaglio.getDsTiSpesa()+" del "+ DateUtils.getDefaultDateAsString(dettaglio.getDataSpesa())+ " è obbligatorio allegare almeno un giustificativo.");
				}
			}
		}
	}
	private void aggiungiDocumento(Document documento,
			StringBuilder nodeRefs) {
		if (documento != null){
			 aggiungiDocumento((String)documento.getPropertyValue(MissioniCMISService.ALFCMIS_NODEREF), nodeRefs);
		 }
	}

	private void aggiungiDocumento(String nodeRef,
			StringBuilder nodeRefs) {
		if (nodeRef != null){
			if (nodeRefs.length() > 0){
				 nodeRefs.append(",");
			}
			 nodeRefs.append(nodeRef);
		 }
	}

	public String getNodeRefRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException{
		Folder node = recuperoFolderRimborsoMissione(rimborsoMissione);
		List<CmisObject> rimborso = missioniCMISService.recuperoDocumento(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_RIMBORSO.value());
		if (rimborso.size() == 0)
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati al Rimborso di Missione. ID Rimborso di Missione:"+rimborsoMissione.getId()+", Anno:"+rimborsoMissione.getAnno()+", Numero:"+rimborsoMissione.getNumero());
		else if (rimborso.size() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files di rimborso di missione aventi l'ID :"+ rimborsoMissione.getId()+", Anno:"+rimborsoMissione.getAnno()+", Numero:"+rimborsoMissione.getNumero());
		} else {
				CmisObject nodeFile = rimborso.get(0); 
				return nodeFile.getId();
		}
	}
	
	public Folder recuperoFolderRimborsoMissione(RimborsoMissione rimborsoMissione)throws ComponentException{
		StringBuilder query = new StringBuilder("select miss.cmis:objectId from missioni:main as miss "
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
	
    public void annullaFlusso(RimborsoMissione rimborsoMissione) throws AwesomeException {
    	abortFlowRimborsoMissione(rimborsoMissione);
    	rimborsoMissione.setStatoFlusso(Costanti.STATO_ANNULLATO);
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

	private void abortFlowRimborsoMissione(RimborsoMissione rimborsoMissione) throws AwesomeException {
		ResultFlows result = getFlowsRimborsoMissione(rimborsoMissione.getIdFlusso());
		if (!StringUtils.isEmpty(result.getTaskId())){
			StringWriter stringWriter = createJsonForAbortFlowOrdineMissione();
			missioniCMISService.abortFlow(stringWriter, result);
		} else {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Task Id del flusso non trovato.");
		}
    }

	public QueryResult recuperoFlusso(String idFlusso)throws AwesomeException{
		return missioniCMISService.recupeorFlusso(idFlusso);
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
			throw new ComponentException("Errore in fase di scrittura dei file del flusso per l'avanzamento del documentale. Errore: "+e,e);
		}
		return stringWriter;
	}
	public ContentStream getContentStreamRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException{
		String id = getNodeRefRimborsoMissione(rimborsoMissione);
		if (id != null){
			return missioniCMISService.recuperoContentFileFromObjectID(id);
		}
		return null;
	}
	
	public CmisPath buildFolderRimborsoMissione(RimborsoMissione rimborsoMissione) {
		Folder folder = (Folder) recuperoFolderRimborsoMissione(rimborsoMissione);
		CmisPath cmisPath;
		if (folder == null){
			cmisPath = createFolderRimborsoMissione(rimborsoMissione);
		} else {
			cmisPath = CmisPath.construct(folder.getPath());
		}
		return cmisPath;
	}

	public CMISFileAttachment uploadAttachmentRimborsoMissione(Principal principal, RimborsoMissione rimborsoMissione, Long idRimborsoMissione, InputStream inputStream, String name, MimeTypes mimeTypes){
		Document doc = salvaAllegatoRimborsoMissioneCMIS(principal, rimborsoMissione, inputStream, name, mimeTypes);
		if (doc != null){
			CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
			cmisFileAttachment.setId(doc.getId());
			cmisFileAttachment.setNomeFile(name);
	        cmisFileAttachment.setIdMissione(idRimborsoMissione);
			return cmisFileAttachment;
		}
		return null;
	}

	private Document salvaAllegatoRimborsoMissioneCMIS(Principal principal,
			RimborsoMissione rimborsoMissione, InputStream stream, String fileName,MimeTypes mimeTypes) {
		
		CmisPath cmisPath = buildFolderRimborsoMissione(rimborsoMissione);

		Map<String, Object> metadataProperties = createMetadataForFileRimborsoMissioneAllegati(principal.getName(), fileName, RimborsoMissione.CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO);
		try{
			Document node = missioniCMISService.restoreSimpleDocument(
					metadataProperties,
					stream,
					mimeTypes.mimetype(),
					fileName, 
					cmisPath);
			missioniCMISService.addAspect(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_ALLEGATI.value());
			missioniCMISService.makeVersionable(node);
			return node;
		} catch (Exception e) {
			if (e.getCause() instanceof CmisConstraintException)
				throw new ComponentException("CMIS - File ["+fileName+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!",e);
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")",e);
		}
	}

	public List<CMISFileAttachment> getAttachmentsRimborsoMissione(RimborsoMissione rimborsoMissione, Long idRimborsoMissione) {
		List<CmisObject> documents = getDocumentsAllegatiRimborsoMissione(rimborsoMissione);
		if (documents != null){
	        List<CMISFileAttachment> lista = new ArrayList<CMISFileAttachment>();
	        for (CmisObject object : documents){
	        	CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
	        	cmisFileAttachment.setNomeFile(object.getName());
	        	cmisFileAttachment.setId(object.getId());
	        	cmisFileAttachment.setNodeRef(object.getPropertyValue(MissioniCMISService.ALFCMIS_NODEREF));
	        	cmisFileAttachment.setIdMissione(idRimborsoMissione);
	        	lista.add(cmisFileAttachment);
	        }
	        return lista;
		}
		return Collections.<CMISFileAttachment>emptyList();
	}

	public List<CmisObject> getDocumentsAllegatiRimborsoMissione(RimborsoMissione rimborsoMissione) {
		Folder node = recuperoFolderRimborsoMissione(rimborsoMissione);
		return missioniCMISService.recuperoDocumento(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_ALLEGATI.value());
	}
}
