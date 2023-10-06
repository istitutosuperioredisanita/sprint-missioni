package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.domain.User;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
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
        "!T(org.springframework.util.StringUtils).isEmpty('${flows.autorizzazione.progdifuo:}')"
)
public class AutorizzazioneMissioneProgDifUo extends AbstractHappySign implements AutorizzazioneMissione {
    @Value("${flows.autorizzazione.progdifuo:#{null}}")
    private String templateName;
    @Override
    public UploadToComplexRequest createUploadComplexrequest(OrdineMissione ordineMissione, StorageObject moduloOrdineMissione) throws IOException {
        UploadToComplexRequest uploadToComplexRequest = new UploadToComplexRequest();
            uploadToComplexRequest.setNametemplate(templateName);
            UserFea userFeaPrg= getUserFeaByCf(getProgetto(ordineMissione).getCodice_fiscale_responsabile());
            UserFea userUoRich = getResponsabile( ordineMissione.getUoRich());
            UserFea userUoSpesa= getResponsabile(ordineMissione.getUoSpesa());
            UserFea userRich = getUserFea(ordineMissione.getUid());
            Signer signer = new Signer(userRich);
                signer.setType(EnumTypeSigner.internal);
             signer.setOrder(0);
        uploadToComplexRequest.addSigner(signer);

        signer = new Signer(userUoRich);
        signer.setType(EnumTypeSigner.internal);
        signer.setOrder(1);
        uploadToComplexRequest.addSigner(signer);

        signer = new Signer(userFeaPrg);
        signer.setType(EnumTypeSigner.internal);
        signer.setOrder(2);
        uploadToComplexRequest.addSigner(signer);

        signer = new Signer(userUoSpesa);
        signer.setType(EnumTypeSigner.internal);
        signer.setOrder(3);
        uploadToComplexRequest.addSigner(signer);

        File f = new File();
        f.setFilename(moduloOrdineMissione.getKey());
        f.setPdf(getDocumento(moduloOrdineMissione));
        uploadToComplexRequest.addPdf(f);

        return uploadToComplexRequest;


    }
    @Override
    public Boolean isFlowToSend(OrdineMissione ordineMissione) {
        return ( signRespProgetto(ordineMissione ) && signRespUoAfferente(ordineMissione));
    }
}
