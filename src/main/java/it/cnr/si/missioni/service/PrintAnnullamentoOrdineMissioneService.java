package it.cnr.si.missioni.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.print.PrintOrdineMissione;

@Service
public class PrintAnnullamentoOrdineMissioneService {
    private final Logger log = LoggerFactory.getLogger(PrintAnnullamentoOrdineMissioneService.class);

    @Autowired
    private PrintService printService;

    @Autowired
    private PrintOrdineMissioneService printOrdineMissioneService;

	public byte[] printOrdineMissione(AnnullamentoOrdineMissione annullamento, String currentLogin) throws AwesomeException, ComponentException {
		String myJson = createJsonPrintOrdineMissione(annullamento, currentLogin);
		return printService.print(myJson, "OrdineMissione.jasper");
	}

	public String createJsonPrintOrdineMissione(AnnullamentoOrdineMissione annullamento, String currentLogin) throws ComponentException {
		PrintOrdineMissione printOrdineMissione = printOrdineMissioneService.getPrintOrdineMissione(annullamento.getOrdineMissione(), currentLogin);
		printOrdineMissione.setTipo("A");
		printOrdineMissione.setMotivoAnnullamento(annullamento.getMotivoAnnullamento());
		return printService.createJsonForPrint(printOrdineMissione);
	}

}
