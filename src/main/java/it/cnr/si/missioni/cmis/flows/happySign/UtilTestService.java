package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.happysign.base.EnumTypeSigner;
import it.iss.si.dto.happysign.base.File;
import it.iss.si.dto.happysign.base.Signer;
import it.iss.si.dto.happysign.base.UserFea;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;
import it.iss.si.service.HappySignService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@ConditionalOnProperty(prefix = "flows", name = "test", havingValue = "true")
public class UtilTestService {
    private static final Log logger = LogFactory.getLog(UtilTestService.class);
    @Autowired
    protected HappySignService happySignService;

    @Autowired
    protected MissioniCMISService missioniCMISService;

    public UserFea getUserFea(String mail){
        return happySignService.getUserFeaByMail(mail);
    }
    public byte[] getDocumento(StorageObject storageObject) throws IOException {
        return IOUtils.toByteArray(missioniCMISService.getResource(storageObject));
    }
    public UploadToComplexRequest createUploadToComplexRequest(OrdineMissione ordineMissione, StorageObject moduloOtdineMissione) throws IOException {
        UploadToComplexRequest uploadToComplexRequest= new UploadToComplexRequest();
        uploadToComplexRequest.setNametemplate("duilio_app");
        UserFea userFea =happySignService.getUserFeaByMail("ciro.salvio@iss.it");
        Signer signer = new Signer(userFea);
        signer.setType(EnumTypeSigner.internal);
        signer.setOrder(0);

        uploadToComplexRequest.addSigner(signer);
        signer = new Signer(userFea);
        signer.setType(EnumTypeSigner.internal);
        signer.setOrder(1);

        uploadToComplexRequest.addSigner(signer);
        File f = new File();
        f.setFilename(moduloOtdineMissione.getKey());
        f.setPdf(getDocumento( moduloOtdineMissione));
        uploadToComplexRequest.addPdf(f);
        logger.info("UtilTestService");
        return uploadToComplexRequest;
    }
}
