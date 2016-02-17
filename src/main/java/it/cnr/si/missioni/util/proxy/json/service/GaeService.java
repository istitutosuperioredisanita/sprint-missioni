package it.cnr.si.missioni.util.proxy.json.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.Gae;
import it.cnr.si.missioni.util.proxy.json.object.GaeJson;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GaeService {

	@Autowired
    private CommonService commonService;

	public Gae loadGae(OrdineMissione ordineMissione) throws AwesomeException {
		if (ordineMissione.getGae() != null){
			List<JSONClause> clauses = prepareJSONClause(ordineMissione);
			List<JSONClause> clausesAdd = prepareJSONClauseToAdd(ordineMissione);
			String app = Costanti.APP_SIGLA;
			String url = Costanti.REST_GAE;
			String risposta = commonService.process(clauses, app, url, clausesAdd);

			try {
				ObjectMapper mapper = new ObjectMapper();
				GaeJson gaeJson = mapper.readValue(risposta, GaeJson.class);
				if (gaeJson != null){
					List<Gae> lista = gaeJson.getElements();
					if (lista != null && !lista.isEmpty()){
						return lista.get(0);
					}
				}
			} catch (Exception ex) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per le GAE ("+Utility.getMessageException(ex)+").");
			}
		}
		return null;
	}

	private List<JSONClause> prepareJSONClause(OrdineMissione ordineMissione) {
		JSONClause clause = new JSONClause();
		clause.setFieldName("cd_linea_attivita");
		clause.setFieldValue(ordineMissione.getGae());
		clause.setCondition("AND");
		clause.setOperator("=");
		List<JSONClause> clauses = new ArrayList<JSONClause>();
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("esercizio_inizio");
		clause.setFieldValue(ordineMissione.getAnno());
		clause.setCondition("AND");
		clause.setOperator("<=");
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("esercizio_fine");
		clause.setFieldValue(ordineMissione.getAnno());
		clause.setCondition("AND");
		clause.setOperator(">=");
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("ti_gestione");
		clause.setFieldValue("S");
		clause.setCondition("AND");
		clause.setOperator("=");
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("cd_centro_responsabilita");
		clause.setCondition("AND");
//			if (!StringUtils.isEmpty(ordineMissione.getCdrSpesa())){
			clause.setFieldValue(ordineMissione.getCdrSpesa());
			clause.setOperator("=");
//			} else {
//				clause.setFieldValue(ordineMissione.getUoSpesa()+"%");
//				clause.setOperator("like");
//			}
		clauses.add(clause);
		return clauses;
	}

	private List<JSONClause> prepareJSONClauseToAdd(OrdineMissione ordineMissione) {
		JSONClause clause = new JSONClause();
		List<JSONClause> clauses = new ArrayList<JSONClause>();
		clause.setFieldName("cd_centro_responsabilita");
		clause.setCondition("AND");
		clause.setFieldValue(ordineMissione.getCdsSpesa()+"%");
		clause.setOperator("LIKE");
		clauses.add(clause);
		return clauses;
	}
}
