package it.cnr.si.missioni.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.ResultProxy;
import it.cnr.si.missioni.util.proxy.cache.service.CacheService;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.object.TerzoPerCompenso;
import it.cnr.si.missioni.util.proxy.json.object.TerzoPerCompensoJson;

@Service
public class TerzoPerCompensoService {
	
    private final Logger log = LoggerFactory.getLogger(TerzoPerCompensoService.class);

    @Autowired
    private ProxyService proxyService;
    
    @Autowired
    private CacheService cacheService;
    
    @Cacheable(value=Costanti.NOME_CACHE_PROXY, key="#key")
    public List<TerzoPerCompenso> getTerzi(String key, JSONBody body, String url, String query, String auth) throws ComponentException{

    	String app = Costanti.APP_SIGLA;
    	cacheService.setContext(body, app);
    	ResultProxy res = proxyService.process(HttpMethod.POST, body, Costanti.APP_SIGLA, url, query, auth, false);
    	TerzoPerCompensoJson terzoJson = null;
		try {
			terzoJson = (TerzoPerCompensoJson)new ObjectMapper().readValue(res.getBody(), TerzoPerCompensoJson.class);
		} catch (IOException e) {
			log.error("Errore", e);
			throw new ComponentException(Utility.getMessageException(e),e);
		}
		return terzoJson.getElements();
    	
    }
}
