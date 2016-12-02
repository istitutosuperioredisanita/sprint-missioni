package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneAspect;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.CmisPath;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiAutoPropria;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.OrdineMissioneAutoPropriaRepository;
import it.cnr.si.missioni.repository.SpostamentiAutoPropriaRepository;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service class for managing users.
 */
@Service
public class OrdineMissioneAutoPropriaService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneAutoPropriaService.class);

    @Inject
    private OrdineMissioneAutoPropriaRepository ordineMissioneAutoPropriaRepository;

    @Inject
    private SpostamentiAutoPropriaRepository spostamentiAutoPropriaRepository;

    @Autowired
    private PrintOrdineMissioneAutoPropriaService printOrdineMissioneAutoPropriaService;

    @Autowired
    private OrdineMissioneService ordineMissioneService;

    
    @Autowired
    private MissioniCMISService missioniCMISService;

    @Autowired
    private CMISOrdineMissioneService cmisOrdineMissioneService;

	@Inject
	private CRUDComponentSession crudServiceBean;


    @Transactional(readOnly = true)
    public OrdineMissioneAutoPropria getAutoPropria(Principal principal, Long idMissione) throws ComponentException {
    	return getAutoPropria(principal, idMissione, false);
    }

    @Transactional(readOnly = true)
    public OrdineMissioneAutoPropria getAutoPropria(Principal principal, Long idMissione, Boolean valorizzaDatiCollegati) throws ComponentException {
		OrdineMissione	ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, idMissione);
		
		if (ordineMissione != null){
			OrdineMissioneAutoPropria ordineMissioneAutoPropria = ordineMissioneAutoPropriaRepository.getAutoPropria(ordineMissione);

			if (valorizzaDatiCollegati && ordineMissioneAutoPropria != null){
				List<SpostamentiAutoPropria> list = spostamentiAutoPropriaRepository.getSpostamenti(ordineMissioneAutoPropria);
				ordineMissioneAutoPropria.setOrdineMissione(ordineMissione);
				ordineMissioneAutoPropria.setListSpostamenti(list);
			}
			return ordineMissioneAutoPropria;
		}
		return null;
    }

    @Transactional(readOnly = true)
    public List<SpostamentiAutoPropria> getSpostamentiAutoPropria(Principal principal, Long idAutoPropriaOrdineMissione) throws ComponentException {
		OrdineMissioneAutoPropria autoPropriaOrdineMissione = (OrdineMissioneAutoPropria)crudServiceBean.findById(principal, OrdineMissioneAutoPropria.class, idAutoPropriaOrdineMissione);
		
		if (autoPropriaOrdineMissione != null){
			List<SpostamentiAutoPropria> lista = spostamentiAutoPropriaRepository.getSpostamenti(autoPropriaOrdineMissione);
			return lista;
		}
		return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAutoPropria createAutoPropria(Principal principal, OrdineMissioneAutoPropria ordineMissioneAutoPropria)  throws AwesomeException, 
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {
    	ordineMissioneAutoPropria.setUid(principal.getName());
    	ordineMissioneAutoPropria.setUser(principal.getName());
    	OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, ordineMissioneAutoPropria.getOrdineMissione().getId());
    	if (ordineMissione != null){
    		ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissione);
    	}
    	ordineMissioneAutoPropria.setOrdineMissione(ordineMissione);
    	ordineMissioneAutoPropria.setStato(Costanti.STATO_INSERITO);
    	ordineMissioneAutoPropria.setToBeCreated();
		validaCRUD(principal, ordineMissioneAutoPropria);
		ordineMissioneAutoPropria = (OrdineMissioneAutoPropria)crudServiceBean.creaConBulk(principal, ordineMissioneAutoPropria);
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Created Information for OrdineMissioneAutoPropria: {}", ordineMissioneAutoPropria);
    	return ordineMissioneAutoPropria;
    }

    private void validaCRUD(Principal principal, OrdineMissioneAutoPropria ordineMissioneAutoPropria) {
    	if (StringUtils.isEmpty(ordineMissioneAutoPropria.getCartaCircolazione()) ||
    			StringUtils.isEmpty(ordineMissioneAutoPropria.getTarga())  ||
    			StringUtils.isEmpty(ordineMissioneAutoPropria.getPolizzaAssicurativa())  ||
    			StringUtils.isEmpty(ordineMissioneAutoPropria.getMarca())  ||
    			StringUtils.isEmpty(ordineMissioneAutoPropria.getModello()) ){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Dati dell'auto propria non esistenti o incompleti.");
    	}
    	if (StringUtils.isEmpty(ordineMissioneAutoPropria.getDataRilascioPatente()) ||
    			StringUtils.isEmpty(ordineMissioneAutoPropria.getDataScadenzaPatente())  ||
    			StringUtils.isEmpty(ordineMissioneAutoPropria.getEntePatente())  ||
    			StringUtils.isEmpty(ordineMissioneAutoPropria.getNumeroPatente())){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Dati della patente non esistenti o incompleti.");
    	}
	}

    private void validaCRUD(Principal principal, SpostamentiAutoPropria spostamentiAutoPropria) {
    	if (StringUtils.isEmpty(spostamentiAutoPropria.getPercorsoDa()) ||
    			StringUtils.isEmpty(spostamentiAutoPropria.getPercorsoA()) ){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Dati degli spostamenti incompleti.");
    	}
	}

	@Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiAutoPropria createSpostamentoAutoPropria(Principal principal, SpostamentiAutoPropria spostamentoAutoPropria)  throws AwesomeException, 
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {
    	spostamentoAutoPropria.setUid(principal.getName());
    	spostamentoAutoPropria.setUser(principal.getName());
    	spostamentoAutoPropria.setStato(Costanti.STATO_INSERITO);
    	OrdineMissioneAutoPropria ordineMissioneAutoPropria = (OrdineMissioneAutoPropria)crudServiceBean.findById(principal, OrdineMissioneAutoPropria.class, spostamentoAutoPropria.getOrdineMissioneAutoPropria().getId());
    	if (ordineMissioneAutoPropria != null){
    		ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissioneAutoPropria.getOrdineMissione());
    	}

    	spostamentoAutoPropria.setOrdineMissioneAutoPropria(ordineMissioneAutoPropria);
    	Long maxRiga = spostamentiAutoPropriaRepository.getMaxRigaSpostamenti(ordineMissioneAutoPropria);
    	if (maxRiga == null ){
    		maxRiga = new Long(0);
    	}
    	maxRiga = maxRiga + 1;
    	spostamentoAutoPropria.setRiga(maxRiga);
    	spostamentoAutoPropria.setToBeCreated();
		validaCRUD(principal, spostamentoAutoPropria);
    	spostamentoAutoPropria = (SpostamentiAutoPropria)crudServiceBean.creaConBulk(principal, spostamentoAutoPropria);
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Created Information for OrdineMissioneAutoPropria: {}", ordineMissioneAutoPropria);
    	return spostamentoAutoPropria;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissioneAutoPropria updateAutoPropria(Principal principal, OrdineMissioneAutoPropria ordineMissioneAutoPropria)  throws AwesomeException, 
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {

    	OrdineMissioneAutoPropria ordineMissioneAutoPropriaDB = (OrdineMissioneAutoPropria)crudServiceBean.findById(principal, OrdineMissioneAutoPropria.class, ordineMissioneAutoPropria.getId());

		if (ordineMissioneAutoPropriaDB==null)
			throw new AwesomeException(CodiciErrore.ERRGEN, "Auto Propria Ordine di Missione da aggiornare inesistente.");
		
    	if (ordineMissioneAutoPropriaDB.getOrdineMissione() != null){
    		ordineMissioneService.controlloOperazioniCRUDDaGui(ordineMissioneAutoPropriaDB.getOrdineMissione());
    	}
		ordineMissioneAutoPropriaDB.setTarga(ordineMissioneAutoPropria.getTarga());
		ordineMissioneAutoPropriaDB.setMarca(ordineMissioneAutoPropria.getMarca());
		ordineMissioneAutoPropriaDB.setModello(ordineMissioneAutoPropria.getModello());
		ordineMissioneAutoPropriaDB.setCartaCircolazione(ordineMissioneAutoPropria.getCartaCircolazione());
		ordineMissioneAutoPropriaDB.setEntePatente(ordineMissioneAutoPropria.getEntePatente());
		
		ordineMissioneAutoPropriaDB.setToBeUpdated();


		validaCRUD(principal, ordineMissioneAutoPropriaDB);
		ordineMissioneAutoPropriaDB = (OrdineMissioneAutoPropria)crudServiceBean.modificaConBulk(principal, ordineMissioneAutoPropriaDB);
    	
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Updated Information for Auto Propria Ordine di Missione: {}", ordineMissioneAutoPropriaDB);
    	return ordineMissioneAutoPropria;
    }

    @Transactional(propagation = Propagation.REQUIRED)
	public void deleteAutoPropria(Principal principal, Long idAutoPropriaOrdineMissione) throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
    	OrdineMissioneAutoPropria ordineMissioneAutoPropria = (OrdineMissioneAutoPropria)crudServiceBean.findById(principal, OrdineMissioneAutoPropria.class, idAutoPropriaOrdineMissione);

		if (ordineMissioneAutoPropria != null){
			Document documentoAutoPropria = null;
			documentoAutoPropria = creaDocumentoRichiestaAutoPropria(principal.getName(), ordineMissioneAutoPropria);
			if (documentoAutoPropria != null){
				missioniCMISService.deleteNode(documentoAutoPropria);
			}
			cancellaOrdineMissioneAutoPropria(principal, ordineMissioneAutoPropria);
		}
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public void deleteAutoPropria(Principal principal, OrdineMissione ordineMissione) throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
    	OrdineMissioneAutoPropria ordineMissioneAutoPropria = ordineMissioneAutoPropriaRepository.getAutoPropria(ordineMissione);

		if (ordineMissioneAutoPropria != null){
			cancellaOrdineMissioneAutoPropria(principal, ordineMissioneAutoPropria);
		}
	}

    @Transactional(propagation = Propagation.REQUIRED)
	private void cancellaOrdineMissioneAutoPropria(Principal principal,
			OrdineMissioneAutoPropria ordineMissioneAutoPropria)
			throws ComponentException {
		cancellaSpostamenti(principal, ordineMissioneAutoPropria);
		cancellaDatiAutoPropriaOrdineMissione(principal, ordineMissioneAutoPropria);
	}

    @Transactional(propagation = Propagation.REQUIRED)
	private void cancellaDatiAutoPropriaOrdineMissione(Principal principal,
			OrdineMissioneAutoPropria ordineMissioneAutoPropria)
			throws ComponentException {
		ordineMissioneAutoPropria.setToBeUpdated();
		ordineMissioneAutoPropria.setStato(Costanti.STATO_ANNULLATO);
		crudServiceBean.modificaConBulk(principal, ordineMissioneAutoPropria);
	}

    @Transactional(propagation = Propagation.REQUIRED)
	private void cancellaSpostamenti(Principal principal,
			OrdineMissioneAutoPropria ordineMissioneAutoPropria)
			throws ComponentException {
		List<SpostamentiAutoPropria> listaSpostamenti = spostamentiAutoPropriaRepository.getSpostamenti(ordineMissioneAutoPropria);
		if (listaSpostamenti != null && !listaSpostamenti.isEmpty()){
			for (Iterator<SpostamentiAutoPropria> iterator = listaSpostamenti.iterator(); iterator.hasNext();){
				SpostamentiAutoPropria spostamento = iterator.next();
				cancellaSpostamento(principal, spostamento);
		    }
		}
	}
	
    @Transactional(propagation = Propagation.REQUIRED)
	public void deleteSpostamenti(Principal principal, Long idSpostamenti) throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
    	SpostamentiAutoPropria spostamentiAutoPropria = (SpostamentiAutoPropria)crudServiceBean.findById(principal, SpostamentiAutoPropria.class, idSpostamenti);

		//effettuo controlli di validazione operazione CRUD
		if (spostamentiAutoPropria != null){
			cancellaSpostamento(principal, spostamentiAutoPropria);
		}
	}

    @Transactional(propagation = Propagation.REQUIRED)
	private void cancellaSpostamento(Principal principal, SpostamentiAutoPropria spostamentiAutoPropria) throws ComponentException {
		spostamentiAutoPropria.setToBeUpdated();
		spostamentiAutoPropria.setStato(Costanti.STATO_ANNULLATO);
		crudServiceBean.modificaConBulk(principal, spostamentiAutoPropria);
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public SpostamentiAutoPropria updateSpostamenti(Principal principal, SpostamentiAutoPropria spostamentiAutoPropria)  throws AwesomeException, 
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {

    	SpostamentiAutoPropria spostamentiAutoPropriaDB = (SpostamentiAutoPropria)crudServiceBean.findById(principal, SpostamentiAutoPropria.class, spostamentiAutoPropria.getId());

		if (spostamentiAutoPropriaDB==null)
			throw new AwesomeException(CodiciErrore.ERRGEN, "Spostamenti Auto Propria Ordine di Missione da aggiornare inesistente.");
		
    	if (spostamentiAutoPropriaDB.getOrdineMissioneAutoPropria().getOrdineMissione() != null){
    		ordineMissioneService.controlloOperazioniCRUDDaGui(spostamentiAutoPropriaDB.getOrdineMissioneAutoPropria().getOrdineMissione());
    	}
		spostamentiAutoPropriaDB.setPercorsoDa(spostamentiAutoPropria.getPercorsoDa());
		spostamentiAutoPropriaDB.setPercorsoA(spostamentiAutoPropria.getPercorsoA());
		
		spostamentiAutoPropriaDB.setToBeUpdated();


		spostamentiAutoPropriaDB = (SpostamentiAutoPropria)crudServiceBean.modificaConBulk(principal, spostamentiAutoPropriaDB);
    	
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Updated Information for Spostamenti: {}", spostamentiAutoPropriaDB);
    	return spostamentiAutoPropria;
    }

    @Transactional(readOnly = true)
   	public Map<String, byte[]>  printOrdineMissioneAutoPropria(Authentication auth, Long idMissione) throws AwesomeException, ComponentException {
    	String username = SecurityUtils.getCurrentUserLogin();
    	Principal principal = (Principal)auth;
    	OrdineMissioneAutoPropria ordineMissioneAutoPropria = getAutoPropria(principal, idMissione, true);
		Map<String, byte[]> map = new HashMap<String, byte[]>();
    	byte[] printOrdineMissione = null;
    	String fileName = null;
    	if (!ordineMissioneAutoPropria.getOrdineMissione().isStatoNonInviatoAlFlusso()){
    		ContentStream content = null;
			try {
				content = cmisOrdineMissioneService.getContentStreamOrdineMissioneAutoPropria(ordineMissioneAutoPropria);
			} catch (Exception e1) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero del contenuto del file Auto Propria sul documentale (" + Utility.getMessageException(e1) + ")");
			}
    		if (content != null){
        		fileName = content.getFileName();
        		InputStream is = null;
    			try {
    				is = content.getStream();
    			} catch (Exception e) {
    				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero dello stream del file Auto Propria sul documentale (" + Utility.getMessageException(e) + ")");
    			}
        		if (is != null){
            		try {
    					printOrdineMissione = IOUtils.toByteArray(is);
    				} catch (IOException e) {
    					throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella conversione dello stream in byte del file Auto Propria (" + Utility.getMessageException(e) + ")");
    				}
        		}
    		} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero del contenuto del file Auto Propria sul documentale");
    		}
    		map.put(fileName, printOrdineMissione);
    	} else {
    		fileName = "OrdineMissioneAutoPropria"+idMissione+".pdf";
    		printOrdineMissione = printAutoPropria(username, ordineMissioneAutoPropria);
    		if (ordineMissioneAutoPropria.isRichiestaAutoPropriaInserita()){
    			salvaStampaAutoPropriaSuCMIS(username, printOrdineMissione, ordineMissioneAutoPropria);
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
    
	public Document creaDocumentoRichiestaAutoPropria(String username,
			OrdineMissioneAutoPropria ordineMissioneAutoPropria)
			throws ComponentException {
		byte[] printOrdineMissione = printAutoPropria(username, ordineMissioneAutoPropria);
		return salvaStampaAutoPropriaSuCMIS(username, printOrdineMissione, ordineMissioneAutoPropria);
	}
    
    @Transactional(readOnly = true)
    private Document salvaStampaAutoPropriaSuCMIS(String currentLogin, byte[] stampa,
			OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException {
		InputStream streamStampa = new ByteArrayInputStream(stampa);
		CmisPath cmisPath = cmisOrdineMissioneService.createFolderOrdineMissione(ordineMissioneAutoPropria.getOrdineMissione());
		Map<String, Object> metadataProperties = cmisOrdineMissioneService.createMetadataForFileOrdineMissioneAutoPropria(currentLogin, ordineMissioneAutoPropria);
		try{
			Document node = missioniCMISService.restoreSimpleDocument(
					metadataProperties,
					streamStampa,
					MimeTypes.PDF.mimetype(),
					ordineMissioneAutoPropria.getFileName(), 
					cmisPath);
			missioniCMISService.addAspect(node, CMISOrdineMissioneAspect.ORDINE_MISSIONE_ATTACHMENT_USO_AUTO_PROPRIA.value());
			missioniCMISService.makeVersionable(node);
			return node;
		} catch (Exception e) {
			if (e.getCause() instanceof CmisConstraintException)
				throw new ComponentException("CMIS - File ["+ordineMissioneAutoPropria.getFileName()+"] già presente o non completo di tutte le proprietà obbligatorie. Inserimento non possibile!");
			throw new ComponentException("CMIS - Errore nella registrazione del file XML sul Documentale (" + Utility.getMessageException(e) + ")");
		}
	}
}
