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
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiAutoPropria;
import it.cnr.si.missioni.domain.custom.print.PrintOrdineMissioneAutoPropria;
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
public class PrintOrdineMissioneAutoPropriaService {
    private final Logger log = LoggerFactory.getLogger(PrintOrdineMissioneAutoPropriaService.class);

    @Autowired
    private Environment env;

    @Autowired
    private PrintService printService;

    @Autowired
    private AccountService accountService;

    //    private PrintOrdineMissione getPrintOrdineMissione(OrdineMissione ordineMissione) throws AwesomeException, ComponentException {
    private PrintOrdineMissioneAutoPropria getPrintOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria, String currentLogin) throws AwesomeException, ComponentException {
        OrdineMissione ordineMissione = ordineMissioneAutoPropria.getOrdineMissione();
        Account account = accountService.loadAccountFromUsername(ordineMissione.getUid());
        PrintOrdineMissioneAutoPropria printOrdineMissioneAutoPropria = new PrintOrdineMissioneAutoPropria();
        printOrdineMissioneAutoPropria.setAnno(ordineMissione.getAnno());
        printOrdineMissioneAutoPropria.setNumero(ordineMissione.getNumero());
        printOrdineMissioneAutoPropria.setCodiceFiscaleRich(account.getCodice_fiscale());
        printOrdineMissioneAutoPropria.setComuneResidenzaRich(Utility.nvl(ordineMissione.getComuneResidenzaRich()));
        if (account.getData_nascita() != null) {
            Date dataNas = DateUtils.parseDate(account.getData_nascita().substring(0, 10), "yyyy-MM-dd");
            printOrdineMissioneAutoPropria.setDataDiNascitaRich(DateUtils.getDateAsString(dataNas, DateUtils.PATTERN_DATE));
        }
        printOrdineMissioneAutoPropria.setDatoreLavoroRich(ordineMissione.getDatoreLavoroRich());
        printOrdineMissioneAutoPropria.setDomicilioFiscaleRich(Utility.nvl(ordineMissione.getDomicilioFiscaleRich()));
        printOrdineMissioneAutoPropria.setIndirizzoResidenzaRich(Utility.nvl(ordineMissione.getIndirizzoResidenzaRich()));
        printOrdineMissioneAutoPropria.setLivelloRich(ordineMissione.getLivelloRich() == null ? "" : ordineMissione.getLivelloRich());
        printOrdineMissioneAutoPropria.setLuogoDiNascitaRich(account.getComune_nascita());
        printOrdineMissioneAutoPropria.setMatricolaRich(account.getMatricola() != null ? account.getMatricola().toString() : "");
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
        printOrdineMissioneAutoPropria.setMotiviIspettivi(Utility.nvl(ordineMissioneAutoPropria.getUtilizzoMotiviIspettivi(), "N").equals("N") ? "" : ordineMissioneAutoPropria.getUtilizzoMotiviIspettivi());
        printOrdineMissioneAutoPropria.setMotiviSediDisagiate(Utility.nvl(ordineMissioneAutoPropria.getUtilizzoMotiviSediDisagiate(), "N").equals("N") ? "" : ordineMissioneAutoPropria.getUtilizzoMotiviSediDisagiate());
        printOrdineMissioneAutoPropria.setMotiviUrgenza(Utility.nvl(ordineMissioneAutoPropria.getUtilizzoMotiviUrgenza(), "N").equals("N") ? "" : ordineMissioneAutoPropria.getUtilizzoMotiviUrgenza());
        printOrdineMissioneAutoPropria.setMotiviTrasporto(Utility.nvl(ordineMissioneAutoPropria.getUtilizzoMotiviTrasporto(), "N").equals("N") ? "" : ordineMissioneAutoPropria.getUtilizzoMotiviTrasporto());
        printOrdineMissioneAutoPropria.setAltriMotivi(Utility.nvl(ordineMissioneAutoPropria.getUtilizzoAltriMotivi()));

        if (ordineMissioneAutoPropria.getListSpostamenti() != null) {
            List<Spostamenti> listSpostamentiPrint = new ArrayList<Spostamenti>();
            for (Iterator<SpostamentiAutoPropria> iterator = ordineMissioneAutoPropria.getListSpostamenti().iterator(); iterator.hasNext(); ) {
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
        String nomeStampa = "";
        if (env != null && env.getProperty("spring.print." + Costanti.NOME_STAMPA_AUTO_PROPRIA) != null) {
            nomeStampa = env.getProperty("spring.print." + Costanti.NOME_STAMPA_AUTO_PROPRIA);
        } else {
            throw new ComponentException("Configurare il nome stampa dell'auto propria");
        }
        return printService.print(myJson, nomeStampa, ordineMissioneAutoPropria.getId());
    }

    public String createJsonPrintOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria, String currentLogin) throws ComponentException {
        PrintOrdineMissioneAutoPropria PrintOrdineMissioneAutoPropria = getPrintOrdineMissioneAutoPropria(ordineMissioneAutoPropria, currentLogin);
        return printService.createJsonForPrint(PrintOrdineMissioneAutoPropria);
    }

}
