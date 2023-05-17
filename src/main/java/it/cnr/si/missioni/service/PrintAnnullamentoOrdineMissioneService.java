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
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.print.PrintOrdineMissione;
import it.cnr.si.missioni.util.Costanti;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class PrintAnnullamentoOrdineMissioneService {
    private final Logger log = LoggerFactory.getLogger(PrintAnnullamentoOrdineMissioneService.class);

    @Autowired
    private Environment env;

    @Autowired
    private PrintService printService;

    @Autowired
    private PrintOrdineMissioneService printOrdineMissioneService;

    public byte[] printOrdineMissione(AnnullamentoOrdineMissione annullamento, String currentLogin) throws AwesomeException, ComponentException {
        String myJson = createJsonPrintOrdineMissione(annullamento, currentLogin);
        String nomeStampa = "";
        if (env != null && env.getProperty("spring.print." + Costanti.NOME_STAMPA_ORDINE) != null) {
            nomeStampa = env.getProperty("spring.print." + Costanti.NOME_STAMPA_ORDINE);
        } else {
            throw new ComponentException("Configurare il nome stampa dell'ordine");
        }
        return printService.print(myJson, nomeStampa, annullamento.getId());
    }

    public String createJsonPrintOrdineMissione(AnnullamentoOrdineMissione annullamento, String currentLogin) throws ComponentException {
        PrintOrdineMissione printOrdineMissione = printOrdineMissioneService.getPrintOrdineMissione(annullamento.getOrdineMissione(), currentLogin);
        printOrdineMissione.setTipo("A");
        printOrdineMissione.setMotivoAnnullamento(annullamento.getMotivoAnnullamento());
        return printService.createJsonForPrint(printOrdineMissione);
    }

}
