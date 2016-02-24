package it.cnr.si.missioni.util.proxy.json.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.Impegno;
import it.cnr.si.missioni.util.proxy.json.object.ImpegnoJson;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ImpegnoService {
	@Autowired
    private CommonService commonService;

	public Impegno loadImpegno(OrdineMissione ordineMissione) throws AwesomeException {
		if (ordineMissione.getPgObbligazione() != null && ordineMissione.getEsercizioOriginaleObbligazione() != null){
			String app = Costanti.APP_SIGLA;
			String url = Costanti.REST_IMPEGNO;
			List<JSONClause> clauses = prepareJSONClause(ordineMissione);
			ObjectMapper mapper = new ObjectMapper();
			String risposta = commonService.process(clauses, app, url);

			try {
				ImpegnoJson impegnoJson = mapper.readValue(risposta, ImpegnoJson.class);
				if (impegnoJson != null){
					List<Impegno> lista = impegnoJson.getElements();
					if (lista != null && !lista.isEmpty()){
						return lista.get(0);
					}
				}
			} catch (Exception ex) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per l'impegno ("+Utility.getMessageException(ex)+").");
			}
		}
		return null;
	}

	private List<JSONClause> prepareJSONClause(OrdineMissione ordineMissione) {
		JSONClause clause = new JSONClause();
		clause.setFieldName("cdCds");
		clause.setFieldValue(ordineMissione.getCdsSpesa());
		clause.setCondition("AND");
		clause.setOperator("=");
		List<JSONClause> clauses = new ArrayList<JSONClause>();
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("esercizio");
		clause.setFieldValue(ordineMissione.getAnno());
		clause.setCondition("AND");
		clause.setOperator("=");
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("esercizioOriginale");
		clause.setFieldValue(ordineMissione.getEsercizioOriginaleObbligazione());
		clause.setCondition("AND");
		clause.setOperator("=");
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("pgObbligazione");
		clause.setFieldValue(ordineMissione.getPgObbligazione());
		clause.setCondition("AND");
		clause.setOperator("=");
		clauses.add(clause);
		return clauses;
	}

}
