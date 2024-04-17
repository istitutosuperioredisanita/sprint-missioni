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
import it.cnr.si.missioni.cmis.flows.happySign.AutorizzazioneRimborsoService;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.service.HappySignURLCondition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Conditional(HappySignURLCondition.class)
public class CMISRimborsoMissioneHappySign extends AbstractCMISRimborsoMissioneService {
    private static final Log logger = LogFactory.getLog(CMISRimborsoMissioneHappySign.class);
    @Autowired
    AutorizzazioneRimborsoService autorizzazioneRimborsoService;

    @Override
    void sendRimborsoOrdineMissioneToSign(RimborsoMissione rimborsoMissione, CMISRimborsoMissione cmisRimborsoMissione, StorageObject documento, List<StorageObject> allegati, List<StorageObject> giustificativi) {
        try {

            if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno()).getTipoMailDopoOrdine(), "N").equals("C")) {
                rimborsoMissioneService.popolaCoda(rimborsoMissione);
            } else {
                List<StorageObject> signAllegati = new ArrayList<>();
                if (giustificativi != null)
                    signAllegati.addAll(giustificativi);
                if (allegati != null)
                    signAllegati.addAll(allegati);
                String idFlusso = autorizzazioneRimborsoService.sendAutorizzazione(rimborsoMissione, documento, signAllegati);

                if (!StringUtils.isEmpty(idFlusso)) {
                    rimborsoMissione.setIdFlusso(idFlusso);

                }
                rimborsoMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
            }
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
        }
        logger.info("sendOrdineMissioneToSign");
    }

    @Override
    public void annullaFlusso(RimborsoMissione rimborsoMissione) throws AwesomeException {

    }
}
