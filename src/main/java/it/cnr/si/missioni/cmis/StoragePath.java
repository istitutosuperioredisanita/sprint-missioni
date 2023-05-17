/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.cmis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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

    public StoragePath() {
        super();
    }

    public StoragePath(String path) {
        super();
        this.path = path;
    }

    public static StoragePath construct(String path) {
        return new StoragePath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public StoragePath appendToPath(String append) {
        return StoragePath.construct(getPath() + "/" + append);
    }

    public String getPathConfig() {
        return getPath() + getFolderConfig();
    }

    public String getPathManual() {
        return getPath() + getFolderManual();
    }

    @PostConstruct
    public void init() {
        if (env != null && env.getProperty(STORAGE_YML + "." + FOLDER_PATH_YML) != null) {
            path = env.getProperty(STORAGE_YML + "." + FOLDER_PATH_YML);
        }
        if (env != null && env.getProperty(STORAGE_YML + "." + FOLDER_PATH_CONFIG_YML) != null) {
            folderConfig = env.getProperty(STORAGE_YML + "." + FOLDER_PATH_CONFIG_YML);
        }
        if (env != null && env.getProperty(STORAGE_YML + "." + FOLDER_PATH_MANUAL_YML) != null) {
            folderManual = env.getProperty(STORAGE_YML + "." + FOLDER_PATH_MANUAL_YML);
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
