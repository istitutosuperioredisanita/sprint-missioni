package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import it.iss.si.dto.happysign.base.*;
import it.iss.si.service.HappySignService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "flows", name = "test", havingValue = "true")
public class UtilTestService {
    private static final Log logger = LogFactory.getLog(UtilTestService.class);
    @Autowired
    protected HappySignService happySignService;

    @Autowired
    protected MissioniCMISService missioniCMISService;


    public byte[] getDocumento(StorageObject storageObject) throws IOException {
        return IOUtils.toByteArray(missioniCMISService.getResource(storageObject));
    }
    public StartWorflowDto createStartWorkflowDto(OrdineMissione ordineMissione, StorageObject modulo, List<StorageObject> allegati) throws IOException{
        StartWorflowDto startInfo= new StartWorflowDto();
        startInfo.setTemplateName("duilio_app");

        startInfo.addSigner(ordineMissione.getUid());
        startInfo.addSigner(ordineMissione.getUid());

        File f = new File();
        f.setFilename(missioniCMISService.parseFilename(modulo.getKey()));
        f.setPdf(getDocumento(modulo));
        if ( allegati!=null && allegati.size()>0){
            for ( StorageObject so:allegati){
                AttachedFile attachedFile = new AttachedFile();
                attachedFile.setAttached(false);
                attachedFile.setContent(getDocumento( so));
                attachedFile.setFilename(so.getPropertyValue(StoragePropertyNames.NAME.value()));
                f.addAttachedFile(attachedFile);
            }
        }

        startInfo.setFileToSign(f);

        return startInfo;
    }
}
