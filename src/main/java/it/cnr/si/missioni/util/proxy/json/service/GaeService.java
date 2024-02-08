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
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.Gae;
import it.cnr.si.missioni.util.proxy.json.object.GaeJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GaeService {

    @Autowired
    private CommonService commonService;

    public Gae loadGae(RimborsoMissione rimborsoMissione) throws AwesomeException {
        LocalDate data = LocalDate.now();
        int anno = data.getYear();

        if (rimborsoMissione.getGae() != null) {
            List<JSONClause> clauses = prepareJSONClause(rimborsoMissione.getGae(), anno, rimborsoMissione.getCdrSpesa());
            List<JSONClause> clausesAdd = prepareJSONClauseToAdd(rimborsoMissione.getCdsSpesa());
            return loadGae(clauses, clausesAdd);
        }
        return null;
    }

    public Gae loadGae(OrdineMissione ordineMissione) throws AwesomeException {
        if (ordineMissione.getGae() != null) {
            List<JSONClause> clauses = prepareJSONClause(ordineMissione.getGae(), ordineMissione.getAnno(), ordineMissione.getCdrSpesa());
            List<JSONClause> clausesAdd = prepareJSONClauseToAdd(ordineMissione.getCdsSpesa());
            return loadGae(clauses, clausesAdd);
        }
        return null;
    }

    private Gae loadGae(List<JSONClause> clauses, List<JSONClause> clausesAdd) throws AwesomeException {
        String app = Costanti.APP_SIGLA;
        String url = Costanti.REST_GAE;
        String risposta = commonService.process(clauses, app, url, clausesAdd);

        try {
            ObjectMapper mapper = new ObjectMapper();
            GaeJson gaeJson = mapper.readValue(risposta, GaeJson.class);
            if (gaeJson != null) {
                List<Gae> lista = gaeJson.getElements();
                if (lista != null && !lista.isEmpty()) {
                    return lista.get(0);
                }
            }
        } catch (Exception ex) {
            throw new ComponentException("Errore nella lettura del file JSON per le GAE (" + Utility.getMessageException(ex) + ").", ex);
        }
        return null;
    }

    private List<JSONClause> prepareJSONClause(String gae, Integer anno, String cdrSpesa) {
        JSONClause clause = new JSONClause();
        clause.setFieldName("cd_linea_attivita");
        clause.setFieldValue(gae);
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
        clause.setFieldName("cd_centro_responsabilita");
        clause.setCondition("AND");
//			if (!StringUtils.isEmpty(ordineMissione.getCdrSpesa())){
        clause.setFieldValue(cdrSpesa);
        clause.setOperator("=");
//			} else {
//				clause.setFieldValue(ordineMissione.getUoSpesa()+"%");
//				clause.setOperator("like");
//			}
        clauses.add(clause);
        return clauses;
    }

    private List<JSONClause> prepareJSONClauseToAdd(String cdsSpesa) {
        JSONClause clause = new JSONClause();
        List<JSONClause> clauses = new ArrayList<JSONClause>();
        clause.setFieldName("cd_centro_responsabilita");
        clause.setCondition("AND");
        clause.setFieldValue(cdsSpesa + "%");
        clause.setOperator("LIKE");
        clauses.add(clause);
        return clauses;
    }
}
