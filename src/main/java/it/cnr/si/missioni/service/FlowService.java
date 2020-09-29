package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.cmis.ResultFlows;
import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.security.Principal;
import java.util.Optional;

@Service
public class FlowService {
    private final Logger log = LoggerFactory.getLogger(FlowService.class);

    @Autowired
    private MailService mailService;

    @Autowired
    OrdineMissioneService ordineMissioneService;

    @Autowired
    AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;

    @Autowired
    RimborsoMissioneService rimborsoMissioneService;

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

    public void aggiornaMissioneFlows(Principal principal, FlowResult flowResult) {
        log.info(flowResult.toString());
        String errore = "";
        try {
            if (flowResult.getIdMissione() != null){
                switch (flowResult.getTipoFlusso() ) {
                    case FlowResult.TIPO_FLUSSO_ORDINE:
                        OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, new Long(flowResult.getIdMissione()));
                        if (ordineMissione != null){
                            ordineMissioneService.aggiornaOrdineMissione(principal, ordineMissione, flowResult);
                        } else {
                            errore = "L'ordine di missione con ID "+flowResult.getIdMissione()+" indicato dal flusso con ID "+flowResult.getIdFlusso()+" non è presente";
                            log.info(errore);
                            mailService.sendEmailError(subjectErrorFlowsOrdine + this.toString(), errore, false, true);
                        }
                        break;
                    case FlowResult.TIPO_FLUSSO_RIMBORSO:
                        RimborsoMissione rimborsoMissione = (RimborsoMissione)crudServiceBean.findById(principal, RimborsoMissione.class, new Long(flowResult.getIdMissione()));
                        if (rimborsoMissione != null){
                            rimborsoMissioneService.aggiornaRimborsoMissione(principal, rimborsoMissione, flowResult);
                        } else {
                            errore = "Il rimborso missione con ID "+flowResult.getIdMissione()+" indicato dal flusso con ID "+flowResult.getIdFlusso()+" non è presente";
                            log.info(errore);
                            mailService.sendEmailError(subjectErrorFlowsRimborso + this.toString(), errore, false, true);
                        }
                        break;
                    case FlowResult.TIPO_FLUSSO_REVOCA:
                        AnnullamentoOrdineMissione annullamento = (AnnullamentoOrdineMissione)crudServiceBean.findById(principal, AnnullamentoOrdineMissione.class, new Long(flowResult.getIdMissione()));
                        if (annullamento != null){
                            annullamentoOrdineMissioneService.aggiornaAnnullamentoOrdineMissione(principal, annullamento, flowResult);
                        } else {
                            errore = "L'annullamento ordine di missione con ID "+flowResult.getIdMissione()+" indicato dal flusso con ID "+flowResult.getIdFlusso()+" non è presente";
                            log.info(errore);
                            mailService.sendEmailError(subjectErrorFlowsAnnullamento + this.toString(), errore, false, true);
                        }
                        break;
                }

            } else {
                try {
                    errore = "ID Missione non presente per l'id del flusso "+flowResult.getIdFlusso();
                    log.info(errore);
                    mailService.sendEmailError(subjectGenericError + this.toString(), errore, false, true);
                } catch (Exception e1) {
                    log.error("Errore durante l'invio dell'e-mail: "+e1);
                }
            }
        } catch (Exception e){
            try {
                errore = e.getMessage();
                log.info(errore);
                mailService.sendEmailError(subjectGenericError, errore,  false, true);
            } catch (Exception e1) {
                log.error("Errore durante l'invio dell'e-mail: "+e1);
            }
        }
/*

        if (idOrdineMissione != null){
            OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, idOrdineMissione);
            if (ordineMissione.isStatoInviatoAlFlusso() && !ordineMissione.isMissioneDaValidare()){
                ResultFlows result = retrieveDataFromFlows(ordineMissione);
                if (result == null){
                    return null;
                }
                if (result.isApprovato()){
                    log.info("Trovato in Scrivania Digitale un ordine di missione con id {} della uo {}, anno {}, numero {} approvato.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
                    ordineMissioneService.aggiornaOrdineMissioneApprovato(principal, ordineMissione);
                    return result;
                } else if (result.isAnnullato()){
                    log.info("Trovato in Scrivania Digitale un ordine di missione con id {} della uo {}, anno {}, numero {} annullato.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
                    ordineMissioneService.aggiornaOrdineMissioneAnnullato(principal, ordineMissione);
                    return result;
                } else if (result.isStateReject()){
                    log.info("Trovato in Scrivania Digitale un ordine di missione con id {} della uo {}, anno {}, numero {} respinto.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
                    ordineMissioneService.aggiornaOrdineMissioneRespinto(principal, result, ordineMissione);
                    return result;
                }
            }
        }
        return null;*/
    }



}
