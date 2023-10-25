package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.happysign.base.EnumTypeSigner;
import it.iss.si.dto.happysign.base.File;
import it.iss.si.dto.happysign.base.Signer;
import it.iss.si.dto.happysign.base.UserFea;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;
import it.iss.si.service.HappySignURLCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Conditional(HappySignURLCondition.class)
@ConditionalOnExpression(
        "!T(org.springframework.util.StringUtils).isEmpty('${flows.rimborso.default.}')"
)
public class AutorizzazioneRimborsoMissioneDefault extends AbstractHappySign implements AutorizzazioneRimborsoMissione {
    @Value("${flows.rimborso.default.template:#{null}}")
    private String templateName;

    @Value("${flows.rimborso.default.signSiglaUo:#{null}}")
    private String signSiglaUo;
    @Override
    public UploadToComplexRequest createUploadComplexrequest(RimborsoMissione rimborsoMissione, StorageObject moduloOrdineMissione) throws IOException {
        UploadToComplexRequest uploadToComplexRequest = new UploadToComplexRequest();
        uploadToComplexRequest.setNametemplate(templateName);
/*
        UserFea userUoRich = getResponsabile( ordineMissione.getUoRich());
        UserFea userRich = getUserFea(ordineMissione.getUid());
        Signer signer = new Signer(userRich);
        signer.setType(EnumTypeSigner.internal);
        signer.setOrder(0);
        uploadToComplexRequest.addSigner(signer);

        signer = new Signer(userUoRich);
        signer.setType(EnumTypeSigner.internal);
        signer.setOrder(1);
        uploadToComplexRequest.addSigner(signer);
*/



        File f = new File();
        f.setFilename(moduloOrdineMissione.getKey());
        f.setPdf(getDocumento(moduloOrdineMissione));
        uploadToComplexRequest.addPdf(f);

        return uploadToComplexRequest;
    }
    @Override
    public Boolean isFlowToSend(RimborsoMissione rimborsoMissione) {
        return true;
    }
}
