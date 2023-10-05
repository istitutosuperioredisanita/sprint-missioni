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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.DatiFlusso;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.repository.CRUDComponentSession;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CMISRimborsoMissioneService {
    public static final String PROPERTY_TIPOLOGIA_DOC = "wfcnr:tipologiaDOC";
    public static final String PROPERTY_TIPOLOGIA_DOC_SPECIFICA = "wfcnr:tipologiaDocSpecifica";
    public static final String PROPERTY_TIPOLOGIA_DOC_MISSIONI = "cnrmissioni:tipologiaDocumentoMissione";
    @Autowired
    NazioneService nazioneService;
    @Autowired
    SecurityService securityService;
    @Autowired
    private DatiIstitutoService datiIstitutoService;
    @Autowired
    private Environment env;
    @Autowired
    private UnitaOrganizzativaService unitaOrganizzativaService;
    @Autowired
    private GaeService gaeService;
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
    private UtentiPresidenteSpecialiService utentiPresidenteSpecialeService;
    @Autowired
    private RimborsoMissioneDettagliService rimborsoMissioneDettagliService;
    @Autowired(required = false)
    private MessageForFlowsService messageForFlowsService;

    public List<CMISFileAttachment> getAttachmentsDetail(Long idDettagliorimborso) throws ComponentException {
        RimborsoMissioneDettagli dettaglio = rimborsoMissioneDettagliService.getRimborsoMissioneDettaglio(idDettagliorimborso);
        List<StorageObject> children = getChildrenDettaglio(dettaglio);
        if (children != null) {
            List<CMISFileAttachment> lista = new ArrayList<CMISFileAttachment>();
            for (StorageObject doc : children) {
                CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
                cmisFileAttachment.setNomeFile(doc.getPropertyValue(StoragePropertyNames.NAME.value()));
                cmisFileAttachment.setId(doc.getKey());
                cmisFileAttachment.setIdMissione(idDettagliorimborso);
                lista.add(cmisFileAttachment);
            }
            return lista;
        }
        return Collections.emptyList();
    }

    public List<StorageObject> getChildrenDettaglio(RimborsoMissioneDettagli dettaglio) {
        return getChildrenDettaglio(dettaglio, false);
    }

    public List<StorageObject> getChildrenDettaglio(RimborsoMissioneDettagli dettaglio, Boolean recuperoFileEliminati) {
        StorageObject folderDettaglio = getFolderDettaglioRimborso(dettaglio);

        if (folderDettaglio != null) {
            List<StorageObject> children = missioniCMISService.recuperoDocumento(folderDettaglio, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_SCONTRINI.value(), recuperoFileEliminati);
            return children;
        }
        return null;
    }

    public StorageObject getFolderDettaglioRimborso(RimborsoMissioneDettagli dettaglio) throws ComponentException {
        StorageObject folderRimborso = recuperoFolderRimborsoMissione(dettaglio.getRimborsoMissione());
        if (folderRimborso != null) {
            StorageObject folderDettaglio = null;
            String path = folderRimborso.getPath();
            try {
                folderDettaglio = missioniCMISService.getStorageObjectByPath(path + "/" + dettaglio.constructCMISNomeFile());
            } catch (StorageException e) {
                return null;
            }
            return folderDettaglio;
        }
        return null;
    }

    public DatiFlusso recuperoDatiFlusso(RimborsoMissione rimborsoMissione, Integer anno, Account account) {
        DatiFlusso datiFlusso = new DatiFlusso();
        UnitaOrganizzativa uoSpesa = unitaOrganizzativaService.loadUo(rimborsoMissione.getUoSpesa(), null, anno);
        UnitaOrganizzativa uoRich = unitaOrganizzativaService.loadUo(rimborsoMissione.getUoRich(), null, anno);
        String uoCompetenzaPerFlusso = Utility.replace(rimborsoMissione.getUoCompetenza(), ".", "");
        String uoSpesaPerFlusso = Utility.replace(rimborsoMissione.getUoSpesa(), ".", "");
        String uoRichPerFlusso = Utility.replace(rimborsoMissione.getUoRich(), ".", "");
        datiFlusso.setUoCompetenzaPerFlusso(uoCompetenzaPerFlusso);
        datiFlusso.setUoRichPerFlusso(uoRichPerFlusso);
        datiFlusso.setUoSpesaPerFlusso(uoSpesaPerFlusso);
        datiFlusso.setUoRich(uoRich);
        datiFlusso.setUoSpesa(uoSpesa);
        return datiFlusso;
    }

    public CMISRimborsoMissione create(RimborsoMissione rimborsoMissione) throws ComponentException {
        CMISRimborsoMissione cmisRimborsoMissione = new CMISRimborsoMissione();
        cmisRimborsoMissione.setIdMissioneRimborso(Long.valueOf(rimborsoMissione.getId().toString()));
        caricaDatiDerivati(rimborsoMissione);

        if (rimborsoMissione != null && rimborsoMissione.getOrdineMissione() != null) {
            OrdineMissione ordineMissione = rimborsoMissione.getOrdineMissione();
            OrdineMissione ordineMissioneDB = crudServiceBean.findById(OrdineMissione.class, ordineMissione.getId());
            if (ordineMissioneDB != null) {
                ordineMissione = ordineMissioneDB;
            }
        }

        String username = securityService.getCurrentUserLogin();

        Account account = accountService.loadAccountFromUsername(rimborsoMissione.getUid());
        account.setUid(rimborsoMissione.getUid());
        Voce voce = voceService.loadVoce(rimborsoMissione);
        Gae gae = gaeService.loadGae(rimborsoMissione);
        UnitaOrganizzativa uoCompetenza = null;
        LocalDate data = LocalDate.now();
        int anno = data.getYear();

        if (rimborsoMissione.getUoCompetenza() != null) {
            uoCompetenza = unitaOrganizzativaService.loadUo(rimborsoMissione.getUoCompetenza(), null, anno);
        }
        String descrImpegno = "";
        BigDecimal dispImpegno = null;
        if (rimborsoMissione.getPgObbligazione() != null) {
            if (gae != null) {
                ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(rimborsoMissione);
                if (impegnoGae != null) {
                    descrImpegno = impegnoGae.getDsObbligazione();
                    dispImpegno = impegnoGae.getDisponibilitaImpegno();
                }
            } else {
                Impegno impegno = impegnoService.loadImpegno(rimborsoMissione);
                if (impegno != null) {
                    descrImpegno = impegno.getDsObbligazione();
                    dispImpegno = impegno.getDisponibilitaImpegno();
                }
            }
        }

        DatiFlusso datiFlusso = recuperoDatiFlusso(rimborsoMissione, anno, account);

        GregorianCalendar dataScadenzaFlusso = new GregorianCalendar();
        dataScadenzaFlusso.setTime(DateUtils.getCurrentTime());
        dataScadenzaFlusso.add(Calendar.DAY_OF_MONTH, 7);

        cmisRimborsoMissione.setUoCompetenzaSigla(rimborsoMissione.getUoCompetenza());
        cmisRimborsoMissione.setUoSpesaSigla(rimborsoMissione.getUoSpesa());
        cmisRimborsoMissione.setUoRichSigla(rimborsoMissione.getUoRich());
        cmisRimborsoMissione.setMissionePresidente(rimborsoMissione.isMissionePresidente());
        cmisRimborsoMissione.setMissioneCug(rimborsoMissione.isMissioneCug());
        cmisRimborsoMissione.setMissioneEstera(rimborsoMissione.isMissioneEstera());
        cmisRimborsoMissione.setCdsRich(rimborsoMissione.getCdsRich());
        cmisRimborsoMissione.setCdsSpesa(rimborsoMissione.getCdsSpesa());
        cmisRimborsoMissione.setAnno(rimborsoMissione.getAnno().toString());
        cmisRimborsoMissione.setNumero(rimborsoMissione.getNumero().toString());
        cmisRimborsoMissione.setCapitolo(voce == null ? "" : rimborsoMissione.getVoce());
        cmisRimborsoMissione.setDescrizioneCapitolo(voce == null ? "" : voce.getDs_elemento_voce());
        cmisRimborsoMissione.setDescrizioneGae(gae == null ? "" : Utility.nvl(gae.getDs_linea_attivita(), ""));
        cmisRimborsoMissione.setDescrizioneImpegno(descrImpegno);
        cmisRimborsoMissione.setDescrizioneUoRich(datiFlusso.getUoRich() == null ? "" : datiFlusso.getUoRich().getDs_unita_organizzativa());
        cmisRimborsoMissione.setDescrizioneUoSpesa(datiFlusso.getUoSpesa() == null ? "" : datiFlusso.getUoSpesa().getDs_unita_organizzativa());
        cmisRimborsoMissione.setDescrizioneUoCompetenza(uoCompetenza == null ? "" : uoCompetenza.getDs_unita_organizzativa());
        cmisRimborsoMissione.setDisponibilita(Utility.nvl(dispImpegno));
        cmisRimborsoMissione.setGae(gae == null ? "" : gae.getCd_linea_attivita());
        cmisRimborsoMissione.setImpegnoAnnoCompetenza(rimborsoMissione.getEsercizioObbligazione() == null ? null : Long.valueOf(rimborsoMissione.getEsercizioObbligazione()));
        cmisRimborsoMissione.setImpegnoAnnoResiduo(rimborsoMissione.getEsercizioOriginaleObbligazione() == null ? null : Long.valueOf(rimborsoMissione.getEsercizioOriginaleObbligazione()));
        cmisRimborsoMissione.setImpegnoNumero(rimborsoMissione.getPgObbligazione());
        cmisRimborsoMissione.setNoleggioFlag(rimborsoMissione.getUtilizzoAutoNoleggio().equals("S") ? "si" : "no");
        cmisRimborsoMissione.setNote(rimborsoMissione.getNote() == null ? "" : rimborsoMissione.getNote());
        cmisRimborsoMissione.setNoteSegreteria(rimborsoMissione.getNoteSegreteria() == null ? "" : rimborsoMissione.getNoteSegreteria());
        cmisRimborsoMissione.setOggetto(rimborsoMissione.getOggetto());
        cmisRimborsoMissione.setTaxiFlag(rimborsoMissione.getUtilizzoTaxi().equals("S") ? "si" : "no");
        cmisRimborsoMissione.setAutoServizioFlag(rimborsoMissione.getUtilizzoAutoServizio().equals("S") ? "si" : "no");
        cmisRimborsoMissione.setPersonaSeguitoFlag(rimborsoMissione.getPersonaleAlSeguito().equals("S") ? "si" : "no");
        cmisRimborsoMissione.setUoRich(datiFlusso.getUoRichPerFlusso());
        cmisRimborsoMissione.setUoSpesa(datiFlusso.getUoSpesaPerFlusso());
        cmisRimborsoMissione.setUoCompetenza(datiFlusso.getUoCompetenzaPerFlusso() == null ? "" : datiFlusso.getUoCompetenzaPerFlusso());
        cmisRimborsoMissione.setUserNameResponsabileModulo("");
        cmisRimborsoMissione.setUsernameRichiedente(username);
        cmisRimborsoMissione.setNoteAutorizzazioniAggiuntive(rimborsoMissione.getNoteUtilizzoTaxiNoleggio() == null ? "" : rimborsoMissione.getNoteUtilizzoTaxiNoleggio());
        cmisRimborsoMissione.setAnticipoRicevuto(rimborsoMissione.getAnticipoRicevuto().equals("S") ? "si" : "no");
        cmisRimborsoMissione.setAnnoMandato(rimborsoMissione.getAnticipoAnnoMandato() == null ? "" : rimborsoMissione.getAnticipoAnnoMandato().toString());
        cmisRimborsoMissione.setNumeroMandato(rimborsoMissione.getAnticipoNumeroMandato() == null ? "" : rimborsoMissione.getAnticipoNumeroMandato().toString());
        cmisRimborsoMissione.setImportoMandato(rimborsoMissione.getAnticipoImporto() == null ? "" : Utility.nvl(rimborsoMissione.getAnticipoImporto()).toString());
        cmisRimborsoMissione.setUsernameUtenteOrdine(rimborsoMissione.getUid());
        cmisRimborsoMissione.setUsernameUtenteOrdine(rimborsoMissione.getUid());
        cmisRimborsoMissione.setValidazioneSpesa(impostaValidazioneSpesa(datiFlusso.getUsernamePrimoFirmatario(), datiFlusso.getUsernameFirmatarioSpesa()));
        cmisRimborsoMissione.setWfDescription("Rimborso Missione n. " + rimborsoMissione.getNumero() + " di " + account.getCognome() + " " + account.getNome());
        cmisRimborsoMissione.setWfDescriptionComplete("Missione a " + rimborsoMissione.getDestinazione() + " del " + DateUtils.getDefaultDateAsString(rimborsoMissione.getDataInizioMissione()) + " per " + rimborsoMissione.getOggetto());
        cmisRimborsoMissione.setWfDueDate(DateUtils.getDateAsString(dataScadenzaFlusso.getTime(), DateUtils.PATTERN_DATE_FOR_DOCUMENTALE));
        cmisRimborsoMissione.setDestinazione(rimborsoMissione.getDestinazione());
        cmisRimborsoMissione.setTrattamento(rimborsoMissione.decodeTrattamento());
        cmisRimborsoMissione.setDifferenzeOrdineRimborso(rimborsoMissioneService.getDifferenzeRimborsoOrdine(rimborsoMissione));
        cmisRimborsoMissione.setMissioneEsteraFlag(rimborsoMissione.getTipoMissione().equals("E") ? "si" : "no");
        cmisRimborsoMissione.setDataInizioMissione(DateUtils.getDateAsString(rimborsoMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
        cmisRimborsoMissione.setDataFineMissione(DateUtils.getDateAsString(rimborsoMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
        if (rimborsoMissione.getDataInizioEstero() != null) {
            cmisRimborsoMissione.setDataInizioEstero(DateUtils.getDateAsString(rimborsoMissione.getDataInizioEstero(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
        }
        if (rimborsoMissione.getDataFineEstero() != null) {
            cmisRimborsoMissione.setDataFineEstero(DateUtils.getDateAsString(rimborsoMissione.getDataFineEstero(), DateUtils.PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE));
        }
        cmisRimborsoMissione.setIdOrdineMissione(rimborsoMissione.getOrdineMissione() == null ? "" : rimborsoMissione.getOrdineMissione().getId().toString());
        cmisRimborsoMissione.setWfOrdineMissione(rimborsoMissione.getOrdineMissione() == null || rimborsoMissione.getOrdineMissione().getIdFlusso() == null || rimborsoMissione.getOrdineMissione().isOrdineMissioneVecchiaScrivania() ? null : rimborsoMissione.getOrdineMissione().getIdFlusso());
        cmisRimborsoMissione.setTotaleRimborsoMissione(rimborsoMissione.getTotaleRimborso());
        cmisRimborsoMissione.setNomeFile(rimborsoMissione.getFileName());
        return cmisRimborsoMissione;
    }

    private String recuperoDirettore(RimborsoMissione rimborsoMissione, String uo, Account account, Boolean isUoRich) {
        String userNameFirmatario;
        if (isDevProfile()) {
            userNameFirmatario = recuperoUidDirettoreUo(uo);
        } else {
            userNameFirmatario = accountService.recuperoDirettore(rimborsoMissione.getAnno(), uo, rimborsoMissione.isMissioneEstera(), account, rimborsoMissione.getDataInizioMissione(), isUoRich);
        }
        return userNameFirmatario;
    }

    private String recuperoDirettore(RimborsoMissione rimborsoMissione, String uo, Account account) {
        return recuperoDirettore(rimborsoMissione, uo, account, false);
    }

    private boolean isDevProfile() {
        return env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT);
    }

    private String impostaValidazioneSpesa(String userNameFirmatario, String userNameFirmatarioSpesa) {
        return "si";
    }

    private void caricaDatiDerivati(RimborsoMissione rimborsoMissione) throws ComponentException {
        if (rimborsoMissione != null) {
            DatiIstituto dati = datiIstitutoService.getDatiIstituto(rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno());
            if (dati == null) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non esistono i dati per uo per il codice " + rimborsoMissione.getUoSpesa() + " nell'anno " + rimborsoMissione.getAnno());
            }
            rimborsoMissione.setDatiIstituto(dati);
            if (rimborsoMissione.getDatiIstituto() == null) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non esistono i dati per istituto per il codice " + rimborsoMissione.getCdsSpesa() + " nell'anno " + rimborsoMissione.getAnno());
            }
        }
    }

    @Transactional(readOnly = true)
    public StorageObject salvaStampaRimborsoMissioneSuCMIS(byte[] stampa, RimborsoMissione rimborsoMissione) throws ComponentException {
        CMISRimborsoMissione cmisRimborsoMissione = create(rimborsoMissione);
        return salvaStampaRimborsoMissioneSuCMIS(stampa, rimborsoMissione, cmisRimborsoMissione);
    }

    private List<String> getBasePathStorage(RimborsoMissione rimborsoMissione) {
        return Arrays.asList(
                missioniCMISService.getBasePath().getPath(),
                Optional.ofNullable(rimborsoMissione.getUoSpesa()).orElse(""),
                "Rimborso Missione",
                Optional.ofNullable(rimborsoMissione.getAnnoIniziale())
                        .map(esercizio -> "Anno " + esercizio)
                        .orElse("Anno " + "0")
        );
    }

    private String getPathStorage(RimborsoMissione rimborsoMissione) {
        return getBasePathStorage(rimborsoMissione).stream().collect(
                Collectors.joining(StorageDriver.SUFFIX)
        );
    }

    public String createFolderRimborsoMissione(RimborsoMissione rimborsoMissione) {
        return missioniCMISService.createFolderIfNotPresent(getPathStorage(rimborsoMissione), rimborsoMissione.constructCMISNomeFile(), getMetadataPropertiesFolderRimborso(rimborsoMissione));
    }

    public String createFolderRimborsoMissioneDettaglio(RimborsoMissioneDettagli dettaglio, String path) {
        return missioniCMISService.createFolderIfNotPresent(path, dettaglio.constructCMISNomeFile(), getMetadataPropertiesFolderRimborsoDettaglio(dettaglio));
    }

    public Map<String, Object> getMetadataPropertiesFolderRimborso(RimborsoMissione rimborsoMissione) {
        Map<String, Object> metadataProperties = new HashMap<String, Object>();
        String name = rimborsoMissione.constructCMISNomeFile();

        metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), OrdineMissione.CMIS_PROPERTY_MAIN);
        metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename(name));
        metadataProperties.put(MissioniCMISService.PROPERTY_NAME, missioniCMISService.sanitizeFilename(name));
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
        if (rimborsoMissione.getDataInizioEstero() != null) {
            metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_NAME_DATA_INIZIO_MISSIONE_ESTERO, DateUtils.getDate(rimborsoMissione.getDataInizioEstero()));
            metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_NAME_DATA_FINE_MISSIONE_ESTERO, DateUtils.getDate(rimborsoMissione.getDataFineEstero()));
        }
        metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_NAME_ID_ORDINE_MISSIONE, rimborsoMissione.getOrdineMissione().getId());
        metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_NAME_TOT_RIMBORSO_MISSIONE, rimborsoMissione.getTotaleRimborso());
        List<String> aspectsToAdd = new ArrayList<String>();
        aspectsToAdd.add(MissioniCMISService.ASPECT_TITLED);
        aspectsToAdd.add(CMISMissioniAspect.RIMBORSO_MISSIONE_ASPECT.value());
        metadataProperties.put(StoragePropertyNames.SECONDARY_OBJECT_TYPE_IDS.value(), aspectsToAdd);
        return metadataProperties;
    }

    private Map<String, Object> getMetadataPropertiesFolderRimborsoDettaglio(RimborsoMissioneDettagli dettaglio) {
        Map<String, Object> metadataProperties = new HashMap<String, Object>();
        String name = dettaglio.constructCMISNomeFile();
        String folderName = name;
        folderName = missioniCMISService.sanitizeFolderName(folderName);
        metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), RimborsoMissioneDettagli.CMIS_PROPERTY_MAIN);
        metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename(name));
        metadataProperties.put(MissioniCMISService.PROPERTY_NAME, missioniCMISService.sanitizeFilename(name));
        metadataProperties.put(RimborsoMissioneDettagli.CMIS_PROPERTY_ID_DETTAGLIO_RIMBORSO, dettaglio.getId());
        metadataProperties.put(RimborsoMissioneDettagli.CMIS_PROPERTY_CD_TIPO_SPESA_DETTAGLIO_RIMBORSO_MISSIONE, dettaglio.getCdTiSpesa());
        metadataProperties.put(RimborsoMissioneDettagli.CMIS_PROPERTY_DS_TIPO_SPESA_DETTAGLIO_RIMBORSO_MISSIONE, dettaglio.getDsTiSpesa());
        metadataProperties.put(RimborsoMissioneDettagli.CMIS_PROPERTY_DATA_SPESA_DETTAGLIO_RIMBORSO_MISSIONE, DateUtils.getDate(dettaglio.getDataSpesa()));
        metadataProperties.put(RimborsoMissioneDettagli.CMIS_PROPERTY_RIGA_DETTAGLIO_RIMBORSO_MISSIONE, dettaglio.getRiga());
        List<String> aspectsToAdd = new ArrayList<String>();
        aspectsToAdd.add(MissioniCMISService.ASPECT_TITLED);
        metadataProperties.put(StoragePropertyNames.SECONDARY_OBJECT_TYPE_IDS.value(), aspectsToAdd);
        return metadataProperties;
    }

    private StorageObject salvaStampaRimborsoMissioneSuCMIS(
            byte[] stampa, RimborsoMissione rimborsoMissione,
            CMISRimborsoMissione cmisRimborsoMissione) {
        InputStream streamStampa = new ByteArrayInputStream(stampa);
        String path = createFolderRimborsoMissione(rimborsoMissione);
        rimborsoMissione.setStringBasePath(path);
        Map<String, Object> metadataProperties = createMetadataForFileRimborsoMissione(securityService.getCurrentUserLogin(), cmisRimborsoMissione);
        try {
            StorageObject node = null;
            if (!rimborsoMissione.isStatoInviatoAlFlusso()) {
                node = missioniCMISService.restoreSimpleDocument(
                        metadataProperties,
                        streamStampa,
                        MimeTypes.PDF.mimetype(),
                        rimborsoMissione.getFileName(),
                        StoragePath.construct(path));

            } else {
                node = getObjectRimborsoMissione(rimborsoMissione);
                node = missioniCMISService.updateStream(node.getKey(), streamStampa, MimeTypes.PDF.mimetype());
                missioniCMISService.addPropertyForExistingDocument(metadataProperties, node);
            }
            missioniCMISService.addAspect(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_RIMBORSO.value());
            return node;
        } catch (Exception e) {
            if (e.getCause() instanceof StorageException)
                throw new ComponentException("File [" + rimborsoMissione.getFileName() + "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!", e);
            throw new ComponentException("Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")", e);
        }
    }

    public StorageObject getObjectRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException {
        StorageObject node = recuperoFolderRimborsoMissione(rimborsoMissione);
        List<StorageObject> rimborso = missioniCMISService.recuperoDocumento(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_RIMBORSO.value());
        if (rimborso.size() == 0)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati al Rimborso di Missione. ID Rimborso Missione:" + rimborsoMissione.getId() + ", Anno:" + rimborsoMissione.getAnno() + ", Numero:" + rimborsoMissione.getNumero());
        else if (rimborso.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files rimborso missione aventi l'ID :" + rimborsoMissione.getId() + ", Anno:" + rimborsoMissione.getAnno() + ", Numero:" + rimborsoMissione.getNumero());
        } else {
            StorageObject nodeFile = rimborso.get(0);
            return nodeFile;
        }
    }

    public String getNodeRefFolderDettaglioRimborso(RimborsoMissioneDettagli dettagliorimborso) {
        StorageObject folder = getFolderDettaglioRimborso(dettagliorimborso);
        if (folder != null) {
            return folder.getKey();
        }
        return null;
    }

    public CMISFileAttachment uploadAttachmentDetail(RimborsoMissioneDettagli rimborsoMissioneDettagli, InputStream inputStream, String name, MimeTypes mimeTypes) {
        StorageObject doc = salvaAllegatoRimborsoMissioneDettaglioCMIS(rimborsoMissioneDettagli, inputStream, name, mimeTypes);
        if (doc != null) {
            CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
            cmisFileAttachment.setId(doc.getKey());
            cmisFileAttachment.setNomeFile(name);
            cmisFileAttachment.setIdMissione(Long.valueOf(rimborsoMissioneDettagli.getId().toString()));
            return cmisFileAttachment;
        }
        return null;
    }

    private StorageObject salvaAllegatoRimborsoMissioneDettaglioCMIS(
            RimborsoMissioneDettagli dettaglio, InputStream stream, String fileName, MimeTypes mimeTypes) {

        StorageObject folder = recuperoFolderRimborsoMissione(dettaglio.getRimborsoMissione());
        String path;
        if (folder == null) {
            path = createFolderRimborsoMissione(dettaglio.getRimborsoMissione());
        } else {
            path = folder.getPath();
        }

        path = createFolderRimborsoMissioneDettaglio(dettaglio, path);

        Map<String, Object> metadataProperties = createMetadataForFileRimborsoMissioneAllegati(securityService.getCurrentUserLogin(), fileName, RimborsoMissione.CMIS_PROPERTY_NAME_TIPODOC_SCONTRINO);
        try {
            StorageObject node = missioniCMISService.restoreSimpleDocument(
                    metadataProperties,
                    stream,
                    mimeTypes.mimetype(),
                    fileName,
                    StoragePath.construct(path));
            missioniCMISService.addAspect(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_SCONTRINI.value());
            return node;
        } catch (Exception e) {
            if (e.getCause() instanceof StorageException)
                throw new ComponentException("File [" + fileName + "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!", e);
            throw new ComponentException("Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")", e);
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


    private Map<String, Object> createMetadataForFileRimborsoMissioneAllegati(String currentLogin, String fileName, String tipoDocumento) {
        Map<String, Object> metadataProperties = new HashMap<String, Object>();
        metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), RimborsoMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
        metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename(fileName));
        metadataProperties.put(MissioniCMISService.PROPERTY_NAME, missioniCMISService.sanitizeFilename(fileName));
        metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename(fileName));
        metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
        metadataProperties.put(PROPERTY_TIPOLOGIA_DOC, OrdineMissione.CMIS_PROPERTY_NAME_DOC_ALLEGATO);
        metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_SPECIFICA, tipoDocumento);
        metadataProperties.put(PROPERTY_TIPOLOGIA_DOC_MISSIONI, tipoDocumento);

        return metadataProperties;
    }

    public Map<String, Object> createMetadataForFileRimborsoMissione(String currentLogin, CMISRimborsoMissione cmisRimborsoMissione) {
        Map<String, Object> metadataProperties = new HashMap<String, Object>();
        metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), RimborsoMissione.CMIS_PROPERTY_ATTACHMENT_DOCUMENT);
        metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, missioniCMISService.sanitizeFilename("Rimborso Missione - anno " + cmisRimborsoMissione.getAnno() + " numero " + cmisRimborsoMissione.getNumero()));
        metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, missioniCMISService.sanitizeFilename("Rimborso di Missione"));
        metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, currentLogin);
        metadataProperties.put(MissioniCMISService.PROPERTY_NAME, cmisRimborsoMissione.getNomeFile());
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
        metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_DESCRIZIONE_UO_ORDINE, cmisRimborsoMissione.getDescrizioneUoRich());
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
        metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_FLOW_ANNO_MANDATO, cmisRimborsoMissione.getAnnoMandato().equals("") ? null : Integer.valueOf(cmisRimborsoMissione.getAnnoMandato()));
        metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_FLOW_NUMERO_MANDATO, cmisRimborsoMissione.getNumeroMandato().equals("") ? null : Integer.valueOf(cmisRimborsoMissione.getNumeroMandato()));
        metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_FLOW_IMPORTO_MANDATO, cmisRimborsoMissione.getImportoMandato().equals("") ? null : new Float(cmisRimborsoMissione.getImportoMandato()));
        metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_TRATTAMENTO, cmisRimborsoMissione.getTrattamento());
        metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_FLOW_TOTALE_RIMBORSO_MISSIONE, cmisRimborsoMissione.getTotaleRimborsoMissione());
        metadataProperties.put(RimborsoMissione.CMIS_PROPERTY_FLOW_DIFFERENZE_ORDINE_RIMBORSO, cmisRimborsoMissione.getDifferenzeOrdineRimborso());

        metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_UO_ORDINE, cmisRimborsoMissione.getUoRich());
        metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_UO_SPESA, cmisRimborsoMissione.getUoSpesa());
        metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_FIRMA_SPESA, cmisRimborsoMissione.getUserNameFirmatarioSpesa());
        metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_FIRMA_UO, cmisRimborsoMissione.getUserNamePrimoFirmatario());
        metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_ORDINE, cmisRimborsoMissione.getUsernameUtenteOrdine());
        metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_USERNAME_RICHIEDENTE, cmisRimborsoMissione.getUsernameRichiedente());
        metadataProperties.put(OrdineMissione.CMIS_PROPERTY_FLOW_VALIDAZIONE_SPESA, cmisRimborsoMissione.getValidazioneSpesa().equals("si"));
        return metadataProperties;
    }


    @Transactional(readOnly = true)
    public void avviaFlusso(RimborsoMissione rimborsoMissione) throws ComponentException {
        String username = securityService.getCurrentUserLogin();
        byte[] stampa = printRimborsoMissioneService.printRimborsoMissione(rimborsoMissione, username);
        CMISRimborsoMissione cmisRimborsoMissione = create(rimborsoMissione);
        StorageObject documento = salvaStampaRimborsoMissioneSuCMIS(stampa, rimborsoMissione, cmisRimborsoMissione);
        StringBuilder nodeRefs = new StringBuilder();
        MessageForFlowRimborso messageForFlow = new MessageForFlowRimborso();
        try {

            messageForFlow.setTipologiaMissione(MessageForFlow.TIPOLOGIA_MISSIONE_RIMBORSO);
            messageForFlow.setIdMissione(cmisRimborsoMissione.getIdMissioneRimborso().toString());
            messageForFlow.setIdMissioneOrdine(cmisRimborsoMissione.getIdMissioneRimborso().toString());
            messageForFlow.setIdMissioneRimborso(cmisRimborsoMissione.getIdMissioneRimborso().toString());
            messageForFlow.setTitolo(cmisRimborsoMissione.getWfDescription());
            messageForFlow.setDescrizione(cmisRimborsoMissione.getWfDescriptionComplete());
            messageForFlow = (MessageForFlowRimborso) messageForFlowsService.impostaGruppiFirmatari(cmisRimborsoMissione, messageForFlow);

            messageForFlow.setPathFascicoloDocumenti(rimborsoMissione.getStringBasePath());

            List<StorageObject> allegati = getDocumentsAllegatiRimborsoMissione(rimborsoMissione, true);
            List<StorageObject> giustificativi = new ArrayList<StorageObject>();
            if (rimborsoMissione.getRimborsoMissioneDettagli() != null && !rimborsoMissione.getRimborsoMissioneDettagli().isEmpty()) {
                for (RimborsoMissioneDettagli dettaglio : rimborsoMissione.getRimborsoMissioneDettagli()) {
                    List<StorageObject> children = getChildrenDettaglio(dettaglio, true);
                    if (children != null && missioniCMISService.esisteAlmenoUnDocumentoValido(children)) {
                        giustificativi.addAll(children);
                    } else {
                        if (dettaglio.isGiustificativoObbligatorio() && !StringUtils.hasLength(dettaglio.getDsNoGiustificativo())) {
                            throw new AwesomeException(CodiciErrore.ERRGEN, "Per il dettaglio spesa " + dettaglio.getDsTiSpesa() + " del " + DateUtils.getDefaultDateAsString(dettaglio.getDataSpesa()) + " è obbligatorio allegare almeno un giustificativo.");
                        }
                    }
                }
            }

            messageForFlow.setNoteAutorizzazioniAggiuntive(cmisRimborsoMissione.getNoteAutorizzazioniAggiuntive());
            messageForFlow.setDescrizioneOrdine(cmisRimborsoMissione.getOggetto());
            messageForFlow.setNote(cmisRimborsoMissione.getNote());
            messageForFlow.setNoteSegreteria(cmisRimborsoMissione.getNoteSegreteria());
            messageForFlow.setBpm_sendEMailNotifications("no");
            messageForFlow.setBpm_workflowDueDate(cmisRimborsoMissione.getWfDueDate());
            messageForFlow.setBpm_workflowPriority(Utility.nvl(cmisRimborsoMissione.getPriorita(), Costanti.PRIORITA_MEDIA));
            messageForFlow.setValidazioneSpesaFlag(cmisRimborsoMissione.getValidazioneSpesa());
            messageForFlow.setUserNameUtenteMissione(cmisRimborsoMissione.getUsernameUtenteOrdine());
            messageForFlow.setUserNameRichiedente(cmisRimborsoMissione.getUsernameRichiedente());
            messageForFlow.setUserNamePrimoFirmatario(cmisRimborsoMissione.getUserNamePrimoFirmatario());
            messageForFlow.setUserNameFirmatarioSpesa(cmisRimborsoMissione.getUserNameFirmatarioSpesa());
            messageForFlow.setUserNameAmministrativo1("");
            messageForFlow.setUserNameAmministrativo2("");
            messageForFlow.setUserNameAmministrativo3("");
            messageForFlow.setUoRich(cmisRimborsoMissione.getUoRich());
            messageForFlow.setDescrizioneUoRich(cmisRimborsoMissione.getDescrizioneUoRich());
            messageForFlow.setUoSpesa(cmisRimborsoMissione.getUoSpesa());
            messageForFlow.setDescrizioneUoSpesa(cmisRimborsoMissione.getDescrizioneUoSpesa());
            messageForFlow.setUoCompetenza(cmisRimborsoMissione.getUoCompetenza());
            messageForFlow.setDescrizioneUoCompetenza(cmisRimborsoMissione.getDescrizioneUoCompetenza());
            messageForFlow.setNoleggioFlag(cmisRimborsoMissione.getNoleggioFlag());
            messageForFlow.setTaxiFlag(cmisRimborsoMissione.getTaxiFlag());
            messageForFlow.setServizioFlagOk(cmisRimborsoMissione.getAutoServizioFlag());
            messageForFlow.setPersonaSeguitoFlagOk(cmisRimborsoMissione.getPersonaSeguitoFlag());
            messageForFlow.setCapitolo(cmisRimborsoMissione.getCapitolo());
            messageForFlow.setDescrizioneCapitolo(cmisRimborsoMissione.getDescrizioneCapitolo());
            messageForFlow.setGae(cmisRimborsoMissione.getGae());
            messageForFlow.setDescrizioneGae(cmisRimborsoMissione.getDescrizioneGae());
            messageForFlow.setImpegnoAnnoResiduo(cmisRimborsoMissione.getImpegnoAnnoResiduo() == null ? "" : cmisRimborsoMissione.getImpegnoAnnoResiduo().toString());
            messageForFlow.setImpegnoAnnoCompetenza(cmisRimborsoMissione.getImpegnoAnnoCompetenza() == null ? "" : cmisRimborsoMissione.getImpegnoAnnoCompetenza().toString());
            messageForFlow.setImpegnoNumeroOk(cmisRimborsoMissione.getImpegnoNumero() == null ? "" : cmisRimborsoMissione.getImpegnoNumero().toString());
            messageForFlow.setDescrizioneImpegno(cmisRimborsoMissione.getDescrizioneImpegno());
            messageForFlow.setImportoMissione(cmisRimborsoMissione.getImportoMissione() == null ? "" : cmisRimborsoMissione.getImportoMissione().toString());
            messageForFlow.setDisponibilita(cmisRimborsoMissione.getDisponibilita() == null ? "" : cmisRimborsoMissione.getDisponibilita().toString());
            messageForFlow.setMissioneEsteraFlag(cmisRimborsoMissione.getMissioneEsteraFlag());
            messageForFlow.setDestinazione(cmisRimborsoMissione.getDestinazione());
            messageForFlow.setDataInizioMissione(cmisRimborsoMissione.getDataInizioMissione());
            messageForFlow.setDataFineMissione(cmisRimborsoMissione.getDataFineMissione());
            messageForFlow.setTrattamento(cmisRimborsoMissione.getTrattamento());
            messageForFlow.setDataInizioEstero(cmisRimborsoMissione.getDataInizioEstero() == null ? "" : cmisRimborsoMissione.getDataInizioEstero());
            messageForFlow.setDataFineEstero(cmisRimborsoMissione.getDataFineEstero() == null ? "" : cmisRimborsoMissione.getDataFineEstero());
            messageForFlow.setAnticipoRicevuto(cmisRimborsoMissione.getAnticipoRicevuto());
            messageForFlow.setAnnoMandato(cmisRimborsoMissione.getAnnoMandato());
            messageForFlow.setNumeroMandatoOk(cmisRimborsoMissione.getNumeroMandato());
            messageForFlow.setImportoMandato(cmisRimborsoMissione.getImportoMandato());
            if (cmisRimborsoMissione.getWfOrdineMissione() != null) {
                messageForFlow.setLinkToOtherWorkflows(cmisRimborsoMissione.getWfOrdineMissione());
            }
            messageForFlow.setDifferenzeOrdineRimborso(cmisRimborsoMissione.getDifferenzeOrdineRimborso());
            messageForFlow.setTotaleRimborsoMissione(cmisRimborsoMissione.getTotaleRimborsoMissione() == null ? "" : cmisRimborsoMissione.getTotaleRimborsoMissione().toString());


            MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> maps = mapper.convertValue(messageForFlow, new TypeReference<Map<String, Object>>() {
            });
            parameters.setAll(maps);

            messageForFlowsService.caricaDocumento(parameters, Costanti.TIPO_DOCUMENTO_MISSIONE, documento, rimborsoMissione.getStatoFlusso());

            messageForFlowsService.aggiungiDocumentiMultipli(allegati, parameters, Costanti.TIPO_DOCUMENTO_ALLEGATO);
            messageForFlowsService.aggiungiDocumentiMultipli(giustificativi, parameters, Costanti.TIPO_DOCUMENTO_GIUSTIFICATIVO);

            if (rimborsoMissione.isStatoNonInviatoAlFlusso()) {
                parameters.add("commento", "");
            } else {
                if ((rimborsoMissione.isStatoInviatoAlFlusso() || rimborsoMissione.isStatoRespintoFlusso()) && !StringUtils.isEmpty(rimborsoMissione.getIdFlusso())) {
                    parameters = messageForFlowsService.aggiungiParametriRiavviaFlusso(parameters, rimborsoMissione.getIdFlusso());
                } else {
                    throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Stato di invio al flusso non valido.");
                }
            }

            try {
                if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno()).getTipoMailDopoOrdine(), "N").equals("C")) {
                    rimborsoMissioneService.popolaCoda(rimborsoMissione);
                } else {
                    String idFlusso = messageForFlowsService.avviaFlusso(parameters);
                    if (StringUtils.isEmpty(rimborsoMissione.getIdFlusso())) {
                        rimborsoMissione.setIdFlusso(idFlusso);
                    }
                    rimborsoMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);

                }
            } catch (AwesomeException e) {
                throw e;
            } catch (Exception e) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
            }

        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di preparazione del flusso documentale. Errore: " + e);
        }
    }

    private void aggiungiAllegatiRimborsoMissione(RimborsoMissione rimborsoMissione, StringBuilder nodeRefs) {
        List<CMISFileAttachment> allegatiRimborsoMissione = getAttachmentsRimborsoMissione(rimborsoMissione, Long.valueOf(rimborsoMissione.getId().toString()));
        List<String> list = new ArrayList<>();
        List<String> listName = new ArrayList<>();
        if (allegatiRimborsoMissione != null && !allegatiRimborsoMissione.isEmpty()) {
            for (CMISFileAttachment cmisFileAttachment : allegatiRimborsoMissione) {
                if (nodeRefs.length() > 0) {
                    nodeRefs.append(",");
                }
                list.add(cmisFileAttachment.getNodeRef());
                listName.add(cmisFileAttachment.getNomeFile());
                nodeRefs.append(cmisFileAttachment.getNodeRef());
            }
        }
        if (rimborsoMissione.getRimborsoMissioneDettagli() != null && !rimborsoMissione.getRimborsoMissioneDettagli().isEmpty()) {
            for (RimborsoMissioneDettagli dettaglio : rimborsoMissione.getRimborsoMissioneDettagli()) {
                List<StorageObject> children = getChildrenDettaglio(dettaglio);
                if (children != null) {
                    for (StorageObject doc : children) {
                        String nodeRef = doc.getKey();
                        String nodeName = doc.getPropertyValue(StoragePropertyNames.NAME.value());
                        if (!list.contains(nodeRef) && !listName.contains(nodeName)) {
                            aggiungiDocumento(nodeRef, nodeRefs);
                            list.add(nodeRef);
                            listName.add(nodeName);
                        }
                    }
                } else {
                    if (dettaglio.isGiustificativoObbligatorio() && !StringUtils.hasLength(dettaglio.getDsNoGiustificativo())) {
                        throw new AwesomeException(CodiciErrore.ERRGEN, "Per il dettaglio spesa " + dettaglio.getDsTiSpesa() + " del " + DateUtils.getDefaultDateAsString(dettaglio.getDataSpesa()) + " è obbligatorio allegare almeno un giustificativo.");
                    }
                }
            }
        }
    }

    public void controlloEsitenzaGiustificativoDettaglio(RimborsoMissione rimborsoMissione)
            throws ComponentException {
        if (rimborsoMissione.getRimborsoMissioneDettagli() != null && !rimborsoMissione.getRimborsoMissioneDettagli().isEmpty()) {
            for (RimborsoMissioneDettagli dettaglio : rimborsoMissione.getRimborsoMissioneDettagli()) {
                List<StorageObject> children = getChildrenDettaglio(dettaglio);
                if (children == null && dettaglio.isGiustificativoObbligatorio() && StringUtils.isEmpty(dettaglio.getDsNoGiustificativo())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, "Per il dettaglio spesa " + dettaglio.getDsTiSpesa() + " del " + DateUtils.getDefaultDateAsString(dettaglio.getDataSpesa()) + " è obbligatorio allegare almeno un giustificativo.");
                }
            }
        }
    }

    private void aggiungiDocumento(StorageObject documento,
                                   StringBuilder nodeRefs) {
        if (documento != null) {
            aggiungiDocumento(documento.getKey(), nodeRefs);
        }
    }

    private void aggiungiDocumento(String nodeRef,
                                   StringBuilder nodeRefs) {
        if (nodeRef != null) {
            if (nodeRefs.length() > 0) {
                nodeRefs.append(",");
            }
            nodeRefs.append(nodeRef);
        }
    }

    public String getNodeRefRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException {
        StorageObject node = recuperoFolderRimborsoMissione(rimborsoMissione);
        List<StorageObject> rimborso = missioniCMISService.recuperoDocumento(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_RIMBORSO.value());
        if (rimborso.size() == 0)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati al Rimborso di Missione. ID Rimborso di Missione:" + rimborsoMissione.getId() + ", Anno:" + rimborsoMissione.getAnno() + ", Numero:" + rimborsoMissione.getNumero());
        else if (rimborso.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files di rimborso di missione aventi l'ID :" + rimborsoMissione.getId() + ", Anno:" + rimborsoMissione.getAnno() + ", Numero:" + rimborsoMissione.getNumero());
        } else {
            StorageObject nodeFile = rimborso.get(0);
            return nodeFile.getKey();
        }
    }

    public StorageObject recuperoFolderRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException {
        final String path = Arrays.asList(
                missioniCMISService.getBasePath().getPath(),
                Optional.ofNullable(rimborsoMissione)
                        .map(RimborsoMissione::getUoSpesa)
                        .orElse(""),
                "Rimborso Missione",
                Optional.ofNullable(rimborsoMissione)
                        .map(rimborso -> "Anno " + rimborso.getAnnoIniziale())
                        .orElse("0"),
                String.valueOf(missioniCMISService.sanitizeFilename(rimborsoMissione.constructCMISNomeFile()))
        ).stream().collect(
                Collectors.joining("/")
        );

        try {
            return Optional.ofNullable(missioniCMISService.getStorageObjectByPath(path))
                    .filter(StorageObject.class::isInstance)
                    .map(StorageObject.class::cast)
                    .orElse(null);
        } catch (StorageException e) {
            String pathFolder = createFolderRimborsoMissione(rimborsoMissione);
            return missioniCMISService.getStorageObjectByPath(pathFolder);
        }
    }

    public void annullaFlusso(RimborsoMissione rimborsoMissione) throws AwesomeException {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        if (rimborsoMissione.isStatoRespintoFlusso() && !StringUtils.isEmpty(rimborsoMissione.getIdFlusso())) {
            messageForFlowsService.annullaFlusso(parameters, rimborsoMissione.getIdFlusso());
        } else {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Stato di invio al flusso non valido. Id Rimborso " + rimborsoMissione.getId());
        }
        rimborsoMissione.setStatoFlusso(Costanti.STATO_ANNULLATO);
    }

    private MessageForFlowRimborso createJsonForAbortFlowOrdineMissione() {
        MessageForFlowRimborso rimborso = new MessageForFlowRimborso();
        return rimborso;
    }

    public InputStream getStreamRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException {
        String id = getNodeRefRimborsoMissione(rimborsoMissione);
        if (id != null) {
            return missioniCMISService.recuperoStreamFileFromObjectID(id);
        }
        return null;
    }

    public StoragePath buildFolderRimborsoMissione(RimborsoMissione rimborsoMissione) {
        StorageObject folder = recuperoFolderRimborsoMissione(rimborsoMissione);
        String path;
        if (folder == null) {
            path = createFolderRimborsoMissione(rimborsoMissione);
        } else {
            path = folder.getPath();
        }
        return StoragePath.construct(path);
    }

    private StoragePath searchFolderRimborsoMissione(RimborsoMissione rimborsoMissione) {
        StorageObject folder = recuperoFolderRimborsoMissione(rimborsoMissione);
        String path;
        if (folder == null) {
            return null;
        } else {
            path = folder.getPath();
        }
        return StoragePath.construct(path);
    }

    public CMISFileAttachment uploadAttachmentRimborsoMissione(RimborsoMissione rimborsoMissione, Long idRimborsoMissione, InputStream inputStream, String name, MimeTypes mimeTypes) {
        StorageObject doc = salvaAllegatoRimborsoMissioneCMIS(rimborsoMissione, inputStream, name, mimeTypes);
        if (doc != null) {
            CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
            cmisFileAttachment.setId(doc.getKey());
            cmisFileAttachment.setNomeFile(name);
            cmisFileAttachment.setIdMissione(idRimborsoMissione);
            return cmisFileAttachment;
        }
        return null;
    }

    public CMISFileAttachment uploadAttachmentAnnullamentoRimborsoMissione(RimborsoMissione rimborsoMissione, Long idAnnullamentoRimborsoMissione, InputStream inputStream, String name, MimeTypes mimeTypes) {
        StorageObject doc = salvaAllegatoAnnullamentoRimborsoMissioneCMIS(rimborsoMissione, inputStream, name, mimeTypes);
        if (doc != null) {
            CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
            cmisFileAttachment.setId(doc.getKey());
            cmisFileAttachment.setNomeFile(name);
            cmisFileAttachment.setIdMissione(idAnnullamentoRimborsoMissione);
            return cmisFileAttachment;
        }
        return null;
    }

    private StorageObject salvaAllegatoRimborsoMissioneCMIS(
            RimborsoMissione rimborsoMissione, InputStream stream, String fileName, MimeTypes mimeTypes) {

        StoragePath cmisPath = buildFolderRimborsoMissione(rimborsoMissione);

        Map<String, Object> metadataProperties = createMetadataForFileRimborsoMissioneAllegati(securityService.getCurrentUserLogin(), fileName, RimborsoMissione.CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO);
        try {
            StorageObject node = missioniCMISService.restoreSimpleDocument(
                    metadataProperties,
                    stream,
                    mimeTypes.mimetype(),
                    fileName,
                    cmisPath);
            missioniCMISService.addAspect(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_ALLEGATI.value());
            return node;
        } catch (Exception e) {
            if (e.getCause() instanceof StorageException)
                throw new ComponentException("File [" + fileName + "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!", e);
            throw new ComponentException("Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")", e);
        }
    }

    private StorageObject salvaAllegatoAnnullamentoRimborsoMissioneCMIS(
            RimborsoMissione rimborsoMissione, InputStream stream, String fileName, MimeTypes mimeTypes) {

        StoragePath cmisPath = searchFolderRimborsoMissione(rimborsoMissione);
        if (cmisPath == null) {
            throw new ComponentException("Errore nel salvataggio del file sul Documentale. Cartella del rimborso non trovata");
        }

        Map<String, Object> metadataProperties = createMetadataForFileRimborsoMissioneAllegati(securityService.getCurrentUserLogin(), fileName, RimborsoMissione.CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO_ANNULLAMENTO);
        try {
            StorageObject node = missioniCMISService.restoreSimpleDocument(
                    metadataProperties,
                    stream,
                    mimeTypes.mimetype(),
                    fileName,
                    cmisPath);
            missioniCMISService.addAspect(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_ALLEGATI_ANNULLAMENTO.value());
            return node;
        } catch (Exception e) {
            if (e.getCause() instanceof StorageException)
                throw new ComponentException("File [" + fileName + "] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!", e);
            throw new ComponentException("Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")", e);
        }
    }

    public List<CMISFileAttachment> getAttachmentsRimborsoMissione(RimborsoMissione rimborsoMissione, Long idRimborsoMissione) {
        List<StorageObject> documents = getDocumentsAllegatiRimborsoMissione(rimborsoMissione);
        if (documents != null) {
            List<CMISFileAttachment> lista = new ArrayList<CMISFileAttachment>();
            for (StorageObject object : documents) {
                CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
                cmisFileAttachment.setNomeFile(object.getPropertyValue(StoragePropertyNames.NAME.value()));
                cmisFileAttachment.setId(object.getKey());
                cmisFileAttachment.setNodeRef(object.getPropertyValue(MissioniCMISService.ALFCMIS_NODEREF));
                cmisFileAttachment.setIdMissione(idRimborsoMissione);
                lista.add(cmisFileAttachment);
            }
            return lista;
        }
        return Collections.emptyList();
    }

    public List<CMISFileAttachment> getAttachmentsAnnullamentoRimborsoMissione(RimborsoMissione rimborsoMissione, Long idAnnullamentoRimborsoMissione) {
        List<StorageObject> documents = getDocumentsAllegatiAnnullamentoRimborsoMissione(rimborsoMissione);
        if (documents != null) {
            List<CMISFileAttachment> lista = new ArrayList<CMISFileAttachment>();
            for (StorageObject object : documents) {
                CMISFileAttachment cmisFileAttachment = new CMISFileAttachment();
                cmisFileAttachment.setNomeFile(object.getPropertyValue(StoragePropertyNames.NAME.value()));
                cmisFileAttachment.setId(object.getKey());
                cmisFileAttachment.setNodeRef(object.getPropertyValue(MissioniCMISService.ALFCMIS_NODEREF));
                cmisFileAttachment.setIdMissione(idAnnullamentoRimborsoMissione);
                lista.add(cmisFileAttachment);
            }
            return lista;
        }
        return Collections.emptyList();
    }

    public List<StorageObject> getDocumentsAllegatiRimborsoMissione(RimborsoMissione rimborsoMissione) {
        return getDocumentsAllegatiRimborsoMissione(rimborsoMissione, false);
    }

    public List<StorageObject> getDocumentsAllegatiRimborsoMissione(RimborsoMissione rimborsoMissione, Boolean recuperoFileEliminati) {
        StorageObject node = recuperoFolderRimborsoMissione(rimborsoMissione);
        return missioniCMISService.recuperoDocumento(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_ALLEGATI.value(), recuperoFileEliminati);
    }

    public List<StorageObject> getDocumentsAllegatiAnnullamentoRimborsoMissione(RimborsoMissione rimborsoMissione) {
        StorageObject node = recuperoFolderRimborsoMissione(rimborsoMissione);
        return missioniCMISService.recuperoDocumento(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_ALLEGATI_ANNULLAMENTO.value());
    }

    public Map<String, byte[]> getFileRimborsoMissione(RimborsoMissione rimborsoMissione) {
        String fileName = null;
        byte[] printRimborsoMissione = null;
        StorageObject storage = getStorageRimborsoMissione(rimborsoMissione);
        if (storage != null) {
            fileName = storage.getPropertyValue(StoragePropertyNames.NAME.value());
            InputStream is = missioniCMISService.recuperoStreamFileFromObjectID(storage.getKey());
            if (is != null) {
                try {
                    printRimborsoMissione = IOUtils.toByteArray(is);
                    is.close();
                } catch (IOException e) {
                    throw new ComponentException("Errore nella conversione dello stream in byte del file (" + Utility.getMessageException(e) + ")", e);
                }
            } else {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero del contenuto del file di annullamento sul documentale");
            }
            Map<String, byte[]> map = new HashMap<String, byte[]>();
            map.put(fileName, printRimborsoMissione);
            return map;
        }
        throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero del contenuto del file di annullamento sul documentale");
    }

    public StorageObject getStorageRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException {
        StorageObject node = recuperoFolderRimborsoMissione(rimborsoMissione);
        List<StorageObject> objs = missioniCMISService.recuperoDocumento(node, CMISRimborsoMissioneAspect.RIMBORSO_MISSIONE_ATTACHMENT_RIMBORSO.value());

        if (objs.size() == 0) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non esistono documenti collegati di annullamento dell'Ordine di Missione. ID Ordine di Missione:" + rimborsoMissione.getId() + ", Anno:" + rimborsoMissione.getAnno() + ", Numero:" + rimborsoMissione.getNumero());
        } else if (objs.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore di sistema, esistono sul documentale piu' files di annullamento dell'ordine di missione aventi l'ID :" + rimborsoMissione.getId() + ", Anno:" + rimborsoMissione.getAnno() + ", Numero:" + rimborsoMissione.getNumero());
        } else {
            return objs.get(0);
        }
    }

    public List<StorageObject> getDocumentsRimborsoMissione(RimborsoMissione missione) throws ComponentException {
        StorageObject node = recuperoFolderRimborsoMissione(missione);
        return Optional.ofNullable(node)
                .map(storageObject -> missioniCMISService.getChildren(storageObject.getKey(), -1))
                .orElse(null);
    }

    public InputStream getResource(StorageObject so) {
        return missioniCMISService.getResource(so.getKey());
    }
}
