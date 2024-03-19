/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.missioni.domain.custom.print.PrintOrdineMissioneAutoNoleggio;
import it.cnr.si.missioni.domain.custom.print.PrintOrdineMissioneAutoNoleggio;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
//TODO
@Service
public class PrintOrdineMissioneAutoNoleggioService {
    private final Logger log = LoggerFactory.getLogger(PrintOrdineMissioneAutoNoleggioService.class);

    @Autowired
    private Environment env;

    @Autowired
    private PrintService printService;

    @Autowired
    private AccountService accountService;

    private PrintOrdineMissioneAutoNoleggio getPrintOrdineMissioneAutoNoleggio(OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio, String currentLogin) throws AwesomeException, ComponentException {
        OrdineMissione ordineMissione = ordineMissioneAutoNoleggio.getOrdineMissione();
        Account account = accountService.loadAccountFromUsername(ordineMissione.getUid());
        PrintOrdineMissioneAutoNoleggio printOrdineMissioneAutoNoleggio = new PrintOrdineMissioneAutoNoleggio();
        printOrdineMissioneAutoNoleggio.setAnno(ordineMissione.getAnno());
        printOrdineMissioneAutoNoleggio.setNumero(ordineMissione.getNumero());
        printOrdineMissioneAutoNoleggio.setCodiceFiscaleRich(account.getCodice_fiscale());
        printOrdineMissioneAutoNoleggio.setComuneResidenzaRich(Utility.nvl(ordineMissione.getComuneResidenzaRich()));
        if (account.getData_nascita() != null) {
            Date dataNas = DateUtils.parseDate(account.getData_nascita().substring(0, 10), "yyyy-MM-dd");
            printOrdineMissioneAutoNoleggio.setDataDiNascitaRich(DateUtils.getDateAsString(dataNas, DateUtils.PATTERN_DATE));
        }
        printOrdineMissioneAutoNoleggio.setDatoreLavoroRich(ordineMissione.getDatoreLavoroRich());
        printOrdineMissioneAutoNoleggio.setDomicilioFiscaleRich(Utility.nvl(ordineMissione.getDomicilioFiscaleRich()));
        printOrdineMissioneAutoNoleggio.setIndirizzoResidenzaRich(Utility.nvl(ordineMissione.getIndirizzoResidenzaRich()));
        printOrdineMissioneAutoNoleggio.setLivelloRich(ordineMissione.getLivelloRich() == null ? "" : ordineMissione.getLivelloRich());
        printOrdineMissioneAutoNoleggio.setLuogoDiNascitaRich(account.getComune_nascita());
        printOrdineMissioneAutoNoleggio.setMatricolaRich(account.getMatricola() != null ? account.getMatricola().toString() : "");
        printOrdineMissioneAutoNoleggio.setCognomeRich(account.getCognome());
        printOrdineMissioneAutoNoleggio.setNomeRich(account.getNome());
        printOrdineMissioneAutoNoleggio.setQualificaRich(ordineMissione.getQualificaRich() == null ? "" : ordineMissione.getQualificaRich());

        printOrdineMissioneAutoNoleggio.setDataFineMissione(DateUtils.getDateAsString(ordineMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC));
        printOrdineMissioneAutoNoleggio.setDataInizioMissione(DateUtils.getDateAsString(ordineMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC));
        printOrdineMissioneAutoNoleggio.setDataInserimento(DateUtils.getDateAsString(ordineMissione.getDataInserimento(), DateUtils.PATTERN_DATE));
        printOrdineMissioneAutoNoleggio.setOggetto(ordineMissione.getOggetto());
        printOrdineMissioneAutoNoleggio.setDestinazione(ordineMissione.getDestinazione());
        printOrdineMissioneAutoNoleggio.setStato(ordineMissione.getDecodeStato());

        printOrdineMissioneAutoNoleggio.setMotivataEccezionalita(Utility.nvl(ordineMissioneAutoNoleggio.getMotivataEccezionalita(), "N").equals("N") ? "" : ordineMissioneAutoNoleggio.getMotivataEccezionalita());
        printOrdineMissioneAutoNoleggio.setEsigenzeServizio(Utility.nvl(ordineMissioneAutoNoleggio.getEsigenzeServizio(), "N").equals("N") ? "" : ordineMissioneAutoNoleggio.getEsigenzeServizio());
        printOrdineMissioneAutoNoleggio.setNote(Utility.nvl(ordineMissioneAutoNoleggio.getNote()));

        if (ordineMissioneAutoNoleggio.getListSpostamenti() != null) {
            List<SpostamentiAutoNoleggio> listSpostamentiPrint = new ArrayList<>();
            for (Iterator<SpostamentiAutoNoleggio> iterator = ordineMissioneAutoNoleggio.getListSpostamenti().iterator(); iterator.hasNext(); ) {
                SpostamentiAutoNoleggio spostamenti = iterator.next();
                SpostamentiAutoNoleggio spostamentiPrint = new SpostamentiAutoNoleggio();
                spostamentiPrint.setPercorsoDa(spostamenti.getPercorsoDa());
                spostamentiPrint.setPercorsoA(spostamenti.getPercorsoA());
                listSpostamentiPrint.add(spostamentiPrint);
            }
            printOrdineMissioneAutoNoleggio.setSpostamenti(listSpostamentiPrint);
        }
        return printOrdineMissioneAutoNoleggio;
    }

    public byte[] printOrdineMissioneAutoNoleggio(OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio, String currentLogin) throws AwesomeException, ComponentException {
        String myJson = createJsonPrintOrdineMissioneAutoNoleggio(ordineMissioneAutoNoleggio, currentLogin);
        String nomeStampa = "";
        if (env != null && env.getProperty("spring.print." + Costanti.NOME_STAMPA_AUTO_NOLEGGIO) != null) {
            nomeStampa = env.getProperty("spring.print." + Costanti.NOME_STAMPA_AUTO_NOLEGGIO);
        } else {
            throw new ComponentException("Configurare il nome stampa del AutoNoleggio");
        }
        return printService.print(myJson, nomeStampa, ordineMissioneAutoNoleggio.getId());
    }

    public String createJsonPrintOrdineMissioneAutoNoleggio(OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio, String currentLogin) throws ComponentException {
        PrintOrdineMissioneAutoNoleggio printOrdineMissioneAutoNoleggio = getPrintOrdineMissioneAutoNoleggio(ordineMissioneAutoNoleggio, currentLogin);
        return printService.createJsonForPrint(printOrdineMissioneAutoNoleggio);
    }

}
