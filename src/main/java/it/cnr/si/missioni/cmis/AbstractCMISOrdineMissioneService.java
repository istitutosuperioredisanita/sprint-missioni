/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.cmis;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.missioni.service.*;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.proxy.json.object.*;
import it.cnr.si.missioni.util.proxy.json.service.*;
import it.cnr.si.service.SecurityService;
import it.cnr.si.spring.storage.StorageDriver;
import it.cnr.si.spring.storage.StorageException;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public abstract class AbstractCMISOrdineMissioneService implements CMISOrdineMissioneService{
    public static final String PROPERTY_TIPOLOGIA_DOC = "wfcnr:tipologiaDOC";
    public static final String PROPERTY_TIPOLOGIA_DOC_SPECIFICA = "wfcnr:tipologiaDocSpecifica";
    public static final String PROPERTY_TIPOLOGIA_DOC_MISSIONI = "cnrmissioni:tipologiaDocumentoMissione";
    private static final Log logger = LogFactory.getLog(AbstractCMISOrdineMissioneService.class);
    @Autowired
    protected DatiIstitutoService datiIstitutoService;


    @Autowired
    protected Environment env;

    @Autowired
    protected PrintOrdineMissioneService printOrdineMissioneService;

    @Autowired
    protected PrintAnnullamentoOrdineMissioneService printAnnullamentoOrdineMissioneService;

    @Autowired
    protected OrdineMissioneService ordineMissioneService;

    @Autowired
    protected ParametriService parametriService;

    @Autowired
    protected AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;

    @Autowired
    protected UnitaOrganizzativaService unitaOrganizzativaService;

    @Autowired
    protected GaeService gaeService;

    @Autowired
    protected ProgettoService progettoService;

    @Autowired
    protected ImpegnoService impegnoService;

    @Autowired
    protected ImpegnoGaeService impegnoGaeService;

    @Autowired
    protected VoceService voceService;

    @Autowired
    protected UoService uoService;

    @Autowired
    protected PrintOrdineMissioneAnticipoService printOrdineMissioneAnticipoService;

    @Autowired
    protected UtentiPresidenteSpecialiService utentiPresidenteSpecialeService;

    @Autowired
    protected PrintOrdineMissioneAutoPropriaService printOrdineMissioneAutoPropriaService;

    @Autowired
    protected OrdineMissioneAnticipoService ordineMissioneAnticipoService;

    @Autowired
    protected OrdineMissioneAutoPropriaService ordineMissioneAutoPropriaService;

    @Autowired
    protected MissioniCMISService missioniCMISService;

    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected AccountService accountService;

    public CMISOrdineMissione create(OrdineMissione ordineMissione) throws ComponentException {
        return create(ordineMissione, ordineMissione.getAnno());
    }

    public CMISOrdineMissione create(OrdineMissione ordineMissione, Integer annoGestione) throws ComponentException {
        if (ordineMissione != null) {
            CMISOrdineMissione cmisOrdineMissione = new CMISOrdineMissione();
            cmisOrdineMissione.setIdMissioneOrdine(Long.valueOf(ordineMissione.getId().toString()));
            Account account = accountService.loadAccountFromUsername(ordineMissione.getUid());
            account.setUid(ordineMissione.getUid());
            caricaDatiDerivati(ordineMissione);
            OrdineMissioneAnticipo anticipo = null;
            OrdineMissioneAutoPropria autoPropria = null;
            if (ordineMissione != null) {
                anticipo = ordineMissioneAnticipoService.getAnticipo(Long.valueOf(ordineMissione.getId().toString()));
                if (anticipo != null) {
                    ordineMissione.setRichiestaAnticipo("S");
                } else {
                    ordineMissione.setRichiestaAnticipo("N");
                }
                autoPropria = ordineMissioneAutoPropriaService.getAutoPropria(Long.valueOf(ordineMissione.getId().toString()));
                if (autoPropria != null) {
                    ordineMissione.setUtilizzoAutoPropria("S");
                } else {
                    ordineMissione.setUtilizzoAutoPropria("N");
                }
            }
            String username = securityService.getCurrentUserLogin();

            LocalDate data = LocalDate.now();
            int anno = data.getYear();

            Progetto progetto = progettoService.loadModulo(ordineMissione.getPgProgetto(), anno, ordineMissione.getUoSpesa());
            Voce voce = voceService.loadVoce(ordineMissione);
            Gae gae = gaeService.loadGae(ordineMissione);
            UnitaOrganizzativa uoCompetenza = null;
            if (ordineMissione.getUoCompetenza() != null) {
                uoCompetenza = unitaOrganizzativaService.loadUo(ordineMissione.getUoCompetenza(), null, ordineMissione.getAnno());
            }
            UnitaOrganizzativa uoSpesa = unitaOrganizzativaService.loadUo(ordineMissione.getUoSpesa(), null, ordineMissione.getAnno());
            UnitaOrganizzativa uoRich = unitaOrganizzativaService.loadUo(ordineMissione.getUoRich(), null, ordineMissione.getAnno());
            String descrImpegno = "";
            BigDecimal dispImpegno = null;
            if (ordineMissione.getPgObbligazione() != null) {
                if (gae != null) {
                    ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(ordineMissione);
                    if (impegnoGae != null) {
                        descrImpegno = impegnoGae.getDsObbligazione();
                        dispImpegno = impegnoGae.getDisponibilitaImpegno();
                    }
                } else {
                    Impegno impegno = impegnoService.loadImpegno(ordineMissione);
                    if (impegno != null) {
                        descrImpegno = impegno.getDsObbligazione();
                        dispImpegno = impegno.getDisponibilitaImpegno();
                    }
                }
            }

            DatiIstituto datiIstitutoUoRich = datiIstitutoService.getDatiIstituto(ordineMissione.getUoRich(), annoGestione);
            cmisOrdineMissione.setUoCompetenzaSigla(ordineMissione.getUoCompetenza());
            cmisOrdineMissione.setUoSpesaSigla(ordineMissione.getUoSpesa());
            cmisOrdineMissione.setUoRichSigla(ordineMissione.getUoRich());
            String uoCompetenzaPerFlusso = Utility.replace(ordineMissione.getUoCompetenza(), ".", "");
            String uoSpesaPerFlusso = Utility.replace(ordineMissione.getUoSpesa(), ".", "");
            String uoRichPerFlusso = Utility.replace(ordineMissione.getUoRich(), ".", "");
            Uo uoDatiSpesa = uoService.recuperoUo(uoSpesaPerFlusso);
            Uo uoDatiCompetenza = null;
            if (uoCompetenzaPerFlusso != null) {
                uoDatiCompetenza = uoService.recuperoUo(uoCompetenzaPerFlusso);
            }

            cmisOrdineMissione.setMissioneGratuita(ordineMissione.isMissioneGratuita());
            cmisOrdineMissione.setMissionePresidente(ordineMissione.isMissionePresidente());
            cmisOrdineMissione.setMissioneCug(ordineMissione.isMissioneCug());
            cmisOrdineMissione.setMissioneEstera(ordineMissione.isMissioneEstera());
            cmisOrdineMissione.setCdsRich(ordineMissione.getCdsRich());
            cmisOrdineMissione.setCdsSpesa(ordineMissione.getCdsSpesa());
            cmisOrdineMissione.setAnno(ordineMissione.getAnno().toString());
            cmisOrdineMissione.setNumero(ordineMissione.getNumero().toString());
            cmisOrdineMissione.setAnticipo(ordineMissione.getRichiestaAnticipo().equals("S") ? "si" : "no");
            cmisOrdineMissione.setAutoPropriaFlag(ordineMissione.getUtilizzoAutoPropria().equals("S") ? "si" : "no");
            cmisOrdineMissione.setCapitolo(voce == null ? "" : ordineMissione.getVoce());
            cmisOrdineMissione.setDescrizioneCapitolo(voce == null ? "" : voce.getDs_elemento_voce());
            cmisOrdineMissione.setDescrizioneGae(gae == null ? "" : Utility.nvl(gae.getDs_linea_attivita(), ""));
            cmisOrdineMissione.setDescrizioneImpegno(descrImpegno);
            cmisOrdineMissione.setDescrizioneModulo(progetto == null ? "" : progetto.getDs_progetto());
            cmisOrdineMissione.setDescrizioneUoRich(uoRich == null ? "" : uoRich.getDs_unita_organizzativa());
            cmisOrdineMissione.setDescrizioneUoSpesa(uoSpesa == null ? "" : uoSpesa.getDs_unita_organizzativa());
            cmisOrdineMissione.setDescrizioneUoCompetenza(uoCompetenza == null ? "" : uoCompetenza.getDs_unita_organizzativa());
            cmisOrdineMissione.setDisponibilita(Utility.nvl(dispImpegno));
            cmisOrdineMissione.setGae(gae == null ? "" : gae.getCd_linea_attivita());
            cmisOrdineMissione.setImpegnoAnnoCompetenza(ordineMissione.getEsercizioObbligazione() == null ? null : Long.valueOf(ordineMissione.getEsercizioObbligazione()));
            cmisOrdineMissione.setImpegnoAnnoResiduo(ordineMissione.getEsercizioOriginaleObbligazione() == null ? null : Long.valueOf(ordineMissione.getEsercizioOriginaleObbligazione()));
            cmisOrdineMissione.setImpegnoNumero(ordineMissione.getPgObbligazione());
            cmisOrdineMissione.setImportoMissione(ordineMissione.getImportoPresunto() == null ? null : Utility.nvl(ordineMissione.getImportoPresunto()));
            cmisOrdineMissione.setModulo(progetto == null ? "" : progetto.getCd_progetto());
            cmisOrdineMissione.setNoleggioFlag(ordineMissione.getUtilizzoAutoNoleggio().equals("S") ? "si" : "no");
            cmisOrdineMissione.setTrattamento(ordineMissione.decodeTrattamento());
            cmisOrdineMissione.setNote(ordineMissione.getNote() == null ? "" : ordineMissione.getNote());
            cmisOrdineMissione.setNoteSegreteria(ordineMissione.getNoteSegreteria() == null ? "" : ordineMissione.getNoteSegreteria());
            cmisOrdineMissione.setOggetto(ordineMissione.getOggetto());
            cmisOrdineMissione.setPriorita(ordineMissione.getPriorita());
            cmisOrdineMissione.setTaxiFlag(ordineMissione.getUtilizzoTaxi().equals("S") ? "si" : "no");
            cmisOrdineMissione.setAutoServizioFlag(ordineMissione.getUtilizzoAutoServizio().equals("S") ? "si" : "no");
            cmisOrdineMissione.setPersonaSeguitoFlag(ordineMissione.getPersonaleAlSeguito().equals("S") ? "si" : "no");
            cmisOrdineMissione.setUoRich(uoRichPerFlusso);
            cmisOrdineMissione.setUoSpesa(uoSpesaPerFlusso);
            cmisOrdineMissione.setUoCompetenza(uoCompetenzaPerFlusso == null ? "" : uoCompetenzaPerFlusso);
            cmisOrdineMissione.setUserNameResponsabileModulo("");
            cmisOrdineMissione.setUsernameResponsabileGruppo("");
            cmisOrdineMissione.setNoteAutorizzazioniAggiuntive(ordineMissione.getNoteUtilizzoTaxiNoleggio() == null ? "" : ordineMissione.getNoteUtilizzoTaxiNoleggio());
            cmisOrdineMissione.setUsernameRichiedente(Utility.nvl(username, ""));
            cmisOrdineMissione.setUsernameUtenteOrdine(ordineMissione.getUid());
            cmisOrdineMissione.setValidazioneModulo(StringUtils.isEmpty(ordineMissione.getResponsabileGruppo()) ? "no" : "si");
            cmisOrdineMissione.setMissioneGratuita(Utility.nvl(ordineMissione.getMissioneGratuita(), "N").equals("S") ? "si" : "no");

            cmisOrdineMissione.setWfDescription("Ordine di Missione n. " + ordineMissione.getNumero() + " di " + account.getCognome() + " " + account.getNome());
            cmisOrdineMissione.setWfDescriptionComplete(ordineMissione.getDestinazione() + " " + DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " per " + ordineMissione.getOggetto());
            cmisOrdineMissione.setWfDueDate(DateUtils.getDateAsString(ordineMissione.getDataInizioMissione(), DateUtils.PATTERN_DATE_FOR_DOCUMENTALE));
            cmisOrdineMissione.setDestinazione(ordineMissione.getDestinazione());
            cmisOrdineMissione.setMissioneEsteraFlag(ordineMissione.getTipoMissione().equals("E") ? "si" : "no");
            cmisOrdineMissione.setDataInizioMissione(DateUtils.getDateAsString(ordineMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
            cmisOrdineMissione.setDataFineMissione(DateUtils.getDateAsString(ordineMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
            cmisOrdineMissione.setFondi(ordineMissione.getDecodeFondi());
            if (autoPropria != null) {
                cmisOrdineMissione.setPrimoMotivoAutoPropria(Utility.nvl(autoPropria.getUtilizzoMotiviIspettivi(), "N").equals("N") ? "" : CMISOrdineMissione.PRIMO_MOTIVO_UTILIZZO_AUTO_PROPRIA);
                cmisOrdineMissione.setSecondoMotivoAutoPropria(Utility.nvl(autoPropria.getUtilizzoMotiviUrgenza(), "N").equals("N") ? "" : CMISOrdineMissione.SECONDO_MOTIVO_UTILIZZO_AUTO_PROPRIA);
                cmisOrdineMissione.setTerzoMotivoAutoPropria(Utility.nvl(autoPropria.getUtilizzoMotiviTrasporto(), "N").equals("N") ? "" : CMISOrdineMissione.TERZO_MOTIVO_UTILIZZO_AUTO_PROPRIA);
                cmisOrdineMissione.setAltriMotiviAutoPropria(autoPropria.getUtilizzoAltriMotivi() == null ? "" : autoPropria.getUtilizzoAltriMotivi());
            } else {
                cmisOrdineMissione.setPrimoMotivoAutoPropria("");
                cmisOrdineMissione.setSecondoMotivoAutoPropria("");
                cmisOrdineMissione.setTerzoMotivoAutoPropria("");
                cmisOrdineMissione.setAltriMotiviAutoPropria("");
            }
            if (!StringUtils.isEmpty(ordineMissione.getResponsabileGruppo())) {
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
        if (isDevProfile()) {
            userNameFirmatario = recuperoUidDirettoreUo(uo);
        } else {
            userNameFirmatario = accountService.recuperoDirettore(annoGestione, uo, ordineMissione.isMissioneEstera(), account, ordineMissione.getDataInizioMissione(), isUoRich);
        }
        return userNameFirmatario;
    }

    private String recuperoDirettore(OrdineMissione ordineMissione, String uo, Account account, Integer annoGestione) {
        return recuperoDirettore(ordineMissione, uo, account, annoGestione, false);
    }

    protected boolean isDevProfile() {
        return env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT);
    }

    private String impostaValidazioneSpesa(String userNameFirmatario, String userNameFirmatarioSpesa) {
        if (userNameFirmatario != null && userNameFirmatario.equals(userNameFirmatarioSpesa)) {
            return "no";
        }
        return "si";
    }

    private void caricaDatiDerivati(OrdineMissione ordineMissione) {
        if (ordineMissione != null) {
            DatiIstituto dati = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), ordineMissione.getAnno());
            if (dati == null) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non esistono i dati per uo per il codice " + ordineMissione.getUoSpesa() + " nell'anno " + ordineMissione.getAnno());
            }
            ordineMissione.setDatiIstituto(dati);
            if (ordineMissione.getDatiIstituto() == null) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non esistono i dati per istituto per il codice " + ordineMissione.getUoSpesa() + " nell'anno " + ordineMissione.getAnno());
            }
        }
    }

    @Transactional(readOnly = true)
    public StorageObject salvaStampaOrdineMissioneSuCMIS(byte[] stampa, OrdineMissione ordineMissione) {
        CMISOrdineMissione cmisOrdineMissione = create(ordineMissione);
        return salvaStampaOrdineMissioneSuCMIS(stampa, ordineMissione, cmisOrdineMissione);
    }

    private List<String> getBasePathStorage(OrdineMissione ordineMissione) {
        return Arrays.asList(
                missioniCMISService.getBasePath().getPath(),
                Optional.ofNullable(ordineMissione.getUoRich()).orElse(""),
                "Ordini di Missione",
                Optional.ofNullable(ordineMissione.getAnno())
                        .map(esercizio -> "Anno " + esercizio)
                        .orElse("Anno " + "0")
        );
    }

    private String getPathStorage(OrdineMissione ordineMissione) {
        return getBasePathStorage(ordineMissione).stream().collect(
                Collectors.joining(StorageDriver.SUFFIX)
        );
    }

    public String createFolderOrdineMissione(OrdineMissione ordineMissione) {
        return missioniCMISService.createFolderIfNotPresent(getPathStorage(ordineMissione), ordineMissione.constructCMISNomeFile(), getMetadataPropertiesFolderOrdine(ordineMissione));
    }

    private Map<String, Object> getMetadataPropertiesFolderOrdine(OrdineMissione ordineMissione) {
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


    protected StorageObject salvaStampaOrdineMissioneSuCMIS(
            byte[] stampa, OrdineMissione ordineMissione,
            CMISOrdineMissione cmisOrdineMissione) {
        InputStream streamStampa = new ByteArrayInputStream(stampa);
        String path = createFolderOrdineMissione(ordineMissione);
        ordineMissione.setStringBasePath(path);
        Map<String, Object> metadataProperties = createMetadataForFileOrdineMissione(securityService.getCurrentUserLogin(), cmisOrdineMissione);
        try {
            StorageObject so = null;
            if (!ordineMissione.isStatoInviatoAlFlusso()) {
                so = missioniCMISService.restoreSimpleDocument(
                        metadataProperties,
                        streamStampa,
                        MimeTypes.PDF.mimetype(),
                        ordineMissione.getFileName(),
                        StoragePath.construct(path));

            } else {
                so = getObjectOrdineMissione(ordineMissione);
                so = missioniCMISService.updateStream(so.getKey(), streamStampa, MimeTypes.PDF.mimetype());
                missioniCMISService.addPropertyForExistingDocument(metadataProperties, so);
            }
            missioniCMISService.addAspect(so, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ORDINE.value());
            return so;
        } catch (Exception e) {
            if (e.getCause() instanceof StorageException)
                throw new AwesomeException(CodiciErrore.ERRGEN, "File [" + ordineMissione.getFileName() + "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!");
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")");
        }
    }

    public StorageObject salvaStampaAnnullamentoOrdineMissioneSuCMIS(
            byte[] stampa, AnnullamentoOrdineMissione annullamento) {
        InputStream streamStampa = new ByteArrayInputStream(stampa);
        String path = createFolderOrdineMissione(annullamento.getOrdineMissione());
        Map<String, Object> metadataProperties = createMetadataForFileAnnullamentoOrdineMissione(securityService.getCurrentUserLogin(), annullamento);

        try {
            StorageObject so = null;
            StoragePath soFolder = StoragePath.construct(path);
            if (!annullamento.isStatoInviatoAlFlusso()) {
                so = missioniCMISService.restoreSimpleDocument(metadataProperties, streamStampa,
                        MimeTypes.PDF.mimetype(), annullamento.getFileName(), soFolder);

            } else {
                so = getObjectAnnullamentoOrdineMissione(annullamento);
                so = missioniCMISService.updateStream(so.getKey(), streamStampa, MimeTypes.PDF.mimetype());
                missioniCMISService.addPropertyForExistingDocument(metadataProperties, so);
            }

            missioniCMISService.addAspect(so,
                    CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ANNULLAMENTO_ORDINE.value());
            return so;
        } catch (Exception e) {
            if (e.getCause() instanceof StorageException)
                throw new ComponentException("File [" + annullamento.getFileName()
                        + "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!", e);
            throw new ComponentException("Errore nella registrazione del file XML sul Documentale ("
                    + Utility.getMessageException(e) + ")", e);
        }
    }

    private String recuperoUidDirettoreUo(String codiceUo) {
        Uo uo = uoService.recuperoUo(codiceUo);
        return recuperoUidDirettoreUo(codiceUo, uo);
    }

    private String recuperoUidDirettoreUo(String codiceUo, Uo uo) {
        if (uo != null && uo.getCodiceUo() != null && uo.getCodiceUo().equals(codiceUo)) {
            return uo.getUidDirettore();
        }
        return null;
    }

    public Map<String, Object> createMetadataForFileOrdineMissione(String currentLogin, CMISOrdineMissione cmisOrdineMissione) {
        Map<String, Object> metadataProperties = new HashMap<String, Object>();
        metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
        metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename("Ordine Missione - anno " + cmisOrdineMissione.getAnno() + " numero " + cmisOrdineMissione.getNumero()));
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
        metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_UO_ORDINE, cmisOrdineMissione.getDescrizioneUoRich());
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

        metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_UO_ORDINE, cmisOrdineMissione.getUoRich());
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
        metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_VALIDAZIONE_MODULO, !StringUtils.isEmpty(cmisOrdineMissione.getUsernameResponsabileGruppo()));
        return metadataProperties;
    }

    public abstract void avviaFlusso(AnnullamentoOrdineMissione annullamento) ;

    public abstract Boolean isActiveSignFlow();
    public void avviaFlusso(OrdineMissione ordineMissione) {
        String username = securityService.getCurrentUserLogin();
        byte[] stampa = printOrdineMissioneService.printOrdineMissione(ordineMissione, username);
        CMISOrdineMissione cmisOrdineMissione = create(ordineMissione);
        StorageObject documento = salvaStampaOrdineMissioneSuCMIS(stampa, ordineMissione, cmisOrdineMissione);
        OrdineMissioneAnticipo anticipo = ordineMissioneAnticipoService.getAnticipo(Long.valueOf(ordineMissione.getId().toString()));
        OrdineMissioneAutoPropria autoPropria = ordineMissioneAutoPropriaService.getAutoPropria(Long.valueOf(ordineMissione.getId().toString()), true);
        StorageObject documentoAnticipo = null;
        List<StorageObject> allegati = new ArrayList<>();
        List<StorageObject> allegatiOrdineMissione = getDocumentsOrdineMissione(ordineMissione, true);
        if (allegatiOrdineMissione != null && !allegatiOrdineMissione.isEmpty()) {
            allegati.addAll(allegatiOrdineMissione);
        }
        if (anticipo != null) {
            anticipo.setOrdineMissione(ordineMissione);
            documentoAnticipo = creaDocumentoAnticipo(username, anticipo);
            List<StorageObject> allegatiAnticipo = getAttachmentsAnticipo(ordineMissione);
            if (allegatiAnticipo != null && !allegatiAnticipo.isEmpty()) {
                allegati.addAll(allegatiAnticipo);
            }
        }


        StorageObject documentoAutoPropria = null;
        if (autoPropria != null) {
            autoPropria.setOrdineMissione(ordineMissione);
            documentoAutoPropria = creaDocumentoAutoPropria(username, autoPropria);
        }

        if (!isActiveSignFlow()) {
            ordineMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
            FlowResult flowResult = new FlowResult();
            flowResult.setStato(FlowResult.ESITO_FLUSSO_FIRMA_UO);
            ordineMissioneService.aggiornaOrdineMissione(ordineMissione, flowResult);
            flowResult.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);
            ordineMissioneService.aggiornaOrdineMissione(ordineMissione, flowResult);
            return;
        }

        sendOrdineMissioneToSign(ordineMissione, cmisOrdineMissione, documento, anticipo, documentoAnticipo, allegati, documentoAutoPropria);

    }

    protected abstract void sendOrdineMissioneToSign(OrdineMissione ordineMissione, CMISOrdineMissione cmisOrdineMissione, StorageObject documento, OrdineMissioneAnticipo anticipo, StorageObject documentoAnticipo, List<StorageObject> allegati, StorageObject documentoAutoPropria);



    public StorageObject getStorageObjectOrdineMissione(OrdineMissione ordineMissione) throws ComponentException {
        String id = getNodeRefOrdineMissione(ordineMissione);
        if (id != null) {
            return missioniCMISService.recuperoContentFileFromObjectID(id);
        }
        return null;
    }


    public InputStream getStreamOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException {
        String id = getNodeRefOrdineMissioneAutoPropria(ordineMissioneAutoPropria);
        if (id != null) {
            return missioniCMISService.recuperoStreamFileFromObjectID(id);
        }
        return null;
    }

    public InputStream getStreamOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo) throws ComponentException {
        String id = getNodeRefOrdineMissioneAnticipo(ordineMissioneAnticipo);
        if (id != null) {
            return missioniCMISService.recuperoStreamFileFromObjectID(id);
        }
        return null;
    }

    public StorageObject getObjectOrdineMissione(OrdineMissione ordineMissione) throws ComponentException {
        StorageObject fo = recuperoFolderOrdineMissione(ordineMissione);
        List<StorageObject> ordine = missioniCMISService.recuperoDocumento(fo, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ORDINE.value());
        if (ordine.size() == 0)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati all'Ordine di Missione. ID Ordine di Missione:" + ordineMissione.getId() + ", Anno:" + ordineMissione.getAnno() + ", Numero:" + ordineMissione.getNumero());
        else if (ordine.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files ordini di missione aventi l'ID :" + ordineMissione.getId() + ", Anno:" + ordineMissione.getAnno() + ", Numero:" + ordineMissione.getNumero());
        } else {
            StorageObject storageObject = ordine.get(0);
            return storageObject;
        }
    }

    public StorageObject getObjectAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento) throws ComponentException {
        StorageObject node = recuperoFolderOrdineMissione(annullamento.getOrdineMissione());
        List<StorageObject> ordine = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ANNULLAMENTO_ORDINE.value());
        if (ordine.size() == 0)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati all'Annullamento Ordine di Missione. ID Ordine di Missione:" + annullamento.getId() + ", Anno:" + annullamento.getAnno() + ", Numero:" + annullamento.getNumero());
        else if (ordine.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files Annullamento ordini di missione aventi l'ID :" + annullamento.getId() + ", Anno:" + annullamento.getAnno() + ", Numero:" + annullamento.getNumero());
        } else {
            StorageObject nodeFile = ordine.get(0);
            return nodeFile;
        }
    }

    public StorageObject getObjectAnticipoOrdineMissione(OrdineMissioneAnticipo anticipo) throws ComponentException {
        StorageObject node = recuperoFolderOrdineMissione(anticipo.getOrdineMissione());
        List<StorageObject> ant = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_RICHIESTA_ANTICIPO.value());
        if (ant.size() == 0)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono anticipi collegati all'Ordine di Missione. ID Anticipo:" + anticipo.getId());
        else if (ant.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files anticipo di missione aventi l'ID :" + anticipo.getId());
        } else {
            StorageObject nodeFile = ant.get(0);
            return nodeFile;
        }
    }

    public String getNodeRefOrdineMissione(OrdineMissione ordineMissione) throws ComponentException {
        return getObjectOrdineMissione(ordineMissione).getKey();
    }

    public String getNodeRefAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento) throws ComponentException {
        StorageObject nodeFile = getStorageAnnullamentoOrdineMissione(annullamento);
        return nodeFile.getKey();
    }

    public StorageObject getStorageAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento) throws ComponentException {
        OrdineMissione ordineMissione = annullamento.getOrdineMissione();
        StorageObject node = recuperoFolderOrdineMissione(ordineMissione);
        List<StorageObject> objs = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ANNULLAMENTO_ORDINE.value());

        if (objs.size() == 0) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati di annullamento dell'Ordine di Missione. ID Ordine di Missione:" + ordineMissione.getId() + ", Anno:" + ordineMissione.getAnno() + ", Numero:" + ordineMissione.getNumero());
        } else if (objs.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files di annullamento dell'ordine di missione aventi l'ID :" + ordineMissione.getId() + ", Anno:" + ordineMissione.getAnno() + ", Numero:" + ordineMissione.getNumero());
        } else {
            return objs.get(0);
        }
    }

    public StorageObject getStorageOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo) throws ComponentException {
        OrdineMissione ordineMissione = ordineMissioneAnticipo.getOrdineMissione();
        StorageObject node = recuperoFolderOrdineMissione(ordineMissione);
        List<StorageObject> objs = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_RICHIESTA_ANTICIPO.value());

        if (objs.size() == 0) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati di richiesta anticipo dell'Ordine di Missione. ID Ordine di Missione:" + ordineMissione.getId() + ", Anno:" + ordineMissione.getAnno() + ", Numero:" + ordineMissione.getNumero());
        } else if (objs.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files di annullamento dell'ordine di missione aventi l'ID :" + ordineMissione.getId() + ", Anno:" + ordineMissione.getAnno() + ", Numero:" + ordineMissione.getNumero());
        } else {
            return objs.get(0);
        }
    }

    public StorageObject getStorageOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException {
        OrdineMissione ordineMissione = ordineMissioneAutoPropria.getOrdineMissione();
        StorageObject node = recuperoFolderOrdineMissione(ordineMissione);
        List<StorageObject> objs = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_USO_AUTO_PROPRIA.value());

        if (objs.size() == 0) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati di auto propria dell'Ordine di Missione. ID Ordine di Missione:" + ordineMissione.getId() + ", Anno:" + ordineMissione.getAnno() + ", Numero:" + ordineMissione.getNumero());
        } else if (objs.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files di annullamento dell'ordine di missione aventi l'ID :" + ordineMissione.getId() + ", Anno:" + ordineMissione.getAnno() + ", Numero:" + ordineMissione.getNumero());
        } else {
            return objs.get(0);
        }
    }

    public String getNodeRefOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException {
        return getNodeRefOrdineMissioneAutoPropria(ordineMissioneAutoPropria, true);
    }

    public String getNodeRefOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria, Boolean erroreSeNonTrovato) throws ComponentException {
        OrdineMissione ordineMissione = ordineMissioneAutoPropria.getOrdineMissione();
        StorageObject node = recuperoFolderOrdineMissione(ordineMissione);
        List<StorageObject> objs = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_USO_AUTO_PROPRIA.value());

        if (objs.size() == 0 && erroreSeNonTrovato) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati di richiesta di auto propria per l'ordine di missione con ID :" + ordineMissione.getId() + ", Anno:" + ordineMissione.getAnno() + ", Numero:" + ordineMissione.getNumero());
        } else if (objs.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files  di richiesta di auto propria per l'ordine di missione con ID :" + ordineMissione.getId() + ", Anno:" + ordineMissione.getAnno() + ", Numero:" + ordineMissione.getNumero());
        } else {
            StorageObject nodeFile = objs.get(0);
            return nodeFile.getKey();
        }
    }

    public String getNodeRefOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo) {
        OrdineMissione ordineMissione = ordineMissioneAnticipo.getOrdineMissione();
        StorageObject node = recuperoFolderOrdineMissione(ordineMissione);
        List<StorageObject> anticipi = missioniCMISService.recuperoDocumento(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_RICHIESTA_ANTICIPO.value());

        if (anticipi.size() == 0) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti di richiesta anticipo collegati all'Ordine di Missione. ID Ordine di Missione:" + ordineMissione.getId() + ", Anno:" + ordineMissione.getAnno() + ", Numero:" + ordineMissione.getNumero());
        } else if (anticipi.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files  di richiesta anticipo per l'ordine di missione con ID :" + ordineMissione.getId() + ", Anno:" + ordineMissione.getAnno() + ", Numero:" + ordineMissione.getNumero());
        } else {
            StorageObject nodeFile = anticipi.get(0);
            return nodeFile.getKey();
        }
    }

    public StorageObject recuperoFolderOrdineMissione(OrdineMissione ordineMissione) {
        final String path = Arrays.asList(
                missioniCMISService.getBasePath().getPath(),
                Optional.ofNullable(ordineMissione)
                        .map(OrdineMissione::getUoRich)
                        .orElse(""),
                "Ordini di Missione",
                Optional.ofNullable(ordineMissione)
                        .map(ordine -> "Anno " + ordine.getAnno())
                        .orElse("0"),
                String.valueOf(missioniCMISService.sanitizeFilename(ordineMissione.constructCMISNomeFile()))
        ).stream().collect(
                Collectors.joining("/")
        );

        try {
            return Optional.ofNullable(missioniCMISService.getStorageObjectByPath(path))
                    .filter(StorageObject.class::isInstance)
                    .map(StorageObject.class::cast)
                    .orElse(null);
        } catch (StorageException e) {
            String pathFolder = createFolderOrdineMissione(ordineMissione);
            return missioniCMISService.getStorageObjectByPath(pathFolder);
        }
    }





    private MessageForFlowOrdine createJsonForAbortFlowOrdineMissione() {
        MessageForFlowOrdine message = new MessageForFlowOrdine();
        return message;
    }

    public Map<String, Object> createMetadataForFileOrdineMissioneAnticipo(String currentLogin, OrdineMissioneAnticipo anticipo) {
        Map<String, Object> metadataProperties = new HashMap<String, Object>();
        metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
        metadataProperties.put(MissioniCMISService.PROPERTY_NAME, anticipo.getFileName());
        metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename("Anticipo per l'Ordine Missione - anno " + anticipo.getOrdineMissione().getAnno() + " numero " + anticipo.getOrdineMissione().getNumero()));
        metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename("Anticipo Ordine di Missione"));
        metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
        metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, OrdineMissioneAnticipo.CMIS_PROPERTY_NAME_DOC_ANTICIPO);
        metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, OrdineMissioneAnticipo.CMIS_PROPERTY_NAME_TIPODOC_ANTICIPO);
        metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, OrdineMissioneAnticipo.CMIS_PROPERTY_NAME_TIPODOC_ANTICIPO);
        return metadataProperties;
    }

    public Map<String, Object> createMetadataForFileOrdineMissioneAutoPropria(String currentLogin, OrdineMissioneAutoPropria autoPropria) {
        Map<String, Object> metadataProperties = new HashMap<String, Object>();
        metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
        metadataProperties.put(MissioniCMISService.PROPERTY_NAME, autoPropria.getFileName());
        metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename("Richiesta Uso Auto Propria per l'Ordine Missione - anno " + autoPropria.getOrdineMissione().getAnno() + " numero " + autoPropria.getOrdineMissione().getNumero()));
        metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename("Richiesta Uso Auto Propria Ordine di Missione"));
        metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
        metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, OrdineMissioneAutoPropria.CMIS_PROPERTY_NAME_DOC_AUTO_PROPRIA);
        metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, OrdineMissioneAutoPropria.CMIS_PROPERTY_NAME_TIPODOC_AUTO_PROPRIA);
        metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, OrdineMissioneAutoPropria.CMIS_PROPERTY_NAME_TIPODOC_AUTO_PROPRIA);
        return metadataProperties;
    }

    public Map<String, Object> createMetadataForFileAnnullamentoOrdineMissione(String currentLogin, AnnullamentoOrdineMissione annullamento) {
        Map<String, Object> metadataProperties = new HashMap<String, Object>();
        metadataProperties.put(MissioniCMISService.PROPERTY_NAME, annullamento.getFileName());
        metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), OrdineMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
        metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename("Annullamento dell'Ordine Missione - anno " + annullamento.getOrdineMissione().getAnno() + " numero " + annullamento.getOrdineMissione().getNumero()));
        metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename("Annullamento dell'Ordine di Missione"));
        metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
        metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, AnnullamentoOrdineMissione.CMIS_PROPERTY_NAME_DOC_ANNULLAMENTO);
        metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, AnnullamentoOrdineMissione.CMIS_PROPERTY_NAME_TIPODOC_ANNULLAMENTO);
        metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, AnnullamentoOrdineMissione.CMIS_PROPERTY_NAME_TIPODOC_ANNULLAMENTO);
        return metadataProperties;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> createMetadataForFileOrdineMissioneAllegati(String currentLogin, String fileName, String tipoAllegato) {
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
        try {
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
                throw new ComponentException("File [" + ordineMissioneAutoPropria.getFileName() + "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!", e);
            throw new ComponentException("Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")", e);
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
            if (!ordineMissioneAnticipo.getOrdineMissione().isStatoInviatoAlFlusso()) {
                node = missioniCMISService.restoreSimpleDocument(metadataProperties, streamStampa,
                        MimeTypes.PDF.mimetype(), ordineMissioneAnticipo.getFileName(), StoragePath.construct(path));

            } else {
                node = getObjectAnticipoOrdineMissione(ordineMissioneAnticipo);
                node = missioniCMISService.updateStream(node.getKey(), streamStampa, MimeTypes.PDF.mimetype());
                missioniCMISService.addPropertyForExistingDocument(metadataProperties, node);
            }

            missioniCMISService.addAspect(node,
                    CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_RICHIESTA_ANTICIPO.value());
            return node;
        } catch (Exception e) {
            if (e.getCause() instanceof StorageException)
                throw new ComponentException("File [" + ordineMissioneAnticipo.getFileName()
                        + "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!", e);
            throw new ComponentException("Errore nella registrazione del file XML sul Documentale ("
                    + Utility.getMessageException(e) + ")", e);
        }
    }

    protected StorageObject creaDocumentoAnticipo(String username, OrdineMissioneAnticipo ordineMissioneAnticipo) throws AwesomeException, ComponentException {
        byte[] print = printOrdineMissioneAnticipoService.printOrdineMissioneAnticipo(ordineMissioneAnticipo, username);
        return salvaStampaAnticipoSuCMIS(username, print, ordineMissioneAnticipo);
    }

    protected StorageObject creaDocumentoAutoPropria(String username, OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws AwesomeException, ComponentException {
        byte[] print = printOrdineMissioneAutoPropriaService.printOrdineMissioneAutoPropria(ordineMissioneAutoPropria, username);
        return salvaStampaAutoPropriaSuCMIS(username, print, ordineMissioneAutoPropria);
    }

    public List<CMISFileAttachment> getAttachmentsAnticipo(OrdineMissione ordineMissione, Long idAnticipo) {
        List<StorageObject> documents = getAttachmentsAnticipo(ordineMissione);
        return creaCMISFileAttachment(idAnticipo, documents);
    }

    private List<CMISFileAttachment> creaCMISFileAttachment(Long id, List<StorageObject> documents) {
        if (documents != null) {
            List<CMISFileAttachment> lista = new ArrayList<CMISFileAttachment>();
            for (StorageObject object : documents) {
                CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
                cmisFileAttachment.setNomeFile(object.getPropertyValue(StoragePropertyNames.NAME.value()));
                cmisFileAttachment.setId(object.getKey());
                cmisFileAttachment.setIdMissione(id);
                lista.add(cmisFileAttachment);
            }
            return lista;
        }
        return Collections.emptyList();
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
        return getDocumentsOrdineMissione(ordineMissione, false);
    }

    public List<StorageObject> getDocumentsOrdineMissione(OrdineMissione ordineMissione, Boolean recuperoFileEliminati) {
        StorageObject folder = recuperoFolderOrdineMissione(ordineMissione);
        List<StorageObject> objs = missioniCMISService.recuperoDocumento(folder, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ALLEGATI.value(), recuperoFileEliminati);

        return objs;
    }

    public CMISFileAttachment uploadAttachmentAnticipo(OrdineMissione ordineMissione, Long idAnticipo, InputStream inputStream, String name, MimeTypes mimeTypes) {
        StorageObject so = salvaAllegatoAnticipoCMIS(ordineMissione, inputStream, name, mimeTypes);
        if (so != null) {
            CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
            cmisFileAttachment.setId(so.getKey());
            cmisFileAttachment.setNomeFile(name);
            cmisFileAttachment.setIdMissione(idAnticipo);
            return cmisFileAttachment;
        }
        return null;
    }

    private StorageObject salvaAllegatoAnticipoCMIS(
            OrdineMissione ordineMissione, InputStream stream, String fileName, MimeTypes mimeTypes) {

        StoragePath cmisPath = buildFolderOrdineMissione(ordineMissione);

        Map<String, Object> metadataProperties = createMetadataForFileOrdineMissioneAllegati(securityService.getCurrentUserLogin(), fileName, OrdineMissione.CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO_ANTICIPO);
        try {
            StorageObject so = missioniCMISService.restoreSimpleDocument(
                    metadataProperties,
                    stream,
                    mimeTypes.mimetype(),
                    fileName,
                    cmisPath);
            missioniCMISService.addAspect(so, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_ALLEGATI_ANTICIPO.value());
            return so;
        } catch (Exception e) {
            if (e.getCause() instanceof StorageException)
                throw new ComponentException("File [" + fileName + "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!", e);
            throw new ComponentException("Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")", e);
        }
    }

    private StoragePath buildFolderOrdineMissione(OrdineMissione ordineMissione) {
        StorageObject folder = recuperoFolderOrdineMissione(ordineMissione);
        String path = null;
        if (folder == null) {
            path = createFolderOrdineMissione(ordineMissione);
        } else {
            path = folder.getPath();
        }
        return StoragePath.construct(path);
    }

    public CMISFileAttachment uploadAttachmentOrdineMissione(OrdineMissione ordineMissione, Long idOrdineMissione, InputStream inputStream, String name, MimeTypes mimeTypes) {
        StorageObject so = salvaAllegatoOrdineMissioneCMIS(ordineMissione, inputStream, name, mimeTypes);
        if (so != null) {
            CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
            cmisFileAttachment.setId(so.getKey());
            cmisFileAttachment.setNomeFile(name);
            cmisFileAttachment.setIdMissione(idOrdineMissione);
            return cmisFileAttachment;
        }
        return null;
    }

    private StorageObject salvaAllegatoOrdineMissioneCMIS(
            OrdineMissione ordineMissione, InputStream stream, String fileName, MimeTypes mimeTypes) {

        StoragePath cmisPath = buildFolderOrdineMissione(ordineMissione);

        Map<String, Object> metadataProperties = createMetadataForFileOrdineMissioneAllegati(securityService.getCurrentUserLogin(), fileName, OrdineMissione.CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO);
        try {
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
                throw new ComponentException("File [" + fileName + "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!", e);
            throw new ComponentException("Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")", e);
        }
    }

    public Map<String, byte[]> getFileAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento) {
        String fileName = null;
        byte[] printAnnullamentoMissione = null;
        StorageObject storage = getStorageAnnullamentoOrdineMissione(annullamento);
        if (storage != null) {
            fileName = storage.getPropertyValue(StoragePropertyNames.NAME.value());
            InputStream is = missioniCMISService.recuperoStreamFileFromObjectID(storage.getKey());
            if (is != null) {
                try {
                    printAnnullamentoMissione = IOUtils.toByteArray(is);
                    is.close();
                } catch (IOException e) {
                    throw new ComponentException("Errore nella conversione dello stream in byte del file (" + Utility.getMessageException(e) + ")", e);
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
        if (storage != null) {
            fileName = storage.getPropertyValue(StoragePropertyNames.NAME.value());
            InputStream is = missioniCMISService.recuperoStreamFileFromObjectID(storage.getKey());
            if (is != null) {
                try {
                    printAnticipo = IOUtils.toByteArray(is);
                    is.close();
                } catch (IOException e) {
                    throw new ComponentException("Errore nella conversione dello stream in byte del file (" + Utility.getMessageException(e) + ")", e);
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
        if (storage != null) {
            fileName = storage.getPropertyValue(StoragePropertyNames.NAME.value());
            InputStream is = missioniCMISService.recuperoStreamFileFromObjectID(storage.getKey());
            if (is != null) {
                try {
                    printAutoPropria = IOUtils.toByteArray(is);
                    is.close();
                } catch (IOException e) {
                    throw new ComponentException("Errore nella conversione dello stream in byte del file (" + Utility.getMessageException(e) + ")", e);
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

    public List<StorageObject> getAllDocumentsOrdineMissione(OrdineMissione missione) throws ComponentException {
        StorageObject node = recuperoFolderOrdineMissione(missione);
        return Optional.ofNullable(node)
                .map(storageObject -> missioniCMISService.getChildren(storageObject.getKey()))
                .orElse(null);
    }

}
