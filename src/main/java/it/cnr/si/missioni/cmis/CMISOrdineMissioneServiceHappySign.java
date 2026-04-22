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
import it.cnr.si.missioni.cmis.flows.happySign.AutorizzazioneAnnulloService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Conditional(HappySignURLCondition.class)
public  class CMISOrdineMissioneServiceHappySign extends AbstractCMISOrdineMissioneService{

    @Autowired
    AutorizzazioneService autorizzazioneService;

    @Autowired
    AutorizzazioneAnnulloService autorizzazioneAnnulloService;
    private static final Log logger = LogFactory.getLog(CMISOrdineMissioneServiceHappySign.class);
    protected void sendAnnullamentoOrdineMissioneToSign(AnnullamentoOrdineMissione annullamentoOrdineMissione, CMISOrdineMissione cmisOrdineMissione,
                                                                  Map<String, StorageObject> mapDocumentiAnnulloMissione,
                                                                  List<StorageObject> allegati){

        try {
            if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(annullamentoOrdineMissione.getOrdineMissione().getUoSpesa(),
                    annullamentoOrdineMissione.getOrdineMissione().getAnno()).getTipoMailDopoOrdine(), "N").equals("C")) {
                annullamentoOrdineMissioneService.popolaCoda(annullamentoOrdineMissione);
            } else {

                String idFlusso = autorizzazioneAnnulloService.sendAutorizzazione(annullamentoOrdineMissione, mapDocumentiAnnulloMissione.get(Costanti.DOCUMENTO_ANNULLAMENTO_MISSIONE_KEY), getAllAllegati( null,allegati,true));

                if (!StringUtils.isEmpty(idFlusso)) {
                    annullamentoOrdineMissione.setIdFlusso(idFlusso);

                }
                annullamentoOrdineMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
            }
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di preparazione del flusso documentale. Errore: " + e);
        }
        logger.info("sendOrdineMissioneToSign");

    }

    @Override
    public Boolean isActiveSignFlow() {
        return true;
    }

    @Override
    public void annullaFlusso(OrdineMissione ordineMissione) {

    }

    //metodo riscrito con l'oggetto Map

    private List<StorageObject> getAllAllegati( Map<String, StorageObject> mapDocumentiMissione, List<StorageObject> allegati, boolean annullamento){
        List<StorageObject> allAllegati= new ArrayList<StorageObject>();
        if ( allegati!=null && allAllegati.size()>0)
            allAllegati.addAll(allegati);
        boolean addAllegato=false;
        if ( mapDocumentiMissione!=null && mapDocumentiMissione.size()>0) {
            for (String s : mapDocumentiMissione.keySet()) {
                addAllegato=true;
                if ( !annullamento && Costanti.DOCUMENTO_MISSIONE_KEY.equalsIgnoreCase(s))
                    addAllegato=false;
                if ( annullamento && Costanti.DOCUMENTO_ANNULLAMENTO_MISSIONE_KEY.equalsIgnoreCase(s))
                    addAllegato=false;
                if (addAllegato)
                    allAllegati.add(mapDocumentiMissione.get(s));
            }
        }
        return allAllegati;
    }

    protected void sendOrdineMissioneToSign(OrdineMissione ordineMissione, CMISOrdineMissione cmisOrdineMissione, Map<String, StorageObject> mapDocumentiMissione, List<StorageObject> allegati,OrdineMissioneAnticipo anticipo) {
        try {
            if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), ordineMissione.getAnno()).getTipoMailDopoOrdine(), "N").equals("C")) {
                ordineMissioneService.popolaCoda(ordineMissione);
            } else {

                String idFlusso = autorizzazioneService.sendAutorizzazione(ordineMissione, mapDocumentiMissione.get(Costanti.DOCUMENTO_MISSIONE_KEY), getAllAllegati( mapDocumentiMissione,allegati,false));

                if (!StringUtils.isEmpty(idFlusso)) {
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






/*
protected void sendOrdineMissioneToSign(OrdineMissione ordineMissione, CMISOrdineMissione cmisOrdineMissione, StorageObject documentoOrdineMissione, OrdineMissioneAnticipo anticipo, StorageObject documentoAnticipo, List<StorageObject> allegati, StorageObject documentoAutoPropria,StorageObject documentoTaxi) {

        try {

            if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), ordineMissione.getAnno()).getTipoMailDopoOrdine(), "N").equals("C")) {
                ordineMissioneService.popolaCoda(ordineMissione);
            } else {
                List<StorageObject> allegatiMissione= new ArrayList<StorageObject>();
                if ( anticipo!=null)
                    allegatiMissione.add( documentoAnticipo);
                if ( documentoAutoPropria!=null)
                    allegatiMissione.add( documentoAutoPropria);
                if ( documentoTaxi!=null)
                    allegatiMissione.add( documentoTaxi);
                String idFlusso = autorizzazioneService.sendAutorizzazione(ordineMissione,documentoOrdineMissione,allegatiMissione);

                if (!StringUtils.isEmpty(idFlusso)) {
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
    */

}