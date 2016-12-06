package it.cnr.si.missioni.util.proxy.json.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.object.Terzo;

@Service
public class ValidaDettaglioRimborsoService {
	@Autowired
    private CommonService commonService;
	
	public Terzo valida(RimborsoMissioneDettagli dettaglio) throws AwesomeException {
		if (dettaglio != null && dettaglio.getRimborsoMissione() != null && dettaglio.getRimborsoMissione().getAnno() != null){
			JSONBody body = prepareJSONBody(dettaglio);

			String app = Costanti.APP_SIGLA;
			String url = Costanti.REST_VALIDA_MASSIMALE_SPESA;
			String risposta = commonService.process(body, app, url);

		}
		return null;
	}

	public JSONBody prepareJSONBody(RimborsoMissioneDettagli dettaglio) {
		JSONBody clause = new JSONBody();
		if (dettaglio.getKmPercorsi() != null){
			clause.setKm(dettaglio.getKmPercorsi().toString());
		}
		clause.setData(DateUtils.getDefaultDateAsString(dettaglio.getDataSpesa()));
		clause.setNazione(dettaglio.getRimborsoMissione().getNazione());
		clause.setInquadramento(dettaglio.getRimborsoMissione().getInquadramento());
		clause.setDivisa(dettaglio.getCdDivisa());
		clause.setImportoSpesa(dettaglio.getImportoEuro().toString());
		clause.setCdTipoSpesa(dettaglio.getCdTiSpesa());
		if (dettaglio.getCdTiPasto() != null){
			clause.setCdTipoPasto(dettaglio.getCdTiPasto());
		}
		return clause;
	}

}
