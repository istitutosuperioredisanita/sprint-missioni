package it.cnr.si.missioni.util.proxy.cache.service;

import it.cnr.jada.util.Introspector;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.service.ConfigService;
import it.cnr.si.missioni.service.OrdineMissioneService;
import it.cnr.si.missioni.service.ProxyService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.ResultProxy;
import it.cnr.si.missioni.util.proxy.cache.CallCache;
import it.cnr.si.missioni.util.proxy.cache.ResultCacheProxy;
import it.cnr.si.missioni.util.proxy.cache.json.Clause;
import it.cnr.si.missioni.util.proxy.cache.json.ClauseToIterate;
import it.cnr.si.missioni.util.proxy.cache.json.RestService;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.CommonJsonRest;
import it.cnr.si.missioni.util.proxy.json.object.RestServiceBean;
import it.cnr.si.missioni.util.proxy.json.object.sigla.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cache.CacheManager;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CacheService implements EnvironmentAware{

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneService.class);

	@Autowired
	private ProxyService proxyService;
	
    @Autowired
    CacheManager cacheManager;
    
	@Autowired
	private ConfigService configService;
	
	private RelaxedPropertyResolver propertyResolver;

	private Environment environment;
	
	@PostConstruct
	public void init(){
		if (configService.getServices()!= null && configService.getServices().getRestService() != null ){
			for (Iterator<RestService> iteratorRest = configService.getServices().getRestService().iterator(); iteratorRest.hasNext();){
				RestService rest = iteratorRest.next();
				if (!Utility.nvl(rest.getSkipLoadStartup(),"N").equals("S")){
					cacheRestService(rest);
				}
			}
		}
	}

	private RestService getRestServiceForFilter(String urlInCache){
		if (configService.getServices()!= null && configService.getServices().getRestService() != null ){
			for (Iterator<RestService> iteratorRestForFilter = configService.getServices().getRestService().iterator(); iteratorRestForFilter.hasNext();){
				RestService restForFilter = iteratorRestForFilter.next();
				if (restForFilter.getUrl() != null && restForFilter.getUrl().equals(urlInCache)){
					return restForFilter;
				}
			}
		}
		return null;
	}
	
	private void cacheRestService(RestService rest) {
		if (rest.getClauseToIterate() != null && !rest.getClauseToIterate().isEmpty()){
			for (Iterator<ClauseToIterate> iteratorClauseToIterate = rest.getClauseToIterate().iterator(); iteratorClauseToIterate.hasNext();){
				ClauseToIterate clauseToIterate = iteratorClauseToIterate.next();
				if (clauseToIterate.getType() != null && clauseToIterate.getType().equals("callRestForGetValuesForFilter")){
					Boolean eseguitaChiamata = executeCallForFilter(rest, clauseToIterate);
					if (!eseguitaChiamata){
						cacheRest(rest, null);
					}
				} else if (clauseToIterate.getType() != null && clauseToIterate.getType().equals("calculateValues")){
					if (!StringUtils.isEmpty(clauseToIterate.getFromValue()) && clauseToIterate.containsToSpecialValue()){
						Integer from = new Integer(clauseToIterate.getFromValue()); 
						Integer to = (int)clauseToIterate.getValueFromToSpecialValue();
						for (int anno=from; anno<=to; anno++){
							JSONClause clause = new JSONClause(clauseToIterate.getCondition(), clauseToIterate.getFieldName(), "=", anno);
							cacheRest(rest, clause);
						}
					} else {
						cacheRest(rest, null);
					}
				}
			}
		} else {
			cacheRest(rest, null);
		}
	}

	private Boolean executeCallForFilter(RestService rest,
			ClauseToIterate clauseToIterate) {
		Boolean eseguitaChiamata = false;
		if (clauseToIterate.getUrlInCache() != null ){
			RestService restForFilter = getRestServiceForFilter(clauseToIterate.getUrlInCache());
			if (restForFilter != null){
				CallCache callCache = prepareCallCache(restForFilter, null);
				ResultProxy result = proxyService.processInCache(callCache);
				String risposta = result.getBody();
				CommonJsonRest<RestServiceBean> commonJsonRest = result.getCommonJsonResponse();
				if (commonJsonRest != null){
					try {
						List lista = commonJsonRest.getElements();
						if (lista != null && !lista.isEmpty()){
							for (Iterator<RestServiceBean> iterator = lista.iterator(); iterator.hasNext();){
								RestServiceBean bean = iterator.next();
								Object value = Introspector.getPropertyValue(bean, clauseToIterate.getFieldGetForSpecialValue());
								if (value != null){
									String valueList = (String)value;
									JSONClause clause = new JSONClause(clauseToIterate.getCondition(), clauseToIterate.getFieldName(), clauseToIterate.getOperator(), valueList);
									eseguitaChiamata = true;
									cacheRest(rest, clause);
								}
							}
						}
					} catch (Exception ex) {
						log.info("Errore nella lettura del servizio REST: " + rest.getUrl());
					}
				}
			}
		}
		return eseguitaChiamata;
	}

	public void cacheRest(RestService rest, JSONClause clause) {
		List<JSONClause> listaClause = null;
		if (clause != null){
			listaClause = new ArrayList<JSONClause>();
			listaClause.add(clause);
		}
		CallCache callCache = prepareCallCache(rest, listaClause);
		ResultProxy result = proxyService.process(callCache);
		/* E' necessario chiamare il service non in cache e poi mettere a mano il risultato in cache perchè quando parte questo service allo startup dell'applicazione
		 * i servizi di cache non sono stati ancora inizializzati. In questo modo i dati vengono messi in cache. */		
		cacheManager.getCache(Costanti.NOME_CACHE_PROXY).put(callCache, result);
	}

	private CallCache prepareCallForCache(RestService rest, JSONBody jBody) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		String app = rest.getApp();
    	setContext(jBody, app);

		CallCache callCache = new CallCache(HttpMethod.POST, jBody, app, rest.getUrl(), "proxyURL="+rest.getUrl(), null, rest.getClasseJson());
		return callCache;
	}

	public void setContext(JSONBody jBody, String app) {
		String uoContext = propertyResolver.getProperty(app + ".context.cd_unita_organizzativa");
    	if (!StringUtils.isEmpty(uoContext)){
    		Context context = jBody.getContext();
    		if (context == null){
    			context = new Context();
    		}
    		if (StringUtils.isEmpty(context.getCd_unita_organizzativa())){
    			context.setCd_unita_organizzativa(uoContext);
    		}
    		if (StringUtils.isEmpty(context.getCd_cds())){
    			context.setCd_cds(propertyResolver.getProperty(app + ".context.cd_cds"));
    		}
    		if (StringUtils.isEmpty(context.getCd_cdr())){
    			context.setCd_cdr(propertyResolver.getProperty(app + ".context.cd_cdr"));
    		}
    		jBody.setContext(context);
    	}
	}

	private JSONBody prepareJSONForRest(RestService rest, List<JSONClause> clauseToAdd) {
		JSONBody jBody = proxyService.inizializzaJson();
		if (rest.getClauseFixed() != null && !rest.getClauseFixed().isEmpty()){
			List<JSONClause> listaClause = new ArrayList<JSONClause>();
			for (Iterator<Clause> iteratorDefault = rest.getClauseFixed().iterator(); iteratorDefault.hasNext();){
				Clause clauseDefault = iteratorDefault.next();
				JSONClause clause = getClause(clauseDefault);
				listaClause.add(clause);
			}
			if (clauseToAdd != null){
				listaClause.addAll(clauseToAdd);
			}
			jBody.setClauses(listaClause);
		}
		setOrderBy(rest, jBody);
		return jBody;
	}

	private void setOrderBy(RestService rest, JSONBody jBody) {
		if (rest.getOrder() != null && !rest.getOrder().isEmpty()){
			jBody.setOrderBy(rest.getOrder());
		}
	}

	public void setOrderBy(String app, String url, JSONBody jBody) {
		if (configService.getServices()!= null && configService.getServices().getRestService() != null ){
			for (Iterator<RestService> iteratorRest = configService.getServices().getRestService().iterator(); iteratorRest.hasNext();){
				RestService rest = iteratorRest.next();
				if (!StringUtils.isEmpty(rest.getUrl()) && rest.getUrl().equals(url) && 
						!StringUtils.isEmpty(rest.getUrl()) && rest.getApp().equals(app)	){
					setOrderBy(rest, jBody);
				}
			}
		}
	}

	public CallCache prepareCallCache(RestService rest, List<JSONClause> clauseToAdd) {
		JSONBody body = prepareJSONForRest(rest, clauseToAdd);
		return  prepareCallForCache(rest, body);
	}
	
	public CallCache prepareCallCache(RestService rest) {
		return prepareCallCache(rest, null);
	}
	
	public RestService getBasicRest(String app, String url) {
		if (configService.getServices()!= null && configService.getServices().getRestService() != null ){
			for (Iterator<RestService> iteratorRest = configService.getServices().getRestService().iterator(); iteratorRest.hasNext();){
				RestService rest = iteratorRest.next();
				if (!StringUtils.isEmpty(rest.getUrl()) && rest.getUrl().equals(url) && 
						!StringUtils.isEmpty(rest.getUrl()) && rest.getApp().equals(app)	){
					return rest;
				}
			}
		}
		return null;
	}

	private JSONClause getClause(Clause clauseDefault) {
		JSONClause clause = null;
		if (clauseDefault.containsSpecialValue()){
			clause = new JSONClause(clauseDefault.getCondition(), clauseDefault.getFieldName(), clauseDefault.getOperator(), clauseDefault.getValueFromSpecialValue());
		} else {
			clause = new JSONClause(clauseDefault.getCondition(), clauseDefault.getFieldName(), clauseDefault.getOperator(), clauseDefault.getFieldValue());
		}
		return clause;
	}

	public ResultCacheProxy manageCache(String url, JSONBody bodyRequest, String app) throws AwesomeException{
		List<JSONClause> listClausesDeleted = new ArrayList<JSONClause>();
		RestService restService = null;
		boolean isUrlToCache = false;
		if (configService.getServices()!= null && configService.getServices().getRestService() != null ){
			for (Iterator<RestService> iteratorRest = configService.getServices().getRestService().iterator(); iteratorRest.hasNext();){
				RestService rest = iteratorRest.next();
				if (rest.getUrl() != null && url != null && url.equals(rest.getUrl())){
					isUrlToCache = true;
					restService = rest;
					bodyRequest.setMaxItemsPerPage(Costanti.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_CACHE);
					if (bodyRequest.getClauses() != null && !bodyRequest.getClauses().isEmpty() &&
							rest.getClauseVariable() != null && !rest.getClauseVariable().isEmpty()){
						List<JSONClause> listNewClauses = new ArrayList<JSONClause>();
						for (Iterator<JSONClause> iterator = bodyRequest.getClauses().iterator(); iterator.hasNext();){
							JSONClause clause = iterator.next();
							boolean clauseTrovata = existsClause(rest.getClauseVariable(), clause);
							if (!clauseTrovata){
								listNewClauses.add(clause);
							} else {
								listClausesDeleted.add(clause);
							}
						}
						if (listNewClauses != null && !listNewClauses.isEmpty()){
							bodyRequest.setClauses(listNewClauses);
						} else {
							bodyRequest.setClauses(null);
						}
					}
					setContext(bodyRequest, app);
					//						}
					ResultCacheProxy resultCacheProxy = new ResultCacheProxy();
					resultCacheProxy.setListClausesDeleted(listClausesDeleted);
					resultCacheProxy.setRestService(restService);
					resultCacheProxy.setUrlToCache(isUrlToCache);
					resultCacheProxy.setBody(bodyRequest);
					return resultCacheProxy;
				}
			} 
		}
		return null;
	}

	public String manageResponse(RestService restService, List<JSONClause> listClausesDeleted, CommonJsonRest<RestServiceBean> commonJson) {
		return manageResponse(restService, listClausesDeleted, commonJson, false);
	}

	public String manageResponse(RestService restService, List<JSONClause> listClausesDeleted, CommonJsonRest<RestServiceBean> commonJson, Boolean returnSingleResult) {
		String risposta = ""; 
		try {
			if (commonJson != null){
				List lista = commonJson.getElements();
				List listResponse = new ArrayList<>();
				if (lista != null && !lista.isEmpty()){
					for (Iterator<RestServiceBean> iterator = lista.iterator(); iterator.hasNext();){
						RestServiceBean bean = iterator.next();
						boolean isValid = true;
						for (Iterator<JSONClause> iteratorClause = listClausesDeleted.iterator(); iteratorClause.hasNext();){
							if (!isValid){
								break;
							}
							JSONClause clause = iteratorClause.next();
							Object value = Introspector.getPropertyValue(bean, clause.getFieldName());
							if (value == null){
								isValid = false;
								break;
							}
							Class type =  Introspector.getPropertyType(Class.forName(restService.getClasse()), clause.getFieldName());
							switch (type.getName()) {
							case "java.lang.String":
								isValid = isValidRowForClauseString(clause, bean, value);
								break;
							case "java.lang.Integer":
								isValid = isValidRowForClauseInteger(clause, bean, value);
								break;
							case "java.lang.Long":
								isValid = isValidRowForClauseLong(clause, bean, value);
								break;
							case "java.lang.Boolean":
								isValid = isValidRowForClauseBoolean(clause, bean, value);
								break;
							case "java.math.BigDecimal":
								isValid = isValidRowForClauseBigDecimal(clause, bean, value);
								break;
							}
						}
						if (isValid){
							listResponse.add(bean);
							if (returnSingleResult){
								break;
							}
						}
					}
				}
				commonJson.setElements(listResponse);
				commonJson.setTotalNumItems(listResponse.size());
				risposta = createResponse(commonJson);
			}
		} catch (Exception ex) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON della richiesta "+ restService.getUrl()+" ("+Utility.getMessageException(ex)+").");
		}
		return risposta;
	}

	public String createResponse(CommonJsonRest<RestServiceBean> commonJson)
			throws JsonProcessingException {
		String risposta;
		risposta = new ObjectMapper().writeValueAsString(commonJson);
		return risposta;
	}

	private Boolean isValidRowForClauseBoolean(JSONClause clause, RestServiceBean bean, Object value){
		Boolean valueList = (Boolean)value;
		Boolean valueClause = (Boolean)clause.getFieldValue();
		return isBeanOk(clause, valueList, valueClause);
	}

	private Boolean isValidRowForClauseBigDecimal(JSONClause clause, RestServiceBean bean, Object value){
		BigDecimal valueList = (BigDecimal)value;
		BigDecimal valueClause = (BigDecimal)clause.getFieldValue();
		return isBeanOk(clause, valueList, valueClause);
	}

	private Boolean isValidRowForClauseString(JSONClause clause, RestServiceBean bean, Object value){
		String valueList = (String)value;
		String valueClause = (String)clause.getFieldValue();
		return isBeanOk(clause, valueList, valueClause);
	}

	private Boolean isValidRowForClauseLong(JSONClause clause, RestServiceBean bean, Object value){
		Long valueList = (Long)value;
		Long valueClause = new Long(clause.getFieldValue().toString());
		return isBeanOk(clause, valueList, valueClause);
	}

	private Boolean isValidRowForClauseInteger(JSONClause clause, RestServiceBean bean, Object value){
		Integer valueList = (Integer)value;
		Integer valueClause = (Integer)clause.getFieldValue();
		return isBeanOk(clause, valueList, valueClause);
	}

	private Boolean isBeanOk(JSONClause clause, String valueList, String valueClause) {
		switch (clause.getOperator()) {
			case "=":
				if (valueList.compareTo(valueClause) == 0){
					return true;
				}
				break;
			case ">=":
				if (valueList.compareTo(valueClause) >= 0){
					return true;
				}
				break;
			case "<=":
				if (valueList.compareTo(valueClause) <= 0){
					return true;
				}
				break;
			case ">":
				if (valueList.compareTo(valueClause) > 0){
					return true;
				}
				break;
			case "<":
				if (valueList.compareTo(valueClause) < 0){
					return true;
				}
				break;
			case "like":
				int position = valueClause.indexOf("%");
				if (position != -1){
					int totalLength = valueClause.length();
					String valueToCompare = null;
					if (totalLength == position){
						valueToCompare = valueClause.substring(position - 1);
						if (valueList.startsWith(valueToCompare)){
							return true;
						}
					} else {
						if (position == 0){
							int lastPosition = valueClause.lastIndexOf("%");
							if (lastPosition == position){
								valueToCompare = valueClause.substring(1, totalLength);
								if (valueList.endsWith(valueToCompare)){
									return true;
								}
							} else {
								if (totalLength == lastPosition){
									valueToCompare = valueClause.substring(1, totalLength - 1);
									if (valueList.contains(valueToCompare)){
										return true;
									}
								} 
// TODO Da Completare								
							}
						} else {
// TODO Da Completare								
						}
					}
				}
				break;
		}
		return false;
	}
	
	private Boolean isBeanOk(JSONClause clause, BigDecimal valueList, BigDecimal valueClause) {
		switch (clause.getOperator()) {
			case "=":
				if (valueList.compareTo(valueClause) == 0){
					return true;
				}
				break;
			case ">=":
				if (valueList.compareTo(valueClause) >= 0){
					return true;
				}
				break;
			case "<=":
				if (valueList.compareTo(valueClause) <= 0){
					return true;
				}
				break;
			case ">":
				if (valueList.compareTo(valueClause) > 0){
					return true;
				}
				break;
			case "<":
				if (valueList.compareTo(valueClause) < 0){
					return true;
				}
				break;
			case "!=":
				if (valueList.compareTo(valueClause) != 0){
					return true;
				}
				break;
		}
		return false;
	}
	
	private Boolean isBeanOk(JSONClause clause, Boolean valueList, Boolean valueClause) {
		switch (clause.getOperator()) {
			case "=":
				if (valueList.compareTo(valueClause) == 0){
					return true;
				}
				break;
			case "!=":
				if (valueList.compareTo(valueClause) != 0){
					return true;
				}
				break;
		}
		return false;
	}
	
	private Boolean isBeanOk(JSONClause clause, Integer valueList, Integer valueClause) {
		switch (clause.getOperator()) {
			case "=":
				if (valueList.compareTo(valueClause) == 0){
					return true;
				}
				break;
			case ">=":
				if (valueList.compareTo(valueClause) >= 0){
					return true;
				}
				break;
			case "<=":
				if (valueList.compareTo(valueClause) <= 0){
					return true;
				}
				break;
			case ">":
				if (valueList.compareTo(valueClause) > 0){
					return true;
				}
				break;
			case "<":
				if (valueList.compareTo(valueClause) < 0){
					return true;
				}
				break;
		}
		return false;
	}
	
	private Boolean isBeanOk(JSONClause clause, Long valueList, Long valueClause) {
		switch (clause.getOperator()) {
			case "=":
				if (valueList.compareTo(valueClause) == 0){
					return true;
				}
				break;
			case ">=":
				if (valueList.compareTo(valueClause) >= 0){
					return true;
				}
				break;
			case "<=":
				if (valueList.compareTo(valueClause) <= 0){
					return true;
				}
				break;
			case ">":
				if (valueList.compareTo(valueClause) > 0){
					return true;
				}
				break;
			case "<":
				if (valueList.compareTo(valueClause) < 0){
					return true;
				}
				break;
		}
		return false;
	}
	
	public boolean existsClause(List<Clause> listClause, JSONClause clause) {
		boolean clauseTrovata = false;
		for (Iterator<Clause> iteratorDefault = listClause.iterator(); iteratorDefault.hasNext();){
			Clause clauseDefault = iteratorDefault.next();
			if (!StringUtils.isEmpty(clauseDefault.getCondition()) && !StringUtils.isEmpty(clause.getCondition()) && 
					clauseDefault.getCondition().equals(clause.getCondition()) && 
					!StringUtils.isEmpty(clauseDefault.getFieldName()) && !StringUtils.isEmpty(clause.getFieldName()) && 
					clauseDefault.getFieldName().equals(clause.getFieldName()) 
					&&
				((	!StringUtils.isEmpty(clauseDefault.getOperator()) && !StringUtils.isEmpty(clause.getOperator()) && 
					clauseDefault.getOperator().toUpperCase().equals(clause.getOperator().toUpperCase())) || ( StringUtils.isEmpty(clause.getOperator())) || 
					(!clause.getOperator().toUpperCase().equals("LIKE")))){
				clauseTrovata = true;
				break;
			}
		}
		return clauseTrovata;
	}
	@Override
	public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.proxy.");
	}
}
