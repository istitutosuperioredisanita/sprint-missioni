package it.cnr.si.missioni.util.proxy.json.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.Inquadramento;
import it.cnr.si.missioni.util.proxy.json.object.InquadramentoJson;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class InquadramentoService {
	@Autowired
    private CommonService commonService;

	public List<Inquadramento> loadInquadramento(Integer cdAnag) throws AwesomeException {
		if (cdAnag != null){
			List<JSONClause> clauses = prepareJSONClause(cdAnag);

			String app = Costanti.APP_SIGLA;
			String url = Costanti.REST_INQUADRAMENTI;
			String risposta = commonService.process(clauses, app, url);

			try {
				ObjectMapper mapper = new ObjectMapper();
				InquadramentoJson inquadramentoJson = mapper.readValue(risposta, InquadramentoJson.class);
				if (inquadramentoJson != null){
					List<Inquadramento> lista = inquadramentoJson.getElements();
					return lista;
				}
			} catch (Exception ex) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per gli inquadramenti ("+Utility.getMessageException(ex)+").");
			}
		}
		return null;
	}

	public List<JSONClause> prepareJSONClause(Integer cdAnag) {
		JSONClause clause = new JSONClause();
		List<JSONClause> clauses = new ArrayList<JSONClause>();
		if (cdAnag != null){
			clause.setFieldName("cd_anag");
			clause.setFieldValue(cdAnag);
			clause.setCondition("AND");
			clause.setOperator("=");
			clauses.add(clause);
		}
		return clauses;
	}
}
