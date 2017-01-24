package it.cnr.si.missioni.util.proxy.json.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.MissioneBulk;

@Service
public class ComunicaRimborsoSiglaService {
	@Autowired
    private CommonService commonService;
	
	public MissioneBulk comunica(MissioneBulk missione) throws AwesomeException {
		String risp = null;
		String body = null;
		if (missione != null){
			String app = Costanti.APP_SIGLA;
			String url = Costanti.REST_COMUNICA_RIMBORSO_SIGLA;
	    	try {
	    		ObjectMapper mapper = new ObjectMapper();
	    		body = mapper.writeValueAsString(missione);
	    	} catch (Exception ex) {
	    		throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella manipolazione del file JSON per la preparazione del body della richiesta REST ("+Utility.getMessageException(ex)+").");
	    	}

			String risposta = commonService.process(body, app, url, true, HttpMethod.PUT);
	    	try {
	    		ObjectMapper mapper = new ObjectMapper();
	    		MissioneBulk missioneBulk = mapper.readValue(risposta, MissioneBulk.class);
	    		return missioneBulk;
	    	} catch (Exception ex) {
	    		throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file di risposta.");
	    	}
		}
		return null;
	}

	public JSONBody prepareJSONBody(MissioneBulk missione) {
		JSONBody body = new JSONBody();
		body.setMissioneBulk(missione);
		return body;
	}

}
