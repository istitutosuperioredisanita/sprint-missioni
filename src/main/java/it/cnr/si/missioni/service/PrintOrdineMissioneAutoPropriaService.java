package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiAutoPropria;
import it.cnr.si.missioni.domain.custom.print.PrintOrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.print.Spostamenti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrintOrdineMissioneAutoPropriaService {
    private final Logger log = LoggerFactory.getLogger(PrintOrdineMissioneAutoPropriaService.class);

    @Autowired
    private PrintService printService;

    @Autowired
    private AccountService accountService;
    
//    private PrintOrdineMissione getPrintOrdineMissione(Principal principal, OrdineMissione ordineMissione) throws AwesomeException, ComponentException {
    private PrintOrdineMissioneAutoPropria getPrintOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria, String currentLogin) throws AwesomeException, ComponentException {
    	OrdineMissione ordineMissione = ordineMissioneAutoPropria.getOrdineMissione();
		Account account = accountService.loadAccountFromRest(ordineMissione.getUid());
    	PrintOrdineMissioneAutoPropria printOrdineMissioneAutoPropria = new PrintOrdineMissioneAutoPropria();
    	printOrdineMissioneAutoPropria.setAnno(ordineMissione.getAnno());
    	printOrdineMissioneAutoPropria.setCodiceFiscaleRich(account.getCodiceFiscale());
    	printOrdineMissioneAutoPropria.setComuneResidenzaRich(Utility.nvl(ordineMissione.getComuneResidenzaRich()));
    	if (account.getDataNascita() != null){
    		Date dataNas = DateUtils.parseDate(account.getDataNascita().substring(0, 10),"yyyy-MM-dd");
    		printOrdineMissioneAutoPropria.setDataDiNascitaRich(DateUtils.getDateAsString(dataNas, DateUtils.PATTERN_DATE));
    	}
    	printOrdineMissioneAutoPropria.setDatoreLavoroRich(ordineMissione.getDatoreLavoroRich());
    	printOrdineMissioneAutoPropria.setDomicilioFiscaleRich(Utility.nvl(ordineMissione.getDomicilioFiscaleRich()));
    	printOrdineMissioneAutoPropria.setIndirizzoResidenzaRich(Utility.nvl(ordineMissione.getIndirizzoResidenzaRich()));
    	printOrdineMissioneAutoPropria.setLivelloRich(ordineMissione.getLivelloRich() == null ? "" : ordineMissione.getLivelloRich().toString());
    	printOrdineMissioneAutoPropria.setLuogoDiNascitaRich(account.getComuneNascita());
    	printOrdineMissioneAutoPropria.setMatricolaRich(account.getMatricola());
    	printOrdineMissioneAutoPropria.setCognomeRich(account.getCognome());
    	printOrdineMissioneAutoPropria.setNomeRich(account.getNome());
    	printOrdineMissioneAutoPropria.setQualificaRich(ordineMissione.getQualificaRich() == null ? "" : ordineMissione.getQualificaRich());

    	printOrdineMissioneAutoPropria.setDataFineMissione(DateUtils.getDateAsString(ordineMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC));
    	printOrdineMissioneAutoPropria.setDataInizioMissione(DateUtils.getDateAsString(ordineMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC));
    	printOrdineMissioneAutoPropria.setDataInserimento(DateUtils.getDateAsString(ordineMissione.getDataInserimento(), DateUtils.PATTERN_DATE));
    	printOrdineMissioneAutoPropria.setOggetto(ordineMissione.getOggetto());
    	printOrdineMissioneAutoPropria.setDestinazione(ordineMissione.getDestinazione());
		printOrdineMissioneAutoPropria.setStato(ordineMissione.getDecodeStato());

		printOrdineMissioneAutoPropria.setCartaCircolazione(ordineMissioneAutoPropria.getCartaCircolazione());
		printOrdineMissioneAutoPropria.setDataRilascioPatente(DateUtils.getDateAsString(ordineMissioneAutoPropria.getDataRilascioPatente(), DateUtils.PATTERN_DATE));
		printOrdineMissioneAutoPropria.setDataScadenzaPatente(DateUtils.getDateAsString(ordineMissioneAutoPropria.getDataScadenzaPatente(), DateUtils.PATTERN_DATE));
		printOrdineMissioneAutoPropria.setEntePatente(ordineMissioneAutoPropria.getEntePatente());
		printOrdineMissioneAutoPropria.setMarca(ordineMissioneAutoPropria.getMarca());
		printOrdineMissioneAutoPropria.setModello(ordineMissioneAutoPropria.getModello());
		printOrdineMissioneAutoPropria.setTarga(ordineMissioneAutoPropria.getTarga());
		printOrdineMissioneAutoPropria.setPolizzaAssicurativa(ordineMissioneAutoPropria.getPolizzaAssicurativa());
		printOrdineMissioneAutoPropria.setNumeroPatente(ordineMissioneAutoPropria.getNumeroPatente());

		if (ordineMissioneAutoPropria.getListSpostamenti() != null){
			List<Spostamenti> listSpostamentiPrint = new ArrayList<Spostamenti>();
	    	for (Iterator<SpostamentiAutoPropria> iterator = ordineMissioneAutoPropria.getListSpostamenti().iterator(); iterator.hasNext();){
	    		SpostamentiAutoPropria spostamenti = iterator.next();
	    		Spostamenti spostamentiPrint = new Spostamenti();
	    		spostamentiPrint.setPercorsoDa(spostamenti.getPercorsoDa());
	    		spostamentiPrint.setPercorsoA(spostamenti.getPercorsoA());
	    		listSpostamentiPrint.add(spostamentiPrint);
	    	}
	    	printOrdineMissioneAutoPropria.setSpostamenti(listSpostamentiPrint);
		}
    	return printOrdineMissioneAutoPropria; 
    }

	public byte[] printOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria, String currentLogin) throws AwesomeException, ComponentException {
		String myJson = createJsonPrintOrdineMissioneAutoPropria(ordineMissioneAutoPropria, currentLogin);
		Map<String, String> mapSubReport = new HashMap<String, String>();
		mapSubReport.put("SUBREPORT_SPOSTAMENTI","ordine_missione_spostamenti_sub.jasper");
		return printService.print(myJson, "OrdineMissioneAutoPropria.jasper", mapSubReport);
	}

	public String createJsonPrintOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria, String currentLogin) throws ComponentException {
		PrintOrdineMissioneAutoPropria PrintOrdineMissioneAutoPropria = getPrintOrdineMissioneAutoPropria(ordineMissioneAutoPropria, currentLogin);
		return printService.createJsonForPrint(PrintOrdineMissioneAutoPropria);
	}

}
