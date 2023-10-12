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
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.Terzo;
import it.cnr.si.missioni.util.proxy.json.object.TerzoInfo;
import it.cnr.si.missioni.util.proxy.json.object.TerzoJson;
import it.cnr.si.service.dto.anagrafica.UserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TerzoService {
    @Autowired
    private CommonService commonService;

    public TerzoInfo loadUserInfo(String cf) {
        String app = Costanti.APP_SIGLA;
        String url = Costanti.REST_USERINFO_SIGLA + cf;
        JSONBody body = new JSONBody();
        try {
            String risposta = commonService.process(body, app, url, false, HttpMethod.GET);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(risposta, TerzoInfo.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public Terzo loadTerzo(String cf, String cdTerzo) throws AwesomeException {
        if (cf != null || cdTerzo != null) {
            List<JSONClause> clauses = prepareJSONClause(cf, cdTerzo);

            String app = Costanti.APP_SIGLA;
            String url = Costanti.REST_TERZO;
            String risposta = commonService.process(clauses, app, url);

            try {
                ObjectMapper mapper = new ObjectMapper();
                TerzoJson terzoJson = mapper.readValue(risposta, TerzoJson.class);
                if (terzoJson != null) {
                    List<Terzo> lista = terzoJson.getElements();
                    if (lista != null && !lista.isEmpty()) {
                        return lista.get(0);
                    }
                }
            } catch (Exception ex) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per i terzi (" + Utility.getMessageException(ex) + ").");
            }
        }
        return null;
    }

    public List<JSONClause> prepareJSONClause(String cf, String cdTerzo) {
        JSONClause clause = new JSONClause();
        List<JSONClause> clauses = new ArrayList<JSONClause>();
        if (cf != null) {
            clause.setFieldName("anagrafico.codice_fiscale");
            clause.setFieldValue(cf);
            clause.setCondition("AND");
            clause.setOperator("=");
            clauses.add(clause);
        }
        if (cdTerzo != null) {
            clause.setFieldName("cd_terzo");
            clause.setFieldValue(cf);
            clause.setCondition("AND");
            clause.setOperator("=");
            clauses.add(clause);
        }
        return clauses;
    }
}
