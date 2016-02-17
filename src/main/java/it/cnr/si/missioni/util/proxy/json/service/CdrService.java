package it.cnr.si.missioni.util.proxy.json.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.service.ProxyService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.ResultProxy;
import it.cnr.si.missioni.util.proxy.cache.CallCache;
import it.cnr.si.missioni.util.proxy.cache.json.RestService;
import it.cnr.si.missioni.util.proxy.cache.service.CacheService;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.Cdr;
import it.cnr.si.missioni.util.proxy.json.object.CdrJson;
import it.cnr.si.missioni.util.proxy.json.object.CommonJsonRest;
import it.cnr.si.missioni.util.proxy.json.object.RestServiceBean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CdrService {
	@Autowired
    private CommonService commonService;

	public Cdr loadCdr(String cdCdr, String cdUo) throws AwesomeException {
		if (cdCdr != null){
			List<JSONClause> clauses = prepareJSONClause(cdCdr, cdUo);
			String app = Costanti.APP_SIGLA;
			String url = Costanti.REST_CDR;
			String risposta = commonService.process(clauses, app, url);
			try {
				ObjectMapper mapper = new ObjectMapper();
				CdrJson cdrJson = mapper.readValue(risposta, CdrJson.class);
				if (cdrJson != null){
					List<Cdr> lista = cdrJson.getElements();
					if (lista != null && !lista.isEmpty()){
						return lista.get(0);
					}
				}
			} catch (Exception ex) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per i CDR ("+Utility.getMessageException(ex)+").");
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
		
		if (!StringUtils.isEmpty(cdUo)){
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
