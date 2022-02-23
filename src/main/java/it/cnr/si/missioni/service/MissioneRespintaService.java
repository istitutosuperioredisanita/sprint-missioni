package it.cnr.si.missioni.service;

import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.domain.custom.persistence.MissioneRespinta;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.MissioneRespintaRepository;
import it.cnr.si.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Service
public class MissioneRespintaService {

    @Autowired
    private CRUDComponentSession crudServiceBean;

    @Autowired
    private MissioneRespintaRepository missioneRespintaRepository;

    @Autowired
    private SecurityService securityService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void inserisciMissioneRespinta(FlowResult flowResult){
        MissioneRespinta missioneRespinta = new MissioneRespinta();
        missioneRespinta.setIdMissione(new Long(flowResult.getIdMissione()));
        missioneRespinta.setDataInserimento(new Timestamp(new Date().getTime()));
        missioneRespinta.setTipoFaseRespingi(FlowResult.STATO_FLUSSO_SCRIVANIA_MISSIONI.get(flowResult.getStato()));
        missioneRespinta.setMotivoRespingi(flowResult.getCommento());
        missioneRespinta.setUidInsert(flowResult.getUser());
        missioneRespinta.setTipoOperazioneMissione(FlowResult.TIPO_FLUSSO_MISSIONE.get(flowResult.getTipologiaMissione()));
        missioneRespinta.setToBeCreated();
        crudServiceBean.creaConBulk(missioneRespinta);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void inserisciOrdineMissioneRespinto(OrdineMissione ordineMissione, String tipoFaseRespingi){
        MissioneRespinta missioneRespinta = new MissioneRespinta();
        missioneRespinta.setIdMissione(new Long(ordineMissione.getId().toString()));
        missioneRespinta.setDataInserimento(new Timestamp(new Date().getTime()));
        missioneRespinta.setTipoFaseRespingi(tipoFaseRespingi);
        missioneRespinta.setMotivoRespingi(ordineMissione.getNoteRespingi());
        missioneRespinta.setUidInsert(securityService.getCurrentUserLogin());
        missioneRespinta.setTipoOperazioneMissione(MissioneRespinta.OPERAZIONE_MISSIONE_ORDINE);
        missioneRespinta.setToBeCreated();
        crudServiceBean.creaConBulk(missioneRespinta);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void inserisciRimborsoMissioneRespinto(RimborsoMissione rimborsoMissione){
        MissioneRespinta missioneRespinta = new MissioneRespinta();
        missioneRespinta.setIdMissione(new Long(rimborsoMissione.getId().toString()));
        missioneRespinta.setDataInserimento(new Timestamp(new Date().getTime()));
        missioneRespinta.setTipoFaseRespingi(MissioneRespinta.FASE_RESPINGI_AMMINISTRATIVI);
        missioneRespinta.setMotivoRespingi(rimborsoMissione.getNoteRespingi());
        missioneRespinta.setUidInsert(securityService.getCurrentUserLogin());
        missioneRespinta.setTipoOperazioneMissione(MissioneRespinta.OPERAZIONE_MISSIONE_RIMBORSO);
        missioneRespinta.setToBeCreated();
        crudServiceBean.creaConBulk(missioneRespinta);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<MissioneRespinta> getCronologiaRespingimentiMissione(String tipoMissione, Long idMissione){
        return missioneRespintaRepository.getRespingimenti(idMissione, tipoMissione);
    }
}
