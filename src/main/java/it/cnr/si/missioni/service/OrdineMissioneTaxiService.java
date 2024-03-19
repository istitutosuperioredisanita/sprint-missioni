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
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneTaxi;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.OrdineMissioneTaxiRepository;
import it.cnr.si.missioni.repository.SpostamentiTaxiRepository;
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
public class OrdineMissioneTaxiService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneTaxiService.class);
    private final OrdineMissioneTaxiRepository ordineMissioneTaxiRepository;
    private final SpostamentiTaxiRepository spostamentiTaxiRepository;
    private final OrdineMissioneService ordineMissioneService;
    private final MissioniCMISService missioniCMISService;
    private final CMISOrdineMissioneService cmisOrdineMissioneService;
    private final CRUDComponentSession crudServiceBean;
    private final SecurityService securityService;
    private final PrintOrdineMissioneTaxiService printOrdineMissioneTaxiService;

    @Autowired
    public OrdineMissioneTaxiService(
            OrdineMissioneTaxiRepository ordineMissioneTaxiRepository,
            SpostamentiTaxiRepository spostamentiTaxiRepository,
            OrdineMissioneService ordineMissioneService,
            MissioniCMISService missioniCMISService,
            CMISOrdineMissioneService cmisOrdineMissioneService,
            CRUDComponentSession crudServiceBean,
            SecurityService securityService, PrintOrdineMissioneTaxiService printOrdineMissioneTaxiService) {
        this.ordineMissioneTaxiRepository = ordineMissioneTaxiRepository;
        this.spostamentiTaxiRepository = spostamentiTaxiRepository;
        this.ordineMissioneService = ordineMissioneService;
        this.missioniCMISService = missioniCMISService;
        this.cmisOrdineMissioneService = cmisOrdineMissioneService;
        this.crudServiceBean = crudServiceBean;
        this.securityService = securityService;
        this.printOrdineMissioneTaxiService = printOrdineMissioneTaxiService;
    }

    @Transactional(readOnly = true)
    public OrdineMissioneTaxi getTaxi(Long idMissione) throws ComponentException {
        return getTaxi(idMissione, false);
    }

    @Transactional(readOnly = true)
    public OrdineMissioneTaxi getTaxi(Long idMissione, Boolean valorizzaDatiCollegati) throws ComponentException {
        OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, idMissione);

        if (ordineMissione != null) {
            OrdineMissioneTaxi ordineMissioneTaxi = ordineMissioneTaxiRepository.getTaxi(ordineMissione);

            if (valorizzaDatiCollegati && ordineMissioneTaxi != null) {
                List<SpostamentiTaxi> list = spostamentiTaxiRepository.getSpostamenti(ordineMissioneTaxi);
                ordineMissioneTaxi.setOrdineMissione(ordineMissione);
                ordineMissioneTaxi.setListSpostamenti(list);
            }
            return ordineMissioneTaxi;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneTaxi createTaxi(OrdineMissioneTaxi ordineMissioneTaxi) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        ordineMissioneTaxi.setUser(securityService.getCurrentUserLogin());
        OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, ordineMissioneTaxi.getOrdineMissione().getId());
        if (ordineMissione != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissione);
        }
        ordineMissioneTaxi.setOrdineMissione(ordineMissione);
        ordineMissioneTaxi.setStato(Costanti.STATO_INSERITO);
        ordineMissioneTaxi.setStatoFlusso(Costanti.STATO_INSERITO);
        ordineMissioneTaxi.setToBeCreated();

        validaCRUD(ordineMissioneTaxi);
        ordineMissioneTaxi = (OrdineMissioneTaxi) crudServiceBean.creaConBulk(ordineMissioneTaxi);
        log.debug("Created Information for OrdineMissioneTaxi: {}", ordineMissioneTaxi);
        return ordineMissioneTaxi;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneTaxi updateTaxi(OrdineMissioneTaxi ordineMissioneTaxi) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {

        OrdineMissioneTaxi ordineMissioneTaxiDB = (OrdineMissioneTaxi) crudServiceBean.findById(OrdineMissioneTaxi.class, ordineMissioneTaxi.getId());

        if (ordineMissioneTaxiDB == null)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Taxi Ordine di Missione da aggiornare inesistente.");

        if (ordineMissioneTaxiDB.getOrdineMissione() != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissioneTaxiDB.getOrdineMissione());
        }

        ordineMissioneTaxiDB.setMancanzaAssMezzi(ordineMissioneTaxi.getMancanzaAssMezzi());
        ordineMissioneTaxiDB.setMancanzaMezzi(ordineMissioneTaxi.getMancanzaMezzi());
        ordineMissioneTaxiDB.setMotiviHandicap(ordineMissioneTaxi.getMotiviHandicap());
        ordineMissioneTaxiDB.setTrasportoMateriali(ordineMissioneTaxi.getTrasportoMateriali());
        ordineMissioneTaxiDB.setUtilizzoAltriMotivi(ordineMissioneTaxi.getUtilizzoAltriMotivi());

        ordineMissioneTaxiDB.setToBeUpdated();

        validaCRUD(ordineMissioneTaxiDB);
        ordineMissioneTaxiDB = (OrdineMissioneTaxi) crudServiceBean.modificaConBulk(ordineMissioneTaxiDB);

        log.debug("Updated Information for Taxi Ordine di Missione: {}", ordineMissioneTaxiDB);
        return ordineMissioneTaxi;
    }



    private void validaCRUD(OrdineMissioneTaxi ordineMissioneTaxi) {

        if (Utility.nvl(ordineMissioneTaxi.getMancanzaAssMezzi(), "N").equals("N") &&
                Utility.nvl(ordineMissioneTaxi.getMancanzaMezzi(), "N").equals("N") &&
                Utility.nvl(ordineMissioneTaxi.getTrasportoMateriali(), "N").equals("N") &&
                Utility.nvl(ordineMissioneTaxi.getMotiviHandicap(), "N").equals("N") &&
                Utility.nvl(ordineMissioneTaxi.getUtilizzoAltriMotivi(), "N").equals("N")) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Indicare almeno un motivo per la richiesta di utilizzo del taxi.");
        }
    }


    @Transactional(readOnly = true)
    public CMISFileAttachment uploadAllegato(Long idTaxi,
                                             InputStream inputStream, String name, MimeTypes mimeTypes) throws ComponentException {
        OrdineMissioneTaxi ordineMissioneTaxi = (OrdineMissioneTaxi) crudServiceBean.findById(
                OrdineMissioneTaxi.class, idTaxi);
        OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class,
                ordineMissioneTaxi.getOrdineMissione().getId());
        if (ordineMissione != null) {
            return cmisOrdineMissioneService.uploadAttachmentTaxi(ordineMissione, idTaxi,
                    inputStream, name, mimeTypes);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<CMISFileAttachment> getAttachments(Long idTaxi)
            throws ComponentException {
        if (idTaxi != null) {
            OrdineMissioneTaxi ordineMissioneTaxi = (OrdineMissioneTaxi) crudServiceBean.findById(
                    OrdineMissioneTaxi.class, idTaxi);
            OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class,
                    ordineMissioneTaxi.getOrdineMissione().getId());
            List<CMISFileAttachment> lista = cmisOrdineMissioneService.getAttachmentsTaxi(ordineMissione,idTaxi);
            return lista;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteTaxi(OrdineMissione ordineMissione) {
        OrdineMissioneTaxi ordineMissioneTaxi = ordineMissioneTaxiRepository.getTaxi(ordineMissione);

        if (ordineMissioneTaxi != null) {
            cancellaOrdineMissioneTaxi(ordineMissioneTaxi);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteTaxi(Long idTaxiOrdineMissione) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        OrdineMissioneTaxi ordineMissioneTaxi = (OrdineMissioneTaxi) crudServiceBean.findById(
                OrdineMissioneTaxi.class, idTaxiOrdineMissione);
        if (ordineMissioneTaxi != null) {
            String nodeRef = cmisOrdineMissioneService.getNodeRefOrdineMissioneTaxi(ordineMissioneTaxi, false);
            if (nodeRef != null) {
                missioniCMISService.deleteNode(nodeRef);
            }
            cancellaOrdineMissioneTaxi(ordineMissioneTaxi);
        }
    }


    private void cancellaSpostamenti(
            OrdineMissioneTaxi ordineMissioneTaxi)
            throws ComponentException {
        List<SpostamentiTaxi> listaSpostamenti = spostamentiTaxiRepository.getSpostamenti(ordineMissioneTaxi);
        if (listaSpostamenti != null && !listaSpostamenti.isEmpty()) {
            for (Iterator<SpostamentiTaxi> iterator = listaSpostamenti.iterator(); iterator.hasNext(); ) {
                SpostamentiTaxi spostamento = iterator.next();
                cancellaSpostamento(spostamento);
            }
        }
    }

    private void cancellaOrdineMissioneTaxi(OrdineMissioneTaxi ordineMissioneTaxi)
            throws ComponentException {
        cancellaSpostamenti(ordineMissioneTaxi);
        if (ordineMissioneTaxi.isStatoNonInviatoAlFlusso()) {
            ordineMissioneTaxi.setToBeUpdated();
            ordineMissioneTaxi.setStato(Costanti.STATO_ANNULLATO);
            crudServiceBean.modificaConBulk(ordineMissioneTaxi);
            OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, ordineMissioneTaxi.getOrdineMissione().getId());

            List<StorageObject> listaAllegati = cmisOrdineMissioneService.getAttachmentsTaxi(ordineMissione);
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
                    "Non è possibile cancellare il taxi");
        }
    }



    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiTaxi createSpostamentoTaxi(SpostamentiTaxi spostamentoTaxi) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        spostamentoTaxi.setUser(securityService.getCurrentUserLogin());
        spostamentoTaxi.setStato(Costanti.STATO_INSERITO);
        OrdineMissioneTaxi ordineMissioneTaxi = (OrdineMissioneTaxi) crudServiceBean.findById(OrdineMissioneTaxi.class, spostamentoTaxi.getTaxi().getId());
        if (ordineMissioneTaxi != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissioneTaxi.getOrdineMissione());
        }
        if (spostamentoTaxi.getPercorsoDa() == null || spostamentoTaxi.getPercorsoA() == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Il punto di partenza e il punto di destinazione degli Spostamenti Taxi devono essere inseriti");
        }

        spostamentoTaxi.setTaxi(ordineMissioneTaxi);
        Long maxRiga = spostamentiTaxiRepository.getMaxRigaSpostamenti(ordineMissioneTaxi);
        if (maxRiga == null) {
            maxRiga = Long.valueOf(0);
        }
        maxRiga = maxRiga + 1;
        spostamentoTaxi.setRiga(maxRiga);
        spostamentoTaxi.setToBeCreated();
        validaCRUD(spostamentoTaxi);
        spostamentoTaxi = (SpostamentiTaxi) crudServiceBean.creaConBulk(spostamentoTaxi);
        log.debug("Created Information for OrdineMissioneTaxi: {}", ordineMissioneTaxi);
        return spostamentoTaxi;
    }


    private void validaCRUD(SpostamentiTaxi spostamentiTaxi) {
        if (StringUtils.isEmpty(spostamentiTaxi.getPercorsoDa()) ||
                StringUtils.isEmpty(spostamentiTaxi.getPercorsoA())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati degli spostamenti incompleti.");
        }
    }
    

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSpostamenti(Long idSpostamenti) throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        SpostamentiTaxi SpostamentiTaxi = (SpostamentiTaxi) crudServiceBean.findById(SpostamentiTaxi.class, idSpostamenti);

        if (SpostamentiTaxi != null) {
            cancellaSpostamento(SpostamentiTaxi);
        }
    }


    private void cancellaSpostamento(SpostamentiTaxi spostamentiTaxi) throws ComponentException {
        spostamentiTaxi.setToBeUpdated();
        spostamentiTaxi.setStato(Costanti.STATO_ANNULLATO);
        crudServiceBean.modificaConBulk(spostamentiTaxi);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiTaxi updateSpostamenti(SpostamentiTaxi spostamentoTaxi)
            throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {

        SpostamentiTaxi spostamentiTaxiDB = (SpostamentiTaxi) crudServiceBean.findById(SpostamentiTaxi.class, spostamentoTaxi.getId());
        if (spostamentiTaxiDB == null)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Spostamenti Taxi Ordine di Missione da aggiornare inesistente.");

        if (spostamentiTaxiDB.getTaxi().getOrdineMissione() != null) {
            ordineMissioneService.controlloOperazioniCRUDDaGui(spostamentiTaxiDB.getTaxi().getOrdineMissione());
        }

        if (spostamentoTaxi.getPercorsoDa() == null || spostamentoTaxi.getPercorsoA() == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Il punto di partenza e il punto di destinazione degli Spostamenti Taxi devono essere inseriti");
        }

        spostamentiTaxiDB.setPercorsoDa(spostamentoTaxi.getPercorsoDa());
        spostamentiTaxiDB.setPercorsoA(spostamentoTaxi.getPercorsoA());
        spostamentiTaxiDB.setToBeUpdated();
        spostamentiTaxiDB = (SpostamentiTaxi) crudServiceBean.modificaConBulk(spostamentiTaxiDB);

        log.debug("Updated Information for Spostamenti: {}", spostamentiTaxiDB);
        return spostamentoTaxi;
    }


    
    @Transactional(readOnly = true)
    public List<SpostamentiTaxi> getSpostamentiTaxi(Long idTaxiOrdineMissione) throws ComponentException {
        OrdineMissioneTaxi TaxiOrdineMissione = (OrdineMissioneTaxi) crudServiceBean.findById(OrdineMissioneTaxi.class, idTaxiOrdineMissione);

        if (TaxiOrdineMissione != null) {
            return spostamentiTaxiRepository.getSpostamenti(TaxiOrdineMissione);
        }
        return null;
    }


    @Transactional(readOnly = true)
    public Map<String, byte[]> printOrdineMissioneTaxi(Long idMissione) throws AwesomeException, ComponentException {
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
            if (ordineMissioneTaxi.isRichiestaTaxiInserito()) {
                cmisOrdineMissioneService.salvaStampaTaxiSuCMIS(username, printOrdineMissione, ordineMissioneTaxi);
            }
            map.put(fileName, printOrdineMissione);
        }
        return map;
    }

    private byte[] printTaxi(String username,
                                    OrdineMissioneTaxi ordineMissioneTaxi)
            throws ComponentException {
        return printOrdineMissioneTaxiService.printOrdineMissioneTaxi(ordineMissioneTaxi, username);
    }

}
