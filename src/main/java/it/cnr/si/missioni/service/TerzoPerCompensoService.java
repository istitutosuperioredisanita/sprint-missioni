package it.cnr.si.missioni.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.ResultProxy;
import it.cnr.si.missioni.util.proxy.cache.service.CacheService;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.JSONOrderBy;
import it.cnr.si.missioni.util.proxy.json.object.TerzoPerCompensoJson;

@Service
public class TerzoPerCompensoService {
	
    private final Logger log = LoggerFactory.getLogger(TerzoPerCompensoService.class);

    @Autowired
    private ProxyService proxyService;
	
    @Autowired
    private CacheService cacheService;
    
    @Cacheable(value=Costanti.NOME_CACHE_TERZO_COMPENSO_SERVICE, key="#key")
    public TerzoPerCompensoJson getTerzi(String key, JSONBody body, String url, String query, String auth) throws ComponentException{

    	String app = Costanti.APP_SIGLA;
    	cacheService.setContext(body, app);
    	ResultProxy res = proxyService.process(HttpMethod.POST, body, app, url, query, auth, false);
    	TerzoPerCompensoJson terzoJson = null;
		try {
			terzoJson = (TerzoPerCompensoJson)new ObjectMapper().readValue(res.getBody(), TerzoPerCompensoJson.class);
		} catch (IOException e) {
			log.error("Errore", e);
			throw new ComponentException(Utility.getMessageException(e),e);
		}
		return terzoJson;
    	
    }

    public TerzoPerCompensoJson getTerzi(String codiceFiscale, ZonedDateTime daData, ZonedDateTime aData) throws ComponentException{
		String key = codiceFiscale;
		
		if (!StringUtils.isEmpty(daData)){
			key += DateUtils.getDefaultDateAsString(daData);
		}
		if (!StringUtils.isEmpty(aData)){
			key += DateUtils.getDefaultDateAsString(aData);
		}

		JSONBody jBody = null;
		jBody = proxyService.inizializzaJson();
		List<JSONClause> clauses = prepareJSONClause(codiceFiscale, daData, aData);
		jBody.setClauses(clauses);
		List<JSONOrderBy> order = prepareJSONOrderBy();
		jBody.setOrderBy(order);
		String url = Costanti.REST_TERZO_PER_COMPENSO;
		return getTerzi(key, jBody, url, "proxyURL="+url, null);
    }

    public List<JSONOrderBy> prepareJSONOrderBy() {
		JSONOrderBy order = new JSONOrderBy();
		order.setName("dt_fin_validita");
		order.setType("DESC");
		List<JSONOrderBy> orderBy = new ArrayList<JSONOrderBy>();
		orderBy.add(order);
		return orderBy;
	}
    public List<JSONClause> prepareJSONClause(String codiceFiscale, ZonedDateTime daData, ZonedDateTime aData) {
		JSONClause clause = new JSONClause();
		clause.setFieldName("codice_fiscale");
		clause.setFieldValue(codiceFiscale);
		clause.setCondition("AND");
		clause.setOperator("=");
		List<JSONClause> clauses = new ArrayList<JSONClause>();
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("aData");
		clause.setFieldValue(DateUtils.getDefaultDateAsString(aData));
		clause.setCondition("AND");
		clause.setOperator(">=");
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("daData");
		clause.setFieldValue(DateUtils.getDefaultDateAsString(daData));
		clause.setCondition("AND");
		clause.setOperator("<=");
		clauses.add(clause);
		return clauses;
	}
}
