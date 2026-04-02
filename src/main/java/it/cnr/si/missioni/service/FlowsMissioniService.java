package it.cnr.si.missioni.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.cmis.ResultFlows;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.repository.AnnullamentoOrdineMissioneRepository;
import it.cnr.si.missioni.repository.OrdineMissioneRepository;
import it.cnr.si.missioni.repository.RimborsoMissioneRepository;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.spring.storage.StorageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlowsMissioniService {

    private final Logger log = LoggerFactory.getLogger(FlowsMissioniService.class);

    @Autowired
    OrdineMissioneService ordineMissioneService;

    @Autowired
    AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;

    @Autowired
    RimborsoMissioneService rimborsoMissioneService;

    @Autowired
    CMISOrdineMissioneService cmisOrdineMissioneService;

    @Autowired
    CMISRimborsoMissioneService cmisRimborsoMissioneService;

    @Autowired
    AccountService accountService;

    @Autowired(required = false)
    MailService mailService;

    @Autowired
    ProxyService proxyService;

//    @Autowired
//    private Environment env;

    @Autowired
    OrdineMissioneRepository ordineMissioneRepository;

    @Autowired
    AnnullamentoOrdineMissioneRepository annOrdMissioneRepository;

    @Autowired
    RimborsoMissioneRepository rimborsoMissioneRepository;


    /*
     * ------------------------------------------------
     * TRANSACTION WRAPPER
     * ------------------------------------------------
     */

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaOrdineMissioneFlowsNewTransaction(Serializable id) throws Exception {
        return aggiornaOrdineMissioneFlows(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaAnnullamentoOrdineMissioneFlowsNewTransaction(Serializable id) throws Exception {
        return aggiornaAnnullamentoOrdineMissioneFlows(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaRimborsoMissioneFlowsNewTransaction(Serializable id) throws Exception {
        return aggiornaRimborsoMissioneFlows(id);
    }


    /*
     * ------------------------------------------------
     * ORDINE MISSIONE
     * ------------------------------------------------
     */

    @Transactional
    public ResultFlows aggiornaOrdineMissioneFlows(Serializable id) throws Exception {

        OrdineMissione ordineMissione = ordineMissioneRepository
                .findById((Long) id)
                .orElse(null);

        if (!isProcessabile("OrdineMissione", id, ordineMissione,
                ordineMissione != null && ordineMissione.isStatoInviatoAlFlusso(),
                ordineMissione != null && ordineMissione.isMissioneDaValidare()))
            return null;

        ResultFlows result = retrieveDataFromFlows(ordineMissione);
        if (result == null) return null;

        if (result.isApprovato()) {

            ordineMissioneService.aggiornaOrdineMissioneApprovato(ordineMissione);

        } else if (result.isAnnullato()) {

            ordineMissioneService.aggiornaOrdineMissioneAnnullato(ordineMissione);

        } else if (result.isStateReject()) {

            ordineMissioneService.aggiornaOrdineMissioneAnnullato(ordineMissione);

            inviaMailReject(
                    ordineMissione.getUid(),
                    ordineMissione.getUidInsert(),
                    ordineMissione.getUtuv(),
                    ordineMissione.getAnno(),
                    Math.toIntExact(ordineMissione.getNumero()),
                    ordineMissione.getDestinazione(),
                    ordineMissione.getOggetto(),
                    result.getComment(),
                    "Ordine di missione",
                    ordineMissione.getUid()
            );
        }

        return result;
    }


    /*
     * ------------------------------------------------
     * ANNULLAMENTO ORDINE
     * ------------------------------------------------
     */

    @Transactional
    public ResultFlows aggiornaAnnullamentoOrdineMissioneFlows(Serializable id) throws Exception {

        AnnullamentoOrdineMissione annullamento =
                annOrdMissioneRepository.findById((Long) id)
                        .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                                "AnnullamentoOrdineMissione con ID " + id + " non trovato."));

        if (!isProcessabile("AnnullamentoOrdineMissione", id, annullamento,
                annullamento.isStatoInviatoAlFlusso(),
                annullamento.isMissioneDaValidare()))
            return null;

        ResultFlows result = retrieveDataFromFlows(annullamento);
        if (result == null) return null;

        OrdineMissione ordineMissione = ordineMissioneRepository
                .findById((Long) annullamento.getOrdineMissione().getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "OrdineMissione con ID " + annullamento.getOrdineMissione().getId() + " non trovato."));

        if (result.isApprovato()) {
            annullamentoOrdineMissioneService.aggiornaAnnullamentoOrdineMissioneFirmato(annullamento);
        } else if (result.isAnnullato()) {
            annullamentoOrdineMissioneService.aggiornaAnnullamentoOrdineMissioneAnnullato(result, annullamento);
        } else if (result.isStateReject()) {
            annullamentoOrdineMissioneService.aggiornaAnnullamentoOrdineMissioneAnnullato(result, annullamento);

            inviaMailReject(
                    annullamento.getUid(),
                    annullamento.getUidInsert(),
                    annullamento.getUtuv(),
                    ordineMissione.getAnno(),
                    Math.toIntExact(ordineMissione.getNumero()),
                    ordineMissione.getDestinazione(),
                    ordineMissione.getOggetto(),
                    result.getComment(),
                    "Annullamento ordine missione",
                    ordineMissione.getUid()
            );
        }

        return result;
    }

    /*
     * ------------------------------------------------
     * RIMBORSO MISSIONE
     * ------------------------------------------------
     */
    @Transactional
    public ResultFlows aggiornaRimborsoMissioneFlows(Serializable id) throws Exception {

        RimborsoMissione rimborso =
                rimborsoMissioneRepository.findById((Long) id)
                        .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                                "RimborsoMissione con ID " + id + " non trovato."));

        if (!isProcessabile("RimborsoMissione", id, rimborso,
                rimborso.isStatoInviatoAlFlusso(),
                rimborso.isMissioneDaValidare()))
            return null;

        ResultFlows result = retrieveDataFromFlows(rimborso);
        if (result == null) return null;

        if (result.isApprovato()) {
            rimborsoMissioneService.aggiornaRimborsoMissioneFirmato(rimborso);
        } else if (result.isAnnullato() || result.isStateReject()) {
            rimborsoMissioneService.aggiornaRimborsoMissioneAnnullato(rimborso);

            if (result.isStateReject()) {
                inviaMailReject(
                        rimborso.getUid(),
                        rimborso.getUidInsert(),
                        rimborso.getUtuv(),
                        rimborso.getAnno(),
                        Math.toIntExact(rimborso.getNumero()),
                        rimborso.getDestinazione(),
                        rimborso.getOggetto(),
                        result.getComment(),
                        "Rimborso missione",
                        rimborso.getUid()
                );
            }
        }

        return result;
    }

    /*
     * ------------------------------------------------
     * FLOWS
     * ------------------------------------------------
     */

    private ResultFlows retrieveDataFromFlows(OrdineMissione ordineMissione)
            throws AwesomeException {

        StorageObject storage =
                cmisOrdineMissioneService.getStorageObjectOrdineMissione(ordineMissione);

        return getResultFlows(storage);
    }

    private ResultFlows retrieveDataFromFlows(AnnullamentoOrdineMissione annullamento)
            throws AwesomeException {

        StorageObject storage =
                cmisOrdineMissioneService.getStorageAnnullamentoOrdineMissione(annullamento);

        return getResultFlows(storage);
    }

    private ResultFlows retrieveDataFromFlows(RimborsoMissione rimborso)
            throws AwesomeException {

        StorageObject storage =
                cmisRimborsoMissioneService.getStorageRimborsoMissione(rimborso);

        return getResultFlows(storage);
    }


    private ResultFlows getResultFlows(StorageObject storage) {

        if (storage == null) return null;

        ResultFlows result = new ResultFlows();

        result.setState(storage.getPropertyValue("wfcnr:statoFlusso"));
        result.setComment(storage.getPropertyValue("wfcnr:commentoFirma"));

        return result;
    }


    /*
     * ------------------------------------------------
     * UTIL
     * ------------------------------------------------
     */

    private boolean isProcessabile(String nome, Serializable id, Object entity,
                                   boolean inviato, boolean daValidare) {

        if (id == null) {
            log.warn("ID {} non fornito.", nome);
            return false;
        }

        if (entity == null) {
            log.warn("{} non trovato per ID {}", nome, id);
            return false;
        }

        if (!inviato || daValidare) {
            log.info("{} ID {} non in stato da processare.", nome, id);
            return false;
        }

        return true;
    }


    private void inviaMailReject(String uid,
                                 String uidInsert,
                                 String utuv,
                                 int anno,
                                 int numero,
                                 String destinazione,
                                 String oggetto,
                                 String comment,
                                 String tipo,
                                 String uidAccount) {

        Account account = accountService.loadAccountFromUsername(uidAccount);

        if (account == null) return;

        String subject = tipo + " respinto ed annullato.";

        String text = String.format(
                "Il %s %d/%d di %s %s a %s per %s è stato respinto ed annullato con la seguente motivazione: %s.",
                tipo,
                anno,
                numero,
                account.getCognome(),
                account.getNome(),
                destinazione,
                oggetto,
                comment
        );

        sendMailFlussoRespintoVecchiaScrivania(uid, uidInsert, utuv, subject, text);
    }


    private void sendMailFlussoRespintoVecchiaScrivania(String uid,
                                                        String uidInsert,
                                                        String utuv,
                                                        String subject,
                                                        String text) {

        List<String> listaMail = new ArrayList<>();

        aggiungiMail(listaMail, getEmailUser(uid));
        aggiungiMail(listaMail, getEmailUser(uidInsert));
        aggiungiMail(listaMail, getEmailUser(utuv));

        if (!listaMail.isEmpty()) {

            String[] mailsTo = mailService.preparaElencoMail(listaMail);

            mailService.sendEmail(
                    subject,
                    text,
                    false,
                    true,
                    mailsTo
            );
        }
    }


    private String getEmailUser(String uid) {

        if (!uid.equals(Costanti.USER_CRON_MISSIONI)) {
            return accountService.getEmail(uid);
        }

        return null;
    }


    private void aggiungiMail(List<String> listaMail, String mail) {

        if (mail != null) {
            listaMail.add(mail);
        }
    }

}