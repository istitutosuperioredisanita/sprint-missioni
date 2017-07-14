package it.cnr.si.missioni.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.cmis.CMISFileAttachmentComplete;
import it.cnr.si.missioni.cmis.MissioniCMISService;

@Service
public class ManualService {

	@Autowired
    private MissioniCMISService missioniCMISService;
	
	public List<CMISFileAttachmentComplete> getManuals() throws ComponentException{
		Folder folder = (Folder) missioniCMISService.getNodeByPath(missioniCMISService.getBasePath().getPathManual());
		if (folder != null){
	        ItemIterable<CmisObject> children = ((Folder) folder).getChildren();
			if (children != null){
		        List<CMISFileAttachmentComplete> lista = new ArrayList<CMISFileAttachmentComplete>();
		        for (CmisObject object : children){
		        	Document doc = (Document)object;
		        	CMISFileAttachmentComplete cmisFileAttachment = new CMISFileAttachmentComplete();
		        	cmisFileAttachment.setNomeFile(doc.getPropertyValue(PropertyIds.DESCRIPTION));
		        	cmisFileAttachment.setVersion(doc.getPropertyValue(PropertyIds.VERSION_LABEL));
		        	GregorianCalendar cal = doc.getPropertyValue(PropertyIds.LAST_MODIFICATION_DATE);
		        	cmisFileAttachment.setDate(cal.getTime());
		        	cmisFileAttachment.setId(doc.getId());
		        	lista.add(cmisFileAttachment);
		        }
		        return lista;
			}
		}
		return Collections.<CMISFileAttachmentComplete>emptyList();
	}
}
