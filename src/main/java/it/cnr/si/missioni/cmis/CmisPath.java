package it.cnr.si.missioni.cmis;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CmisPath {
	
	private final String CMIS_YML = "cmis";
	private final String FOLDER_PATH_YML = "folder_path";
	private final String FOLDER_PATH_CONFIG_YML = "folder_path_config";
	
	@Value("${cmis.folder_path}")
	private String path;

	@Value("${cmis.folder_path_config}")
	private String folderConfig;

	@Autowired
	private Environment env;

	private RelaxedPropertyResolver propertyResolver;	
	

	public CmisPath() {
    	super();
	}

	public static CmisPath construct(String path){
		return new CmisPath(path);
	}
	
	public CmisPath(String path) {
		super();
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public CmisPath appendToPath(String append){
		return CmisPath.construct(getPath()+ "/" + append);
	}
	
	public String getPathConfig(){
		return getPath()+getFolderConfig();
	}
	
	@PostConstruct
	public void init(){
		this.propertyResolver = new RelaxedPropertyResolver(env, CMIS_YML+".");
    	if (propertyResolver != null && propertyResolver.getProperty(FOLDER_PATH_YML) != null) {
    		path = propertyResolver.getProperty(FOLDER_PATH_YML);
    	}
    	if (propertyResolver != null && propertyResolver.getProperty(FOLDER_PATH_CONFIG_YML) != null) {
    		folderConfig = propertyResolver.getProperty(FOLDER_PATH_CONFIG_YML);
    	}
	}

	public String getFolderConfig() {
		return folderConfig;
	}

	public void setFolderConfig(String folderConfig) {
		this.folderConfig = folderConfig;
	}
}
