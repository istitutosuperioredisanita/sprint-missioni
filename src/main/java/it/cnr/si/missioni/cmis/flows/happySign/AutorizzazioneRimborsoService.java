package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.flows.happySign.dto.StartSignatureFlowDto;
import it.cnr.si.missioni.cmis.flows.happySign.interfaces.AutorizzazioneRimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.spring.storage.StorageObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AutorizzazioneRimborsoService {

    private static final Log logger = LogFactory.getLog(AutorizzazioneRimborsoService.class);

    @Autowired (required = false)
    UtilTestRimborsoService utilTestRimborsoService;
    private final List<AutorizzazioneRimborsoMissione> autorizzazioneRimborsoMissione;

    @Autowired
    public AutorizzazioneRimborsoService(List<AutorizzazioneRimborsoMissione> autorizzazioneRimborsoMissione) {
        this.autorizzazioneRimborsoMissione = autorizzazioneRimborsoMissione;
    }

    public List<AutorizzazioneRimborsoMissione> getFlowAutorizzazione(RimborsoMissione rimborsoMissione){
        return Optional.ofNullable(autorizzazioneRimborsoMissione).orElse(null).
                stream().
                filter(autorizzazione->autorizzazione.isFlowToSend(rimborsoMissione)).
                collect(Collectors.toList());
    }


    public String sendAutorizzazione(RimborsoMissione rimborsoMissione, StorageObject modulo, List<StorageObject> allegati) throws Exception {
        AutorizzazioneRimborsoMissione autorizzazioneRimborso = Optional.ofNullable(getFlowAutorizzazione(rimborsoMissione)).
                orElse(null).stream().findFirst().orElse(null);
        if (Optional.ofNullable(autorizzazioneRimborso).isPresent()) {
            logger.info(autorizzazioneRimborso);
        }

        StartSignatureFlowDto startSignatureFlowDto = autorizzazioneRimborso.createStartSignatureFlowDto(rimborsoMissione, modulo, allegati);
        List<String> signersDef = UtilHappySign.getNoDoubleSigners(startSignatureFlowDto.getSigners());
        startSignatureFlowDto.setSigners(signersDef);

        if (Optional.ofNullable(utilTestRimborsoService).isPresent()) {
            UtilTestService.showSigned(startSignatureFlowDto);
            startSignatureFlowDto = utilTestRimborsoService.createStartWorkflowDto(rimborsoMissione, modulo, allegati);
        }

        String result = autorizzazioneRimborso.send(startSignatureFlowDto);

        if (Optional.ofNullable(utilTestRimborsoService).isPresent()) {
            utilTestRimborsoService.sendMailForRimborsoMissione(rimborsoMissione, signersDef);
        }

        return result;
    }

}
