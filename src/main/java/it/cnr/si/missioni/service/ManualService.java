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

package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.cmis.CMISFileAttachmentComplete;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class ManualService {

    @Autowired
    private MissioniCMISService missioniCMISService;

    public List<CMISFileAttachmentComplete> getManuals() throws ComponentException {
        StorageObject folder = missioniCMISService.getStorageObjectByPath(missioniCMISService.getBasePath().getPathManual());
        if (folder != null) {
            List<StorageObject> children = missioniCMISService.getChildren(folder);
            if (children != null) {
                List<CMISFileAttachmentComplete> lista = new ArrayList<CMISFileAttachmentComplete>();
                for (StorageObject doc : children) {
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
        return Collections.emptyList();
    }
}
