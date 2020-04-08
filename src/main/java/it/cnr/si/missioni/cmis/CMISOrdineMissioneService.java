package it.cnr.si.missioni.cmis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.flows.FlowResubmitType;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAnticipo;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.Parametri;
import it.cnr.si.missioni.service.AnnullamentoOrdineMissioneService;
import it.cnr.si.missioni.service.DatiIstitutoService;
import it.cnr.si.missioni.service.OrdineMissioneAnticipoService;
import it.cnr.si.missioni.service.OrdineMissioneAutoPropriaService;
import it.cnr.si.missioni.service.OrdineMissioneService;
import it.cnr.si.missioni.service.ParametriService;
import it.cnr.si.missioni.service.PrintAnnullamentoOrdineMissioneService;
import it.cnr.si.missioni.service.PrintOrdineMissioneAnticipoService;
import it.cnr.si.missioni.service.PrintOrdineMissioneAutoPropriaService;
import it.cnr.si.missioni.service.PrintOrdineMissioneService;
import it.cnr.si.missioni.service.UoService;
import it.cnr.si.missioni.service.UtentiPresidenteSpecialiService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.data.UtentePresidenteSpeciale;
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
import it.cnr.si.spring.storage.StorageException;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.StorageService;
import it.cnr.si.spring.storage.config.StoragePropertyNames;

@Service
public class CMISOrdineMissioneService {
	private static final Log logger = LogFactory.getLog(CMISOrdineMissioneService.class);

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
	private PrintAnnullamentoOrdineMissioneService printAnnullamentoOrdineMissioneService;

	@Autowired
	private OrdineMissioneService ordineMissioneService;

	@Autowired
	private ParametriService parametriService;

	@Autowired
	private AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;

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
	private UtentiPresidenteSpecialiService utentiPresidenteSpecialeService;
	
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
		return create(principal, ordineMissione,ordineMissione.getAnno());
	}
	public CMISOrdineMissione create(Principal principal, OrdineMissione ordineMissione, Integer annoGestione) throws ComponentException{
		if (ordineMissione != null){
			CMISOrdineMissione cmisOrdineMissione = new CMISOrdineMissione();
			Account account = accountService.loadAccountFromRest(ordineMissione.getUid());
			account.setUid(ordineMissione.getUid());
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
			String username = "";
				username = principal.getName();
			
			LocalDate data = LocalDate.now();
			int anno = data.getYear();
			
			Progetto progetto = progettoService.loadModulo(ordineMissione.getPgProgetto(), anno, ordineMissione.getUoSpesa());
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
					if (impegnoGae != null){
						descrImpegno = impegnoGae.getDsObbligazione();
						dispImpegno = impegnoGae.getDisponibilitaImpegno();
					}
				} else {
					Impegno impegno = impegnoService.loadImpegno(ordineMissione);
					if (impegno != null){
						descrImpegno = impegno.getDsObbligazione();
						dispImpegno = impegno.getDisponibilitaImpegno();
					}
				}
			}

			DatiIstituto datiIstitutoUoRich = datiIstitutoService.getDatiIstituto(ordineMissione.getUoRich(), annoGestione);
			String uoCompetenzaPerFlusso = Utility.replace(ordineMissione.getUoCompetenza(), ".", "");
			String uoSpesaPerFlusso = Utility.replace(ordineMissione.getUoSpesa(), ".", "");
			String uoRichPerFlusso = Utility.replace(ordineMissione.getUoRich(), ".", "");
			Uo uoDatiSpesa = uoService.recuperoUo(uoSpesaPerFlusso);
			Uo uoDatiCompetenza = null;
			if (uoCompetenzaPerFlusso != null){
				uoDatiCompetenza = uoService.recuperoUo(uoCompetenzaPerFlusso);
			}
			
			String userNameFirmatario = null;
			String userNameFirmatarioSpesa = null;
			Boolean usernameImpostati = false;
			if (!Utility.nvl(ordineMissione.getCug(),"N").equals("S") && !Utility.nvl(ordineMissione.getPresidente(),"N").equals("S") && !Costanti.CDS_SAC.equals(ordineMissione.getCdsRich())){
				String uoSiglaRich = ordineMissione.getUoRich();
				String uoSiglaSpesa = null;
				if (uoDatiSpesa != null && uoDatiSpesa.getFirmaSpesa() != null && uoDatiSpesa.getFirmaSpesa().equals("N")){
					if (StringUtils.hasLength(ordineMissione.getUoCompetenza())){
						uoSiglaSpesa = ordineMissione.getUoCompetenza();
					} else {
						uoSiglaSpesa = uoSiglaRich;
					}
				} else {
					uoSiglaSpesa = ordineMissione.getUoSpesa();
				}
				if (!uoSiglaRich.equals(uoSiglaSpesa) && uoSiglaRich.substring(0,3).equals(uoSiglaSpesa.substring(0,3))){
					UnitaOrganizzativa uoSigla = unitaOrganizzativaService.loadUo(uoSiglaSpesa, null, ordineMissione.getAnno());
					if (uoSigla != null && Utility.nvl(uoSigla.getFl_uo_cds()).equals("true")){
						DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(uoSiglaSpesa, annoGestione);
						if (Utility.nvl(datiIstituto.getSaltaFirmaUosUoCds(),"N").equals("S")){
							userNameFirmatario = recuperoDirettore(ordineMissione, Utility.replace(uoSiglaSpesa, ".", ""), account, annoGestione);
							userNameFirmatarioSpesa = userNameFirmatario;
							usernameImpostati = true;
						}
					}
					uoSigla = unitaOrganizzativaService.loadUo(uoSiglaRich, null, ordineMissione.getAnno());
					if (uoSigla != null && Utility.nvl(uoSigla.getFl_uo_cds()).equals("true")){
						DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(uoSiglaRich, annoGestione);
						if (Utility.nvl(datiIstituto.getSaltaFirmaUosUoCds(),"N").equals("S")){
							userNameFirmatario = recuperoDirettore(ordineMissione, Utility.replace(uoSiglaRich, ".", ""), account, annoGestione, true);
							userNameFirmatarioSpesa = userNameFirmatario;
							usernameImpostati = true;
						}
					}
				}
			}
			
			String userNameAggiunto = null;
			String userNameSpesaAggiunto = null;
			if (!usernameImpostati){
				userNameFirmatario = recuperoDirettore(ordineMissione, uoRichPerFlusso, account, annoGestione, true);
				if (StringUtils.hasLength(datiIstitutoUoRich.getUoFirmaAggiunta())){
					userNameAggiunto = recuperoDirettore(ordineMissione, Utility.replace(datiIstitutoUoRich.getUoFirmaAggiunta(),".",""), account, annoGestione);
				}
				if (ordineMissione.isMissioneGratuita()){
					userNameFirmatarioSpesa = userNameFirmatario;
				} else {
					Parametri parametri = parametriService.getParametri();
					if (Utility.nvl(ordineMissione.getCug(),"N").equals("S") && parametri != null && parametri.getResponsabileCug() != null){
						userNameFirmatarioSpesa = parametri.getResponsabileCug();
					} else 	if (Utility.nvl(ordineMissione.getPresidente(),"N").equals("S") && parametri != null && parametri.getPresidente() != null){
						userNameFirmatarioSpesa = parametri.getPresidente();

						String primo = userNameFirmatario;
						userNameFirmatario = userNameFirmatarioSpesa;
						userNameFirmatarioSpesa = primo;

					} else if (uoDatiSpesa != null && uoDatiSpesa.getFirmaSpesa() != null && uoDatiSpesa.getFirmaSpesa().equals("N")){
						if (uoCompetenzaPerFlusso != null){
							if (uoDatiCompetenza != null && uoDatiCompetenza.getFirmaSpesa() != null && uoDatiCompetenza.getFirmaSpesa().equals("N")){
								userNameFirmatarioSpesa = userNameFirmatario;
							} else {
								DatiIstituto datiIstitutoUoComp = datiIstitutoService.getDatiIstituto(ordineMissione.getUoCompetenza(), annoGestione);
								if (StringUtils.hasLength(datiIstitutoUoComp.getUoFirmaAggiunta())){
									userNameSpesaAggiunto = recuperoDirettore(ordineMissione, Utility.replace(datiIstitutoUoComp.getUoFirmaAggiunta(),".",""), account, annoGestione);
								}
								userNameFirmatarioSpesa = recuperoDirettore(ordineMissione, uoCompetenzaPerFlusso, account, annoGestione);
							}
						} else {
							userNameFirmatarioSpesa = userNameFirmatario;
						}
					} else {
						userNameFirmatarioSpesa = recuperoDirettore(ordineMissione, uoSpesaPerFlusso, account, annoGestione);
						DatiIstituto datiIstitutoUoSpesa = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), annoGestione);
						if (StringUtils.hasLength(datiIstitutoUoSpesa.getUoFirmaAggiunta())){
							userNameSpesaAggiunto = recuperoDirettore(ordineMissione, Utility.replace(datiIstitutoUoSpesa.getUoFirmaAggiunta(),".",""), account, annoGestione);
						}
					}
				}
			}
			
			if ((userNameSpesaAggiunto != null && (userNameFirmatario.equals(userNameSpesaAggiunto) || (userNameAggiunto != null && userNameAggiunto.equals(userNameSpesaAggiunto))))|| 
					(userNameAggiunto != null && userNameAggiunto.equals(userNameFirmatarioSpesa))){
				userNameFirmatario = userNameAggiunto;
			}

			if (userNameFirmatarioSpesa.equals(ordineMissione.getUid())){
				userNameFirmatario = userNameFirmatarioSpesa;
			}
			cmisOrdineMissione.setUsernameFirmatarioAggiunto(userNameAggiunto);
			cmisOrdineMissione.setUsernameFirmatarioSpesaAggiunto(userNameSpesaAggiunto);
			cmisOrdineMissione.setAnno(ordineMissione.getAnno().toString());
			cmisOrdineMissione.setNumero(ordineMissione.getNumero().toString());
			cmisOrdineMissione.setAnticipo(ordineMissione.getRichiestaAnticipo().equals("S") ? "true" : "false");
			cmisOrdineMissione.setAutoPropriaFlag(ordineMissione.getUtilizzoAutoPropria().equals("S") ? "true" : "false");
			cmisOrdineMissione.setCapitolo(voce == null ? "" : ordineMissione.getVoce());
			cmisOrdineMissione.setDescrizioneCapitolo(voce == null ? "" : voce.getDs_elemento_voce());
			cmisOrdineMissione.setDescrizioneGae(gae == null ? "" : Utility.nvl(gae.getDs_linea_attivita(),""));
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
			cmisOrdineMissione.setNoteAutorizzazioniAggiuntive(ordineMissione.getNoteUtilizzoTaxiNoleggio() == null ? "": ordineMissione.getNoteUtilizzoTaxiNoleggio());
			cmisOrdineMissione.setUsernameRichiedente(Utility.nvl(username,""));
			cmisOrdineMissione.setUsernameUtenteOrdine(ordineMissione.getUid());
			cmisOrdineMissione.setValidazioneModulo(StringUtils.isEmpty(ordineMissione.getResponsabileGruppo()) ? "false" : "true");
			cmisOrdineMissione.setMissioneGratuita(Utility.nvl(ordineMissione.getMissioneGratuita(),"N").equals("S") ? "true" : "false");
			cmisOrdineMissione.setValidazioneSpesa(impostaValidazioneSpesa(userNameFirmatario, userNameFirmatarioSpesa));
			cmisOrdineMissione.setWfDescription("Ordine di Missione n. "+ordineMissione.getNumero()+" di "+account.getCognome() + " "+account.getNome());
			cmisOrdineMissione.setWfDueDate(DateUtils.getDateAsString(ordineMissione.getDataInizioMissione(), DateUtils.PATTERN_DATE_FOR_DOCUMENTALE));
			cmisOrdineMissione.setDestinazione(ordineMissione.getDestinazione());
			cmisOrdineMissione.setMissioneEsteraFlag(ordineMissione.getTipoMissione().equals("E") ? "true" : "false");
			cmisOrdineMissione.setDataInizioMissione(DateUtils.getDateAsString(ordineMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
			cmisOrdineMissione.setDataFineMissione(DateUtils.getDateAsString(ordineMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
			cmisOrdineMissione.setFondi(ordineMissione.getDecodeFondi());
			if (autoPropria != null){
				cmisOrdineMissione.setPrimoMotivoAutoPropria(Utility.nvl(autoPropria.getUtilizzoMotiviIspettivi(),"N").equals("N") ? "" : CMISOrdineMissione.PRIMO_MOTIVO_UTILIZZO_AUTO_PROPRIA);
				cmisOrdineMissione.setSecondoMotivoAutoPropria(Utility.nvl(autoPropria.getUtilizzoMotiviUrgenza(),"N").equals("N") ? "" : CMISOrdineMissione.SECONDO_MOTIVO_UTILIZZO_AUTO_PROPRIA);
				cmisOrdineMissione.setTerzoMotivoAutoPropria(Utility.nvl(autoPropria.getUtilizzoMotiviTrasporto(),"N").equals("N") ? "" : CMISOrdineMissione.TERZO_MOTIVO_UTILIZZO_AUTO_PROPRIA);
				cmisOrdineMissione.setAltriMotiviAutoPropria(autoPropria.getUtilizzoAltriMotivi() == null ? "": autoPropria.getUtilizzoAltriMotivi());
			} else {
				cmisOrdineMissione.setPrimoMotivoAutoPropria("");
				cmisOrdineMissione.setSecondoMotivoAutoPropria("");
				cmisOrdineMissione.setTerzoMotivoAutoPropria("");
				cmisOrdineMissione.setAltriMotiviAutoPropria("");
			}
			if (!StringUtils.isEmpty(ordineMissione.getResponsabileGruppo())){
				cmisOrdineMissione.setUsernameResponsabileGruppo(ordineMissione.getResponsabileGruppo());
				cmisOrdineMissione.setUserNameResponsabileModulo(ordineMissione.getResponsabileGruppo());
			}
			cmisOrdineMissione.setNomeFile(ordineMissione.getFileName());

			return cmisOrdineMissione;
		}
		return null;
	}

	private String recuperoDirettore(OrdineMissione ordineMissione, String uo, Account account, Integer annoGestione, Boolean isUoRich) {
		String userNameFirmatario;
		if (isDevProfile()){
			userNameFirmatario = recuperoUidDirettoreUo(uo);
		} else {
			userNameFirmatario = accountService.recuperoDirettore(annoGestione, uo, ordineMissione.isMissioneEstera(), account, ordineMissione.getDataInizioMissione(), isUoRich);
		}
		return userNameFirmatario;
	}

	private String recuperoDirettore(OrdineMissione ordineMissione, String uo, Account account, Integer annoGestione) {
		return recuperoDirettore(ordineMissione, uo, account, annoGestione, false);
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
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non esistono i dati per uo per il codice "+ordineMissione.getUoSpesa()+" nell'anno "+ordineMissione.getAnno());
			}
			ordineMissione.setDatiIstituto(dati);
			if (ordineMissione.getDatiIstituto() == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non esistono i dati per istituto per il codice "+ordineMissione.getUoSpesa()+" nell'anno "+ordineMissione.getAnno());
			}
		}
	}

	@Transactional(readOnly = true)
	public StorageObject salvaStampaOrdineMissioneSuCMIS(Principal principal, byte[] stampa, OrdineMissione ordineMissione) {
		CMISOrdineMissione cmisOrdineMissione = create(principal, ordineMissione);
		return salvaStampaOrdineMissioneSuCMIS(principal, stampa, ordineMissione, cmisOrdineMissione);
	}
	
	private List<String> getBasePathStorage(OrdineMissione ordineMissione) {
		return Arrays.asList(
				missioniCMISService.getBasePath().getPath(),
				Optional.ofNullable(ordineMissione.getUoRich()).orElse(""),
				"Ordini di Missione",
				Optional.ofNullable(ordineMissione.getAnno())
						.map(esercizio -> "Anno "+String.valueOf(esercizio))
						.orElse("Anno "+"0")
		);
	}

	private String getPathStorage(OrdineMissione ordineMissione) {
		return getBasePathStorage(ordineMissione).stream().collect(
				Collectors.joining(StorageService.SUFFIX)
		);
	}

	public String createFolderOrdineMissione(OrdineMissione ordineMissione){
		return missioniCMISService.createFolderIfNotPresent(getPathStorage(ordineMissione), ordineMissione.constructCMISNomeFile(), getMetadataPropertiesFolderOrdine(ordineMissione));
	}

	private Map<String, Object> getMetadataPropertiesFolderOrdine(OrdineMissione ordineMissione){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), OrdineMissione.CMIS_PROPERTY_MAIN);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename(ordineMissione.constructCMISNomeFile()));
		metadataProperties.put(MissioniCMISService.PROPERTY_NAME, missioniCMISService.sanitizeFilename(ordineMissione.constructCMISNomeFile()));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename(ordineMissione.constructCMISNomeFile()));
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
		metadataProperties.put(StoragePropertyNames.SECONDARY_OBJECT_TYPE_IDS.value(), aspectsToAdd);
		return metadataProperties;
	}
	
	
	private StorageObject salvaStampaOrdineMissioneSuCMIS(Principal principal,
			byte[] stampa, OrdineMissione ordineMissione,
			CMISOrdineMissione cmisOrdineMissione) {
		InputStream streamStampa = new ByteArrayInputStream(stampa);
		String path = createFolderOrdineMissione(ordineMissione);
		Map<String, Object> metadataProperties = createMetadataForFileOrdineMissione(principal.getName(), cmisOrdineMissione);
		try{
			StorageObject so = null;
			if (!ordineMissione.isStatoInviatoAlFlusso()){
				so = missioniCMISService.restoreSimpleDocument(
						metadataProperties,
						streamStampa,
						MimeTypes.PDF.mimetype(),
						ordineMissione.getFileName(), 
						StoragePath.construct(path));
				
			}else{
				so = (StorageObject)getObjectOrdineMissione(ordineMissione);
				so = missioniCMISService.updateStream(so.getKey(), streamStampa, MimeTypes.PDF.mimetype());
				missioniCMISService.addPropertyForExistingDocument(metadataProperties, so);
			}
			missioniCMISService.addAspect(so, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ORDINE.value());
			return so;
		} catch (Exception e) {
			if (e.getCause() instanceof StorageException)
				throw new AwesomeException(CodiciErrore.ERRGEN, "CMIS - File ["+ordineMissione.getFileName()+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!");
			throw new AwesomeException(CodiciErrore.ERRGEN, "CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")");
		}
	}

	public StorageObject salvaStampaAnnullamentoOrdineMissioneSuCMIS(Principal principal,
			byte[] stampa, AnnullamentoOrdineMissione annullamento) {
		InputStream streamStampa = new ByteArrayInputStream(stampa);
		String path = createFolderOrdineMissione(annullamento.getOrdineMissione());
		Map<String, Object> metadataProperties = createMetadataForFileAnnullamentoOrdineMissione(principal.getName(), annullamento);
		
		try {
			StorageObject so = null;
			StoragePath soFolder = StoragePath.construct(path);
			if (!annullamento.isStatoInviatoAlFlusso()){
				so = missioniCMISService.restoreSimpleDocument(metadataProperties, streamStampa,
						MimeTypes.PDF.mimetype(), annullamento.getFileName(), soFolder);

			}else{
				so = (StorageObject)getObjectAnnullamentoOrdineMissione(annullamento);
				so = missioniCMISService.updateStream(so.getKey(), streamStampa, MimeTypes.PDF.mimetype());
				missioniCMISService.addPropertyForExistingDocument(metadataProperties, so);
			}

			missioniCMISService.addAspect(so,
					CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ANNULLAMENTO_ORDINE.value());
			return so;
		} catch (Exception e) {
			if (e.getCause() instanceof StorageException)
				throw new ComponentException("CMIS - File [" + annullamento.getFileName()
						+ "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!",e);
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale ("
					+ Utility.getMessageException(e) + ")",e);
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
		metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename("Ordine Missione - anno "+cmisOrdineMissione.getAnno()+" numero "+cmisOrdineMissione.getNumero()));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename("Ordine di Missione"));
		metadataProperties.put(MissioniCMISService.PROPERTY_NAME, cmisOrdineMissione.getNomeFile());
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
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_MISSIONE_GRATUITA, cmisOrdineMissione.getMissioneGratuita().equals("true"));
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_NOTE_AUTORIZZAZIONI_AGGIUNTIVE, cmisOrdineMissione.getNoteAutorizzazioniAggiuntive());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_FONDI, cmisOrdineMissione.getFondi());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_AUTO_PROPRIA_PRIMO_MOTIVO, cmisOrdineMissione.getPrimoMotivoAutoPropria());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_AUTO_PROPRIA_SECONDO_MOTIVO, cmisOrdineMissione.getSecondoMotivoAutoPropria());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_AUTO_PROPRIA_TERZO_MOTIVO, cmisOrdineMissione.getTerzoMotivoAutoPropria());
		
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_RICHIEDENTE, cmisOrdineMissione.getUsernameRichiedente());
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_VALIDAZIONE_MODULO, StringUtils.isEmpty(cmisOrdineMissione.getUsernameResponsabileGruppo()) ? false : true);
		metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_VALIDAZIONE_SPESA, cmisOrdineMissione.getValidazioneSpesa().equals("true"));
		return metadataProperties;
	}

	public void avviaFlusso(Principal principal, AnnullamentoOrdineMissione annullamento) {
		String username = principal.getName();
		byte[] stampa = printAnnullamentoOrdineMissioneService.printOrdineMissione(annullamento, username);
		CMISOrdineMissione cmisOrdineMissione = create(principal, annullamento.getOrdineMissione(), annullamento.getAnno());
		StorageObject so = salvaStampaAnnullamentoOrdineMissioneSuCMIS(principal, stampa, annullamento);

//		String nodeRefFirmatario = missioniCMISService.recuperoNodeRefUtente(cmisOrdineMissione.getUserNamePrimoFirmatario());

		
//		String nodeRefFirmatarioAggiunto = null;
//		String nodeRefFirmatarioSpesaAggiunto = null;
//		if (cmisOrdineMissione.getUsernameFirmatarioAggiunto() != null){
//			nodeRefFirmatarioAggiunto = missioniCMISService.recuperoNodeRefUtente(cmisOrdineMissione.getUsernameFirmatarioAggiunto());
//		}
//		if (cmisOrdineMissione.getUsernameFirmatarioSpesaAggiunto() != null){
//			nodeRefFirmatarioSpesaAggiunto = missioniCMISService.recuperoNodeRefUtente(cmisOrdineMissione.getUsernameFirmatarioSpesaAggiunto());
//		}

		MessageForFlowAnnullamento messageForFlows = new MessageForFlowAnnullamento();
		try {
			StringBuilder nodeRefs = new StringBuilder();
			if (annullamento.isStatoNonInviatoAlFlusso()){
				aggiungiDocumento(so, nodeRefs);
				messageForFlows.setProp_bpm_comment("");
				messageForFlows.setProp_bpm_percentComplete("0");
			}

			messageForFlows.setAssoc_packageItems_added(nodeRefs.toString());
			messageForFlows.setAssoc_packageItems_removed("");


			messageForFlows.setProp_cnrmissioni_noteAutorizzazioniAggiuntive(cmisOrdineMissione.getNoteAutorizzazioniAggiuntive());
			messageForFlows.setProp_cnrmissioni_missioneGratuita(cmisOrdineMissione.getMissioneGratuita());
			messageForFlows.setProp_cnrmissioni_descrizioneOrdine(cmisOrdineMissione.getOggetto());
			messageForFlows.setProp_cnrmissioni_note(cmisOrdineMissione.getNote());
			messageForFlows.setProp_cnrmissioni_noteSegreteria(cmisOrdineMissione.getNoteSegreteria());
			messageForFlows.setProp_bpm_workflowDescription(cmisOrdineMissione.getWfDescription());
			messageForFlows.setProp_bpm_workflowDueDate(cmisOrdineMissione.getWfDueDate());
			messageForFlows.setProp_bpm_status("Not Yet Started");
			messageForFlows.setProp_wfcnr_groupName("GENERICO");
			messageForFlows.setProp_wfcnr_wfCounterIndex( "");
			messageForFlows.setProp_wfcnr_wfCounterId("");
			messageForFlows.setProp_wfcnr_wfCounterAnno("");
			messageForFlows.setProp_bpm_workflowPriority(cmisOrdineMissione.getPriorita());
			messageForFlows.setProp_cnrmissioni_validazioneSpesaFlag(cmisOrdineMissione.getValidazioneSpesa());
			messageForFlows.setProp_cnrmissioni_missioneConAnticipoFlag(cmisOrdineMissione.getAnticipo());
			messageForFlows.setProp_cnrmissioni_validazioneModuloFlag(StringUtils.isEmpty(cmisOrdineMissione.getUserNameResponsabileModulo()) ? "false": "true");
			messageForFlows.setProp_cnrmissioni_userNameUtenteOrdineMissione(cmisOrdineMissione.getUsernameUtenteOrdine());
			messageForFlows.setProp_cnrmissioni_userNameRichiedente(cmisOrdineMissione.getUsernameRichiedente());
			messageForFlows.setProp_cnrmissioni_userNameResponsabileModulo(cmisOrdineMissione.getUserNameResponsabileModulo());
			messageForFlows.setProp_cnrmissioni_userNamePrimoFirmatario(cmisOrdineMissione.getUserNamePrimoFirmatario());
			messageForFlows.setProp_cnrmissioni_userNameFirmatarioSpesa(cmisOrdineMissione.getUserNameFirmatarioSpesa());
			messageForFlows.setProp_cnrmissioni_userNameAmministrativo1("");
			messageForFlows.setProp_cnrmissioni_userNameAmministrativo2("");
			messageForFlows.setProp_cnrmissioni_userNameAmministrativo3("");
			messageForFlows.setProp_cnrmissioni_uoOrdine(cmisOrdineMissione.getUoOrdine());
			messageForFlows.setProp_cnrmissioni_descrizioneUoOrdine(cmisOrdineMissione.getDescrizioneUoOrdine());
			messageForFlows.setProp_cnrmissioni_uoSpesa(cmisOrdineMissione.getUoSpesa());
			messageForFlows.setProp_cnrmissioni_descrizioneUoSpesa(cmisOrdineMissione.getDescrizioneUoSpesa());
			messageForFlows.setProp_cnrmissioni_uoCompetenza(cmisOrdineMissione.getUoCompetenza());
			messageForFlows.setProp_cnrmissioni_descrizioneUoCompetenza(cmisOrdineMissione.getDescrizioneUoCompetenza());
			messageForFlows.setProp_cnrmissioni_autoPropriaFlag(cmisOrdineMissione.getAutoPropriaFlag());
			messageForFlows.setProp_cnrmissioni_noleggioFlag(cmisOrdineMissione.getNoleggioFlag());
			messageForFlows.setProp_cnrmissioni_taxiFlag(cmisOrdineMissione.getTaxiFlag());
			messageForFlows.setProp_cnrmissioni_servizioFlagOk(cmisOrdineMissione.getAutoServizioFlag());
			messageForFlows.setProp_cnrmissioni_personaSeguitoFlagOk(cmisOrdineMissione.getPersonaSeguitoFlag());
			messageForFlows.setProp_cnrmissioni_capitolo(cmisOrdineMissione.getCapitolo());
			messageForFlows.setProp_cnrmissioni_descrizioneCapitolo(cmisOrdineMissione.getDescrizioneCapitolo());
			messageForFlows.setProp_cnrmissioni_modulo(cmisOrdineMissione.getModulo());
			messageForFlows.setProp_cnrmissioni_descrizioneModulo(cmisOrdineMissione.getDescrizioneModulo());
			messageForFlows.setProp_cnrmissioni_gae(cmisOrdineMissione.getGae());
			messageForFlows.setProp_cnrmissioni_descrizioneGae(cmisOrdineMissione.getDescrizioneGae());
			messageForFlows.setProp_cnrmissioni_impegnoAnnoResiduo(cmisOrdineMissione.getImpegnoAnnoResiduo() == null ? "": cmisOrdineMissione.getImpegnoAnnoResiduo().toString());
			messageForFlows.setProp_cnrmissioni_impegnoAnnoCompetenza(cmisOrdineMissione.getImpegnoAnnoCompetenza() == null ? "": cmisOrdineMissione.getImpegnoAnnoCompetenza().toString());
			messageForFlows.setProp_cnrmissioni_impegnoNumeroOk(cmisOrdineMissione.getImpegnoNumero() == null ? "": cmisOrdineMissione.getImpegnoNumero().toString());
			messageForFlows.setProp_cnrmissioni_descrizioneImpegno(cmisOrdineMissione.getDescrizioneImpegno());
			messageForFlows.setProp_cnrmissioni_importoMissione(cmisOrdineMissione.getImportoMissione() == null ? "": cmisOrdineMissione.getImportoMissione().toString());
			messageForFlows.setProp_cnrmissioni_disponibilita(cmisOrdineMissione.getDisponibilita() == null ? "": cmisOrdineMissione.getDisponibilita().toString());
			messageForFlows.setProp_cnrmissioni_missioneEsteraFlag(cmisOrdineMissione.getMissioneEsteraFlag());
			messageForFlows.setProp_cnrmissioni_destinazione(cmisOrdineMissione.getDestinazione());
			messageForFlows.setProp_cnrmissioni_dataInizioMissione(cmisOrdineMissione.getDataInizioMissione());
			messageForFlows.setProp_cnrmissioni_dataFineMissione(cmisOrdineMissione.getDataFineMissione());
			messageForFlows.setProp_cnrmissioni_trattamento(cmisOrdineMissione.getTrattamento());
			messageForFlows.setProp_cnrmissioni_competenzaResiduo(cmisOrdineMissione.getFondi());
			messageForFlows.setProp_cnrmissioni_autoPropriaAltriMotivi(cmisOrdineMissione.getAltriMotiviAutoPropria());
			messageForFlows.setProp_cnrmissioni_autoPropriaPrimoMotivo(cmisOrdineMissione.getPrimoMotivoAutoPropria());
			messageForFlows.setProp_cnrmissioni_autoPropriaSecondoMotivo(cmisOrdineMissione.getSecondoMotivoAutoPropria());
			messageForFlows.setProp_cnrmissioni_autoPropriaTerzoMotivo(cmisOrdineMissione.getTerzoMotivoAutoPropria());
			messageForFlows.setProp_cnrmissioni_wfOrdineDaRevoca(annullamento.getOrdineMissione().getIdFlusso());
			
			if (annullamento.isStatoInviatoAlFlusso() && !StringUtils.isEmpty(annullamento.getIdFlusso())){
				messageForFlows.setProp_bpm_comment("AVANZAMENTO");
				messageForFlows.setProp_wfcnr_reviewOutcome(FlowResubmitType.RESTART_FLOW.operation());
				messageForFlows.setProp_transitions("Next");
			}
		} catch (Exception e) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di preparazione del flusso documentale. Errore: "+e);
		}

		if (annullamento.isStatoNonInviatoAlFlusso()){
			try {
				OrdineMissione ordineMissione = annullamento.getOrdineMissione();
				if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), ordineMissione.getAnno()).getTipoMailDopoOrdine(),"N").equals("C")){
					annullamentoOrdineMissioneService.popolaCoda(annullamento);
				} else {
					String idFlusso = missioniCMISService.startFlowAnnullamentoOrdineMissione(messageForFlows);

					annullamento.setIdFlusso(idFlusso);
					annullamento.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
				}
			} catch (AwesomeException e) {
				throw e;
			} catch (Exception e) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
			}
		} else {
			if (annullamento.isStatoInviatoAlFlusso() && !StringUtils.isEmpty(annullamento.getIdFlusso())){
				ResultFlows result = getFlowsOrdineMissione(annullamento.getIdFlusso());
				if (!StringUtils.isEmpty(result.getTaskId())){
					missioniCMISService.restartFlow(messageForFlows, result);
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Task Id del flusso non trovato.");
				}
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Stato di invio al flusso non valido.");
			}
		}
	}

	public void avviaFlusso(Principal principal, OrdineMissione ordineMissione) {
		String username = principal.getName();
		byte[] stampa = printOrdineMissioneService.printOrdineMissione(ordineMissione, username);
		CMISOrdineMissione cmisOrdineMissione = create(principal, ordineMissione);
		StorageObject documento = salvaStampaOrdineMissioneSuCMIS(principal, stampa, ordineMissione, cmisOrdineMissione);
		OrdineMissioneAnticipo anticipo = ordineMissioneAnticipoService.getAnticipo(principal, new Long(ordineMissione.getId().toString()));
		OrdineMissioneAutoPropria autoPropria = ordineMissioneAutoPropriaService.getAutoPropria(principal, new Long(ordineMissione.getId().toString()), true);
		StorageObject documentoAnticipo = null;
		List<CMISFileAttachment> allegati = new ArrayList<>();
		List<CMISFileAttachment> allegatiOrdineMissione = getAttachmentsOrdineMissione(ordineMissione, new Long(ordineMissione.getId().toString()));
		if (allegatiOrdineMissione != null && !allegatiOrdineMissione.isEmpty()){
			allegati.addAll(allegatiOrdineMissione);
		}
		if (anticipo != null){
			anticipo.setOrdineMissione(ordineMissione);
			documentoAnticipo = creaDocumentoAnticipo(username, anticipo);
			List<CMISFileAttachment> allegatiAnticipo = getAttachmentsAnticipo(ordineMissione, new Long(anticipo.getId().toString()));
			if (allegatiAnticipo != null && !allegatiAnticipo.isEmpty()){
				allegati.addAll(allegatiAnticipo);
			}
		}

		
		StorageObject documentoAutoPropria = null;
		if (autoPropria != null){
			autoPropria.setOrdineMissione(ordineMissione);
			documentoAutoPropria = creaDocumentoAutoPropria(username, autoPropria);
		}

		String nodeRefFirmatario = missioniCMISService.recuperoNodeRefUtente(cmisOrdineMissione.getUserNamePrimoFirmatario());

		MessageForFlowOrdine messageForFlows = new MessageForFlowOrdine();
		try {
			messageForFlows.setAssoc_bpm_assignee_added(nodeRefFirmatario);
			messageForFlows.setAssoc_bpm_assignee_removed("");
			StringBuilder nodeRefs = new StringBuilder();
			aggiungiDocumento(documento, nodeRefs);
			aggiungiDocumento(documentoAnticipo, nodeRefs);
			aggiungiAllegati(allegati, nodeRefs);
			if (ordineMissione.isStatoNonInviatoAlFlusso()){
				messageForFlows.setProp_bpm_comment("");
				messageForFlows.setProp_bpm_percentComplete("0");
			}

			aggiungiDocumento(documentoAutoPropria, nodeRefs);

			StringBuilder nodeRefsPrimoFirmatario = new StringBuilder();
			aggiungiFirmatario(cmisOrdineMissione.getUserNamePrimoFirmatario(), nodeRefsPrimoFirmatario);
//			String nodeRefFirmatarioAggiunto = null;
//			String nodeRefFirmatarioSpesaAggiunto = null;
//			if (cmisOrdineMissione.getUsernameFirmatarioAggiunto() != null){
//				nodeRefFirmatarioAggiunto = missioniCMISService.recuperoNodeRefUtente(cmisOrdineMissione.getUsernameFirmatarioAggiunto());
//			}
			aggiungiFirmatario(cmisOrdineMissione.getUsernameFirmatarioAggiunto(), nodeRefsPrimoFirmatario);
			StringBuilder nodeRefsFirmatarioSpesa = new StringBuilder();
			aggiungiFirmatario(cmisOrdineMissione.getUserNameFirmatarioSpesa(), nodeRefsFirmatarioSpesa);
			aggiungiFirmatario(cmisOrdineMissione.getUsernameFirmatarioSpesaAggiunto(), nodeRefsFirmatarioSpesa);


			
			
			messageForFlows.setAssoc_packageItems_added(nodeRefs.toString());
			messageForFlows.setAssoc_packageItems_removed("");


			messageForFlows.setProp_cnrmissioni_noteAutorizzazioniAggiuntive(cmisOrdineMissione.getNoteAutorizzazioniAggiuntive());
			messageForFlows.setProp_cnrmissioni_missioneGratuita(cmisOrdineMissione.getMissioneGratuita());
			messageForFlows.setProp_cnrmissioni_descrizioneOrdine(cmisOrdineMissione.getOggetto());
			messageForFlows.setProp_cnrmissioni_note(cmisOrdineMissione.getNote());
			messageForFlows.setProp_cnrmissioni_noteSegreteria(cmisOrdineMissione.getNoteSegreteria());
			messageForFlows.setProp_bpm_workflowDescription(cmisOrdineMissione.getWfDescription());
			messageForFlows.setProp_bpm_workflowDueDate(cmisOrdineMissione.getWfDueDate());
			messageForFlows.setProp_bpm_status("Not Yet Started");
			messageForFlows.setProp_wfcnr_groupName("GENERICO");
			messageForFlows.setProp_wfcnr_wfCounterIndex("");
			messageForFlows.setProp_wfcnr_wfCounterId("");
			messageForFlows.setProp_wfcnr_wfCounterAnno("");
			messageForFlows.setProp_bpm_workflowPriority(cmisOrdineMissione.getPriorita());
			messageForFlows.setProp_cnrmissioni_validazioneSpesaFlag(cmisOrdineMissione.getValidazioneSpesa());
			messageForFlows.setProp_cnrmissioni_missioneConAnticipoFlag(cmisOrdineMissione.getAnticipo());
			messageForFlows.setProp_cnrmissioni_validazioneModuloFlag(StringUtils.isEmpty(cmisOrdineMissione.getUserNameResponsabileModulo()) ? "false": "true");
			messageForFlows.setProp_cnrmissioni_userNameUtenteOrdineMissione(cmisOrdineMissione.getUsernameUtenteOrdine());
			messageForFlows.setProp_cnrmissioni_userNameRichiedente(cmisOrdineMissione.getUsernameRichiedente());
			messageForFlows.setProp_cnrmissioni_userNameResponsabileModulo(cmisOrdineMissione.getUserNameResponsabileModulo());
			messageForFlows.setProp_cnrmissioni_userNamePrimoFirmatario(cmisOrdineMissione.getUserNamePrimoFirmatario());
			messageForFlows.setProp_cnrmissioni_userNameFirmatarioSpesa(cmisOrdineMissione.getUserNameFirmatarioSpesa());
			messageForFlows.setProp_cnrmissioni_userNameAmministrativo1("");
			messageForFlows.setProp_cnrmissioni_userNameAmministrativo2("");
			messageForFlows.setProp_cnrmissioni_userNameAmministrativo3("");
			messageForFlows.setProp_cnrmissioni_uoOrdine(cmisOrdineMissione.getUoOrdine());
			messageForFlows.setProp_cnrmissioni_descrizioneUoOrdine(cmisOrdineMissione.getDescrizioneUoOrdine());
			messageForFlows.setProp_cnrmissioni_uoSpesa(cmisOrdineMissione.getUoSpesa());
			messageForFlows.setProp_cnrmissioni_descrizioneUoSpesa(cmisOrdineMissione.getDescrizioneUoSpesa());
			messageForFlows.setProp_cnrmissioni_uoCompetenza(cmisOrdineMissione.getUoCompetenza());
			messageForFlows.setProp_cnrmissioni_descrizioneUoCompetenza(cmisOrdineMissione.getDescrizioneUoCompetenza());
			messageForFlows.setProp_cnrmissioni_autoPropriaFlag(cmisOrdineMissione.getAutoPropriaFlag());
			messageForFlows.setProp_cnrmissioni_noleggioFlag(cmisOrdineMissione.getNoleggioFlag());
			messageForFlows.setProp_cnrmissioni_taxiFlag(cmisOrdineMissione.getTaxiFlag());
			messageForFlows.setProp_cnrmissioni_servizioFlagOk(cmisOrdineMissione.getAutoServizioFlag());
			messageForFlows.setProp_cnrmissioni_personaSeguitoFlagOk(cmisOrdineMissione.getPersonaSeguitoFlag());
			messageForFlows.setProp_cnrmissioni_capitolo(cmisOrdineMissione.getCapitolo());
			messageForFlows.setProp_cnrmissioni_descrizioneCapitolo(cmisOrdineMissione.getDescrizioneCapitolo());
			messageForFlows.setProp_cnrmissioni_modulo(cmisOrdineMissione.getModulo());
			messageForFlows.setProp_cnrmissioni_descrizioneModulo(cmisOrdineMissione.getDescrizioneModulo());
			messageForFlows.setProp_cnrmissioni_gae(cmisOrdineMissione.getGae());
			messageForFlows.setProp_cnrmissioni_descrizioneGae(cmisOrdineMissione.getDescrizioneGae());
			messageForFlows.setProp_cnrmissioni_impegnoAnnoResiduo(cmisOrdineMissione.getImpegnoAnnoResiduo() == null ? "": cmisOrdineMissione.getImpegnoAnnoResiduo().toString());
			messageForFlows.setProp_cnrmissioni_impegnoAnnoCompetenza(cmisOrdineMissione.getImpegnoAnnoCompetenza() == null ? "": cmisOrdineMissione.getImpegnoAnnoCompetenza().toString());
			messageForFlows.setProp_cnrmissioni_impegnoNumeroOk(cmisOrdineMissione.getImpegnoNumero() == null ? "": cmisOrdineMissione.getImpegnoNumero().toString());
			messageForFlows.setProp_cnrmissioni_descrizioneImpegno(cmisOrdineMissione.getDescrizioneImpegno());
			messageForFlows.setProp_cnrmissioni_importoMissione(cmisOrdineMissione.getImportoMissione() == null ? "": cmisOrdineMissione.getImportoMissione().toString());
			messageForFlows.setProp_cnrmissioni_disponibilita(cmisOrdineMissione.getDisponibilita() == null ? "": cmisOrdineMissione.getDisponibilita().toString());
			messageForFlows.setProp_cnrmissioni_missioneEsteraFlag(cmisOrdineMissione.getMissioneEsteraFlag());
			messageForFlows.setProp_cnrmissioni_destinazione(cmisOrdineMissione.getDestinazione());
			messageForFlows.setProp_cnrmissioni_dataInizioMissione(cmisOrdineMissione.getDataInizioMissione());
			messageForFlows.setProp_cnrmissioni_dataFineMissione(cmisOrdineMissione.getDataFineMissione());
			messageForFlows.setProp_cnrmissioni_trattamento(cmisOrdineMissione.getTrattamento());
			messageForFlows.setProp_cnrmissioni_competenzaResiduo(cmisOrdineMissione.getFondi());
			messageForFlows.setProp_cnrmissioni_autoPropriaAltriMotivi(cmisOrdineMissione.getAltriMotiviAutoPropria());
			messageForFlows.setProp_cnrmissioni_autoPropriaPrimoMotivo(cmisOrdineMissione.getPrimoMotivoAutoPropria());
			messageForFlows.setProp_cnrmissioni_autoPropriaSecondoMotivo(cmisOrdineMissione.getSecondoMotivoAutoPropria());
			messageForFlows.setProp_cnrmissioni_autoPropriaTerzoMotivo(cmisOrdineMissione.getTerzoMotivoAutoPropria());
			if (ordineMissione.isStatoInviatoAlFlusso() && !StringUtils.isEmpty(ordineMissione.getIdFlusso())){
				messageForFlows.setProp_bpm_comment("AVANZAMENTO");
				messageForFlows.setProp_wfcnr_reviewOutcome(FlowResubmitType.RESTART_FLOW.operation());
				messageForFlows.setProp_transitions("Next");
			}
		} catch (Exception e) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di preparazione del flusso documentale. Errore: "+e);
		}

		if (ordineMissione.isStatoNonInviatoAlFlusso()){
			try {
				if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), ordineMissione.getAnno()).getTipoMailDopoOrdine(),"N").equals("C")){
					ordineMissioneService.popolaCoda(ordineMissione);
				} else {
					String idFlusso = missioniCMISService.startFlowOrdineMissione(messageForFlows);
					ordineMissione.setIdFlusso(idFlusso);
					ordineMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
					if (anticipo != null){
						anticipo.setIdFlusso(idFlusso);
					}
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
					missioniCMISService.restartFlow(messageForFlows, result);
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Task Id del flusso non trovato.");
				}
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Stato di invio al flusso non valido.");
			}
		}
	}
	private void aggiungiDocumento(StorageObject documento,
			StringBuilder nodeRefs) {
		if (documento != null){
			if (nodeRefs.length() > 0){
				 nodeRefs.append(",");
			}
			nodeRefs.append(documento.getKey());
		 }
	}

	private void aggiungiFirmatario(String newFirmatario,
			StringBuilder nodeRefs) {
		if (newFirmatario != null){
			if (nodeRefs.length() > 0){
				 nodeRefs.append(",");
			}
			nodeRefs.append(newFirmatario);
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

	public StorageObject getStorageObjectOrdineMissione(OrdineMissione ordineMissione) throws ComponentException{
		String id = getNodeRefOrdineMissione(ordineMissione);
		if (id != null){
			return missioniCMISService.recuperoContentFileFromObjectID(id);
		}
		return null;
	}
	

	public InputStream getStreamOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException{
		String id = getNodeRefOrdineMissioneAutoPropria(ordineMissioneAutoPropria);
		if (id != null){
			return missioniCMISService.recuperoStreamFileFromObjectID(id);
		}
		return null;
	}
	
	public InputStream getStreamOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo) throws ComponentException{
		String id = getNodeRefOrdineMissioneAnticipo(ordineMissioneAnticipo);
		if (id != null){
			return missioniCMISService.recuperoStreamFileFromObjectID(id);
		}
		return null;
	}
	
	public StorageObject getObjectOrdineMissione(OrdineMissione ordineMissione) throws ComponentException{
		StorageObject fo = recuperoFolderOrdineMissione(ordineMissione);
		List<StorageObject> ordine = missioniCMISService.recuperoDocumento(fo, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ORDINE.value());
		if (ordine.size() == 0)
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati all'Ordine di Missione. ID Ordine di Missione:"+ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		else if (ordine.size() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files ordini di missione aventi l'ID :"+ ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		} else {
			StorageObject storageObject = ordine.get(0);
			return storageObject;
		}
	}

	public StorageObject getObjectAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento) throws ComponentException{
		StorageObject node = recuperoFolderOrdineMissione(annullamento.getOrdineMissione());
		List<StorageObject> ordine = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ANNULLAMENTO_ORDINE.value());
		if (ordine.size() == 0)
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati all'Annullamento Ordine di Missione. ID Ordine di Missione:"+annullamento.getId()+", Anno:"+annullamento.getAnno()+", Numero:"+annullamento.getNumero());
		else if (ordine.size() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files Annullamento ordini di missione aventi l'ID :"+ annullamento.getId()+", Anno:"+annullamento.getAnno()+", Numero:"+annullamento.getNumero());
		} else {
			StorageObject nodeFile = ordine.get(0);
				return nodeFile;
		}
	}

	public StorageObject getObjectAnticipoOrdineMissione(OrdineMissioneAnticipo anticipo) throws ComponentException{
		StorageObject node = recuperoFolderOrdineMissione(anticipo.getOrdineMissione());
		List<StorageObject> ant = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_RICHIESTA_ANTICIPO.value());
		if (ant.size() == 0)
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono anticipi collegati all'Ordine di Missione. ID Anticipo:"+anticipo.getId());
		else if (ant.size() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files anticipo di missione aventi l'ID :"+ anticipo.getId());
		} else {
			StorageObject nodeFile = ant.get(0);
				return nodeFile;
		}
	}
	public String getNodeRefOrdineMissione(OrdineMissione ordineMissione) throws ComponentException{
		return getObjectOrdineMissione(ordineMissione).getKey();
	}
	
	public String getNodeRefAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento) throws ComponentException{
			StorageObject nodeFile = getStorageAnnullamentoOrdineMissione(annullamento);
			return nodeFile.getKey();
	}

	public StorageObject getStorageAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento) throws ComponentException{
		OrdineMissione ordineMissione = annullamento.getOrdineMissione();
		StorageObject node = recuperoFolderOrdineMissione(ordineMissione);
		List<StorageObject> objs = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ANNULLAMENTO_ORDINE.value());

		if (objs.size() == 0){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati di annullamento dell'Ordine di Missione. ID Ordine di Missione:"+ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		}
		else if (objs.size() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files di annullamento dell'ordine di missione aventi l'ID :"+ ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		} else {
			return objs.get(0);
		}
	}

	public StorageObject getStorageOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo) throws ComponentException{
		OrdineMissione ordineMissione = ordineMissioneAnticipo.getOrdineMissione();
		StorageObject node = recuperoFolderOrdineMissione(ordineMissione);
		List<StorageObject> objs = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_RICHIESTA_ANTICIPO.value());

		if (objs.size() == 0){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati di annullamento dell'Ordine di Missione. ID Ordine di Missione:"+ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		}
		else if (objs.size() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files di annullamento dell'ordine di missione aventi l'ID :"+ ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		} else {
			return objs.get(0);
		}
	}

	public StorageObject getStorageOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException{
		OrdineMissione ordineMissione = ordineMissioneAutoPropria.getOrdineMissione();
		StorageObject node = recuperoFolderOrdineMissione(ordineMissione);
		List<StorageObject> objs = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_USO_AUTO_PROPRIA.value());

		if (objs.size() == 0){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati di annullamento dell'Ordine di Missione. ID Ordine di Missione:"+ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		}
		else if (objs.size() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files di annullamento dell'ordine di missione aventi l'ID :"+ ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		} else {
			return objs.get(0);
		}
	}
	
	public String getNodeRefOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException{
		OrdineMissione ordineMissione = ordineMissioneAutoPropria.getOrdineMissione();
		StorageObject node = recuperoFolderOrdineMissione(ordineMissione);
		List<StorageObject> objs = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_USO_AUTO_PROPRIA.value());

		if (objs.size() == 0){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati di richiesta di auto propria per l'ordine di missione con ID :"+ ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		}
		else if (objs.size() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files  di richiesta di auto propria per l'ordine di missione con ID :"+ ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		} else {
			StorageObject nodeFile = objs.get(0);
			return nodeFile.getKey();
		}
	}
	
	public String getNodeRefOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo) {
		OrdineMissione ordineMissione = ordineMissioneAnticipo.getOrdineMissione();
		StorageObject node = recuperoFolderOrdineMissione(ordineMissione);
		List<StorageObject> anticipi = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_RICHIESTA_ANTICIPO.value());

		if (anticipi.size() == 0){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti di richiesta anticipo collegati all'Ordine di Missione. ID Ordine di Missione:"+ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		}
		else if (anticipi.size() > 1){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files  di richiesta anticipo per l'ordine di missione con ID :"+ ordineMissione.getId()+", Anno:"+ordineMissione.getAnno()+", Numero:"+ordineMissione.getNumero());
		} else {
			StorageObject nodeFile = anticipi.get(0);
			return nodeFile.getKey();
		}
	}

	public StorageObject recuperoFolderOrdineMissione(OrdineMissione ordineMissione){
		final String path = Arrays.asList(
				missioniCMISService.getBasePath().getPath(),
				Optional.ofNullable(ordineMissione)
						.map(OrdineMissione::getUoRich)
						.orElse(""),
				"Ordini di Missione",
				Optional.ofNullable(ordineMissione)
						.map(ordine -> "Anno " + String.valueOf(ordine.getAnno()))
						.orElse("0"),
				String.valueOf(missioniCMISService.sanitizeFilename(ordineMissione.constructCMISNomeFile()))
		).stream().collect(
				Collectors.joining("/")
		);
		
		try{
			return Optional.ofNullable(missioniCMISService.getStorageObjectByPath(path))
					.filter(StorageObject.class::isInstance)
					.map(StorageObject.class::cast)
					.orElse(null);
		} catch (StorageException e){
			String pathFolder = createFolderOrdineMissione(ordineMissione);
			return (StorageObject)missioniCMISService.getStorageObjectByPath(pathFolder);
		}
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
		List<StorageObject> result = recuperoFlusso(idFlusso);
		if (result != null && result.size() == 1){
			ResultFlows flows = new ResultFlows();
			flows.setState((String) result.get(0).getPropertyValue(fieldStato));
			flows.setComment((String) result.get(0).getPropertyValue(fieldComment));
			flows.setTaskId((String) result.get(0).getPropertyValue(fieldTaskId));
			return flows;
		}
		return null;
	}

	private void abortFlowOrdineMissione(OrdineMissione ordineMissione)  {
    	ResultFlows result = getFlowsOrdineMissione(ordineMissione.getIdFlusso());
    	if (!StringUtils.isEmpty(result.getTaskId())){
        		MessageForFlowOrdine message = createJsonForAbortFlowOrdineMissione();
        		missioniCMISService.abortFlow(message, result);
    	} else {
    		throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Task Id del flusso non trovato.");
    	}
    }

	public List<StorageObject> recuperoFlusso(String idFlusso){
		return missioniCMISService.recuperoFlusso(idFlusso);
	}

	private MessageForFlowOrdine createJsonForAbortFlowOrdineMissione() {
		MessageForFlowOrdine message = new MessageForFlowOrdine();
		message.setProp_bpm_comment( "AVANZAMENTO");
		message.setProp_wfcnr_reviewOutcome(FlowResubmitType.ABORT_FLOW.operation());
		return message;
	}
	public Map<String, Object> createMetadataForFileOrdineMissioneAnticipo(String currentLogin, OrdineMissioneAnticipo anticipo){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
		metadataProperties.put(MissioniCMISService.PROPERTY_NAME, anticipo.getFileName());
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
		metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
		metadataProperties.put(MissioniCMISService.PROPERTY_NAME, autoPropria.getFileName());
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename("Richiesta Uso Auto Propria per l'Ordine Missione - anno "+autoPropria.getOrdineMissione().getAnno()+" numero "+autoPropria.getOrdineMissione().getNumero()));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename("Richiesta Uso Auto Propria Ordine di Missione"));
		metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, OrdineMissioneAutoPropria.CMIS_PROPERTY_NAME_DOC_AUTO_PROPRIA);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, OrdineMissioneAutoPropria.CMIS_PROPERTY_NAME_TIPODOC_AUTO_PROPRIA);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, OrdineMissioneAutoPropria.CMIS_PROPERTY_NAME_TIPODOC_AUTO_PROPRIA);
		return metadataProperties;
	}
	
	public Map<String, Object> createMetadataForFileAnnullamentoOrdineMissione(String currentLogin, AnnullamentoOrdineMissione annullamento){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(MissioniCMISService.PROPERTY_NAME, annullamento.getFileName());
		metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename("Annullamento dell'Ordine Missione - anno "+annullamento.getOrdineMissione().getAnno()+" numero "+annullamento.getOrdineMissione().getNumero()));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename("Annullamento dell'Ordine di Missione"));
		metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, AnnullamentoOrdineMissione.CMIS_PROPERTY_NAME_DOC_ANNULLAMENTO);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, AnnullamentoOrdineMissione.CMIS_PROPERTY_NAME_TIPODOC_ANNULLAMENTO);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, AnnullamentoOrdineMissione.CMIS_PROPERTY_NAME_TIPODOC_ANNULLAMENTO);
		return metadataProperties;
	}
	
	@Transactional(readOnly = true)
	public Map<String, Object> createMetadataForFileOrdineMissioneAllegati(String currentLogin, String fileName, String tipoAllegato){
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename(fileName));
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename(fileName));
		metadataProperties.put(MissioniCMISService.PROPERTY_NAME, missioniCMISService.sanitizeFilename(fileName));
		metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, OrdineMissione.CMIS_PROPERTY_NAME_DOC_ALLEGATO);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, tipoAllegato);
		metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, tipoAllegato);
		return metadataProperties;
	}

	@Transactional(readOnly = true)
    public StorageObject salvaStampaAutoPropriaSuCMIS(String currentLogin, byte[] stampa,
			OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException {
		InputStream streamStampa = new ByteArrayInputStream(stampa);
		String path = createFolderOrdineMissione(ordineMissioneAutoPropria.getOrdineMissione());
		Map<String, Object> metadataProperties = createMetadataForFileOrdineMissioneAutoPropria(currentLogin, ordineMissioneAutoPropria);
		try{
			StorageObject node = missioniCMISService.restoreSimpleDocument(
					metadataProperties,
					streamStampa,
					MimeTypes.PDF.mimetype(),
					ordineMissioneAutoPropria.getFileName(), 
					StoragePath.construct(path));
			missioniCMISService.addAspect(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_USO_AUTO_PROPRIA.value());
			return node;
		} catch (Exception e) {
			if (e.getCause() instanceof StorageException)
				throw new ComponentException("CMIS - File ["+ordineMissioneAutoPropria.getFileName()+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!",e);
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")",e);
		}
	}

	@Transactional(readOnly = true)
	public StorageObject salvaStampaAnticipoSuCMIS(String currentLogin, byte[] stampa,
			OrdineMissioneAnticipo ordineMissioneAnticipo) throws ComponentException {
		InputStream streamStampa = new ByteArrayInputStream(stampa);
		String path = createFolderOrdineMissione(ordineMissioneAnticipo.getOrdineMissione());
		Map<String, Object> metadataProperties = createMetadataForFileOrdineMissioneAnticipo(currentLogin, ordineMissioneAnticipo);
		try {
			StorageObject node = null;
			if (!ordineMissioneAnticipo.getOrdineMissione().isStatoInviatoAlFlusso()){
				node = missioniCMISService.restoreSimpleDocument(metadataProperties, streamStampa,
						MimeTypes.PDF.mimetype(), ordineMissioneAnticipo.getFileName(), StoragePath.construct(path));

			}else{
				node = (StorageObject)getObjectAnticipoOrdineMissione(ordineMissioneAnticipo);
				node = missioniCMISService.updateStream(node.getKey(), streamStampa, MimeTypes.PDF.mimetype());
				missioniCMISService.addPropertyForExistingDocument(metadataProperties, node);
			}

			missioniCMISService.addAspect(node,
					CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_RICHIESTA_ANTICIPO.value());
			return node;
		} catch (Exception e) {
			if (e.getCause() instanceof StorageException)
				throw new ComponentException("CMIS - File [" + ordineMissioneAnticipo.getFileName()
						+ "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!",e);
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale ("
					+ Utility.getMessageException(e) + ")",e);
		}
	}
	private StorageObject creaDocumentoAnticipo(String username, OrdineMissioneAnticipo ordineMissioneAnticipo) throws AwesomeException, ComponentException{
		byte[] print = printOrdineMissioneAnticipoService.printOrdineMissioneAnticipo(ordineMissioneAnticipo, username);
		return salvaStampaAnticipoSuCMIS(username, print, ordineMissioneAnticipo);
	}
	private StorageObject creaDocumentoAutoPropria(String username, OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws AwesomeException, ComponentException{
		byte[] print = printOrdineMissioneAutoPropriaService.printOrdineMissioneAutoPropria(ordineMissioneAutoPropria, username);
		return salvaStampaAutoPropriaSuCMIS(username, print, ordineMissioneAutoPropria);
	}

	public List<CMISFileAttachment> getAttachmentsAnticipo(OrdineMissione ordineMissione, Long idAnticipo) {
		List<StorageObject> documents = getAttachmentsAnticipo(ordineMissione);
		return creaCMISFileAttachment(idAnticipo, documents);
	}

	private List<CMISFileAttachment> creaCMISFileAttachment(Long id, List<StorageObject> documents) {
		if (documents != null){
	        List<CMISFileAttachment> lista = new ArrayList<CMISFileAttachment>();
	        for (StorageObject object : documents){
	        	CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
	        	cmisFileAttachment.setNomeFile(object.getPropertyValue(StoragePropertyNames.NAME.value()));
	        	cmisFileAttachment.setId(object.getKey());
	        	cmisFileAttachment.setIdMissione(id);
	        	lista.add(cmisFileAttachment);
	        }
	        return lista;
		}
		return Collections.<CMISFileAttachment>emptyList();
	}
		
	public List<CMISFileAttachment> getAttachmentsOrdineMissione(OrdineMissione ordineMissione, Long idOrdineMissione) {
		List<StorageObject> documents = getDocumentsOrdineMissione(ordineMissione);
		return creaCMISFileAttachment(idOrdineMissione, documents);
	}
		
	public List<StorageObject> getAttachmentsAnticipo(OrdineMissione ordineMissione) {
		StorageObject node = recuperoFolderOrdineMissione(ordineMissione);
		return missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ALLEGATI_ANTICIPO.value());
	}

	public List<StorageObject> getDocumentsOrdineMissione(OrdineMissione ordineMissione) {
		StorageObject folder = recuperoFolderOrdineMissione(ordineMissione);
		List<StorageObject> objs = missioniCMISService.recuperoDocumento(folder, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ALLEGATI.value());

		return objs;
	}

	public CMISFileAttachment uploadAttachmentAnticipo(Principal principal, OrdineMissione ordineMissione, Long idAnticipo, InputStream inputStream, String name, MimeTypes mimeTypes){
		StorageObject so = salvaAllegatoAnticipoCMIS(principal, ordineMissione, inputStream, name, mimeTypes);
		if (so != null){
			CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
			cmisFileAttachment.setId(so.getKey());
			cmisFileAttachment.setNomeFile(name);
	        cmisFileAttachment.setIdMissione(idAnticipo);
			return cmisFileAttachment;
		}
		return null;
	}
	
	private StorageObject salvaAllegatoAnticipoCMIS(Principal principal,
			OrdineMissione ordineMissione, InputStream stream, String fileName,MimeTypes mimeTypes) {
		
		StoragePath cmisPath = buildFolderOrdineMissione(ordineMissione);

		Map<String, Object> metadataProperties = createMetadataForFileOrdineMissioneAllegati(principal.getName(), fileName, OrdineMissione.CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO_ANTICIPO);
		try{
			Document node = missioniCMISService.restoreSimpleDocument(
					metadataProperties,
					stream,
					mimeTypes.mimetype(),
					fileName, 
					cmisPath);
			missioniCMISService.addAspect(so, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ALLEGATI_ANTICIPO.value());
			return so;
		} catch (Exception e) {
			if (e.getCause() instanceof StorageException)
				throw new ComponentException("CMIS - File ["+fileName+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!",e);
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")",e);
		}
	}

	private StoragePath buildFolderOrdineMissione(OrdineMissione ordineMissione) {
		StorageObject folder = (StorageObject) recuperoFolderOrdineMissione(ordineMissione);
		String path = null;
		if (folder == null){
			path = createFolderOrdineMissione(ordineMissione);
		} else {
			path = folder.getPath();
		}
		return StoragePath.construct(path);
	}

	public CMISFileAttachment uploadAttachmentOrdineMissione(Principal principal, OrdineMissione ordineMissione, Long idOrdineMissione, InputStream inputStream, String name, MimeTypes mimeTypes){
		StorageObject so = salvaAllegatoOrdineMissioneCMIS(principal, ordineMissione, inputStream, name, mimeTypes);
		if (so != null){
			CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
			cmisFileAttachment.setId(so.getKey());
			cmisFileAttachment.setNomeFile(name);
	        cmisFileAttachment.setIdMissione(idOrdineMissione);
			return cmisFileAttachment;
		}
		return null;
	}

	private StorageObject salvaAllegatoOrdineMissioneCMIS(Principal principal,
			OrdineMissione ordineMissione, InputStream stream, String fileName,MimeTypes mimeTypes) {
		
		StoragePath cmisPath = buildFolderOrdineMissione(ordineMissione);

		Map<String, Object> metadataProperties = createMetadataForFileOrdineMissioneAllegati(principal.getName(), fileName, OrdineMissione.CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO);
		try{
			StorageObject so = missioniCMISService.restoreSimpleDocument(
					metadataProperties,
					stream,
					mimeTypes.mimetype(),
					fileName, 
					cmisPath);
			missioniCMISService.addAspect(so, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ALLEGATI.value());
			return so;
		} catch (Exception e) {
			if (e.getCause() instanceof StorageException)
				throw new ComponentException("CMIS - File ["+fileName+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!",e);
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")",e);
		}
	}
	public Map<String, byte[]> getFileAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento) {
		String fileName = null;
		byte[] printAnnullamentoMissione = null;
		StorageObject storage = getStorageAnnullamentoOrdineMissione(annullamento);
		if (storage != null){
			fileName = storage.getPropertyValue(StoragePropertyNames.NAME.value());
			InputStream is = missioniCMISService.recuperoStreamFileFromObjectID(storage.getKey());
			if (is != null){
				try {
					printAnnullamentoMissione = IOUtils.toByteArray(is);
					is.close();
				} catch (IOException e) {
					throw new ComponentException("Errore nella conversione dello stream in byte del file (" + Utility.getMessageException(e) + ")",e);
				}
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero del contenuto del file di annullamento sul documentale");
			}
			Map<String, byte[]> map = new HashMap<String, byte[]>();
			map.put(fileName, printAnnullamentoMissione);
			return map;
		}
		throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero del contenuto del file di annullamento sul documentale");
	}
	public Map<String, byte[]> getFileOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo) {
		String fileName = null;
		byte[] printAnticipo = null;
		StorageObject storage = getStorageOrdineMissioneAnticipo(ordineMissioneAnticipo);
		if (storage != null){
			fileName = storage.getPropertyValue(StoragePropertyNames.NAME.value());
			InputStream is = missioniCMISService.recuperoStreamFileFromObjectID(storage.getKey());
			if (is != null){
				try {
					printAnticipo = IOUtils.toByteArray(is);
					is.close();
				} catch (IOException e) {
					throw new ComponentException("Errore nella conversione dello stream in byte del file (" + Utility.getMessageException(e) + ")",e);
				}
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero del contenuto del file di annullamento sul documentale");
			}
			Map<String, byte[]> map = new HashMap<String, byte[]>();
			map.put(fileName, printAnticipo);
			return map;
		}
		throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero del contenuto del file di annullamento sul documentale");
	}

	public Map<String, byte[]> getFileOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) {
		String fileName = null;
		byte[] printAutoPropria = null;
		StorageObject storage = getStorageOrdineMissioneAutoPropria(ordineMissioneAutoPropria);
		if (storage != null){
			fileName = storage.getPropertyValue(StoragePropertyNames.NAME.value());
			InputStream is = missioniCMISService.recuperoStreamFileFromObjectID(storage.getKey());
			if (is != null){
				try {
					printAutoPropria = IOUtils.toByteArray(is);
					is.close();
				} catch (IOException e) {
					throw new ComponentException("Errore nella conversione dello stream in byte del file (" + Utility.getMessageException(e) + ")",e);
				}
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero del contenuto del file di annullamento sul documentale");
			}
			Map<String, byte[]> map = new HashMap<String, byte[]>();
			map.put(fileName, printAutoPropria);
			return map;
		}
		throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero del contenuto del file di annullamento sul documentale");
	}

}
