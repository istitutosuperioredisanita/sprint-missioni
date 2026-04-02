package it.cnr.si.missioni.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.missioni.repository.OrdineMissioneAutoNoleggioRepository;
import it.cnr.si.missioni.repository.OrdineMissioneRepository;
import it.cnr.si.missioni.repository.SpostamentiAutoNoleggioRepository;
import it.cnr.si.missioni.service.security.SecurityService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.spring.storage.StorageObject;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrdineMissioneAutoNoleggioService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneAutoNoleggioService.class);
    private final OrdineMissioneAutoNoleggioRepository ordineMissioneAutoNoleggioRepository;
    private final SpostamentiAutoNoleggioRepository spostamentiAutoNoleggioRepository;
    private final OrdineMissioneRepository ordineMissioneRepository;
    private final OrdineMissioneService ordineMissioneService;
    private final MissioniCMISService missioniCMISService;
    private final CMISOrdineMissioneService cmisOrdineMissioneService;
    private final SecurityService securityService;
    private final PrintOrdineMissioneAutoNoleggioService printOrdineMissioneAutoNoleggioService;

    @Autowired
    public OrdineMissioneAutoNoleggioService(
            OrdineMissioneAutoNoleggioRepository ordineMissioneAutoNoleggioRepository,
            SpostamentiAutoNoleggioRepository spostamentiAutoNoleggioRepository,
            OrdineMissioneRepository ordineMissioneRepository,
            OrdineMissioneService ordineMissioneService,
            MissioniCMISService missioniCMISService,
            CMISOrdineMissioneService cmisOrdineMissioneService,
            SecurityService securityService,
            PrintOrdineMissioneAutoNoleggioService printOrdineMissioneAutoNoleggioService) {

        this.ordineMissioneAutoNoleggioRepository = ordineMissioneAutoNoleggioRepository;
        this.spostamentiAutoNoleggioRepository = spostamentiAutoNoleggioRepository;
        this.ordineMissioneRepository = ordineMissioneRepository;
        this.ordineMissioneService = ordineMissioneService;
        this.missioniCMISService = missioniCMISService;
        this.cmisOrdineMissioneService = cmisOrdineMissioneService;
        this.securityService = securityService;
        this.printOrdineMissioneAutoNoleggioService = printOrdineMissioneAutoNoleggioService;
    }

    // --- GET Auto Noleggio ---
    @Transactional(readOnly = true)
    public OrdineMissioneAutoNoleggio getAutoNoleggio(Long idMissione) throws AwesomeException {
        return getAutoNoleggio(idMissione, false);
    }

    @Transactional(readOnly = true)
    public OrdineMissioneAutoNoleggio getAutoNoleggio(Long idMissione, Boolean valorizzaDatiCollegati) throws AwesomeException {
        OrdineMissione ordineMissione = ordineMissioneRepository.findById(idMissione)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Ordine missione non trovato"));

        if (ordineMissione != null) {
            OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio = ordineMissioneAutoNoleggioRepository.getAutoNoleggio(ordineMissione);

            if (valorizzaDatiCollegati && ordineMissioneAutoNoleggio != null) {
                List<SpostamentiAutoNoleggio> list = spostamentiAutoNoleggioRepository.getSpostamenti(ordineMissioneAutoNoleggio);
                ordineMissioneAutoNoleggio.setOrdineMissione(ordineMissione);
                ordineMissioneAutoNoleggio.setListSpostamenti(list);
            }
            return ordineMissioneAutoNoleggio;
        }
        return null;
    }

    // --- CREATE Auto Noleggio ---
    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAutoNoleggio createAutoNoleggio(OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio) throws AwesomeException {
        ordineMissioneAutoNoleggio.setUser(securityService.getCurrentUserLogin());

        OrdineMissione ordineMissione = ordineMissioneRepository.findById(
                        (Long) ordineMissioneAutoNoleggio.getOrdineMissione().getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Ordine missione non trovato"));

        ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissione);

        ordineMissioneAutoNoleggio.setOrdineMissione(ordineMissione);
        ordineMissioneAutoNoleggio.setStato(Costanti.STATO_INSERITO);
        ordineMissioneAutoNoleggio.setStatoFlusso(Costanti.STATO_INSERITO);
        ordineMissioneAutoNoleggio.setToBeCreated();

        validaCRUD(ordineMissioneAutoNoleggio);
        ordineMissioneAutoNoleggio = ordineMissioneAutoNoleggioRepository.save(ordineMissioneAutoNoleggio);

        log.debug("Created Auto Noleggio: {}", ordineMissioneAutoNoleggio);
        return ordineMissioneAutoNoleggio;
    }

    private void validaCRUD(OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio) {
        if ("N".equals(Utility.nvl(ordineMissioneAutoNoleggio.getEsigenzeServizio(), "N")) &&
                "N".equals(Utility.nvl(ordineMissioneAutoNoleggio.getMotivataEccezionalita(), "N"))) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Indicare almeno un motivo per la richiesta di utilizzo dell'auto a noleggio.");
        }

        int countMotivi = Utility.countMotiviRichiestaMezzi(
                ordineMissioneAutoNoleggio.getEsigenzeServizio(),
                ordineMissioneAutoNoleggio.getMotivataEccezionalita()
        );

        if (countMotivi > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Indicare SOLO un motivo per la richiesta di utilizzo dell'auto a noleggio.");
        }
    }

    // --- UPDATE Auto Noleggio ---
    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAutoNoleggio updateAutoNoleggio(OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio) throws AwesomeException {

        OrdineMissioneAutoNoleggio dbAuto = ordineMissioneAutoNoleggioRepository.findById(ordineMissioneAutoNoleggio.getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Auto Noleggio Ordine di Missione inesistente."));

        if (dbAuto.getOrdineMissione() != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(dbAuto.getOrdineMissione());
        }

        dbAuto.setEsigenzeServizio(ordineMissioneAutoNoleggio.getEsigenzeServizio());
        dbAuto.setMotivataEccezionalita(ordineMissioneAutoNoleggio.getMotivataEccezionalita());
        dbAuto.setNote(ordineMissioneAutoNoleggio.getNote());
        dbAuto.setToBeUpdated();

        validaCRUD(dbAuto);
        dbAuto = ordineMissioneAutoNoleggioRepository.save(dbAuto);

        log.debug("Updated Auto Noleggio: {}", dbAuto);
        return dbAuto;
    }

    // --- DELETE Auto Noleggio ---
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAutoNoleggio(Long idAutoNoleggio) throws AwesomeException {
        OrdineMissioneAutoNoleggio auto = ordineMissioneAutoNoleggioRepository.findById(idAutoNoleggio)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Auto Noleggio non trovato"));

        String nodeRef = cmisOrdineMissioneService.getNodeRefOrdineMissioneAutoNoleggio(auto, false);
        if (nodeRef != null) {
            missioniCMISService.deleteNode(nodeRef);
        }

        cancellaOrdineMissioneAutoNoleggio(auto);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAutoNoleggio(OrdineMissione ordineMissione) {
        Optional<OrdineMissioneAutoNoleggio> autoNoleggioOpt =
                Optional.ofNullable(ordineMissioneAutoNoleggioRepository.getAutoNoleggio(ordineMissione));

        autoNoleggioOpt.ifPresent(autoNoleggio -> {
            autoNoleggio.setStato(Costanti.STATO_ANNULLATO);
            autoNoleggio.setToBeUpdated();
            ordineMissioneAutoNoleggioRepository.save(autoNoleggio);
            log.debug("Annullato OrdineMissioneAutoNoleggio: {}", autoNoleggio);
        });
    }

    private void cancellaOrdineMissioneAutoNoleggio(OrdineMissioneAutoNoleggio auto) throws AwesomeException {
        // Cancella spostamenti
        List<SpostamentiAutoNoleggio> spostamenti = spostamentiAutoNoleggioRepository.getSpostamenti(auto);
        if (spostamenti != null) {
            for (SpostamentiAutoNoleggio s : spostamenti) {
                cancellaSpostamento(s);
            }
        }

        if (auto.isStatoNonInviatoAlFlusso()) {
            auto.setToBeUpdated();
            auto.setStato(Costanti.STATO_ANNULLATO);
            ordineMissioneAutoNoleggioRepository.save(auto);

            OrdineMissione ordineMissione = auto.getOrdineMissione();
            List<StorageObject> allegati = cmisOrdineMissioneService.getAttachmentsAutoNoleggio(ordineMissione);
            if (allegati != null) {
                for (StorageObject obj : allegati) {
                    if (ordineMissione != null && StringUtils.hasLength(ordineMissione.getIdFlusso())) {
                        missioniCMISService.eliminaFilePresenteNelFlusso(
                                obj.getKey(),
                                cmisOrdineMissioneService.recuperoFolderOrdineMissione(ordineMissione));
                    } else {
                        missioniCMISService.deleteNode(obj.getKey());
                    }
                }
            }
        } else {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Non è possibile cancellare l'auto a noleggio.");
        }
    }

    private void cancellaSpostamento(SpostamentiAutoNoleggio s) {
        s.setToBeUpdated();
        s.setStato(Costanti.STATO_ANNULLATO);
        spostamentiAutoNoleggioRepository.save(s);
    }

    // --- GET / UPLOAD Allegati ---
    @Transactional(readOnly = true)
    public List<CMISFileAttachment> getAttachments(Long idAutoNoleggio) throws AwesomeException {
        OrdineMissioneAutoNoleggio auto = ordineMissioneAutoNoleggioRepository.findById(idAutoNoleggio)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Auto Noleggio non trovato"));

        OrdineMissione ordineMissione = auto.getOrdineMissione();
        return cmisOrdineMissioneService.getAttachmentsAutoNoleggio(ordineMissione, idAutoNoleggio);
    }

    @Transactional(readOnly = true)
    public CMISFileAttachment uploadAllegato(Long idAutoNoleggio,
                                             InputStream inputStream, String name, MimeTypes mimeTypes) throws AwesomeException {
        OrdineMissioneAutoNoleggio auto = ordineMissioneAutoNoleggioRepository.findById(idAutoNoleggio)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Auto Noleggio non trovato"));

        OrdineMissione ordineMissione = auto.getOrdineMissione();
        return cmisOrdineMissioneService.uploadAttachmentAutoNoleggio(ordineMissione, idAutoNoleggio, inputStream, name, mimeTypes);
    }

    // --- SPOSTAMENTI ---
    @Transactional(readOnly = true)
    public List<SpostamentiAutoNoleggio> getSpostamentiAutoNoleggio(Long idAutoNoleggio) throws AwesomeException {
        OrdineMissioneAutoNoleggio auto = ordineMissioneAutoNoleggioRepository.findById(idAutoNoleggio)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Auto Noleggio non trovato"));

        return spostamentiAutoNoleggioRepository.getSpostamenti(auto);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiAutoNoleggio createSpostamentoAutoNoleggio(SpostamentiAutoNoleggio s) throws AwesomeException {
        s.setUser(securityService.getCurrentUserLogin());
        s.setStato(Costanti.STATO_INSERITO);

        OrdineMissioneAutoNoleggio auto = ordineMissioneAutoNoleggioRepository.findById(s.getAutoNoleggio().getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Auto Noleggio non trovato"));

        ordineMissioneService.controlloOperazioniCRUDDaGui(auto.getOrdineMissione());

        if (s.getPercorsoDa() == null || s.getPercorsoA() == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Percorso da/a obbligatorio");
        }

        s.setAutoNoleggio(auto);
        Long maxRiga = spostamentiAutoNoleggioRepository.getMaxRigaSpostamenti(auto);
        s.setRiga(maxRiga == null ? 1 : maxRiga + 1);
        s.setToBeCreated();

        validaCRUD(s);
        return spostamentiAutoNoleggioRepository.save(s);
    }

    private void validaCRUD(SpostamentiAutoNoleggio s) {
        if (StringUtils.isEmpty(s.getPercorsoDa()) || StringUtils.isEmpty(s.getPercorsoA())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati spostamento incompleti.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiAutoNoleggio updateSpostamenti(SpostamentiAutoNoleggio s) throws AwesomeException {
        SpostamentiAutoNoleggio db = spostamentiAutoNoleggioRepository.findById(s.getId())
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Spostamento inesistente."));

        ordineMissioneService.controlloOperazioniCRUDDaGui(db.getAutoNoleggio().getOrdineMissione());

        if (s.getPercorsoDa() == null || s.getPercorsoA() == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Percorso da/a obbligatorio");
        }

        db.setPercorsoDa(s.getPercorsoDa());
        db.setPercorsoA(s.getPercorsoA());
        db.setToBeUpdated();

        return spostamentiAutoNoleggioRepository.save(db);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSpostamenti(Long idSpostamenti) throws AwesomeException {
        SpostamentiAutoNoleggio s = spostamentiAutoNoleggioRepository.findById(idSpostamenti)
                .orElseThrow(() -> new AwesomeException(CodiciErrore.ERRGEN, "Spostamenti non trovati"));

        cancellaSpostamento(s);
    }

    // --- PRINT ---
    @Transactional(readOnly = true)
    public Map<String, byte[]> printOrdineMissioneAutoNoleggio(Long idMissione) throws AwesomeException {
        String username = securityService.getCurrentUserLogin();
        OrdineMissioneAutoNoleggio auto = getAutoNoleggio(idMissione, true);
        OrdineMissione ordineMissione = auto.getOrdineMissione();

        Map<String, byte[]> map = new HashMap<>();
        if ((ordineMissione.isStatoInviatoAlFlusso() && (!ordineMissione.isMissioneInserita() || !ordineMissione.isMissioneDaValidare()))
                || ordineMissione.isStatoFlussoApprovato()) {
            return cmisOrdineMissioneService.getFileOrdineMissioneAutoNoleggio(auto);
        } else {
            String fileName = "OrdineMissioneAutoNoleggio" + idMissione + ".pdf";
            byte[] pdf = printAutoNoleggio(username, auto);
            map.put(fileName, pdf);
        }
        return map;
    }

    private byte[] printAutoNoleggio(String username, OrdineMissioneAutoNoleggio auto) throws AwesomeException {
        return printOrdineMissioneAutoNoleggioService.printOrdineMissioneAutoNoleggio(auto, username);
    }
}