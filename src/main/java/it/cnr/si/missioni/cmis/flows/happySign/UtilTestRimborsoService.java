package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.anagrafica.EmployeeDetails;
import it.iss.si.dto.happysign.base.EnumTypeSigner;
import it.iss.si.dto.happysign.base.File;
import it.iss.si.dto.happysign.base.Signer;
import it.iss.si.dto.happysign.base.UserFea;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;
import it.iss.si.service.HappySignService;
import it.iss.si.service.UtilAce;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@ConditionalOnProperty(prefix = "flows", name = "test", havingValue = "true")
public class UtilTestRimborsoService {
    private static final Log logger = LogFactory.getLog(UtilTestRimborsoService.class);
    @Autowired
    protected HappySignService happySignService;

    @Autowired
    protected MissioniCMISService missioniCMISService;

    public byte[] getDocumento(StorageObject storageObject) throws IOException {
        return IOUtils.toByteArray(missioniCMISService.getResource(storageObject));
    }
    public StartWorflowDto createUStartWorfloDto(RimborsoMissione rimborsoMissione, StorageObject modulo) throws IOException{
        StartWorflowDto startInfo= new StartWorflowDto();
        startInfo.setTemplateName("duilio_app");

        startInfo.addSigner(rimborsoMissione.getUid());
        startInfo.addSigner(rimborsoMissione.getUid());

        File f = new File();
        f.setFilename(modulo.getKey());
        f.setPdf(getDocumento(modulo));

        startInfo.setFileToSign(f);

        return startInfo;
    }
}
