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
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativaJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UnitaOrganizzativaService {

    @Autowired
    private CommonService commonService;

    public UnitaOrganizzativa loadUoBySiglaEnteInt(String siglaEnteInt,Integer anno) throws ComponentException {
        if (Optional.ofNullable(siglaEnteInt).isPresent()) {
            List<JSONClause> clauses = prepareJSONClause(siglaEnteInt, anno);
            String app = Costanti.APP_SIGLA;
            String url = Costanti.REST_UO;
            String risposta = commonService.process(clauses, app, url);
            try {
                ObjectMapper mapper = new ObjectMapper();
                UnitaOrganizzativaJson uoJson = mapper.readValue(risposta, UnitaOrganizzativaJson.class);
                if (uoJson != null) {
                    List<UnitaOrganizzativa> lista = uoJson.getElements();
                    if (lista != null && !lista.isEmpty()) {
                        return lista.get(0);
                    }
                }
            } catch (Exception ex) {
                throw new ComponentException("Errore nella lettura del file JSON per le Unità Organizzative (" + Utility.getMessageException(ex) + ").", ex);
            }
        }
        return null;
    }



    public UnitaOrganizzativa loadUo(String uo, String cds, Integer anno) throws ComponentException {
        if (uo != null) {
            List<JSONClause> clauses = prepareJSONClause(uo, cds, anno);
            String app = Costanti.APP_SIGLA;
            String url = Costanti.REST_UO;
            String risposta = commonService.process(clauses, app, url);
            try {
                ObjectMapper mapper = new ObjectMapper();
                UnitaOrganizzativaJson uoJson = mapper.readValue(risposta, UnitaOrganizzativaJson.class);
                if (uoJson != null) {
                    List<UnitaOrganizzativa> lista = uoJson.getElements();
                    if (lista != null && !lista.isEmpty()) {
                        return lista.get(0);
                    }
                }
            } catch (Exception ex) {
                throw new ComponentException("Errore nella lettura del file JSON per le Unità Organizzative (" + Utility.getMessageException(ex) + ").", ex);
            }
        }
        return null;
    }

    private List<JSONClause> prepareJSONClause(String siglaEnteInt,
                                               Integer anno) {
        JSONClause clause = new JSONClause();
        clause.setFieldName("sigla_int_ente");
        clause.setFieldValue(siglaEnteInt);
        clause.setCondition("AND");
        clause.setOperator("=");
        List<JSONClause> clauses = new ArrayList<JSONClause>();
        clauses.add(clause);


        clause = new JSONClause();
        clause.setFieldName("esercizio_fine");
        clause.setFieldValue(anno);
        clause.setCondition("AND");
        clause.setOperator(">=");
        clauses.add(clause);
        return clauses;
    }

    private List<JSONClause> prepareJSONClause(String uo, String cds,
                                               Integer anno) {
        JSONClause clause = new JSONClause();
        clause.setFieldName("cd_unita_organizzativa");
        clause.setFieldValue(uo);
        clause.setCondition("AND");
        clause.setOperator("=");
        List<JSONClause> clauses = new ArrayList<JSONClause>();
        clauses.add(clause);

        if (!StringUtils.isEmpty(cds)) {
            clause = new JSONClause();
            clause.setFieldName("cd_unita_padre");
            clause.setFieldValue(cds);
            clause.setCondition("AND");
            clause.setOperator("=");
            clauses.add(clause);
        }

        clause = new JSONClause();
        clause.setFieldName("esercizio_fine");
        clause.setFieldValue(anno);
        clause.setCondition("AND");
        clause.setOperator(">=");
        clauses.add(clause);
        return clauses;
    }

}
