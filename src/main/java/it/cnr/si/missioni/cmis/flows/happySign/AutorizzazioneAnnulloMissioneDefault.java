package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.flows.happySign.dto.StartSignatureFlowDto;
import it.cnr.si.missioni.cmis.flows.happySign.interfaces.AutorizzazioneAnnullamentoMissione;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.signature.flow.condition.SignatureDriverIsHappySign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Conditional(SignatureDriverIsHappySign.class)
@ConditionalOnExpression(
        "!T(org.springframework.util.StringUtils).isEmpty('${flows.rimborso.default.}')"
)
public class AutorizzazioneAnnulloMissioneDefault extends AbstractHappySign implements AutorizzazioneAnnullamentoMissione {
    @Value("${flows.annullo.default.template:#{null}}")
    private String templateName;

    public StartSignatureFlowDto createStartSignatureFlowDto(AnnullamentoOrdineMissione annullamentoOrdineMissione, StorageObject modulo, List<StorageObject> allegati) throws IOException {
        StartSignatureFlowDto startInfo = new StartSignatureFlowDto();
        startInfo.setTemplateName(templateName);
        startInfo.addSigner(annullamentoOrdineMissione.getUid());

        setDocumenti(startInfo, modulo, allegati);

        return startInfo;
    }

    @Override
    public Boolean isFlowToSend(AnnullamentoOrdineMissione annullamentoOrdineMission) {
        return true;
    }
}
