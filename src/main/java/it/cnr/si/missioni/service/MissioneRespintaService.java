/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

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
    public void inserisciMissioneRespinta(FlowResult flowResult) {
        MissioneRespinta missioneRespinta = new MissioneRespinta();
        missioneRespinta.setIdMissione(Long.valueOf(flowResult.getIdMissione()));
        missioneRespinta.setDataInserimento(new Timestamp(new Date().getTime()));
        missioneRespinta.setTipoFaseRespingi(FlowResult.STATO_FLUSSO_SCRIVANIA_MISSIONI.get(flowResult.getStato()));
        missioneRespinta.setMotivoRespingi(flowResult.getCommento());
        missioneRespinta.setUidInsert(flowResult.getUser());
        missioneRespinta.setTipoOperazioneMissione(FlowResult.TIPO_FLUSSO_MISSIONE.get(flowResult.getTipologiaMissione()));
        missioneRespinta.setToBeCreated();
        crudServiceBean.creaConBulk(missioneRespinta);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void inserisciOrdineMissioneRespinto(OrdineMissione ordineMissione, String tipoFaseRespingi) {
        MissioneRespinta missioneRespinta = new MissioneRespinta();
        missioneRespinta.setIdMissione(Long.valueOf(ordineMissione.getId().toString()));
        missioneRespinta.setDataInserimento(new Timestamp(new Date().getTime()));
        missioneRespinta.setTipoFaseRespingi(tipoFaseRespingi);
        missioneRespinta.setMotivoRespingi(ordineMissione.getNoteRespingi());
        missioneRespinta.setUidInsert(securityService.getCurrentUserLogin());
        missioneRespinta.setTipoOperazioneMissione(MissioneRespinta.OPERAZIONE_MISSIONE_ORDINE);
        missioneRespinta.setToBeCreated();
        crudServiceBean.creaConBulk(missioneRespinta);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void inserisciRimborsoMissioneRespinto(RimborsoMissione rimborsoMissione) {
        MissioneRespinta missioneRespinta = new MissioneRespinta();
        missioneRespinta.setIdMissione(Long.valueOf(rimborsoMissione.getId().toString()));
        missioneRespinta.setDataInserimento(new Timestamp(new Date().getTime()));
        missioneRespinta.setTipoFaseRespingi(MissioneRespinta.FASE_RESPINGI_AMMINISTRATIVI);
        missioneRespinta.setMotivoRespingi(rimborsoMissione.getNoteRespingi());
        missioneRespinta.setUidInsert(securityService.getCurrentUserLogin());
        missioneRespinta.setTipoOperazioneMissione(MissioneRespinta.OPERAZIONE_MISSIONE_RIMBORSO);
        missioneRespinta.setToBeCreated();
        crudServiceBean.creaConBulk(missioneRespinta);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<MissioneRespinta> getCronologiaRespingimentiMissione(String tipoMissione, Long idMissione) {
        return missioneRespintaRepository.getRespingimenti(idMissione, tipoMissione);
    }
}
