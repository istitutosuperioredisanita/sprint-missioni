package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoNoleggio;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.OrdineMissioneAutoNoleggioRepository;
import it.cnr.si.missioni.repository.SpostamentiAutoNoleggioRepository;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.service.SecurityService;
import it.cnr.si.spring.storage.StorageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.OptimisticLockException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class OrdineMissioneAutoNoleggioService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneAutoNoleggioService.class);
    private final OrdineMissioneAutoNoleggioRepository ordineMissioneAutoNoleggioRepository;
    private final SpostamentiAutoNoleggioRepository spostamentiAutoNoleggioRepository;
    private final OrdineMissioneService ordineMissioneService;
    private final MissioniCMISService missioniCMISService;
    private final CMISOrdineMissioneService cmisOrdineMissioneService;
    private final CRUDComponentSession crudServiceBean;
    private final SecurityService securityService;
    private final PrintOrdineMissioneAutoNoleggioService printOrdineMissioneAutoNoleggioService;

    @Autowired
    public OrdineMissioneAutoNoleggioService(
            OrdineMissioneAutoNoleggioRepository ordineMissioneAutoNoleggioRepository,
            SpostamentiAutoNoleggioRepository spostamentiAutoNoleggioRepository,
            OrdineMissioneService ordineMissioneService,
            MissioniCMISService missioniCMISService,
            CMISOrdineMissioneService cmisOrdineMissioneService,
            CRUDComponentSession crudServiceBean,
            SecurityService securityService, PrintOrdineMissioneAutoNoleggioService printOrdineMissioneAutoNoleggioService) {
        this.ordineMissioneAutoNoleggioRepository = ordineMissioneAutoNoleggioRepository;
        this.spostamentiAutoNoleggioRepository = spostamentiAutoNoleggioRepository;
        this.ordineMissioneService = ordineMissioneService;
        this.missioniCMISService = missioniCMISService;
        this.cmisOrdineMissioneService = cmisOrdineMissioneService;
        this.crudServiceBean = crudServiceBean;
        this.securityService = securityService;
        this.printOrdineMissioneAutoNoleggioService = printOrdineMissioneAutoNoleggioService;
    }

    @Transactional(readOnly = true)
    public OrdineMissioneAutoNoleggio getAutoNoleggio(Long idMissione) throws ComponentException {
        return getAutoNoleggio(idMissione, false);
    }

    @Transactional(readOnly = true)
    public OrdineMissioneAutoNoleggio getAutoNoleggio(Long idMissione, Boolean valorizzaDatiCollegati) throws ComponentException {
        OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, idMissione);

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

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAutoNoleggio createAutoNoleggio(OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        ordineMissioneAutoNoleggio.setUser(securityService.getCurrentUserLogin());
        OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, ordineMissioneAutoNoleggio.getOrdineMissione().getId());
        if (ordineMissione != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissione);
        }
        ordineMissioneAutoNoleggio.setOrdineMissione(ordineMissione);
        ordineMissioneAutoNoleggio.setStato(Costanti.STATO_INSERITO);
        ordineMissioneAutoNoleggio.setStatoFlusso(Costanti.STATO_INSERITO);
        ordineMissioneAutoNoleggio.setToBeCreated();

        validaCRUD(ordineMissioneAutoNoleggio);
        ordineMissioneAutoNoleggio = (OrdineMissioneAutoNoleggio) crudServiceBean.creaConBulk(ordineMissioneAutoNoleggio);
        log.debug("Created Information for OrdineMissioneAutoNoleggio: {}", ordineMissioneAutoNoleggio);
        return ordineMissioneAutoNoleggio;
    }

    private void validaCRUD(OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio) {

        if (Utility.nvl(ordineMissioneAutoNoleggio.getEsigenzeServizio(), "N").equals("N") &&
                Utility.nvl(ordineMissioneAutoNoleggio.getMotivataEccezionalita(), "N").equals("N") &&
                Utility.nvl(ordineMissioneAutoNoleggio.getNote(), "N").equals("N")) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Indicare almeno un motivo per la richiesta di utilizzo dell'auto a noleggio.");
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAutoNoleggio updateAutoNoleggio(OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {

        OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggioDB = (OrdineMissioneAutoNoleggio) crudServiceBean.findById(OrdineMissioneAutoNoleggio.class, ordineMissioneAutoNoleggio.getId());

        if (ordineMissioneAutoNoleggioDB == null)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Auto Noleggio Ordine di Missione da aggiornare inesistente.");

        if (ordineMissioneAutoNoleggioDB.getOrdineMissione() != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissioneAutoNoleggioDB.getOrdineMissione());
        }

        ordineMissioneAutoNoleggioDB.setEsigenzeServizio(ordineMissioneAutoNoleggio.getEsigenzeServizio());
        ordineMissioneAutoNoleggioDB.setMotivataEccezionalita(ordineMissioneAutoNoleggio.getMotivataEccezionalita());
        ordineMissioneAutoNoleggioDB.setNote(ordineMissioneAutoNoleggio.getNote());

        ordineMissioneAutoNoleggioDB.setToBeUpdated();

        validaCRUD(ordineMissioneAutoNoleggioDB);
        ordineMissioneAutoNoleggioDB = (OrdineMissioneAutoNoleggio) crudServiceBean.modificaConBulk(ordineMissioneAutoNoleggioDB);

        log.debug("Updated Information for Auto Noleggio Ordine di Missione: {}", ordineMissioneAutoNoleggioDB);
        return ordineMissioneAutoNoleggio;
    }


    @Transactional(readOnly = true)
    public List<CMISFileAttachment> getAttachments(Long idAutoNoleggio)
            throws ComponentException {
        if (idAutoNoleggio != null) {
            OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio = (OrdineMissioneAutoNoleggio) crudServiceBean.findById(
                    OrdineMissioneAutoNoleggio.class, idAutoNoleggio);
            OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class,
                    ordineMissioneAutoNoleggio.getOrdineMissione().getId());
            List<CMISFileAttachment> lista = cmisOrdineMissioneService.getAttachmentsAutoNoleggio(ordineMissione,idAutoNoleggio);
            return lista;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public CMISFileAttachment uploadAllegato(Long idAutoNoleggio,
                                             InputStream inputStream, String name, MimeTypes mimeTypes) throws ComponentException {
        OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio = (OrdineMissioneAutoNoleggio) crudServiceBean.findById(
                OrdineMissioneAutoNoleggio.class, idAutoNoleggio);
        OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class,
                ordineMissioneAutoNoleggio.getOrdineMissione().getId());
        if (ordineMissione != null) {
            return cmisOrdineMissioneService.uploadAttachmentAutoNoleggio(ordineMissione, idAutoNoleggio,
                    inputStream, name, mimeTypes);
        }
        return null;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAutoNoleggio(OrdineMissione ordineMissione) {
        OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio = ordineMissioneService.getAutoNoleggio(ordineMissione);

        if (ordineMissioneAutoNoleggio != null) {
            cancellaOrdineMissioneAutoNoleggio(ordineMissioneAutoNoleggio);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAutoNoleggio(Long idAutoNoleggioOrdineMissione) throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio = (OrdineMissioneAutoNoleggio) crudServiceBean.findById(OrdineMissioneAutoNoleggio.class, idAutoNoleggioOrdineMissione);

        if (ordineMissioneAutoNoleggio != null) {
            String nodeRef = cmisOrdineMissioneService.getNodeRefOrdineMissioneAutoNoleggio(ordineMissioneAutoNoleggio, false);
            if (nodeRef != null) {
                missioniCMISService.deleteNode(nodeRef);
            }
            cancellaOrdineMissioneAutoNoleggio(ordineMissioneAutoNoleggio);
        }
    }

    private void cancellaSpostamenti(
            OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio)
            throws ComponentException {
        List<SpostamentiAutoNoleggio> listaSpostamenti = spostamentiAutoNoleggioRepository.getSpostamenti(ordineMissioneAutoNoleggio);
        if (listaSpostamenti != null && !listaSpostamenti.isEmpty()) {
            for (Iterator<SpostamentiAutoNoleggio> iterator = listaSpostamenti.iterator(); iterator.hasNext(); ) {
                SpostamentiAutoNoleggio spostamento = iterator.next();
                cancellaSpostamento(spostamento);
            }
        }
    }

    private void cancellaOrdineMissioneAutoNoleggio(OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio)
            throws ComponentException {
        cancellaSpostamenti(ordineMissioneAutoNoleggio);
        if (ordineMissioneAutoNoleggio.isStatoNonInviatoAlFlusso()) {
            ordineMissioneAutoNoleggio.setToBeUpdated();
            ordineMissioneAutoNoleggio.setStato(Costanti.STATO_ANNULLATO);
            crudServiceBean.modificaConBulk(ordineMissioneAutoNoleggio);
            OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, ordineMissioneAutoNoleggio.getOrdineMissione().getId());

            List<StorageObject> listaAllegati = cmisOrdineMissioneService.getAttachmentsAutoNoleggio(ordineMissione);
            if (listaAllegati != null) {
                for (StorageObject object : listaAllegati) {
                    if (ordineMissione != null && StringUtils.hasLength(ordineMissione.getIdFlusso())) {
                        StorageObject folderOrdineMissione = cmisOrdineMissioneService.recuperoFolderOrdineMissione(ordineMissione);
                        missioniCMISService.eliminaFilePresenteNelFlusso(object.getKey(), folderOrdineMissione);
                    } else {
                        missioniCMISService.deleteNode(object.getKey());
                    }
                }
            }
        } else {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Non è possibile cancellare l'auto a noleggio.");
        }
    }



    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiAutoNoleggio createSpostamentoAutoNoleggio(SpostamentiAutoNoleggio spostamentoAutoNoleggio) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        spostamentoAutoNoleggio.setUser(securityService.getCurrentUserLogin());
        spostamentoAutoNoleggio.setStato(Costanti.STATO_INSERITO);
        OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio = (OrdineMissioneAutoNoleggio) crudServiceBean.findById(OrdineMissioneAutoNoleggio.class, spostamentoAutoNoleggio.getAutoNoleggio().getId());
        if (ordineMissioneAutoNoleggio != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissioneAutoNoleggio.getOrdineMissione());
        }
        if (spostamentoAutoNoleggio.getPercorsoDa() == null || spostamentoAutoNoleggio.getPercorsoA() == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Il punto di partenza e il punto di destinazione degli Spostamenti Auto Noleggio devono essere inseriti");
        }

        spostamentoAutoNoleggio.setAutoNoleggio(ordineMissioneAutoNoleggio);
        Long maxRiga = spostamentiAutoNoleggioRepository.getMaxRigaSpostamenti(ordineMissioneAutoNoleggio);
        if (maxRiga == null) {
            maxRiga = Long.valueOf(0);
        }
        maxRiga = maxRiga + 1;
        spostamentoAutoNoleggio.setRiga(maxRiga);
        spostamentoAutoNoleggio.setToBeCreated();
        validaCRUD(spostamentoAutoNoleggio);
        spostamentoAutoNoleggio = (SpostamentiAutoNoleggio) crudServiceBean.creaConBulk(spostamentoAutoNoleggio);
        log.debug("Created Information for OrdineMissioneAutoNoleggio: {}", ordineMissioneAutoNoleggio);
        return spostamentoAutoNoleggio;
    }


    private void validaCRUD(SpostamentiAutoNoleggio spostamentiAutoNoleggio) {
        if (StringUtils.isEmpty(spostamentiAutoNoleggio.getPercorsoDa()) ||
                StringUtils.isEmpty(spostamentiAutoNoleggio.getPercorsoA())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati degli spostamenti incompleti.");
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSpostamenti(Long idSpostamenti) throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        SpostamentiAutoNoleggio spostamentiAutoNoleggio = (SpostamentiAutoNoleggio) crudServiceBean.findById(SpostamentiAutoNoleggio.class, idSpostamenti);

        if (spostamentiAutoNoleggio != null) {
            cancellaSpostamento(spostamentiAutoNoleggio);
        }
    }


    private void cancellaSpostamento(SpostamentiAutoNoleggio spostamentoAutoNoleggio) throws ComponentException {
        spostamentoAutoNoleggio.setToBeUpdated();
        spostamentoAutoNoleggio.setStato(Costanti.STATO_ANNULLATO);
        crudServiceBean.modificaConBulk(spostamentoAutoNoleggio);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiAutoNoleggio updateSpostamenti(SpostamentiAutoNoleggio spostamentoAutoNoleggio)
            throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {

        SpostamentiAutoNoleggio spostamentiAutoNoleggioDB = (SpostamentiAutoNoleggio) crudServiceBean.findById(SpostamentiAutoNoleggio.class, spostamentoAutoNoleggio.getId());
        if (spostamentiAutoNoleggioDB == null)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Spostamenti Auto Noleggio Ordine di Missione da aggiornare inesistente.");

        if (spostamentiAutoNoleggioDB.getAutoNoleggio().getOrdineMissione() != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(spostamentiAutoNoleggioDB.getAutoNoleggio().getOrdineMissione());
        }


        if (spostamentoAutoNoleggio.getPercorsoDa() == null || spostamentoAutoNoleggio.getPercorsoA() == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Il punto di partenza e il punto di destinazione degli Spostamenti Auto Noleggio devono essere inseriti");
        }

        spostamentiAutoNoleggioDB.setPercorsoDa(spostamentoAutoNoleggio.getPercorsoDa());
        spostamentiAutoNoleggioDB.setPercorsoA(spostamentoAutoNoleggio.getPercorsoA());
        spostamentiAutoNoleggioDB.setToBeUpdated();
        spostamentiAutoNoleggioDB = (SpostamentiAutoNoleggio) crudServiceBean.modificaConBulk(spostamentiAutoNoleggioDB);

        log.debug("Updated Information for Spostamenti: {}", spostamentiAutoNoleggioDB);
        return spostamentoAutoNoleggio;
    }



    @Transactional(readOnly = true)
    public List<SpostamentiAutoNoleggio> getSpostamentiAutoNoleggio(Long idAutoNoleggioOrdineMissione) throws ComponentException {
        OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio = (OrdineMissioneAutoNoleggio) crudServiceBean.findById(OrdineMissioneAutoNoleggio.class, idAutoNoleggioOrdineMissione);

        if (ordineMissioneAutoNoleggio != null) {
            return spostamentiAutoNoleggioRepository.getSpostamenti(ordineMissioneAutoNoleggio);
        }
        return null;
    }


    @Transactional(readOnly = true)
    public Map<String, byte[]> printOrdineMissioneAutoNoleggio(Long idMissione) throws AwesomeException, ComponentException {
        String username = securityService.getCurrentUserLogin();
        OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio = getAutoNoleggio(idMissione, true);
        OrdineMissione ordineMissione = ordineMissioneAutoNoleggio.getOrdineMissione();
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        byte[] printOrdineMissione = null;
        String fileName = null;
        if ((ordineMissione.isStatoInviatoAlFlusso() && (!ordineMissione.isMissioneInserita() || !ordineMissione.isMissioneDaValidare())) || (ordineMissione.isStatoFlussoApprovato())) {
            map = cmisOrdineMissioneService.getFileOrdineMissioneAutoNoleggio(ordineMissioneAutoNoleggio);
        } else {
            fileName = "OrdineMissioneAutoNoleggio" + idMissione + ".pdf";
            printOrdineMissione = printAutoNoleggio(username, ordineMissioneAutoNoleggio);
            if (ordineMissioneAutoNoleggio.isRichiestaAutoNoleggioInserito()) {
                cmisOrdineMissioneService.salvaStampaAutoNoleggioSuCMIS(username, printOrdineMissione, ordineMissioneAutoNoleggio);
            }
            map.put(fileName, printOrdineMissione);
        }
        return map;
    }

    private byte[] printAutoNoleggio(String username,
                             OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio)
            throws ComponentException {
        return printOrdineMissioneAutoNoleggioService.printOrdineMissioneAutoNoleggio(ordineMissioneAutoNoleggio, username);
    }

}