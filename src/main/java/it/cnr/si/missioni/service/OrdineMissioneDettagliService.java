/*
 * Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneDettagli;
import it.cnr.si.missioni.repository.OrdineMissioneDettagliRepository;
import it.cnr.si.missioni.repository.OrdineMissioneRepository;
import it.cnr.si.missioni.service.security.SecurityService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing users.
 */
@Service
public class OrdineMissioneDettagliService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneDettagliService.class);

    @Autowired
    private OrdineMissioneRepository ordineMissioneRepository;

    @Autowired
    private OrdineMissioneDettagliRepository ordineMissioneDettagliRepository;

    @Autowired
    private OrdineMissioneService ordineMissioneService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private OrdineMissioneAutoPropriaService ordineMissioneAutoPropriaService;

    @Autowired
    private OrdineMissioneTaxiService ordineMissioneTaxiService;

    @Autowired
    private OrdineMissioneAutoNoleggioService ordineMissioneAutoNoleggioService;

    // Removed uploadAllegato method
    // Removed getAttachments method

    @Transactional(readOnly = true)
    public List<OrdineMissioneDettagli> getOrdineMissioneDettagli(Long idOrdineMissione)
            throws AwesomeException {

        OrdineMissione ordineMissione = ordineMissioneRepository.findById(idOrdineMissione)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "Ordine Missione non trovato con ID: " + idOrdineMissione));

        List<OrdineMissioneDettagli> lista = ordineMissioneDettagliRepository
                .getOrdineMissioneDettagli(ordineMissione);

        return lista != null ? lista : List.of(); // restituisce lista vuota se null
    }


    private void validaCRUD(OrdineMissioneDettagli ordineMissioneDettagli) throws AwesomeException {
        // Removed kmPercorsi validation as kmPercorsi is not in the entity
        // Removed validaDettaglioOrdineService.valida call

        if (StringUtils.isEmpty(ordineMissioneDettagli.getDsSpesa())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Indicare una descrizione per la spesa.");
        }
    }

    // Removed controlliPasto method and its helper methods (recuperoLivelloEquivalente, controlloCongruenzaPasto)
    // as they rely on fields/methods not present in the provided OrdineMissioneDettagli entity (like cdTiPasto, isDettaglioPasto)

//    private void controlliSpeseMezzi(OrdineMissioneDettagli ordineMissioneDettagli, OrdineMissione ordineMissione) {
//        Long idMissione = Long.valueOf(ordineMissione.getId().toString());
//
//        boolean isTaxiUsed = ordineMissioneTaxiService.getTaxi(idMissione) != null;
//        boolean isAutoNoleggioUsed = ordineMissioneAutoNoleggioService.getAutoNoleggio(idMissione) != null;
//        boolean isAutoPropriaUsed = ordineMissioneAutoPropriaService.getAutoPropria(idMissione) != null;
//
//        // This check for tassaSoggiorno is based on cdTiSpesa which IS in the entity
//        boolean tassaSoggiorno = ordineMissioneDettagliRepository
//                .getOrdineMissioneDettagli(ordineMissione)
//                .stream()
//                .anyMatch(dettaglio -> dettaglio.getImportoEuro() != null && dettaglio.getImportoEuro().compareTo(BigDecimal.ZERO) != 0
//                        && dettaglio.getCdTiSpesa() != null && dettaglio.getCdTiSpesa().equalsIgnoreCase(Costanti.SPESA_PERNOTTAMENTO));
//
//        String cdTiSpesa = ordineMissioneDettagli.getCdTiSpesa();
//        String messaggioErrore = "ATTENZIONE! Voce non selezionabile in quanto NON preventivamente autorizzata";
//
//        String utilizzoMotiviIspettivi = null;
//        String utilizzoMotiviSediDisagiate = null;
//        if (isAutoPropriaUsed) {
//            OrdineMissioneAutoPropria autoPropria = ordineMissioneAutoPropriaService.getAutoPropria(idMissione);
//            if (autoPropria != null) { // Added null check for autoPropria
//                utilizzoMotiviIspettivi = Utility.nvl(autoPropria.getUtilizzoMotiviIspettivi(), "N");
//                utilizzoMotiviSediDisagiate = Utility.nvl(autoPropria.getUtilizzoMotiviSediDisagiate(), "N");
//            }
//        }
//
//        // Gestione delle spese in base al codice di spesa - uses cdTiSpesa which is in the entity
//        if (cdTiSpesa != null) {
//            switch (cdTiSpesa) {
//                case Costanti.SPESA_INDENNITA_KM:
//                    if (!isAutoPropriaUsed || (utilizzoMotiviIspettivi != null && utilizzoMotiviIspettivi.equals("N") && utilizzoMotiviSediDisagiate != null && utilizzoMotiviSediDisagiate.equals("S"))) { // Added null checks for motivi
//                        throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
//                    }
//                    break;
//
//                case Costanti.SPESA_IND_AUTO_PROPRIA:
//                    if (!isAutoPropriaUsed || (utilizzoMotiviIspettivi != null && utilizzoMotiviIspettivi.equals("S") && utilizzoMotiviSediDisagiate != null && utilizzoMotiviSediDisagiate.equals("N"))) { // Added null checks for motivi
//                        throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
//                    }
//                    break;
//
//                case Costanti.SPESA_NOLEGGIO_AUTO:
//                    if (!isAutoNoleggioUsed) {
//                        throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
//                    }
//                    break;
//
//                case Costanti.SPESA_TAXI:
//                    if (!isTaxiUsed) {
//                        throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
//                    }
//                    break;
//
//                case Costanti.SPESA_PEDAGGIO_AUTOSTRADA:
//                    if (!isAutoPropriaUsed && !isAutoNoleggioUsed) {
//                        throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
//                    }
//                    break;
//                case Costanti.SPESA_PARCHEGGIO:
//                    if (!isAutoNoleggioUsed && !isAutoPropriaUsed) {
//                        throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
//                    }
//                    break;
//
//                case Costanti.SPESA_ACC_DISABILE:
//                    if (!isTaxiUsed) {
//                        throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
//                    }
//
//                    OrdineMissioneTaxi taxi = ordineMissioneTaxiService.getTaxi(idMissione);
//                    if (taxi == null || StringUtils.isEmpty(taxi.getMotiviHandicap())) { // Added null check for taxi
//                        throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
//                    }
//                    break;
//
//
//                case Costanti.SPESE_VISTO_VIAGGI_ESTERO:
//                    if (ordineMissione != null && ordineMissione.getTipoMissione() != null && ordineMissione.getTipoMissione().equals("I")) { // Added null checks
//                        throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
//                    }
//                    break;
//
//                default:
//                    // Consider adding a check here if the expense type is not recognized/allowed by default
//                    break;
//            }
//        }
//    }

    // Removed aggiornaDatiImpegni method


    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneDettagli createOrdineMissioneDettagli(
            OrdineMissioneDettagli ordineMissioneDettagli) throws AwesomeException {

        if (ordineMissioneDettagli == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "OrdineMissioneDettagli non può essere nullo.");
        }

        // Imposto utente e stato
        ordineMissioneDettagli.setUid(securityService.getCurrentUserLogin());
        ordineMissioneDettagli.setStato(Costanti.STATO_INSERITO);
        if (ordineMissioneDettagli.getTiSpesaDiaria() == null) {
            ordineMissioneDettagli.setTiSpesaDiaria("S");
        }

        // Recupero OrdineMissione in modo sicuro
        Long idOrdineMissione = ordineMissioneDettagli.getOrdineMissione() != null
                ? (Long) ordineMissioneDettagli.getOrdineMissione().getId()
                : null;

        if (idOrdineMissione == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine Missione non specificato.");
        }

        OrdineMissione ordineMissione = ordineMissioneRepository.findById(idOrdineMissione)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Ordine Missione non trovato."));

        // Controlli CRUD su OrdineMissione
        ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissione);

        // Associo l'ordine al dettaglio
        ordineMissioneDettagli.setOrdineMissione(ordineMissione);

        // Calcolo la riga successiva
        Long maxRiga = ordineMissioneDettagliRepository.getMaxRigaDettaglio(ordineMissione);
        ordineMissioneDettagli.setRiga((maxRiga != null ? maxRiga : 0L) + 1);

        // Imposto flag di creazione
        ordineMissioneDettagli.setToBeCreated();

        // Validazioni
        controlloDatiObbligatoriDaGui(ordineMissioneDettagli);
        impostaImportoDivisa(ordineMissioneDettagli);
        validaCRUD(ordineMissioneDettagli);

        // Salvataggio tramite repository
        OrdineMissioneDettagli saved = ordineMissioneDettagliRepository.save(ordineMissioneDettagli);

        log.debug("Created OrdineMissioneDettagli: {}", saved);
        return saved;
    }


    private void controlloDatiObbligatoriDaGui(OrdineMissioneDettagli dettaglio) {
        if (dettaglio != null) {
            if (dettaglio.getCdTiSpesa() == null) { // Check for null LocalDate
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Dettaglio Spesa");
            } else if (dettaglio.getImportoEuro() == null) { // Check for null BigDecimal
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Importo Euro");
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void cancellaOrdineMissioneDettagli(OrdineMissione ordineMissione,
                                               Boolean deleteDocument) throws AwesomeException {
        List<OrdineMissioneDettagli> listaOrdineMissioneDettagli = ordineMissioneDettagliRepository
                .getOrdineMissioneDettagli(ordineMissione);
        if (listaOrdineMissioneDettagli != null && !listaOrdineMissioneDettagli.isEmpty()) {
            for (Iterator<OrdineMissioneDettagli> iterator = listaOrdineMissioneDettagli.iterator(); iterator
                    .hasNext(); ) {
                OrdineMissioneDettagli dettaglio = iterator.next();
                cancellaOrdineMissioneDettagli(dettaglio, deleteDocument);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteOrdineMissioneDettagli(Long idOrdineMissioneDettagli)
            throws AwesomeException, OptimisticLockException {
        OrdineMissioneDettagli ordineMissioneDettagli = getOrdineMissioneDettaglio(idOrdineMissioneDettagli);

        // effettuo controlli di validazione operazione CRUD
        if (ordineMissioneDettagli != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissioneDettagli.getOrdineMissione());
            cancellaOrdineMissioneDettagli(ordineMissioneDettagli, true);
        }
    }

    /**
     * Recupera un dettaglio OrdineMissioneDettagli per ID in modo sicuro.
     */
    public OrdineMissioneDettagli getOrdineMissioneDettaglio(Long idOrdineMissioneDettagli) throws AwesomeException {
        if (idOrdineMissioneDettagli == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "ID OrdineMissioneDettagli non può essere nullo.");
        }

        return ordineMissioneDettagliRepository.findById(idOrdineMissioneDettagli)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "Dettaglio Ordine Missione con ID " + idOrdineMissioneDettagli + " non trovato."));
    }

    /**
     * Annulla un dettaglio OrdineMissioneDettagli.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    private void cancellaOrdineMissioneDettagli(
            OrdineMissioneDettagli ordineMissioneDettagli, Boolean deleteDocument) throws AwesomeException {

        if (ordineMissioneDettagli == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "OrdineMissioneDettagli da cancellare non può essere nullo.");
        }

        // Imposto flag di update e stato
        ordineMissioneDettagli.setToBeUpdated();
        ordineMissioneDettagli.setStato(Costanti.STATO_ANNULLATO);

        // Salvo direttamente con il repository
        ordineMissioneDettagliRepository.save(ordineMissioneDettagli);

        log.debug("OrdineMissioneDettagli annullato: {}", ordineMissioneDettagli);

        // deleteDocument non gestito: la logica CMIS è stata rimossa
    }

    private void impostaImportoDivisa(OrdineMissioneDettagli ordineMissioneDettagli) {
        if (ordineMissioneDettagli.getCambio() != null && ordineMissioneDettagli.getCambio().compareTo(BigDecimal.ONE) == 0) { // Added null check for cambio
            ordineMissioneDettagli.setImportoDivisa(ordineMissioneDettagli.getImportoEuro());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneDettagli updateOrdineMissioneDettagli(
            OrdineMissioneDettagli ordineMissioneDettagli) throws AwesomeException, OptimisticLockException {

        if (ordineMissioneDettagli == null || ordineMissioneDettagli.getId() == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dettaglio Ordine Missione da aggiornare non valido.");
        }

        // Recupero il dettaglio dal repository
        OrdineMissioneDettagli ordineMissioneDettagliDB = ordineMissioneDettagliRepository
                .findById((Long) ordineMissioneDettagli.getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "Dettaglio Ordine Missione da aggiornare inesistente."));

        // Recupero l'ordine missione associato
        OrdineMissione ordineMissione = Optional.ofNullable(ordineMissioneDettagli.getOrdineMissione())
                .flatMap(o -> ordineMissioneRepository.findById((Long) o.getId()))
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Ordine Missione non trovato."));

        // Controllo operazioni CRUD solo se non è modifica solo dati finanziari
        if (!ordineMissioneDettagli.isModificaSoloDatiFinanziari(ordineMissioneDettagliDB)) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissione);
        }

        ordineMissioneDettagliDB.setOrdineMissione(ordineMissione);

        // Validazione dati obbligatori
        controlloDatiObbligatoriDaGui(ordineMissioneDettagli);

        // Aggiorno solo i campi gestiti nell'entità
        ordineMissioneDettagliDB.setCdTiSpesa(ordineMissioneDettagli.getCdTiSpesa());
        ordineMissioneDettagliDB.setTiCdTiSpesa(ordineMissioneDettagli.getTiCdTiSpesa());
        ordineMissioneDettagliDB.setDsSpesa(ordineMissioneDettagli.getDsSpesa());
        ordineMissioneDettagliDB.setTiSpesaDiaria(ordineMissioneDettagli.getTiSpesaDiaria());
        ordineMissioneDettagliDB.setDsTiSpesa(ordineMissioneDettagli.getDsTiSpesa());

        ordineMissioneDettagliDB.setCambio(ordineMissioneDettagli.getCambio());
        ordineMissioneDettagliDB.setCdDivisa(ordineMissioneDettagli.getCdDivisa());
        ordineMissioneDettagliDB.setImportoEuro(ordineMissioneDettagli.getImportoEuro());

        // Imposta importo in divisa se necessario
        impostaImportoDivisa(ordineMissioneDettagliDB);

        ordineMissioneDettagliDB.setToBeUpdated();

        // Validazione CRUD
        validaCRUD(ordineMissioneDettagliDB);

        // Salvataggio diretto tramite repository
        ordineMissioneDettagliDB = ordineMissioneDettagliRepository.save(ordineMissioneDettagliDB);

        log.debug("Updated Information for Dettaglio Ordine Missione: {}", ordineMissioneDettagliDB);
        return ordineMissioneDettagliDB;
    }
}