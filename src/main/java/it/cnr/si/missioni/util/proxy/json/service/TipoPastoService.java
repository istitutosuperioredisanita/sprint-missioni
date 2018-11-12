package it.cnr.si.missioni.util.proxy.json.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.TipoPasto;
import it.cnr.si.missioni.util.proxy.json.object.TipoPastoJson;

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
				if (tipoPastoJson != null){
					List<TipoPasto> lista = tipoPastoJson.getElements();
					return lista;
				}
			} catch (Exception ex) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per gli inquadramenti ("+Utility.getMessageException(ex)+").");
			}
		return null;
	}

	public List<JSONClause> prepareJSONClause(String cdTipoPasto, Long nazione, Long inquadramento, LocalDate data) {
		JSONClause clause = new JSONClause();
		List<JSONClause> clauses = new ArrayList<JSONClause>();
		if (cdTipoPasto != null){
			clause.setFieldName("cd_tipo_pasto");
			clause.setFieldValue(cdTipoPasto);
			clause.setCondition("AND");
			clause.setOperator("=");
			clauses.add(clause);
		}
		if (nazione != null){
			clause.setFieldName("nazione");
			clause.setFieldValue(nazione);
			clause.setCondition("AND");
			clause.setOperator("=");
			clauses.add(clause);
		}
		if (inquadramento != null){
			clause.setFieldName("inquadramento");
			clause.setFieldValue(inquadramento);
			clause.setCondition("AND");
			clause.setOperator("=");
			clauses.add(clause);
		}
		if (data != null){
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
