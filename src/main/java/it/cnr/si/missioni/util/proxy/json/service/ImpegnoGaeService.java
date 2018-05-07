package it.cnr.si.missioni.util.proxy.json.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.ImpegnoGae;
import it.cnr.si.missioni.util.proxy.json.object.ImpegnoGaeJson;

@Service
public class ImpegnoGaeService {
	@Autowired
    private CommonService commonService;


	public ImpegnoGae loadImpegno(OrdineMissione ordineMissione) throws AwesomeException {
		return loadImpegno(ordineMissione.getCdsSpesa(), ordineMissione.getUoSpesa(), ordineMissione.getEsercizioOriginaleObbligazione(), ordineMissione.getPgObbligazione(), ordineMissione.getGae());

	}

	public ImpegnoGae loadImpegno(RimborsoMissione rimborsoMissione) throws AwesomeException {
		return loadImpegno(rimborsoMissione.getCdsSpesa(), rimborsoMissione.getUoSpesa(), rimborsoMissione.getEsercizioOriginaleObbligazione(), rimborsoMissione.getPgObbligazione(), rimborsoMissione.getGae());
	}

	public ImpegnoGae loadImpegno(String cds, String unitaOrganizzativa, Integer esercizio, Long pgObbligazione, String gae) throws AwesomeException {
		if (pgObbligazione != null && esercizio != null){
			LocalDate data = LocalDate.now();
			int anno = data.getYear();

			List<JSONClause> clauses = prepareJSONClause(cds, unitaOrganizzativa, anno, esercizio, pgObbligazione, gae);
			return loadImpegno(clauses);
		}
		return null;
	}

	private ImpegnoGae loadImpegno(List<JSONClause> clauses) throws AwesomeException {
		String app = Costanti.APP_SIGLA;
		String url = Costanti.REST_IMPEGNO_GAE;
		String risposta = commonService.process(clauses, app, url);

		try {
			ObjectMapper mapper = new ObjectMapper();
			ImpegnoGaeJson impegnoGaeJson = mapper.readValue(risposta, ImpegnoGaeJson.class);
			if (impegnoGaeJson != null){
				List<ImpegnoGae> lista = impegnoGaeJson.getElements();
				if (lista != null && !lista.isEmpty()){
					return lista.get(0);
				}
			}
		} catch (Exception ex) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per l'Impegno/GAE ("+Utility.getMessageException(ex)+").");
		}
		return null;
	}
	
	public List<JSONClause> prepareJSONClause(String cdsSpesa, String cdUnitaOrganizzativa, Integer anno, Integer esercizioOriginaleObbligazione, Long pgObbligazione, String gae) {
		JSONClause clause = new JSONClause();
		clause.setFieldName("cdCds");
		clause.setFieldValue(cdsSpesa);
		clause.setCondition("AND");
		clause.setOperator("=");
		List<JSONClause> clauses = new ArrayList<JSONClause>();
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("esercizio");
		clause.setFieldValue(anno);
		clause.setCondition("AND");
		clause.setOperator("=");
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("cdUnitaOrganizzativa");
		clause.setFieldValue(cdUnitaOrganizzativa);
		clause.setCondition("AND");
		clause.setOperator("=");
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("esercizioOriginale");
		clause.setFieldValue(esercizioOriginaleObbligazione);
		clause.setCondition("AND");
		clause.setOperator("=");
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("pgObbligazione");
		clause.setFieldValue(pgObbligazione);
		clause.setCondition("AND");
		clause.setOperator("=");
		clauses.add(clause);
		clause = new JSONClause();
		clause.setFieldName("cdLineaAttivita");
		clause.setFieldValue(gae);
		clause.setCondition("AND");
		clause.setOperator("=");
		clauses.add(clause);
		return clauses;
	}

}
