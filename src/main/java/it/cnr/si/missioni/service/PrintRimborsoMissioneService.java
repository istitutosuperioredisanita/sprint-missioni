package it.cnr.si.missioni.service;

import java.math.BigDecimal;
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

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.domain.custom.print.PrintRimborsoMissione;
import it.cnr.si.missioni.domain.custom.print.PrintRimborsoMissioneDettagli;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.object.Nazione;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.missioni.util.proxy.json.service.NazioneService;

@Service
public class PrintRimborsoMissioneService {
    private final Logger log = LoggerFactory.getLogger(PrintRimborsoMissioneService.class);

    @Autowired
    private PrintService printService;

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private NazioneService nazioneService;

    private PrintRimborsoMissione getPrintRimborsoMissione(RimborsoMissione rimborsoMissione, String currentLogin) throws AwesomeException, ComponentException {
		Account account = accountService.loadAccountFromRest(rimborsoMissione.getUid());
		Nazione nazione = nazioneService.loadNazione(rimborsoMissione.getNazione());
    	PrintRimborsoMissione printRimborsoMissione = new PrintRimborsoMissione();
    	printRimborsoMissione.setAnno(rimborsoMissione.getAnno());
    	printRimborsoMissione.setNumero(rimborsoMissione.getNumero());
    	printRimborsoMissione.setCodiceFiscaleRich(account.getCodiceFiscale());
    	printRimborsoMissione.setComuneResidenzaRich(rimborsoMissione.getComuneResidenzaRich());
    	if (account.getDataNascita() != null){
    		Date dataNas = DateUtils.parseDate(account.getDataNascita().substring(0, 10),"yyyy-MM-dd");
    		printRimborsoMissione.setDataDiNascitaRich(DateUtils.getDateAsString(dataNas, DateUtils.PATTERN_DATE));
    	}
    	printRimborsoMissione.setDatoreLavoroRich(rimborsoMissione.getDatoreLavoroRich());
    	printRimborsoMissione.setDomicilioFiscaleRich(Utility.nvl(rimborsoMissione.getDomicilioFiscaleRich()));
    	printRimborsoMissione.setIndirizzoResidenzaRich(rimborsoMissione.getIndirizzoResidenzaRich());
    	printRimborsoMissione.setLivelloRich(rimborsoMissione.getLivelloRich() == null ? "" : rimborsoMissione.getLivelloRich().toString());
    	printRimborsoMissione.setLuogoDiNascitaRich(account.getComuneNascita());
    	printRimborsoMissione.setMatricolaRich(account.getMatricola());
    	printRimborsoMissione.setCognomeRich(account.getCognome());
    	printRimborsoMissione.setNomeRich(account.getNome());
    	printRimborsoMissione.setQualificaRich(rimborsoMissione.getQualificaRich() == null ? "" : rimborsoMissione.getQualificaRich());

    	printRimborsoMissione.setDataFineMissione(DateUtils.getDateAsString(rimborsoMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC));
    	printRimborsoMissione.setDataInizioMissione(DateUtils.getDateAsString(rimborsoMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC));
    	printRimborsoMissione.setDataInserimento(DateUtils.getDateAsString(rimborsoMissione.getDataInserimento(), DateUtils.PATTERN_DATE));
    	printRimborsoMissione.setOggetto(rimborsoMissione.getOggetto());
    	printRimborsoMissione.setDestinazione(rimborsoMissione.getDestinazione());
    	printRimborsoMissione.setAltreSpese(rimborsoMissione.getAltreSpeseAntDescrizione());
    	printRimborsoMissione.setAltreSpeseImporto(Utility.numberFormat(rimborsoMissione.getAltreSpeseAntImporto()));
    	if (rimborsoMissione.getAnticipoAnnoMandato() != null){
        	printRimborsoMissione.setAnnoMandato(rimborsoMissione.getAnticipoAnnoMandato().toString());
    	} else {
        	printRimborsoMissione.setAnnoMandato("");
    	}
    	printRimborsoMissione.setAnticipo(rimborsoMissione.getAnticipoRicevuto());
    	if (rimborsoMissione.getCdTerzoSigla() != null){
        	printRimborsoMissione.setCdTerzo(rimborsoMissione.getCdTerzoSigla().toString());
    	} else {
        	printRimborsoMissione.setCdTerzo("");
    	}
    	if (rimborsoMissione.getDataFineEstero() != null){
        	printRimborsoMissione.setDataFineEstero(DateUtils.getDateAsString(rimborsoMissione.getDataFineEstero(), DateUtils.PATTERN_DATETIME_NO_SEC));
    	} else {
    		printRimborsoMissione.setDataFineEstero("");
    	}
    	if (rimborsoMissione.getDataInizioEstero() != null){
        	printRimborsoMissione.setDataInizioEstero(DateUtils.getDateAsString(rimborsoMissione.getDataInizioEstero(), DateUtils.PATTERN_DATETIME_NO_SEC));
    	} else {
    		printRimborsoMissione.setDataInizioEstero("");
    	}
    	printRimborsoMissione.setDataInizioMissione(DateUtils.getDateAsString(rimborsoMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC));
    	printRimborsoMissione.setDataFineMissione(DateUtils.getDateAsString(rimborsoMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC));
    	if (rimborsoMissione.getIban() != null){
        	printRimborsoMissione.setIban(rimborsoMissione.getIban());
    	} else {
        	printRimborsoMissione.setIban("");
    	}
    	if (rimborsoMissione.getAnticipoImporto() != null){
        	printRimborsoMissione.setImportoMandato(Utility.numberFormat(rimborsoMissione.getAnticipoImporto()));
    	} else {
    		printRimborsoMissione.setImportoMandato("");
    	}
    	printRimborsoMissione.setItaliaEstero(rimborsoMissione.decodeTipoMissione());
    	printRimborsoMissione.setTipoMissione(rimborsoMissione.decodeTipoMissione());
    	printRimborsoMissione.setModpag(rimborsoMissione.getModpag());
    	if (rimborsoMissione.isMissioneEstera()){
        	printRimborsoMissione.setNazione(nazione.getDs_nazione());
    	} else {
    		printRimborsoMissione.setNazione("");
    	}
    	if (rimborsoMissione.getAnticipoNumeroMandato() != null){
        	printRimborsoMissione.setNumeroMandato(rimborsoMissione.getAnticipoNumeroMandato().toString());
    	} else {
        	printRimborsoMissione.setNumeroMandato("");
    	}
    	printRimborsoMissione.setSpeseTerzi(rimborsoMissione.getSpeseTerziRicevute());
    	if (rimborsoMissione.getSpeseTerziImporto() != null){
        	printRimborsoMissione.setSpeseTerziImporto(Utility.numberFormat(rimborsoMissione.getSpeseTerziImporto()));
    	} else {
    		printRimborsoMissione.setSpeseTerziImporto("");
    	}
		printRimborsoMissione.setTrattamento(rimborsoMissione.decodeTrattamento());
		BigDecimal totMissione = BigDecimal.ZERO;
		if (rimborsoMissione.getRimborsoMissioneDettagli() != null){
			List<PrintRimborsoMissioneDettagli> listDettagliPrint = new ArrayList<PrintRimborsoMissioneDettagli>();
	    	for (Iterator<RimborsoMissioneDettagli> iterator = rimborsoMissione.getRimborsoMissioneDettagli().iterator(); iterator.hasNext();){
	    		RimborsoMissioneDettagli dettagli = iterator.next();
	    		PrintRimborsoMissioneDettagli dettagliPrint = new PrintRimborsoMissioneDettagli();
	    		totMissione = totMissione.add(dettagli.getImportoEuro());
	    		String dsSpesa = "";
	    		if (dettagli.getDsSpesa() != null){
	    			dsSpesa = dettagli.getDsTiSpesa() + " - "+dettagli.getDsSpesa();
	    		} else {
	    			dsSpesa = dettagli.getDsTiSpesa();
	    		}
	    		dettagliPrint.setDsSpesa(dsSpesa);
	    		dettagliPrint.setData(DateUtils.getDateAsString(dettagli.getDataSpesa(),DateUtils.PATTERN_DATE));
	    		if (dettagli.getImportoEuro() != null){
		    		dettagliPrint.setImporto(Utility.numberFormat(dettagli.getImportoEuro()));
	    		} else {
	    			dettagliPrint.setImporto("");
	    		}
	    		if (dettagli.getKmPercorsi() != null){
	    			dettagliPrint.setKmPercorsi(dettagli.getKmPercorsi().toString());
	    		} else {
	    			dettagliPrint.setKmPercorsi("");
	    		}
	    		listDettagliPrint.add(dettagliPrint);
	    	}
	    	printRimborsoMissione.setPrintDettagliSpeseRimborsoMissione(listDettagliPrint);
		}
		printRimborsoMissione.setTotMissione(Utility.numberFormat(totMissione));
		return printRimborsoMissione; 
    }

	public byte[] printRimborsoMissione(RimborsoMissione rimborsoMissione, String currentLogin) throws AwesomeException, ComponentException {
		String myJson = createJsonPrintRimborsoMissione(rimborsoMissione, currentLogin);
		Map<String, String> mapSubReport = new HashMap<String, String>();
		mapSubReport.put("SUBREPORT_DETTAGLI","rimborso_missione_dettagli_sub.jasper");
		return printService.print(myJson, "RimborsoMissione.jasper", mapSubReport);
	}

	public String createJsonPrintRimborsoMissione(RimborsoMissione rimborsoMissione, String currentLogin) throws ComponentException {
		PrintRimborsoMissione printRimborsoMissione = getPrintRimborsoMissione(rimborsoMissione, currentLogin);
		return printService.createJsonForPrint(printRimborsoMissione);
	}

}
