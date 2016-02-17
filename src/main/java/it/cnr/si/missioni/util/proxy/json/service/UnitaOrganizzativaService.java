package it.cnr.si.missioni.util.proxy.json.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativaJson;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UnitaOrganizzativaService {
	
	@Autowired
    private CommonService commonService;

	public UnitaOrganizzativa loadUo(String uo, String cds, Integer anno) throws AwesomeException {
		if (uo != null){
			List<JSONClause> clauses = prepareJSONClause(uo, cds, anno);
			String app = Costanti.APP_SIGLA;
			String url = Costanti.REST_UO;
			String risposta = commonService.process(clauses, app, url);
			try {
				ObjectMapper mapper = new ObjectMapper();
				UnitaOrganizzativaJson uoJson = mapper.readValue(risposta, UnitaOrganizzativaJson.class);
				if (uoJson != null){
					List<UnitaOrganizzativa> lista = uoJson.getElements();
					if (lista != null && !lista.isEmpty()){
						return lista.get(0);
					}
				}
			} catch (Exception ex) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per le Unità Organizzative ("+Utility.getMessageException(ex)+").");
			}
		}
		return null;
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
		
		if (!StringUtils.isEmpty(cds)){
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
