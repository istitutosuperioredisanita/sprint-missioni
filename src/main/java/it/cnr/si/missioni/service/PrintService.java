package it.cnr.si.missioni.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.SimpleClientHttpRequestWithGetBodyFactory;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.print.Key;
import it.cnr.si.missioni.util.proxy.json.object.print.Param;
import it.cnr.si.missioni.util.proxy.json.object.print.Params;

@Service
public class PrintService{

    private final Logger log = LoggerFactory.getLogger(PrintService.class);

	@Autowired	
	private Environment env;

	private RelaxedPropertyResolver propertyResolver;

    public String createJsonForPrint(Object object){
		ObjectMapper mapper = new ObjectMapper();
		String myJson = null;
		try {
			myJson = mapper.writeValueAsString(object);
		} catch (Exception ex) {
			throw new ComponentException("Errore nella generazione del file JSON per l'esecuzione della stampa ("+Utility.getMessageException(ex)+").",ex);
		}
		return myJson;
    }
    
    public ResponseEntity<byte[]> processForPrint(HttpMethod httpMethod, Params params) throws ComponentException{
    	this.propertyResolver = new RelaxedPropertyResolver(env, "spring.print.");
    	String url = "";
    	if (propertyResolver != null && propertyResolver.getProperty("endpoint") != null) {
    		url = propertyResolver.getProperty("endpoint");
    	} else {
    		throw new ComponentException("Configurare l'EndPoint per le stampe");
    	}
    	log.info("Base Print Url is: " + url);  
        try {
        	HttpHeaders headers = new HttpHeaders();
    		headers.add("Authorization", "");
    		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    		String body = createJsonForPrint(params);
            log.info("Body: " + body);
            log.info("Headers: " + headers);
        	HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);  
        	RestTemplate rest = new RestTemplate(new SimpleClientHttpRequestWithGetBodyFactory());
        	rest.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        	return rest.exchange(url, httpMethod, requestEntity, byte[].class);
        } catch (HttpClientErrorException _ex) {
        	log.error(_ex.getMessage(), _ex);
			throw new ComponentException("Errore nella registrazione degli allegati. "+_ex.getMessage());
        }    	
    }

    
    public byte[] print(String myJson, String printNameJasper) throws AwesomeException, ComponentException {
		try {
			Params params = createParamsForPrint(myJson, printNameJasper);
			ResponseEntity<byte[]> response = processForPrint(HttpMethod.POST, params);
			log.debug("Stampa "+printNameJasper+" length: "+response.getHeaders().get("Content-Length").get( 0 ));
			return response.getBody();

		} catch (Exception e) {
			throw new ComponentException("Error in JASPER ("+ e + ").",e);
		}
	}

	protected Params createParamsForPrint(String myJson, String printNameJasper) {
		Key key = new Key();
		key.setNomeParam(Costanti.PARAMETER_DATA_SOURCE_FOR_PRINT);
		Param param = new Param();
		param.setKey(key);
		param.setValoreParam(myJson);
		param.setParamType(String.class.getCanonicalName());
		Params params = new Params();
		List<Param> lista = new ArrayList<Param>();
		lista.add(param);
		params.setParams(lista);
		params.setReport(printNameJasper);
		return params;
	}
}
