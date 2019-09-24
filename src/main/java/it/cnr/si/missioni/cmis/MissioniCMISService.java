package it.cnr.si.missioni.cmis;

import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.cmis.acl.Permission;
import it.cnr.si.missioni.service.ProxyService;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.ResultProxy;
import it.cnr.si.missioni.util.proxy.json.object.DatiGruppoSAC;
import it.cnr.si.missioni.util.proxy.json.object.GruppoSAC;
import it.cnr.si.spring.storage.StorageException;
import it.cnr.si.spring.storage.StorageException.Type;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.StorageService;
import it.cnr.si.spring.storage.StoreService;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
@Component
public class MissioniCMISService extends StoreService {
	private transient static final Log logger = LogFactory.getLog(MissioniCMISService.class);

	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private ProxyService proxyService;
	
    @Value("${spring.proxy.FLOWS.urlOrdineMissione}")
    private String urlFlowOrdineMissione;
	
    @Value("${spring.proxy.FLOWS.urlAnnullamentoOrdineMissione}")
    private String urlFlowAnnullamentoOrdineMissione;
	
    @Value("${spring.proxy.FLOWS.urlRimborsoMissione}")
    private String urlFlowRimborsoMissione;
	
    @Value("${spring.proxy.STORAGE.urlForPerson}")
    private String urlForPerson;
	
	public static final String ASPECT_TITLED = "P:cm:titled";
	public static final String PROPERTY_LAST_MODIFICATION_DATE = "cmis:lastModificationDate";
	public static final String ASPECT_FLUSSO = "P:wfcnr:parametriFlusso";
	public static final String ASPECT_FLUSSO_MISSIONI = "P:cnrmissioni:parametriFlussoMissioni";
	public static final String PROPERTY_TITLE = "cm:title"; 
	public static final String PROPERTY_DESCRIPTION = "cm:description"; 
	public static final String PROPERTY_AUTHOR = "cm:author";
	public static final String ASPECT_AUTHOR = "P:"+PROPERTY_AUTHOR;
	public static final String ALFCMIS_NODEREF = "alfcmis:nodeRef";
	public static final String PROPERTY_AUTOVERSION = "cm:autoVersion"; 
	public static final String PROPERTY_AUTOVERSION_ON_UPDATE = "cm:autoVersionOnUpdateProps";
	public static final String PROPERTY_FOLDER = "cmis:folder";
	public static final String PROPERTY_NAME = "cmis:name";
	
    private String url;

    private String username;

    private String password;

    private int maxItemsPerPage;

    private String alfresco;
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }

    public void setMaxItemsPerPage(int maxItemsPerPage) {
        this.maxItemsPerPage = maxItemsPerPage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlfresco() {
        return alfresco;
    }

    public void setAlfresco(String alfresco) {
        this.alfresco = alfresco;
    }
    
	public String sanitizeFilename(String name) {
		name = name.trim();
		Pattern pattern = Pattern.compile(Costanti.STRING_FOR_SANITIZE_FILE_NAME);
		Matcher matcher = pattern.matcher(name);
		if(!matcher.matches()){
			return matcher.replaceAll("_"); 
		} else {
			return name;
		}		
	}
	
	public String sanitizeFolderName(String name) {
		name = name.trim();
		Pattern pattern = Pattern.compile(Costanti.STRING_FOR_SANITIZE_FILE_NAME);
		Matcher matcher = pattern.matcher(name);
		if(!matcher.matches()){
			return matcher.replaceAll("'"); 
		} else {
			return name;
		}		
	}

	public StoragePath getBasePath() {
		return (StoragePath)appContext.getBean("storagePath");
	}
	
	public String createFolderIfNotPresent(StoragePath cmisPath, Map<String, Object> metadataProperties, List<String> aspectsToAdd, String folderName) {
		metadataProperties.put(StoragePropertyNames.SECONDARY_OBJECT_TYPE_IDS.value(), aspectsToAdd);
		return createFolderIfNotPresent(cmisPath.getPath(), folderName, metadataProperties);
	}
	
	
	public InputStream recuperoStreamFileFromObjectID(String id){
		if (id != null){
			return getResource(recuperoContentFileFromObjectID(id));
		}
		return null;
	}

	public StorageObject recuperoContentFileFromObjectID(String id){
		if (id != null){
			return getStorageObjectBykey(id);
		}
		return null;
	}
	
    @Transactional(readOnly = true)
	public StorageObject restoreSimpleDocument(Map<String, Object> metadataProperties, InputStream inputStream, String contentType, String name, 
			StoragePath cmisPath, Permission... permissions){
		return restoreSimpleDocument(metadataProperties, inputStream, contentType, name, cmisPath, null, false, permissions);
	}

    @Transactional(readOnly = true)
	public StorageObject restoreSimpleDocument(Map<String, Object> metadataProperties, InputStream inputStream, String contentType, String name, 
			StoragePath path, String objectTypeName, boolean makeVersionable, Permission... permissions){
		StorageObject storage = null;
	        Optional<StorageObject> optStorageObject = Optional.ofNullable(getStorageObjectByPath(path.getPath().concat(StorageService.SUFFIX).concat(sanitizeFilename(name))));
			List<String> lista = new ArrayList<>();
			lista.add(MissioniCMISService.ASPECT_FLUSSO);
			lista.add(MissioniCMISService.ASPECT_AUTHOR);
			lista.add(MissioniCMISService.ASPECT_TITLED);
			lista.add(MissioniCMISService.ASPECT_FLUSSO_MISSIONI);
			metadataProperties.put(StoragePropertyNames.SECONDARY_OBJECT_TYPE_IDS.value(), lista);
	        if (optStorageObject.isPresent()) {
	            storage =  updateStream(optStorageObject.get().getKey(), inputStream, contentType);
				addPropertyForExistingDocument(metadataProperties, storage);
				return storage;
	        } else {
	            return storeSimpleDocument(inputStream, contentType, path.getPath(), metadataProperties);
	        }

	}

	public void addPropertyForExistingDocument(Map<String, Object> metadataProperties, StorageObject node) {
		List<String> lista = new ArrayList<>();
		lista.add(MissioniCMISService.ASPECT_FLUSSO);
		lista.add(MissioniCMISService.ASPECT_AUTHOR);
		lista.add(MissioniCMISService.ASPECT_TITLED);
		lista.add(MissioniCMISService.ASPECT_FLUSSO_MISSIONI);
		metadataProperties.put(StoragePropertyNames.SECONDARY_OBJECT_TYPE_IDS.value(), lista);
		updateProperties(metadataProperties, node);
	}



    public String recuperoNodeRefUtente(String username){
		JsonFactory jsonFactory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(jsonFactory); 
		try {
			String appUrl = "service/cnr/person/person/";
			String url = appUrl+username;
			String body = null;
			ResultProxy result = proxyService.process(HttpMethod.GET, body, Costanti.APP_STORAGE, url, null, null, false);
			String risposta = result.getBody();
			TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
			HashMap<String,Object> mapFirmatario = mapper.readValue(risposta, typeRef); 
			
			Boolean userEnabled = (Boolean)mapFirmatario.get("enabled");
			String userNodeRef = (String)mapFirmatario.get("node-uuid");
			if (userEnabled==null || !userEnabled || userNodeRef==null)
				throw new StorageException(Type.NOT_FOUND, "Errore in fase avvio flusso documentale. Utente " + username + " non registrato.");
			return "workspace://SpacesStore/"+userNodeRef;
		} catch (StorageException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Errore nel recuperoNodeRefUtente.", e);
			throw new StorageException(Type.GENERIC, "Errore in fase avvio flusso documentale. Utente " + username);
		}

    }
    
	public String startFlowOrdineMissione(MessageForFlow message) throws ComponentException{
		return startFlow(message, urlFlowOrdineMissione);
	}

	public String startFlowAnnullamentoOrdineMissione(MessageForFlow  message) throws ComponentException{
		return startFlow(message, urlFlowAnnullamentoOrdineMissione);
	}

	public String startFlowRimborsoMissione(MessageForFlow message) throws ComponentException{
		return startFlow(message, urlFlowRimborsoMissione);
	}

	private String startFlow(MessageForFlow  message, String simpleUrl) {
		JsonFactory jsonFactory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(jsonFactory); 
		try {
			String url = simpleUrl;
			ResultProxy result = proxyService.process(HttpMethod.POST, message, Costanti.APP_STORAGE, url, null, null, false);
			String risposta = result.getBody();
			logger.info("Risposta Start Flow. Content: "+message.toString() + ". Risposta: "+risposta);

			TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
			HashMap<String,Object> map = mapper.readValue(risposta, typeRef); 
			
			String text = map.get(Costanti.PROPERTY_RESULT_FLOW).toString();

			Pattern pattern = Pattern.compile(Costanti.PATTERN_RESULT_FLOW);
			Matcher matcher = pattern.matcher(text);
			if (matcher.find())
				return matcher.group(1);
			throw new StorageException(Type.GENERIC, "Flusso non avviato "+message.toString());
		} catch (StorageException e) {
			throw e;
		} catch (Exception e) {
			throw new StorageException(Type.GENERIC, "Errore in fase avvio flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
		}
	}
	
	public void restartFlow(MessageForFlow message, ResultFlows result) {
		try {
			String url = "service/api/task/"+result.getTaskId()+"/formprocessor";
			logger.info("url for Restart: "+url + " body: "+message.toString());
			proxyService.process(HttpMethod.POST, message, Costanti.APP_STORAGE, url, null, null, false);
		} catch (StorageException e) {
			throw e;
		} catch (Exception e) {
			throw new StorageException(Type.GENERIC, "Errore in fase di riproposizione del flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
		}
	}

	private void nextStepForRestartFlow(ResultFlows result) {
		try {
			String url = "service/api/workflow/task/end/"+result.getTaskId()+"/Next";
			MessageForFlowNext msg = new MessageForFlowNext();
			msg.setNext("next");
			proxyService.process(HttpMethod.POST, msg, Costanti.APP_STORAGE, url, null, null, false);
		} catch (StorageException e) {
			throw e;
		} catch (Exception e) {
			throw new StorageException(Type.GENERIC, "Errore in fase di avanzamento del flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
		}
	}

	public void abortFlow(MessageForFlow message, ResultFlows result) {

		try {
			String url = "service/api/task-instances/"+result.getTaskId();
			logger.info("url for Restart: "+url + " body: "+message.toString());
			proxyService.process(HttpMethod.POST, message, Costanti.APP_STORAGE, url, null, null, false);
			nextStepForRestartFlow(result);
		} catch (StorageException e) {
			throw e;
		} catch (Exception e) {
			throw new StorageException(Type.GENERIC, "Errore in fase di riproposizione del flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
		}
	}
	public CMISFileContent getAttachment(String nodeRef){
		CMISFileContent cmisFileContent = new CMISFileContent();
        StorageObject obj = getStorageObjectBykey(nodeRef);
        if (obj != null){
        	cmisFileContent.setStream(getResource(obj));
        	cmisFileContent.setFileName(obj.getPropertyValue(StoragePropertyNames.NAME.value()));
        	cmisFileContent.setMimeType(obj.getPropertyValue(StoragePropertyNames.CONTENT_STREAM_MIME_TYPE.value()));
        	return cmisFileContent;
        }

		return null;
	}

	public DatiGruppoSAC getDatiGruppoSAC(String uo) {

		JsonFactory jsonFactory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(jsonFactory); 
		try {
			String url = "service/api/groups/"+org.apache.commons.lang.StringUtils.rightPad(uo, 26, '0')+"/parents?";
			logger.info("url for GET Dati Gruppo SAC: "+url);
			String body = null;
			ResultProxy result = proxyService.process(HttpMethod.GET, body, Costanti.APP_STORAGE, url, null, null, false);
			String risposta = result.getBody();
			Class<GruppoSAC> grpSAC = GruppoSAC.class;
			GruppoSAC gruppoSac = mapper.readValue(risposta, grpSAC); 
			
			if (gruppoSac != null && gruppoSac.getData() != null && gruppoSac.getData().size() > 0){
				return gruppoSac.getData().get(0);
			} else {
				return null;
			}
		} catch (StorageException e) {
			throw e;
		} catch (Exception e) {
			throw new StorageException(Type.GENERIC, "Errore in fase di riproposizione del flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
		}
	}
	
	
	public Boolean deleteNode(String id) {
		return super.delete(id);
	}
	public List<StorageObject> recuperoFlusso(String idFlusso) {
		StringBuilder query = new StringBuilder("select parametriFlusso.wfcnr:statoFlusso, parametriFlusso.wfcnr:taskId, flussoMissioni.cnrmissioni:commento from cmis:document as t ");
		query.append( "inner join wfcnr:parametriFlusso as parametriFlusso on t.cmis:objectId = parametriFlusso.cmis:objectId ");
		query.append(" inner join cnrmissioni:parametriFlussoMissioni as flussoMissioni on t.cmis:objectId = flussoMissioni.cmis:objectId ");
		query.append(" where parametriFlusso.wfcnr:wfInstanceId = '").append(idFlusso).append("'");

		return super.search(query.toString());
	}
	protected List<StorageObject> recuperoDocumento(StorageObject fo, String tipoDocumento) {
		return Optional.ofNullable(fo) 
			.map(folder -> super.getChildren(folder.getKey()))
			.map(storageObjects -> {
                List<StorageObject> list = new ArrayList<StorageObject>();
                storageObjects.forEach(so ->
                        list.add(so));
                return list;
            })
			.map(lista -> lista.stream()
			.filter(stor -> {
				Boolean str = ((ArrayList<String>)stor.getPropertyValue(StoragePropertyNames.SECONDARY_OBJECT_TYPE_IDS.value())).contains(tipoDocumento) && 
						!((ArrayList<String>)stor.getPropertyValue(StoragePropertyNames.SECONDARY_OBJECT_TYPE_IDS.value())).contains(CMISMissioniAspect.FILE_ELIMINATO.value());
				return str;
			}).collect(Collectors.toList())).orElse(new ArrayList<StorageObject>());
	}
	
	public List<StorageObject> getChildren(StorageObject fo) {
		return super.getChildren(fo.getKey());
	}
	
	public void eliminaFilePresenteNelFlusso(Principal principal, String idNodo, StorageObject storageFolderRimborso) {
		List<StorageObject> listaStorageObject = super.getChildren(storageFolderRimborso.getKey(), -1);
		StorageObject node = (StorageObject)getStorageObjectByPath(idNodo);
		String oldNomeFile = node.getPropertyValue(StoragePropertyNames.NAME.value());
		Boolean nameAlreadyExists = true;
		
		String nomeFileEliminato = oldNomeFile;
	    while( nameAlreadyExists ) {
	    	nameAlreadyExists = false;
			nomeFileEliminato = sanitizeFilename(nomeFileEliminato+".eliminato");
			for (StorageObject so : listaStorageObject){
				String path = so.getPath();
				String newPath = path.substring(0, path.length() - oldNomeFile.length());
				try {
					getStorageObjectByPath(newPath+nomeFileEliminato);
					nameAlreadyExists = true;
					break;
				} catch (StorageException e){
				}
			}
	    }
		String nomeFile = nomeFileEliminato;
		
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(StoragePropertyNames.NAME.value(), nomeFile);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, nomeFile);
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, nomeFile);
		metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, principal.getName());
		updateProperties(metadataProperties, node);
		addAspect(node, CMISMissioniAspect.FILE_ELIMINATO.value());
	}
	
}
