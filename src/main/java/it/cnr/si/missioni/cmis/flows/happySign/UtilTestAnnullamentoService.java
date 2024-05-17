package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import it.iss.si.dto.happysign.base.AttachedFile;
import it.iss.si.dto.happysign.base.File;
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
public class UtilTestAnnullamentoService {
    private static final Log logger = LogFactory.getLog(UtilTestAnnullamentoService.class);
    @Autowired
    protected HappySignService happySignService;

    @Autowired
    protected MissioniCMISService missioniCMISService;

    public byte[] getDocumento(StorageObject storageObject) throws IOException {
        return IOUtils.toByteArray(missioniCMISService.getResource(storageObject));
    }
    public StartWorflowDto createUStartWorfloDto(AnnullamentoOrdineMissione annullamentoOrdineMissione, StorageObject modulo, List<StorageObject> allegati) throws IOException{
        StartWorflowDto startInfo= new StartWorflowDto();
        startInfo.setTemplateName("duilio_app");

        startInfo.addSigner(annullamentoOrdineMissione.getUid());
        startInfo.addSigner(annullamentoOrdineMissione.getUid());

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
