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
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.Nazione;
import it.cnr.si.missioni.util.proxy.json.object.NazioneJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NazioneService {
    @Autowired
    private CommonService commonService;

    public Nazione loadNazione(OrdineMissione ordineMissione) throws AwesomeException {
        return loadNazione(ordineMissione.getNazione());
    }

    public Nazione loadNazione(Long nazione) throws AwesomeException {
        if (nazione != null) {
            List<JSONClause> clauses = prepareJSONClause(nazione);
            String app = Costanti.APP_SIGLA;
            String url = Costanti.REST_NAZIONE;
            String risposta = commonService.process(clauses, app, url);

            try {
                ObjectMapper mapper = new ObjectMapper();
                NazioneJson nazioneJson = mapper.readValue(risposta, NazioneJson.class);
                if (nazioneJson != null) {
                    List<Nazione> lista = nazioneJson.getElements();
                    if (lista != null && !lista.isEmpty()) {
                        return lista.get(0);
                    }
                }
            } catch (Exception ex) {
                throw new AwesomeException("Errore nella lettura del file JSON per le nazioni (" + Utility.getMessageException(ex) + ").");
            }
        }
        return null;
    }

    private List<JSONClause> prepareJSONClause(Long nazione) {
        JSONClause clause = new JSONClause();
        clause.setFieldName("pg_nazione");
        clause.setFieldValue(nazione);
        clause.setCondition("AND");
        clause.setOperator("=");
        List<JSONClause> clauses = new ArrayList<JSONClause>();
        clauses.add(clause);
        return clauses;
    }

}
