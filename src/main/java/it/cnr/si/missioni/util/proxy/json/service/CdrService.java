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
import it.cnr.si.missioni.util.proxy.json.object.Cdr;
import it.cnr.si.missioni.util.proxy.json.object.CdrJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CdrService {
    @Autowired
    private CommonService commonService;

    public Cdr loadCdr(String cdCdr, String cdUo) throws AwesomeException {
        if (cdCdr != null) {
            List<JSONClause> clauses = prepareJSONClause(cdCdr, cdUo);
            String app = Costanti.APP_SIGLA;
            String url = Costanti.REST_CDR;
            String risposta = commonService.process(clauses, app, url);
            try {
                ObjectMapper mapper = new ObjectMapper();
                CdrJson cdrJson = mapper.readValue(risposta, CdrJson.class);
                if (cdrJson != null) {
                    List<Cdr> lista = cdrJson.getElements();
                    if (lista != null && !lista.isEmpty()) {
                        return lista.get(0);
                    }
                }
            } catch (Exception ex) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per i CDR (" + Utility.getMessageException(ex) + ").");
            }
        }
        return null;
    }

    private List<JSONClause> prepareJSONClause(String cdCdr, String cdUo) {
        JSONClause clause = new JSONClause();
        clause.setFieldName("cd_centro_responsabilita");
        clause.setFieldValue(cdCdr);
        clause.setCondition("AND");
        clause.setOperator("=");
        List<JSONClause> clauses = new ArrayList<JSONClause>();
        clauses.add(clause);

        if (!StringUtils.isEmpty(cdUo)) {
            clause = new JSONClause();
            clause.setFieldName("cd_unita_organizzativa");
            clause.setFieldValue(cdUo);
            clause.setCondition("AND");
            clause.setOperator("=");
            clauses.add(clause);
        }
        return clauses;
    }
}
