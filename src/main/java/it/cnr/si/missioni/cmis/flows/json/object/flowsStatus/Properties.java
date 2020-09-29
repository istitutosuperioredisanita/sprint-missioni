
package it.cnr.si.missioni.cmis.flows.json.object.flowsStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "bpm_assignee",
    "cnrmissioni_dataFineMissione",
    "wfcnr_wfCounterAnno",
    "cnrmissioni_userNameResponsabileModulo",
    "cnrmissioni_descrizioneModulo",
    "cnrmissioni_autoPropriaTerzoMotivo",
    "wfcnr_reviewOutcome",
    "cnrmissioni_autoPropriaSecondoMotivo",
    "bpm_hiddenTransitions",
    "cnrmissioni_destinazione",
    "cnrmissioni_validazioneSpesaFlag",
    "cm_owner",
    "cnrmissioni_descrizioneCapitolo",
    "cnrmissioni_autoPropriaFlag",
    "cnrmissioni_note",
    "cnrmissioni_trattamento",
    "cnrmissioni_uoOrdine",
    "wfcnr_wfNodeRefCartellaFlusso",
    "bpm_package",
    "cnrmissioni_competenzaResiduo",
    "bpm_packageItemActionGroup",
    "cnrmissioni_descrizioneOrdine",
    "cnrmissioni_capitolo",
    "cnrmissioni_descrizioneGae",
    "cnrmissioni_descrizioneUoOrdine",
    "cnrmissioni_autoPropriaPrimoMotivo",
    "cnrmissioni_missioneEsteraFlag",
    "cnrmissioni_noteAutorizzazioniAggiuntive",
    "bpm_outcomePropertyName",
    "cnrmissioni_wfOrdineDaRevoca",
    "cm_name",
    "cnrmissioni_missioneGratuita",
    "cnrmissioni_userNameAmministrativo3",
    "cnrmissioni_userNameAmministrativo2",
    "bpm_taskId",
    "wfcnr_wfCounterId",
    "bpm_startDate",
    "bpm_packageActionGroup",
    "bpm_reassignable",
    "cnrmissioni_userNameAmministrativo1",
    "bpm_dueDate",
    "cnrmissioni_userNameRichiedente",
    "bpm_priority",
    "cnrmissioni_servizioFlagOk",
    "cnrmissioni_validazioneModuloFlag",
    "bpm_percentComplete",
    "cnrmissioni_dataInizioMissione",
    "bpm_pooledActors",
    "cnrmissioni_noleggioFlag",
    "cnrmissioni_gae",
    "cnrmissioni_missioneConAnticipoFlag",
    "cnrmissioni_taxiFlag",
    "cnrmissioni_modulo",
    "cnrmissioni_autoPropriaAltriMotivi",
    "cnrmissioni_userNameFirmatarioSpesa",
    "wfcnr_groupName",
    "cnrmissioni_noteSegreteria",
    "bpm_description",
    "cnrmissioni_userNamePrimoFirmatario",
    "cnrmissioni_descrizioneImpegno",
    "cnrmissioni_uoSpesa",
    "cnrmissioni_personaSeguitoFlagOk",
    "cnrmissioni_userNameUtenteOrdineMissione",
    "cnrmissioni_disponibilita",
    "cnrmissioni_descrizioneUoCompetenza",
    "bpm_status",
    "bpm_comment",
    "cnrmissioni_uoCompetenza",
    "cm_created",
    "wfcnr_wfCounterIndex",
    "cnrmissioni_descrizioneUoSpesa",
    "bpm_completionDate",
    "bpm_outcome",
    "initiator",
    "bpm_sendEMailNotifications",
    "wfvarGruppoStrutturaSpesaSUPERVISORI",
    "workflowinstanceid",
    "bpm_workflowPriority",
    "wfcnr_linkToOtherWorkflows",
    "wfvarNomeFlusso",
    "bpm_workflowDueDate",
    "wfvarUtenteOrdineMissione",
    "wfvarUtenteRichiedente",
    "initiatorhome",
    "wfvarGruppoStrutturaSpesaRESPONSABILI",
    "wfvarWorkflowInstanceId",
    "cancelled",
    "wfvarGruppoRESPONSABILI",
    "wfvarGruppoStrutturaUoSUPERVISORI",
    "wfvarValidazioneSpesa",
    "wfvarGruppoSUPERVISORI",
    "bpm_workflowDescription",
    "companyhome",
    "wfvarUtentePrimoFirmatario",
    "wfvarUtenteFirmatarioSpesa",
    "wfvarGruppoStrutturaUoRESPONSABILI",
    "wfvarTitoloFlusso"
})
public class Properties {

    @JsonProperty("bpm_assignee")
    private String bpmAssignee;
    @JsonProperty("cnrmissioni_dataFineMissione")
    private String cnrmissioniDataFineMissione;
    @JsonProperty("wfcnr_wfCounterAnno")
    private String wfcnrWfCounterAnno;
    @JsonProperty("cnrmissioni_userNameResponsabileModulo")
    private String cnrmissioniUserNameResponsabileModulo;
    @JsonProperty("cnrmissioni_descrizioneModulo")
    private String cnrmissioniDescrizioneModulo;
    @JsonProperty("cnrmissioni_autoPropriaTerzoMotivo")
    private String cnrmissioniAutoPropriaTerzoMotivo;
    @JsonProperty("wfcnr_reviewOutcome")
    private String wfcnrReviewOutcome;
    @JsonProperty("cnrmissioni_autoPropriaSecondoMotivo")
    private String cnrmissioniAutoPropriaSecondoMotivo;
    @JsonProperty("bpm_hiddenTransitions")
    private List<Object> bpmHiddenTransitions = null;
    @JsonProperty("cnrmissioni_destinazione")
    private String cnrmissioniDestinazione;
    @JsonProperty("cnrmissioni_validazioneSpesaFlag")
    private Boolean cnrmissioniValidazioneSpesaFlag;
    @JsonProperty("cm_owner")
    private String cmOwner;
    @JsonProperty("cnrmissioni_descrizioneCapitolo")
    private String cnrmissioniDescrizioneCapitolo;
    @JsonProperty("cnrmissioni_autoPropriaFlag")
    private Boolean cnrmissioniAutoPropriaFlag;
    @JsonProperty("cnrmissioni_note")
    private String cnrmissioniNote;
    @JsonProperty("cnrmissioni_trattamento")
    private String cnrmissioniTrattamento;
    @JsonProperty("cnrmissioni_uoOrdine")
    private String cnrmissioniUoOrdine;
    @JsonProperty("wfcnr_wfNodeRefCartellaFlusso")
    private String wfcnrWfNodeRefCartellaFlusso;
    @JsonProperty("bpm_package")
    private String bpmPackage;
    @JsonProperty("cnrmissioni_competenzaResiduo")
    private String cnrmissioniCompetenzaResiduo;
    @JsonProperty("bpm_packageItemActionGroup")
    private String bpmPackageItemActionGroup;
    @JsonProperty("cnrmissioni_descrizioneOrdine")
    private String cnrmissioniDescrizioneOrdine;
    @JsonProperty("cnrmissioni_capitolo")
    private String cnrmissioniCapitolo;
    @JsonProperty("cnrmissioni_descrizioneGae")
    private String cnrmissioniDescrizioneGae;
    @JsonProperty("cnrmissioni_descrizioneUoOrdine")
    private String cnrmissioniDescrizioneUoOrdine;
    @JsonProperty("cnrmissioni_autoPropriaPrimoMotivo")
    private String cnrmissioniAutoPropriaPrimoMotivo;
    @JsonProperty("cnrmissioni_missioneEsteraFlag")
    private Boolean cnrmissioniMissioneEsteraFlag;
    @JsonProperty("cnrmissioni_noteAutorizzazioniAggiuntive")
    private String cnrmissioniNoteAutorizzazioniAggiuntive;
    @JsonProperty("bpm_outcomePropertyName")
    private String bpmOutcomePropertyName;
    @JsonProperty("cnrmissioni_wfOrdineDaRevoca")
    private String cnrmissioniWfOrdineDaRevoca;
    @JsonProperty("cm_name")
    private String cmName;
    @JsonProperty("cnrmissioni_missioneGratuita")
    private Boolean cnrmissioniMissioneGratuita;
    @JsonProperty("cnrmissioni_userNameAmministrativo3")
    private String cnrmissioniUserNameAmministrativo3;
    @JsonProperty("cnrmissioni_userNameAmministrativo2")
    private String cnrmissioniUserNameAmministrativo2;
    @JsonProperty("bpm_taskId")
    private String bpmTaskId;
    @JsonProperty("wfcnr_wfCounterId")
    private String wfcnrWfCounterId;
    @JsonProperty("bpm_startDate")
    private String bpmStartDate;
    @JsonProperty("bpm_packageActionGroup")
    private String bpmPackageActionGroup;
    @JsonProperty("bpm_reassignable")
    private Boolean bpmReassignable;
    @JsonProperty("cnrmissioni_userNameAmministrativo1")
    private String cnrmissioniUserNameAmministrativo1;
    @JsonProperty("bpm_dueDate")
    private String bpmDueDate;
    @JsonProperty("cnrmissioni_userNameRichiedente")
    private String cnrmissioniUserNameRichiedente;
    @JsonProperty("bpm_priority")
    private Integer bpmPriority;
    @JsonProperty("cnrmissioni_servizioFlagOk")
    private Boolean cnrmissioniServizioFlagOk;
    @JsonProperty("cnrmissioni_validazioneModuloFlag")
    private Boolean cnrmissioniValidazioneModuloFlag;
    @JsonProperty("bpm_percentComplete")
    private Integer bpmPercentComplete;
    @JsonProperty("cnrmissioni_dataInizioMissione")
    private String cnrmissioniDataInizioMissione;
    @JsonProperty("bpm_pooledActors")
    private List<Object> bpmPooledActors = null;
    @JsonProperty("cnrmissioni_noleggioFlag")
    private Boolean cnrmissioniNoleggioFlag;
    @JsonProperty("cnrmissioni_gae")
    private String cnrmissioniGae;
    @JsonProperty("cnrmissioni_missioneConAnticipoFlag")
    private Boolean cnrmissioniMissioneConAnticipoFlag;
    @JsonProperty("cnrmissioni_taxiFlag")
    private Boolean cnrmissioniTaxiFlag;
    @JsonProperty("cnrmissioni_modulo")
    private String cnrmissioniModulo;
    @JsonProperty("cnrmissioni_autoPropriaAltriMotivi")
    private String cnrmissioniAutoPropriaAltriMotivi;
    @JsonProperty("cnrmissioni_userNameFirmatarioSpesa")
    private String cnrmissioniUserNameFirmatarioSpesa;
    @JsonProperty("wfcnr_groupName")
    private String wfcnrGroupName;
    @JsonProperty("cnrmissioni_noteSegreteria")
    private String cnrmissioniNoteSegreteria;
    @JsonProperty("bpm_description")
    private String bpmDescription;
    @JsonProperty("cnrmissioni_userNamePrimoFirmatario")
    private String cnrmissioniUserNamePrimoFirmatario;
    @JsonProperty("cnrmissioni_descrizioneImpegno")
    private String cnrmissioniDescrizioneImpegno;
    @JsonProperty("cnrmissioni_uoSpesa")
    private String cnrmissioniUoSpesa;
    @JsonProperty("cnrmissioni_personaSeguitoFlagOk")
    private Boolean cnrmissioniPersonaSeguitoFlagOk;
    @JsonProperty("cnrmissioni_userNameUtenteOrdineMissione")
    private String cnrmissioniUserNameUtenteOrdineMissione;
    @JsonProperty("cnrmissioni_disponibilita")
    private Integer cnrmissioniDisponibilita;
    @JsonProperty("cnrmissioni_descrizioneUoCompetenza")
    private String cnrmissioniDescrizioneUoCompetenza;
    @JsonProperty("bpm_status")
    private String bpmStatus;
    @JsonProperty("bpm_comment")
    private String bpmComment;
    @JsonProperty("cnrmissioni_uoCompetenza")
    private String cnrmissioniUoCompetenza;
    @JsonProperty("cm_created")
    private String cmCreated;
    @JsonProperty("wfcnr_wfCounterIndex")
    private Integer wfcnrWfCounterIndex;
    @JsonProperty("cnrmissioni_descrizioneUoSpesa")
    private String cnrmissioniDescrizioneUoSpesa;
    @JsonProperty("bpm_completionDate")
    private String bpmCompletionDate;
    @JsonProperty("bpm_outcome")
    private String bpmOutcome;
    @JsonProperty("initiator")
    private String initiator;
    @JsonProperty("bpm_sendEMailNotifications")
    private Boolean bpmSendEMailNotifications;
    @JsonProperty("wfvarGruppoStrutturaSpesaSUPERVISORI")
    private String wfvarGruppoStrutturaSpesaSUPERVISORI;
    @JsonProperty("workflowinstanceid")
    private String workflowinstanceid;
    @JsonProperty("bpm_workflowPriority")
    private Integer bpmWorkflowPriority;
    @JsonProperty("wfcnr_linkToOtherWorkflows")
    private String wfcnrLinkToOtherWorkflows;
    @JsonProperty("wfvarNomeFlusso")
    private String wfvarNomeFlusso;
    @JsonProperty("bpm_workflowDueDate")
    private String bpmWorkflowDueDate;
    @JsonProperty("wfvarUtenteOrdineMissione")
    private String wfvarUtenteOrdineMissione;
    @JsonProperty("wfvarUtenteRichiedente")
    private String wfvarUtenteRichiedente;
    @JsonProperty("initiatorhome")
    private String initiatorhome;
    @JsonProperty("wfvarGruppoStrutturaSpesaRESPONSABILI")
    private String wfvarGruppoStrutturaSpesaRESPONSABILI;
    @JsonProperty("wfvarWorkflowInstanceId")
    private String wfvarWorkflowInstanceId;
    @JsonProperty("cancelled")
    private Boolean cancelled;
    @JsonProperty("wfvarGruppoRESPONSABILI")
    private String wfvarGruppoRESPONSABILI;
    @JsonProperty("wfvarGruppoStrutturaUoSUPERVISORI")
    private String wfvarGruppoStrutturaUoSUPERVISORI;
    @JsonProperty("wfvarValidazioneSpesa")
    private Boolean wfvarValidazioneSpesa;
    @JsonProperty("wfvarGruppoSUPERVISORI")
    private String wfvarGruppoSUPERVISORI;
    @JsonProperty("bpm_workflowDescription")
    private String bpmWorkflowDescription;
    @JsonProperty("companyhome")
    private String companyhome;
    @JsonProperty("wfvarUtentePrimoFirmatario")
    private String wfvarUtentePrimoFirmatario;
    @JsonProperty("wfvarUtenteFirmatarioSpesa")
    private String wfvarUtenteFirmatarioSpesa;
    @JsonProperty("wfvarGruppoStrutturaUoRESPONSABILI")
    private String wfvarGruppoStrutturaUoRESPONSABILI;
    @JsonProperty("wfvarTitoloFlusso")
    private String wfvarTitoloFlusso;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("bpm_assignee")
    public String getBpmAssignee() {
        return bpmAssignee;
    }

    @JsonProperty("bpm_assignee")
    public void setBpmAssignee(String bpmAssignee) {
        this.bpmAssignee = bpmAssignee;
    }

    @JsonProperty("cnrmissioni_dataFineMissione")
    public String getCnrmissioniDataFineMissione() {
        return cnrmissioniDataFineMissione;
    }

    @JsonProperty("cnrmissioni_dataFineMissione")
    public void setCnrmissioniDataFineMissione(String cnrmissioniDataFineMissione) {
        this.cnrmissioniDataFineMissione = cnrmissioniDataFineMissione;
    }

    @JsonProperty("wfcnr_wfCounterAnno")
    public String getWfcnrWfCounterAnno() {
        return wfcnrWfCounterAnno;
    }

    @JsonProperty("wfcnr_wfCounterAnno")
    public void setWfcnrWfCounterAnno(String wfcnrWfCounterAnno) {
        this.wfcnrWfCounterAnno = wfcnrWfCounterAnno;
    }

    @JsonProperty("cnrmissioni_userNameResponsabileModulo")
    public String getCnrmissioniUserNameResponsabileModulo() {
        return cnrmissioniUserNameResponsabileModulo;
    }

    @JsonProperty("cnrmissioni_userNameResponsabileModulo")
    public void setCnrmissioniUserNameResponsabileModulo(String cnrmissioniUserNameResponsabileModulo) {
        this.cnrmissioniUserNameResponsabileModulo = cnrmissioniUserNameResponsabileModulo;
    }

    @JsonProperty("cnrmissioni_descrizioneModulo")
    public String getCnrmissioniDescrizioneModulo() {
        return cnrmissioniDescrizioneModulo;
    }

    @JsonProperty("cnrmissioni_descrizioneModulo")
    public void setCnrmissioniDescrizioneModulo(String cnrmissioniDescrizioneModulo) {
        this.cnrmissioniDescrizioneModulo = cnrmissioniDescrizioneModulo;
    }

    @JsonProperty("cnrmissioni_autoPropriaTerzoMotivo")
    public String getCnrmissioniAutoPropriaTerzoMotivo() {
        return cnrmissioniAutoPropriaTerzoMotivo;
    }

    @JsonProperty("cnrmissioni_autoPropriaTerzoMotivo")
    public void setCnrmissioniAutoPropriaTerzoMotivo(String cnrmissioniAutoPropriaTerzoMotivo) {
        this.cnrmissioniAutoPropriaTerzoMotivo = cnrmissioniAutoPropriaTerzoMotivo;
    }

    @JsonProperty("wfcnr_reviewOutcome")
    public String getWfcnrReviewOutcome() {
        return wfcnrReviewOutcome;
    }

    @JsonProperty("wfcnr_reviewOutcome")
    public void setWfcnrReviewOutcome(String wfcnrReviewOutcome) {
        this.wfcnrReviewOutcome = wfcnrReviewOutcome;
    }

    @JsonProperty("cnrmissioni_autoPropriaSecondoMotivo")
    public String getCnrmissioniAutoPropriaSecondoMotivo() {
        return cnrmissioniAutoPropriaSecondoMotivo;
    }

    @JsonProperty("cnrmissioni_autoPropriaSecondoMotivo")
    public void setCnrmissioniAutoPropriaSecondoMotivo(String cnrmissioniAutoPropriaSecondoMotivo) {
        this.cnrmissioniAutoPropriaSecondoMotivo = cnrmissioniAutoPropriaSecondoMotivo;
    }

    @JsonProperty("bpm_hiddenTransitions")
    public List<Object> getBpmHiddenTransitions() {
        return bpmHiddenTransitions;
    }

    @JsonProperty("bpm_hiddenTransitions")
    public void setBpmHiddenTransitions(List<Object> bpmHiddenTransitions) {
        this.bpmHiddenTransitions = bpmHiddenTransitions;
    }

    @JsonProperty("cnrmissioni_destinazione")
    public String getCnrmissioniDestinazione() {
        return cnrmissioniDestinazione;
    }

    @JsonProperty("cnrmissioni_destinazione")
    public void setCnrmissioniDestinazione(String cnrmissioniDestinazione) {
        this.cnrmissioniDestinazione = cnrmissioniDestinazione;
    }

    @JsonProperty("cnrmissioni_validazioneSpesaFlag")
    public Boolean getCnrmissioniValidazioneSpesaFlag() {
        return cnrmissioniValidazioneSpesaFlag;
    }

    @JsonProperty("cnrmissioni_validazioneSpesaFlag")
    public void setCnrmissioniValidazioneSpesaFlag(Boolean cnrmissioniValidazioneSpesaFlag) {
        this.cnrmissioniValidazioneSpesaFlag = cnrmissioniValidazioneSpesaFlag;
    }

    @JsonProperty("cm_owner")
    public String getCmOwner() {
        return cmOwner;
    }

    @JsonProperty("cm_owner")
    public void setCmOwner(String cmOwner) {
        this.cmOwner = cmOwner;
    }

    @JsonProperty("cnrmissioni_descrizioneCapitolo")
    public String getCnrmissioniDescrizioneCapitolo() {
        return cnrmissioniDescrizioneCapitolo;
    }

    @JsonProperty("cnrmissioni_descrizioneCapitolo")
    public void setCnrmissioniDescrizioneCapitolo(String cnrmissioniDescrizioneCapitolo) {
        this.cnrmissioniDescrizioneCapitolo = cnrmissioniDescrizioneCapitolo;
    }

    @JsonProperty("cnrmissioni_autoPropriaFlag")
    public Boolean getCnrmissioniAutoPropriaFlag() {
        return cnrmissioniAutoPropriaFlag;
    }

    @JsonProperty("cnrmissioni_autoPropriaFlag")
    public void setCnrmissioniAutoPropriaFlag(Boolean cnrmissioniAutoPropriaFlag) {
        this.cnrmissioniAutoPropriaFlag = cnrmissioniAutoPropriaFlag;
    }

    @JsonProperty("cnrmissioni_note")
    public String getCnrmissioniNote() {
        return cnrmissioniNote;
    }

    @JsonProperty("cnrmissioni_note")
    public void setCnrmissioniNote(String cnrmissioniNote) {
        this.cnrmissioniNote = cnrmissioniNote;
    }

    @JsonProperty("cnrmissioni_trattamento")
    public String getCnrmissioniTrattamento() {
        return cnrmissioniTrattamento;
    }

    @JsonProperty("cnrmissioni_trattamento")
    public void setCnrmissioniTrattamento(String cnrmissioniTrattamento) {
        this.cnrmissioniTrattamento = cnrmissioniTrattamento;
    }

    @JsonProperty("cnrmissioni_uoOrdine")
    public String getCnrmissioniUoOrdine() {
        return cnrmissioniUoOrdine;
    }

    @JsonProperty("cnrmissioni_uoOrdine")
    public void setCnrmissioniUoOrdine(String cnrmissioniUoOrdine) {
        this.cnrmissioniUoOrdine = cnrmissioniUoOrdine;
    }

    @JsonProperty("wfcnr_wfNodeRefCartellaFlusso")
    public String getWfcnrWfNodeRefCartellaFlusso() {
        return wfcnrWfNodeRefCartellaFlusso;
    }

    @JsonProperty("wfcnr_wfNodeRefCartellaFlusso")
    public void setWfcnrWfNodeRefCartellaFlusso(String wfcnrWfNodeRefCartellaFlusso) {
        this.wfcnrWfNodeRefCartellaFlusso = wfcnrWfNodeRefCartellaFlusso;
    }

    @JsonProperty("bpm_package")
    public String getBpmPackage() {
        return bpmPackage;
    }

    @JsonProperty("bpm_package")
    public void setBpmPackage(String bpmPackage) {
        this.bpmPackage = bpmPackage;
    }

    @JsonProperty("cnrmissioni_competenzaResiduo")
    public String getCnrmissioniCompetenzaResiduo() {
        return cnrmissioniCompetenzaResiduo;
    }

    @JsonProperty("cnrmissioni_competenzaResiduo")
    public void setCnrmissioniCompetenzaResiduo(String cnrmissioniCompetenzaResiduo) {
        this.cnrmissioniCompetenzaResiduo = cnrmissioniCompetenzaResiduo;
    }

    @JsonProperty("bpm_packageItemActionGroup")
    public String getBpmPackageItemActionGroup() {
        return bpmPackageItemActionGroup;
    }

    @JsonProperty("bpm_packageItemActionGroup")
    public void setBpmPackageItemActionGroup(String bpmPackageItemActionGroup) {
        this.bpmPackageItemActionGroup = bpmPackageItemActionGroup;
    }

    @JsonProperty("cnrmissioni_descrizioneOrdine")
    public String getCnrmissioniDescrizioneOrdine() {
        return cnrmissioniDescrizioneOrdine;
    }

    @JsonProperty("cnrmissioni_descrizioneOrdine")
    public void setCnrmissioniDescrizioneOrdine(String cnrmissioniDescrizioneOrdine) {
        this.cnrmissioniDescrizioneOrdine = cnrmissioniDescrizioneOrdine;
    }

    @JsonProperty("cnrmissioni_capitolo")
    public String getCnrmissioniCapitolo() {
        return cnrmissioniCapitolo;
    }

    @JsonProperty("cnrmissioni_capitolo")
    public void setCnrmissioniCapitolo(String cnrmissioniCapitolo) {
        this.cnrmissioniCapitolo = cnrmissioniCapitolo;
    }

    @JsonProperty("cnrmissioni_descrizioneGae")
    public String getCnrmissioniDescrizioneGae() {
        return cnrmissioniDescrizioneGae;
    }

    @JsonProperty("cnrmissioni_descrizioneGae")
    public void setCnrmissioniDescrizioneGae(String cnrmissioniDescrizioneGae) {
        this.cnrmissioniDescrizioneGae = cnrmissioniDescrizioneGae;
    }

    @JsonProperty("cnrmissioni_descrizioneUoOrdine")
    public String getCnrmissioniDescrizioneUoOrdine() {
        return cnrmissioniDescrizioneUoOrdine;
    }

    @JsonProperty("cnrmissioni_descrizioneUoOrdine")
    public void setCnrmissioniDescrizioneUoOrdine(String cnrmissioniDescrizioneUoOrdine) {
        this.cnrmissioniDescrizioneUoOrdine = cnrmissioniDescrizioneUoOrdine;
    }

    @JsonProperty("cnrmissioni_autoPropriaPrimoMotivo")
    public String getCnrmissioniAutoPropriaPrimoMotivo() {
        return cnrmissioniAutoPropriaPrimoMotivo;
    }

    @JsonProperty("cnrmissioni_autoPropriaPrimoMotivo")
    public void setCnrmissioniAutoPropriaPrimoMotivo(String cnrmissioniAutoPropriaPrimoMotivo) {
        this.cnrmissioniAutoPropriaPrimoMotivo = cnrmissioniAutoPropriaPrimoMotivo;
    }

    @JsonProperty("cnrmissioni_missioneEsteraFlag")
    public Boolean getCnrmissioniMissioneEsteraFlag() {
        return cnrmissioniMissioneEsteraFlag;
    }

    @JsonProperty("cnrmissioni_missioneEsteraFlag")
    public void setCnrmissioniMissioneEsteraFlag(Boolean cnrmissioniMissioneEsteraFlag) {
        this.cnrmissioniMissioneEsteraFlag = cnrmissioniMissioneEsteraFlag;
    }

    @JsonProperty("cnrmissioni_noteAutorizzazioniAggiuntive")
    public String getCnrmissioniNoteAutorizzazioniAggiuntive() {
        return cnrmissioniNoteAutorizzazioniAggiuntive;
    }

    @JsonProperty("cnrmissioni_noteAutorizzazioniAggiuntive")
    public void setCnrmissioniNoteAutorizzazioniAggiuntive(String cnrmissioniNoteAutorizzazioniAggiuntive) {
        this.cnrmissioniNoteAutorizzazioniAggiuntive = cnrmissioniNoteAutorizzazioniAggiuntive;
    }

    @JsonProperty("bpm_outcomePropertyName")
    public String getBpmOutcomePropertyName() {
        return bpmOutcomePropertyName;
    }

    @JsonProperty("bpm_outcomePropertyName")
    public void setBpmOutcomePropertyName(String bpmOutcomePropertyName) {
        this.bpmOutcomePropertyName = bpmOutcomePropertyName;
    }

    @JsonProperty("cnrmissioni_wfOrdineDaRevoca")
    public String getCnrmissioniWfOrdineDaRevoca() {
        return cnrmissioniWfOrdineDaRevoca;
    }

    @JsonProperty("cnrmissioni_wfOrdineDaRevoca")
    public void setCnrmissioniWfOrdineDaRevoca(String cnrmissioniWfOrdineDaRevoca) {
        this.cnrmissioniWfOrdineDaRevoca = cnrmissioniWfOrdineDaRevoca;
    }

    @JsonProperty("cm_name")
    public String getCmName() {
        return cmName;
    }

    @JsonProperty("cm_name")
    public void setCmName(String cmName) {
        this.cmName = cmName;
    }

    @JsonProperty("cnrmissioni_missioneGratuita")
    public Boolean getCnrmissioniMissioneGratuita() {
        return cnrmissioniMissioneGratuita;
    }

    @JsonProperty("cnrmissioni_missioneGratuita")
    public void setCnrmissioniMissioneGratuita(Boolean cnrmissioniMissioneGratuita) {
        this.cnrmissioniMissioneGratuita = cnrmissioniMissioneGratuita;
    }

    @JsonProperty("cnrmissioni_userNameAmministrativo3")
    public String getCnrmissioniUserNameAmministrativo3() {
        return cnrmissioniUserNameAmministrativo3;
    }

    @JsonProperty("cnrmissioni_userNameAmministrativo3")
    public void setCnrmissioniUserNameAmministrativo3(String cnrmissioniUserNameAmministrativo3) {
        this.cnrmissioniUserNameAmministrativo3 = cnrmissioniUserNameAmministrativo3;
    }

    @JsonProperty("cnrmissioni_userNameAmministrativo2")
    public String getCnrmissioniUserNameAmministrativo2() {
        return cnrmissioniUserNameAmministrativo2;
    }

    @JsonProperty("cnrmissioni_userNameAmministrativo2")
    public void setCnrmissioniUserNameAmministrativo2(String cnrmissioniUserNameAmministrativo2) {
        this.cnrmissioniUserNameAmministrativo2 = cnrmissioniUserNameAmministrativo2;
    }

    @JsonProperty("bpm_taskId")
    public String getBpmTaskId() {
        return bpmTaskId;
    }

    @JsonProperty("bpm_taskId")
    public void setBpmTaskId(String bpmTaskId) {
        this.bpmTaskId = bpmTaskId;
    }

    @JsonProperty("wfcnr_wfCounterId")
    public String getWfcnrWfCounterId() {
        return wfcnrWfCounterId;
    }

    @JsonProperty("wfcnr_wfCounterId")
    public void setWfcnrWfCounterId(String wfcnrWfCounterId) {
        this.wfcnrWfCounterId = wfcnrWfCounterId;
    }

    @JsonProperty("bpm_startDate")
    public String getBpmStartDate() {
        return bpmStartDate;
    }

    @JsonProperty("bpm_startDate")
    public void setBpmStartDate(String bpmStartDate) {
        this.bpmStartDate = bpmStartDate;
    }

    @JsonProperty("bpm_packageActionGroup")
    public String getBpmPackageActionGroup() {
        return bpmPackageActionGroup;
    }

    @JsonProperty("bpm_packageActionGroup")
    public void setBpmPackageActionGroup(String bpmPackageActionGroup) {
        this.bpmPackageActionGroup = bpmPackageActionGroup;
    }

    @JsonProperty("bpm_reassignable")
    public Boolean getBpmReassignable() {
        return bpmReassignable;
    }

    @JsonProperty("bpm_reassignable")
    public void setBpmReassignable(Boolean bpmReassignable) {
        this.bpmReassignable = bpmReassignable;
    }

    @JsonProperty("cnrmissioni_userNameAmministrativo1")
    public String getCnrmissioniUserNameAmministrativo1() {
        return cnrmissioniUserNameAmministrativo1;
    }

    @JsonProperty("cnrmissioni_userNameAmministrativo1")
    public void setCnrmissioniUserNameAmministrativo1(String cnrmissioniUserNameAmministrativo1) {
        this.cnrmissioniUserNameAmministrativo1 = cnrmissioniUserNameAmministrativo1;
    }

    @JsonProperty("bpm_dueDate")
    public String getBpmDueDate() {
        return bpmDueDate;
    }

    @JsonProperty("bpm_dueDate")
    public void setBpmDueDate(String bpmDueDate) {
        this.bpmDueDate = bpmDueDate;
    }

    @JsonProperty("cnrmissioni_userNameRichiedente")
    public String getCnrmissioniUserNameRichiedente() {
        return cnrmissioniUserNameRichiedente;
    }

    @JsonProperty("cnrmissioni_userNameRichiedente")
    public void setCnrmissioniUserNameRichiedente(String cnrmissioniUserNameRichiedente) {
        this.cnrmissioniUserNameRichiedente = cnrmissioniUserNameRichiedente;
    }

    @JsonProperty("bpm_priority")
    public Integer getBpmPriority() {
        return bpmPriority;
    }

    @JsonProperty("bpm_priority")
    public void setBpmPriority(Integer bpmPriority) {
        this.bpmPriority = bpmPriority;
    }

    @JsonProperty("cnrmissioni_servizioFlagOk")
    public Boolean getCnrmissioniServizioFlagOk() {
        return cnrmissioniServizioFlagOk;
    }

    @JsonProperty("cnrmissioni_servizioFlagOk")
    public void setCnrmissioniServizioFlagOk(Boolean cnrmissioniServizioFlagOk) {
        this.cnrmissioniServizioFlagOk = cnrmissioniServizioFlagOk;
    }

    @JsonProperty("cnrmissioni_validazioneModuloFlag")
    public Boolean getCnrmissioniValidazioneModuloFlag() {
        return cnrmissioniValidazioneModuloFlag;
    }

    @JsonProperty("cnrmissioni_validazioneModuloFlag")
    public void setCnrmissioniValidazioneModuloFlag(Boolean cnrmissioniValidazioneModuloFlag) {
        this.cnrmissioniValidazioneModuloFlag = cnrmissioniValidazioneModuloFlag;
    }

    @JsonProperty("bpm_percentComplete")
    public Integer getBpmPercentComplete() {
        return bpmPercentComplete;
    }

    @JsonProperty("bpm_percentComplete")
    public void setBpmPercentComplete(Integer bpmPercentComplete) {
        this.bpmPercentComplete = bpmPercentComplete;
    }

    @JsonProperty("cnrmissioni_dataInizioMissione")
    public String getCnrmissioniDataInizioMissione() {
        return cnrmissioniDataInizioMissione;
    }

    @JsonProperty("cnrmissioni_dataInizioMissione")
    public void setCnrmissioniDataInizioMissione(String cnrmissioniDataInizioMissione) {
        this.cnrmissioniDataInizioMissione = cnrmissioniDataInizioMissione;
    }

    @JsonProperty("bpm_pooledActors")
    public List<Object> getBpmPooledActors() {
        return bpmPooledActors;
    }

    @JsonProperty("bpm_pooledActors")
    public void setBpmPooledActors(List<Object> bpmPooledActors) {
        this.bpmPooledActors = bpmPooledActors;
    }

    @JsonProperty("cnrmissioni_noleggioFlag")
    public Boolean getCnrmissioniNoleggioFlag() {
        return cnrmissioniNoleggioFlag;
    }

    @JsonProperty("cnrmissioni_noleggioFlag")
    public void setCnrmissioniNoleggioFlag(Boolean cnrmissioniNoleggioFlag) {
        this.cnrmissioniNoleggioFlag = cnrmissioniNoleggioFlag;
    }

    @JsonProperty("cnrmissioni_gae")
    public String getCnrmissioniGae() {
        return cnrmissioniGae;
    }

    @JsonProperty("cnrmissioni_gae")
    public void setCnrmissioniGae(String cnrmissioniGae) {
        this.cnrmissioniGae = cnrmissioniGae;
    }

    @JsonProperty("cnrmissioni_missioneConAnticipoFlag")
    public Boolean getCnrmissioniMissioneConAnticipoFlag() {
        return cnrmissioniMissioneConAnticipoFlag;
    }

    @JsonProperty("cnrmissioni_missioneConAnticipoFlag")
    public void setCnrmissioniMissioneConAnticipoFlag(Boolean cnrmissioniMissioneConAnticipoFlag) {
        this.cnrmissioniMissioneConAnticipoFlag = cnrmissioniMissioneConAnticipoFlag;
    }

    @JsonProperty("cnrmissioni_taxiFlag")
    public Boolean getCnrmissioniTaxiFlag() {
        return cnrmissioniTaxiFlag;
    }

    @JsonProperty("cnrmissioni_taxiFlag")
    public void setCnrmissioniTaxiFlag(Boolean cnrmissioniTaxiFlag) {
        this.cnrmissioniTaxiFlag = cnrmissioniTaxiFlag;
    }

    @JsonProperty("cnrmissioni_modulo")
    public String getCnrmissioniModulo() {
        return cnrmissioniModulo;
    }

    @JsonProperty("cnrmissioni_modulo")
    public void setCnrmissioniModulo(String cnrmissioniModulo) {
        this.cnrmissioniModulo = cnrmissioniModulo;
    }

    @JsonProperty("cnrmissioni_autoPropriaAltriMotivi")
    public String getCnrmissioniAutoPropriaAltriMotivi() {
        return cnrmissioniAutoPropriaAltriMotivi;
    }

    @JsonProperty("cnrmissioni_autoPropriaAltriMotivi")
    public void setCnrmissioniAutoPropriaAltriMotivi(String cnrmissioniAutoPropriaAltriMotivi) {
        this.cnrmissioniAutoPropriaAltriMotivi = cnrmissioniAutoPropriaAltriMotivi;
    }

    @JsonProperty("cnrmissioni_userNameFirmatarioSpesa")
    public String getCnrmissioniUserNameFirmatarioSpesa() {
        return cnrmissioniUserNameFirmatarioSpesa;
    }

    @JsonProperty("cnrmissioni_userNameFirmatarioSpesa")
    public void setCnrmissioniUserNameFirmatarioSpesa(String cnrmissioniUserNameFirmatarioSpesa) {
        this.cnrmissioniUserNameFirmatarioSpesa = cnrmissioniUserNameFirmatarioSpesa;
    }

    @JsonProperty("wfcnr_groupName")
    public String getWfcnrGroupName() {
        return wfcnrGroupName;
    }

    @JsonProperty("wfcnr_groupName")
    public void setWfcnrGroupName(String wfcnrGroupName) {
        this.wfcnrGroupName = wfcnrGroupName;
    }

    @JsonProperty("cnrmissioni_noteSegreteria")
    public String getCnrmissioniNoteSegreteria() {
        return cnrmissioniNoteSegreteria;
    }

    @JsonProperty("cnrmissioni_noteSegreteria")
    public void setCnrmissioniNoteSegreteria(String cnrmissioniNoteSegreteria) {
        this.cnrmissioniNoteSegreteria = cnrmissioniNoteSegreteria;
    }

    @JsonProperty("bpm_description")
    public String getBpmDescription() {
        return bpmDescription;
    }

    @JsonProperty("bpm_description")
    public void setBpmDescription(String bpmDescription) {
        this.bpmDescription = bpmDescription;
    }

    @JsonProperty("cnrmissioni_userNamePrimoFirmatario")
    public String getCnrmissioniUserNamePrimoFirmatario() {
        return cnrmissioniUserNamePrimoFirmatario;
    }

    @JsonProperty("cnrmissioni_userNamePrimoFirmatario")
    public void setCnrmissioniUserNamePrimoFirmatario(String cnrmissioniUserNamePrimoFirmatario) {
        this.cnrmissioniUserNamePrimoFirmatario = cnrmissioniUserNamePrimoFirmatario;
    }

    @JsonProperty("cnrmissioni_descrizioneImpegno")
    public String getCnrmissioniDescrizioneImpegno() {
        return cnrmissioniDescrizioneImpegno;
    }

    @JsonProperty("cnrmissioni_descrizioneImpegno")
    public void setCnrmissioniDescrizioneImpegno(String cnrmissioniDescrizioneImpegno) {
        this.cnrmissioniDescrizioneImpegno = cnrmissioniDescrizioneImpegno;
    }

    @JsonProperty("cnrmissioni_uoSpesa")
    public String getCnrmissioniUoSpesa() {
        return cnrmissioniUoSpesa;
    }

    @JsonProperty("cnrmissioni_uoSpesa")
    public void setCnrmissioniUoSpesa(String cnrmissioniUoSpesa) {
        this.cnrmissioniUoSpesa = cnrmissioniUoSpesa;
    }

    @JsonProperty("cnrmissioni_personaSeguitoFlagOk")
    public Boolean getCnrmissioniPersonaSeguitoFlagOk() {
        return cnrmissioniPersonaSeguitoFlagOk;
    }

    @JsonProperty("cnrmissioni_personaSeguitoFlagOk")
    public void setCnrmissioniPersonaSeguitoFlagOk(Boolean cnrmissioniPersonaSeguitoFlagOk) {
        this.cnrmissioniPersonaSeguitoFlagOk = cnrmissioniPersonaSeguitoFlagOk;
    }

    @JsonProperty("cnrmissioni_userNameUtenteOrdineMissione")
    public String getCnrmissioniUserNameUtenteOrdineMissione() {
        return cnrmissioniUserNameUtenteOrdineMissione;
    }

    @JsonProperty("cnrmissioni_userNameUtenteOrdineMissione")
    public void setCnrmissioniUserNameUtenteOrdineMissione(String cnrmissioniUserNameUtenteOrdineMissione) {
        this.cnrmissioniUserNameUtenteOrdineMissione = cnrmissioniUserNameUtenteOrdineMissione;
    }

    @JsonProperty("cnrmissioni_disponibilita")
    public Integer getCnrmissioniDisponibilita() {
        return cnrmissioniDisponibilita;
    }

    @JsonProperty("cnrmissioni_disponibilita")
    public void setCnrmissioniDisponibilita(Integer cnrmissioniDisponibilita) {
        this.cnrmissioniDisponibilita = cnrmissioniDisponibilita;
    }

    @JsonProperty("cnrmissioni_descrizioneUoCompetenza")
    public String getCnrmissioniDescrizioneUoCompetenza() {
        return cnrmissioniDescrizioneUoCompetenza;
    }

    @JsonProperty("cnrmissioni_descrizioneUoCompetenza")
    public void setCnrmissioniDescrizioneUoCompetenza(String cnrmissioniDescrizioneUoCompetenza) {
        this.cnrmissioniDescrizioneUoCompetenza = cnrmissioniDescrizioneUoCompetenza;
    }

    @JsonProperty("bpm_status")
    public String getBpmStatus() {
        return bpmStatus;
    }

    @JsonProperty("bpm_status")
    public void setBpmStatus(String bpmStatus) {
        this.bpmStatus = bpmStatus;
    }

    @JsonProperty("bpm_comment")
    public String getBpmComment() {
        return bpmComment;
    }

    @JsonProperty("bpm_comment")
    public void setBpmComment(String bpmComment) {
        this.bpmComment = bpmComment;
    }

    @JsonProperty("cnrmissioni_uoCompetenza")
    public String getCnrmissioniUoCompetenza() {
        return cnrmissioniUoCompetenza;
    }

    @JsonProperty("cnrmissioni_uoCompetenza")
    public void setCnrmissioniUoCompetenza(String cnrmissioniUoCompetenza) {
        this.cnrmissioniUoCompetenza = cnrmissioniUoCompetenza;
    }

    @JsonProperty("cm_created")
    public String getCmCreated() {
        return cmCreated;
    }

    @JsonProperty("cm_created")
    public void setCmCreated(String cmCreated) {
        this.cmCreated = cmCreated;
    }

    @JsonProperty("wfcnr_wfCounterIndex")
    public Integer getWfcnrWfCounterIndex() {
        return wfcnrWfCounterIndex;
    }

    @JsonProperty("wfcnr_wfCounterIndex")
    public void setWfcnrWfCounterIndex(Integer wfcnrWfCounterIndex) {
        this.wfcnrWfCounterIndex = wfcnrWfCounterIndex;
    }

    @JsonProperty("cnrmissioni_descrizioneUoSpesa")
    public String getCnrmissioniDescrizioneUoSpesa() {
        return cnrmissioniDescrizioneUoSpesa;
    }

    @JsonProperty("cnrmissioni_descrizioneUoSpesa")
    public void setCnrmissioniDescrizioneUoSpesa(String cnrmissioniDescrizioneUoSpesa) {
        this.cnrmissioniDescrizioneUoSpesa = cnrmissioniDescrizioneUoSpesa;
    }

    @JsonProperty("bpm_completionDate")
    public String getBpmCompletionDate() {
        return bpmCompletionDate;
    }

    @JsonProperty("bpm_completionDate")
    public void setBpmCompletionDate(String bpmCompletionDate) {
        this.bpmCompletionDate = bpmCompletionDate;
    }

    @JsonProperty("bpm_outcome")
    public String getBpmOutcome() {
        return bpmOutcome;
    }

    @JsonProperty("bpm_outcome")
    public void setBpmOutcome(String bpmOutcome) {
        this.bpmOutcome = bpmOutcome;
    }

    @JsonProperty("initiator")
    public String getInitiator() {
        return initiator;
    }

    @JsonProperty("initiator")
    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    @JsonProperty("bpm_sendEMailNotifications")
    public Boolean getBpmSendEMailNotifications() {
        return bpmSendEMailNotifications;
    }

    @JsonProperty("bpm_sendEMailNotifications")
    public void setBpmSendEMailNotifications(Boolean bpmSendEMailNotifications) {
        this.bpmSendEMailNotifications = bpmSendEMailNotifications;
    }

    @JsonProperty("wfvarGruppoStrutturaSpesaSUPERVISORI")
    public String getWfvarGruppoStrutturaSpesaSUPERVISORI() {
        return wfvarGruppoStrutturaSpesaSUPERVISORI;
    }

    @JsonProperty("wfvarGruppoStrutturaSpesaSUPERVISORI")
    public void setWfvarGruppoStrutturaSpesaSUPERVISORI(String wfvarGruppoStrutturaSpesaSUPERVISORI) {
        this.wfvarGruppoStrutturaSpesaSUPERVISORI = wfvarGruppoStrutturaSpesaSUPERVISORI;
    }

    @JsonProperty("workflowinstanceid")
    public String getWorkflowinstanceid() {
        return workflowinstanceid;
    }

    @JsonProperty("workflowinstanceid")
    public void setWorkflowinstanceid(String workflowinstanceid) {
        this.workflowinstanceid = workflowinstanceid;
    }

    @JsonProperty("bpm_workflowPriority")
    public Integer getBpmWorkflowPriority() {
        return bpmWorkflowPriority;
    }

    @JsonProperty("bpm_workflowPriority")
    public void setBpmWorkflowPriority(Integer bpmWorkflowPriority) {
        this.bpmWorkflowPriority = bpmWorkflowPriority;
    }

    @JsonProperty("wfcnr_linkToOtherWorkflows")
    public String getWfcnrLinkToOtherWorkflows() {
        return wfcnrLinkToOtherWorkflows;
    }

    @JsonProperty("wfcnr_linkToOtherWorkflows")
    public void setWfcnrLinkToOtherWorkflows(String wfcnrLinkToOtherWorkflows) {
        this.wfcnrLinkToOtherWorkflows = wfcnrLinkToOtherWorkflows;
    }

    @JsonProperty("wfvarNomeFlusso")
    public String getWfvarNomeFlusso() {
        return wfvarNomeFlusso;
    }

    @JsonProperty("wfvarNomeFlusso")
    public void setWfvarNomeFlusso(String wfvarNomeFlusso) {
        this.wfvarNomeFlusso = wfvarNomeFlusso;
    }

    @JsonProperty("bpm_workflowDueDate")
    public String getBpmWorkflowDueDate() {
        return bpmWorkflowDueDate;
    }

    @JsonProperty("bpm_workflowDueDate")
    public void setBpmWorkflowDueDate(String bpmWorkflowDueDate) {
        this.bpmWorkflowDueDate = bpmWorkflowDueDate;
    }

    @JsonProperty("wfvarUtenteOrdineMissione")
    public String getWfvarUtenteOrdineMissione() {
        return wfvarUtenteOrdineMissione;
    }

    @JsonProperty("wfvarUtenteOrdineMissione")
    public void setWfvarUtenteOrdineMissione(String wfvarUtenteOrdineMissione) {
        this.wfvarUtenteOrdineMissione = wfvarUtenteOrdineMissione;
    }

    @JsonProperty("wfvarUtenteRichiedente")
    public String getWfvarUtenteRichiedente() {
        return wfvarUtenteRichiedente;
    }

    @JsonProperty("wfvarUtenteRichiedente")
    public void setWfvarUtenteRichiedente(String wfvarUtenteRichiedente) {
        this.wfvarUtenteRichiedente = wfvarUtenteRichiedente;
    }

    @JsonProperty("initiatorhome")
    public String getInitiatorhome() {
        return initiatorhome;
    }

    @JsonProperty("initiatorhome")
    public void setInitiatorhome(String initiatorhome) {
        this.initiatorhome = initiatorhome;
    }

    @JsonProperty("wfvarGruppoStrutturaSpesaRESPONSABILI")
    public String getWfvarGruppoStrutturaSpesaRESPONSABILI() {
        return wfvarGruppoStrutturaSpesaRESPONSABILI;
    }

    @JsonProperty("wfvarGruppoStrutturaSpesaRESPONSABILI")
    public void setWfvarGruppoStrutturaSpesaRESPONSABILI(String wfvarGruppoStrutturaSpesaRESPONSABILI) {
        this.wfvarGruppoStrutturaSpesaRESPONSABILI = wfvarGruppoStrutturaSpesaRESPONSABILI;
    }

    @JsonProperty("wfvarWorkflowInstanceId")
    public String getWfvarWorkflowInstanceId() {
        return wfvarWorkflowInstanceId;
    }

    @JsonProperty("wfvarWorkflowInstanceId")
    public void setWfvarWorkflowInstanceId(String wfvarWorkflowInstanceId) {
        this.wfvarWorkflowInstanceId = wfvarWorkflowInstanceId;
    }

    @JsonProperty("cancelled")
    public Boolean getCancelled() {
        return cancelled;
    }

    @JsonProperty("cancelled")
    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    @JsonProperty("wfvarGruppoRESPONSABILI")
    public String getWfvarGruppoRESPONSABILI() {
        return wfvarGruppoRESPONSABILI;
    }

    @JsonProperty("wfvarGruppoRESPONSABILI")
    public void setWfvarGruppoRESPONSABILI(String wfvarGruppoRESPONSABILI) {
        this.wfvarGruppoRESPONSABILI = wfvarGruppoRESPONSABILI;
    }

    @JsonProperty("wfvarGruppoStrutturaUoSUPERVISORI")
    public String getWfvarGruppoStrutturaUoSUPERVISORI() {
        return wfvarGruppoStrutturaUoSUPERVISORI;
    }

    @JsonProperty("wfvarGruppoStrutturaUoSUPERVISORI")
    public void setWfvarGruppoStrutturaUoSUPERVISORI(String wfvarGruppoStrutturaUoSUPERVISORI) {
        this.wfvarGruppoStrutturaUoSUPERVISORI = wfvarGruppoStrutturaUoSUPERVISORI;
    }

    @JsonProperty("wfvarValidazioneSpesa")
    public Boolean getWfvarValidazioneSpesa() {
        return wfvarValidazioneSpesa;
    }

    @JsonProperty("wfvarValidazioneSpesa")
    public void setWfvarValidazioneSpesa(Boolean wfvarValidazioneSpesa) {
        this.wfvarValidazioneSpesa = wfvarValidazioneSpesa;
    }

    @JsonProperty("wfvarGruppoSUPERVISORI")
    public String getWfvarGruppoSUPERVISORI() {
        return wfvarGruppoSUPERVISORI;
    }

    @JsonProperty("wfvarGruppoSUPERVISORI")
    public void setWfvarGruppoSUPERVISORI(String wfvarGruppoSUPERVISORI) {
        this.wfvarGruppoSUPERVISORI = wfvarGruppoSUPERVISORI;
    }

    @JsonProperty("bpm_workflowDescription")
    public String getBpmWorkflowDescription() {
        return bpmWorkflowDescription;
    }

    @JsonProperty("bpm_workflowDescription")
    public void setBpmWorkflowDescription(String bpmWorkflowDescription) {
        this.bpmWorkflowDescription = bpmWorkflowDescription;
    }

    @JsonProperty("companyhome")
    public String getCompanyhome() {
        return companyhome;
    }

    @JsonProperty("companyhome")
    public void setCompanyhome(String companyhome) {
        this.companyhome = companyhome;
    }

    @JsonProperty("wfvarUtentePrimoFirmatario")
    public String getWfvarUtentePrimoFirmatario() {
        return wfvarUtentePrimoFirmatario;
    }

    @JsonProperty("wfvarUtentePrimoFirmatario")
    public void setWfvarUtentePrimoFirmatario(String wfvarUtentePrimoFirmatario) {
        this.wfvarUtentePrimoFirmatario = wfvarUtentePrimoFirmatario;
    }

    @JsonProperty("wfvarUtenteFirmatarioSpesa")
    public String getWfvarUtenteFirmatarioSpesa() {
        return wfvarUtenteFirmatarioSpesa;
    }

    @JsonProperty("wfvarUtenteFirmatarioSpesa")
    public void setWfvarUtenteFirmatarioSpesa(String wfvarUtenteFirmatarioSpesa) {
        this.wfvarUtenteFirmatarioSpesa = wfvarUtenteFirmatarioSpesa;
    }

    @JsonProperty("wfvarGruppoStrutturaUoRESPONSABILI")
    public String getWfvarGruppoStrutturaUoRESPONSABILI() {
        return wfvarGruppoStrutturaUoRESPONSABILI;
    }

    @JsonProperty("wfvarGruppoStrutturaUoRESPONSABILI")
    public void setWfvarGruppoStrutturaUoRESPONSABILI(String wfvarGruppoStrutturaUoRESPONSABILI) {
        this.wfvarGruppoStrutturaUoRESPONSABILI = wfvarGruppoStrutturaUoRESPONSABILI;
    }

    @JsonProperty("wfvarTitoloFlusso")
    public String getWfvarTitoloFlusso() {
        return wfvarTitoloFlusso;
    }

    @JsonProperty("wfvarTitoloFlusso")
    public void setWfvarTitoloFlusso(String wfvarTitoloFlusso) {
        this.wfvarTitoloFlusso = wfvarTitoloFlusso;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
