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

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.web.filter.MissioneFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
public class StepService {

    @Autowired
    private OrdineMissioneService ordineMissioneService;

    @Autowired
    private RimborsoMissioneService rimborsoMissioneService;

    @Autowired
    private CRUDComponentSession crudServiceBean;

    public void verifyStep() {
        MissioneFilter filtro = new MissioneFilter();
        filtro.setStato(Costanti.STATO_CONFERMATO);
        filtro.setDaCron("S");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void verifyStepAmministrativoNewTransaction(Serializable idOrdineMissione) throws Exception {
        verifyStepAmministrativo(idOrdineMissione);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void verifyStepRespGruppoNewTransaction(Serializable idOrdineMissione) throws Exception {
        verifyStepRespGruppo(idOrdineMissione);
    }


    public void verifyStepAmministrativo(Serializable idOrdineMissione)
            throws Exception {
        if (idOrdineMissione != null) {
            OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, idOrdineMissione);
            ordineMissioneService.verifyStepAmministrativo(ordineMissione);
        }
    }

    public void verifyStepRespGruppo(Serializable idOrdineMissione)
            throws Exception {
        if (idOrdineMissione != null) {
            OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, idOrdineMissione);
            ordineMissioneService.verifyStepRespGruppo(ordineMissione);
        }
    }

}
