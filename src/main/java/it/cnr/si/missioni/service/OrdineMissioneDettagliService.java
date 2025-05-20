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

    @Autowired
    private OrdineMissioneAutoPropriaService ordineMissioneAutoPropriaService;

    @Autowired
    private OrdineMissioneTaxiService ordineMissioneTaxiService;

    @Autowired
    private OrdineMissioneAutoNoleggioService ordineMissioneAutoNoleggioService;


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

        if (StringUtils.isEmpty(ordineMissioneDettagli.getDsSpesa())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Indicare una descrizione per la spesa.");
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneDettagli createOrdineMissioneDettagli(
            OrdineMissioneDettagli ordineMissioneDettagli) throws AwesomeException, ComponentException,
            OptimisticLockException, PersistencyException, BusyResourceException {
        ordineMissioneDettagli.setUid(securityService.getCurrentUserLogin());
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

        ordineMissioneDettagli = (OrdineMissioneDettagli) crudServiceBean.creaConBulk(ordineMissioneDettagli);
        log.debug("Created Information for OrdineMissioneDettagli: {}", ordineMissioneDettagli);
        return ordineMissioneDettagli;
    }


    private void controlloDatiObbligatoriDaGui(OrdineMissioneDettagli dettaglio) {
        if (dettaglio != null) {
            if (dettaglio.getCdTiSpesa() == null) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Dettaglio Spesa");
            } else if (dettaglio.getImportoEuro() == null) {
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

        ordineMissioneDettagliDB = (OrdineMissioneDettagli) crudServiceBean.modificaConBulk(ordineMissioneDettagliDB);

        log.debug("Updated Information for Dettaglio Ordine Missione: {}", ordineMissioneDettagliDB);
        return ordineMissioneDettagliDB;
    }

}