package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;
import it.iss.si.dto.happysign.response.UploadToComplexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AutorizzazioneService {

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


    public UploadToComplexResponse sendAutorizzazione(OrdineMissione ordineMissione, StorageObject moduloOrdineMissione) throws IOException {
        AutorizzazioneMissione autorizzazione = Optional.ofNullable(getFlowAutorizzazione(ordineMissione)).
                orElse(null).stream().findFirst().orElse(null);
        if ( Optional.ofNullable(autorizzazione).isPresent()){
           System.out.println("Ciao");
        }
        UploadToComplexRequest uploadToComplexRequest= null;
        if ( Optional.ofNullable(utilTestService).isPresent())
            uploadToComplexRequest = utilTestService.createUploadToComplexRequest(ordineMissione,moduloOrdineMissione);
        else
            uploadToComplexRequest = autorizzazione.createUploadComplexrequest(ordineMissione, moduloOrdineMissione);
       return autorizzazione.send(uploadToComplexRequest);

    }

}
