package it.cnr.si.missioni.cmis;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.config.AlfrescoConfiguration;
import it.cnr.si.missioni.cmis.acl.ACLType;
import it.cnr.si.missioni.cmis.acl.Permission;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.DatiGruppoSAC;
import it.cnr.si.missioni.util.proxy.json.object.GruppoSAC;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.bindings.impl.CmisBindingsHelper;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
@Component
public class MissioniCMISService {
	private transient static final Log logger = LogFactory.getLog(MissioniCMISService.class);

	@Autowired
	private ApplicationContext appContext;
	
	public static final String ASPECT_TITLED = "P:cm:titled";
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
	public static final String PATH_SERVICE_PERMISSIONS = "service/cnr/nodes/permissions/";

	@Value("${cmis.alfresco}")
	private String baseURL;

    @Autowired
    private Session session;

    @Autowired
    private BindingSession bindingSession;

    @Autowired
    private AlfrescoConfiguration alfrescoConfiguration;

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public void makeVersionable(CmisObject node){
		addAutoVersion((Document) node, false);
	}
	
	public String sanitizeFilename(String name) {
		name = name.trim();
		Pattern pattern = Pattern.compile("([\\/:@()&\u20AC<>?\"])");
		Matcher matcher = pattern.matcher(name);
		if(!matcher.matches()){
			return matcher.replaceAll("_"); 
		} else {
			return name;
		}		
	}
	
	public String sanitizeFolderName(String name) {
		name = name.trim();
		Pattern pattern = Pattern.compile("([\\/:@()&\u20AC<>?\"])");
		Matcher matcher = pattern.matcher(name);
		if(!matcher.matches()){
			return matcher.replaceAll("'"); 
		} else {
			return name;
		}		
	}

	public String getRepositoryURL() {
		return baseURL;
	}
	
	public CmisObject getNodeByPath(CmisPath cmisPath){
		return getNodeByPath(cmisPath.getPath());
	}

	public CmisObject getNodeByPath(String path){
		return session.getObjectByPath(path);
	}
	
	public CmisObject getNodeByNodeRef(String nodeRef){
		return session.getObject(nodeRef);
	}

	public CmisObject getNodeByNodeRef(String nodeRef, UsernamePasswordCredentials usernamePasswordCredentials){
		return session.getObject(nodeRef);
	}
	
	public CmisPath createFolderIfNotPresent(CmisPath cmisPath, String folderName){
		folderName = sanitizeFolderName(folderName);
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(PropertyIds.OBJECT_TYPE_ID, PROPERTY_FOLDER);
		metadataProperties.put(PROPERTY_DESCRIPTION, folderName);
		metadataProperties.put(PropertyIds.NAME, folderName);
		metadataProperties.put(PROPERTY_TITLE, folderName);
		List<String> aspectsToAdd = new ArrayList<String>();
		aspectsToAdd.add(ASPECT_TITLED);
		cmisPath = createFolderIfNotPresent(cmisPath, metadataProperties, aspectsToAdd, folderName);
		return cmisPath;
	}
	
	public CmisPath getBasePath() {
		return (CmisPath)appContext.getBean("cmisPath");
	}
	
	public CmisPath createFolderIfNotPresent(CmisPath cmisPath, Map<String, Object> metadataProperties, List<String> aspectsToAdd, String folderName) {
		CmisObject cmisObject = getNodeByPath(cmisPath);
		try{
			metadataProperties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, aspectsToAdd);
			Folder folder = (Folder) getNodeByNodeRef(session.createFolder(metadataProperties, cmisObject).getId());
			return CmisPath.construct(folder.getPath());
		}catch(CmisContentAlreadyExistsException _ex){
				Folder folder = (Folder) getNodeByPath(cmisPath.getPath()+(cmisPath.getPath().equals("/")?"":"/")+sanitizeFilename(folderName).toLowerCase());
		        List<String> aspects = folder.getPropertyValue(PropertyIds.SECONDARY_OBJECT_TYPE_IDS);
		        aspects.addAll(aspectsToAdd);
		        metadataProperties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, aspects);
				folder.updateProperties(metadataProperties, true);
				return CmisPath.construct(folder.getPath());
		} catch (Exception e) {
			logger.error("Errore nella creazione della folder.", e);
			throw new ComponentException(e);
		}
	}
	

	public void deleteNode(CmisObject cmisObject){
		if (cmisObject.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER))
			((Folder)cmisObject).deleteTree(true, UnfileObject.DELETE, false);
		else	
			session.delete(cmisObject);
	}

	public void deleteNode(String nodeRef){
        CmisObject obj = getNodeByNodeRef(nodeRef);
        deleteNode(obj);
	}

	public InputStream getResource(CmisObject cmisObject){
		ContentStream content = getContent(cmisObject);
		if (content != null){
			return content.getStream();
		}
		return null;
	}
	
	public ContentStream getContent(CmisObject cmisObject){
		return ((Document)cmisObject).getContentStream();
	}
	
	public Document storeSimpleDocument(Map<String, Object> metadataProperties, InputStream inputStream, String contentType, String name, 
			CmisPath cmisPath, Permission... permissions){
		return storeSimpleDocument(metadataProperties, inputStream, contentType, name, cmisPath, null, false, permissions);
	}
	
	public Document storeSimpleDocument(Map<String, Object> metadataProperties, InputStream inputStream, String contentType, String name, 
				CmisPath cmisPath, String objectTypeName, boolean makeVersionable, Permission... permissions){
		CmisObject parentNode = getNodeByPath(cmisPath);
		try {
			name = sanitizeFilename(name);
			metadataProperties.put(PropertyIds.NAME, name);

			List<String> aspectsToAdd = new ArrayList<String>();
			aspectsToAdd.add(ASPECT_TITLED);
			aspectsToAdd.add(ASPECT_AUTHOR);
			aspectsToAdd.add(ASPECT_FLUSSO);
			aspectsToAdd.add(ASPECT_FLUSSO_MISSIONI);
			metadataProperties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, aspectsToAdd);

			ContentStream contentStream = new ContentStreamImpl(
					name,
					BigInteger.ZERO,
					contentType,
					inputStream);			
			Document node = (Document) getNodeByNodeRef(
					session.createDocument(metadataProperties, parentNode, contentStream, VersioningState.MAJOR).getId());
			if (permissions.length > 0 ){
				setInheritedPermission(bindingSession, node.getProperty(ALFCMIS_NODEREF).getValueAsString(), Boolean.FALSE);
				if (permissions != null && permissions.length > 0) {
					addAcl(bindingSession, node.getProperty(ALFCMIS_NODEREF).getValueAsString(), Permission.convert(permissions));
				}
			}
			if (makeVersionable)
				addAutoVersion((Document) node, false);
			return node;
		}catch (CmisBaseException e) {
			throw e;
		}catch (Exception e) {
			logger.error("Errore nel salvataggio del documento.", e);
			throw new ComponentException(e);
		}
	}
	
	
	public InputStream recuperoStreamFileFromObjectID(String id){
		if (id != null){
			try{
				return getResource(getNodeByNodeRef(id));
			}catch (CmisObjectNotFoundException _ex){
				throw new ComponentException(_ex);
			}
		}
		return null;
	}
	
	public ContentStream recuperoContentFileFromObjectID(String id){
		if (id != null){
			try{
				return getContent(getNodeByNodeRef(id));
			}catch (CmisObjectNotFoundException _ex){
				throw new ComponentException(_ex);
			}
		}
		return null;
	}
	
    @Transactional(readOnly = true)
	public Document restoreSimpleDocument(Map<String, Object> metadataProperties, InputStream inputStream, String contentType, String name, 
			CmisPath cmisPath, Permission... permissions){
		return restoreSimpleDocument(metadataProperties, inputStream, contentType, name, cmisPath, null, false, permissions);
	}

    @Transactional(readOnly = true)
	public Document restoreSimpleDocument(Map<String, Object> metadataProperties, InputStream inputStream, String contentType, String name, 
			CmisPath cmisPath, String objectTypeName, boolean makeVersionable, Permission... permissions){
		Document node = null;
		boolean existsDocument = false;
		try {
			node = (Document) getNodeByPath(cmisPath.getPath()+
											(cmisPath.getPath().equals("/")?"":"/")+
											sanitizeFilename(name).toLowerCase());
			existsDocument = true;
		} catch (CmisObjectNotFoundException e){
			return storeSimpleDocument(metadataProperties, inputStream, contentType, name, cmisPath, objectTypeName, makeVersionable, permissions);
		}
		node = updateContent(node.getObjectOfLatestVersion(false).getId(), inputStream, contentType);
		if (existsDocument){
			addPropertyForExistingDocument(metadataProperties, node);
		}
		return node;
	}

	public void addPropertyForExistingDocument(Map<String, Object> metadataProperties, Document node) {
		addAspect(node, ASPECT_FLUSSO);
		addAspect(node, ASPECT_FLUSSO_MISSIONI);
		updateProperties(metadataProperties, node);
	}

    @Transactional(readOnly = true)
	public void updateProperties(Map<String, Object> metadataProperties, CmisObject node){
		try {
			if (node.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)) {
				node = ((Document)node).getObjectOfLatestVersion(false);
				node = session.getObject(node);
				node.refresh();
			}
			node.updateProperties(metadataProperties, true);
		} catch (Exception e) {
			logger.error("Errore nell'aggiornamento delle properties.", e);
			throw new ComponentException(e);
		}
	}

	public Document updateContent(String nodeRef, InputStream inputStream, String contentType){
		Document document = (Document) getNodeByNodeRef(nodeRef);
		return updateContent(document, inputStream, contentType, document.getName());
	}

	public Document updateContent(Document document, InputStream inputStream, String contentType, String name){
		ContentStream contentStream = new ContentStreamImpl(
				name,
				BigInteger.ZERO,
				contentType,
				inputStream);
		document.setContentStream(contentStream, true, true);
		return document.getObjectOfLatestVersion(false);
	}

	public ItemIterable<CmisObject> getChildren(Folder folder){
		return folder.getChildren();
	}
	
	public void copyNode(Document source, Folder target){
		source.addToFolder(target, true);
	}
	
	public ItemIterable<QueryResult> search(StringBuilder query){
		return search(query, session.getDefaultContext());
	}

	public ItemIterable<QueryResult> search(StringBuilder query, OperationContext operationContext){
		return session.query(query.toString(), false, operationContext);
	}
	
	public List<CmisObject> searchAndFetchNode(StringBuilder query){
		List<CmisObject> results = new ArrayList<CmisObject>();
		for (QueryResult queryResult : search(query)) {
			results.add(getNodeByNodeRef((String) queryResult.getPropertyValueById(PropertyIds.OBJECT_ID)));
		}
		return results;
	}

    public void addAspect(CmisObject cmisObject, String... aspectName){
        Map<String, Object> metadataProperties = new HashMap<String, Object>();
        List<String> aspects = cmisObject.getPropertyValue(PropertyIds.SECONDARY_OBJECT_TYPE_IDS);
        aspects.addAll(Arrays.asList(aspectName));
        metadataProperties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, aspects);
        cmisObject.updateProperties(metadataProperties);
    }
    
	public void removeAspect(CmisObject cmisObject, String... aspectName){
		List<Object> aspects = cmisObject.getProperty(PropertyIds.SECONDARY_OBJECT_TYPE_IDS).getValues();
		aspects.removeAll(Arrays.asList(aspectName));
		cmisObject.updateProperties(Collections.singletonMap(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, aspects));
	}

	public boolean hasAspect(CmisObject cmisObject, String aspectName) {
		return cmisObject.getProperty(PropertyIds.SECONDARY_OBJECT_TYPE_IDS).getValues().contains(aspectName);
	}
	
	public void setInheritedPermission(CmisPath cmisPath, Boolean inheritedPermission){
		setInheritedPermission(bindingSession, getNodeByPath(cmisPath).getProperty(ALFCMIS_NODEREF).getValueAsString(), inheritedPermission);
	}
		
	private void setInheritedPermission(BindingSession cmisSession,
			String objectId, final Boolean inheritedPermission) {
		String link = baseURL
				.concat(PATH_SERVICE_PERMISSIONS)
				.concat(objectId.replace(":/", ""));
		UrlBuilder url = new UrlBuilder(link);
		Response resp = CmisBindingsHelper.getHttpInvoker(cmisSession).invokePOST(url,
				MimeTypes.JSON.mimetype(), new Output() {
					public void write(OutputStream out) throws Exception {
						JSONObject jsonObject = new JSONObject();
						JSONArray jsonArray = new JSONArray();
						jsonObject.put("permissions", jsonArray);
						jsonObject.put("isInherited", inheritedPermission);
						out.write(jsonObject.toString().getBytes());
					}
				}, cmisSession);
		int status = resp.getResponseCode();
		if (status == HttpStatus.SC_NOT_FOUND
				|| status == HttpStatus.SC_BAD_REQUEST
				|| status == HttpStatus.SC_INTERNAL_SERVER_ERROR)
			throw new CmisRuntimeException("Inherited Permission error. Exception: "
					+ resp.getErrorContent());
	}

	private void addAcl(BindingSession cmisSession, String nodeRef,
			Map<String, ACLType> permission) {
		managePermission(cmisSession, nodeRef, permission, false);
	}

	private void managePermission(BindingSession cmisSession, String objectId,
			final Map<String, ACLType> permission, final boolean remove) {
		String link = baseURL
				.concat(PATH_SERVICE_PERMISSIONS)
				.concat(objectId.replace(":/", ""));
		UrlBuilder url = new UrlBuilder(link);
		Response resp = CmisBindingsHelper.getHttpInvoker(cmisSession).invokePOST(url,
				MimeTypes.JSON.mimetype(), new Output() {
					public void write(OutputStream out) throws Exception {
						JSONObject jsonObject = new JSONObject();
						JSONArray jsonArray = new JSONArray();
						for (String authority : permission.keySet()) {
							JSONObject jsonAutority = new JSONObject();
							jsonAutority.put("authority", authority);
							jsonAutority.put("role", permission.get(authority));
							if (remove)
								jsonAutority.put("remove", remove);
							jsonArray.put(jsonAutority);
						}
						jsonObject.put("permissions", jsonArray);
						out.write(jsonObject.toString().getBytes());
					}
				}, cmisSession);
		int status = resp.getResponseCode();

		logger.info((remove ? "remove" : "add") + " permission " + permission + " on item "
				+ objectId + ", status = " + status);

		if (status == HttpStatus.SC_NOT_FOUND
				|| status == HttpStatus.SC_BAD_REQUEST
				|| status == HttpStatus.SC_INTERNAL_SERVER_ERROR)
			throw new CmisRuntimeException("Manage permission error. Exception: "
					+ resp.getErrorContent());
	}

	public void addAutoVersion(Document doc,
			final boolean autoVersionOnUpdateProps) {
		String link = baseURL.concat(
				"service/api/metadata/node/");
		link = link.concat(doc.getProperty(ALFCMIS_NODEREF).getValueAsString().replace(":/", ""));
		UrlBuilder url = new UrlBuilder(link);
		Response resp = CmisBindingsHelper.getHttpInvoker(bindingSession).invokePOST(url,
				MimeTypes.JSON.mimetype(), new Output() {
					public void write(OutputStream out) throws Exception {
						JSONObject jsonObject = new JSONObject();
						JSONObject jsonObjectProp = new JSONObject();
						jsonObjectProp.put(PROPERTY_AUTOVERSION, true);
						jsonObjectProp.put(PROPERTY_AUTOVERSION_ON_UPDATE,
								autoVersionOnUpdateProps);
						jsonObject.put("properties", jsonObjectProp);
						out.write(jsonObject.toString().getBytes());
					}
				}, bindingSession);
		int status = resp.getResponseCode();
		if (status == HttpStatus.SC_NOT_FOUND
				|| status == HttpStatus.SC_BAD_REQUEST
				|| status == HttpStatus.SC_INTERNAL_SERVER_ERROR)
			throw new CmisRuntimeException("Add Auto Version. Exception: "
					+ resp.getErrorContent());
	}

	public Response invokeGET(UrlBuilder url) {
		return CmisBindingsHelper.getHttpInvoker(bindingSession).invokeGET(url, bindingSession);		
	}
	 
	public Response invokePOST(UrlBuilder url, MimeTypes mimeType, final byte[] content) {
		if (logger.isDebugEnabled())
			logger.debug("Invoke URL:" + url);
		return CmisBindingsHelper.getHttpInvoker(bindingSession).invokePOST(url, mimeType.mimetype(),
				new Output() {
					public void write(OutputStream out) throws Exception {
            			out.write(content);
            		}
        		}, bindingSession);
	}

	public Response invokePUT(UrlBuilder url, MimeTypes mimeType, final byte[] content, Map<String, String> headers) {
		if (logger.isDebugEnabled())
			logger.debug("Invoke URL:" + url);
		return CmisBindingsHelper.getHttpInvoker(bindingSession).invokePUT(url, mimeType.mimetype(), headers, 
				new Output() {
					public void write(OutputStream out) throws Exception {
            			out.write(content);
            		}
        		}, bindingSession);
	}

    public String recuperoNodeRefUtente(String username){
		JsonFactory jsonFactory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(jsonFactory); 
		try {
			Response responseFirmatario = invokeGET(new UrlBuilder(getRepositoryURL()+"service/cnr/person/person/"+username));
			if (responseFirmatario.getResponseCode()!=200) 
				throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Richiesta Utente " + username + ". Errore "+responseFirmatario.getResponseMessage());
			TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
			HashMap<String,Object> mapFirmatario = mapper.readValue(responseFirmatario.getStream(), typeRef); 
			
			Boolean userEnabled = (Boolean)mapFirmatario.get("enabled");
			String userNodeRef = (String)mapFirmatario.get("node-uuid");
			if (userEnabled==null || !userEnabled || userNodeRef==null)
				throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Utente " + username + " non registrato.");
			return "workspace://SpacesStore/"+userNodeRef;
		} catch (CMISException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Errore nel recuperoNodeRefUtente.", e);
			throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Utente " + username + " non registrato.");
		}

    }
    
	public Response startFlowOrdineMissione(StringWriter stringWriter) throws ComponentException{
		String simpleUrl = getUrlOrdineMissione();
		return startFlow(stringWriter, simpleUrl);
	}

	public Response startFlowAnnullamentoOrdineMissione(StringWriter stringWriter) throws ComponentException{
		String simpleUrl = getUrlAnnullamentoOrdineMissione();
		return startFlow(stringWriter, simpleUrl);
	}

	private String getUrlOrdineMissione() {
		return "service/api/workflow/activiti$flussoMissioniOrdine/formprocessor";
	}
	
	private String getUrlAnnullamentoOrdineMissione() {
		return "service/api/workflow/activiti$flussoMissioniRevoca/formprocessor";
	}
	
	private String getUrlRimborsoMissione() {
		return "service/api/workflow/activiti$flussoMissioniRimborso/formprocessor";
	}
	
	public Response startFlowRimborsoMissione(StringWriter stringWriter) throws ComponentException{
		String simpleUrl = getUrlRimborsoMissione();
		return startFlow(stringWriter, simpleUrl);
	}

	private Response startFlow(StringWriter stringWriter, String simpleUrl) {
		try {
			String url = getRepositoryURL()+simpleUrl;
			logger.info("Start Flow. Url: "+url+" - Content: "+stringWriter.getBuffer().toString());
			Response responsePost = invokePOST(new UrlBuilder(url), MimeTypes.JSON, stringWriter.getBuffer().toString().getBytes());
			if (responsePost.getResponseCode()!=200) 
				throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: "+ responsePost.getErrorContent()+".");
			return responsePost;
		} catch (CMISException e) {
			throw e;
		} catch (Exception e) {
			throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
		}
	}
	
	public void restartFlow(StringWriter stringWriter, ResultFlows result) {

		try {
			String url = getRepositoryURL()+"service/api/task/"+result.getTaskId()+"/formprocessor";
			logger.info("url for Restart: "+url + " body: "+stringWriter.getBuffer().toString());
			Response responsePost = invokePOST(new UrlBuilder(url), MimeTypes.JSON, stringWriter.getBuffer().toString().getBytes());
			if (responsePost.getResponseCode()!=200) 
				throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase di riproposizione del flusso documentale. Errore: "+ responsePost.getErrorContent()+".");
		} catch (CMISException e) {
			throw e;
		} catch (Exception e) {
			throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase di riproposizione del flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
		}
	}

	private void nextStepForRestartFlow(ResultFlows result) {
		try {
			String next = "next";
			Response responseNext = invokePOST(new UrlBuilder(getRepositoryURL()+"service/api/workflow/task/end/"+result.getTaskId()+"/Next" ), MimeTypes.JSON, next.getBytes());
			if (responseNext.getResponseCode()!=200) 
				throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase di avanzamento del flusso documentale. Errore: "+ responseNext.getErrorContent()+".");
		} catch (CMISException e) {
			throw e;
		} catch (Exception e) {
			throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase di avanzamento del flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
		}
	}

	public void abortFlow(StringWriter stringWriter, ResultFlows result) {

		try {
			String url = getRepositoryURL()+"service/api/task-instances/"+result.getTaskId();
			logger.info("url for Restart: "+url + " body: "+stringWriter.getBuffer().toString());
			Response responsePut = invokePUT(new UrlBuilder(url), MimeTypes.JSON, stringWriter.getBuffer().toString().getBytes(), null);
			if (responsePut.getResponseCode()!=200) 
				throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase di riproposizione del flusso documentale. Errore: "+ responsePut.getErrorContent()+".");
			nextStepForRestartFlow(result);
		} catch (CMISException e) {
			throw e;
		} catch (Exception e) {
			throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase di riproposizione del flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
		}
	}
	public CMISFileContent getAttachment(String nodeRef){
		CMISFileContent cmisFileContent = new CMISFileContent();
        CmisObject obj = getNodeByNodeRef(nodeRef);
        if (obj != null){
        	cmisFileContent.setStream(getResource(obj));
        	cmisFileContent.setFileName(obj.getName());
        	cmisFileContent.setMimeType(obj.getPropertyValue(PropertyIds.CONTENT_STREAM_MIME_TYPE));
        	return cmisFileContent;
        }

		return null;
	}

	public DatiGruppoSAC getDatiGruppoSAC(String uo) {

		JsonFactory jsonFactory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(jsonFactory); 
		try {
			String url = getRepositoryURL()+"service/api/groups/"+org.apache.commons.lang.StringUtils.rightPad(uo, 26, '0')+"/parents?";
			logger.info("url for GET Dati Gruppo SAC: "+url);
			Response responseGet = invokeGET(new UrlBuilder(url));
			if (responseGet.getResponseCode()!=200) 
				throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase di GET Dati Gruppo SAC. Errore: "+ responseGet.getErrorContent()+".");
			Class<GruppoSAC> grpSAC = GruppoSAC.class;
			GruppoSAC gruppoSac = (GruppoSAC)mapper.readValue(responseGet.getStream(), grpSAC);
			if (gruppoSac != null && gruppoSac.getData() != null && gruppoSac.getData().size() > 0){
				return gruppoSac.getData().get(0);
			} else {
				return null;
			}
		} catch (CMISException e) {
			throw e;
		} catch (Exception e) {
			throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase di riproposizione del flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
		}
	}
	public QueryResult recupeorFlusso(String idFlusso) {
		StringBuilder query = new StringBuilder("select parametriFlusso.wfcnr:statoFlusso, parametriFlusso.wfcnr:taskId, flussoMissioni.cnrmissioni:commento from cmis:document as t ");
		query.append( "inner join wfcnr:parametriFlusso as parametriFlusso on t.cmis:objectId = parametriFlusso.cmis:objectId ");
		query.append(" inner join cnrmissioni:parametriFlussoMissioni as flussoMissioni on t.cmis:objectId = flussoMissioni.cmis:objectId ");
		query.append(" where parametriFlusso.wfcnr:wfInstanceId = '").append(idFlusso).append("'");

		ItemIterable<QueryResult> resultsFolder = search(query);
		if (resultsFolder.getTotalNumItems() == 0){
			return null;
		} else {
			for (QueryResult queryResult : resultsFolder) {
				return queryResult;
			}
		}
		return null;
	}
	protected List<CmisObject> recuperoDocumento(Folder node, String tipoDocumento) {
		return Optional.ofNullable(node) 
			.map(folder -> folder.getChildren())
			.map(cmisObjects -> {
                List<CmisObject> list = new ArrayList<CmisObject>();
                cmisObjects.forEach(cmisObject ->
                        list.add(cmisObject));
                return list;
            })
			.map(lista -> lista.stream()
			.filter(cmisObj -> {
				Boolean str = ((ArrayList<String>)cmisObj.getPropertyValue(PropertyIds.SECONDARY_OBJECT_TYPE_IDS)).contains(tipoDocumento) && 
						!((ArrayList<String>)cmisObj.getPropertyValue(PropertyIds.SECONDARY_OBJECT_TYPE_IDS)).contains(CMISMissioniAspect.FILE_ELIMINATO.value());
				return str;
			}).collect(Collectors.toList())).orElse(new ArrayList<CmisObject>());
	}
	
	public void eliminaFilePresenteNelFlusso(Principal principal, String idNodo) {
		Document node = (Document)getNodeByNodeRef(idNodo);
		String nomeFile = node.getName();
		nomeFile = sanitizeFilename(nomeFile+".eliminato");
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(PropertyIds.NAME, nomeFile);
		metadataProperties.put(MissioniCMISService.PROPERTY_DESCRIPTION, nomeFile);
		metadataProperties.put(MissioniCMISService.PROPERTY_TITLE, nomeFile);
		metadataProperties.put(MissioniCMISService.PROPERTY_AUTHOR, principal.getName());
		updateProperties(metadataProperties, node);
		addAspect(node, CMISMissioniAspect.FILE_ELIMINATO.value());
	}
}
