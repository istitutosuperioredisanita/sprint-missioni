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

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.cmis.ResultFlows;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.spring.storage.StorageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlowsMissioniService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneService.class);
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
    @Autowired
    private Environment env;
    @Autowired
    private CRUDComponentSession crudServiceBean;
    @Value("${spring.proxy.vecchiaScrivania.url}")
    private String urlVecchiaScrivania;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaOrdineMissioneFlowsNewTransaction(Serializable idOrdineMissione) throws Exception {
        return aggiornaOrdineMissioneFlows(idOrdineMissione);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaAnnullamentoOrdineMissioneFlowsNewTransaction(Serializable idAnnullamentoMissione) throws Exception {
        return aggiornaAnnullamentoOrdineMissioneFlows(idAnnullamentoMissione);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaRimborsoMissioneFlowsNewTransaction(Serializable idRimborsoMissione) throws Exception {
        return aggiornaRimborsoMissioneFlows(idRimborsoMissione);
    }

    public ResultFlows aggiornaOrdineMissioneFlows(Serializable idOrdineMissione)
            throws Exception {
        if (idOrdineMissione != null) {
            OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, idOrdineMissione);
            if (ordineMissione.isStatoInviatoAlFlusso() && !ordineMissione.isMissioneDaValidare()) {
                ResultFlows result = retrieveDataFromFlows(ordineMissione);
                if (result == null) {
                    return null;
                }
                if (result.isApprovato()) {
                    log.info("Trovato in Scrivania Digitale un ordine di missione con id {} della uo {}, anno {}, numero {} approvato.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
                    ordineMissioneService.aggiornaOrdineMissioneApprovato(ordineMissione);
                    return result;
                } else if (result.isAnnullato()) {
                    log.info("Trovato in Scrivania Digitale un ordine di missione con id {} della uo {}, anno {}, numero {} annullato.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
                    ordineMissioneService.aggiornaOrdineMissioneAnnullato(ordineMissione);
                    return result;
                } else if (result.isStateReject()) {
                    log.info("Trovato in Scrivania Digitale un ordine di missione con id {} della uo {}, anno {}, numero {} respinto.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
                    ordineMissioneService.aggiornaOrdineMissioneAnnullato(ordineMissione);
                    Account account = accountService.loadAccountFromUsername(ordineMissione.getUid());
                    String subject = "Ordine di missione respinto ed annullato.";
                    String text = "L'ordine di missione " + ordineMissione.getAnno() + "/" + ordineMissione.getNumero() + " di " + account.getCognome() + " " + account.getNome() + " a " + ordineMissione.getDestinazione() + " per " + ordineMissione.getOggetto() + " è stato respinto ed annullato con la seguente motivazione: " + result.getComment() + ". Nel caso la richiesta di missione fosse ancora valida sarà necessario inserirla nuovamente.";
                    sendMailFlussoRespintoVecchiaScrivania(ordineMissione.getUid(), ordineMissione.getUidInsert(), ordineMissione.getUtuv(), subject, text);
                    return result;
                }
            }
        }
        return null;
    }

    private ResultFlows retrieveDataFromFlows(OrdineMissione ordineMissione)
            throws ComponentException {
        StorageObject storage = null;
        try {
            storage = cmisOrdineMissioneService.getStorageObjectOrdineMissione(ordineMissione);
        } catch (ComponentException e1) {
            throw new ComponentException("Errore nel recupero del contenuto del file sul documentale ("
                    + Utility.getMessageException(e1) + ")", e1);
        }
        return getResultFlows(storage);
    }

    private ResultFlows retrieveDataFromFlows(AnnullamentoOrdineMissione annullamento)
            throws ComponentException {
        StorageObject storage = null;
        try {
            storage = cmisOrdineMissioneService.getStorageAnnullamentoOrdineMissione(annullamento);
        } catch (ComponentException e1) {
            throw new ComponentException("Errore nel recupero del contenuto del file sul documentale ("
                    + Utility.getMessageException(e1) + ")", e1);
        }
        return getResultFlows(storage);
    }

    private ResultFlows retrieveDataFromFlows(RimborsoMissione rimborsoMissione)
            throws ComponentException {
        StorageObject storage = null;
        try {
            storage = cmisRimborsoMissioneService.getStorageRimborsoMissione(rimborsoMissione);
        } catch (ComponentException e1) {
            throw new ComponentException("Errore nel recupero del contenuto del file sul documentale ("
                    + Utility.getMessageException(e1) + ")", e1);
        }
        return getResultFlows(storage);
    }

    private ResultFlows getResultFlows(StorageObject storage) {
        ResultFlows result = new ResultFlows();
        if (storage != null) {
            result.setState(storage.getPropertyValue("wfcnr:statoFlusso"));
            result.setComment(storage.getPropertyValue("wfcnr:commentoFirma"));
        } else {
            return null;
        }
        return result;
    }

    public ResultFlows aggiornaAnnullamentoOrdineMissioneFlows(Serializable idAnnullamento)
            throws Exception {
        if (idAnnullamento != null) {
            AnnullamentoOrdineMissione annullamento = (AnnullamentoOrdineMissione) crudServiceBean.findById(AnnullamentoOrdineMissione.class, idAnnullamento);
            if (annullamento.isStatoInviatoAlFlusso() && !annullamento.isMissioneDaValidare()) {
                log.info("Trovato un annullamento ordine di missione da approvare con id {}.", annullamento.getId());
                ResultFlows result = retrieveDataFromFlows(annullamento);
                if (result == null) {
                    return null;
                }
                if (result.isApprovato()) {
                    OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, annullamento.getOrdineMissione().getId());
                    log.info("Trovato in Scrivania Digitale un annullamento ordine di missione con id {} della uo {}, anno {}, numero {} approvato.", annullamento.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
                    annullamentoOrdineMissioneService.aggiornaAnnullamentoOrdineMissioneFirmato(annullamento);
                    return result;
                } else if (result.isAnnullato()) {
                    OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, annullamento.getOrdineMissione().getId());
                    log.info("Trovato in Scrivania Digitale un annullamento ordine di missione con id {} della uo {}, anno {}, numero {} annullato.", annullamento.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
                    annullamentoOrdineMissioneService.aggiornaAnnullamentoOrdineMissioneAnnullato(result, annullamento);
                    return result;
                } else if (result.isStateReject()) {
                    OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, annullamento.getOrdineMissione().getId());
                    log.info("Trovato in Scrivania Digitale un annullamento ordine di missione con id {} della uo {}, anno {}, numero {} respinto.", annullamento.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
                    annullamentoOrdineMissioneService.aggiornaAnnullamentoOrdineMissioneAnnullato(result, annullamento);
                    Account account = accountService.loadAccountFromUsername(ordineMissione.getUid());
                    String subject = "Annullamento Ordine di missione respinto ed annullato.";
                    String text = "L'Annullamento ordine di missione " + ordineMissione.getAnno() + "/" + ordineMissione.getNumero() + " di " + account.getCognome() + " " + account.getNome() + " a " + ordineMissione.getDestinazione() + " per " + ordineMissione.getOggetto() + " è stato respinto ed annullato con la seguente motivazione: " + result.getComment() + ". Nel caso la richiesta di annullamento missione fosse ancora valida sarà necessario inserirla nuovamente.";
                    sendMailFlussoRespintoVecchiaScrivania(annullamento.getUid(), annullamento.getUidInsert(), annullamento.getUtuv(), subject, text);
                    return result;
                }
            }
        }
        return null;
    }

    private boolean isDevProfile() {
        return env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaRimborsoMissioneFlows(Serializable idRimborsoMissione) throws Exception {
        if (idRimborsoMissione != null) {
            RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById(RimborsoMissione.class, idRimborsoMissione);
            if (rimborsoMissione.isStatoInviatoAlFlusso() && !rimborsoMissione.isMissioneDaValidare()) {
                ResultFlows result = retrieveDataFromFlows(rimborsoMissione);
                if (result == null) {
                    return null;
                }
                if (result.isApprovato()) {
                    log.info("Trovato in Scrivania Digitale un rimborso missione con id {} della uo {}, anno {}, numero {} approvato.", rimborsoMissione.getId(), rimborsoMissione.getUoRich(), rimborsoMissione.getAnno(), rimborsoMissione.getNumero());
                    rimborsoMissioneService.aggiornaRimborsoMissioneFirmato(rimborsoMissione);
                    return result;
                } else if (result.isAnnullato()) {
                    log.info("Trovato in Scrivania Digitale un rimborso missione con id {} della uo {}, anno {}, numero {} annullato.", rimborsoMissione.getId(), rimborsoMissione.getUoRich(), rimborsoMissione.getAnno(), rimborsoMissione.getNumero());
                    rimborsoMissioneService.aggiornaRimborsoMissioneAnnullato(rimborsoMissione);
                    return result;
                } else if (result.isStateReject()) {
                    log.info("Trovato in Scrivania Digitale un rimborso missione con id {} della uo {}, anno {}, numero {} respinto.", rimborsoMissione.getId(), rimborsoMissione.getUoRich(), rimborsoMissione.getAnno(), rimborsoMissione.getNumero());
                    rimborsoMissioneService.aggiornaRimborsoMissioneAnnullato(rimborsoMissione);
                    Account account = accountService.loadAccountFromUsername(rimborsoMissione.getUid());
                    String subject = "Rimborso missione respinto ed annullato.";
                    String text = "Il rimborso missione " + rimborsoMissione.getAnno() + "/" + rimborsoMissione.getNumero() + " di " + account.getCognome() + " " + account.getNome() + " a " + rimborsoMissione.getDestinazione() + " per " + rimborsoMissione.getOggetto() + " è stato respinto ed annullato con la seguente motivazione: " + result.getComment() + ". Nel caso la richiesta di rimborso missione fosse ancora valida sarà necessario inserirla nuovamente.";
                    sendMailFlussoRespintoVecchiaScrivania(rimborsoMissione.getUid(), rimborsoMissione.getUidInsert(), rimborsoMissione.getUtuv(), subject, text);
                    return result;
                }
            }
        }
        return null;
    }

    private void sendMailFlussoRespintoVecchiaScrivania(String uid, String uidInsert, String utuv, String subject, String text) {
        List<String> listaMail = new ArrayList<>();

        String mailUid = getEmailUser(uid);

        aggiungiMail(listaMail, mailUid);
        String mailUidInsert = getEmailUser(uidInsert);
        aggiungiMail(listaMail, mailUidInsert);
        String mailUtuv = getEmailUser(utuv);
        aggiungiMail(listaMail, mailUtuv);
        if (listaMail.size() > 0) {
            String[] mailsTo = mailService.preparaElencoMail(listaMail);
            mailService.sendEmail(subject, text, false, true, mailsTo);
        }
    }

    private String getEmailUser(String uid) {
        if (!uid.equals(Costanti.USER_CRON_MISSIONI)) {
            return accountService.getEmail(uid);
        }
        return null;
    }

    private void aggiungiMail(List<String> listaMail, String mailUid) {
        if (mailUid != null) {
            listaMail.add(mailUid);
        }
    }


}
