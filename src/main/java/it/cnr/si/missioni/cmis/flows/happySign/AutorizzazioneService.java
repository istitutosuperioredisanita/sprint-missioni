package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
import it.cnr.si.missioni.cmis.flows.happySign.interfaces.AutorizzazioneMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.spring.storage.StorageObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AutorizzazioneService {

    private static final Log logger = LogFactory.getLog(AutorizzazioneService.class);

    @Autowired
    UtilTestService utilTestService;

    private final List<AutorizzazioneMissione> autorizzazioneMissione;
    @Autowired
    public AutorizzazioneService(List<AutorizzazioneMissione> autorizzazioneMissione) {
        this.autorizzazioneMissione = autorizzazioneMissione;
    }

    public List<AutorizzazioneMissione> getFlowAutorizzazione(OrdineMissione ordineMissione){
        return Optional.ofNullable(autorizzazioneMissione).orElse(null).
                stream().
                filter(autorizzazione->autorizzazione.isFlowToSend(ordineMissione)).
                collect(Collectors.toList());
    }


    public String sendAutorizzazione(OrdineMissione ordineMissione, StorageObject modulo, List<StorageObject> allegati) throws Exception {
        AutorizzazioneMissione autorizzazione = Optional.ofNullable(getFlowAutorizzazione(ordineMissione)).
                orElse(null).stream().findFirst().orElse(null);
        if (Optional.ofNullable(autorizzazione).isPresent()) {
            logger.info(autorizzazione);
        }

        StartWorflowDto startWorflowDto = autorizzazione.createStartWorkflowDto(ordineMissione, modulo, allegati);
        List<String> signersDef = UtilHappySign.getNoDoubleSigners(startWorflowDto.getSigners());
        startWorflowDto.setSigners(signersDef);
        UtilHappySign.setTemplateFirme(startWorflowDto);

        if (Optional.ofNullable(utilTestService).isPresent()) {
            UtilTestService.showSigned(startWorflowDto);
            startWorflowDto = utilTestService.createStartWorkflowDto(ordineMissione, modulo, allegati);
        }

        String result = autorizzazione.send(startWorflowDto.getTemplateName(),
                startWorflowDto.getSigners(),
                startWorflowDto.getApprovers(),
                startWorflowDto.getFileToSign());

        if (Optional.ofNullable(utilTestService).isPresent()) {
            utilTestService.sendMailForOrdineMissione(ordineMissione, signersDef, autorizzazione);
        }

        return result;
    }

}
