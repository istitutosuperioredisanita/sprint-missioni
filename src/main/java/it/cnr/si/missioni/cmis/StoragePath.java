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
public class StoragePath {
	
	private final String STORAGE_YML = "storage";
	private final String FOLDER_PATH_YML = "folder_path";
	private final String FOLDER_PATH_CONFIG_YML = "folder_path_config";
	private final String FOLDER_PATH_MANUAL_YML = "folder_path_manual";
	
	@Value("${storage.folder_path}")
	private String path;

	@Value("${storage.folder_path_config}")
	private String folderConfig;

	@Value("${storage.folder_path_manual}")
	private String folderManual;

	@Autowired
	private Environment env;

	private RelaxedPropertyResolver propertyResolver;	
	

	public StoragePath() {
    	super();
	}

	public static StoragePath construct(String path){
		return new StoragePath(path);
	}
	
	public StoragePath(String path) {
		super();
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public StoragePath appendToPath(String append){
		return StoragePath.construct(getPath()+ "/" + append);
	}
	
	public String getPathConfig(){
		return getPath()+getFolderConfig();
	}
	
	public String getPathManual(){
		return getPath()+getFolderManual();
	}
	
	@PostConstruct
	public void init(){
		this.propertyResolver = new RelaxedPropertyResolver(env, STORAGE_YML+".");
    	if (propertyResolver != null && propertyResolver.getProperty(FOLDER_PATH_YML) != null) {
    		path = propertyResolver.getProperty(FOLDER_PATH_YML);
    	}
    	if (propertyResolver != null && propertyResolver.getProperty(FOLDER_PATH_CONFIG_YML) != null) {
    		folderConfig = propertyResolver.getProperty(FOLDER_PATH_CONFIG_YML);
    	}
    	if (propertyResolver != null && propertyResolver.getProperty(FOLDER_PATH_MANUAL_YML) != null) {
    		folderManual = propertyResolver.getProperty(FOLDER_PATH_MANUAL_YML);
    	}
	}

	public String getFolderConfig() {
		return folderConfig;
	}

	public void setFolderConfig(String folderConfig) {
		this.folderConfig = folderConfig;
	}

	public String getFolderManual() {
		return folderManual;
	}

	public void setFolderManual(String folderManual) {
		this.folderManual = folderManual;
	}
}
