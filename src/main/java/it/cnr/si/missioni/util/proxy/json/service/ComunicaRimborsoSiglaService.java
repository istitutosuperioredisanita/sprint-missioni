package it.cnr.si.missioni.util.proxy.json.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.MissioneSigla;

@Service
public class ComunicaRimborsoSiglaService {
	@Autowired
    private CommonService commonService;
	
	public OggettoBulk comunica(MissioneSigla missione) throws AwesomeException {
		if (missione != null){
			JSONBody body = prepareJSONBody(missione);

			String app = Costanti.APP_SIGLA;
			String url = Costanti.REST_COMUNICA_RIMBORSO_SIGLA;
			String risposta = commonService.process(body, app, url, true, HttpMethod.PUT);

		}
		return null;
	}

	public JSONBody prepareJSONBody(MissioneSigla missione) {
		JSONBody body = new JSONBody();
		if (missione.getOggettoBulk() != null){
			body.setOggettoBulk(missione.getOggettoBulk());
		}
		return body;
	}

}
