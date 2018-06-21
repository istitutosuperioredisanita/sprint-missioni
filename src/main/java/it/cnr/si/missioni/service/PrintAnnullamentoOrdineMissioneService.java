package it.cnr.si.missioni.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.print.PrintOrdineMissione;
import it.cnr.si.missioni.util.Costanti;

@Service
public class PrintAnnullamentoOrdineMissioneService {
    private final Logger log = LoggerFactory.getLogger(PrintAnnullamentoOrdineMissioneService.class);

	@Autowired	
	private Environment env;

	private RelaxedPropertyResolver propertyResolver;

    @Autowired
    private PrintService printService;

    @Autowired
    private PrintOrdineMissioneService printOrdineMissioneService;

	public byte[] printOrdineMissione(AnnullamentoOrdineMissione annullamento, String currentLogin) throws AwesomeException, ComponentException {
		String myJson = createJsonPrintOrdineMissione(annullamento, currentLogin);
    	this.propertyResolver = new RelaxedPropertyResolver(env, "spring.print.");
    	String nomeStampa = "";
    	if (propertyResolver != null && propertyResolver.getProperty(Costanti.NOME_STAMPA_ORDINE) != null) {
    		nomeStampa = propertyResolver.getProperty(Costanti.NOME_STAMPA_ORDINE);
    	} else {
    		throw new ComponentException("Configurare il nome stampa dell'ordine");
    	}
		return printService.print(myJson, nomeStampa);
	}

	public String createJsonPrintOrdineMissione(AnnullamentoOrdineMissione annullamento, String currentLogin) throws ComponentException {
		PrintOrdineMissione printOrdineMissione = printOrdineMissioneService.getPrintOrdineMissione(annullamento.getOrdineMissione(), currentLogin);
		printOrdineMissione.setTipo("A");
		printOrdineMissione.setMotivoAnnullamento(annullamento.getMotivoAnnullamento());
		return printService.createJsonForPrint(printOrdineMissione);
	}

}
