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

package it.cnr.si.missioni.service;

import it.cnr.si.missioni.amq.domain.Missione;
import it.cnr.si.missioni.amq.domain.TypeMissione;
import it.cnr.si.missioni.amq.domain.TypeTipoMissione;
import it.cnr.si.missioni.amq.service.RabbitMQService;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.*;
import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.missioni.repository.*;
import it.cnr.si.missioni.repository.specification.RimborsoSpecification;
import it.cnr.si.missioni.repository.specification.SecuritySpecification;
import it.cnr.si.missioni.repository.specification.SpecificationBuilder;
import it.cnr.si.missioni.service.security.SecurityService;
import it.cnr.si.missioni.util.*;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.json.object.*;
import it.cnr.si.missioni.util.proxy.json.service.*;
import it.cnr.si.missioni.web.filter.MissioneFilter;
import it.cnr.si.missioni.web.filter.MissioneFilterMapper;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import jakarta.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static it.cnr.si.missioni.util.SecurityUtils.getCurrentUser;

/**
 * Service class for managing users.
 */
@Service
public class OrdineMissioneService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    SecurityService securityService;
    @Autowired
    private OrdineMissioneRepository ordineMissioneRepository;
    @Autowired
    OrdineMissioneAutoPropriaRepository OrdineMissioneAutoPropriaRepository;
    @Autowired
    OrdineMissioneTaxiRepository ordineMissioneTaxiRepository;
    @Autowired
    OrdineMissioneAutoNoleggioRepository ordineMissioneAutoNoleggioRepository;
    @Autowired
    private Environment env;
    @Autowired
    private DatiSedeService datiSedeService;
    @Autowired
    private TerzoPerCompensoService terzoPerCompensoService;
    @Autowired
    private DatiIstitutoService datiIstitutoService;
    @Autowired
    @Lazy
    private PrintOrdineMissioneService printOrdineMissioneService;
    @Autowired
    @Lazy
    private CMISOrdineMissioneService cmisOrdineMissioneService;
    @Autowired
    private UoService uoService;
    @Autowired
    private ParametriService parametriService;

    @Autowired
    private UnitaOrganizzativaService unitaOrganizzativaService;

    @Autowired
    private CdrService cdrService;

    @Autowired
    private GaeService gaeService;

    @Autowired
    private ProgettoService progettoService;

    @Autowired
    private ImpegnoService impegnoService;

    @Autowired
    private ImpegnoGaeService impegnoGaeService;

    @Autowired
    @Lazy
    private OrdineMissioneAnticipoService ordineMissioneAnticipoService;

    @Autowired
    @Lazy
    private OrdineMissioneAutoPropriaService ordineMissioneAutoPropriaService;
    @Autowired
    @Lazy
    private OrdineMissioneTaxiService ordineMissioneTaxiService;
    @Autowired
    @Lazy
    private OrdineMissioneAutoNoleggioService ordineMissioneAutoNoleggioService;
    @Autowired
    private ConfigService configService;

    @Autowired(required = false)
    private MailService mailService;

    @Autowired(required = false)
    private RabbitMQService rabbitMQService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MissioniCMISService missioniCMISService;

    @Autowired
    private MissioneRespintaService missioneRespintaService;

    @Autowired
    @Lazy
    private AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;

    @Autowired
    @Lazy
    private OrdineMissioneDettagliService ordineMissioneDettagliService;

    @Value("${spring.mail.messages.invioResponsabileGruppo.oggetto}")
    private String subjectSendToManagerOrdine;

    @Value("${spring.mail.messages.invioOrdinePerValidazioneDatiFinanziari.oggetto}")
    private String subjectSendToAdministrative;

    @Value("${spring.mail.messages.ritornoOrdineMissioneMittente.oggetto}")
    private String subjectReturnToSenderOrdine;

    @Value("${spring.mail.messages.approvazioneAnticipo.oggetto}")
    private String subjectAnticipo;

    @Value("${spring.mail.messages.approvazioneOrdineMissione.oggetto}")
    private String approvazioneOrdineMissione;

    @Value("${spring.mail.messages.approvazioneAnnullamentoOrdineMissione.oggetto}")
    private String approvazioneAnnullamentoOrdineMissione;

    @Value("${spring.mail.messages.erroreLetturaFlussoOrdine.oggetto}")
    private String subjectErrorFlowsOrdine;

    @Value("${spring.mail.messages.erroreLetturaFlussoOrdine.testo}")
    private String textErrorFlowsOrdine;

    @Value("${spring.mail.messages.erroreComunicazioneRimborsoSigla.oggetto}")
    private String subjectErrorComunicazioneRimborso;

    @Value("${spring.mail.messages.erroreComunicazioneRimborsoSigla.testo}")
    private String textErrorComunicazioneRimborso;

    @Value("${spring.mail.messages.erroreGenerico.oggetto}")
    private String subjectGenericError;


    public Boolean isUserEnabledToViewMissione(OrdineMissione ordineMissione) {
        return ordineMissione.getUid().equals(securityService.getCurrentUserLogin()) || (ordineMissione.getResponsabileGruppo() != null && ordineMissione.getResponsabileGruppo().equals(securityService.getCurrentUserLogin()))
                || accountService.isUserEnableToWorkUo(ordineMissione.getUoRich()) || accountService.isUserEnableToWorkUo(ordineMissione.getUoSpesa());
    }


    //TODO capire gestione degli stati della missione per la visualizzazione degli stati delle autorizz. agg. (SI/NO)
    public OrdineMissione getOrdineMissione(Long idMissione, Boolean retrieveDetail, Boolean retrieveDataFromFlows)
            throws AwesomeException {
        MissioneFilter filter = new MissioneFilter();
        filter.setDaId(idMissione);
        filter.setaId(idMissione);
        OrdineMissione ordineMissione = null;
        List<OrdineMissione> listaOrdiniMissione = getOrdiniMissione(filter, false, true);
        if (listaOrdiniMissione != null && !listaOrdiniMissione.isEmpty()) {
            ordineMissione = listaOrdiniMissione.get(0);
            if (retrieveDetail) {
                retrieveDetails(ordineMissione);
            }
            if (retrieveDataFromFlows) {
                OrdineMissioneAutoPropria autoPropria = getAutoPropria(ordineMissione);
                if (autoPropria != null && autoPropria.getOrdineMissione().checkStatiFlussoTrue()) {
                    ordineMissione.setUtilizzoAutoPropria("S");
                } else {
                    ordineMissione.setUtilizzoAutoPropria("N");
                }
                OrdineMissioneAnticipo anticipo = getAnticipo(ordineMissione);
                if (anticipo != null && anticipo.getOrdineMissione().checkStatiFlussoTrue()) {
                    ordineMissione.setRichiestaAnticipo("S");
                } else {
                    ordineMissione.setRichiestaAnticipo("N");
                }
                OrdineMissioneTaxi taxi = getTaxi(ordineMissione);
                if (taxi != null && taxi.getOrdineMissione().checkStatiFlussoTrue()) {
                    ordineMissione.setUtilizzoTaxi("S");
                } else {
                    ordineMissione.setUtilizzoTaxi("N");
                }
                OrdineMissioneAutoNoleggio autoNoleggio = getAutoNoleggio(ordineMissione);
                if (autoNoleggio != null && autoNoleggio.getOrdineMissione().checkStatiFlussoTrue()) {
                    ordineMissione.setUtilizzoAutoNoleggio("S");
                } else {
                    ordineMissione.setUtilizzoAutoNoleggio("N");
                }
            }
        }
        return ordineMissione;
    }


    public OrdineMissione getOrdineMissione(Long idMissione, Boolean retrieveDetail) throws AwesomeException {
        return getOrdineMissione(idMissione, retrieveDetail, true);
    }


    public Map<String, byte[]> printOrdineMissione(Long idMissione) throws AwesomeException {
        String username = securityService.getCurrentUserLogin();
        OrdineMissione ordineMissione = getOrdineMissione(idMissione, true);

        if (ordineMissione == null) {
            return Collections.emptyMap();
        }

        Map<String, byte[]> map = new HashMap<>();
        retrieveDetails(ordineMissione);

        byte[] fileContent;
        String fileName;

        boolean stampaDaCMIS = (ordineMissione.isStatoInviatoAlFlusso() && !ordineMissione.isMissioneInserita()
                && !ordineMissione.isMissioneDaValidare()) || ordineMissione.isStatoFlussoApprovato();

        if (stampaDaCMIS) {
            StorageObject storage;
            try {
                storage = cmisOrdineMissioneService.getStorageObjectOrdineMissione(ordineMissione);
            } catch (AwesomeException e) {
                throw new AwesomeException(
                        "Errore nel recupero del contenuto del file sul documentale (" + Utility.getMessageException(e) + ")",
                        e
                );
            }

            if (storage == null) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Errore nel recupero del contenuto del file sul documentale");
            }

            fileName = storage.getPropertyValue(StoragePropertyNames.NAME.value());

            try (InputStream is = missioniCMISService.getResource(storage)) {
                if (is == null) {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            "Errore nel recupero dello stream del file sul documentale");
                }
                fileContent = IOUtils.toByteArray(is);
            } catch (IOException e) {
                throw new AwesomeException(
                        "Errore nella conversione dello stream in byte del file (" + Utility.getMessageException(e) + ")",
                        e
                );
            } catch (Exception e) {
                throw new AwesomeException(
                        "Errore nel recupero dello stream del file sul documentale (" + Utility.getMessageException(e) + ")",
                        e
                );
            }

        } else {
            // stampa locale
            fileName = "OrdineMissione" + idMissione + ".pdf";
            fileContent = printOrdineMissioneService.printOrdineMissione(ordineMissione, username);

            if (ordineMissione.isMissioneInserita()) {
                cmisOrdineMissioneService.salvaStampaOrdineMissioneSuCMIS(fileContent, ordineMissione);
            }
        }

        map.put(fileName, fileContent);
        return map;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void retrieveDetails(OrdineMissione ordineMissione) throws NumberFormatException, AwesomeException {
        List<OrdineMissioneDettagli> list = ordineMissioneDettagliService.getOrdineMissioneDettagli(Long.valueOf(ordineMissione.getId().toString()));
        ordineMissione.setOrdineMissioneDettagli(list);
    }

    private boolean isDevProfile() {
        return env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT);
    }

    public String jsonForPrintOrdineMissione(Long idMissione) throws AwesomeException {
        OrdineMissione ordineMissione = getOrdineMissione(idMissione, true);
        return printOrdineMissioneService.createJsonPrintOrdineMissione(ordineMissione, securityService.getCurrentUserLogin());
    }

    public List<OrdineMissione> getOrdiniMissioneForValidateFlows(MissioneFilter filter,
                                                                  Boolean isServiceRest) throws AwesomeException {
        List<OrdineMissione> lista = getOrdiniMissione(filter, isServiceRest, true);
        if (lista != null) {
            List<OrdineMissione> listaNew = new ArrayList<OrdineMissione>();
            for (OrdineMissione ordineMissione : lista) {
                if (ordineMissione.isStatoInviatoAlFlusso() && !ordineMissione.isMissioneDaValidare()) {
                    ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_DA_AUTORIZZARE_PER_HOME);
                    listaNew.add(ordineMissione);
                } else {
                    if (ordineMissione.isMissioneDaValidare() && ordineMissione.isMissioneConfermata()) {
                        ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_DA_VALIDARE_PER_HOME);
                        listaNew.add(ordineMissione);
                    } else if (ordineMissione.isMissioneInserita()) {
                        ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_DA_CONFERMARE_PER_HOME);
                        listaNew.add(ordineMissione);
                    } else if (ordineMissione.isMissioneInviataResponsabile()) {
                        ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_PER_RESPONSABILE_GRUPPO_PER_HOME);
                        listaNew.add(ordineMissione);
                    }
                }
            }
            return listaNew;
        }
        return lista;
    }

    public void aggiornaOrdineMissioneRespinto(FlowResult result,
                                               OrdineMissione ordineMissioneDaAggiornare) throws AwesomeException {
        aggiornaValidazione(ordineMissioneDaAggiornare);
        ordineMissioneDaAggiornare.setCommentoFlusso(result.getCommento() == null ? null : (result.getCommento().length() > 1000 ? result.getCommento().substring(0, 1000) : result.getCommento()));
        ordineMissioneDaAggiornare.setStatoFlusso(FlowResult.STATO_FLUSSO_SCRIVANIA_MISSIONI.get(result.getStato()));
        ordineMissioneDaAggiornare.setStato(Costanti.STATO_INSERITO);
        ordineMissioneDaAggiornare.setDataInvioAmministrativo(null);
        ordineMissioneDaAggiornare.setDataInvioFirma(null);
        ordineMissioneDaAggiornare.setDataInvioRespGruppo(null);
        ordineMissioneDaAggiornare.setBypassAmministrativo(null);
        ordineMissioneDaAggiornare.setBypassRespGruppo(null);
        OrdineMissioneAnticipo anticipo = getAnticipo(ordineMissioneDaAggiornare);
        if (anticipo != null) {
            anticipo.setStato(Costanti.STATO_INSERITO);
            ordineMissioneAnticipoService.updateAnticipo(anticipo, false);
        }
        updateOrdineMissione(ordineMissioneDaAggiornare, true);

        missioneRespintaService.inserisciMissioneRespinta(result);
    }

    public OrdineMissioneAnticipo getAnticipo(OrdineMissione ordineMissioneDaAggiornare) throws AwesomeException {
        return ordineMissioneAnticipoService.getAnticipo(ordineMissioneDaAggiornare, false);
    }

    public void aggiornaOrdineMissioneAnnullato(OrdineMissione ordineMissioneDaAggiornare) {
        ordineMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_ANNULLATO);
        ordineMissioneDaAggiornare.setStato(Costanti.STATO_ANNULLATO);
        updateOrdineMissione(ordineMissioneDaAggiornare, true);
    }

    private String getTextErrorOrdine(OrdineMissione ordineMissione, FlowResult flow, String error) {
        return textErrorFlowsOrdine + getTextErrorOrdineMissione(ordineMissione, flow, error);
    }

    private String getTextErrorOrdineMissione(OrdineMissione ordineMissione, FlowResult flow, String error) {
        return " con id " + ordineMissione.getId() + " " + ordineMissione.getAnno() + "-" + ordineMissione.getNumero() + " di " + ordineMissione.getDatoreLavoroRich() + " collegato al flusso " + flow.getProcessInstanceId() + " con esito " + flow.getStato() + " è andata in errore per il seguente motivo: " + error;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void aggiornaOrdineMissione(OrdineMissione ordineMissioneDaAggiornare, FlowResult flowResult) {
        try {
            if (ordineMissioneDaAggiornare == null) {
                return;
            }

            if (ordineMissioneDaAggiornare.isStatoInviatoAlFlusso()
                    && ordineMissioneDaAggiornare.isMissioneConfermata()
                    && !ordineMissioneDaAggiornare.isMissioneDaValidare()) {

                switch (flowResult.getStato()) {
                    case FlowResult.ESITO_FLUSSO_FIRMATO:
                        aggiornaOrdineMissioneFirmato(ordineMissioneDaAggiornare);
                        break;

                    case FlowResult.ESITO_FLUSSO_FIRMA_UO:
                        aggiornaOrdineMissionePrimaFirma(ordineMissioneDaAggiornare);
                        break;

                    case FlowResult.ESITO_FLUSSO_RESPINTO_UO:
                    case FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA:
                        aggiornaOrdineMissioneRespinto(flowResult, ordineMissioneDaAggiornare);
                        break;
                }

            } else {
                erroreOrdineMissione(ordineMissioneDaAggiornare, flowResult);
            }

        } catch (Exception e) {
            String errore = Utility.getMessageException(e);

            if (flowResult != null
                    && FlowResult.ESITO_FLUSSO_FIRMATO.equals(flowResult.getStato())
                    && ordineMissioneDaAggiornare != null
                    && ordineMissioneDaAggiornare.getId() != null) {

                gestisciErroreOrdineFirmato(ordineMissioneDaAggiornare, flowResult, errore);
                return;
            }

            throw new AwesomeException(CodiciErrore.ERRGEN, errore);
        }
    }

    private void gestisciErroreOrdineFirmato(
            OrdineMissione ordine,
            FlowResult flowResult,
            String errore
    ) {
        String msgUtente = "Firma HappySign acquisita, ma aggiornamento ordine non completato per incongruenze o dati mancanti.";

        String msgTecnico = "Firma HappySign acquisita, ma aggiornamento ordine non completato: <b>"
                + errore + "</b>";

        OrdineMissione o = ordineMissioneRepository
                .findById((Long) ordine.getId())
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Ordine di missione non trovato per id " + ordine.getId()
                ));

        o.setCommentoFlusso(msgUtente.length() > 1000 ? msgUtente.substring(0, 1000) : msgUtente);
        o.setStatoFlusso(Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO);
        o.setStato(Costanti.STATO_INSERITO);
        o.setDataInvioFirma(null);
        o.setDataInvioAmministrativo(null);
        o.setDataInvioRespGruppo(null);
        o.setBypassAmministrativo(null);
        o.setBypassRespGruppo(null);
        o.setToBeUpdated();

        ordineMissioneRepository.save(o);

        if (mailService != null) {
            try {
                // email tecnica
                String testoTecnico = getTextErrorOrdine(o, flowResult, msgTecnico);
                mailService.sendEmailError(subjectErrorFlowsOrdine, testoTecnico, false, true);
            } catch (Exception e) {
                log.error("Errore invio email tecnica ordine missione firmato", e);
            }

            try {
                // email utente
                Account acc = accountService.loadAccountFromUsername(o.getUid());
                if (acc != null && StringUtils.hasLength(acc.getEmail_comunicazioni())) {
                    String testoUtente = getTextMailErroreFirmaOrdine(o);
                    mailService.sendEmail(
                            "Firma acquisita – verifica ordine missione " + o.getAnno() + "-" + o.getNumero(),
                            testoUtente,
                            false,
                            true,
                            acc.getEmail_comunicazioni()
                    );
                }

            } catch (Exception e) {
                log.error("Errore invio email utente ordine missione firmato", e);
            }
        }
    }

    private String getTextMailErroreFirmaOrdine(OrdineMissione o) {
        return "<p>Gentile " + getNominativo(o.getUid()) + ",</p>" +
                "<p>La firma digitale dell'ordine <b>" + o.getAnno() + "-" + o.getNumero() +
                "</b> (missione a <b>" + o.getDestinazione() + "</b>, dal " +
                DateUtils.getDefaultDateAsString(o.getDataInizioMissione()) + " al " +
                DateUtils.getDefaultDateAsString(o.getDataFineMissione()) +
                ") è stata acquisita con successo.</p>" +
                "<p>Tuttavia, per incongruenze o dati mancanti, la procedura non è stata completata. " +
                "Il documento è tornato in stato INSERITO. L'assistenza è stata avvisata.</p>";
    }


    private void erroreOrdineMissione(OrdineMissione ordineMissioneDaAggiornare, FlowResult flowResult) {
        String errore = "Esito flusso non corrispondente con lo stato dell'ordine.";
        String testoErrore = getTextErrorOrdine(ordineMissioneDaAggiornare, flowResult, errore);
        throw new AwesomeException(CodiciErrore.ERRGEN, errore + " " + testoErrore);
    }

    private void aggiornaOrdineMissioneFirmato(OrdineMissione ordineMissioneDaAggiornare) {
        retrieveDetails(ordineMissioneDaAggiornare);
        ordineMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
        ordineMissioneDaAggiornare.setStato(Costanti.STATO_DEFINITIVO);
        updateOrdineMissione(ordineMissioneDaAggiornare, true);    // ← PRIMA: salva (può lanciare eccezione)
        gestioneEmailDopoApprovazione(ordineMissioneDaAggiornare); // ← POI: email (solo se salvataggio ok)
        popolaCoda(ordineMissioneDaAggiornare);
    }

    private void aggiornaOrdineMissionePrimaFirma(OrdineMissione ordineMissioneDaAggiornare) {
        ordineMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_FIRMATO_PRIMA_FIRMA_FLUSSO);
        updateOrdineMissione(ordineMissioneDaAggiornare, true);
    }

    public void gestioneEmailDopoApprovazione(OrdineMissione ordineMissioneDaAggiornare) {
        gestioneEmailDopoApprovazione(ordineMissioneDaAggiornare, false);
    }

    public void gestioneEmailDopoApprovazione(OrdineMissione ordineMissioneDaAggiornare, Boolean isAnnullamento) {
        DatiSede datiSede = null;
        Account account = accountService.loadAccountFromUsername(ordineMissioneDaAggiornare.getUid());
        String emailRich = ordineMissioneDaAggiornare.getUid();

//        //todo x i test setto l'utente che crea l'ordine
//        String emailRich = ordineMissioneDaAggiornare.getUidInsert();

        boolean missioneConAnticipo = false;
        OrdineMissioneAnticipo anticipo = getAnticipo(ordineMissioneDaAggiornare);
        if (anticipo != null) {
            anticipo.setStato(Costanti.STATO_DEFINITIVO);
            ordineMissioneAnticipoService.updateAnticipo(anticipo, false);
            missioneConAnticipo = true;
        }
//            DatiIstituto dati = datiIstitutoService.getDatiIstituto(ordineMissioneDaAggiornare.getUoSpesa(),
//                    ordineMissioneDaAggiornare.getAnno());
//            if (dati != null && dati.getMailNotifiche() != null) {
//                if (!dati.getMailNotifiche().equals("N")) {
//                    mailService.sendEmail(subjectAnticipo, getTextMailAnticipo(ordineMissioneDaAggiornare, anticipo), false,
//                            true, dati.getMailNotifiche());
//                }
//            } else {
//                Account account = accountService.loadAccountFromUsername(ordineMissioneDaAggiornare.getUid());
//                UsersSpecial richiedente = accountService.getUoForUsersSpecial(account.getUid());
//                List<UsersSpecial> lista = accountService
//                        .getUserSpecialForUoPerValidazione(ordineMissioneDaAggiornare.getUoSpesa());
//                aggiuntaRichMailList(lista,richiedente);
//                if (lista != null && lista.size() > 0) {
//                    mailService.sendEmail(subjectAnticipo, getTextMailAnticipo(ordineMissioneDaAggiornare, anticipo),
//                            false, true, mailService.prepareTo(lista));
//                }
//            }
//        }


        if (account != null && account.getCodice_sede() != null) {
            datiSede = datiSedeService.getDatiSede(account.getCodice_sede(), LocalDate.now());
        }

        DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(ordineMissioneDaAggiornare.getUoRich(),
                ordineMissioneDaAggiornare.getAnno());
        DatiIstituto datiIstitutoSpesa = null;
        if (!ordineMissioneDaAggiornare.getUoRich().equals(ordineMissioneDaAggiornare.getUoSpesa())) {
            datiIstitutoSpesa = datiIstitutoService.getDatiIstituto(ordineMissioneDaAggiornare.getUoSpesa(),
                    ordineMissioneDaAggiornare.getAnno());
        }

        Set<UsersSpecial> utentiUnici = new HashSet<>();

        // Aggiungi utenti della UO richiedente
        if (Utility.nvl(datiIstituto.getTipoMailDopoOrdine(), "N").equals("U")) {
            utentiUnici.addAll(accountService.getUserSpecialForUo(ordineMissioneDaAggiornare.getUoRich(), false));
        }
        if (Utility.nvl(datiIstituto.getTipoMailDopoOrdine(), "N").equals("V")) {
            utentiUnici.addAll(accountService.getUserSpecialForUo(ordineMissioneDaAggiornare.getUoRich(), true));
        }

        // Aggiungi il richiedente direttamente
        if (emailRich != null) {
            UsersSpecial richiedente = accountService.findOrCreateUserSpecial(emailRich);
            if (richiedente != null) {
                utentiUnici.add(richiedente);
            }
        }

        // Aggiungi utenti della UO spesa
        if (datiIstitutoSpesa != null) {
            if (Utility.nvl(datiIstitutoSpesa.getTipoMailDopoOrdine(), "N").equals("V")) {
                utentiUnici.addAll(accountService.getUserSpecialForUo(ordineMissioneDaAggiornare.getUoSpesa(), true));
            }
            if (Utility.nvl(datiIstitutoSpesa.getTipoMailDopoOrdine(), "N").equals("U")) {
                utentiUnici.addAll(accountService.getUserSpecialForUo(ordineMissioneDaAggiornare.getUoSpesa(), false));
            }
        }


        String oggetto = isAnnullamento ? approvazioneAnnullamentoOrdineMissione : approvazioneOrdineMissione;
        String testo = isAnnullamento ? getTextMailApprovazioneAnnullamentoOrdine(ordineMissioneDaAggiornare)
                : getTextMailApprovazioneOrdine(ordineMissioneDaAggiornare, missioneConAnticipo);

        // Invia una singola email a tutti i destinatari
        if (utentiUnici.size() > 0) {
            List<UsersSpecial> listaUtenti = new ArrayList<>(utentiUnici);
            mailService.sendEmail(oggetto, testo, false, true, mailService.prepareTo(listaUtenti));
        }
        if (Utility.nvl(datiIstituto.getTipoMailDopoOrdine(), "N").equals("E")
                && !StringUtils.isEmpty(datiIstituto.getMailNotifiche()) && !datiIstituto.getMailNotifiche().equals("N")) {
            mailService.sendEmail(oggetto, testo, false, true, datiIstituto.getMailNotifiche());
        }
        if (datiSede != null && Utility.nvl(datiSede.getTipoMailDopoOrdine(), "N").equals("A")
                && !StringUtils.isEmpty(datiSede.getMailDopoOrdine())) {
            mailService.sendEmail(oggetto, testo, false, true, datiSede.getMailDopoOrdine());
        }
        if (Utility.nvl(datiIstituto.getTipoMailDopoOrdine(), "N").equals("A")
                && !StringUtils.isEmpty(datiIstituto.getMailDopoOrdine())) {
            mailService.sendEmail(oggetto, testo, false, true, datiIstituto.getMailDopoOrdine());
        }
        if (datiIstitutoSpesa != null) {
            if (Utility.nvl(datiIstitutoSpesa.getTipoMailDopoOrdine(), "N").equals("E")
                    && !StringUtils.isEmpty(datiIstitutoSpesa.getMailNotifiche()) && !datiIstitutoSpesa.getMailNotifiche().equals("N")) {
                mailService.sendEmail(oggetto, testo, false, true, datiIstitutoSpesa.getMailNotifiche());
            }
            if (Utility.nvl(datiIstitutoSpesa.getTipoMailDopoOrdine(), "N").equals("A")
                    && !StringUtils.isEmpty(datiIstitutoSpesa.getMailDopoOrdine())) {
                mailService.sendEmail(oggetto, testo, false, true, datiIstitutoSpesa.getMailDopoOrdine());
            }
        }
    }

    public void popolaCoda(OrdineMissione ordineMissione) {
        if (ordineMissione == null) {
            log.warn("OrdineMissione nullo, niente da inviare in coda");
            return;
        }

        if (rabbitMQService == null) {
            log.warn("rabbitMQService non inizializzato");
            return;
        }

        if (ordineMissione.getMatricola() != null && !isDevProfile()) {
            Account account = accountService.loadAccountFromUsername(ordineMissione.getUid());
            String idSede = account != null ? account.getCodice_sede() : null;

            Missione missione = new Missione(
                    TypeMissione.ORDINE,
                    (Long) ordineMissione.getId(), // Long già
                    idSede,
                    ordineMissione.getMatricola(),
                    ordineMissione.getDataInizioMissione(),
                    ordineMissione.getDataFineMissione(),
                    null,
                    ordineMissione.isMissioneEstera() ? TypeTipoMissione.ESTERA : TypeTipoMissione.ITALIA,
                    ordineMissione.getAnno(),
                    ordineMissione.getNumero()
            );
            rabbitMQService.send(missione);
        }
    }

    private void aggiornaValidazione(OrdineMissione ordineMissione) {
        if (accountService.isUserSpecialEnableToValidateOrder(securityService.getCurrentUserLogin(), ordineMissione.getUoSpesa())) {
            ordineMissione.setValidato("S");
        } else {
            ordineMissione.setValidato("N");
        }
    }

    public String retrieveStateFromFlows(ResultFlows result) {
        return result.getState();
    }

    public List<OrdineMissione> getOrdiniMissione(MissioneFilter filter, Boolean isServiceRest)
            throws AwesomeException {
        return getOrdiniMissione(filter, isServiceRest, false);
    }

    public List<OrdineMissione> getOrdiniMissione(MissioneFilter filter,
                                                  Boolean isServiceRest,
                                                  Boolean isForValidateFlows) throws AwesomeException {

        Specification<OrdineMissione> spec = MissioneFilterMapper.mapBaseFilters(filter);

        if (filter != null) {

            // Se impostati sia user che uoRich, resetta uoRich
            if (filter.getUoRich() != null && filter.getUser() != null) {
                filter.setUoRich(null);
            }

            // --- Ramo daCron ---
            if ("S".equals(Utility.nvl(filter.getDaCron(), "N"))) {
                // Nessun filtro utente aggiuntivo
            }

            // --- Ramo toFinal ---
            else if ("S".equals(Utility.nvl(filter.getToFinal(), "N"))) {
                if (StringUtils.isEmpty(filter.getUoRich())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            "Non è stata selezionata la uo per rendere definitivi ordini di missione.");
                }
                UsersSpecial userSpecial = accountService.getUoForUsersSpecial(SecurityUtils.getCurrentUser().getName());
                boolean uoAbilitata = false;
                if (userSpecial != null) {
                    if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")) {
                        if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()) {
                            for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()) {
                                if (uoService.getUoSigla(uoUser).equals(filter.getUoRich())) {
                                    uoAbilitata = true;
                                    if (!"S".equals(Utility.nvl(uoUser.getRendi_definitivo(), "N"))) {
                                        throw new AwesomeException(CodiciErrore.ERRGEN,
                                                "L'utente non è abilitato a rendere definitivi ordini di missione.");
                                    }
                                }
                            }
                        }
                    }
                }
                if (!uoAbilitata) {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            "L'utente non è abilitato a rendere definitivi ordini di missione.");
                }
                spec = new SpecificationBuilder<OrdineMissione>()
                        .and(spec)
                        .and((root, query, cb) -> cb.equal(root.get("statoFlusso"), Costanti.STATO_APPROVATO_FLUSSO))
                        .and((root, query, cb) -> cb.equal(root.get("stato"), Costanti.STATO_CONFERMATO))
                        .and((root, query, cb) -> cb.equal(root.get("validato"), "S"))
                        .build();
            }

            // --- Ramo normale ---
            else {
                if (!isForValidateFlows) {
                    if (!StringUtils.isEmpty(filter.getUser())) {
                        spec = new SpecificationBuilder<OrdineMissione>()
                                .and(spec)
                                .and(SecuritySpecification.forUser(filter.getUser()))
                                .build();
                    } else {
                        if (StringUtils.isEmpty(filter.getUoRich())) {
                            if ("S".equals(Utility.nvl(filter.getRespGruppo(), "N"))) {
                                // Solo responsabile gruppo (stato != INS)
                                String currentUser = SecurityUtils.getCurrentUser().getName();
                                spec = new SpecificationBuilder<OrdineMissione>()
                                        .and(spec)
                                        .and((root, query, cb) -> cb.and(
                                                cb.equal(root.get("responsabileGruppo"), currentUser),
                                                cb.notEqual(root.get("stato"), "INS")
                                        ))
                                        .build();
                            } else {
                                spec = new SpecificationBuilder<OrdineMissione>()
                                        .and(spec)
                                        .and(SecuritySpecification.forUser(SecurityUtils.getCurrentUser().getName()))
                                        .build();
                            }
                        }
                    }
                } else {
                    String currentUser = SecurityUtils.getCurrentUser().getName();
                    UsersSpecial userSpecial = accountService.getUoForUsersSpecial(currentUser);
                    SpecificationBuilder<OrdineMissione> sb = new SpecificationBuilder<OrdineMissione>().and(spec);

                    if (userSpecial != null && !"S".equals(userSpecial.getAll())) {
                        if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()) {
                            // OR tra tutte le UO + responsabileGruppo
                            List<Specification<OrdineMissione>> uoSpecs = new ArrayList<>();
                            for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()) {
                                final String uoSigla = uoService.getUoSigla(uoUser);
                                uoSpecs.add((root, query, cb) -> cb.equal(root.get("uoRich"), uoSigla));
                                uoSpecs.add((root, query, cb) -> cb.equal(root.get("uoSpesa"), uoSigla));
                                uoSpecs.add((root, query, cb) -> cb.equal(root.get("uoCompetenza"), uoSigla));
                            }
                            // Aggiunge responsabileGruppo alla disgiunzione (come in condizioneResponsabileGruppo)
                            uoSpecs.add(buildResponsabileGruppoSpec(filter, currentUser));

                            Specification<OrdineMissione> uoDisjunction = uoSpecs.stream()
                                    .reduce(Specification::or)
                                    .orElse(null);
                            if (uoDisjunction != null) {
                                sb.and(uoDisjunction);
                            }
                        } else {
                            sb.and(buildUtenteConResponsabileGruppoSpec(filter, currentUser));
                        }
                    } else {
                        sb.and(buildUtenteConResponsabileGruppoSpec(filter, currentUser));
                    }
                    spec = sb.build();
                }

                // Escludi stati annullati (a meno che includiMissioniAnnullate o daId==aId)
                if (!"S".equals(Utility.nvl(filter.getIncludiMissioniAnnullate()))
                        && !(filter.getDaId() != null && filter.getaId() != null
                        && filter.getDaId().compareTo(filter.getaId()) == 0)) {
                    SpecificationBuilder<OrdineMissione> sb = new SpecificationBuilder<OrdineMissione>().and(spec)
                            .and((root, query, cb) -> cb.notEqual(root.get("stato"), Costanti.STATO_ANNULLATO))
                            .and((root, query, cb) -> cb.notEqual(root.get("stato"), Costanti.STATO_ANNULLATO_DOPO_APPROVAZIONE));
                    if (StringUtils.isEmpty(filter.getGiaRimborsato())) {
                        sb.and((root, query, cb) -> cb.notEqual(root.get("stato"),
                                Costanti.STATO_ANNULLATO_DOPO_APPROVAZIONE_CONSENTITO_RIMBORSO));
                    }
                    spec = sb.build();
                }

                // Filtro statoFlusso per REST + validateFlows
                if (Boolean.TRUE.equals(isServiceRest) && Boolean.TRUE.equals(isForValidateFlows)) {
                    List<String> listaStatiFlusso = Arrays.asList(
                            Costanti.STATO_INVIATO_FLUSSO,
                            Costanti.STATO_FIRMATO_PRIMA_FIRMA_FLUSSO,
                            Costanti.STATO_INSERITO,
                            Costanti.STATO_RESPINTO_UO_FLUSSO,
                            Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO
                    );
                    spec = new SpecificationBuilder<OrdineMissione>()
                            .and(spec)
                            .and((root, query, cb) -> cb.or(
                                    root.get("statoFlusso").in(listaStatiFlusso),
                                    cb.equal(root.get("stato"), Costanti.STATO_INSERITO)
                            ))
                            .build();
                }
            }

            // Filtro UO (comune, escluso daCron)
            if (filter.getUoRich() != null && !"S".equals(filter.getDaCron())) {
                if (accountService.isUserEnableToWorkUo(filter.getUoRich())) {
                    Specification<OrdineMissione> s = (root, query, cb) -> cb.or(
                            cb.equal(root.get("uoRich"), filter.getUoRich()),
                            cb.equal(root.get("uoSpesa"), filter.getUoRich()),
                            cb.equal(root.get("uoCompetenza"), filter.getUoRich())
                    );
                    spec = new SpecificationBuilder<OrdineMissione>().and(spec).and(s).build();
                } else {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            "L'utente " + SecurityUtils.getCurrentUser().getName()
                                    + " non è abilitato a vedere i dati della uo " + filter.getUoRich());
                }
            }

            // Solo missioni non gratuite
            if (Boolean.TRUE.equals(filter.getSoloMissioniNonGratuite())) {
                spec = new SpecificationBuilder<OrdineMissione>()
                        .and(spec)
                        .and((root, query, cb) -> cb.or(
                                cb.isNull(root.get("missioneGratuita")),
                                cb.equal(root.get("missioneGratuita"), "N")
                        ))
                        .build();
            }

            // Filtro giaRimborsato
            if ("N".equals(Utility.nvl(filter.getGiaRimborsato(), "A"))) {
                spec = new SpecificationBuilder<OrdineMissione>()
                        .and(spec)
                        .and(RimborsoSpecification.nonRimborsato())
                        .build();
            } else if ("R".equals(Utility.nvl(filter.getGiaRimborsato(), "A"))) {
                spec = new SpecificationBuilder<OrdineMissione>()
                        .and(spec)
                        .and(RimborsoSpecification.giaRimborsato())
                        .build();
            }

            // Filtro daAnnullare
            if ("S".equals(Utility.nvl(filter.getDaAnnullare(), "N"))) {
                spec = new SpecificationBuilder<OrdineMissione>()
                        .and(spec)
                        .and((root, query, cb) -> {
                            Subquery<Long> sub = query.subquery(Long.class);
                            var ann = sub.from(AnnullamentoOrdineMissione.class);
                            sub.select(ann.get("id"))
                                    .where(
                                            cb.equal(ann.get("ordineMissione").get("id"), root.get("id")),
                                            cb.notEqual(ann.get("stato"), "ANN"),
                                            cb.notEqual(ann.get("stato"), "ANA"),
                                            cb.notEqual(ann.get("stato"), "DEF")
                                    );
                            return cb.not(cb.exists(sub));
                        })
                        .build();
            }
        }

        Sort sort = Sort.by(Sort.Order.desc("dataInserimento"))
                .and(Sort.by(Sort.Order.desc("anno")))
                .and(Sort.by(Sort.Order.desc("numero")));

        List<OrdineMissione> ordineMissioneList = ordineMissioneRepository.findAll(spec, sort);

        // Recupero anticipo (solo isServiceRest senza validateFlows)
        if (Boolean.TRUE.equals(isServiceRest) && !Boolean.TRUE.equals(isForValidateFlows) && ordineMissioneList != null) {
            for (OrdineMissione ordineMissione : ordineMissioneList) {
                OrdineMissioneAnticipo anticipo = ordineMissioneAnticipoService
                        .getAnticipo(Long.valueOf(ordineMissione.getId().toString()));
                ordineMissione.setRichiestaAnticipo(anticipo != null ? Costanti.SI_NO.get("S") : Costanti.SI_NO.get("N"));
            }
        }

        // Recupero auto propria, taxi, noleggio
        if (ordineMissioneList != null && filter != null) {
            if ("S".equals(Utility.nvl(filter.getRecuperoAutoPropria()))) {
                for (OrdineMissione o : ordineMissioneList) {
                    o.setUtilizzoAutoPropria(
                            ordineMissioneAutoPropriaService.getAutoPropria(Long.valueOf(o.getId().toString())) != null ? "S" : "N");
                }
            }
            if ("S".equals(Utility.nvl(filter.getRecuperoTaxi()))) {
                for (OrdineMissione o : ordineMissioneList) {
                    o.setUtilizzoTaxi(
                            ordineMissioneTaxiService.getTaxi(Long.valueOf(o.getId().toString())) != null ? "S" : "N");
                }
            }
            if ("S".equals(Utility.nvl(filter.getRecuperoAutoNoleggio()))) {
                for (OrdineMissione o : ordineMissioneList) {
                    o.setUtilizzoAutoNoleggio(
                            ordineMissioneAutoNoleggioService.getAutoNoleggio(Long.valueOf(o.getId().toString())) != null ? "S" : "N");
                }
            }
        }

        return ordineMissioneList;
    }

    // Equivalente di condizioneResponsabileGruppo
    private Specification<OrdineMissione> buildResponsabileGruppoSpec(MissioneFilter filter, String currentUser) {
        if (filter.getDaId() != null && filter.getaId() != null
                && filter.getDaId().compareTo(filter.getaId()) == 0) {
            return (root, query, cb) -> cb.and(
                    cb.equal(root.get("responsabileGruppo"), currentUser),
                    cb.notEqual(root.get("stato"), "INS")
            );
        } else {
            return (root, query, cb) -> cb.and(
                    cb.equal(root.get("responsabileGruppo"), currentUser),
                    cb.equal(root.get("stato"), "INR")
            );
        }
    }

    // Equivalente di condizioneOrdineDellUtenteConResponsabileGruppo
    private Specification<OrdineMissione> buildUtenteConResponsabileGruppoSpec(MissioneFilter filter, String currentUser) {
        Specification<OrdineMissione> respGruppoSpec = buildResponsabileGruppoSpec(filter, currentUser);
        return respGruppoSpec.or((root, query, cb) -> cb.equal(root.get("uid"), currentUser));
    }

    public OrdineMissioneAutoPropria getAutoPropria(OrdineMissione ordineMissione) {
        return OrdineMissioneAutoPropriaRepository.getAutoPropria(ordineMissione);
    }

    public OrdineMissioneTaxi getTaxi(OrdineMissione ordineMissione) {
        return ordineMissioneTaxiRepository.getTaxi(ordineMissione);
    }

    public OrdineMissioneAutoNoleggio getAutoNoleggio(OrdineMissione ordineMissione) {
        return ordineMissioneAutoNoleggioRepository.getAutoNoleggio(ordineMissione);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissione createOrdineMissione(OrdineMissione ordineMissione)
            throws AwesomeException {
        controlloDatiObbligatoriDaGUI(ordineMissione);
        inizializzaCampiPerInserimento(ordineMissione);
        boolean updateOrdineMissione = false;
        calcSpeseTotOrdine(ordineMissione, null);
        validaCRUD(ordineMissione, updateOrdineMissione);
        ordineMissione = ordineMissioneRepository.save(ordineMissione);
        // autoPropriaRepository.save(autoPropria);
        log.debug("Created Information for User: {}", ordineMissione);
        return ordineMissione;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void verifyStepRespGruppo(OrdineMissione ordineMissione) throws AwesomeException {
        log.info("Start 2 Resp gruppo");
        DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(),
                ordineMissione.getAnno());
        ZonedDateTime oggi = ZonedDateTime.now();
        long minutiDifferenza = 10000;
        long minutiDifferenzaDaInizioMissione = 0;
        if (oggi.isBefore(ordineMissione.getDataInizioMissione())) {
            minutiDifferenzaDaInizioMissione = ChronoUnit.MINUTES.between(oggi.truncatedTo(ChronoUnit.MINUTES),
                    ordineMissione.getDataInizioMissione().truncatedTo(ChronoUnit.MINUTES));
        }
        log.info("Start 2.1 Resp gruppo");
        if (ordineMissione.getDataInvioRespGruppo() != null) {
            minutiDifferenza = ChronoUnit.MINUTES.between(
                    ordineMissione.getDataInvioRespGruppo().truncatedTo(ChronoUnit.MINUTES),
                    oggi.truncatedTo(ChronoUnit.MINUTES));
        }
        log.info("Start 2.2 Resp gruppo");
        if (istituto.getMinutiPrimaInizioResp() != null
                && minutiDifferenzaDaInizioMissione < istituto.getMinutiPrimaInizioResp()) {
            if (istituto.getMinutiMinimiResp() != null && minutiDifferenza > istituto.getMinutiMinimiResp()) {
                bypassRespGruppo(ordineMissione);
            }
        }
        log.info("Start 2.3 Resp gruppo");
        if (istituto.getMinutiPassatiResp() != null && minutiDifferenza > istituto.getMinutiPassatiResp()) {
            bypassRespGruppo(ordineMissione);
        }

    }

    private void bypassRespGruppo(OrdineMissione ordineMissione) {
        ZonedDateTime oggi = ZonedDateTime.now();
        ordineMissione.setBypassRespGruppo("S");
        ordineMissione.setDaChron("S");
        updateOrdineMissione(ordineMissione, false, true, null);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void verifyStepAmministrativo(OrdineMissione ordineMissione) throws AwesomeException {
        log.info("Start 2 Amm");
        DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(),
                ordineMissione.getAnno());
        ZonedDateTime oggi = ZonedDateTime.now();
        long minutiDifferenza = 10000;
        long minutiDifferenzaDaInizioMissione = 0;
        log.info("Start 2.1 Amm");
        if (oggi.isBefore(ordineMissione.getDataInizioMissione())) {
            minutiDifferenzaDaInizioMissione = ChronoUnit.MINUTES.between(oggi.truncatedTo(ChronoUnit.MINUTES),
                    ordineMissione.getDataInizioMissione().truncatedTo(ChronoUnit.MINUTES));
        }
        log.info("Start 2.2 Amm");
        if (ordineMissione.getDataInvioAmministrativo() != null) {
            minutiDifferenza = ChronoUnit.MINUTES.between(
                    ordineMissione.getDataInvioAmministrativo().truncatedTo(ChronoUnit.MINUTES),
                    oggi.truncatedTo(ChronoUnit.MINUTES));
        }

        log.info("Start 2.3 Amm");
        if (istituto.getMinutiPrimaInizioAmm() != null
                && minutiDifferenzaDaInizioMissione < istituto.getMinutiPrimaInizioAmm()) {
            if (istituto.getMinutiMinimiAmm() != null && minutiDifferenza > istituto.getMinutiMinimiAmm()) {
                bypassVerificaAmministrativo(ordineMissione);
            }
        }
        log.info("Start 2.4 Amm");
        if (istituto.getMinutiPassatiAmm() != null && minutiDifferenza > istituto.getMinutiPassatiAmm()) {
            bypassVerificaAmministrativo(ordineMissione);
        }
        log.info("Start 2.5 Amm");

    }

    private void bypassVerificaAmministrativo(OrdineMissione ordineMissione) {
        ordineMissione.setBypassAmministrativo("S");
        ordineMissione.setDaValidazione("S");
        ordineMissione.setDaChron("S");
        ordineMissione.setToBeUpdated();
        updateOrdineMissione(ordineMissione, false, true);
    }

    private void inizializzaCampiPerInserimento(OrdineMissione ordineMissione)
            throws AwesomeException {
        ordineMissione.setUidInsert(securityService.getCurrentUserLogin());
        ordineMissione.setUser(securityService.getCurrentUserLogin());
        Integer anno = recuperoAnno(ordineMissione);
        ordineMissione.setAnno(anno);
        ordineMissione.setNumero(datiIstitutoService.getNextPG(ordineMissione.getUoRich(), anno,
                Costanti.TIPO_ORDINE_DI_MISSIONE));
        if (StringUtils.isEmpty(ordineMissione.getTrattamento())) {
            ordineMissione.setTrattamento("O");
        }
        if (StringUtils.isEmpty(ordineMissione.getObbligoRientro())) {
            ordineMissione.setObbligoRientro("S");
        }
        if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoNoleggio())) {
            ordineMissione.setUtilizzoAutoNoleggio("N");
        }
        if (StringUtils.isEmpty(ordineMissione.getUtilizzoTaxi())) {
            ordineMissione.setUtilizzoTaxi("N");
        }
        if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoServizio())) {
            ordineMissione.setUtilizzoAutoServizio("N");
        }
        if (StringUtils.isEmpty(ordineMissione.getPersonaleAlSeguito())) {
            ordineMissione.setPersonaleAlSeguito("N");
        }
        if (StringUtils.isEmpty(ordineMissione.getRichiestaAnticipo())) {
            ordineMissione.setRichiestaAnticipo("N");
        }
        if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoPropria())) {
            ordineMissione.setUtilizzoAutoPropria("N");
        }

        aggiornaValidazione(ordineMissione);

        ordineMissione.setStato(Costanti.STATO_INSERITO);
        ordineMissione.setStatoFlusso(Costanti.STATO_INSERITO);
        if (StringUtils.isEmpty(ordineMissione.getMatricola()) && StringUtils.isEmpty(ordineMissione.getQualificaRich())) {
            Account account = accountService.loadAccountFromUsername(ordineMissione.getUid());
            if (account != null && account.getCodice_fiscale() != null) {
                TerzoPerCompensoJson terzoJson = terzoPerCompensoService.getTerzi(account.getCodice_fiscale(),
                        ordineMissione.getDataInizioMissione(), ordineMissione.getDataFineMissione());
                for (TerzoPerCompenso terzo : terzoJson.getElements()) {
                    ordineMissione.setQualificaRich(terzo.getDsTipoRapporto());
                    break;
                }
            }
        }
        ordineMissione.setToBeCreated();
    }

    private Integer recuperoAnno(OrdineMissione ordineMissione) {
        if (ordineMissione.getDataInserimento() == null) {
            ordineMissione.setDataInserimento(LocalDate.now());
        }
        return ordineMissione.getDataInserimento().getYear();
    }

    private Boolean isInvioOrdineAlResponsabileGruppo(OrdineMissione ordineMissione) {
        return Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("M");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissione updateOrdineMissione(OrdineMissione ordineMissione)
            throws AwesomeException {
        return updateOrdineMissione(ordineMissione, false);
    }

    private OrdineMissione updateOrdineMissione(OrdineMissione ordineMissione, Boolean fromFlows)
            throws AwesomeException {
        return updateOrdineMissione(ordineMissione, fromFlows, false);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissione updateOrdineMissione(OrdineMissione ordineMissione, Boolean fromFlows,
                                               Boolean confirm) {
        return updateOrdineMissione(ordineMissione, fromFlows, confirm, null);
    }

    //controlla che ci sia la Gae ,la voce di bilancio, l'impegno e l'importo presunto
    private void checkObbDatiContabili(OrdineMissione ordineMissione, boolean sendToSign) {
        if (sendToSign) {
            if (StringUtils.isEmpty(ordineMissione.getFondi()))
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Fondi");

            if (StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione()))
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Anno Impegno");

            if (StringUtils.isEmpty(ordineMissione.getPgObbligazione()))
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Numero Impegno");

            if (StringUtils.isEmpty(ordineMissione.getVoce()))
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Voce Bilancio");

            String missioneGratuita = ordineMissione.getMissioneGratuita();
            if (StringUtils.isEmpty(ordineMissione.getImportoPresunto()) && !"S".equals(missioneGratuita)) {
                throw new AwesomeException(
                        CodiciErrore.ERRGEN,
                        CodiciErrore.CAMPO_OBBLIGATORIO + ": Importo Presunto"
                );
            }

        }
        if (!sendToSign) {
            if (StringUtils.isEmpty(ordineMissione.getGae()))
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": GAE");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissione updateOrdineMissione(OrdineMissione ordineMissione, Boolean fromFlows,
                                               Boolean confirm, String basePath) {
        ZonedDateTime oggi = ZonedDateTime.now();
        if (!fromFlows && !isUserEnabledToViewMissione(ordineMissione) && Utility.nvl(ordineMissione.getDaChron(), "N").equals("N")) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Non Autorizzato");
        }
        OrdineMissione ordineMissioneDB = ordineMissioneRepository.findById((Long) ordineMissione.getId()).orElse(null);

        if (ordineMissioneDB == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di Missione da aggiornare inesistente.");
        }

        try {
            entityManager.lock(ordineMissioneDB, LockModeType.OPTIMISTIC);
        } catch (OptimisticLockException e) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Ordine in modifica. Ripetere l'operazione. ID: " + ordineMissioneDB.getId());
        }
        boolean isCambioResponsabileGruppo = false;
        boolean isRitornoMissioneMittente = false;
        boolean emailToValidatorSent = false;

        if (ordineMissioneDB == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di Missione da aggiornare inesistente.");
        }

        boolean isValidatore = accountService.isUserSpecialEnableToValidateOrder(securityService.getCurrentUserLogin(), ordineMissioneDB.getUoSpesa()) && Utility.nvl(ordineMissione.getDaChron(), "N").equals("N");

        calcSpeseTotOrdine(ordineMissione, ordineMissioneDB);
        // try {
        // crudServiceBean.lockBulk(ordineMissioneDB);
        // } catch (OptimisticLockException | PersistencyException |
        // BusyResourceException e) {
        // throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di missione
        // in modifica. Ripetere l'operazione.");
        // }
        if (ordineMissione.getResponsabileGruppo() != null && ordineMissioneDB.getResponsabileGruppo() != null
                && !ordineMissione.getResponsabileGruppo().equals(ordineMissioneDB.getResponsabileGruppo())) {
            isCambioResponsabileGruppo = true;
            ordineMissioneDB.setNoteRespingi(null);
        }
        if (ordineMissioneDB.isMissioneConfermata() && !fromFlows
                && !Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("D")) {
            if (ordineMissioneDB.isStatoFlussoApprovato()) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Non è possibile modificare l'ordine di missione. E' già stato approvato.");
            }
            if (!ordineMissioneDB.isMissioneDaValidare()) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Non è possibile modificare l'ordine di missione. E' già stato avviato il flusso di approvazione.");
            }
            ordineMissioneDB.setNoteRespingi(null);
        }

        if (Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("S")) {

            if (!ordineMissioneDB.getStato().equals(Costanti.STATO_CONFERMATO)) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di missione non confermato.");
            }
            if (!ordineMissioneDB.isMissioneDaValidare()) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di missione già validato.");
            }

            if (!isValidatore) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Utente non abilitato a validare gli ordini di missione per la uo " + ordineMissioneDB.getUoSpesa() + ".");
            }

            validaCRUD(ordineMissione, true);
            aggiornaDatiOrdineMissione(ordineMissione, confirm, ordineMissioneDB);

            if (confirm) {
                checkObbDatiContabili(ordineMissione, true);
                ordineMissioneDB.setValidato("S");
                ordineMissioneDB.setNoteRespingi(null);
            }

        } else if (Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("D")) {
            if (ordineMissione.getEsercizioOriginaleObbligazione() == null
                    || ordineMissione.getPgObbligazione() == null) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Per rendere definitivo l'ordine di missione è necessario valorizzare l'impegno.");
            }
            if (!StringUtils.isEmpty(ordineMissione.getGae())) {
                ordineMissioneDB.setGae(ordineMissione.getGae());
            }
            if (!StringUtils.isEmpty(ordineMissione.getVoce())) {
                ordineMissioneDB.setVoce(ordineMissione.getVoce());
            }
            ordineMissioneDB.setEsercizioOriginaleObbligazione(ordineMissione.getEsercizioOriginaleObbligazione());
            ordineMissioneDB.setPgObbligazione(ordineMissione.getPgObbligazione());
            ordineMissioneDB.setNoteRespingi(null);
            ordineMissioneDB.setStato(Costanti.STATO_DEFINITIVO);
        } else if (Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("R")) {
            if (!ordineMissione.isMissioneInviataResponsabile() && !accountService
                    .isUserSpecialEnableToValidateOrder(securityService.getCurrentUserLogin(), ordineMissione.getUoSpesa())) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Utente non abilitato a validare gli ordini di missione per la uo "
                                + ordineMissione.getUoSpesa() + ".");
            }
            if (ordineMissioneDB.isStatoNonInviatoAlFlusso() || ordineMissioneDB.isMissioneDaValidare()) {
                if (StringUtils.isEmpty(ordineMissione.getNoteRespingi())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            "Non è possibile respingere un ordine di missione senza indicarne il motivo.");
                }
                ordineMissioneDB.setDataInvioAmministrativo(null);
                ordineMissioneDB.setDataInvioRespGruppo(null);
                ordineMissioneDB.setBypassRespGruppo(null);
                ordineMissioneDB.setStato(Costanti.STATO_INSERITO);
                ordineMissioneDB.setNoteRespingi(ordineMissione.getNoteRespingi());
                isRitornoMissioneMittente = true;
            } else {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Non è possibile sbloccare un ordine di missione se è stato già inviato al flusso.");
            }
        } else if (isInvioOrdineAlResponsabileGruppo(ordineMissione)) {
            if (ordineMissione.getResponsabileGruppo() != null) {
                if (ordineMissioneDB.isMissioneInserita()) {
                    ordineMissioneDB.setResponsabileGruppo(ordineMissione.getResponsabileGruppo());
                    ordineMissioneDB.setStato(Costanti.STATO_INVIATO_RESPONSABILE);
                    ordineMissioneDB.setDataInvioRespGruppo(oggi);
                    ordineMissioneDB.setNoteRespingi(null);
                } else {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            "Non è possibile inviare al responsabile una missione in stato diverso da 'Inserito'.");
                }
            } else {
                throw new AwesomeException(CodiciErrore.ERRGEN, "E' obbligatorio indicare il responsabile del gruppo.");
            }
// CODICE DA TOGLIERE DA INIZIO ANNO FINO  AL RIBALTAMENTO EFFETTUATO
            /*if (StringUtils.isEmpty(ordineMissioneDB.getPgProgetto())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "E' necessario indicare il Progetto.");
            }*/

        } else {
            validaCRUD(ordineMissione, true);
            aggiornaDatiOrdineMissione(ordineMissione, confirm, ordineMissioneDB);
        }

        if (confirm) {
            DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(),
                    ordineMissione.getAnno());
            if (istituto != null && istituto.isAttivaGestioneResponsabileModulo()
                    && !ordineMissione.isMissioneGratuita()) {
                if (StringUtils.isEmpty(ordineMissioneDB.getResponsabileGruppo())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            "Per il cds di spesa indicato è attiva la gestione del responsabile del gruppo ma non è stato inserito il responsabile del gruppo.");
                }
                // CODICE DA TOGLIERE DA INIZIO ANNO FINO  AL RIBALTAMENTO EFFETTUATO
                /*if (StringUtils.isEmpty(ordineMissioneDB.getPgProgetto())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, "E' necessario indicare il Progetto.");
                }*/
                if (ordineMissioneDB.isMissioneInserita()
                        && !ordineMissioneDB.getResponsabileGruppo().equals(ordineMissione.getUid())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            "Per il cds di spesa indicato è attiva la gestione del responsabile del gruppo ma l'ordine di missione non è stato inviato alla sua approvazione.");
                }
            }
            if (Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("N") && ordineMissione.isMissioneConfermata()
                    && ordineMissione.isMissioneDaValidare()) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di missione già confermato.");
            }
            ordineMissioneDB.setStato(Costanti.STATO_CONFERMATO);
            ordineMissioneDB.setNoteRespingi(null);
        }

        ordineMissioneDB.setToBeUpdated();

        // //effettuo controlli di validazione operazione CRUD
        if (!Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("R") && !fromFlows) {
            boolean updateOrdineMissione = true;
            validaCRUD(ordineMissioneDB, updateOrdineMissione);
        }

        if (confirm) {
            Parametri parametri = parametriService.getParametri();
            if (parametri != null && StringUtils.hasLength(parametri.getDipendenteCda())
                    && Utility.nvl(ordineMissione.getUid(), "N").equals(parametri.getDipendenteCda())
                    && Utility.nvl(ordineMissione.getPresidente(), "N").equals("S")) {
                ordineMissioneDB.setStateFlows(Costanti.STATO_FLUSSO_FROM_CMIS.get(Costanti.STATO_FIRMATO_FROM_CMIS));
                ordineMissioneDB.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
                ordineMissioneDB.setValidato("S");
                ordineMissioneDB.setStato(Costanti.STATO_DEFINITIVO);

            } else if (!ordineMissioneDB.isMissioneDaValidare()) {
                DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(),
                        ordineMissione.getAnno());
                //bosgna controllare Anno Impegno, numero Impegno Fondi
                if (Utility.nvl(istituto.getCreaImpegnoAut(), "N").equals("S")) {

                }
                checkObbDatiContabili(ordineMissione, true);
                cmisOrdineMissioneService.avviaFlusso(ordineMissioneDB);
                ordineMissioneDB.setStateFlows(Costanti.STATO_FLUSSO_FROM_CMIS.get(Costanti.STATO_FIRMA_UO_FROM_CMIS));
                ordineMissioneDB.setDataInvioFirma(oggi);
                if (istituto.isAttivaGestioneResponsabileModulo()) {
                    if (ordineMissioneDB.getDataInvioRespGruppo() == null) {
                        ordineMissioneDB.setDataInvioRespGruppo(oggi);
                    }
                }
                if (ordineMissioneDB.getDataInvioAmministrativo() == null) {
                    ordineMissioneDB.setDataInvioAmministrativo(oggi);
                }
            } else if (ordineMissioneDB.isMissioneDaValidare()) {

                checkObbDatiContabili(ordineMissione, false);
                DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(),
                        ordineMissione.getAnno());
                if (istituto.isAttivaGestioneResponsabileModulo()) {
                    if (ordineMissioneDB.getDataInvioRespGruppo() == null) {
                        ordineMissioneDB.setDataInvioRespGruppo(oggi);
                    }
                }
                ordineMissioneDB.setDataInvioAmministrativo(oggi);
                //invio email all'invio in validazione di un richiedente (aggiungi nel corpo anche ...per la verifica/completamento dei dati finanziari.)
                sendMailToValidator(basePath, ordineMissioneDB);
                emailToValidatorSent = true;
            }

        }

        ordineMissioneDB = ordineMissioneRepository.save(ordineMissioneDB);

        // autoPropriaRepository.save(autoPropria);
        log.debug("Updated Information for Ordine di Missione: {}", ordineMissioneDB);
        if (isInvioOrdineAlResponsabileGruppo(ordineMissione)
                || (isCambioResponsabileGruppo && ordineMissioneDB.isMissioneInviataResponsabile())
                && basePath != null) {
            mailService.sendEmail(subjectSendToManagerOrdine + " " + getNominativo(ordineMissioneDB.getUid()),
                    getTextMailSendToManager(basePath, ordineMissioneDB), false, true,
                    accountService.getEmail(ordineMissione.getResponsabileGruppo()));
            //controlla se non è stata già inviata l'email ai validatori, in tal caso non fare il reinvio
        } else if (confirm && ordineMissioneDB.isMissioneDaValidare() && !emailToValidatorSent) {
            sendMailToAdministrative(basePath, ordineMissioneDB);
            emailToValidatorSent = true;
        }
        if (isRitornoMissioneMittente) {
            missioneRespintaService.inserisciOrdineMissioneRespinto(ordineMissioneDB, ordineMissione.isMissioneInviataResponsabile() ? MissioneRespinta.FASE_RESPINGI_RESP_GRUPPO : MissioneRespinta.FASE_RESPINGI_AMMINISTRATIVI);
            mailService.sendEmail(subjectReturnToSenderOrdine,
                    getTextMailReturnToSender(basePath, ordineMissioneDB), false, true,
                    accountService.getEmail(ordineMissioneDB.getUidInsert()));
        }
        return ordineMissioneDB;
    }

    private void calcSpeseTotOrdine(OrdineMissione ordineMissione, @Nullable OrdineMissione ordineMissioneDB) {
        BigDecimal dbTotOrdine = ordineMissioneDB != null
                ? Utility.nvl(ordineMissioneDB.getTotaleSpesePresComplessivo())
                : BigDecimal.ZERO;

        BigDecimal calcTotOrdine = Utility.nvl(ordineMissione.getTotaleSpeseOrdine());
        BigDecimal toUpdateTotOrdine = Utility.nvl(ordineMissione.getTotaleSpesePresComplessivo());

        BigDecimal totaleSpesaOrdine = calcolaTot(dbTotOrdine, calcTotOrdine, toUpdateTotOrdine);

        if (ordineMissioneDB != null) {
            ordineMissioneDB.setTotaleSpesePresComplessivo(totaleSpesaOrdine);
        } else {
            ordineMissione.setTotaleSpesePresComplessivo(totaleSpesaOrdine);
        }
    }


    private void aggiornaDatiOrdineMissione(OrdineMissione ordineMissione, Boolean confirm,
                                            OrdineMissione ordineMissioneDB) {
        ordineMissioneDB.setStato(ordineMissione.getStato());
        ordineMissioneDB.setStatoFlusso(ordineMissione.getStatoFlusso());
        ordineMissioneDB.setCdrSpesa(ordineMissione.getCdrSpesa());
        ordineMissioneDB.setCdsSpesa(ordineMissione.getCdsSpesa());
        ordineMissioneDB.setUoSpesa(ordineMissione.getUoSpesa());
        ordineMissioneDB.setCdsCompetenza(ordineMissione.getCdsCompetenza());
        ordineMissioneDB.setUoCompetenza(ordineMissione.getUoCompetenza());
        ordineMissioneDB.setDomicilioFiscaleRich(ordineMissione.getDomicilioFiscaleRich());
        ordineMissioneDB.setDataInizioMissione(ordineMissione.getDataInizioMissione());
        ordineMissioneDB.setDataFineMissione(ordineMissione.getDataFineMissione());
        ordineMissioneDB.setDestinazione(ordineMissione.getDestinazione());
        ordineMissioneDB.setDistanzaDallaSede(ordineMissione.getDistanzaDallaSede());

        String gaeField = ordineMissione.getGae();
        if (gaeField != null) {
            ordineMissione.setGae(gaeField);
            ordineMissioneDB.setGae(ordineMissione.getGae());
        }

        Gae gae = gaeService.loadGae(ordineMissione);

        ordineMissione.setPgProgetto(gae.getPg_progetto());
        ordineMissioneDB.setPgProgetto(ordineMissione.getPgProgetto());

        ordineMissioneDB.setImportoPresunto(ordineMissione.getImportoPresunto());
        ordineMissioneDB.setModulo(ordineMissione.getModulo());
        ordineMissioneDB.setNote(ordineMissione.getNote());
        ordineMissioneDB.setNoteSegreteria(ordineMissione.getNoteSegreteria());
        ordineMissioneDB.setObbligoRientro(ordineMissione.getObbligoRientro());
        if (confirm) {
            aggiornaValidazione(ordineMissioneDB);
        } else {
            ordineMissioneDB.setValidato(ordineMissione.getValidato());
        }
        ordineMissioneDB.setOggetto(ordineMissione.getOggetto());
        ordineMissioneDB.setPartenzaDa(ordineMissione.getPartenzaDa());
        ordineMissioneDB.setPartenzaDaAltro(ordineMissione.getPartenzaDaAltro());
        if (!ordineMissioneDB.getPartenzaDa().equals("A")) {
            ordineMissioneDB.setPartenzaDaAltro(null);
        }
        ordineMissioneDB.setPriorita(ordineMissione.getPriorita());
        ordineMissioneDB.setTipoMissione(ordineMissione.getTipoMissione());
        ordineMissioneDB.setVoce(ordineMissione.getVoce());
        ordineMissioneDB.setTrattamento(ordineMissione.getTrattamento());
        ordineMissioneDB.setNazione(ordineMissione.getNazione());
        ordineMissioneDB.setRichiestaAnticipo(ordineMissione.getRichiestaAnticipo());
        ordineMissioneDB.setNoteUtilizzoTaxiNoleggio(ordineMissione.getNoteUtilizzoTaxiNoleggio());
        ordineMissioneDB.setUtilizzoAutoNoleggio(ordineMissione.getUtilizzoAutoNoleggio());
        ordineMissioneDB.setUtilizzoTaxi(ordineMissione.getUtilizzoTaxi());
        ordineMissioneDB.setPersonaleAlSeguito(ordineMissione.getPersonaleAlSeguito());
        ordineMissioneDB.setUtilizzoAutoServizio(ordineMissione.getUtilizzoAutoServizio());
        ordineMissioneDB.setUtilizzoAutoPropria(ordineMissione.getUtilizzoAutoPropria());
        //ordineMissioneDB.setPgProgetto(ordineMissione.getPgProgetto());
        ordineMissioneDB.setEsercizioOriginaleObbligazione(ordineMissione.getEsercizioOriginaleObbligazione());
        ordineMissioneDB.setPgObbligazione(ordineMissione.getPgObbligazione());
        ordineMissioneDB.setResponsabileGruppo(ordineMissione.getResponsabileGruppo());
        ordineMissioneDB.setFondi(ordineMissione.getFondi());
        ordineMissioneDB.setCup(ordineMissione.getCup());
        ordineMissioneDB.setMissioneGratuita(ordineMissione.getMissioneGratuita());
        ordineMissioneDB.setCug(ordineMissione.getCug());
        ordineMissioneDB.setPresidente(ordineMissione.getPresidente());
        ordineMissioneDB.setBypassAmministrativo(ordineMissione.getBypassAmministrativo());
        ordineMissioneDB.setBypassRespGruppo(ordineMissione.getBypassRespGruppo());
        ordineMissioneDB.setDataInvioAmministrativo(ordineMissione.getDataInvioAmministrativo());
        ordineMissioneDB.setDataInvioRespGruppo(ordineMissione.getDataInvioRespGruppo());
        ordineMissioneDB.setDataInvioFirma(ordineMissione.getDataInvioFirma());
        ordineMissioneDB.setCommentoFlusso(ordineMissione.getCommentoFlusso());
        ordineMissioneDB.setLivelloRich(ordineMissione.getLivelloRich());
        ordineMissioneDB.setQualificaRich(ordineMissione.getQualificaRich());
    }

    private void sendMailToAdministrative(String basePath, OrdineMissione ordineMissioneDB) {
        DatiIstituto dati = datiIstitutoService.getDatiIstituto(ordineMissioneDB.getUoSpesa(),
                ordineMissioneDB.getAnno());
        String testoMail = getTextMailSendToAdministrative(basePath, ordineMissioneDB);
        String subjectMail = subjectSendToAdministrative + " " + getNominativo(ordineMissioneDB.getUid());
        if (dati != null && dati.getMailNotifiche() != null) {
            if (!dati.getMailNotifiche().equals("N")) {
                mailService.sendEmail(subjectMail, testoMail, false, true, dati.getMailNotifiche());
            }
        } else {
            log.info("Ricerca amministrativi per mail. Uo: " + ordineMissioneDB.getUoSpesa());
            List<UsersSpecial> lista = accountService.getUserSpecialForUoPerValidazione(ordineMissioneDB.getUoSpesa());
            sendMailToAdministrative(lista, testoMail, subjectMail);
        }
    }

    private void sendMailToValidator(String basePath, OrdineMissione ordineMissioneDB) {
        String testoMail = getTextMailToSendToValidator(basePath, ordineMissioneDB);
        String subjectMail = subjectSendToAdministrative + " " + getNominativo(ordineMissioneDB.getUid());
        List<UsersSpecial> listaValidatori = accountService.getUserSpecialForUoPerValidazione(ordineMissioneDB.getUoSpesa());
        sendMailToValidatori(listaValidatori, testoMail, subjectMail);
    }

    private void sendMailToGroup(List<UsersSpecial> lista, String testoMail, String oggetto) {
        if (lista != null && lista.size() > 0) {
            String[] elencoMail = mailService.prepareTo(lista);
            if (elencoMail != null && elencoMail.length > 0) {
                mailService.sendEmail(oggetto, testoMail, false, true, elencoMail);
            }
        }
    }

    private void sendMailToAdministrative(List<UsersSpecial> lista, String testoMail, String oggetto) {
        sendMailToGroup(lista, testoMail, oggetto);
    }

    private void sendMailToValidatori(List<UsersSpecial> lista, String testoMail, String oggetto) {
        sendMailToGroup(lista, testoMail, oggetto);
    }


    private String getNominativo(String user) {
        Account utente = accountService.loadAccountFromUsername(user);
        return utente.getCognome() + " " + utente.getNome();
    }

    private List<String> getTosMail(OrdineMissione ordineMissione) {
        List<String> mails = new ArrayList<>();
        Account utenteMissione = accountService.loadAccountFromUsername(ordineMissione.getUid());
        mails.add(utenteMissione.getEmail_comunicazioni());
        if (!ordineMissione.getUid().equals(ordineMissione.getUidInsert())) {
            Account utenteInserimentoMissione = accountService.loadAccountFromUsername(ordineMissione.getUid());
            mails.add(utenteInserimentoMissione.getEmail_comunicazioni());
        }
        return mails;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteOrdineMissione(Long idOrdineMissione) {
        OrdineMissione ordineMissione = ordineMissioneRepository.findById(idOrdineMissione).orElse(null);
        if (ordineMissione != null && isUserEnabledToViewMissione(ordineMissione)) {
            controlloOperazioniCRUDDaGui(ordineMissione);
            ordineMissioneAnticipoService.deleteAnticipo(ordineMissione);
            ordineMissioneAutoPropriaService.deleteAutoPropria(ordineMissione);
            ordineMissioneTaxiService.deleteTaxi(ordineMissione);
            ordineMissioneAutoNoleggioService.deleteAutoNoleggio(ordineMissione);

            ordineMissioneDettagliService.cancellaOrdineMissioneDettagli(ordineMissione, false);

            // effettuo controlli di validazione operazione CRUD
            ordineMissione.setStato(Costanti.STATO_ANNULLATO);
            ordineMissione.setToBeUpdated();
            if (ordineMissione.isStatoRespintoFlusso() && !StringUtils.isEmpty(ordineMissione.getIdFlusso())) {
                cmisOrdineMissioneService.annullaFlusso(ordineMissione);
            }
            ordineMissioneRepository.save(ordineMissione);
        }
    }

    public void controlloOperazioniCRUDDaGui(OrdineMissione ordineMissione) {
        if (!ordineMissione.isMissioneInserita()) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Non è possibile effettuare l'operazione su un ordine di missione che non si trova in uno stato "
                            + Costanti.STATO.get(Costanti.STATO_INSERITO));
        }
    }

    private void controlloDatiObbligatoriDaGUI(OrdineMissione ordineMissione) {
        if (ordineMissione != null) {
            if (StringUtils.isEmpty(ordineMissione.getCdsRich())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Cds Richiedente");
            } else if (StringUtils.isEmpty(ordineMissione.getCdsSpesa())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Cds Spesa");
            } else if (StringUtils.isEmpty(ordineMissione.getUoRich())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Uo Richiedente");
            } else if (StringUtils.isEmpty(ordineMissione.getUoSpesa())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Uo Spesa");
            } else if (StringUtils.isEmpty(ordineMissione.getCdrSpesa())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Cdr Spesa");
            } else if (StringUtils.isEmpty(ordineMissione.getGae())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": GAE");
            } else if (StringUtils.isEmpty(ordineMissione.getDataInizioMissione())) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        CodiciErrore.CAMPO_OBBLIGATORIO + ": Data Inizio Missione");
            } else if (StringUtils.isEmpty(ordineMissione.getDataFineMissione())) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        CodiciErrore.CAMPO_OBBLIGATORIO + ": Data Fine Missione");
            } else if (StringUtils.isEmpty(ordineMissione.getDataInserimento())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Data Inserimento");
            } else if (StringUtils.isEmpty(ordineMissione.getDatoreLavoroRich())) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        CodiciErrore.CAMPO_OBBLIGATORIO + ": Datore di Lavoro Richiedente");
            } else if (StringUtils.isEmpty(ordineMissione.getDestinazione())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Destinazione");
            } else if (StringUtils.isEmpty(ordineMissione.getOggetto())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Oggetto");
            } else if (StringUtils.isEmpty(ordineMissione.getPriorita())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Priorità");
            } else if (StringUtils.isEmpty(ordineMissione.getTipoMissione())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Tipo Missione");
            }
            if (ordineMissione.isMissioneEstera()) {
                if (StringUtils.isEmpty(ordineMissione.getNazione())
                        || Costanti.NAZIONE_ITALIA_SIGLA.compareTo(ordineMissione.getNazione()) == 0) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Nazione");
                }
                if (StringUtils.isEmpty(ordineMissione.getTrattamento())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Trattamento");
                }
            }

            if (StringUtils.isEmpty(ordineMissione.getPartenzaDa())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Partenza Da");
            }

            if (Utility.nvl(ordineMissione.getPartenzaDa(), "N").equals("A")) {
                if (StringUtils.isEmpty(ordineMissione.getPartenzaDaAltro())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            CodiciErrore.CAMPO_OBBLIGATORIO + ": Specificare il luogo di partenza");
                }
            }

            if (ordineMissione.isMissioneConGiorniDivervi()) {
                if (StringUtils.isEmpty(ordineMissione.getObbligoRientro())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            CodiciErrore.CAMPO_OBBLIGATORIO + ": Obbligo di Rientro");
                }
            }
            if (ordineMissione.isMissioneDipendente()) {
                if (StringUtils.isEmpty(ordineMissione.getComuneResidenzaRich())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            CodiciErrore.CAMPO_OBBLIGATORIO + ": Comune di Residenza del Richiedente");
                } /*else if (StringUtils.isEmpty(ordineMissione.getIndirizzoResidenzaRich())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            CodiciErrore.CAMPO_OBBLIGATORIO + ": Indirizzo di Residenza del Richiedente");
                }*/
            }

            String missioneGratuita = ordineMissione.getMissioneGratuita();
            if (StringUtils.isEmpty(ordineMissione.getImportoPresunto()) && !"S".equals(missioneGratuita)) {
                throw new AwesomeException(
                        CodiciErrore.ERRGEN,
                        CodiciErrore.CAMPO_OBBLIGATORIO + ": Importo Presunto"
                );
            }

        }
    }

    private void controlloCongruenzaDatiInseriti(OrdineMissione ordineMissione, boolean updateOrdineMissione) {

        if (!StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione())) {
            if (!String.valueOf(ordineMissione.getEsercizioOriginaleObbligazione()).matches("\\d{4}")) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "L'anno dell' impegno deve essere composto da 4 cifre");
            }
        }

        if (ordineMissione.getDataFineMissione().isBefore(ordineMissione.getDataInizioMissione())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI
                    + ": La data di fine missione non può essere precedente alla data di inizio missione");
        }
        if (DateUtils.getDateAsString(ordineMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC).equals(
                DateUtils.getDateAsString(ordineMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC))) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI
                    + ": Le date di inizio e fine missione non possono essere uguali");
        }

        if (StringUtils.isEmpty(ordineMissione.getIdFlusso()) && ordineMissione.isStatoInviatoAlFlusso()) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Non è possibile avere lo stato Inviato al flusso e non avere l'ID del flusso");
        }
        if (!ordineMissione.isMissioneEstera()) {
            ordineMissione.setNazione(Long.valueOf("1"));
        }
        // if (ordineMissione.getUtilizzoAutoNoleggio() != null &&
        // ordineMissione.getUtilizzoAutoNoleggio().equals("S") &&
        // !ordineMissione.isToBeCreated() && getAutoPropria(ordineMissione) !=
        // null ){
        // throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile
        // salvare una missione con la richiesta di utilizzo dell'auto a
        // noleggio e dell'auto propria.");
        // }
        // if (ordineMissione.getUtilizzoTaxi() != null &&
        // ordineMissione.getUtilizzoTaxi().equals("S") &&
        // !ordineMissione.isToBeCreated() && getAutoPropria(ordineMissione) !=
        // null ){
        // throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile
        // salvare una missione con la richiesta di utilizzo del taxi e
        // dell'auto propria.");
        // }
        /*if (!StringUtils.isEmpty(ordineMissione.getNoteUtilizzoTaxiNoleggio())) {
            if (ordineMissione.getUtilizzoAutoNoleggio().equals("N")) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI
                        + ": Non è possibile indicare le note all'utilizzo dell'auto a noleggio se non si è scelto il suo utilizzo");
            }
        }*/
        // if (ordineMissione.getUtilizzoAutoServizio() != null &&
        // ordineMissione.getUtilizzoAutoServizio().equals("S") &&
        // !ordineMissione.isToBeCreated() && getAutoPropria(ordineMissione) !=
        // null ){
        // throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile
        // salvare una missione con la richiesta di utilizzo dell'auto di
        // servizio e dell'auto propria.");
        // }
        /*if (Utility.nvl(ordineMissione.getUtilizzoAutoNoleggio()).equals("S")
                && StringUtils.isEmpty(ordineMissione.getNoteUtilizzoTaxiNoleggio())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI
                    + ": E' obbligatorio indicare le note all'utilizzo dell'auto a noleggio se si è scelto il suo utilizzo");
        }*/
        // if
        // ((Utility.nvl(ordineMissione.getUtilizzoAutoNoleggio()).equals("S")
        // && Utility.nvl(ordineMissione.getUtilizzoAutoServizio()).equals("S"))
        // ||
        // (Utility.nvl(ordineMissione.getUtilizzoTaxi()).equals("S") &&
        // Utility.nvl(ordineMissione.getUtilizzoAutoServizio()).equals("S")) ||
        // (Utility.nvl(ordineMissione.getUtilizzoTaxi()).equals("S") &&
        // Utility.nvl(ordineMissione.getUtilizzoAutoNoleggio()).equals("S"))){
        // throw new AwesomeException(CodiciErrore.ERRGEN,
        // CodiciErrore.DATI_INCONGRUENTI+": Scegliere solo un utilizzo
        // dell'auto ");
        // }
        if (ordineMissione.isFondiCompetenza() && !StringUtils.isEmpty(ordineMissione.getEsercizioObbligazione())
                && !StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione())
                && ordineMissione.getEsercizioObbligazione()
                .compareTo(ordineMissione.getEsercizioOriginaleObbligazione()) != 0) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Incongruenza tra fondi e esercizio obbligazione.");
        }
        if (ordineMissione.isFondiResiduo() && !StringUtils.isEmpty(ordineMissione.getEsercizioObbligazione())
                && !StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione())
                && ordineMissione.getEsercizioObbligazione()
                .compareTo(ordineMissione.getEsercizioOriginaleObbligazione()) <= 0) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Incongruenza tra fondi e esercizio obbligazione.");
        }
        if (ordineMissione.isTrattamentoAlternativoMissione()) {
            long oreDifferenza = ChronoUnit.HOURS.between(
                    ordineMissione.getDataInizioMissione().truncatedTo(ChronoUnit.MINUTES),
                    ordineMissione.getDataFineMissione().truncatedTo(ChronoUnit.MINUTES));
            if (oreDifferenza < 24) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Per il trattamento alternativo di missione è necessario avere una durata non inferiore a 24 ore.");
            }
        }
        if (Utility.nvl(ordineMissione.getMissioneGratuita()).equals("S") && ordineMissione.getImportoPresunto() != null
                && ordineMissione.getImportoPresunto().compareTo(BigDecimal.ZERO) != 0) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Non è possibile inserire una missione con spese a carico di altro ente e l'importo presunto");
        }
        if (!StringUtils.hasLength(ordineMissione.getMatricola())) {
            ordineMissione.setMatricola(null);
        }
        if (!updateOrdineMissione) {
            if (ordineMissione.getDataInizioMissione().isBefore(ZonedDateTime.now())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI
                        + ": La data di inizio missione non può essere precedente alla data di oggi");
            }
        }

        if (ordineMissione.getImportoPresunto() != null && ordineMissione.getImportoPresunto().compareTo(BigDecimal.ZERO) < 0) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "L'importo presunto non può essere negativo");
        }

        if (ordineMissione.getImportoPresunto() != null) {
            BigDecimal totaleSpesePresComp = ordineMissione.getTotaleSpesePresComplessivo();

            if (totaleSpesePresComp.compareTo(ordineMissione.getImportoPresunto()) > 0) {
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.ITALY);
                currencyFormatter.setMinimumFractionDigits(2);
                currencyFormatter.setMaximumFractionDigits(2);

                String totaleSpeseFormattato = currencyFormatter.format(totaleSpesePresComp);
                String importoPresuntoFormattato = currencyFormatter.format(ordineMissione.getImportoPresunto());

                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Il totale delle spese (" + totaleSpeseFormattato + ") non può superare l'importo presunto ("
                                + importoPresuntoFormattato + ").");
            }
        }


    }

    private void controlloDatiFinanziari(OrdineMissione ordineMissione) {
        UnitaOrganizzativa uo = unitaOrganizzativaService.loadUo(ordineMissione.getUoSpesa(),
                ordineMissione.getCdsSpesa(), ordineMissione.getAnno());
        if (uo == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI + ": La UO "
                    + ordineMissione.getUoSpesa() + " non è corretta rispetto al CDS " + ordineMissione.getCdsSpesa());
        }
        if (!StringUtils.isEmpty(ordineMissione.getCdrSpesa())) {
            Cdr cdr = cdrService.loadCdr(ordineMissione.getCdrSpesa(), ordineMissione.getUoSpesa());
            if (cdr == null) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        CodiciErrore.DATI_INCONGRUENTI + ": Il CDR " + ordineMissione.getCdrSpesa()
                                + " non è corretto rispetto alla UO " + ordineMissione.getUoSpesa());
            }
        }
        LocalDate data = LocalDate.now();
        int anno = data.getYear();
        /*if (!StringUtils.isEmpty(ordineMissione.getPgProgetto())) {
            Progetto progetto = progettoService.loadModulo(ordineMissione.getPgProgetto(), anno,
                    ordineMissione.getUoSpesa());
            if (progetto == null) {
                progetto = progettoService.loadModulo(ordineMissione.getPgProgetto(), ordineMissione.getAnno(),
                        ordineMissione.getUoSpesa());
                if (progetto == null) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI
                            + ": Il modulo indicato non è corretto rispetto alla UO " + ordineMissione.getUoSpesa());
                }
            }
        }*/
        if (!StringUtils.isEmpty(ordineMissione.getGae())) {
            if (StringUtils.isEmpty(ordineMissione.getCdrSpesa())) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Non è possibile indicare la GAE senza il centro di responsabilità");
            }
            Gae gae = gaeService.loadGae(ordineMissione);
            if (gae == null) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "La GAE " + ordineMissione.getGae() + " indicata non esiste");
            } else {
                boolean progettoCdrIndicato = false;
                /*if (!StringUtils.isEmpty(ordineMissione.getPgProgetto())
                        && !StringUtils.isEmpty(gae.getPg_progetto())) {
                    progettoCdrIndicato = true;
                    if (gae.getPg_progetto().compareTo(ordineMissione.getPgProgetto()) != 0) {
                        throw new AwesomeException(CodiciErrore.ERRGEN,
                                CodiciErrore.DATI_INCONGRUENTI + ": La GAE indicata " + ordineMissione.getGae()
                                        + " non corrisponde al modulo indicato.");
                    }
                }*/
                if (!StringUtils.isEmpty(ordineMissione.getCdrSpesa())) {
                    progettoCdrIndicato = true;
                    if (!gae.getCd_centro_responsabilita().equals(ordineMissione.getCdrSpesa())) {
                        throw new AwesomeException(CodiciErrore.ERRGEN,
                                CodiciErrore.DATI_INCONGRUENTI + ": La GAE indicata " + ordineMissione.getGae()
                                        + " non corrisponde con il CDR " + ordineMissione.getCdrSpesa() + " indicato.");
                    }
                }
                if (!progettoCdrIndicato) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI
                            + ": Non è possibile indicare solo La GAE senza il modulo o il CDR.");
                }
            }
        }

        if (!StringUtils.isEmpty(ordineMissione.getPgObbligazione())) {
            if (StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione())) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Oltre al numero dell'impegno è necessario indicare anche l'anno dell'impegno");
            }
            if (!StringUtils.isEmpty(ordineMissione.getGae())) {
                ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(ordineMissione);
                if (impegnoGae == null) {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            "L'impegno indicato " + ordineMissione.getEsercizioOriginaleObbligazione() + "-"
                                    + ordineMissione.getPgObbligazione() + " non corrisponde con la GAE "
                                    + ordineMissione.getGae() + " indicata oppure non esiste");
                } else {
                    if (!StringUtils.isEmpty(ordineMissione.getVoce())) {
                        if (!impegnoGae.getCdElementoVoce().equals(ordineMissione.getVoce())) {
                            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI
                                    + ": L'impegno indicato " + ordineMissione.getEsercizioOriginaleObbligazione() + "-"
                                    + ordineMissione.getPgObbligazione()
                                    + " non corrisponde con la voce di Bilancio indicata." + ordineMissione.getVoce());
                        }
                    } else {
                        ordineMissione.setVoce(impegnoGae.getCdElementoVoce());
                    }
                    ordineMissione.setCdCdsObbligazione(impegnoGae.getCdCds());
                    ordineMissione.setEsercizioObbligazione(impegnoGae.getEsercizio());
                }
            } else {
                Impegno impegno = impegnoService.loadImpegno(ordineMissione);
                if (impegno == null) {
                    throw new AwesomeException(CodiciErrore.ERRGEN,
                            "L'impegno indicato " + ordineMissione.getEsercizioOriginaleObbligazione() + "-"
                                    + ordineMissione.getPgObbligazione() + " non esiste");
                } else {
                    if (!StringUtils.isEmpty(ordineMissione.getVoce())) {
                        if (!impegno.getCdElementoVoce().equals(ordineMissione.getVoce())) {
                            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI
                                    + ": L'impegno indicato " + ordineMissione.getEsercizioOriginaleObbligazione() + "-"
                                    + ordineMissione.getPgObbligazione()
                                    + " non corrisponde con la voce di Bilancio indicata." + ordineMissione.getVoce());
                        }
                    } else {
                        ordineMissione.setVoce(impegno.getCdElementoVoce());
                    }
                    ordineMissione.setCdCdsObbligazione(impegno.getCdCds());
                    ordineMissione.setEsercizioObbligazione(impegno.getEsercizio());
                }
            }
        } else {
            if (!StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione())
                    && StringUtils.isEmpty(ordineMissione.getFondi())) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Oltre all'anno dell'impegno è necessario indicare anche il numero dell'impegno");
            }
            ordineMissione.setCdCdsObbligazione(null);
            ordineMissione.setEsercizioObbligazione(null);
        }

    }

    private void validaCRUD(OrdineMissione ordineMissione, boolean updateOrdineMissione) {
        if (ordineMissione != null) {

            controlloCampiObbligatori(ordineMissione);
            controlloCongruenzaDatiInseriti(ordineMissione, updateOrdineMissione);
            controlloDatiFinanziari(ordineMissione);
            DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(),
                    ordineMissione.getAnno());
            if (istituto == null) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Dati uo non presenti per il codice "
                        + ordineMissione.getUoSpesa() + " nell'anno " + ordineMissione.getAnno());
            }
        }
    }

    private void controlloCampiObbligatori(OrdineMissione ordineMissione) {
        // if (!ordineMissione.isToBeCreated()){
        controlloDatiObbligatoriDaGUI(ordineMissione);
        // }
        if (StringUtils.isEmpty(ordineMissione.getAnno())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Anno");
        } else if (StringUtils.isEmpty(ordineMissione.getObbligoRientro())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Obbligo di Rientro");
        } else if (StringUtils.isEmpty(ordineMissione.getUid())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Utente");
        /*} else if (StringUtils.isEmpty(ordineMissione.getUtilizzoTaxi())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Utilizzo del Taxi");
        } else if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoServizio())) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    CodiciErrore.CAMPO_OBBLIGATORIO + ": Utilizzo dell'auto di servizio");
        } else if (StringUtils.isEmpty(ordineMissione.getPersonaleAlSeguito())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Personale al seguito");
        } else if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoNoleggio())) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    CodiciErrore.CAMPO_OBBLIGATORIO + ": Utilizzo auto a noleggio");*/
        } else if (StringUtils.isEmpty(ordineMissione.getStato())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Stato");
        } else if (StringUtils.isEmpty(ordineMissione.getValidato())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Validato");
        } else if (StringUtils.isEmpty(ordineMissione.getNumero())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Numero");
        }

    }

    public List<CMISFileAttachment> getAttachments(Long idOrdineMissione) throws AwesomeException {
        if (idOrdineMissione == null) {
            return Collections.emptyList();
        }

        OrdineMissione ordineMissione =
                ordineMissioneRepository.findById(idOrdineMissione).orElse(null);

        if (ordineMissione == null) {
            return Collections.emptyList();
        }

        if (!isUserEnabledToViewMissione(ordineMissione)) {
            return Collections.emptyList();
        }

        if (ordineMissione.isMissioneInserita()) {
            return Collections.emptyList();
        }

        return cmisOrdineMissioneService.getAttachmentsOrdineMissione(
                ordineMissione,
                idOrdineMissione
        );
    }

    public CMISFileAttachment uploadAllegato(Long idOrdineMissione, InputStream inputStream,
                                             String name, MimeTypes mimeTypes) throws AwesomeException {
        OrdineMissione ordineMissione = ordineMissioneRepository.findById(idOrdineMissione).orElse(null);
        if (ordineMissione != null && isUserEnabledToViewMissione(ordineMissione)) {
            return cmisOrdineMissioneService.uploadAttachmentOrdineMissione(ordineMissione, idOrdineMissione,
                    inputStream, name, mimeTypes);
        }
        return null;
    }

    public void gestioneCancellazioneAllegati(String idNodo, Long idOrdineMissione) {
        if (idOrdineMissione != null) {
            OrdineMissione ordineMissione = ordineMissioneRepository.findById(idOrdineMissione).orElse(null);
            if (ordineMissione != null && StringUtils.hasLength(ordineMissione.getIdFlusso()) && isUserEnabledToViewMissione(ordineMissione)) {
                StorageObject folderOrdineMissione = cmisOrdineMissioneService.recuperoFolderOrdineMissione(ordineMissione);
                missioniCMISService.eliminaFilePresenteNelFlusso(idNodo, folderOrdineMissione);
            } else {
                missioniCMISService.deleteNode(idNodo);
            }
        }
    }

    public void popolaCoda(String id) {
        OrdineMissione missione = ordineMissioneRepository.findById(Long.valueOf(id)).orElse(null);
        popolaCoda(missione);
    }

    public void aggiornaOrdineMissioneApprovato(OrdineMissione ordineMissioneDaAggiornare) {
        ordineMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
        ordineMissioneDaAggiornare.setStato(Costanti.STATO_DEFINITIVO);
        gestioneEmailDopoApprovazione(ordineMissioneDaAggiornare);
//        OrdineMissioneAnticipo anticipo = getAnticipo(ordineMissioneDaAggiornare);
//        if (anticipo != null) {
//            anticipo.setStato(Costanti.STATO_DEFINITIVO);
//            ordineMissioneAnticipoService.updateAnticipo(anticipo, false);
//            DatiIstituto dati = datiIstitutoService.getDatiIstituto(ordineMissioneDaAggiornare.getUoSpesa(),
//                    ordineMissioneDaAggiornare.getAnno());
//            if (dati != null && dati.getMailNotifiche() != null) {
//                if (!dati.getMailNotifiche().equals("N")) {
//                    mailService.sendEmail(subjectAnticipo, getTextMailAnticipo(ordineMissioneDaAggiornare, anticipo), false,
//                            true, dati.getMailNotifiche());
//                }
//            } else {
//                Account account = accountService.loadAccountFromUsername(ordineMissioneDaAggiornare.getUid());
//                UsersSpecial richiedente = accountService.getUoForUsersSpecial(account.getUid());
//                List<UsersSpecial> lista = accountService
//                        .getUserSpecialForUoPerValidazione(ordineMissioneDaAggiornare.getUoSpesa());
//                aggiuntaRichMailList(lista,richiedente);
//                if (lista != null && lista.size() > 0) {
//                    mailService.sendEmail(subjectAnticipo, getTextMailAnticipo(ordineMissioneDaAggiornare, anticipo),
//                            false, true, mailService.prepareTo(lista));
//                }
//            }
//        }
        updateOrdineMissione(ordineMissioneDaAggiornare, true);
        popolaCoda(ordineMissioneDaAggiornare);
    }

    public List<StorageObject> getDocumentsOrdineMissione(OrdineMissione missione) throws AwesomeException {
        return cmisOrdineMissioneService.getAllDocumentsOrdineMissione(missione);
    }


    private String getTextMailToSendToValidator(String basePath, OrdineMissione ordineMissione) {
        String url = basePath + "/#/ordine-missione/" + ordineMissione.getId() + "/S";

        return "<p>L'ordine di missione <b>" + ordineMissione.getAnno() + "-" + ordineMissione.getNumero() + "</b> della UO "
                + ordineMissione.getUoRich() + " " + ordineMissione.getDatoreLavoroRich() + " di "
                + getNominativo(ordineMissione.getUid()) + " per la missione a <b>" + ordineMissione.getDestinazione()
                + "</b> dal " + DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " al "
                + DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()) + " avente per oggetto: <u>"
                + ordineMissione.getOggetto() + "</u> è stato inviato per la tua validazione (verifica/completamento dei dati finanziari).</p>"
                + "<p>Si prega di verificarlo attraverso il link: <a href='" + url + "'>Clicca qui per aprire</a></p>";
    }

    private String getTextMailSendToManager(String basePath, OrdineMissione ordineMissione) {
        String url = basePath + "/#/ordine-missione/" + ordineMissione.getId();

        return "<p>L'ordine di missione <b>" + ordineMissione.getAnno() + "-" + ordineMissione.getNumero() + "</b> di "
                + getNominativo(ordineMissione.getUid()) + " per la missione a <b>" + ordineMissione.getDestinazione()
                + "</b> dal " + DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " al "
                + DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()) + " avente per oggetto: <u>"
                + ordineMissione.getOggetto()
                + "</u> le è stata inviata per l'approvazione in quanto responsabile del gruppo.</p>"
                + "<p>Si prega di confermarlo attraverso il link: <a href='" + url + "'>Clicca qui per aprire</a></p>";
    }

    private String getTextMailApprovazioneOrdine(OrdineMissione ordineMissione, boolean missioneConAnticipo) {
        OrdineMissioneAnticipo anticipo = ordineMissioneAnticipoService.getAnticipo((Long) ordineMissione.getId());
        StringBuilder testoMail = new StringBuilder();

        testoMail.append("<p>L'ordine di missione ")
                .append("<b>").append(ordineMissione.getAnno()).append("-").append(ordineMissione.getNumero()).append("</b>")
                .append(" di ").append(getNominativo(ordineMissione.getUid()))
                .append(" per la missione a <b>").append(ordineMissione.getDestinazione()).append("</b>")
                .append(" dal ").append(DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()))
                .append(" al ").append(DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()))
                .append(" avente per oggetto: <u>").append(ordineMissione.getOggetto()).append("</u>");

        if (missioneConAnticipo) {
            testoMail.append(" è stata approvata con una richiesta di anticipo di € ")
                    .append(Utility.numberFormat(anticipo.getImporto())).append(".</p>");
        } else {
            testoMail.append(" è stata approvata.</p>");
        }
        return testoMail.toString();
    }

    private String getTextMailApprovazioneAnnullamentoOrdine(OrdineMissione ordineMissione) {
        return "<p>L'ordine di missione <b>" + ordineMissione.getAnno() + "-" + ordineMissione.getNumero() + "</b> di "
                + getNominativo(ordineMissione.getUid()) + " per la missione a <b>" + ordineMissione.getDestinazione()
                + "</b> dal " + DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " al "
                + DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()) + " avente per oggetto: <u>"
                + ordineMissione.getOggetto() + "</u> è stato annullato.</p>";
    }

// private String getTextMailAnticipo(OrdineMissione ordineMissione, OrdineMissioneAnticipo anticipo) {
//     return "<p>È stata approvata la richiesta di anticipo di € " + Utility.numberFormat(anticipo.getImporto())
//             + " relativa all'ordine di missione <b>" + ordineMissione.getAnno() + "-" + ordineMissione.getNumero()
//             + "</b> di " + getNominativo(ordineMissione.getUid()) + " per la missione a <b>"
//             + ordineMissione.getDestinazione() + "</b> dal "
//             + DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " al "
//             + DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()) + " avente per oggetto <i>"
//             + ordineMissione.getOggetto() + "</i>.</p>";
// }

    private String getTextMailSendToAdministrative(String basePath, OrdineMissione ordineMissione) {
        String url = basePath + "/#/ordine-missione/" + ordineMissione.getId() + "/S";

        return "<p>L'ordine di missione <b>" + ordineMissione.getAnno() + "-" + ordineMissione.getNumero() + "</b> della UO "
                + ordineMissione.getUoRich() + " " + ordineMissione.getDatoreLavoroRich() + " di "
                + getNominativo(ordineMissione.getUid()) + " per la missione a <b>" + ordineMissione.getDestinazione()
                + "</b> dal " + DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " al "
                + DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()) + " avente per oggetto: <u>"
                + ordineMissione.getOggetto() + "</u> è stato inviato per la verifica/completamento dei dati finanziari.</p>"
                + "<p>Si prega di verificarlo attraverso il link: <a href='" + url + "'>Clicca qui per aprire</a></p>";
    }

    private String getTextMailReturnToSender(String basePath, OrdineMissione ordineMissione) {
        String url = basePath + "/#/ordine-missione/" + ordineMissione.getId();

        return "<p>L'ordine di missione <b>" + ordineMissione.getAnno() + "-" + ordineMissione.getNumero() + "</b> di "
                + getNominativo(ordineMissione.getUid()) + " per la missione a <b>" + ordineMissione.getDestinazione()
                + "</b> dal " + DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " al "
                + DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()) + " avente per oggetto: <u>"
                + ordineMissione.getOggetto() + "</u> le è stata respinto da " + getNominativo(securityService.getCurrentUserLogin())
                + " per il seguente motivo: <i>" + ordineMissione.getNoteRespingi() + "</i>.</p>"
                + "<p>Si prega di effettuare le opportune correzioni attraverso il link: <a href='" + url + "'>Clicca qui per aprire</a></p>";
    }

    // Se il valore attuale è zero, prova con i valori aggiornati; altrimenti mantiene o aggiorna se disponibile.
    private BigDecimal calcolaTot(BigDecimal dbTotOrdine, BigDecimal calcTotOrdine, BigDecimal toUpdateTotOrdine) {

        if (dbTotOrdine.compareTo(BigDecimal.ZERO) == 0) {
            return calcTotOrdine.compareTo(BigDecimal.ZERO) > 0 ? calcTotOrdine :
                    toUpdateTotOrdine.compareTo(BigDecimal.ZERO) > 0 ? toUpdateTotOrdine :
                            BigDecimal.ZERO;
        } else {
            return toUpdateTotOrdine.compareTo(BigDecimal.ZERO) > 0 ? toUpdateTotOrdine : dbTotOrdine;
        }
    }
}
