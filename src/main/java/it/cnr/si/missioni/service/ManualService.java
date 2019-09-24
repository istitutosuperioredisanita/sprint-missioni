package it.cnr.si.missioni.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.cmis.CMISFileAttachmentComplete;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;

@Service
public class ManualService {

	@Autowired
    private MissioniCMISService missioniCMISService;
	
	public List<CMISFileAttachmentComplete> getManuals() throws ComponentException{
		StorageObject folder = missioniCMISService.getStorageObjectByPath(missioniCMISService.getBasePath().getPathManual());
		if (folder != null){
	        List<StorageObject> children = missioniCMISService.getChildren(folder);
			if (children != null){
		        List<CMISFileAttachmentComplete> lista = new ArrayList<CMISFileAttachmentComplete>();
		        for (StorageObject doc : children){
		        	CMISFileAttachmentComplete cmisFileAttachment = new CMISFileAttachmentComplete();
		        	cmisFileAttachment.setNomeFile(doc.getPropertyValue(StoragePropertyNames.DESCRIPTION.value()));
		        	cmisFileAttachment.setVersion("1");
		        	GregorianCalendar cal = doc.getPropertyValue(MissioniCMISService.PROPERTY_LAST_MODIFICATION_DATE);
		        	cmisFileAttachment.setDate(cal.getTime());
		        	cmisFileAttachment.setId(doc.getKey());
		        	lista.add(cmisFileAttachment);
		        }
		        return lista;
			}
		}
		return Collections.<CMISFileAttachmentComplete>emptyList();
	}
}
