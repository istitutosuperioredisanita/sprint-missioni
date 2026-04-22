package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
import it.cnr.si.missioni.cmis.flows.happySign.interfaces.AutorizzazioneMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.anagrafica.EmployeeDetails;
import it.iss.si.service.HappySignURLCondition;
import it.iss.si.service.UtilAce;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@Conditional(HappySignURLCondition.class)
@ConditionalOnExpression(
        "!T(org.springframework.util.StringUtils).isEmpty('${flows.autorizzazione.default.}')"
)
public class AutorizzazioneMissioneSenzaProgStessaUo extends AbstractHappySign implements AutorizzazioneMissione {
    @Value("${flows.autorizzazione.default.template:#{null}}")
    private String templateName;

    public StartWorflowDto createStartWorkflowDto(OrdineMissione ordineMissione, StorageObject modulo, List<StorageObject> allegati) throws IOException {
        StartWorflowDto startInfo = new StartWorflowDto();
        startInfo.setTemplateName(templateName);
        EmployeeDetails userUoRich = getResponsabile(ordineMissione.getUoRich());
        startInfo.addSigner(ordineMissione.getUid());
        startInfo.addSigner(UtilAce.getEmail(userUoRich));

        /*
        File f = new File();
        f.setFilename(modulo.getKey());
        f.setPdf(getDocumento(modulo));
*/
        startInfo.setFileToSign(getFile(modulo, allegati));

        return startInfo;
    }

    @Override
    public Boolean isFlowToSend(OrdineMissione ordineMissione) {
        return (!signRespProgetto(ordineMissione) && (signUoRichEqUoGae(ordineMissione)) && setSignersToMissioni(ordineMissione, Costanti.OLD_AUTH));
    }
}
