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

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.CodiciErrore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;


@Service
public class FlowService {
    private final Logger log = LoggerFactory.getLogger(FlowService.class);
    @Autowired
    OrdineMissioneService ordineMissioneService;
    @Autowired
    AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;
    @Autowired
    RimborsoMissioneService rimborsoMissioneService;
    @Autowired
    TaskExecutor taskExecutor;
    @Autowired(required = false)
    private MailService mailService;
    @Autowired
    private ComunicaMissioneSiglaService comunicaMissioneSiglaService;
    @Value("${spring.mail.messages.erroreLetturaFlussoOrdine.oggetto}")
    private String subjectErrorFlowsOrdine;

    @Value("${spring.mail.messages.erroreLetturaFlussoRimborso.oggetto}")
    private String subjectErrorFlowsRimborso;

    @Value("${spring.mail.messages.erroreLetturaFlussoAnnullamento.oggetto}")
    private String subjectErrorFlowsAnnullamento;

    @Value("${spring.mail.messages.erroreGenerico.oggetto}")
    private String subjectGenericError;

    @Autowired
    private CRUDComponentSession crudServiceBean;

    public void aggiornaMissioneFlows(FlowResult flowResult) {
        log.info(flowResult.toString());
        String errore = "";
        if (flowResult.getIdMissione() != null) {
            switch (flowResult.getTipologiaMissione()) {
                case FlowResult.TIPO_FLUSSO_ORDINE:
                    OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, Long.valueOf(flowResult.getIdMissione()));
                    if (ordineMissione != null) {
                        ordineMissioneService.aggiornaOrdineMissione(ordineMissione, flowResult);
                    } else {
                        errore = "L'ordine di missione con ID " + flowResult.getIdMissione() + " indicato dal flusso con ID " + flowResult.getProcessInstanceId() + " non è presente";
                        log.info(errore);
                        throw new AwesomeException(CodiciErrore.ERRGEN, errore);
                    }
                    break;
                case FlowResult.TIPO_FLUSSO_RIMBORSO:
                    RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById(RimborsoMissione.class, Long.valueOf(flowResult.getIdMissione()));
                    if (rimborsoMissione != null) {
                        final RimborsoMissione rimborsoMissioneDaComunicare = rimborsoMissioneService.aggiornaRimborsoMissione(rimborsoMissione, flowResult);
                        if (rimborsoMissioneDaComunicare != null) {
                            taskExecutor.execute(new Runnable() {
                                public void run() {
                                    comunicaMissioneSiglaService.comunicaRimborsoSigla(rimborsoMissioneDaComunicare.getId());
                                }
                            });
                        }
                    } else {
                        errore = "Il rimborso missione con ID " + flowResult.getIdMissione() + " indicato dal flusso con ID " + flowResult.getProcessInstanceId() + " non è presente";
                        log.info(errore);
                        throw new AwesomeException(CodiciErrore.ERRGEN, errore);
                    }
                    break;
                case FlowResult.TIPO_FLUSSO_REVOCA:
                    AnnullamentoOrdineMissione annullamento = (AnnullamentoOrdineMissione) crudServiceBean.findById(AnnullamentoOrdineMissione.class, Long.valueOf(flowResult.getIdMissione()));
                    if (annullamento != null) {
                        annullamentoOrdineMissioneService.aggiornaAnnullamentoOrdineMissione(annullamento, flowResult);
                    } else {
                        errore = "L'annullamento ordine di missione con ID " + flowResult.getIdMissione() + " indicato dal flusso con ID " + flowResult.getProcessInstanceId() + " non è presente";
                        log.info(errore);
                        throw new AwesomeException(CodiciErrore.ERRGEN, errore);
                    }
                    break;
            }

        } else {
            errore = "ID Missione non presente per l'id del flusso " + flowResult.getProcessInstanceId();
            log.info(errore);
            throw new AwesomeException(CodiciErrore.ERRGEN, errore);
        }
    }
}