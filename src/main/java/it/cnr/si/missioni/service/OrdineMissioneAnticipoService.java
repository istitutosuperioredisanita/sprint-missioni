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
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAnticipo;
import it.cnr.si.missioni.repository.OrdineMissioneAnticipoRepository;
import it.cnr.si.missioni.repository.OrdineMissioneRepository;
import it.cnr.si.missioni.service.security.SecurityService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.spring.storage.StorageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.OptimisticLockException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for managing users.
 */
@Service
public class OrdineMissioneAnticipoService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneAnticipoService.class);

    @Autowired
    private OrdineMissioneAnticipoRepository ordineMissioneAnticipoRepository;

    @Autowired
    private MissioniCMISService missioniCMISService;

    @Autowired
    @Lazy
    private CMISOrdineMissioneService cmisOrdineMissioneService;

    @Autowired
    private PrintOrdineMissioneAnticipoService printOrdineMissioneAnticipoService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private OrdineMissioneRepository ordineMissioneRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UoService uoService;

    @Transactional(readOnly = true)
    public OrdineMissioneAnticipo getAnticipo(Long idMissione, Boolean valorizzaOrdineMissione)
            throws AwesomeException {
        OrdineMissione ordineMissione = ordineMissioneRepository.findById(idMissione).orElse(null);

        if (ordineMissione != null) {
            return getAnticipo(ordineMissione, valorizzaOrdineMissione);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public OrdineMissioneAnticipo getAnticipo(OrdineMissione ordineMissione, Boolean valorizzaOrdineMissione)
            throws AwesomeException {
        if (ordineMissione != null) {
            OrdineMissioneAnticipo anticipo = ordineMissioneAnticipoRepository.getAnticipo(ordineMissione);
            if (anticipo != null && valorizzaOrdineMissione) {
                anticipo.setOrdineMissione(ordineMissione);
            }
            return anticipo;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public OrdineMissioneAnticipo getAnticipo(Long idOrdineMissione)
            throws AwesomeException {
        OrdineMissioneAnticipo anticipo = ordineMissioneAnticipoRepository.getAnticipo(idOrdineMissione);
        return anticipo;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAnticipo createAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo)
            throws AwesomeException, OptimisticLockException {

        if (ordineMissioneAnticipo == null || ordineMissioneAnticipo.getOrdineMissione() == null) {
            throw new AwesomeException("OrdineMissioneAnticipo o OrdineMissione non validi");
        }

        Long idMissione = (Long) ordineMissioneAnticipo.getOrdineMissione().getId();

        OrdineMissione ordineMissione = ordineMissioneRepository
                .findById(idMissione)
                .orElseThrow(() -> new AwesomeException("Ordine missione non trovata: " + idMissione));

        ordineMissioneAnticipo.setUid(securityService.getCurrentUserLogin());
        ordineMissioneAnticipo.setUser(securityService.getCurrentUserLogin());
        ordineMissioneAnticipo.setOrdineMissione(ordineMissione);
        ordineMissioneAnticipo.setStato(Costanti.STATO_INSERITO);
        ordineMissioneAnticipo.setStatoFlusso(Costanti.STATO_INSERITO);
        ordineMissioneAnticipo.setDataRichiesta(DateUtils.getCurrentLocalDateTime());        ordineMissioneAnticipo.setToBeCreated();
        ordineMissioneAnticipo = ordineMissioneAnticipoRepository.save(ordineMissioneAnticipo);
        log.debug("Created Information for OrdineMissioneAnticipo: {}", ordineMissioneAnticipo);

        return ordineMissioneAnticipo;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAnticipo updateAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo)
            throws AwesomeException, OptimisticLockException {
        return updateAnticipo(ordineMissioneAnticipo, false);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAnticipo updateAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo,
                                                 Boolean confirm) throws AwesomeException {

        // Recupera l'anticipo dal DB
        OrdineMissioneAnticipo ordineMissioneAnticipoDB = ordineMissioneAnticipoRepository
                .findById((Long) ordineMissioneAnticipo.getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "Anticipo di Ordine di Missione da aggiornare inesistente."));

        if (confirm) {
            if (ordineMissioneAnticipoDB.isAnticipoConfermato()) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Non è possibile rendere definitivo l'anticipo. È già stato avviato il flusso di approvazione.");
            }
            if (ordineMissioneAnticipoDB.getOrdineMissione().isMissioneInserita()) {
                throw new AwesomeException(CodiciErrore.ERRGEN,
                        "Non è possibile rendere definitivo l'anticipo. È necessario prima avviare il flusso di approvazione per il relativo ordine di missione.");
            }
            ordineMissioneAnticipoDB.setStato(Costanti.STATO_CONFERMATO);
            ordineMissioneAnticipoDB.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        } else {
            // Aggiorna solo campi modificabili
            ordineMissioneAnticipoDB.setStato(ordineMissioneAnticipo.getStato());
            ordineMissioneAnticipoDB.setStatoFlusso(ordineMissioneAnticipo.getStatoFlusso());
            ordineMissioneAnticipoDB.setImporto(ordineMissioneAnticipo.getImporto());
            ordineMissioneAnticipoDB.setNote(ordineMissioneAnticipo.getNote());
        }

        ordineMissioneAnticipoDB.setToBeUpdated();

        if (confirm) {
            avviaFlusso(ordineMissioneAnticipoDB);
        }

        ordineMissioneAnticipoDB = ordineMissioneAnticipoRepository.save(ordineMissioneAnticipoDB);
        log.debug("Updated Information for Anticipo Ordine di Missione: {}", ordineMissioneAnticipoDB);

        return ordineMissioneAnticipoDB;
    }

    /**
     * Salvataggio dei dati del mandato dell'anticipo, consentito solo ai validatori della uo spesa
     * sull'ordine firmato e definitivo. In bozza resta modificabile, in definitivo no.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAnticipo salvaMandato(OrdineMissioneAnticipo datiMandato, Boolean definitivo) throws AwesomeException {
        if (datiMandato == null || datiMandato.getId() == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Anticipo dell'Ordine di Missione non indicato.");
        }
        OrdineMissioneAnticipo anticipoDB = ordineMissioneAnticipoRepository
                .findById((Long) datiMandato.getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Anticipo dell'Ordine di Missione inesistente."));
        if (Costanti.STATO_ANNULLATO.equals(anticipoDB.getStato())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "La richiesta di anticipo risulta annullata.");
        }
        OrdineMissione ordineMissione = ordineMissioneRepository
                .findById((Long) anticipoDB.getOrdineMissione().getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Ordine di Missione non trovato."));
        if (!Boolean.TRUE.equals(ordineMissione.isStatoFlussoApprovato())
                || !Costanti.STATO_DEFINITIVO.equals(ordineMissione.getStato())) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "I dati del mandato possono essere inseriti solo sull'ordine di missione firmato e definitivo.");
        }
        if (!isUtenteValidatore(securityService.getCurrentUserLogin(), ordineMissione.getUoSpesa())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Utente non abilitato a inserire i dati del mandato.");
        }
        if (Boolean.TRUE.equals(anticipoDB.isMandatoDefinitivo())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "I dati del mandato sono già stati salvati in definitivo.");
        }

        anticipoDB.setModpag(datiMandato.getModpag());
        anticipoDB.setTipoPagamento(datiMandato.getTipoPagamento());
        anticipoDB.setPgBanca(datiMandato.getPgBanca());
        anticipoDB.setIban(datiMandato.getIban());
        anticipoDB.setAnticipoRicevuto(Utility.nvl(datiMandato.getAnticipoRicevuto(), "N"));
        anticipoDB.setAnticipoAnnoMandato(datiMandato.getAnticipoAnnoMandato());
        anticipoDB.setAnticipoNumeroMandato(datiMandato.getAnticipoNumeroMandato());
        anticipoDB.setAnticipoImporto(datiMandato.getAnticipoImporto());

        if (Boolean.TRUE.equals(definitivo)) {
            controlloDatiMandato(anticipoDB);
            anticipoDB.setStatoMandato(Costanti.STATO_DEFINITIVO);
        } else {
            anticipoDB.setStatoMandato(Costanti.STATO_INSERITO);
        }
        anticipoDB.setUidMandato(securityService.getCurrentUserLogin());
        anticipoDB.setDataMandato(DateUtils.getCurrentLocalDateTime());
        anticipoDB.setToBeUpdated();
        anticipoDB = ordineMissioneAnticipoRepository.save(anticipoDB);
        log.info("Salvati in {} i dati del mandato dell'anticipo {}",
                Boolean.TRUE.equals(definitivo) ? "definitivo" : "bozza", anticipoDB.getId());
        return anticipoDB;
    }

    private void controlloDatiMandato(OrdineMissioneAnticipo anticipo) throws AwesomeException {
        if (!StringUtils.hasLength(anticipo.getModpag())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Modalità di Pagamento");
        }
        if (anticipo.getPgBanca() == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Dati Modalità di Pagamento");
        }
        if (Costanti.TIPO_PAGAMENTO_BONIFICO.equals(anticipo.getModpag()) && !StringUtils.hasLength(anticipo.getIban())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Codice IBAN");
        }
        boolean qualcheDato = "S".equals(anticipo.getAnticipoRicevuto()) || anticipo.getAnticipoAnnoMandato() != null
                || anticipo.getAnticipoNumeroMandato() != null || anticipo.getAnticipoImporto() != null;
        boolean tuttiIDati = "S".equals(anticipo.getAnticipoRicevuto()) && anticipo.getAnticipoAnnoMandato() != null
                && anticipo.getAnticipoNumeroMandato() != null && anticipo.getAnticipoImporto() != null;
        if (qualcheDato && !tuttiIDati) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "E' necessario indicare tutti i campi dell'anticipo ricevuto e del mandato, oppure nessuno.");
        }
    }

    /**
     * Validatore: utente con abilitazione alla validazione degli ordini sulla uo (o su tutte le uo).
     */
    public boolean isUtenteValidatore(String user, String uo) {
        if (!StringUtils.hasLength(user) || !StringUtils.hasLength(uo)) {
            return false;
        }
        UsersSpecial userSpecial = accountService.getUoForUsersSpecial(user);
        if (userSpecial == null) {
            return false;
        }
        if ("S".equals(userSpecial.getAll())) {
            return true;
        }
        if (userSpecial.getUoForUsersSpecials() != null) {
            for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()) {
                if (uo.equals(uoService.getUoSigla(uoUser)) && "S".equals(Utility.nvl(uoUser.getOrdine_da_validare()))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void avviaFlusso(OrdineMissioneAnticipo ordineMissioneAnticipo)
            throws AwesomeException, AwesomeException {

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnticipo(OrdineMissione ordineMissione)
            throws AwesomeException, AwesomeException {
        OrdineMissioneAnticipo anticipo = ordineMissioneAnticipoRepository.getAnticipo(ordineMissione);
        if (anticipo != null && anticipo.getId() != null) {
            cancellaOrdineMissioneAnticipo(anticipo);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnticipo(Long idAnticipoOrdineMissione) throws AwesomeException {
        OrdineMissioneAnticipo ordineMissioneAnticipo = ordineMissioneAnticipoRepository
                .findById(idAnticipoOrdineMissione)
                .orElse(null);

        if (ordineMissioneAnticipo != null) {
            cancellaOrdineMissioneAnticipo(ordineMissioneAnticipo);
        }
    }

    @Transactional
    public void cancellaOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo)
            throws AwesomeException {

        if (!ordineMissioneAnticipo.isStatoNonInviatoAlFlusso()) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Non è possibile cancellare l'anticipo. È già stato inviato al flusso per l'approvazione.");
        }

        // Aggiorna lo stato a annullato
        ordineMissioneAnticipo.setStato(Costanti.STATO_ANNULLATO);
        ordineMissioneAnticipo.setToBeUpdated();
        ordineMissioneAnticipoRepository.save(ordineMissioneAnticipo); // uso repository direttamente

        // Recupera l'ordine associato
        Optional<OrdineMissione> ordineOpt = ordineMissioneRepository.findById(
                (Long) ordineMissioneAnticipo.getOrdineMissione().getId());
        if (ordineOpt.isEmpty()) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Ordine missione non trovato per l'anticipo.");
        }
        OrdineMissione ordineMissione = ordineOpt.get();

        // Recupera gli allegati dell'anticipo
        List<StorageObject> listaAllegati = cmisOrdineMissioneService.getAttachmentsAnticipo(ordineMissione);
        if (listaAllegati != null) {
            for (StorageObject object : listaAllegati) {
                if (StringUtils.hasLength(ordineMissione.getIdFlusso())) {
                    StorageObject folderOrdineMissione = cmisOrdineMissioneService.recuperoFolderOrdineMissione(ordineMissione);
                    missioniCMISService.eliminaFilePresenteNelFlusso(object.getKey(), folderOrdineMissione);
                } else {
                    missioniCMISService.deleteNode(object.getKey());
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public Map<String, byte[]> printOrdineMissioneAnticipo(Long idMissione)
            throws AwesomeException, AwesomeException {
        String username = securityService.getCurrentUserLogin();
        OrdineMissioneAnticipo ordineMissioneAnticipo = getAnticipo(idMissione, true);
        OrdineMissione ordineMissione = ordineMissioneAnticipo.getOrdineMissione();
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        byte[] printOrdineMissione = null;
        String fileName = null;
        if ((ordineMissione.isStatoInviatoAlFlusso() && (!ordineMissione.isMissioneInserita() || !ordineMissione.isMissioneDaValidare())) || (ordineMissione.isStatoFlussoApprovato())) {
            map = cmisOrdineMissioneService.getFileOrdineMissioneAnticipo(ordineMissioneAnticipo);
        } else {
            fileName = "OrdineMissioneAnticipo" + idMissione + ".pdf";
            printOrdineMissione = printAnticipo(username, ordineMissioneAnticipo);
//            if (ordineMissioneAnticipo.isAnticipoInserito()) {
//                cmisOrdineMissioneService.salvaStampaAnticipoSuCMIS(username, printOrdineMissione, ordineMissioneAnticipo);
//            }
            map.put(fileName, printOrdineMissione);
        }
        return map;
    }

    private byte[] printAnticipo(String username, OrdineMissioneAnticipo ordineMissioneAnticipo)
            throws AwesomeException {
        byte[] printOrdineMissione = printOrdineMissioneAnticipoService
                .printOrdineMissioneAnticipo(ordineMissioneAnticipo, username);
        return printOrdineMissione;
    }

    @Transactional(readOnly = true)
    public String jsonForPrintOrdineMissione(Long idMissione)
            throws AwesomeException, AwesomeException {
        OrdineMissioneAnticipo ordineMissioneAnticipo = getAnticipo(idMissione, true);
        return printOrdineMissioneAnticipoService.createJsonPrintOrdineMissioneAnticipo(ordineMissioneAnticipo,
                securityService.getCurrentUserLogin());
    }

    @Transactional(readOnly = true)
    public List<CMISFileAttachment> getAttachments(Long idAnticipo) throws AwesomeException {
        if (idAnticipo == null) return null;

        OrdineMissioneAnticipo ordineMissioneAnticipo = ordineMissioneAnticipoRepository
                .findById(idAnticipo)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "Anticipo non trovato per id: " + idAnticipo));

        OrdineMissione ordineMissione = ordineMissioneRepository
                .findById((Long) ordineMissioneAnticipo.getOrdineMissione().getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "Ordine missione non trovato per anticipo id: " + idAnticipo));

        return cmisOrdineMissioneService.getAttachmentsAnticipo(ordineMissione, idAnticipo);
    }

    @Transactional(readOnly = true)
    public CMISFileAttachment uploadAllegato(Long idAnticipo,
                                             InputStream inputStream, String name, MimeTypes mimeTypes) throws AwesomeException {
        OrdineMissioneAnticipo ordineMissioneAnticipo = ordineMissioneAnticipoRepository
                .findById(idAnticipo)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "Anticipo non trovato per id: " + idAnticipo));

        OrdineMissione ordineMissione = ordineMissioneRepository
                .findById((Long) ordineMissioneAnticipo.getOrdineMissione().getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "Ordine missione non trovato per anticipo id: " + idAnticipo));

        return cmisOrdineMissioneService.uploadAttachmentAnticipo(ordineMissione, idAnticipo,
                inputStream, name, mimeTypes);
    }
}
