package it.cnr.si.missioni.cmis.flows.happySign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutorizzazioneService {
    private final List<AutorizzazioneMissione> autorizzazioneMissione;
    @Autowired
    public AutorizzazioneService(List<AutorizzazioneMissione> autorizzazioneMissione) {
        this.autorizzazioneMissione = autorizzazioneMissione;
    }

}
