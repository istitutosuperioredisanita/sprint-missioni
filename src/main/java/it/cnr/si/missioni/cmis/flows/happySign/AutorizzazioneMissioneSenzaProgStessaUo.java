package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;
import it.iss.si.service.HappySignURLCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(HappySignURLCondition.class)
@ConditionalOnExpression(
        "!T(org.springframework.util.StringUtils).isEmpty('${flows.autorizzazione.noprogstessauo:}')"
)
public class AutorizzazioneMissioneSenzaProgStessaUo extends FlussiToHappySignService implements AutorizzazioneMissione{
    @Value("${flows.autorizzazione.noprogstessauo:#{null}}")
    private String templateName;
    @Override
    public UploadToComplexRequest createUploadComplexrequest(OrdineMissione ordineMissione) {
        return null;
    }
    @Override
    public Boolean isFlowToSend(OrdineMissione ordineMissione) {
        return ( !signRespProgetto(ordineMissione ) && ( !signResoUoAfferente(ordineMissione)));
    }
}
