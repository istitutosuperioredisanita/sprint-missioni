package it.cnr.si.missioni.util;

import it.cnr.si.spring.storage.StoreService;

import java.util.StringTokenizer;

public class UtilStorage {

    public String parseFilename(String file,StoreService storeService) {
        StringTokenizer fileName = new StringTokenizer(file,"\\",false);
        String newFileName = null;

        while (fileName.hasMoreTokens()){
            newFileName = fileName.nextToken();
        }

        if (newFileName != null){
            storeService.sanitizeFilename(newFileName);
        }
        return storeService.sanitizeFilename(file);
    }
}
