package it.cnr.si.missioni.cmis;

import it.cnr.si.missioni.cmis.acl.ACLType;
import it.cnr.si.missioni.cmis.acl.Permission;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Utility;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.bindings.CmisBindingFactory;
import org.apache.chemistry.opencmis.client.bindings.impl.CmisBindingsHelper;
import org.apache.chemistry.opencmis.client.bindings.impl.SessionImpl;
import org.apache.chemistry.opencmis.client.bindings.spi.AbstractAuthenticationProvider;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.client.util.OperationContextUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
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
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class MissioniCMISService {
	private static String REPOSITORY_URL = "repository.base.url";
	private static String CHEMISTRY_ATOMPUB_URL = "org.apache.chemistry.opencmis.binding.atompub.url";
	private static String CHEMISTRY_TYPE = "org.apache.chemistry.opencmis.binding.spi.type";
	private static String CHEMISTRY_CONN_TIMEOUT = "org.apache.chemistry.opencmis.binding.connecttimeout";
	private static String CHEMISTRY_READ_TIMEOUT = "org.apache.chemistry.opencmis.binding.readtimeout";
	private static String CHEMISTRY_HTTP_CLASSNAME = "org.apache.chemistry.opencmis.binding.httpinvoker.classname";
	private static String USERNAME = "user.admin.username";
	private static String PASSWORD = "user.admin.password";
	private static String ROOT_CONFIGURATION_CMIS = "spring.cmis.";
	
	private transient static final Log logger = LogFactory.getLog(MissioniCMISService.class);

//	@Autowired
//	private CmisPath cmisPath;

	@Autowired
	private ApplicationContext appContext;
	
	@Inject	
	private Environment env;

	private RelaxedPropertyResolver propertyResolver;	
	
	
	public static final String ASPECT_TITLED = "P:cm:titled",
			ASPECT_FLUSSO = "P:wfcnr:parametriFlusso",
			ASPECT_FLUSSO_MISSIONI = "P:cnrmissioni:parametriFlussoMissioni",
			PROPERTY_TITLE = "cm:title", 
			PROPERTY_DESCRIPTION = "cm:description", 
			PROPERTY_AUTHOR = "cm:author",
			ASPECT_AUTHOR = "P:"+PROPERTY_AUTHOR,
			ALFCMIS_NODEREF = "alfcmis:nodeRef",
			PROPERTY_AUTOVERSION = "cm:autoVersion", 
			PROPERTY_AUTOVERSION_ON_UPDATE = "cm:autoVersionOnUpdateProps",
			PROPERTY_FOLDER = "cmis:folder",
			PATH_SERVICE_PERMISSIONS = "service/cnr/nodes/permissions/";

	private String baseURL;
	private Map<String, String> serverParameters = new HashMap<String, String>();
	
	protected Session missioniSession;
	protected BindingSession missioniBindingSession;
	
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public void setServerParameters(Map<String, String> serverParameters) {
		this.serverParameters = serverParameters;
	}

	@PostConstruct
	public void init(){
    	loadCMISConfiguration();

    	missioniSession = createSession();
		OperationContext operationContext = OperationContextUtils.createMaximumOperationContext();
		operationContext.setMaxItemsPerPage(Integer.MAX_VALUE);
		missioniSession.setDefaultContext(operationContext);
		missioniBindingSession = createBindingSession();
	}

	private void loadCMISConfiguration() {
		this.propertyResolver = new RelaxedPropertyResolver(env, ROOT_CONFIGURATION_CMIS);
    	if (propertyResolver != null && propertyResolver.getProperty(REPOSITORY_URL) != null) {
    		baseURL = propertyResolver.getProperty(REPOSITORY_URL);
    		serverParameters.put(CHEMISTRY_ATOMPUB_URL, baseURL+propertyResolver.getProperty(CHEMISTRY_ATOMPUB_URL));
    		serverParameters.put(CHEMISTRY_TYPE, propertyResolver.getProperty(CHEMISTRY_TYPE));
    		serverParameters.put(CHEMISTRY_CONN_TIMEOUT, propertyResolver.getProperty(CHEMISTRY_CONN_TIMEOUT));
    		serverParameters.put(CHEMISTRY_READ_TIMEOUT, propertyResolver.getProperty(CHEMISTRY_READ_TIMEOUT));
    		serverParameters.put(CHEMISTRY_HTTP_CLASSNAME, propertyResolver.getProperty(CHEMISTRY_HTTP_CLASSNAME));
    		serverParameters.put(USERNAME, propertyResolver.getProperty(USERNAME));
    		serverParameters.put(PASSWORD, propertyResolver.getProperty(PASSWORD));
    	}
	}

    private Session createSession(){
    	return createSession(serverParameters.get(USERNAME), serverParameters.get(PASSWORD));
    }

    private Session createSession(String userName, String password){
    	SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> sessionParameters = new HashMap<String, String>();
        sessionParameters.putAll(serverParameters);
        sessionParameters.put(SessionParameter.USER, userName);
        sessionParameters.put(SessionParameter.PASSWORD, password);

        sessionParameters.put(SessionParameter.REPOSITORY_ID, sessionFactory.getRepositories(sessionParameters).get(0).getId());
        sessionParameters.put(SessionParameter.LOCALE_ISO3166_COUNTRY, Locale.ITALY.getCountry());
        sessionParameters.put(SessionParameter.LOCALE_ISO639_LANGUAGE, Locale.ITALY.getLanguage());
        sessionParameters.put(SessionParameter.LOCALE_VARIANT, Locale.ITALY.getVariant());

    	return sessionFactory.createSession(sessionParameters);
    }

	private BindingSession createBindingSession(){
    	BindingSession session = new SessionImpl();
        if (serverParameters == null){
            return null;
        }
        Map<String, String> sessionParameters = new HashMap<String, String>();
        sessionParameters.putAll(serverParameters);
        sessionParameters.put(SessionParameter.USER, serverParameters.get(USERNAME));
        sessionParameters.put(SessionParameter.PASSWORD, serverParameters.get(PASSWORD));
        if (!sessionParameters.containsKey(SessionParameter.AUTHENTICATION_PROVIDER_CLASS)) {
            sessionParameters.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS, CmisBindingFactory.STANDARD_AUTHENTICATION_PROVIDER);
        }
        sessionParameters.put(SessionParameter.AUTH_HTTP_BASIC, "true");
        sessionParameters.put(SessionParameter.AUTH_SOAP_USERNAMETOKEN, "false");
        for (Map.Entry<String, String> entry : sessionParameters.entrySet()) {
            session.put(entry.getKey(), entry.getValue());
        }
        // create authentication provider and add it session
        String authProvider = sessionParameters.get(SessionParameter.AUTHENTICATION_PROVIDER_CLASS);
        if (authProvider != null) {
            Object authProviderObj = null;

            try {
                authProviderObj = Class.forName(authProvider).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Could not load authentication provider: " + e, e);
            }

            if (!(authProviderObj instanceof AbstractAuthenticationProvider)) {
                throw new IllegalArgumentException(
                        "Authentication provider does not extend AbstractAuthenticationProvider!");
            }

            session.put(CmisBindingsHelper.AUTHENTICATION_PROVIDER_OBJECT,
                    (AbstractAuthenticationProvider) authProviderObj);
            ((AbstractAuthenticationProvider) authProviderObj).setSession(session);
        }
    	return session;
    }

	public void makeVersionable(CmisObject node){
		addAutoVersion((Document) node, false);
	}
	
	public String sanitizeFilename(String name) {
		name = name.trim();
		Pattern pattern = Pattern.compile("([\\/:@()&<>?\"])");
		Matcher matcher = pattern.matcher(name);
		if(!matcher.matches()){
			String str1 = matcher.replaceAll("_"); 
			return str1;
		} else {
			return name;
		}		
	}
	
	public String sanitizeFolderName(String name) {
		name = name.trim();
		Pattern pattern = Pattern.compile("([\\/:@()&<>?\"])");
		Matcher matcher = pattern.matcher(name);
		if(!matcher.matches()){
			String str1 = matcher.replaceAll("'"); 
			return str1;
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
		return missioniSession.getObjectByPath(path);
	}
	
	public CmisObject getNodeByNodeRef(String nodeRef){
		return missioniSession.getObject(nodeRef);
	}

	public CmisObject getNodeByNodeRef(String nodeRef, UsernamePasswordCredentials usernamePasswordCredentials){
		return createSession(usernamePasswordCredentials.getUserName(), usernamePasswordCredentials.getPassword()).getObject(nodeRef);
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
		CmisPath cmisPath = (CmisPath)appContext.getBean("cmisPath");
		return cmisPath;
	}
	
	public CmisPath createFolderIfNotPresent(CmisPath cmisPath, Map<String, Object> metadataProperties, List<String> aspectsToAdd, String folderName){
		CmisObject cmisObject = getNodeByPath(cmisPath);
		try{
			metadataProperties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, aspectsToAdd);
			Folder folder = (Folder) getNodeByNodeRef(missioniSession.createFolder(metadataProperties, cmisObject).getId());
			return CmisPath.construct(folder.getPath());
		}catch(CmisContentAlreadyExistsException _ex){
				Folder folder = (Folder) getNodeByPath(cmisPath.getPath()+(cmisPath.getPath().equals("/")?"":"/")+sanitizeFilename(folderName).toLowerCase());
		        List<String> aspects = folder.getPropertyValue(PropertyIds.SECONDARY_OBJECT_TYPE_IDS);
		        aspects.addAll(aspectsToAdd);
		        metadataProperties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, aspects);
				folder.updateProperties(metadataProperties, true);
				return CmisPath.construct(folder.getPath());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	public void deleteNode(CmisObject cmisObject){
		if (cmisObject.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER))
			((Folder)cmisObject).deleteTree(true, UnfileObject.DELETE, false);
		else	
			missioniSession.delete(cmisObject);
	}
	
	public InputStream getResource(CmisObject cmisObject){
		return ((Document)cmisObject).getContentStream().getStream();
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
					missioniSession.createDocument(metadataProperties, parentNode, contentStream, VersioningState.MAJOR).getId());
			if (permissions.length > 0 ){
				setInheritedPermission(missioniBindingSession, node.getProperty(ALFCMIS_NODEREF).getValueAsString(), Boolean.FALSE);
				if (permissions != null && permissions.length > 0) {
					addAcl(missioniBindingSession, node.getProperty(ALFCMIS_NODEREF).getValueAsString(), Permission.convert(permissions));
				}
			}
			if (makeVersionable)
				addAutoVersion((Document) node, false);
			return node;
		}catch (CmisBaseException e) {
			throw e;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public InputStream recuperoFileFromObjectID(String id){
		if (id != null){
			try{
				return getResource(getNodeByNodeRef(id));
			}catch (CmisObjectNotFoundException _ex){
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
			addAspect(node, ASPECT_FLUSSO);
			addAspect(node, ASPECT_FLUSSO_MISSIONI);
			updateProperties(metadataProperties, node);
		}
		return node;
	}

    @Transactional(readOnly = true)
	public void updateProperties(Map<String, Object> metadataProperties, CmisObject node){
		try {
			if (node.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)) {
				node = ((Document)node).getObjectOfLatestVersion(false);
				node = missioniSession.getObject(node);
				node.refresh();
			}
			node.updateProperties(metadataProperties, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Document updateContent(String nodeRef, InputStream inputStream, String contentType){
		Document document = (Document) getNodeByNodeRef(nodeRef);
		ContentStream contentStream = new ContentStreamImpl(
				document.getName(),
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
	
	public ItemIterable<QueryResult> search(StringBuffer query){
		return search(query, missioniSession.getDefaultContext());
	}

	public ItemIterable<QueryResult> search(StringBuffer query, OperationContext operationContext){
		return missioniSession.query(query.toString(), false, operationContext);
	}
	
	public List<CmisObject> searchAndFetchNode(StringBuffer query){
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
		setInheritedPermission(missioniBindingSession, getNodeByPath(cmisPath).getProperty(ALFCMIS_NODEREF).getValueAsString(), inheritedPermission);
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
		Response resp = CmisBindingsHelper.getHttpInvoker(missioniBindingSession).invokePOST(url,
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
				}, missioniBindingSession);
		int status = resp.getResponseCode();
		if (status == HttpStatus.SC_NOT_FOUND
				|| status == HttpStatus.SC_BAD_REQUEST
				|| status == HttpStatus.SC_INTERNAL_SERVER_ERROR)
			throw new CmisRuntimeException("Add Auto Version. Exception: "
					+ resp.getErrorContent());
	}

	public Response invokeGET(UrlBuilder url) {
		return CmisBindingsHelper.getHttpInvoker(missioniBindingSession).invokeGET(url, missioniBindingSession);		
	}
	 
	public Response invokePOST(UrlBuilder url, MimeTypes mimeType, final byte[] content) {
		if (logger.isDebugEnabled())
			logger.debug("Invoke URL:" + url);
		return CmisBindingsHelper.getHttpInvoker(missioniBindingSession).invokePOST(url, mimeType.mimetype(),
				new Output() {
					public void write(OutputStream out) throws Exception {
            			out.write(content);
            		}
        		}, missioniBindingSession);
	}

	public Response invokePUT(UrlBuilder url, MimeTypes mimeType, final byte[] content, Map<String, String> headers) {
		if (logger.isDebugEnabled())
			logger.debug("Invoke URL:" + url);
		return CmisBindingsHelper.getHttpInvoker(missioniBindingSession).invokePUT(url, mimeType.mimetype(), headers, 
				new Output() {
					public void write(OutputStream out) throws Exception {
            			out.write(content);
            		}
        		}, missioniBindingSession);
	}

    public String recuperoNodeRefUtente(String username){
		JsonFactory jsonFactory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(jsonFactory); 
		try {
			Response responseFirmatario = invokeGET(new UrlBuilder(getRepositoryURL()+"service/cnr/person/person/"+username));
			if (responseFirmatario.getResponseCode()!=200) 
				throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Utente " + username + " non registrato o servizio non disponibile.");
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
			throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Utente " + username + " non registrato.");
		}

    }
    
	public Response startFlowOrdineMissione(StringWriter stringWriter) throws Exception{
		try {
			logger.debug(stringWriter.getBuffer().toString());
			Response responsePost = invokePOST(new UrlBuilder(getRepositoryURL()+"service/api/workflow/activiti$flussoMissioni/formprocessor"), MimeTypes.JSON, stringWriter.getBuffer().toString().getBytes());
			if (responsePost.getResponseCode()!=200) 
				throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: "+ responsePost.getErrorContent()+".");
			return responsePost;
		} catch (CMISException e) {
			throw e;
		} catch (Exception e) {
			throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
		}
	}
	
	public void restartFlowOrdineMissione(StringWriter stringWriter, ResultFlows result) {

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

	private void nextStepForRestartFlowOrdineMissione(ResultFlows result) {
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

	public void abortFlowOrdineMissione(StringWriter stringWriter, ResultFlows result) {

		try {
			String url = getRepositoryURL()+"service/api/task-instances/"+result.getTaskId();
			logger.info("url for Restart: "+url + " body: "+stringWriter.getBuffer().toString());
			Response responsePut = invokePUT(new UrlBuilder(url), MimeTypes.JSON, stringWriter.getBuffer().toString().getBytes(), null);
			if (responsePut.getResponseCode()!=200) 
				throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase di riproposizione del flusso documentale. Errore: "+ responsePut.getErrorContent()+".");
			nextStepForRestartFlowOrdineMissione(result);
		} catch (CMISException e) {
			throw e;
		} catch (Exception e) {
			throw new CMISException(CodiciErrore.ERRGEN, "Errore in fase di riproposizione del flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
		}
	}

}
