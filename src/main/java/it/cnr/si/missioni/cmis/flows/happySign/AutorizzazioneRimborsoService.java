package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;
import it.iss.si.dto.happysign.response.UploadToComplexResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AutorizzazioneRimborsoService {

    private static final Log logger = LogFactory.getLog(AutorizzazioneRimborsoService.class);

    @Autowired
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


    public UploadToComplexResponse sendAutorizzazione(RimborsoMissione rimborsoMissione, StorageObject moduloOrdineMissione) throws IOException {
        AutorizzazioneRimborsoMissione autorizzazioneRimborso = Optional.ofNullable(getFlowAutorizzazione(rimborsoMissione)).
                orElse(null).stream().findFirst().orElse(null);
        if ( Optional.ofNullable(autorizzazioneRimborso).isPresent()){
           logger.info(autorizzazioneRimborso);
        }
        UploadToComplexRequest uploadToComplexRequest= null;
        if ( Optional.ofNullable(utilTestRimborsoService).isPresent())
            uploadToComplexRequest = utilTestRimborsoService.createUploadToComplexRequest(rimborsoMissione,moduloOrdineMissione);
        else
            uploadToComplexRequest = autorizzazioneRimborso.createUploadComplexrequest(rimborsoMissione, moduloOrdineMissione);
       return autorizzazioneRimborso.send(uploadToComplexRequest);

    }

}
