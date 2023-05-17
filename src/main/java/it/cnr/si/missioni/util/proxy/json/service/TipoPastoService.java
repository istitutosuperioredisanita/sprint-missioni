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
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.TipoPasto;
import it.cnr.si.missioni.util.proxy.json.object.TipoPastoJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TipoPastoService {
    @Autowired
    private CommonService commonService;

    public List<TipoPasto> loadTipoPasto(String cdTipoPasto, Long nazione, Long inquadramento, LocalDate data) throws AwesomeException {
        List<JSONClause> clauses = prepareJSONClause(cdTipoPasto, nazione, inquadramento, data);

        String app = Costanti.APP_SIGLA;
        String url = Costanti.REST_TIPO_PASTO;
        String risposta = commonService.process(clauses, app, url);

        try {
            ObjectMapper mapper = new ObjectMapper();
            TipoPastoJson tipoPastoJson = mapper.readValue(risposta, TipoPastoJson.class);
            if (tipoPastoJson != null) {
                List<TipoPasto> lista = tipoPastoJson.getElements();
                return lista;
            }
        } catch (Exception ex) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per gli inquadramenti (" + Utility.getMessageException(ex) + ").");
        }
        return null;
    }

    public List<JSONClause> prepareJSONClause(String cdTipoPasto, Long nazione, Long inquadramento, LocalDate data) {
        JSONClause clause = new JSONClause();
        List<JSONClause> clauses = new ArrayList<JSONClause>();
        if (cdTipoPasto != null) {
            clause.setFieldName("cd_tipo_pasto");
            clause.setFieldValue(cdTipoPasto);
            clause.setCondition("AND");
            clause.setOperator("=");
            clauses.add(clause);
        }
        if (nazione != null) {
            clause.setFieldName("nazione");
            clause.setFieldValue(nazione);
            clause.setCondition("AND");
            clause.setOperator("=");
            clauses.add(clause);
        }
        if (inquadramento != null) {
            clause.setFieldName("inquadramento");
            clause.setFieldValue(inquadramento);
            clause.setCondition("AND");
            clause.setOperator("=");
            clauses.add(clause);
        }
        if (data != null) {
            clause.setFieldName("data");
            clause.setFieldValue(data);
            clause.setCondition("AND");
            clause.setOperator("=");
            clauses.add(clause);
        }
        clause.setFieldName("condizioneTipiPastoMissione");
        clause.setFieldValue("S");
        clause.setCondition("AND");
        clause.setOperator("=");
        clauses.add(clause);
        return clauses;
    }
}
