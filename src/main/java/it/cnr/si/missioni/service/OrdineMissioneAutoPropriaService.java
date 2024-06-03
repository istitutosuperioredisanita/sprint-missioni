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


import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiAutoPropria;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.OrdineMissioneAutoPropriaRepository;
import it.cnr.si.missioni.repository.SpostamentiAutoPropriaRepository;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing users.
 */
@Service
public class OrdineMissioneAutoPropriaService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneAutoPropriaService.class);

    @Autowired
    private OrdineMissioneAutoPropriaRepository ordineMissioneAutoPropriaRepository;

    @Autowired
    private SpostamentiAutoPropriaRepository spostamentiAutoPropriaRepository;

    @Autowired
    private PrintOrdineMissioneAutoPropriaService printOrdineMissioneAutoPropriaService;

    @Autowired
    private OrdineMissioneService ordineMissioneService;


    @Autowired
    private MissioniCMISService missioniCMISService;

    @Autowired
    private CMISOrdineMissioneService cmisOrdineMissioneService;

    @Autowired
    private CRUDComponentSession crudServiceBean;

    @Autowired
    private SecurityService securityService;

    @Transactional(readOnly = true)
    public OrdineMissioneAutoPropria getAutoPropria(Long idMissione) throws ComponentException {
        return getAutoPropria(idMissione, false);
    }

    @Transactional(readOnly = true)
    public OrdineMissioneAutoPropria getAutoPropria(Long idMissione, Boolean valorizzaDatiCollegati) throws ComponentException {
        OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, idMissione);

        if (ordineMissione != null) {
            OrdineMissioneAutoPropria ordineMissioneAutoPropria = ordineMissioneAutoPropriaRepository.getAutoPropria(ordineMissione);

            if (valorizzaDatiCollegati && ordineMissioneAutoPropria != null) {
                List<SpostamentiAutoPropria> list = spostamentiAutoPropriaRepository.getSpostamenti(ordineMissioneAutoPropria);
                ordineMissioneAutoPropria.setOrdineMissione(ordineMissione);
                ordineMissioneAutoPropria.setListSpostamenti(list);
            }
            return ordineMissioneAutoPropria;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<SpostamentiAutoPropria> getSpostamentiAutoPropria(Long idAutoPropriaOrdineMissione) throws ComponentException {
        OrdineMissioneAutoPropria autoPropriaOrdineMissione = (OrdineMissioneAutoPropria) crudServiceBean.findById(OrdineMissioneAutoPropria.class, idAutoPropriaOrdineMissione);

        if (autoPropriaOrdineMissione != null) {
            List<SpostamentiAutoPropria> lista = spostamentiAutoPropriaRepository.getSpostamenti(autoPropriaOrdineMissione);
            return lista;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAutoPropria createAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        ordineMissioneAutoPropria.setUid(securityService.getCurrentUserLogin());
        ordineMissioneAutoPropria.setUser(securityService.getCurrentUserLogin());
        OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, ordineMissioneAutoPropria.getOrdineMissione().getId());
        if (ordineMissione != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissione);
        }
        ordineMissioneAutoPropria.setOrdineMissione(ordineMissione);
        ordineMissioneAutoPropria.setStato(Costanti.STATO_INSERITO);
        ordineMissioneAutoPropria.setToBeCreated();
        OrdineMissioneAutoPropria otherAuto = ordineMissioneAutoPropriaRepository.getAutoPropria(ordineMissione);
        if (otherAuto != null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati dell'auto propria già inseriti.");
        }

        validaCRUD(ordineMissioneAutoPropria);
        ordineMissioneAutoPropria = (OrdineMissioneAutoPropria) crudServiceBean.creaConBulk(ordineMissioneAutoPropria);
        log.debug("Created Information for OrdineMissioneAutoPropria: {}", ordineMissioneAutoPropria);
        return ordineMissioneAutoPropria;
    }

    private void validaCRUD(OrdineMissioneAutoPropria ordineMissioneAutoPropria) {
        if (StringUtils.isEmpty(ordineMissioneAutoPropria.getCartaCircolazione()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getTarga()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getPolizzaAssicurativa()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getMarca()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getModello())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati dell'auto propria non esistenti o incompleti.");
        }
        if (StringUtils.isEmpty(ordineMissioneAutoPropria.getDataRilascioPatente()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getDataScadenzaPatente()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getEntePatente()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getNumeroPatente())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati della patente non esistenti o incompleti.");
        }
        if (Utility.nvl(ordineMissioneAutoPropria.getUtilizzoMotiviIspettivi(), "N").equals("N") &&
                Utility.nvl(ordineMissioneAutoPropria.getUtilizzoMotiviSediDisagiate(), "N").equals("N") &&
                /*Utility.nvl(ordineMissioneAutoPropria.getUtilizzoMotiviUrgenza(), "N").equals("N") &&
                Utility.nvl(ordineMissioneAutoPropria.getUtilizzoMotiviTrasporto(), "N").equals("N") &&*/
                Utility.nvl(ordineMissioneAutoPropria.getUtilizzoAltriMotivi(), "N").equals("N")) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Indicare almeno un motivo per la richiesta di utilizzo dell'auto propria.");
        }
    }

    private void validaCRUD(SpostamentiAutoPropria spostamentiAutoPropria) {
        if (StringUtils.isEmpty(spostamentiAutoPropria.getPercorsoDa()) ||
                StringUtils.isEmpty(spostamentiAutoPropria.getPercorsoA())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati degli spostamenti incompleti.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiAutoPropria createSpostamentoAutoPropria(SpostamentiAutoPropria spostamentoAutoPropria) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        spostamentoAutoPropria.setUid(securityService.getCurrentUserLogin());
        spostamentoAutoPropria.setUser(securityService.getCurrentUserLogin());
        spostamentoAutoPropria.setStato(Costanti.STATO_INSERITO);
        OrdineMissioneAutoPropria ordineMissioneAutoPropria = (OrdineMissioneAutoPropria) crudServiceBean.findById(OrdineMissioneAutoPropria.class, spostamentoAutoPropria.getOrdineMissioneAutoPropria().getId());
        if (ordineMissioneAutoPropria != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissioneAutoPropria.getOrdineMissione());
        }

        spostamentoAutoPropria.setOrdineMissioneAutoPropria(ordineMissioneAutoPropria);
        Long maxRiga = spostamentiAutoPropriaRepository.getMaxRigaSpostamenti(ordineMissioneAutoPropria);
        if (maxRiga == null) {
            maxRiga = Long.valueOf(0);
        }
        maxRiga = maxRiga + 1;
        spostamentoAutoPropria.setRiga(maxRiga);
        spostamentoAutoPropria.setToBeCreated();
        validaCRUD(spostamentoAutoPropria);
        spostamentoAutoPropria = (SpostamentiAutoPropria) crudServiceBean.creaConBulk(spostamentoAutoPropria);
//    	autoPropriaRepository.save(autoPropria);
        log.debug("Created Information for OrdineMissioneAutoPropria: {}", ordineMissioneAutoPropria);
        return spostamentoAutoPropria;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAutoPropria updateAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {

        OrdineMissioneAutoPropria ordineMissioneAutoPropriaDB = (OrdineMissioneAutoPropria) crudServiceBean.findById(OrdineMissioneAutoPropria.class, ordineMissioneAutoPropria.getId());

        if (ordineMissioneAutoPropriaDB == null)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Auto Propria Ordine di Missione da aggiornare inesistente.");

        if (ordineMissioneAutoPropriaDB.getOrdineMissione() != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissioneAutoPropriaDB.getOrdineMissione());
        }
        ordineMissioneAutoPropriaDB.setTarga(ordineMissioneAutoPropria.getTarga());
        ordineMissioneAutoPropriaDB.setMarca(ordineMissioneAutoPropria.getMarca());
        ordineMissioneAutoPropriaDB.setModello(ordineMissioneAutoPropria.getModello());
        ordineMissioneAutoPropriaDB.setCartaCircolazione(ordineMissioneAutoPropria.getCartaCircolazione());
        ordineMissioneAutoPropriaDB.setEntePatente(ordineMissioneAutoPropria.getEntePatente());
        ordineMissioneAutoPropriaDB.setUtilizzoMotiviIspettivi(ordineMissioneAutoPropria.getUtilizzoMotiviIspettivi());
        ordineMissioneAutoPropriaDB.setUtilizzoMotiviSediDisagiate(ordineMissioneAutoPropria.getUtilizzoMotiviSediDisagiate());
        /*ordineMissioneAutoPropriaDB.setUtilizzoMotiviTrasporto(ordineMissioneAutoPropria.getUtilizzoMotiviTrasporto());
        ordineMissioneAutoPropriaDB.setUtilizzoMotiviUrgenza(ordineMissioneAutoPropria.getUtilizzoMotiviUrgenza());*/
        ordineMissioneAutoPropriaDB.setUtilizzoAltriMotivi(ordineMissioneAutoPropria.getUtilizzoAltriMotivi());

        ordineMissioneAutoPropriaDB.setToBeUpdated();


        validaCRUD(ordineMissioneAutoPropriaDB);
        ordineMissioneAutoPropriaDB = (OrdineMissioneAutoPropria) crudServiceBean.modificaConBulk(ordineMissioneAutoPropriaDB);

        log.debug("Updated Information for Auto Propria Ordine di Missione: {}", ordineMissioneAutoPropriaDB);
        return ordineMissioneAutoPropria;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAutoPropria(Long idAutoPropriaOrdineMissione) throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        OrdineMissioneAutoPropria ordineMissioneAutoPropria = (OrdineMissioneAutoPropria) crudServiceBean.findById(OrdineMissioneAutoPropria.class, idAutoPropriaOrdineMissione);

        if (ordineMissioneAutoPropria != null) {
            String nodeRef = cmisOrdineMissioneService.getNodeRefOrdineMissioneAutoPropria(ordineMissioneAutoPropria, false);
            if (nodeRef != null) {
                missioniCMISService.deleteNode(nodeRef);
            }
            cancellaOrdineMissioneAutoPropria(ordineMissioneAutoPropria);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAutoPropria(OrdineMissione ordineMissione) {
        OrdineMissioneAutoPropria ordineMissioneAutoPropria = ordineMissioneAutoPropriaRepository.getAutoPropria(ordineMissione);

        if (ordineMissioneAutoPropria != null) {
            cancellaOrdineMissioneAutoPropria(ordineMissioneAutoPropria);
        }
    }

    private void cancellaOrdineMissioneAutoPropria(
            OrdineMissioneAutoPropria ordineMissioneAutoPropria)
            throws ComponentException {
        cancellaSpostamenti(ordineMissioneAutoPropria);
        cancellaDatiAutoPropriaOrdineMissione(ordineMissioneAutoPropria);
    }

    private void cancellaDatiAutoPropriaOrdineMissione(
            OrdineMissioneAutoPropria ordineMissioneAutoPropria)
            throws ComponentException {
        ordineMissioneAutoPropria.setToBeUpdated();
        ordineMissioneAutoPropria.setStato(Costanti.STATO_ANNULLATO);
        crudServiceBean.modificaConBulk(ordineMissioneAutoPropria);
    }

    private void cancellaSpostamenti(
            OrdineMissioneAutoPropria ordineMissioneAutoPropria)
            throws ComponentException {
        List<SpostamentiAutoPropria> listaSpostamenti = spostamentiAutoPropriaRepository.getSpostamenti(ordineMissioneAutoPropria);
        if (listaSpostamenti != null && !listaSpostamenti.isEmpty()) {
            for (Iterator<SpostamentiAutoPropria> iterator = listaSpostamenti.iterator(); iterator.hasNext(); ) {
                SpostamentiAutoPropria spostamento = iterator.next();
                cancellaSpostamento(spostamento);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSpostamenti(Long idSpostamenti) throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        SpostamentiAutoPropria spostamentiAutoPropria = (SpostamentiAutoPropria) crudServiceBean.findById(SpostamentiAutoPropria.class, idSpostamenti);

        //effettuo controlli di validazione operazione CRUD
        if (spostamentiAutoPropria != null) {
            cancellaSpostamento(spostamentiAutoPropria);
        }
    }

    private void cancellaSpostamento(SpostamentiAutoPropria spostamentiAutoPropria) throws ComponentException {
        spostamentiAutoPropria.setToBeUpdated();
        spostamentiAutoPropria.setStato(Costanti.STATO_ANNULLATO);
        crudServiceBean.modificaConBulk(spostamentiAutoPropria);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiAutoPropria updateSpostamenti(SpostamentiAutoPropria spostamentiAutoPropria) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {

        SpostamentiAutoPropria spostamentiAutoPropriaDB = (SpostamentiAutoPropria) crudServiceBean.findById(SpostamentiAutoPropria.class, spostamentiAutoPropria.getId());

        if (spostamentiAutoPropriaDB == null)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Spostamenti Auto Propria Ordine di Missione da aggiornare inesistente.");

        if (spostamentiAutoPropriaDB.getOrdineMissioneAutoPropria().getOrdineMissione() != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(spostamentiAutoPropriaDB.getOrdineMissioneAutoPropria().getOrdineMissione());
        }
        spostamentiAutoPropriaDB.setPercorsoDa(spostamentiAutoPropria.getPercorsoDa());
        spostamentiAutoPropriaDB.setPercorsoA(spostamentiAutoPropria.getPercorsoA());

        spostamentiAutoPropriaDB.setToBeUpdated();


        spostamentiAutoPropriaDB = (SpostamentiAutoPropria) crudServiceBean.modificaConBulk(spostamentiAutoPropriaDB);

        log.debug("Updated Information for Spostamenti: {}", spostamentiAutoPropriaDB);
        return spostamentiAutoPropria;
    }

    @Transactional(readOnly = true)
    public Map<String, byte[]> printOrdineMissioneAutoPropria(Long idMissione) throws AwesomeException, ComponentException {
        String username = securityService.getCurrentUserLogin();
        OrdineMissioneAutoPropria ordineMissioneAutoPropria = getAutoPropria(idMissione, true);
        OrdineMissione ordineMissione = ordineMissioneAutoPropria.getOrdineMissione();
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        byte[] printOrdineMissione = null;
        String fileName = null;
        if ((ordineMissione.isStatoInviatoAlFlusso() && (!ordineMissione.isMissioneInserita() || !ordineMissione.isMissioneDaValidare())) || (ordineMissione.isStatoFlussoApprovato())) {
            map = cmisOrdineMissioneService.getFileOrdineMissioneAutoPropria(ordineMissioneAutoPropria);
        } else {
            fileName = "OrdineMissioneAutoPropria" + idMissione + ".pdf";
            printOrdineMissione = printAutoPropria(username, ordineMissioneAutoPropria);
            if (ordineMissioneAutoPropria.isRichiestaAutoPropriaInserita()) {
                cmisOrdineMissioneService.salvaStampaAutoPropriaSuCMIS(username, printOrdineMissione, ordineMissioneAutoPropria);
            }
            map.put(fileName, printOrdineMissione);
        }
        return map;
    }

    private byte[] printAutoPropria(String username,
                                    OrdineMissioneAutoPropria ordineMissioneAutoPropria)
            throws ComponentException {
        byte[] print = printOrdineMissioneAutoPropriaService.printOrdineMissioneAutoPropria(ordineMissioneAutoPropria, username);
        return print;
    }

}
