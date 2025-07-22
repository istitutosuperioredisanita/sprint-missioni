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

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneDettagli;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneTaxi;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.OrdineMissioneDettagliRepository;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

/**
 * Service class for managing users.
 */
@Service
public class OrdineMissioneDettagliService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneDettagliService.class);

    @Autowired
    private OrdineMissioneDettagliRepository ordineMissioneDettagliRepository;

    @Autowired
    private OrdineMissioneService ordineMissioneService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CRUDComponentSession crudServiceBean;
    // Removed Environment env as it's no longer used for profile checking or print name config here

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
            throws ComponentException {
        OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(
                OrdineMissione.class, idOrdineMissione);

        if (ordineMissione != null) {
            List<OrdineMissioneDettagli> lista = ordineMissioneDettagliRepository
                    .getOrdineMissioneDettagli(ordineMissione);
            return lista;
        }
        return null;
    }


    private void validaCRUD(OrdineMissioneDettagli ordineMissioneDettagli) throws ComponentException {
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
            OrdineMissioneDettagli ordineMissioneDettagli) throws AwesomeException, ComponentException,
            OptimisticLockException, PersistencyException, BusyResourceException {
        ordineMissioneDettagli.setUid(securityService.getCurrentUserLogin());
        // Removed setting the 'user' field as it's not in the provided entity
        ordineMissioneDettagli.setStato(Costanti.STATO_INSERITO);
        if (ordineMissioneDettagli.getTiSpesaDiaria() == null) {
            ordineMissioneDettagli.setTiSpesaDiaria("S");
        }
        OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(
                OrdineMissione.class, ordineMissioneDettagli.getOrdineMissione().getId());
        if (ordineMissione != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissione);
        } else {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine Missione non trovato.");
        }
        ordineMissioneDettagli.setOrdineMissione(ordineMissione);
        Long maxRiga = ordineMissioneDettagliRepository.getMaxRigaDettaglio(ordineMissione);
        if (maxRiga == null) {
            maxRiga = Long.valueOf(0);
        }
        maxRiga = maxRiga + 1;
        ordineMissioneDettagli.setRiga(maxRiga);
        ordineMissioneDettagli.setToBeCreated();
        controlloDatiObbligatoriDaGui(ordineMissioneDettagli);
        impostaImportoDivisa(ordineMissioneDettagli);
        validaCRUD(ordineMissioneDettagli);
        // Removed call to controlliPasto

        //controlliSpeseMezzi(ordineMissioneDettagli, ordineMissione);

        // Removed call to aggiornaDatiImpegni

        ordineMissioneDettagli = (OrdineMissioneDettagli) crudServiceBean.creaConBulk(ordineMissioneDettagli);
        log.debug("Created Information for OrdineMissioneDettagli: {}", ordineMissioneDettagli);
        return ordineMissioneDettagli;
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
                                               Boolean deleteDocument) throws ComponentException {
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
            throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException,
            BusyResourceException {
        OrdineMissioneDettagli ordineMissioneDettagli = getOrdineMissioneDettaglio(idOrdineMissioneDettagli);

        // effettuo controlli di validazione operazione CRUD
        if (ordineMissioneDettagli != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissioneDettagli.getOrdineMissione());
            cancellaOrdineMissioneDettagli(ordineMissioneDettagli, true);
        }
    }

    public OrdineMissioneDettagli getOrdineMissioneDettaglio(Long idOrdineMissioneDettagli) {
        OrdineMissioneDettagli ordineMissioneDettagli = (OrdineMissioneDettagli) crudServiceBean
                .findById(OrdineMissioneDettagli.class, idOrdineMissioneDettagli);
        return ordineMissioneDettagli;
    }

    private void cancellaOrdineMissioneDettagli(
            OrdineMissioneDettagli ordineMissioneDettagli, Boolean deleteDocument) throws ComponentException {
        ordineMissioneDettagli.setToBeUpdated();
        ordineMissioneDettagli.setStato(Costanti.STATO_ANNULLATO);
        crudServiceBean.modificaConBulk(ordineMissioneDettagli);
        // Removed CMIS attachment deletion logic as methods are removed
    }

    private void impostaImportoDivisa(OrdineMissioneDettagli ordineMissioneDettagli) {
        if (ordineMissioneDettagli.getCambio() != null && ordineMissioneDettagli.getCambio().compareTo(BigDecimal.ONE) == 0) { // Added null check for cambio
            ordineMissioneDettagli.setImportoDivisa(ordineMissioneDettagli.getImportoEuro());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneDettagli updateOrdineMissioneDettagli(
            OrdineMissioneDettagli ordineMissioneDettagli) throws AwesomeException, ComponentException,
            OptimisticLockException, PersistencyException, BusyResourceException {

        OrdineMissioneDettagli ordineMissioneDettagliDB = (OrdineMissioneDettagli) crudServiceBean
                .findById(OrdineMissioneDettagli.class, ordineMissioneDettagli.getId());
        if (ordineMissioneDettagliDB == null)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dettaglio Ordine Missione da aggiornare inesistente.");
        OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(
                OrdineMissione.class, ordineMissioneDettagli.getOrdineMissione().getId());
        if (ordineMissione != null && !ordineMissioneDettagli.isModificaSoloDatiFinanziari(ordineMissioneDettagliDB)) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissione);
        } else if (ordineMissione == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine Missione non trovato.");
        }
        ordineMissioneDettagli.setOrdineMissione(ordineMissione);

        controlloDatiObbligatoriDaGui(ordineMissioneDettagli);

        // Update only fields present in the OrdineMissioneDettagli entity
        // Removed: setCdTiPasto, setNote, setFlSpesaAnticipata, setKmPercorsi, setDsNoGiustificativo, setLocalitaSpostamento, setIdRimborsoImpegni
        ordineMissioneDettagliDB.setCdTiSpesa(ordineMissioneDettagli.getCdTiSpesa());
        ordineMissioneDettagliDB.setTiCdTiSpesa(ordineMissioneDettagli.getTiCdTiSpesa());
        ordineMissioneDettagliDB.setDsSpesa(ordineMissioneDettagli.getDsSpesa());
        ordineMissioneDettagliDB.setTiSpesaDiaria(ordineMissioneDettagli.getTiSpesaDiaria());
        ordineMissioneDettagliDB.setDsTiSpesa(ordineMissioneDettagli.getDsTiSpesa());

        ordineMissioneDettagliDB.setCambio(ordineMissioneDettagli.getCambio());
        ordineMissioneDettagliDB.setCdDivisa(ordineMissioneDettagli.getCdDivisa());
        ordineMissioneDettagliDB.setImportoEuro(ordineMissioneDettagli.getImportoEuro());


        impostaImportoDivisa(ordineMissioneDettagliDB);

        ordineMissioneDettagliDB.setToBeUpdated();

        validaCRUD(ordineMissioneDettagliDB);
        // Removed call to controlliPasto

        //controlliSpeseMezzi(ordineMissioneDettagli, ordineMissione);

        // Removed call to aggiornaDatiImpegni


        ordineMissioneDettagliDB = (OrdineMissioneDettagli) crudServiceBean.modificaConBulk(ordineMissioneDettagliDB);

        log.debug("Updated Information for Dettaglio Ordine Missione: {}", ordineMissioneDettagliDB);
        return ordineMissioneDettagliDB;
    }

}