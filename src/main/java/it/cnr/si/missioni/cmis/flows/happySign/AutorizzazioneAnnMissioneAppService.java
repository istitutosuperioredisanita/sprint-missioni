package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
import it.cnr.si.missioni.cmis.flows.happySign.interfaces.AutorizzazioneAnnMissioneApp;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.spring.storage.StorageObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AutorizzazioneAnnMissioneAppService {

    private static final Log logger = LogFactory.getLog(AutorizzazioneAnnMissioneAppService.class);

    @Autowired
    UtilTestService utilTestService;
    private final List<AutorizzazioneAnnMissioneApp> missioniAnnullate;
    @Autowired
    public AutorizzazioneAnnMissioneAppService(List<AutorizzazioneAnnMissioneApp> missioniAnnullate) {
        this.missioniAnnullate = missioniAnnullate;
    }

    public List<AutorizzazioneAnnMissioneApp> getFlowAutorizzazione(OrdineMissione ordineMissione){
        return Optional.ofNullable(missioniAnnullate).orElse(null).
                stream().
                filter(autorizzazione->autorizzazione.isFlowToSend(ordineMissione)).
                collect(Collectors.toList());
    }


    public String sendAutorizzazione(OrdineMissione ordineMissione, StorageObject modulo,List<StorageObject> allegati) throws Exception {
        AutorizzazioneAnnMissioneApp autorizzazione = Optional.ofNullable(getFlowAutorizzazione(ordineMissione)).
                orElse(null).stream().findFirst().orElse(null);
        if ( Optional.ofNullable(autorizzazione).isPresent()){
            logger.info(autorizzazione);
        }
        StartWorflowDto startWorflowDto=   autorizzazione.createStartWorkflowDto(ordineMissione, modulo,allegati);
        if ( Optional.ofNullable(utilTestService).isPresent())
            startWorflowDto = utilTestService.createStartWorkflowDto(ordineMissione,modulo,allegati);


       return autorizzazione.send(startWorflowDto.getTemplateName(),
               startWorflowDto.getSigners(),
               startWorflowDto.getApprovers(),
               startWorflowDto.getFileToSign());

    }

}
