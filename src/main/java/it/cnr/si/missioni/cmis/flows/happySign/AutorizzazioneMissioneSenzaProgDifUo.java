package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.anagrafica.EmployeeDetails;
import it.iss.si.dto.happysign.base.EnumTypeSigner;
import it.iss.si.dto.happysign.base.File;
import it.iss.si.dto.happysign.base.Signer;
import it.iss.si.dto.happysign.base.UserFea;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;
import it.iss.si.service.HappySignURLCondition;
import it.iss.si.service.UtilAce;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Conditional(HappySignURLCondition.class)
@ConditionalOnExpression(
        "!T(org.springframework.util.StringUtils).isEmpty('${flows.autorizzazione.noprogdifuo:}')"
)
public class AutorizzazioneMissioneSenzaProgDifUo extends AbstractHappySign implements AutorizzazioneMissione {
    @Value("${flows.autorizzazione.noprogdifuo:#{null}}")
    private String templateName;

    public StartWorflowDto createUStartWorfloDto(OrdineMissione ordineMissione, StorageObject modulo,List<StorageObject> allegati) throws IOException{
        StartWorflowDto startInfo= new StartWorflowDto();
        startInfo.setTemplateName(templateName);


        EmployeeDetails userUoRich = getResponsabile( ordineMissione.getUoRich());
        EmployeeDetails userUoSpesa= getResponsabile(ordineMissione.getUoSpesa());

        startInfo.addSigner(ordineMissione.getUid());
        startInfo.addSigner(UtilAce.getEmail(userUoRich));
        startInfo.addSigner(UtilAce.getEmail(userUoSpesa));


        File f = new File();
        f.setFilename(modulo.getKey());
        f.setPdf(getDocumento(modulo));

        startInfo.setFileToSign(f);

        return startInfo;
    }

    @Override
    public Boolean isFlowToSend(OrdineMissione ordineMissione) {
        return ( !signRespProgetto(ordineMissione ) && ( signRespUoAfferente(ordineMissione)));
    }
}
