package it.cnr.si.missioni.service;

import it.cnr.si.missioni.util.proxy.json.object.rimborso.MissioneBulk;
import it.cnr.si.missioni.util.proxy.json.service.ComunicaRimborsoSiglaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.security.Principal;

@Service
public class ComunicaMissioneSiglaService {
    @Autowired
    ComunicaRimborsoSiglaService comunicaRimborsoSiglaService;

    public MissioneBulk comunicaRimborsoSigla(Principal principal, Serializable rimborsoApprovatoId) {
        return comunicaRimborsoSiglaService.comunicaRimborsoSigla(principal, rimborsoApprovatoId);
    }
}
