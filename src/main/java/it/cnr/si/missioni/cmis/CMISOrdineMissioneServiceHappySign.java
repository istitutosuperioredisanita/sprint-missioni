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

package it.cnr.si.missioni.cmis;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.flows.happySign.AutorizzazioneService;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAnticipo;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;
import it.iss.si.dto.happysign.response.UploadToComplexResponse;
import it.iss.si.service.HappySignURLCondition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;


@Service
@Conditional(HappySignURLCondition.class)
public  class CMISOrdineMissioneServiceHappySign extends AbstractCMISOrdineMissioneService{

    @Autowired
    AutorizzazioneService autorizzazioneService;
    private static final Log logger = LogFactory.getLog(CMISOrdineMissioneServiceHappySign.class);
    public void avviaFlusso(AnnullamentoOrdineMissione annullamento) {
        String username = securityService.getCurrentUserLogin();
        byte[] stampa = printAnnullamentoOrdineMissioneService.printOrdineMissione(annullamento, username);
        CMISOrdineMissione cmisOrdineMissione = create(annullamento.getOrdineMissione(), annullamento.getAnno());
        StorageObject so = salvaStampaAnnullamentoOrdineMissioneSuCMIS(stampa, annullamento);

        logger.info("da implementare Annullamento");
    }

    @Override
    public Boolean isActiveSignFlow() {
        return true;
    }

    @Override
    public void annullaFlusso(OrdineMissione ordineMissione) {

    }

    protected void sendOrdineMissioneToSign(OrdineMissione ordineMissione, CMISOrdineMissione cmisOrdineMissione, StorageObject documentoOrdineMissione, OrdineMissioneAnticipo anticipo, StorageObject documentoAnticipo, List<StorageObject> allegati, StorageObject documentoAutoPropria) {

        try {

                if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), ordineMissione.getAnno()).getTipoMailDopoOrdine(), "N").equals("C")) {
                    ordineMissioneService.popolaCoda(ordineMissione);
                } else {
                    UploadToComplexResponse response = autorizzazioneService.sendAutorizzazione(ordineMissione,documentoOrdineMissione);
                    if ( response.getStatus()!=0)
                        throw  new Exception(response.getReason());
                    String idFlusso = response.getListiddocument().get(0).getUuid();
                    if (StringUtils.isEmpty(ordineMissione.getIdFlusso())) {
                        ordineMissione.setIdFlusso(idFlusso);
                        if (anticipo != null) {
                            anticipo.setIdFlusso(idFlusso);
                        }
                    }
                    ordineMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);

                }
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di preparazione del flusso documentale. Errore: " + e);
        }
        logger.info("sendOrdineMissioneToSign");

    }

}
