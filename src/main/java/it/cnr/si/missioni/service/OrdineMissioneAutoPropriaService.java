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
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiAutoPropria;
import it.cnr.si.missioni.repository.OrdineMissioneAutoPropriaRepository;
import it.cnr.si.missioni.repository.OrdineMissioneRepository;
import it.cnr.si.missioni.repository.SpostamentiAutoPropriaRepository;
import it.cnr.si.missioni.service.security.SecurityService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.OptimisticLockException;
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
    @Lazy
    private OrdineMissioneService ordineMissioneService;

    @Autowired
    private MissioniCMISService missioniCMISService;

    @Autowired
    @Lazy
    private CMISOrdineMissioneService cmisOrdineMissioneService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private OrdineMissioneRepository ordineMissioneRepository;


    @Transactional(readOnly = true)
    public OrdineMissioneAutoPropria getAutoPropria(Long idMissione) throws AwesomeException {
        return getAutoPropria(idMissione, false);
    }

    @Transactional(readOnly = true)
    public OrdineMissioneAutoPropria getAutoPropria(Long idMissione, Boolean valorizzaDatiCollegati) throws AwesomeException {
        OrdineMissione ordineMissione = ordineMissioneRepository.findById(idMissione)
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "OrdineMissione con ID " + idMissione + " non trovato"
                ));

        OrdineMissioneAutoPropria ordineMissioneAutoPropria = ordineMissioneAutoPropriaRepository.getAutoPropria(ordineMissione);

        if (valorizzaDatiCollegati && ordineMissioneAutoPropria != null) {
            List<SpostamentiAutoPropria> list = spostamentiAutoPropriaRepository.getSpostamenti(ordineMissioneAutoPropria);
            ordineMissioneAutoPropria.setOrdineMissione(ordineMissione);
            ordineMissioneAutoPropria.setListSpostamenti(list);
        }

        return ordineMissioneAutoPropria;
    }

    @Transactional(readOnly = true)
    public List<SpostamentiAutoPropria> getSpostamentiAutoPropria(Long idAutoPropriaOrdineMissione) throws AwesomeException {
        OrdineMissioneAutoPropria autoPropriaOrdineMissione = ordineMissioneAutoPropriaRepository.findById(idAutoPropriaOrdineMissione)
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "OrdineMissioneAutoPropria con ID " + idAutoPropriaOrdineMissione + " non trovato"
                ));

        return spostamentiAutoPropriaRepository.getSpostamenti(autoPropriaOrdineMissione);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAutoPropria createAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria)
            throws AwesomeException, OptimisticLockException {

        ordineMissioneAutoPropria.setUid(securityService.getCurrentUserLogin());
        ordineMissioneAutoPropria.setUser(securityService.getCurrentUserLogin());

        // Recupera l'OrdineMissione dal repository e lancia eccezione se non trovato
        OrdineMissioneAutoPropria finalOrdineMissioneAutoPropria = ordineMissioneAutoPropria;
        OrdineMissione ordineMissione = ordineMissioneRepository.findById(
                (Long) ordineMissioneAutoPropria.getOrdineMissione().getId()
        ).orElseThrow(() -> new AwesomeException(
                CodiciErrore.ERRGEN,
                "OrdineMissione con ID " + finalOrdineMissioneAutoPropria.getOrdineMissione().getId() + " non trovato"
        ));

        // Controlli CRUD sulla GUI
        ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissione);

        ordineMissioneAutoPropria.setOrdineMissione(ordineMissione);
        ordineMissioneAutoPropria.setStato(Costanti.STATO_INSERITO);
        ordineMissioneAutoPropria.setToBeCreated();

        // Verifica se esiste già un record auto propria per questo ordine
        OrdineMissioneAutoPropria otherAuto = ordineMissioneAutoPropriaRepository.getAutoPropria(ordineMissione);
        if (otherAuto != null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati dell'auto propria già inseriti.");
        }

        validaCRUD(ordineMissioneAutoPropria);

        // Salva l'entità
        ordineMissioneAutoPropria = ordineMissioneAutoPropriaRepository.save(ordineMissioneAutoPropria);
        log.debug("Created Information for OrdineMissioneAutoPropria: {}", ordineMissioneAutoPropria);

        return ordineMissioneAutoPropria;
    }


    private void validaCRUD(OrdineMissioneAutoPropria ordineMissioneAutoPropria) {
        // Verifica se i dati dell'auto propria sono completi
        if (StringUtils.isEmpty(ordineMissioneAutoPropria.getCartaCircolazione()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getTarga()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getPolizzaAssicurativa()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getMarca()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getModello())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati dell'auto propria non esistenti o incompleti.");
        }

        // Verifica se i dati della patente sono completi
        if (StringUtils.isEmpty(ordineMissioneAutoPropria.getDataRilascioPatente()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getDataScadenzaPatente()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getEntePatente()) ||
                StringUtils.isEmpty(ordineMissioneAutoPropria.getNumeroPatente())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati della patente non esistenti o incompleti.");
        }

        int countMotivi = Utility.countMotiviRichiestaMezzi(
                ordineMissioneAutoPropria.getUtilizzoMotiviIspettivi(),
                ordineMissioneAutoPropria.getUtilizzoMotiviSediDisagiate()
        );

        if (countMotivi == 0) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Indicare almeno un motivo per la richiesta di utilizzo dell'auto propria.");
        } else if (countMotivi > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Indicare SOLO un motivo per la richiesta di utilizzo dell'auto propria.");
        }
    }


    private void validaCRUD(SpostamentiAutoPropria spostamentiAutoPropria) {
        if (StringUtils.isEmpty(spostamentiAutoPropria.getPercorsoDa()) ||
                StringUtils.isEmpty(spostamentiAutoPropria.getPercorsoA())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati degli spostamenti incompleti.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiAutoPropria createSpostamentoAutoPropria(SpostamentiAutoPropria spostamentoAutoPropria)
            throws AwesomeException, OptimisticLockException {

        spostamentoAutoPropria.setUid(securityService.getCurrentUserLogin());
        spostamentoAutoPropria.setUser(securityService.getCurrentUserLogin());
        spostamentoAutoPropria.setStato(Costanti.STATO_INSERITO);

        // Recupera l'OrdineMissioneAutoPropria dal repository, lancia eccezione se non trovato
        SpostamentiAutoPropria finalSpostamentoAutoPropria = spostamentoAutoPropria;
        OrdineMissioneAutoPropria ordineMissioneAutoPropria = ordineMissioneAutoPropriaRepository.findById(
                (Long) spostamentoAutoPropria.getOrdineMissioneAutoPropria().getId()
        ).orElseThrow(() -> new AwesomeException(
                CodiciErrore.ERRGEN,
                "OrdineMissioneAutoPropria con ID " + finalSpostamentoAutoPropria.getOrdineMissioneAutoPropria().getId() + " non trovato"
        ));

        // Controlli CRUD sulla GUI dell'ordine missione collegato
        ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissioneAutoPropria.getOrdineMissione());

        spostamentoAutoPropria.setOrdineMissioneAutoPropria(ordineMissioneAutoPropria);

        // Calcolo della riga successiva
        Long maxRiga = spostamentiAutoPropriaRepository.getMaxRigaSpostamenti(ordineMissioneAutoPropria);
        maxRiga = (maxRiga != null ? maxRiga : 0L) + 1;
        spostamentoAutoPropria.setRiga(maxRiga);

        spostamentoAutoPropria.setToBeCreated();
        validaCRUD(spostamentoAutoPropria);

        // Salvataggio
        spostamentoAutoPropria = spostamentiAutoPropriaRepository.save(spostamentoAutoPropria);

        log.debug("Created Information for SpostamentiAutoPropria: {}", spostamentoAutoPropria);

        return spostamentoAutoPropria;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAutoPropria updateAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria)
            throws AwesomeException, OptimisticLockException {

        // Recupera l'entità dal repository, lancia eccezione se non esiste
        OrdineMissioneAutoPropria ordineMissioneAutoPropriaDB = ordineMissioneAutoPropriaRepository.findById((Long) ordineMissioneAutoPropria.getId())
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Auto Propria Ordine di Missione da aggiornare inesistente."
                ));

        // Controllo operazioni CRUD sulla missione collegata
        if (ordineMissioneAutoPropriaDB.getOrdineMissione() != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissioneAutoPropriaDB.getOrdineMissione());
        }

        // Aggiorna i campi dell'auto propria
        ordineMissioneAutoPropriaDB.setTarga(ordineMissioneAutoPropria.getTarga());
        ordineMissioneAutoPropriaDB.setMarca(ordineMissioneAutoPropria.getMarca());
        ordineMissioneAutoPropriaDB.setModello(ordineMissioneAutoPropria.getModello());
        ordineMissioneAutoPropriaDB.setCartaCircolazione(ordineMissioneAutoPropria.getCartaCircolazione());
        ordineMissioneAutoPropriaDB.setEntePatente(ordineMissioneAutoPropria.getEntePatente());
        ordineMissioneAutoPropriaDB.setUtilizzoMotiviIspettivi(ordineMissioneAutoPropria.getUtilizzoMotiviIspettivi());
        ordineMissioneAutoPropriaDB.setUtilizzoMotiviSediDisagiate(ordineMissioneAutoPropria.getUtilizzoMotiviSediDisagiate());
        ordineMissioneAutoPropriaDB.setUtilizzoAltriMotivi(ordineMissioneAutoPropria.getUtilizzoAltriMotivi());

        ordineMissioneAutoPropriaDB.setToBeUpdated();

        // Validazione e salvataggio
        validaCRUD(ordineMissioneAutoPropriaDB);
        ordineMissioneAutoPropriaDB = ordineMissioneAutoPropriaRepository.save(ordineMissioneAutoPropriaDB);

        log.debug("Updated Information for Auto Propria Ordine di Missione: {}", ordineMissioneAutoPropriaDB);
        return ordineMissioneAutoPropriaDB;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAutoPropria(Long idAutoPropriaOrdineMissione)
            throws AwesomeException, OptimisticLockException {

        // Recupera l'OrdineMissioneAutoPropria dal repository, lancia eccezione se non trovato
        OrdineMissioneAutoPropria ordineMissioneAutoPropria = ordineMissioneAutoPropriaRepository.findById(idAutoPropriaOrdineMissione)
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "OrdineMissioneAutoPropria con ID " + idAutoPropriaOrdineMissione + " non trovato"
                ));

        // Recupera il nodo CMIS e lo elimina se presente
        String nodeRef = cmisOrdineMissioneService.getNodeRefOrdineMissioneAutoPropria(ordineMissioneAutoPropria, false);
        if (nodeRef != null) {
            missioniCMISService.deleteNode(nodeRef);
        }

        // Cancella l'ordine auto propria
        cancellaOrdineMissioneAutoPropria(ordineMissioneAutoPropria);
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
            throws AwesomeException {
        cancellaSpostamenti(ordineMissioneAutoPropria);
        cancellaDatiAutoPropriaOrdineMissione(ordineMissioneAutoPropria);
    }

    private void cancellaDatiAutoPropriaOrdineMissione(
            OrdineMissioneAutoPropria ordineMissioneAutoPropria)
            throws AwesomeException {
        ordineMissioneAutoPropria.setToBeUpdated();
        ordineMissioneAutoPropria.setStato(Costanti.STATO_ANNULLATO);
        ordineMissioneAutoPropriaRepository.save(ordineMissioneAutoPropria);
    }

    private void cancellaSpostamenti(
            OrdineMissioneAutoPropria ordineMissioneAutoPropria)
            throws AwesomeException {
        List<SpostamentiAutoPropria> listaSpostamenti = spostamentiAutoPropriaRepository.getSpostamenti(ordineMissioneAutoPropria);
        if (listaSpostamenti != null && !listaSpostamenti.isEmpty()) {
            for (Iterator<SpostamentiAutoPropria> iterator = listaSpostamenti.iterator(); iterator.hasNext(); ) {
                SpostamentiAutoPropria spostamento = iterator.next();
                cancellaSpostamento(spostamento);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSpostamenti(Long idSpostamenti) throws AwesomeException, OptimisticLockException {
        // Recupera lo spostamento dal repository, lancia eccezione se non esiste
        SpostamentiAutoPropria spostamento = spostamentiAutoPropriaRepository.findById(idSpostamenti)
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Spostamento Auto Propria con id " + idSpostamenti + " inesistente."
                ));

        // Effettua controlli di validazione operazione CRUD se necessario
        cancellaSpostamento(spostamento);
    }

    private void cancellaSpostamento(SpostamentiAutoPropria spostamentiAutoPropria) throws AwesomeException {
        spostamentiAutoPropria.setToBeUpdated();
        spostamentiAutoPropria.setStato(Costanti.STATO_ANNULLATO);
        spostamentiAutoPropriaRepository.save(spostamentiAutoPropria);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiAutoPropria updateSpostamenti(SpostamentiAutoPropria spostamentiAutoPropria)
            throws AwesomeException, OptimisticLockException {

        // Recupera lo spostamento dal repository, lancia eccezione se non esiste
        SpostamentiAutoPropria spostamentiDB = spostamentiAutoPropriaRepository.findById((Long) spostamentiAutoPropria.getId())
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Spostamenti Auto Propria Ordine di Missione da aggiornare inesistente."
                ));

        // Controllo operazioni CRUD sull'ordine di missione collegato
        if (spostamentiDB.getOrdineMissioneAutoPropria().getOrdineMissione() != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(
                    spostamentiDB.getOrdineMissioneAutoPropria().getOrdineMissione()
            );
        }

        // Aggiornamento campi modificabili
        spostamentiDB.setPercorsoDa(spostamentiAutoPropria.getPercorsoDa());
        spostamentiDB.setPercorsoA(spostamentiAutoPropria.getPercorsoA());
        spostamentiDB.setToBeUpdated();

        // Salvataggio aggiornamento
        spostamentiDB = spostamentiAutoPropriaRepository.save(spostamentiDB);

        log.debug("Updated Information for Spostamenti: {}", spostamentiDB);
        return spostamentiDB;
    }

    @Transactional(readOnly = true)
    public Map<String, byte[]> printOrdineMissioneAutoPropria(Long idMissione) throws AwesomeException {
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
//            if (ordineMissioneAutoPropria.isRichiestaAutoPropriaInserita()) {
//                cmisOrdineMissioneService.salvaStampaAutoPropriaSuCMIS(username, printOrdineMissione, ordineMissioneAutoPropria);
//            }
            map.put(fileName, printOrdineMissione);
        }
        return map;
    }

    private byte[] printAutoPropria(String username,
                                    OrdineMissioneAutoPropria ordineMissioneAutoPropria)
            throws AwesomeException {
        byte[] print = printOrdineMissioneAutoPropriaService.printOrdineMissioneAutoPropria(ordineMissioneAutoPropria, username);
        return print;
    }

}
