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

package it.cnr.si.missioni.util.proxy.json.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.object.Terzo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidaDettaglioRimborsoService {
    @Autowired
    private CommonService commonService;

    public Terzo valida(RimborsoMissioneDettagli dettaglio) throws AwesomeException {
        if (dettaglio != null && dettaglio.getRimborsoMissione() != null && dettaglio.getRimborsoMissione().getAnno() != null) {
            JSONBody body = prepareJSONBody(dettaglio);

            String app = Costanti.APP_SIGLA;
            String url = Costanti.REST_VALIDA_MASSIMALE_SPESA;
            String risposta = commonService.processWithContextHeader(body, app, url);

        }
        return null;
    }

    public JSONBody prepareJSONBody(RimborsoMissioneDettagli dettaglio) {
        JSONBody clause = new JSONBody();
        if (dettaglio.getKmPercorsi() != null) {
            clause.setKm(dettaglio.getKmPercorsi().toString());
        }
        clause.setData(DateUtils.getDateAsString(dettaglio.getDataSpesa(), "yyyy-MM-dd"));
        clause.setNazione(dettaglio.getRimborsoMissione().getNazione());
        clause.setInquadramento(dettaglio.getRimborsoMissione().getInquadramento());
        clause.setDivisa(dettaglio.getCdDivisa());
        clause.setImportoSpesa(dettaglio.getImportoEuro().toString());
        clause.setCdTipoSpesa(dettaglio.getCdTiSpesa());
        if (Utility.nvl(dettaglio.getTiCdTiSpesa()).equals("P") && dettaglio.getCdTiPasto() == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "E' necessario indicare il tipo pasto.");
        }
        if (dettaglio.getCdTiPasto() != null) {
            clause.setCdTipoPasto(dettaglio.getCdTiPasto());
        }
        return clause;
    }

}
