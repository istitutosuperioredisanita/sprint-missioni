package it.cnr.si.missioni.util.proxy.json.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.Terzo;
import it.cnr.si.missioni.util.proxy.json.object.TerzoJson;

import java.util.ArrayList;
import java.util.List;

import it.cnr.si.service.dto.anagrafica.UserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TerzoService {
	@Autowired
    private CommonService commonService;

	public UserInfoDto loadUserInfo(String cf) {
		String app = Costanti.APP_SIGLA;
		String url = Costanti.REST_USERINFO_SIGLA + cf;
		JSONBody body = new JSONBody();
		try {
			String risposta = commonService.process(body, app, url, false, HttpMethod.GET);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(risposta, UserInfoDto.class);
		} catch (Exception ex) {
			return null;
		}
	}

	public Terzo loadTerzo(String cf, String cdTerzo) throws AwesomeException {
		if (cf != null || cdTerzo != null){
			List<JSONClause> clauses = prepareJSONClause(cf, cdTerzo);

			String app = Costanti.APP_SIGLA;
			String url = Costanti.REST_TERZO;
			String risposta = commonService.process(clauses, app, url);

			try {
				ObjectMapper mapper = new ObjectMapper();
				TerzoJson terzoJson = mapper.readValue(risposta, TerzoJson.class);
				if (terzoJson != null){
					List<Terzo> lista = terzoJson.getElements();
					if (lista != null && !lista.isEmpty()){
						return lista.get(0);
					}
				}
			} catch (Exception ex) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per i terzi ("+Utility.getMessageException(ex)+").");
			}
		}
		return null;
	}

	public List<JSONClause> prepareJSONClause(String cf, String cdTerzo) {
		JSONClause clause = new JSONClause();
		List<JSONClause> clauses = new ArrayList<JSONClause>();
		if (cf != null){
			clause.setFieldName("anagrafico.codice_fiscale");
			clause.setFieldValue(cf);
			clause.setCondition("AND");
			clause.setOperator("=");
			clauses.add(clause);
		}
		if (cdTerzo != null){
			clause.setFieldName("cd_terzo");
			clause.setFieldValue(cf);
			clause.setCondition("AND");
			clause.setOperator("=");
			clauses.add(clause);
		}
		return clauses;
	}
}
