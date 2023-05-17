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
import it.cnr.si.missioni.util.proxy.json.object.Impegno;
import it.cnr.si.missioni.util.proxy.json.object.ImpegnoJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImpegnoService {
    @Autowired
    private CommonService commonService;

    public Impegno loadImpegno(OrdineMissione ordineMissione) throws AwesomeException {
        return loadImpegno(ordineMissione.getCdsSpesa(), ordineMissione.getUoSpesa(), ordineMissione.getEsercizioOriginaleObbligazione(), ordineMissione.getPgObbligazione());

    }

    public Impegno loadImpegno(RimborsoMissione rimborsoMissione) throws AwesomeException {
        return loadImpegno(rimborsoMissione.getCdsSpesa(), rimborsoMissione.getUoSpesa(), rimborsoMissione.getEsercizioOriginaleObbligazione(), rimborsoMissione.getPgObbligazione());
    }

    public Impegno loadImpegno(String cds, String unitaOrganizzativa, Integer esercizio, Long pgObbligazione) throws AwesomeException {
        if (pgObbligazione != null && esercizio != null) {
            LocalDate data = LocalDate.now();
            int anno = data.getYear();

            List<JSONClause> clauses = prepareJSONClause(cds, unitaOrganizzativa, anno, esercizio, pgObbligazione);
            return loadImpegno(clauses);
        }
        return null;
    }

    private Impegno loadImpegno(List<JSONClause> clauses) {
        String app = Costanti.APP_SIGLA;
        String url = Costanti.REST_IMPEGNO;
        ObjectMapper mapper = new ObjectMapper();
        String risposta = commonService.process(clauses, app, url);

        try {
            ImpegnoJson impegnoJson = mapper.readValue(risposta, ImpegnoJson.class);
            if (impegnoJson != null) {
                List<Impegno> lista = impegnoJson.getElements();
                if (lista != null && !lista.isEmpty()) {
                    return lista.get(0);
                }
            }
        } catch (Exception ex) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per l'impegno (" + Utility.getMessageException(ex) + ").");
        }
        return null;
    }

    private List<JSONClause> prepareJSONClause(String cdsSpesa, String cdUnitaOrganizzativa, Integer anno, Integer esercizioOriginaleObbligazione, Long pgObbligazione) {
        JSONClause clause = new JSONClause();
        clause.setFieldName("cdCds");
        clause.setFieldValue(cdsSpesa);
        clause.setCondition("AND");
        clause.setOperator("=");
        List<JSONClause> clauses = new ArrayList<JSONClause>();
        clauses.add(clause);
        clause = new JSONClause();
        clause.setFieldName("cdUnitaOrganizzativa");
        clause.setFieldValue(cdUnitaOrganizzativa);
        clause.setCondition("AND");
        clause.setOperator("=");
        clauses.add(clause);
        clause = new JSONClause();
        clause.setFieldName("esercizioOriginale");
        clause.setFieldValue(esercizioOriginaleObbligazione);
        clause.setCondition("AND");
        clause.setOperator("=");
        clauses.add(clause);
        clause = new JSONClause();
        clause.setFieldName("pgObbligazione");
        clause.setFieldValue(pgObbligazione);
        clause.setCondition("AND");
        clause.setOperator("=");
        clauses.add(clause);
        return clauses;
    }

}
