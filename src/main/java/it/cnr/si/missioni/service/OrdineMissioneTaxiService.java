package it.cnr.si.missioni.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneTaxi;
import it.cnr.si.missioni.repository.OrdineMissioneRepository;
import it.cnr.si.missioni.repository.OrdineMissioneTaxiRepository;
import it.cnr.si.missioni.repository.SpostamentiTaxiRepository;
import it.cnr.si.missioni.service.security.SecurityService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.spring.storage.StorageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.OptimisticLockException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class OrdineMissioneTaxiService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneTaxiService.class);
    private final OrdineMissioneTaxiRepository ordineMissioneTaxiRepository;
    private final SpostamentiTaxiRepository spostamentiTaxiRepository;
    private final OrdineMissioneRepository ordineMissioneRepository;
    private final OrdineMissioneService ordineMissioneService;
    private final MissioniCMISService missioniCMISService;
    private final CMISOrdineMissioneService cmisOrdineMissioneService;
    private final SecurityService securityService;
    private final PrintOrdineMissioneTaxiService printOrdineMissioneTaxiService;

    @Autowired
    public OrdineMissioneTaxiService(
            OrdineMissioneTaxiRepository ordineMissioneTaxiRepository,
            SpostamentiTaxiRepository spostamentiTaxiRepository,
            OrdineMissioneRepository ordineMissioneRepository,
            OrdineMissioneService ordineMissioneService,
            MissioniCMISService missioniCMISService,
            CMISOrdineMissioneService cmisOrdineMissioneService,
            SecurityService securityService, PrintOrdineMissioneTaxiService printOrdineMissioneTaxiService) {
        this.ordineMissioneTaxiRepository = ordineMissioneTaxiRepository;
        this.spostamentiTaxiRepository = spostamentiTaxiRepository;
        this.ordineMissioneRepository = ordineMissioneRepository;
        this.ordineMissioneService = ordineMissioneService;
        this.missioniCMISService = missioniCMISService;
        this.cmisOrdineMissioneService = cmisOrdineMissioneService;
        this.securityService = securityService;
        this.printOrdineMissioneTaxiService = printOrdineMissioneTaxiService;
    }

    @Transactional(readOnly = true)
    public OrdineMissioneTaxi getTaxi(Long idMissione) throws AwesomeException {
        return getTaxi(idMissione, false);
    }

    @Transactional(readOnly = true)
    public OrdineMissioneTaxi getTaxi(Long idMissione, Boolean valorizzaDatiCollegati) throws AwesomeException {
        OrdineMissione ordineMissione = ordineMissioneRepository.findById(idMissione)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "Ordine Missione non trovato con ID: " + idMissione));

        OrdineMissioneTaxi taxi = ordineMissioneTaxiRepository.getTaxi(ordineMissione);
        if (taxi != null && Boolean.TRUE.equals(valorizzaDatiCollegati)) {
            List<SpostamentiTaxi> list = spostamentiTaxiRepository.getSpostamenti(taxi);
            taxi.setOrdineMissione(ordineMissione);
            taxi.setListSpostamenti(list != null ? list : List.of());
        }
        return taxi;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneTaxi createTaxi(OrdineMissioneTaxi taxi) throws AwesomeException, OptimisticLockException {
        taxi.setUser(securityService.getCurrentUserLogin());

        OrdineMissioneTaxi finalTaxi = taxi;
        OrdineMissione ordineMissione = ordineMissioneRepository.findById((Long) taxi.getOrdineMissione().getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "Ordine Missione non trovato con ID: " + finalTaxi.getOrdineMissione().getId()));

        ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissione);

        taxi.setOrdineMissione(ordineMissione);
        taxi.setStato(Costanti.STATO_INSERITO);
        taxi.setStatoFlusso(Costanti.STATO_INSERITO);
        taxi.setToBeCreated();

        validaCRUD(taxi);
        taxi = ordineMissioneTaxiRepository.save(taxi);

        log.debug("Created Information for OrdineMissioneTaxi: {}", taxi);
        return taxi;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneTaxi updateTaxi(OrdineMissioneTaxi ordineMissioneTaxi)
            throws AwesomeException, OptimisticLockException {

        OrdineMissioneTaxi taxiDB = ordineMissioneTaxiRepository.findById(ordineMissioneTaxi.getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "Taxi Ordine di Missione da aggiornare inesistente."));

        OrdineMissione ordineMissione = taxiDB.getOrdineMissione();
        if (ordineMissione != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissione);
        }

        // Aggiorno solo i campi presenti nell'entità
        taxiDB.setMancanzaAssMezzi(ordineMissioneTaxi.getMancanzaAssMezzi());
        taxiDB.setMancanzaMezzi(ordineMissioneTaxi.getMancanzaMezzi());
        taxiDB.setMotiviHandicap(ordineMissioneTaxi.getMotiviHandicap());
        taxiDB.setTrasportoMateriali(ordineMissioneTaxi.getTrasportoMateriali());
        taxiDB.setUtilizzoAltriMotivi(ordineMissioneTaxi.getUtilizzoAltriMotivi());

        taxiDB.setToBeUpdated();

        validaCRUD(taxiDB);

        taxiDB = ordineMissioneTaxiRepository.save(taxiDB);

        log.debug("Updated Information for Taxi Ordine di Missione: {}", taxiDB);
        return taxiDB;
    }

    private void validaCRUD(OrdineMissioneTaxi ordineMissioneTaxi) {
        // Controlla se nessun motivo è selezionato
        if (Utility.nvl(ordineMissioneTaxi.getMancanzaAssMezzi(), "N").equals("N") &&
                Utility.nvl(ordineMissioneTaxi.getMancanzaMezzi(), "N").equals("N") &&
                Utility.nvl(ordineMissioneTaxi.getTrasportoMateriali(), "N").equals("N") &&
                Utility.nvl(ordineMissioneTaxi.getMotiviHandicap(), "N").equals("N")) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Indicare almeno un motivo per la richiesta di utilizzo del taxi.");
        }

        int countMotivi = Utility.countMotiviRichiestaMezzi(
                ordineMissioneTaxi.getMancanzaAssMezzi(),
                ordineMissioneTaxi.getMancanzaMezzi(),
                ordineMissioneTaxi.getTrasportoMateriali(),
                ordineMissioneTaxi.getMotiviHandicap()
        );

        if (countMotivi > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Indicare SOLO un motivo per la richiesta di utilizzo del taxi.");
        }
    }


    @Transactional(readOnly = true)
    public CMISFileAttachment uploadAllegato(Long idTaxi, InputStream inputStream, String name, MimeTypes mimeTypes)
            throws AwesomeException {

        OrdineMissioneTaxi taxi = ordineMissioneTaxiRepository.findById(idTaxi)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Taxi non trovato con ID: " + idTaxi));

        OrdineMissione ordineMissione = taxi.getOrdineMissione();
        if (ordineMissione != null) {
            return cmisOrdineMissioneService.uploadAttachmentTaxi(ordineMissione, idTaxi, inputStream, name, mimeTypes);
        }
        throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine Missione non presente per il taxi ID: " + idTaxi);
    }


    @Transactional(readOnly = true)
    public List<CMISFileAttachment> getAttachments(Long idTaxi) throws AwesomeException {
        if (idTaxi == null) return List.of();

        OrdineMissioneTaxi taxi = ordineMissioneTaxiRepository.findById(idTaxi)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Taxi non trovato con ID: " + idTaxi));

        OrdineMissione ordineMissione = taxi.getOrdineMissione();
        return cmisOrdineMissioneService.getAttachmentsTaxi(ordineMissione, idTaxi);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteTaxi(OrdineMissione ordineMissione) {
        OrdineMissioneTaxi ordineMissioneTaxi = ordineMissioneTaxiRepository.getTaxi(ordineMissione);

        if (ordineMissioneTaxi != null) {
            cancellaOrdineMissioneTaxi(ordineMissioneTaxi);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteTaxi(Long idTaxi) throws AwesomeException, OptimisticLockException {
        OrdineMissioneTaxi taxi = ordineMissioneTaxiRepository.findById(idTaxi)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Taxi non trovato con ID: " + idTaxi));

        String nodeRef = cmisOrdineMissioneService.getNodeRefOrdineMissioneTaxi(taxi, false);
        if (nodeRef != null) {
            missioniCMISService.deleteNode(nodeRef);
        }
        cancellaOrdineMissioneTaxi(taxi);
    }


    private void cancellaSpostamenti(
            OrdineMissioneTaxi ordineMissioneTaxi)
            throws AwesomeException {
        List<SpostamentiTaxi> listaSpostamenti = spostamentiTaxiRepository.getSpostamenti(ordineMissioneTaxi);
        if (listaSpostamenti != null && !listaSpostamenti.isEmpty()) {
            for (Iterator<SpostamentiTaxi> iterator = listaSpostamenti.iterator(); iterator.hasNext(); ) {
                SpostamentiTaxi spostamento = iterator.next();
                cancellaSpostamento(spostamento);
            }
        }
    }

    private void cancellaOrdineMissioneTaxi(OrdineMissioneTaxi taxi) throws AwesomeException {
        cancellaSpostamenti(taxi);

        if (!taxi.isStatoNonInviatoAlFlusso()) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile cancellare il taxi");
        }

        taxi.setToBeUpdated();
        taxi.setStato(Costanti.STATO_ANNULLATO);
        ordineMissioneTaxiRepository.save(taxi);

        OrdineMissione ordineMissione = taxi.getOrdineMissione();
        List<StorageObject> allegati = cmisOrdineMissioneService.getAttachmentsTaxi(ordineMissione);
        if (allegati != null) {
            for (StorageObject obj : allegati) {
                if (ordineMissione != null && StringUtils.hasLength(ordineMissione.getIdFlusso())) {
                    StorageObject folder = cmisOrdineMissioneService.recuperoFolderOrdineMissione(ordineMissione);
                    missioniCMISService.eliminaFilePresenteNelFlusso(obj.getKey(), folder);
                } else {
                    missioniCMISService.deleteNode(obj.getKey());
                }
            }
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiTaxi createSpostamentoTaxi(SpostamentiTaxi spostamento) throws AwesomeException, OptimisticLockException {
        spostamento.setUser(securityService.getCurrentUserLogin());
        spostamento.setStato(Costanti.STATO_INSERITO);

        SpostamentiTaxi finalSpostamento = spostamento;
        OrdineMissioneTaxi taxi = ordineMissioneTaxiRepository.findById(spostamento.getTaxi().getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Taxi non trovato con ID: " + finalSpostamento.getTaxi().getId()));

        ordineMissioneService.controlloOperazioniCRUDDaGui(taxi.getOrdineMissione());

        if (spostamento.getPercorsoDa() == null || spostamento.getPercorsoA() == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Il punto di partenza e il punto di destinazione degli Spostamenti Taxi devono essere inseriti");
        }

        spostamento.setTaxi(taxi);
        Long maxRiga = spostamentiTaxiRepository.getMaxRigaSpostamenti(taxi);
        spostamento.setRiga(maxRiga != null ? maxRiga + 1 : 1);
        spostamento.setToBeCreated();

        validaCRUD(spostamento);
        spostamento = spostamentiTaxiRepository.save(spostamento);

        log.debug("Created Spostamento Taxi: {}", spostamento);
        return spostamento;
    }

    private void validaCRUD(SpostamentiTaxi spostamentiTaxi) {
        if (StringUtils.isEmpty(spostamentiTaxi.getPercorsoDa()) ||
                StringUtils.isEmpty(spostamentiTaxi.getPercorsoA())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati degli spostamenti incompleti.");
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSpostamenti(Long idSpostamento) throws AwesomeException, OptimisticLockException {
        SpostamentiTaxi spostamento = spostamentiTaxiRepository.findById(idSpostamento)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Spostamento non trovato con ID: " + idSpostamento));

        cancellaSpostamento(spostamento);
    }



    private void cancellaSpostamento(SpostamentiTaxi spostamento) {
        spostamento.setToBeUpdated();
        spostamento.setStato(Costanti.STATO_ANNULLATO);
        spostamentiTaxiRepository.save(spostamento);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiTaxi updateSpostamenti(SpostamentiTaxi spostamento) throws AwesomeException, OptimisticLockException {
        SpostamentiTaxi db = spostamentiTaxiRepository.findById(spostamento.getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN,
                        "Spostamenti Taxi Ordine di Missione da aggiornare inesistente."));

        ordineMissioneService.controlloOperazioniCRUDDaGui(db.getTaxi().getOrdineMissione());

        if (spostamento.getPercorsoDa() == null || spostamento.getPercorsoA() == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Il punto di partenza e il punto di destinazione degli Spostamenti Taxi devono essere inseriti");
        }

        db.setPercorsoDa(spostamento.getPercorsoDa());
        db.setPercorsoA(spostamento.getPercorsoA());
        db.setToBeUpdated();

        db = spostamentiTaxiRepository.save(db);

        log.debug("Updated Spostamento Taxi: {}", db);
        return db;
    }


    @Transactional(readOnly = true)
    public List<SpostamentiTaxi> getSpostamentiTaxi(Long idTaxi) throws AwesomeException {
        OrdineMissioneTaxi taxi = ordineMissioneTaxiRepository.findById(idTaxi)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Taxi non trovato con ID: " + idTaxi));

        List<SpostamentiTaxi> spostamenti = spostamentiTaxiRepository.getSpostamenti(taxi);
        return spostamenti != null ? spostamenti : List.of();
    }


    @Transactional(readOnly = true)
    public Map<String, byte[]> printOrdineMissioneTaxi(Long idMissione) throws AwesomeException {
        String username = securityService.getCurrentUserLogin();
        OrdineMissioneTaxi ordineMissioneTaxi = getTaxi(idMissione, true);
        OrdineMissione ordineMissione = ordineMissioneTaxi.getOrdineMissione();
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        byte[] printOrdineMissione = null;
        String fileName = null;
        if ((ordineMissione.isStatoInviatoAlFlusso() && (!ordineMissione.isMissioneInserita() || !ordineMissione.isMissioneDaValidare())) || (ordineMissione.isStatoFlussoApprovato())) {
            map = cmisOrdineMissioneService.getFileOrdineMissioneTaxi(ordineMissioneTaxi);
        } else {
            fileName = "OrdineMissioneTaxi" + idMissione + ".pdf";
            printOrdineMissione = printTaxi(username, ordineMissioneTaxi);
//            if (ordineMissioneTaxi.isRichiestaTaxiInserito()) {
//                cmisOrdineMissioneService.salvaStampaTaxiSuCMIS(username, printOrdineMissione, ordineMissioneTaxi);
//            }
            map.put(fileName, printOrdineMissione);
        }
        return map;
    }

    private byte[] printTaxi(String username,
                                    OrdineMissioneTaxi ordineMissioneTaxi)
            throws AwesomeException {
        return printOrdineMissioneTaxiService.printOrdineMissioneTaxi(ordineMissioneTaxi, username);
    }

}
