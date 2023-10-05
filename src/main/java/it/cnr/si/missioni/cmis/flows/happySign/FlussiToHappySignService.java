package it.cnr.si.missioni.cmis.flows.happySign;

import com.sun.org.apache.xpath.internal.operations.Bool;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.proxy.json.object.Progetto;
import it.cnr.si.missioni.util.proxy.json.service.ProgettoService;
import it.iss.si.service.HappySign;
import it.iss.si.service.HappySignService;
import org.springframework.beans.factory.annotation.Autowired;

public class FlussiToHappySignService implements FlussiToHappySign {
    @Autowired
    protected HappySignService happySignService;

    @Autowired
    ProgettoService progettoService;


    protected Boolean signRespProgetto(OrdineMissione ordineMissione) {
        if ( ordineMissione==null)
            return Boolean.FALSE;
        if(ordineMissione.getPgProgetto()==null)
            return Boolean.FALSE;
        if(ordineMissione.getPgProgetto()<=0)
            return Boolean.FALSE;

        Integer anno = DateUtils.getCurrentYear();
        Progetto progetto = progettoService.loadModulo(ordineMissione.getPgProgetto(), anno, ordineMissione.getUoSpesa());
        if (progetto.getCd_responsabile_terzo() != null)
                return Boolean.TRUE;

        return Boolean.FALSE;
    }
    protected Boolean signResoUoAfferente(OrdineMissione ordineMissione) {
        if ( ordineMissione==null)
            return Boolean.FALSE;
        if ( !ordineMissione.getUoRich().equalsIgnoreCase(ordineMissione.getUoSpesa()))
            return Boolean.FALSE;
        return Boolean.FALSE;
    }
}
