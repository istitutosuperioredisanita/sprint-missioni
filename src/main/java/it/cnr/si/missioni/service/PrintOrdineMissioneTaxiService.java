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
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneTaxi;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneTaxi;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiTaxi;
import it.cnr.si.missioni.domain.custom.print.PrintOrdineMissioneTaxi;
import it.cnr.si.missioni.domain.custom.print.PrintOrdineMissioneTaxi;
import it.cnr.si.missioni.domain.custom.print.Spostamenti;
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

@Service
public class PrintOrdineMissioneTaxiService {
    private final Logger log = LoggerFactory.getLogger(PrintOrdineMissioneTaxiService.class);

    @Autowired
    private Environment env;

    @Autowired
    private PrintService printService;

    @Autowired
    private AccountService accountService;

    //    private PrintOrdineMissione getPrintOrdineMissione(OrdineMissione ordineMissione) throws AwesomeException, ComponentException {
    private PrintOrdineMissioneTaxi getPrintOrdineMissioneTaxi(OrdineMissioneTaxi ordineMissioneTaxi, String currentLogin) throws AwesomeException, ComponentException {
        OrdineMissione ordineMissione = ordineMissioneTaxi.getOrdineMissione();
        Account account = accountService.loadAccountFromUsername(ordineMissione.getUid());
        PrintOrdineMissioneTaxi printOrdineMissioneTaxi = new PrintOrdineMissioneTaxi();
        printOrdineMissioneTaxi.setAnno(ordineMissione.getAnno());
        printOrdineMissioneTaxi.setNumero(ordineMissione.getNumero());
        printOrdineMissioneTaxi.setCodiceFiscaleRich(account.getCodice_fiscale());
        printOrdineMissioneTaxi.setComuneResidenzaRich(Utility.nvl(ordineMissione.getComuneResidenzaRich()));
        if (account.getData_nascita() != null) {
            Date dataNas = DateUtils.parseDate(account.getData_nascita().substring(0, 10), "yyyy-MM-dd");
            printOrdineMissioneTaxi.setDataDiNascitaRich(DateUtils.getDateAsString(dataNas, DateUtils.PATTERN_DATE));
        }
        printOrdineMissioneTaxi.setDatoreLavoroRich(ordineMissione.getDatoreLavoroRich());
        printOrdineMissioneTaxi.setDomicilioFiscaleRich(Utility.nvl(ordineMissione.getDomicilioFiscaleRich()));
        printOrdineMissioneTaxi.setIndirizzoResidenzaRich(Utility.nvl(ordineMissione.getIndirizzoResidenzaRich()));
        printOrdineMissioneTaxi.setLivelloRich(ordineMissione.getLivelloRich() == null ? "" : ordineMissione.getLivelloRich());
        printOrdineMissioneTaxi.setLuogoDiNascitaRich(account.getComune_nascita());
        printOrdineMissioneTaxi.setMatricolaRich(account.getMatricola() != null ? account.getMatricola().toString() : "");
        printOrdineMissioneTaxi.setCognomeRich(account.getCognome());
        printOrdineMissioneTaxi.setNomeRich(account.getNome());
        printOrdineMissioneTaxi.setQualificaRich(ordineMissione.getQualificaRich() == null ? "" : ordineMissione.getQualificaRich());

        printOrdineMissioneTaxi.setDataFineMissione(DateUtils.getDateAsString(ordineMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC));
        printOrdineMissioneTaxi.setDataInizioMissione(DateUtils.getDateAsString(ordineMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC));
        printOrdineMissioneTaxi.setDataInserimento(DateUtils.getDateAsString(ordineMissione.getDataInserimento(), DateUtils.PATTERN_DATE));
        printOrdineMissioneTaxi.setOggetto(ordineMissione.getOggetto());
        printOrdineMissioneTaxi.setDestinazione(ordineMissione.getDestinazione());
        printOrdineMissioneTaxi.setStato(ordineMissione.getDecodeStato());

        printOrdineMissioneTaxi.setMancanzaAssMezzi(Utility.nvl(ordineMissioneTaxi.getMancanzaAssMezzi(), "N").equals("N") ? "" : ordineMissioneTaxi.getMancanzaAssMezzi());
        printOrdineMissioneTaxi.setMancanzaMezzi(Utility.nvl(ordineMissioneTaxi.getMancanzaMezzi(), "N").equals("N") ? "" : ordineMissioneTaxi.getMancanzaMezzi());
        printOrdineMissioneTaxi.setMotiviHandicap(Utility.nvl(ordineMissioneTaxi.getMotiviHandicap(), "N").equals("N") ? "" : ordineMissioneTaxi.getMotiviHandicap());
        printOrdineMissioneTaxi.setTrasportoMateriali(Utility.nvl(ordineMissioneTaxi.getTrasportoMateriali(), "N").equals("N") ? "" : ordineMissioneTaxi.getTrasportoMateriali());
        printOrdineMissioneTaxi.setUtilizzoAltriMotivi(Utility.nvl(ordineMissioneTaxi.getUtilizzoAltriMotivi()));

        if (ordineMissioneTaxi.getListSpostamenti() != null) {
            List<SpostamentiTaxi> listSpostamentiPrint = new ArrayList<>();
            for (Iterator<SpostamentiTaxi> iterator = ordineMissioneTaxi.getListSpostamenti().iterator(); iterator.hasNext(); ) {
                SpostamentiTaxi spostamenti = iterator.next();
                SpostamentiTaxi spostamentiPrint = new SpostamentiTaxi();
                spostamentiPrint.setPercorsoDa(spostamenti.getPercorsoDa());
                spostamentiPrint.setPercorsoA(spostamenti.getPercorsoA());
                listSpostamentiPrint.add(spostamentiPrint);
            }
            printOrdineMissioneTaxi.setSpostamenti(listSpostamentiPrint);
        }
        return printOrdineMissioneTaxi;
    }

    public byte[] printOrdineMissioneTaxi(OrdineMissioneTaxi ordineMissioneTaxi, String currentLogin) throws AwesomeException, ComponentException {
        String myJson = createJsonPrintOrdineMissioneTaxi(ordineMissioneTaxi, currentLogin);
        String nomeStampa = "";
        if (env != null && env.getProperty("spring.print." + Costanti.NOME_STAMPA_TAXI) != null) {
            nomeStampa = env.getProperty("spring.print." + Costanti.NOME_STAMPA_TAXI);
        } else {
            throw new ComponentException("Configurare il nome stampa del taxi");
        }
        return printService.print(myJson, nomeStampa, ordineMissioneTaxi.getId());
    }

    public String createJsonPrintOrdineMissioneTaxi(OrdineMissioneTaxi ordineMissioneTaxi, String currentLogin) throws ComponentException {
        PrintOrdineMissioneTaxi printOrdineMissioneTaxi = getPrintOrdineMissioneTaxi(ordineMissioneTaxi, currentLogin);
        return printService.createJsonForPrint(printOrdineMissioneTaxi);
    }

}
