package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
import it.cnr.si.missioni.cmis.flows.happySign.interfaces.AutorizzazioneMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.service.HappySignURLCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Conditional(HappySignURLCondition.class)
@ConditionalOnExpression(
        "!T(org.springframework.util.StringUtils).isEmpty('${flows.autorizzazione.cns_cnt:}')"
)
public class AutorizzazioneMissioneCNS_CNT extends AbstractHappySign implements AutorizzazioneMissione {
    @Value("${flows.autorizzazione.cns_cnt:#{null}}")
    private String templateName;

    @Override
    public StartWorflowDto createStartWorkflowDto(OrdineMissione ordineMissione, StorageObject modulo, List<StorageObject> allegati) throws IOException {
        StartWorflowDto startInfo = new StartWorflowDto();
        startInfo.setTemplateName(templateName);

        startInfo.addSigner(ordineMissione.getUid());
        setDirDipToSign(startInfo,ordineMissione);
        startInfo.setFileToSign(getFile(modulo, allegati));

        return startInfo;
    }

    @Override
    public Boolean isFlowToSend(OrdineMissione ordineMissione) {
        return (signGae(ordineMissione) && uoGaeSuCNSOrCNT(ordineMissione));
    }
}
