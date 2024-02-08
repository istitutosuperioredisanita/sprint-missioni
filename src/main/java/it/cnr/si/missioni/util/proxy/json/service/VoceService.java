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

import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.Voce;
import it.cnr.si.missioni.util.proxy.json.object.VoceJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VoceService {
    @Autowired
    private CommonService commonService;

    public Voce loadVoce(Integer anno, String voce) throws AwesomeException {
        if (voce != null) {
            List<JSONClause> clauses = prepareJSONClause(anno, voce);

            String app = Costanti.APP_SIGLA;
            String url = Costanti.REST_VOCE;
            String risposta = commonService.process(clauses, app, url);

            try {
                ObjectMapper mapper = new ObjectMapper();
                VoceJson voceJson = mapper.readValue(risposta, VoceJson.class);
                if (voceJson != null) {
                    List<Voce> lista = voceJson.getElements();
                    if (lista != null && !lista.isEmpty()) {
                        return lista.get(0);
                    }
                }
            } catch (Exception ex) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per le voci (" + Utility.getMessageException(ex) + ").");
            }

        }
        return null;
    }

    public Voce loadVoce(OrdineMissione ordineMissione) throws AwesomeException {
        return loadVoce(ordineMissione.getAnno(), ordineMissione.getVoce());
    }

    public Voce loadVoce(RimborsoMissione rimborsoMissione) throws AwesomeException {
        LocalDate data = LocalDate.now();
        int anno = data.getYear();

        return loadVoce(anno, rimborsoMissione.getVoce());
    }

    public List<JSONClause> prepareJSONClause(OrdineMissione ordineMissione) {
        return prepareJSONClause(ordineMissione.getAnno(), ordineMissione.getVoce());
    }

    public List<JSONClause> prepareJSONClause(RimborsoMissione rimborsoMissione) {
        LocalDate data = LocalDate.now();
        int anno = data.getYear();

        return prepareJSONClause(anno, rimborsoMissione.getVoce());
    }

    public List<JSONClause> prepareJSONClause(Integer anno, String cdVoce) {
        JSONClause clause = new JSONClause();
        clause.setFieldName("cd_elemento_voce");
        clause.setFieldValue(cdVoce);
        clause.setCondition("AND");
        clause.setOperator("=");
        List<JSONClause> clauses = new ArrayList<JSONClause>();
        clauses.add(clause);
        clause = new JSONClause();
        clause.setFieldName("esercizio");
        clause.setFieldValue(anno);
        clause.setCondition("AND");
        clause.setOperator("=");
        clauses.add(clause);
        clause = new JSONClause();
        clause.setFieldName("ti_gestione");
        clause.setFieldValue("E");
        clause.setCondition("AND");
        clause.setOperator("!=");
        clauses.add(clause);
        clause = new JSONClause();
        clause.setFieldName("ti_elemento_voce");
        clause.setFieldValue("C");
        clause.setCondition("AND");
        clause.setOperator("=");
        clauses.add(clause);
        clause = new JSONClause();
        clause.setFieldName("fl_solo_residuo");
        clause.setFieldValue(false);
        clause.setCondition("AND");
        clause.setOperator("=");
        clauses.add(clause);
        clause = new JSONClause();
        clause.setFieldName("ti_appartenenza");
        clause.setFieldValue("D");
        clause.setCondition("AND");
        clause.setOperator("=");
        clauses.add(clause);
        return clauses;
    }

}
