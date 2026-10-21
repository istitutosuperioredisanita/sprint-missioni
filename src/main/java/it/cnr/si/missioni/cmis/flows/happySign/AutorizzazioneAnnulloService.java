package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.flows.happySign.dto.StartSignatureFlowDto;
import it.cnr.si.missioni.cmis.flows.happySign.interfaces.AutorizzazioneAnnullamentoMissione;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.spring.storage.StorageObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AutorizzazioneAnnulloService {

    private static final Log logger = LogFactory.getLog(AutorizzazioneAnnulloService.class);

    @Autowired(required = false)
    UtilTestAnnullamentoService utilTestAnnullamentoService;

    private final List<AutorizzazioneAnnullamentoMissione> autorizzazioneAnnullamentoMissione;

    @Autowired
    public AutorizzazioneAnnulloService(List<AutorizzazioneAnnullamentoMissione> autorizzazioneAnnullamentoMissione) {
        this.autorizzazioneAnnullamentoMissione = autorizzazioneAnnullamentoMissione;
    }

    public List<AutorizzazioneAnnullamentoMissione> getFlowAutorizzazione(AnnullamentoOrdineMissione annullamentoOrdineMissione) {
        return Optional.ofNullable(autorizzazioneAnnullamentoMissione).orElse(null).
                stream().
                filter(autorizzazione -> autorizzazione.isFlowToSend(annullamentoOrdineMissione)).
                collect(Collectors.toList());
    }


    public String sendAutorizzazione(AnnullamentoOrdineMissione annullamentoOrdineMissione, StorageObject modulo, List<StorageObject> allegati) throws Exception {
        AutorizzazioneAnnullamentoMissione autorizzazioneAnnullamentoMissione = Optional.ofNullable(getFlowAutorizzazione(annullamentoOrdineMissione)).
                orElse(null).stream().findFirst().orElse(null);
        if (Optional.ofNullable(autorizzazioneAnnullamentoMissione).isPresent()) {
            logger.info(autorizzazioneAnnullamentoMissione);
        }

        StartSignatureFlowDto startSignatureFlowDto = autorizzazioneAnnullamentoMissione.createStartSignatureFlowDto(annullamentoOrdineMissione, modulo, allegati);
        List<String> signersDef = UtilHappySign.getNoDoubleSigners(startSignatureFlowDto.getSigners());
        startSignatureFlowDto.setSigners(signersDef);

        if (Optional.ofNullable(utilTestAnnullamentoService).isPresent()) {
            UtilTestService.showSigned(startSignatureFlowDto);
            startSignatureFlowDto = utilTestAnnullamentoService.createStartWorkflowDto(annullamentoOrdineMissione, modulo, allegati);
        }

        String result = autorizzazioneAnnullamentoMissione.send(startSignatureFlowDto);

        if (Optional.ofNullable(utilTestAnnullamentoService).isPresent()) {
            utilTestAnnullamentoService.sendMailForAnnullamentoOrdineMissione(annullamentoOrdineMissione, signersDef);
        }

        return result;
    }

}
