package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAnticipo;
import it.cnr.si.missioni.domain.custom.print.PrintOrdineMissioneAnticipo;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrintOrdineMissioneAnticipoService {
    private final Logger log = LoggerFactory.getLogger(PrintOrdineMissioneAnticipoService.class);

    @Autowired
    private PrintService printService;

    @Autowired
    private AccountService accountService;
    
//    private PrintOrdineMissione getPrintOrdineMissione(Principal principal, OrdineMissione ordineMissione) throws AwesomeException, ComponentException {
    private PrintOrdineMissioneAnticipo getPrintOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo, String currentLogin) throws AwesomeException, ComponentException {
    	OrdineMissione ordineMissione = ordineMissioneAnticipo.getOrdineMissione();
		Account account = accountService.loadAccountFromRest(ordineMissione.getUid());
    	PrintOrdineMissioneAnticipo printOrdineMissioneAnticipo = new PrintOrdineMissioneAnticipo();
    	printOrdineMissioneAnticipo.setAnno(ordineMissione.getAnno());
    	printOrdineMissioneAnticipo.setNumero(new Integer(ordineMissione.getNumero().toString()));
    	printOrdineMissioneAnticipo.setCodiceFiscaleRich(account.getCodiceFiscale());
    	printOrdineMissioneAnticipo.setComuneResidenzaRich(ordineMissione.getComuneResidenzaRich());
    	if (account.getDataNascita() != null){
    		Date dataNas = DateUtils.parseDate(account.getDataNascita().substring(0, 10),"yyyy-MM-dd");
    		printOrdineMissioneAnticipo.setDataDiNascitaRich(DateUtils.getDateAsString(dataNas, DateUtils.PATTERN_DATE));
    	}
    	printOrdineMissioneAnticipo.setDataFineMissione(DateUtils.getDateAsString(ordineMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC));
    	printOrdineMissioneAnticipo.setDataInizioMissione(DateUtils.getDateAsString(ordineMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC));
    	printOrdineMissioneAnticipo.setDataInserimento(DateUtils.getDateAsString(ordineMissione.getDataInserimento(), DateUtils.PATTERN_DATE));
    	printOrdineMissioneAnticipo.setDatoreLavoroRich(ordineMissione.getDatoreLavoroRich());

    	printOrdineMissioneAnticipo.setDomicilioFiscaleRich(Utility.nvl(ordineMissione.getDomicilioFiscaleRich()));
    	printOrdineMissioneAnticipo.setImportoPresunto(Utility.numberFormat(ordineMissione.getImportoPresunto()));
    	printOrdineMissioneAnticipo.setIndirizzoResidenzaRich(ordineMissione.getIndirizzoResidenzaRich());
    	printOrdineMissioneAnticipo.setLivelloRich(ordineMissione.getLivelloRich() == null ? "" : ordineMissione.getLivelloRich().toString());
    	printOrdineMissioneAnticipo.setLuogoDiNascitaRich(account.getComuneNascita());
    	printOrdineMissioneAnticipo.setMatricolaRich(account.getMatricola());
    	printOrdineMissioneAnticipo.setCognomeRich(account.getCognome());
    	printOrdineMissioneAnticipo.setNomeRich(account.getNome());
    	printOrdineMissioneAnticipo.setNote(Utility.nvl(ordineMissioneAnticipo.getNote()));
    	printOrdineMissioneAnticipo.setQualificaRich(ordineMissione.getQualificaRich() == null ? "" : ordineMissione.getQualificaRich());
    	printOrdineMissioneAnticipo.setOggetto(ordineMissione.getOggetto());
    	printOrdineMissioneAnticipo.setImportoAnticipo(Utility.numberFormat(ordineMissioneAnticipo.getImporto()));
		printOrdineMissioneAnticipo.setDataAnticipo(DateUtils.getDateAsString(ordineMissioneAnticipo.getDataRichiesta(), DateUtils.PATTERN_DATE));
		printOrdineMissioneAnticipo.setStato(ordineMissioneAnticipo.getDecodeStato());
    	return printOrdineMissioneAnticipo; 
    }

	public byte[] printOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo, String currentLogin) throws AwesomeException, ComponentException {
		String myJson = createJsonPrintOrdineMissioneAnticipo(ordineMissioneAnticipo, currentLogin);
		return printService.print(myJson, "OrdineMissioneAnticipo.jasper");
	}

	public String createJsonPrintOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo, String currentLogin) throws ComponentException {
		PrintOrdineMissioneAnticipo printOrdineMissioneAnticipo = getPrintOrdineMissioneAnticipo(ordineMissioneAnticipo, currentLogin);
		return printService.createJsonForPrint(printOrdineMissioneAnticipo);
	}

}
